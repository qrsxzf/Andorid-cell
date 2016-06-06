package com.qdb.agent.utils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class Utility {

	/*
	 * 初始化UUID
	 */
	public static String getImei(Context context) {
		String imei = "";
		try {
			TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			imei = mTelephonyMgr.getDeviceId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imei;
	}

	public static final String FORMATTER = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 将毫秒时间转化为规则格式的时间
	 * 
	 * @param datetime
	 * @return
	 */
	public static String formatMillionsToFormatted(long datetime) {
		String datetimeStr = null;
		if (datetime != 0) {
			Date date = new Date(datetime);
			SimpleDateFormat sdf = new SimpleDateFormat(FORMATTER, Locale.getDefault());
			datetimeStr = sdf.format(date);
		}
		return datetimeStr;
	}

	/**
	 * 将具有一定规则的时间转化为毫秒值的时间
	 * 
	 * @param formatter
	 * @return
	 */
	public static long formatFormattedToMillions(String formatter) {
		SimpleDateFormat sdf = new SimpleDateFormat(FORMATTER, Locale.getDefault());

		long millionSeconds = 0L;
		try {
			millionSeconds = sdf.parse(formatter).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return millionSeconds;
		}
		return millionSeconds;
	}

	/**
	 * 得到应用的版本号
	 * 
	 * @param context
	 * @return
	 * @throws NameNotFoundException
	 */
	public static String getAppVersion(Context context) {
		String versionCode = "";
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionCode = pi.versionName;
		} catch (NameNotFoundException e1) {
			MyLog.e("Utility", "can not find version name");
		}
		return versionCode;
	}

	/**
	 * 版本更新，安装所下载的apk包
	 * 
	 * @param context
	 * @param file
	 *            安装包文件
	 */
	public static void installApk(Context context, File file) {
		if (file == null || !file.exists() || context == null || file.length() == 0) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + file.toString()), "application/vnd.android.package-archive");
		// android4.0及其以后的系统中不显示安装成功的界面，所以 addFlags
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		context.startActivity(i);
	}

	/**
	 * 判断应用是否已安装
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean isInstalled(Context context, String packageName) {
		boolean hasInstalled = false;
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> list = pm.getInstalledPackages(PackageManager.PERMISSION_GRANTED);
		for (PackageInfo p : list) {
			if (packageName != null && packageName.equals(p.packageName)) {
				hasInstalled = true;
				break;
			}
		}
		return hasInstalled;
	}

	public static void openApk(Context context, String packageName) {
		Intent mIntent = new Intent();
		mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ComponentName comp = new ComponentName(packageName, packageName + ".activity.Welcome");
		mIntent.setComponent(comp);
		mIntent.setAction("android.intent.action.VIEW");
		context.startActivity(mIntent);
	}


	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

}
