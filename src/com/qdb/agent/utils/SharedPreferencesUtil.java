package com.qdb.agent.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences 工具类
 * 
 * @author xuzhenfeng
 * 
 */
public class SharedPreferencesUtil extends PreferencesUtils {
	public final static String CONFIG_FILES = "mobsaas";
	public final static String PHONENUM = "phone_number";
	public final static String SESSIONID = "sessionid";
	public final static String USERID = "userid";
	public final static String HXPASSWORD = "hxpassword";
	public final static String FILE_PATH = "file_path";// 拍照图片路径
	public final static String FILE_TYPE = "file_type";// 图片类型
	public final static String MY_FACE_URL = "my_face_url";// 我的头像地址
	public final static String MY_USER_NAME = "my_user_name";// 我的用户名称

	public final static String MY_USER_SEX = "my_user_sex";// 我的性别

	public final static String MY_USER_LOCATION = "my_user_location";// 我的位置

	public final static String MY_USER_IDENTITY = "my_user_identity";// 我的身份

	public final static String PHONE_NUMBER_PERMISSTION = "phone_number_permisstion";// 是否保存获取通讯录权限

	public final static String FIRST_ENTER = "first_enter";// 是否第一次进入该页面

	public final static String USE_SPEARK = "use_speark";// 是否使用扬声器

	public final static String USE_SPEECH = "use_speech";// 是否使用语音播报

	public final static String GET_LOCAL_POSITION = "get_local_position";// 是否允许获取本地位置

	public final static String WELCOME_URL = "welcome_url";// 欢迎图片的url

	public final static String TRACE_INTERVAL = "trace_interval";// 上传时间间隔时间（最低5分钟）
	public final static String SHOW_SIGNON = "showsignon";// 是否显示上班打点

	public final static String MAIN_MODULES = "main_modules";// 主界面模块
	public final static String FIND_MODULES = "find_modules";// 发现模块

	public final static String WRITE_DAILY_TODAY = "write_daily_today";// 缓存写日报
	public final static String WRITE_DAILY_TOMORROW = "write_daily_tomorrow";// 缓存写日报
	
	public final static String CID = "cid";// cid
	public final static String SIGNAL = "signal";// signal
	public final static String WIFI_MAC = "wifi_mac";// iswifi

	/**
	 * 保存用户
	 */
	public static void savePhoneNum(Context con, String phoneNum, String sessionid, String userid, String password, String name, String myRole) {
		if (con == null) {
			return;
		}
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		sp.edit().putString(PHONENUM, phoneNum).commit();
		sp.edit().putString(SESSIONID, sessionid).commit();
		sp.edit().putString(USERID, userid).commit();
		sp.edit().putString(HXPASSWORD, password).commit();
		sp.edit().putString(MY_USER_NAME, name).commit();
		sp.edit().putString(MY_USER_IDENTITY, myRole).commit();
	}

	/**
	 * 读取用户手机号
	 */
	public static String readPhoneNum(Context con) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		return sp.getString(PHONENUM, "");
	}

	/**
	 * 保存用户手机号
	 */
	public static void saveMobile(Context con, String mobile) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		sp.edit().putString(PHONENUM, mobile).commit();
	}

	/**
	 * 读取sessionid
	 */
	public static String readSessionid(Context con) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		return sp.getString(SESSIONID, "");
	}

	/**
	 * 读取userid
	 */
	public static String readUserid(Context con) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		return sp.getString(USERID, "");
	}

	/**
	 * 日志上报
	 */
	public final static String ISADDUSELOG = "isadduselog";// 是否开启用户操作日志
	public final static String UPSIZELOG = "upSizeLog";
	public final static String ISREALTIME = "isRealTime";// 是否开启实时上传

	public static void saveLogControl(Context con, int isAddUseLog, int upsizelog, int isrealtime) {
		if (con == null) {
			return;
		}
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		System.out.println("saveLogControl:" + isAddUseLog + " -- " + upsizelog + " -- " + isrealtime);
		sp.edit().putInt(ISADDUSELOG, isAddUseLog).commit();
		sp.edit().putInt(UPSIZELOG, upsizelog).commit();
		sp.edit().putInt(ISREALTIME, isrealtime).commit();
	}

	public static int readisAddUseLog(Context con) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		System.out.println("readisAddUseLog:" + sp.getInt(ISADDUSELOG, 0));
		return sp.getInt(ISADDUSELOG, 0);
	}

	public static int readUpsizelog(Context con) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		System.out.println("readUpsizelog:" + sp.getInt(UPSIZELOG, 200));
		return sp.getInt(UPSIZELOG, 200);
	}

	public static int readIsrealtime(Context con) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		System.out.println("readIsrealtime:" + sp.getInt(ISREALTIME, 0));
		return sp.getInt(ISREALTIME, 1);
	}

	/**
	 * 保存拍照图片
	 * 
	 */
	public static void saveTakePhotoContent(Context con, String filePath, String fileType) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		sp.edit().putString(FILE_PATH, filePath).commit();// 拍照图片路径
		sp.edit().putString(FILE_TYPE, fileType).commit();// 图片类型
	}

	/**
	 * 获取拍照图片路径
	 * 
	 */
	public static String getTakePhotoPath(Context con) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		return sp.getString(FILE_PATH, "");
	}

	/**
	 * 获取拍照图片类型
	 * 
	 */
	public static String getTakePhotoType(Context con) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		return sp.getString(FILE_TYPE, "");
	}

	/**
	 * 保存我的头像Url
	 */
	public static void saveMyFaceUrl(Context con, String faceUrl) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		sp.edit().putString(MY_FACE_URL, faceUrl).commit();
	}

	/**
	 * 获取我的头像Url
	 */
	public static String getMyFaceUrl(Context con) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		return sp.getString(MY_FACE_URL, "");
	}

	/**
	 * 保存我的用户名称
	 */
	public static void saveMyUserName(Context con, String username) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		sp.edit().putString(MY_USER_NAME, username).commit();
	}

	/**
	 * 获取我的用户名称
	 */
	public static String getMyUserName(Context con) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		return sp.getString(MY_USER_NAME, "");
	}

	/**
	 * 保存我的性别
	 */
	public static void saveMyUserSex(Context con, String faceUrl) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		sp.edit().putString(MY_USER_SEX, faceUrl).commit();
	}

	/**
	 * 获取我的性别
	 */
	public static String getMyUserSex(Context con) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		return sp.getString(MY_USER_SEX, "");
	}

	/**
	 * 保存我的位置
	 */
	public static void saveMyUserLocation(Context con, String faceUrl) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		sp.edit().putString(MY_USER_LOCATION, faceUrl).commit();
	}

	/**
	 * 获取我的位置
	 */
	public static String getMyUserLocation(Context con) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		return sp.getString(MY_USER_LOCATION, "");
	}

	/**
	 * 设置是否使用扬声器
	 */
	public static void saveUseSpeak(Context con, String faceUrl) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		sp.edit().putString(USE_SPEARK, faceUrl).commit();
	}

	/**
	 * 获取是否使用扬声器
	 */
	public static String getUseSPEAK(Context con) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		return sp.getString(USE_SPEARK, "");
	}

	/**
	 * 设置是否使用语音播报
	 */
	public static void saveUseSpeech(Context con, String faceUrl) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		sp.edit().putString(USE_SPEECH, faceUrl).commit();
	}

	/**
	 * 获取是否使用语音播报
	 */
	public static String getUseSpeech(Context con) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		return sp.getString(USE_SPEECH, "true");
	}

	/**
	 * 设置是否可以获取本地位置 true 允许 false 不允许
	 */
	public static void saveSetLocalPosition(Context con, boolean isAllow) {
		Logger.e("Pref", "saveSetLocalPosition:" + isAllow);
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		sp.edit().putBoolean(GET_LOCAL_POSITION, isAllow).commit();
	}

	/**
	 * 获取否可以获取本地位置
	 */
	public static boolean getSetLocalPosition(Context con) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		return sp.getBoolean(GET_LOCAL_POSITION, false);
	}

	/**
	 * 设置是否可以获取通讯录
	 */
	public static void savePhoneNumberPermisstion(Context con, String tel) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		sp.edit().putString(PHONE_NUMBER_PERMISSTION, tel).commit();
	}

	/**
	 * 获取否可以获取本地通讯录
	 */
	public static String getPhoneNumberPermisstion(Context con) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		return sp.getString(PHONE_NUMBER_PERMISSTION, "否");
	}

	/**
	 * 设置是否第一次进入该界面
	 */
	public static void savefirstenter(Context con, String faceUrl) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		sp.edit().putString(FIRST_ENTER, faceUrl).commit();
	}

	/**
	 * 获取否弹出动画
	 */
	public static String readfirstenter(Context con) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		return sp.getString(FIRST_ENTER, "否");
	}

	/**
	 * 保存欢迎界面的url
	 */
	public static void saveWelcomeUrl(Context con, String faceUrl) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		sp.edit().putString(WELCOME_URL, faceUrl).commit();
	}

	/**
	 * 获取欢迎界面的url
	 */
	public static String getWelComeUrl(Context con) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		return sp.getString(WELCOME_URL, "");
	}

	/**
	 * 保存上传时间间隔时间
	 */
	public static void saveTraceInterval(Context con, int time) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		sp.edit().putInt(TRACE_INTERVAL, time).commit();
	}

	/**
	 * 获取上传时间间隔时间
	 */
	public static int getTraceInterval(Context con) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		return sp.getInt(TRACE_INTERVAL, 5);
	}
	

	/**
	 * 保存是否显示上班打点
	 */
	public static void saveShowSignon(Context con, String time) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		sp.edit().putString(SHOW_SIGNON, time).commit();
	}

	/**
	 * 获取是否显示上班打点
	 */
	public static String getShowSignon(Context con) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		return sp.getString(SHOW_SIGNON, "false");
	}

	/**
	 * 保存主界面模块
	 */
	public static void saveMainModules(Context con, String faceUrl) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		sp.edit().putString(MAIN_MODULES, faceUrl).commit();
	}

	/**
	 * 获取主界面模块
	 */
	public static String getMainModules(Context con) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		return sp.getString(MAIN_MODULES, "");
	}

	/**
	 * 保存发现模块
	 */
	public static void saveFindModules(Context con, String faceUrl) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		sp.edit().putString(FIND_MODULES, faceUrl).commit();
	}

	/**
	 * 获取发现模块
	 */
	public static String getFindModules(Context con) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		return sp.getString(FIND_MODULES, "");
	}

	/**
	 * 保存今日日报
	 */
	public static void saveTodayDaily(Context con, String faceUrl) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		sp.edit().putString(WRITE_DAILY_TODAY, faceUrl).commit();
	}

	/**
	 * 获取今日日报
	 */
	public static String getTodayDaily(Context con) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		return sp.getString(WRITE_DAILY_TODAY, "");
	}

	/**
	 * 保存明日日报
	 */
	public static void saveTomorrowDaily(Context con, String faceUrl) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		sp.edit().putString(WRITE_DAILY_TOMORROW, faceUrl).commit();
	}

	/**
	 * 获取明日日报
	 */
	public static String getTomorrowDaily(Context con) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		return sp.getString(WRITE_DAILY_TOMORROW, "");
	}
	
	/**
	 * 保存cid
	 */
	public static void saveCid(Context con, int cid) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		sp.edit().putInt(CID, cid).commit();
	}

	/**
	 * 获取Cid
	 */
	public static int getCid(Context con) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		return sp.getInt(CID, 0);
	}

	/**
	 * 保存signal
	 */
	public static void saveSignal(Context con, int signal) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		sp.edit().putInt(SIGNAL, signal).commit();
	}

	/**
	 * 获取signal
	 */
	public static int getSignal(Context con) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		return sp.getInt(SIGNAL, 0);
	}

	/**
	 * 保存wifi的mac
	 */
	public static void saveWifiMac(Context con, String wifiMac) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		sp.edit().putString(WIFI_MAC, wifiMac).commit();
	}

	/**
	 * 获取wifi的mac
	 */
	public static String getWifiMac(Context con) {
		SharedPreferences sp = con.getSharedPreferences(CONFIG_FILES, Context.MODE_PRIVATE);
		return sp.getString(WIFI_MAC, "");
	}

}
