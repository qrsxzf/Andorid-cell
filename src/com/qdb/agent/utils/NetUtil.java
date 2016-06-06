package com.qdb.agent.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class NetUtil // extends BroadcastReceiver
{

	/**
	 * 判断当前网络是不是wifi 网络
	 * 
	 * @param context
	 * @return true wifi
	 */
	public static boolean isWifi(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	/**
	 * �?查网络是否连接包含正在连�?
	 * 
	 * @param context
	 * @return true connected or connecting
	 */
	public static boolean checkNetworkStatus(Context context) {
		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isConnectedOrConnecting()) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * �?查网络是否已连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkNet(Context context) {// 获取手机�?有连接管理对象（包括对wi-fi,net等连接的管理�?
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {// 获取网络连接管理的对�?
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					// 判断当前网络是否已经连接
					if (info.getState() == NetworkInfo.State.CONNECTED)
						return true;
				}
			}
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * �?查当前的网络类型
	 * 
	 * @return 0：Wifi�?2:2G网络�?3:3G网络
	 */
	public static int checkNetworkType(Context context) {
		int type = 0;
		try {
			ConnectivityManager conMan = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (conMan != null) {
				NetworkInfo info = conMan.getActiveNetworkInfo();
				if (info != null
						&& info.getType() == ConnectivityManager.TYPE_WIFI) {
					type = 0;
				} else if (info != null
						&& info.getType() == ConnectivityManager.TYPE_MOBILE) {
					// NETWORK_TYPE_EVDO_A是电�?3G
					// NETWORK_TYPE_EVDO_A是中国电�?3G的getNetworkType
					// NETWORK_TYPE_CDMA电信2G是CDMA
					// 移动2G�? + CMCC + 2//type = NETWORK_TYPE_EDGE
					// 联�?�的2G经过测试 China Unicom 1 NETWORK_TYPE_GPRS
					if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS
							|| info.getSubtype() == TelephonyManager.NETWORK_TYPE_CDMA
							|| info.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE) {// 2G
						type = 2;
					} else {
						type = 3;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return type;
	}

}
