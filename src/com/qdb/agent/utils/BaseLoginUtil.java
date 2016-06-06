package com.qdb.agent.utils;

import android.content.Context;

public class BaseLoginUtil {
	/**
	 * 查用户是否登
	 * 
	 * @return
	 */
	public static boolean checkLogin(Context context) {
		try {
			if (!StringUtil.isBlank(SharedPreferencesUtil.readPhoneNum(context)) && !StringUtil.isBlank(SharedPreferencesUtil.readSessionid(context))
					&& !StringUtil.isBlank(SharedPreferencesUtil.readUserid(context)) && !StringUtil.isBlank(SharedPreferencesUtil.getMyUserName(context))) {
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean checkQdbLogin(Context context) {
		try {
			if (!StringUtil.isBlank(SharedPreferencesUtil.readPhoneNum(context)) && !StringUtil.isBlank(SharedPreferencesUtil.readSessionid(context))
					&& !StringUtil.isBlank(SharedPreferencesUtil.readUserid(context))) {
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * �?查用户是否第�?次登�?
	 * 
	 * @return
	 */
	public static boolean checkIsFirstLogin(Context context) {
		try {
			if (!StringUtil.isBlank(SharedPreferencesUtil.readPhoneNum(context))) {
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
