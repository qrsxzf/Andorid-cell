package com.qdb.agent;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.qdb.agent.http.ErrCode;
import com.qdb.agent.http.HttpRspObject;
import com.qdb.agent.http.HttpUtilQdbEx;
import com.qdb.agent.http.UrlConstantQdb;
import com.qdb.agent.utils.DialogLoading;
import com.qdb.agent.utils.DownloadUtil;
import com.qdb.agent.utils.NetWorkTool;
import com.qdb.agent.utils.PackageInfoUtil;
import com.qdb.agent.utils.SharedPreferencesUtil;
import com.qdb.agent.utils.StringUtil;
import com.qdb.agent.utils.ToastUtil;
import com.qdb.agent.utils.Utility;

public class MainActivity extends Activity {
	private Dialog loginDialog;
	private Button send_ok;
	private TimeCount time;
	private TextView location_tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main1);
		EventBus.getDefault().register(this);
		location_tv = (TextView) findViewById(R.id.location_tv);
	}

	public void checkNewVersion() {
		RequestParams params = new RequestParams();
		final String appVersion = Utility.getAppVersion(this);
		HttpUtilQdbEx.getInstance().newGetHttpReq(this, UrlConstantQdb.QDBAGENT_UPDATECHECK + "?verid=" + appVersion + "&apptype=" + UrlConstantQdb.APP_TYPE, params,
				UrlConstantQdb.QDBAGENT_UPDATECHECK);

	}

	@Subscriber(tag = UrlConstantQdb.QDBAGENT_UPDATECHECK)
	private void updateCheckNewVersion(HttpRspObject rspObj) {
		DialogLoading.getInstance().dimissLoading();
		String status = rspObj.getStatus();
		if (status.equals(ErrCode.err_succ)) {
			Map<String, Object> response = (Map<String, Object>) rspObj.getRspObj();
			String verid = response.get("verid").toString();// 服务器
			String oldVerid = PackageInfoUtil.getAppVersion(this);// 本地
			if (!verid.equals(oldVerid)) {
				String[] newVer = verid.split("\\.");// 服务器
				String[] oldVer = oldVerid.split("\\.");// 本地
				if (Integer.parseInt(newVer[0]) > Integer.parseInt(oldVer[0])) {
					checkVersionDialog(response.get("updatemsg").toString(), response.get("download").toString(), "0");
				} else if (Integer.parseInt(newVer[0]) == Integer.parseInt(oldVer[0])) {
					if (Integer.parseInt(newVer[1]) > Integer.parseInt(oldVer[1])) {
						checkVersionDialog(response.get("updatemsg").toString(), response.get("download").toString(), "0");
					} else if (Integer.parseInt(newVer[1]) == Integer.parseInt(oldVer[1])) {
						if (Integer.parseInt(newVer[2]) > Integer.parseInt(oldVer[2])) {
							checkVersionDialog(response.get("updatemsg").toString(), response.get("download").toString(), "0");
						} else if (Integer.parseInt(newVer[2]) == Integer.parseInt(oldVer[2])) {

						}
					}
				}
			}
		} else {
			ToastUtil.showMessage(this, rspObj.getErrMsg());
		}

	}

	private void checkVersionDialog(String message, final String url, String is_force) {
		final Dialog checkVersion = new Dialog(this, R.style.MyDialogStyle);
		checkVersion.setContentView(R.layout.check_version_dialog);
		TextView tv_title = (TextView) checkVersion.findViewById(R.id.content_tv);
		Button btn_cancel = (Button) checkVersion.findViewById(R.id.btn_cancel);
		Button btn_ok = (Button) checkVersion.findViewById(R.id.btn_ok);
		tv_title.setText(message);
		if (!"1".equals(is_force)) {
			btn_cancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					checkVersion.dismiss();
				}
			});
		}
		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				checkVersion.dismiss();
				if (StringUtil.isBlank(url)) {
					ToastUtil.showMessage(MainActivity.this, R.string.app_update_fail);
				} else {
					DownloadUtil.getInstance(MainActivity.this).download(url, R.drawable.icon);
				}
			}
		});
		checkVersion.show();

	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	public void okFinish(View view) {
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// EventBus.getDefault().unregister(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		checkNewVersion();
		time = new TimeCount(60000, 1000);// 构造CountDownTimer对象
		location_tv.setText("当前位置：" + SharedPreferencesUtil.getMyUserLocation(this));
	}

	/**
	 * 上传位置成功
	 * 
	 * @param rspObj
	 */
	@Subscriber(tag = UrlConstantQdb.LBS_AGENT + "online")
	private void updateServerInfo(HttpRspObject rspObj) {
		String status = rspObj.getStatus();
		if (status.equals(ErrCode.err_succ)) {
			// {"interval": 5, "resid": 0, "location": "", "resmsg": ""}
			Map<String, Object> response = (Map<String, Object>) rspObj.getRspObj();
			location_tv.setText("当前位置：" + String.valueOf(response.get("location")));
			String interval = String.valueOf(response.get("interval"));
			if (StringUtil.isBlank(interval) || (!StringUtil.isNumeric(interval))) {
				SharedPreferencesUtil.saveTraceInterval(this, 0);
				SharedPreferencesUtil.saveMyUserLocation(this, "");
			} else {
				SharedPreferencesUtil.saveTraceInterval(this, Integer.parseInt(interval));
				SharedPreferencesUtil.saveMyUserLocation(this, String.valueOf(response.get("location")));
			}
			System.out.println("lbs上传位置成功!!!!!!");
		} else if (status.equals(ErrCode.err_relogin)) {
			if (loginDialog != null && loginDialog.isShowing()) {
				System.out.println();
			} else {
				showLoginDialog();
			}
		} else {
			ToastUtil.showMessage(this, rspObj.getErrMsg());
		}
	}

	private String mobileString = "";

	private void showLoginDialog() {
		loginDialog = new Dialog(this, R.style.MyDialogStyle);
		loginDialog.setContentView(R.layout.login);
		final EditText tel_et = (EditText) loginDialog.findViewById(R.id.tel_et);
		final EditText ver_et = (EditText) loginDialog.findViewById(R.id.ver_et);
		send_ok = (Button) loginDialog.findViewById(R.id.send_ok);
		Button btn_ok = (Button) loginDialog.findViewById(R.id.btn_ok);
		loginDialog.setCanceledOnTouchOutside(false);
		send_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (StringUtil.isBlank(tel_et.getText().toString())) {
					ToastUtil.showMessage(MainActivity.this, "请先输入您的手机号");
					tel_et.requestFocus(); // 请求获取焦点
				} else if (!StringUtil.isMobileNum(tel_et.getText().toString())) {
					ToastUtil.showMessage(MainActivity.this, "您输入的手机号格式有误");
					tel_et.requestFocus(); // 请求获取焦点
				} else {
					getCode(tel_et);
				}
			}
		});
		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (StringUtil.isBlank(tel_et.getText().toString())) {
					ToastUtil.showMessage(MainActivity.this, "请先输入您的手机号");
					tel_et.requestFocus(); // 请求获取焦点
				} else if (!StringUtil.isMobileNum(tel_et.getText().toString())) {
					ToastUtil.showMessage(MainActivity.this, "您输入的手机号格式有误");
					tel_et.requestFocus(); // 请求获取焦点
				} else if (StringUtil.isBlank(ver_et.getText().toString())) {
					ToastUtil.showMessage(MainActivity.this, "请输入手机号验证码");
					ver_et.requestFocus(); // 请求获取焦点
				} else {
					mobileString = tel_et.getText().toString().trim();
					login(tel_et, ver_et);
				}
			}
		});
		loginDialog.show();
	}

	/**
	 * 登录
	 * 
	 * @param tel
	 * @param vercode
	 */
	private void login(EditText tel, EditText vercode) {
		if (!bShowNetWorkOk())
			return;
		DialogLoading.getInstance().showLoading(this);
		JSONObject jObj = new JSONObject();
		try {
			jObj.put("mobileno", tel.getText().toString().trim());//
			jObj.put("vericode", vercode.getText().toString().trim());//
		} catch (JSONException e) {
			e.printStackTrace();
		}
		HttpUtilQdbEx.getInstance().newPostHttpReq(this, UrlConstantQdb.QDBAGENT_VERCODE, jObj, UrlConstantQdb.QDBAGENT_VERCODE + "login");
	}

	@Subscriber(tag = UrlConstantQdb.QDBAGENT_VERCODE + "login")
	private void updateLogin(HttpRspObject rspObj) {
		String status = rspObj.getStatus();
		DialogLoading.getInstance().dimissLoading();
		if (status.equals(ErrCode.err_succ)) {
			SharedPreferencesUtil.saveMobile(this, mobileString);
			if (loginDialog != null)
				loginDialog.dismiss();
			ToastUtil.showMessage(this, rspObj.getErrMsg());
		} else {
			ToastUtil.showMessage(this, rspObj.getErrMsg());
		}
	}

	/**
	 * 获取验证码
	 * 
	 * @param et
	 */
	public void getCode(EditText et) {
		if (!bShowNetWorkOk())
			return;
		DialogLoading.getInstance().showLoading(this);
		RequestParams params = new RequestParams();
		HttpUtilQdbEx.getInstance().newGetHttpReq(this, UrlConstantQdb.QDBAGENT_VERCODE + "?mobileno=" + et.getText().toString().trim(), params, UrlConstantQdb.QDBAGENT_VERCODE);
	}

	@Subscriber(tag = UrlConstantQdb.QDBAGENT_VERCODE)
	private void updateVercode(HttpRspObject rspObj) {
		String status = rspObj.getStatus();
		DialogLoading.getInstance().dimissLoading();
		if (status.equals(ErrCode.err_succ)) {
			time.start();// 开始计时
			ToastUtil.showMessage(this, rspObj.getErrMsg());
		} else {
			ToastUtil.showMessage(this, rspObj.getErrMsg());
		}
	}

	public boolean bShowNetWorkOk() {
		if (!NetWorkTool.isNetworkAvailable(this)) {
			ToastUtil.showMessage(this, getString(R.string.net_err));
			return false;
		}
		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	/* 定义一个倒计时的内部类 */
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			send_ok.setText("重新获取");
			send_ok.setClickable(true);
			send_ok.setEnabled(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			send_ok.setClickable(false);
			long time = millisUntilFinished / 1000;
			send_ok.setText(time + "秒");
			send_ok.setEnabled(false);
		}
	}

}
