package com.qdb.agent.receiver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.qdb.agent.bean.TeleBS;
import com.qdb.agent.converter.JsonMessageConverter;
import com.qdb.agent.converter.MessageConverter;
import com.qdb.agent.http.HttpUtilQdbEx;
import com.qdb.agent.http.UrlConstantQdb;
import com.qdb.agent.utils.DeviceUtil;
import com.qdb.agent.utils.FileUtil;
import com.qdb.agent.utils.Logger;
import com.qdb.agent.utils.MyLog;
import com.qdb.agent.utils.NetWorkTool;
import com.qdb.agent.utils.SharedPreferencesUtil;
import com.qdb.agent.utils.StringUtil;
import com.qdb.agent.utils.TeleBSUtil;
import com.qdb.agent.utils.TimeUtils;
import com.qdb.agent.utils.ToastUtil;

public class MyService extends Service {
	private RandomAccessFile fWriter = null;
	private int MAX_LOG_SIZE = 50 * 1024;
	private File file = null;
	private Gps gps = null;
	private ArrayList<CellInfo> cellIds = null;
	private final static String TAG = MyService.class.getSimpleName();
	private int keepAliveInterval = 60 * 1000;// 1分钟获取一次位置
	private boolean gpsIsEnable = false;// 判断GPS是否可用
	Location location;

	private AlarmManager mAlarmManger = null;
	private TimerReceiver mLister = null;
	private PendingIntent pi = null;
	private String actionStr = "com.baidu.locSDK.test.timer2";
	private PowerManager.WakeLock wl = null;

	public void releaseWackLock() {
		if (wl != null && wl.isHeld())
			wl.release();
	}

	public void start() {
		mAlarmManger = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		mLister = new TimerReceiver();
		registerReceiver(mLister, new IntentFilter(actionStr));
		pi = PendingIntent.getBroadcast(this, 0, new Intent(actionStr), PendingIntent.FLAG_UPDATE_CURRENT);
		int traceInterval = SharedPreferencesUtil.getTraceInterval(this);
		if (traceInterval < 5) {
			traceInterval = 5;
		}// 间隔多少分钟上传位置一次（最少5分钟）
			// mAlarmManger.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
			// 0, traceInterval * keepAliveInterval, pi);
		mAlarmManger.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0, 5000, pi);
		initTelephonyManager();
	}

	public void stop() {
		unregisterReceiver(mLister);
		mAlarmManger.cancel(pi);
	}

	public class TimerReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			System.out.println("定位定时器还活着....");
			if (wl != null)
				wl.acquire();
			// 判断GPS是否可用
			gpsIsEnable = UtilTool.isGpsEnabled((LocationManager) getSystemService(Context.LOCATION_SERVICE));
			if (gpsIsEnable) {
				if (gps != null) { // 当结束服务时gps为空
					// 获取经纬度
					location = gps.getLocation();
					// 如果gps无法获取经纬度，改用基站定位获取
					if (location == null) {
						MyLog.i(TAG, "gps location null");
						// 2.根据基站信息获取经纬度
						try {
							location = UtilTool.callGear(MyService.this, cellIds);
						} catch (Exception e) {
							location = null;
							e.printStackTrace();
						}
						// handler.sendEmptyMessage(100);
						sendMessage(100);
					} else {
						MyLog.e(TAG, "lat=" + location.getLatitude());
						MyLog.e(TAG, "lng=" + location.getLongitude());
						// handler.sendEmptyMessage(101);
						sendMessage(101);
					}
				} else {
					// handler.sendEmptyMessage(100);
					sendMessage(100);
				}
			} else {
				// handler.sendEmptyMessage(100);
				sendMessage(100);
			}
			releaseWackLock();
		}
	}

	private void sendMessage(int mess) {
		if (NetWorkTool.isWifiConnected(MyService.this)) {//  连接wifi
			WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wm.getConnectionInfo();
			if (SharedPreferencesUtil.getWifiMac(MyService.this).equals(wifiInfo.getBSSID())) {// 同一个wifi

			} else {// 不同wifi
				handler.sendEmptyMessage(mess);
			}
			SharedPreferencesUtil.saveWifiMac(MyService.this, wifiInfo.getBSSID());
		} else {
			SharedPreferencesUtil.saveWifiMac(MyService.this, "no wifi");
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		EventBus.getDefault().register(this);
		start();
		try {
			gps = new Gps(MyService.this);
			cellIds = UtilTool.init(MyService.this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private TelephonyManager telephonyManager;
	private PhoneStateListener phoneStateListener;
	private int lastSignal;
	private int lastCid;
	private List<GsmCell> gsmCells = new ArrayList<GsmCell>();
	private List<CdmaCell> cdmaCells = new ArrayList<CdmaCell>();

	private void initTelephonyManager() {
		InitPhoneStateListener();
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CELL_LOCATION);
		if (telephonyManager.getCellLocation() != null) {
			// 获取当前基站信息
			phoneStateListener.onCellLocationChanged(telephonyManager.getCellLocation());
		}
	}

	private void InitPhoneStateListener() {
		phoneStateListener = new PhoneStateListener() {
			@Override
			public void onCellLocationChanged(CellLocation location) {
				gsmCells.clear();
				if (location instanceof GsmCellLocation) {// gsm网络
					GsmCell gsmCell = new GsmCell();
					gsmCell.lac = ((GsmCellLocation) location).getLac();
					gsmCell.cid = ((GsmCellLocation) location).getCid();
					lastCid = ((GsmCellLocation) location).getCid();
					/** 获取mcc，mnc */
					String mccMnc = telephonyManager.getNetworkOperator();
					if (mccMnc != null && mccMnc.length() >= 5) {
						gsmCell.mcc = mccMnc.substring(0, 3);
						gsmCell.mnc = mccMnc.substring(3, 5);
					}
					lastSignal = gsmCell.signal;
					gsmCell.time = System.currentTimeMillis();
					if (gsmCell.lac != -1 && gsmCell.cid != -1) {
						gsmCells.add(0, gsmCell);
					}
					// 获取相邻基站信息
					List<NeighboringCellInfo> neighboringList = telephonyManager.getNeighboringCellInfo();
					for (NeighboringCellInfo ni : neighboringList) {
						GsmCell gb = new GsmCell();
						gb.mnc = mccMnc.substring(3, 5);
						gb.lac = ni.getLac();
						gb.cid = ni.getCid();
						gb.signal = -133 + 2 * ni.getRssi();
						gb.time = System.currentTimeMillis();
						gsmCells.add(gb);
					}
				} else {// 其他CDMA等网络
					try {
						CdmaCellLocation cdma = (CdmaCellLocation) location;
						CdmaCell cdmaCell = new CdmaCell();
						cdmaCell.stationId = cdma.getBaseStationId() >= 0 ? cdma.getBaseStationId() : cdmaCell.stationId;
						lastCid = cdma.getBaseStationId() >= 0 ? cdma.getBaseStationId() : cdmaCell.stationId;
						cdmaCell.networkId = cdma.getNetworkId() >= 0 ? cdma.getNetworkId() : cdmaCell.networkId;
						cdmaCell.systemId = cdma.getSystemId() >= 0 ? cdma.getSystemId() : cdmaCell.systemId;
						/** 获取mcc，mnc */
						String mccMnc = telephonyManager.getNetworkOperator();
						if (mccMnc != null && mccMnc.length() >= 5) {
							cdmaCell.mcc = mccMnc.substring(0, 3);
							cdmaCell.mnc = mccMnc.substring(3, 5);
						}
						lastSignal = cdmaCell.signal;
						cdmaCell.time = System.currentTimeMillis();
						int lat = cdma.getBaseStationLatitude();
						int lon = cdma.getBaseStationLongitude();
						if (lat < Integer.MAX_VALUE && lon < Integer.MAX_VALUE) {
							cdmaCell.lat = lat;
							cdmaCell.lon = lon;
						}
						if (cdmaCell.stationId != -1 && cdmaCell.networkId != -1 && cdmaCell.systemId != -1) {
							cdmaCells.add(0, cdmaCell);
						}
						List<NeighboringCellInfo> neighboringList = telephonyManager.getNeighboringCellInfo();
						for (NeighboringCellInfo ni : neighboringList) {
							CdmaCell cdmaBean = new CdmaCell();
							cdmaBean.systemId = cdmaCell.systemId;
							cdmaBean.lac = ni.getLac();
							cdmaBean.cellid = ni.getCid();
							cdmaBean.signal = -113 + 2 * ni.getRssi();
							cdmaCells.add(cdmaBean);
						}
					} catch (Exception classnotfoundexception) {
					}
				}// end CDMA网络
				int cid = SharedPreferencesUtil.getCid(MyService.this);
				int signal = SharedPreferencesUtil.getSignal(MyService.this);
				if (lastCid != cid && lastSignal != 0) {
					writeCidLog("listen", lastCid, lastSignal);
					handler.sendEmptyMessage(100);
				} else if (Math.abs(signal - lastSignal) > 30) {
					writeCidLog("listen", lastCid, lastSignal);
					handler.sendEmptyMessage(100);
				}
				SharedPreferencesUtil.saveCid(MyService.this, lastCid);
				SharedPreferencesUtil.saveSignal(MyService.this, lastSignal);
				ToastUtil.showMessage(MyService.this, "lbs监听信号中.....lastCid=" + lastCid);
				super.onCellLocationChanged(location);
			}// end onCellLocationChanged

			@Override
			public void onServiceStateChanged(ServiceState serviceState) {
				super.onServiceStateChanged(serviceState);
			}

			@Override
			public void onSignalStrengthsChanged(SignalStrength signalStrength) {
				int asu = signalStrength.getGsmSignalStrength();
				lastSignal = -113 + 2 * asu; // 信号强度
				ToastUtil.showMessage(MyService.this, "lbs信号强度改变-----------lastCid=" + lastCid);
				int cid = SharedPreferencesUtil.getCid(MyService.this);
				int signal = SharedPreferencesUtil.getSignal(MyService.this);
				if (lastCid != cid && lastSignal != 0) {
					writeCidLog("signalChanged", lastCid, lastSignal);
				} else if (Math.abs(signal - lastSignal) > 30) {
					writeCidLog("listen", lastCid, lastSignal);
					handler.sendEmptyMessage(100);
				}
				SharedPreferencesUtil.saveCid(MyService.this, lastCid);
				SharedPreferencesUtil.saveSignal(MyService.this, lastSignal);
				super.onSignalStrengthsChanged(signalStrength);
			}
		};

	}

	private void writeCidLog(String type, int cid, int signalStrength) {
		String log = FileUtil.getLogCachePath(MyService.this) + File.separator + "cid.txt";
		file = new File(log);
		JSONObject obj = new JSONObject();
		try {
			obj.put("time", TimeUtils.getCurrentTime());
			obj.put("cid", cid);
			obj.put("type", type);
			obj.put("signal", signalStrength);
			writeLog(file, obj.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public MessageConverter converter = new JsonMessageConverter();

	/**
	 * 上传位置
	 * 
	 * @param latitude
	 * @param longitude
	 */
	private long exitTime = 0;

	public void realtimeTrack(double latitude, double longitude) {
		if ((System.currentTimeMillis() - exitTime) <=  keepAliveInterval) {
			exitTime = System.currentTimeMillis();
			return;
		}
		exitTime = System.currentTimeMillis();
		String log = FileUtil.getLogCachePath(this) + File.separator + "locationcache";
		file = new File(log);
		// 应用退到后台的时候检查log文件是否存在
		if (FileUtil.isValidAttach(log, false)) {
			// 如果是wifi方式直接上传
			if (NetWorkTool.isNetworkAvailable(this)) {
				uploadLocation(file);
			}
		}

		JSONObject obj = new JSONObject();
		JSONObject jObj = new JSONObject();
		JSONArray aplistArray = new JSONArray();
		JSONArray array = new JSONArray();
		JSONArray celltowersArray = new JSONArray();
		try {
			jObj.put("lat", latitude);
			jObj.put("lng", longitude);
			jObj.put("deviceid", DeviceUtil.getDeviceId(this));
			jObj.put("imei", DeviceUtil.getIMEI(this));
			jObj.put("imsi", DeviceUtil.getIMSI(this));
			jObj.put("curtime", TimeUtils.getCurrentTimeInString());
			WifiInfo info = null;
			TeleBS teleBS = TeleBSUtil.getGSMCellLocationInfo(this);
			if (teleBS != null)
				info = teleBS.getInfo();
			// 获取wifi列表
			List<ScanResult> scanResultList = TeleBSUtil.getScanResult(this);
			if (scanResultList != null && scanResultList.size() > 0) {
				for (int i = 0; i < scanResultList.size(); i++) {
					ScanResult scanResult = scanResultList.get(i);
					JSONObject aplistObj = new JSONObject();
					aplistObj.put("name", scanResult.SSID);
					aplistObj.put("mac", scanResult.BSSID);
					aplistObj.put("level", scanResult.level);
					if (info != null && scanResult.level > -70) {
						if (!StringUtil.isBlank(info.getBSSID()) && info.getBSSID().equals(scanResult.BSSID)) {
							aplistObj.put("connected", 1);
						} else {
							aplistObj.put("connected", 0);
						}
					} else {
						aplistObj.put("connected", 0);
					}
					aplistArray.put(aplistObj);
				}
			}
			jObj.put("aplist", aplistArray);
			if (teleBS != null)
				jObj.put("type", teleBS.getMncType());
			else
				jObj.put("type", "");
			if (teleBS != null && teleBS.getInfoLists() != null)
				if (teleBS.getInfoLists().size() == 0) {
					JSONObject celltowerstObj = new JSONObject();
					celltowerstObj.put("mnc", teleBS.getMnc());
					celltowerstObj.put("level", -76);
					celltowerstObj.put("lac", teleBS.getLac());
					celltowerstObj.put("cellid", teleBS.getCellid());
					celltowerstObj.put("mcc", teleBS.getMcc());
					celltowersArray.put(celltowerstObj);
				} else {
					for (int i = 0; i < teleBS.getInfoLists().size(); i++) {
						JSONObject celltowerstObj = new JSONObject();
						celltowerstObj.put("mnc", teleBS.getMnc());
						celltowerstObj.put("level", -76);
						celltowerstObj.put("lac", teleBS.getLac());
						celltowerstObj.put("mcc", teleBS.getMcc());
						celltowerstObj.put("cellid", teleBS.getInfoLists().get(i).getRssi());
						celltowersArray.put(celltowerstObj);
					}
				}
			jObj.put("celltowers", celltowersArray);
			array.put(jObj);
			obj.put("data", array);
			if (!NetWorkTool.isNetworkAvailable(this)) {// 断网情况，先保存本地
				writeLog(file, jObj.toString());
				return;
			}
			HttpUtilQdbEx.getInstance().newPostHttpReq(this, UrlConstantQdb.LBS_AGENT, obj, UrlConstantQdb.LBS_AGENT + "online");
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 上传离线位置信息
	 * 
	 * @param _file
	 */
	private void uploadLocation(File _file) {
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = readFileData(_file);
		if (jsonArray == null) {
			return;
		}
		try {
			jsonObject.put("data", jsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		HttpUtilQdbEx.newPost(this, UrlConstantQdb.LBS_AGENT, jsonObject, new BaseJsonHttpResponseHandler<Map<String, Object>>() {
			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Map<String, Object> errorResponse) {
				System.out.println();
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Map<String, Object> response) {
				if (response != null) {
					String resid = response.get("resid").toString();
					if (resid.equals("0")) {
						if (file != null && file.exists())
							file.delete();
					}
				}
			}

			@Override
			protected Map<String, Object> parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
				return converter.convertStringToMap(rawJsonData);
			}

		});
	}

	/**
	 * 读取文件
	 * 
	 * @return
	 */
	private JSONArray readFileData(File file) {
		JSONArray jsonArray = null;
		if (FileUtil.isValidAttach(file.getAbsolutePath(), false)) {
			try {
				FileInputStream fin = new FileInputStream(file);
				int length = fin.available();
				byte[] buffer = new byte[length];
				fin.read(buffer);
				String str = EncodingUtils.getString(buffer, "UTF-8");
				Logger.d(TAG, "[" + str.substring(0, str.length() - 1) + "]");
				jsonArray = new JSONArray("[" + str.substring(0, str.length() - 1) + "]");
				fin.close();
			} catch (Exception e) {
				Logger.e(TAG, e.getMessage().toString());
				e.printStackTrace();
				if (file.exists())
					file.delete();
				return null;
			}
		}
		return jsonArray;
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 100:
				if (wl != null)
					wl.acquire();
				realtimeTrack(0, 0);
				break;
			case 101:
				if (wl != null)
					wl.acquire();
				realtimeTrack(location.getLatitude(), location.getLongitude());
				break;

			default:
				break;
			}
		}

	};

	private void writeLog(File _file, String content) {
		try {
			try {
				if (!_file.getParentFile().exists()) {
					_file.getParentFile().mkdirs();
				}
				boolean isFileExist = _file.exists();
				if (fWriter != null) {
					fWriter.close();
				}
				fWriter = new RandomAccessFile(_file, "rws");
				if (isFileExist) {
					fWriter.seek(_file.length());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			StringBuffer sb = new StringBuffer();
			sb.append(content);
			sb.append(",");
			final byte[] data = sb.toString().getBytes();
			fWriter.write(data);

		} catch (Exception e) {
			Log.e("MyLog", "log output exception,maybe the log file is not exists");
		} finally {
			if (_file != null && _file.length() >= MAX_LOG_SIZE) {
				if (_file.exists())
					_file.delete();
				return;
			}
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		 EventBus.getDefault().unregister(this);
		 stop();
		 startService(new Intent(MyService.this, MyService.class));
	}
}
