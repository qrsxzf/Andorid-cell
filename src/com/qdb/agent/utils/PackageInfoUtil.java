package com.qdb.agent.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * @author xuzhenfeng
 * @date 2015-6-3 下午3:38:24
 */
public class PackageInfoUtil {
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
}
