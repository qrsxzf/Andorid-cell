package com.qdb.agent.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;








import org.json.JSONObject;






import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

public class DeviceUtil {

	private static String TAG = DeviceUtil.class.getName();
	public static String getOSVersion() {
		return "android_"+Build.VERSION.RELEASE;
	}

	public static String getDevice() {
		return Build.MODEL;
	}
	// 获取系统imei
	public static String getIMEI(Context context) {
		TelephonyManager  telemgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		return telemgr.getDeviceId();
	}
	// 获取系统imsi
	public static String getIMSI(Context context) {
		try {
			TelephonyManager  telemgr = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			return telemgr.getSubscriberId();
		} catch (SecurityException e) {
			// TODO: handle exception
			Log.e(TAG,"getIMSI"+e.getMessage());
			return null;
		}
	}
	/**
	 * 判断是否存在sim卡
	 * @return
	 */
	public static boolean isHaveSim(Context context) {
		String sim = getIMSI(context);
		return sim != null && sim.length() > 1;
	}

	public static int getSIMState(Context context) {
		TelephonyManager  telemgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telemgr.getSimState();
	}
	/**
	 * 获取版本号
	 * @return
	 */
	public  static String getAppVersionName(Context context) {
		String version = "1.0.0";
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			version = packageInfo.versionName;
		} catch (NameNotFoundException e) {
			Log.e(TAG,"getAppVersionName:"+e.getMessage());
		}
		return version;
	}
	public static int getAppVersionCode(Context context) {
		int code = 1;
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			code = packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			Log.e(TAG,"getAppVersionCode:"+e.getMessage());
		}
		return code;
	}
	public static String getManufacturer() {
		return Build.MANUFACTURER;
	}
	/**
	 * 检查sd卡是否存在
	 * 
	 * @return
	 */
	public static boolean checkSDCard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			return true;
		return false;
	}
	//获取sd卡可用空间 MIB单位
	public static long getAvailaleSize(){

		File path = Environment.getExternalStorageDirectory(); //取得sdcard文件路径
		StatFs stat = new StatFs(path.getPath()); 
		long blockSize = stat.getBlockSize(); 
		long availableBlocks = stat.getAvailableBlocks();
		// return availableBlocks * blockSize; 
		//(availableBlocks * blockSize)/1024      KIB 单位
		long size= (availableBlocks * blockSize)/1024 /1024;//  MIB单位
		Log.e(TAG,"getAvailaleSize:"+size);
		return size;
	}
	//sd卡总空间 
	public static long getAllSize(){

		File path = Environment.getExternalStorageDirectory(); 
		StatFs stat = new StatFs(path.getPath()); 
		long blockSize = stat.getBlockSize(); 
		long availableBlocks = stat.getBlockCount();
		// return availableBlocks * blockSize; 
		return (availableBlocks * blockSize)/1024 /1024;//  MIB单位
	}
	/**
	 * 判断App是否处于后台运行 true-后台运行
	 * @param context
	 * @return
	 */
	public static boolean isBackgroundRunning(String packname,Context context) {
		if(context==null)
			return false;
		String processName = packname;//MyApplication.getInstance().getPackageName();
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		KeyguardManager keyguardManager = (KeyguardManager) context
				.getSystemService(Context.KEYGUARD_SERVICE);
		if (activityManager == null)
			return false;
		List<ActivityManager.RunningAppProcessInfo> processList = activityManager
				.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo process : processList) {
			if (process.processName.startsWith(processName)) {
				boolean isBackground = process.importance != android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
						&& process.importance != android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;
				boolean isLockedState = keyguardManager
						.inKeyguardRestrictedInputMode();
				if (isBackground || isLockedState) {
					Log.e("DeviceUtil", "APP isBackgroundRunning");
					return true;
				}
				else
					return false;
			}
		}
		return false;
	}

	/**
	 * 获取设备Ip
	 */
	public static String getDeviceIp(Context context) {

		String deviceIp = "";
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {

					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLinkLocalAddress()) {
						deviceIp = inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("DeviceUtil", "WifiPreference IpAddress=" + ex.toString());
		}
		return deviceIp;
	}
	public static String getLocalIpAddress() { 
		try { 
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) { 
				NetworkInterface intf = en.nextElement(); 
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) { 
					InetAddress inetAddress = enumIpAddr.nextElement(); 
					if (!inetAddress.isLoopbackAddress()) { 
						return inetAddress.getHostAddress().toString(); 
					} 
				} 
			} 
		} catch (SocketException ex) { 
			Log.e("DeviceUtil",  ex.toString()); 
		} 
		return null; 
	} 

	/**
	 * 获取手机mac地址<br/>
	 * 错误返回12个0
	 */
	public static String getMacAddress(Context context) {
		// 获取mac地址：
		String macAddress = "000000000000";
		try {
			WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
			if (null != info) {
				if (!TextUtils.isEmpty(info.getMacAddress()))
					macAddress = info.getMacAddress().replace(":", "");
				else
					return macAddress;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return macAddress;
		}
		return macAddress;
	}
	public static String getNetIp() {
		String ip="";
		try {
			String address = "http://ip.taobao.com/service/getIpInfo2.php?ip=myip";
			URL url = new URL(address);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setUseCaches(false);
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream in = connection.getInputStream();
				// 将流转化为字符串
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));

				String tmpString = "";
				StringBuilder retJSON = new StringBuilder();
				while ((tmpString = reader.readLine()) != null) {
					retJSON.append(tmpString + "\n");
				}

				JSONObject jsonObject = new JSONObject(retJSON.toString());
				String code = jsonObject.getString("code");

				if (code.equals("0")) {

					JSONObject data = jsonObject.getJSONObject("data");

					ip = data.getString("ip");
					Log.e("DeviceUtil", "提示您的IP地址是：" + data.getString("ip") + "(" + data.getString("country") + data.getString("area") + "区"
							+ data.getString("region") + data.getString("city") + data.getString("isp") + ")");
				} else {
					Log.e("DeviceUtil", "提示IP接口异常，无法获取IP地址！");
				}

			} else {
				Log.e("DeviceUtil", "提示网络连接异常，无法获取IP地址！");
			}

		} catch (Exception e) {
			Log.e("DeviceUtil", "提示获取IP地址时出现异常，异常信息是：" + e.toString());
		}
		return ip;
	}
	//	public static void getNetIp() {
	//		new Thread(new Runnable() {
	//
	//			@Override
	//			public void run() {
	//				try {
	//					String address = "http://ip.taobao.com/service/getIpInfo2.php?ip=myip";
	//					URL url = new URL(address);
	//					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	//					connection.setUseCaches(false);
	//					if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
	//						InputStream in = connection.getInputStream();
	//						// 将流转化为字符串
	//						BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	//
	//						String tmpString = "";
	//						StringBuilder retJSON = new StringBuilder();
	//						while ((tmpString = reader.readLine()) != null) {
	//							retJSON.append(tmpString + "\n");
	//						}
	//
	//						JSONObject jsonObject = new JSONObject(retJSON.toString());
	//						String code = jsonObject.getString("code");
	//
	//						if (code.equals("0")) {
	//							
	//							JSONObject data = jsonObject.getJSONObject("data");
	//							
	//							String ip = data.getString("ip");
	//							Log.e("DeviceUtil", "提示您的IP地址是：" + data.getString("ip") + "(" + data.getString("country") + data.getString("area") + "区"
	//									+ data.getString("region") + data.getString("city") + data.getString("isp") + ")");
	// 							} else {
	//							Log.e("DeviceUtil", "提示IP接口异常，无法获取IP地址！");
	//						}
	//
	//					} else {
	//						Log.e("DeviceUtil", "提示网络连接异常，无法获取IP地址！");
	//						}
	//
	//				} catch (Exception e) {
	//					Log.e("DeviceUtil", "提示获取IP地址时出现异常，异常信息是：" + e.toString());
	//				}
	//			}
	//		}).start();
	// 	}

	// 手机串号:GSM手机的 IMEI 和 CDMA手机的 MEID.
	public static String getDeviceId(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm != null ? tm.getDeviceId() : "";
	}

}
