package com.qdb.agent.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.DialogInterface.OnShowListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qdb.agent.R;

public class DialogLoading {

	/**
	 * 得到自定义的progressDialog
	 * 
	 * @param context
	 * @param msg
	 *            加载过程显示文字
	 * @param cancelFlag
	 *            是否可以取消 true 可取消 false 不可取消
	 * @return
	 */
	private static DialogLoading instance = null;
	private Dialog loadingDialog = null;
	private TextView tipTextView;

	public static DialogLoading getInstance() {
		if (null == instance) {
			instance = new DialogLoading();
		}
		return instance;

	}

	/**
	 * 显示loading框
	 * 
	 * @param message
	 * @param cancelFlag
	 *            false 不能取消
	 */
	public void showLoading(Context context, String message, boolean cancelFlag) {
		try {
			if (context == null) {
				return;
			}
			if (loadingDialog == null)
				loadingDialog = createLoadingDialog(context, message, cancelFlag);
			loadingDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 显示loading框
	 * 
	 * @param message
	 * @param cancelFlag
	 *            false 不能取消
	 */

	private Dialog createLoadingDialog(final Context context, String msg, boolean cancelFlag) {

		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.dlg_loading, null);// 得到加载view
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
		// main.xml中的ImageView
		final ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
		tipTextView = (TextView) v.findViewById(R.id.tipTextView);
		// 加载动画
		final Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context, R.anim.refresh_progress);
		// 使用ImageView显示动画
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		if (StringUtil.isBlank(msg)) {
			tipTextView.setVisibility(View.GONE);
		} else {
			tipTextView.setVisibility(View.VISIBLE);
			tipTextView.setText(msg);// 设置加载信息
		}

		dimissLoading();
		// loading_dialog
		loadingDialog = new Dialog(context, R.style.LoadingDialog);// 创建自定义样式dialog

		loadingDialog.setCancelable(cancelFlag);// 不可以用“返回键”取消
		loadingDialog.setCanceledOnTouchOutside(false);

		loadingDialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				spaceshipImage.startAnimation(hyperspaceJumpAnimation);
			}
		});
		loadingDialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {//监听返回键
					if (cancelCallback != null)
						cancelCallback.onListen(true);
				}
				return false;
			}
		});
		loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
		return loadingDialog;

	}

	private CancelCallback cancelCallback = null;

	public static interface CancelCallback {
		public void onListen(boolean flag);
	}

	public void setTextCallback(CancelCallback listener) {
		cancelCallback = listener;
	}

	public TextView getTipTextView() {
		return tipTextView;
	}

	public void showLoading(Context context) {
		try {
			if (loadingDialog == null)
				loadingDialog = createLoadingDialog(context, "", true);
			loadingDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void dimissLoading() {
		try {
			if (loadingDialog != null) {
				loadingDialog.dismiss();
				loadingDialog = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
