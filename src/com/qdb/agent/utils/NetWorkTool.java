package com.qdb.agent.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

public class NetWorkTool {
	/**
	 * 判断网络.
	 * 
	 * @param context
	 *            the context
	 * @return true - 有网络可用，false -网网络
	 */
	public static boolean isNetworkAvailable(Context context) {
		if (context == null) {
			return false;
		}
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (manager == null) {
			return false;
		}

		NetworkInfo networkinfo = manager.getActiveNetworkInfo();

		if (networkinfo == null || !networkinfo.isAvailable()) {
			return false;
		}
		if (networkinfo != null && networkinfo.isConnected()) {
			// 判断当前网络是否已经连接
			if (networkinfo.getState() == NetworkInfo.State.CONNECTED) {
				return true;
			} else {
				return false;
			}
		}
		return true;

	}

	/**
	 * 判断是否联网
	 * 
	 * @param ctx
	 *            context
	 * @return
	 */
	 public static boolean isHaveNetwork(Context ctx) {
	 return isWifiConnected(ctx) || isMobileConnected(ctx);
	 }

	public static boolean isWifiConnected(Context context) {
		return getNetworkState(context, ConnectivityManager.TYPE_WIFI) == State.CONNECTED;
	}

	public static boolean isMobileConnected(Context context) {
		return getNetworkState(context, ConnectivityManager.TYPE_MOBILE) == State.CONNECTED;
	}

	public static State getNetworkState(Context context, int networkType) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getNetworkInfo(networkType);

		return info == null ? null : info.getState();
	}

}
