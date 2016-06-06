package com.qdb.agent.utils;

import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class PhoneInfoUtil {

	/*
	 * 获取品牌
	 */
	public static String getBrand() {
		String brand = android.os.Build.BRAND;
		return brand;
	}

	/*
	 * 获取型号
	 */
	public static String getModels() {
		String models = android.os.Build.MODEL;
		return models;
	}

	public static String getChannel(Context context) {
		String channel = "dywl";
		try {
			ApplicationInfo info = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			if (info != null && info.metaData != null) {
				String metaData = info.metaData.getString("UMENG_CHANNEL");
				if (!StringUtil.isBlank(metaData)) {
					channel = metaData;
					Log.i("testrecharge", " testrecharge, set serverip:"
							+ metaData);
				}
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return channel;
	}

	/*
	 * 获取系统版本�?
	 */
	public static String getVerid() {
		String verid = android.os.Build.VERSION.RELEASE;
		return verid;
	}

	/*
	 * 程序
	 */
	public static String getPrograms(Context context) {
		StringBuffer stringBuffer = new StringBuffer();
		PackageManager pManager = context.getPackageManager();
		// 获取手机内所有应�?
		List<PackageInfo> paklist = pManager.getInstalledPackages(0);
		for (int i = 0; i < paklist.size(); i++) {
			PackageInfo pak = (PackageInfo) paklist.get(i);
			// 判断是否为非系统预装的应用程�?
			if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
				stringBuffer.append(pak.applicationInfo.loadLabel(
						context.getPackageManager()).toString());
				stringBuffer.append(",");
			}
		}
		return stringBuffer.toString();
	}
}
