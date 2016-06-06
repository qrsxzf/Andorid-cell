package com.qdb.agent.comm;

import android.content.Context;

import com.qdb.agent.utils.PackageInfoUtil;

public class Constant {
//	public static String NEW_URL = "http://a.qqqdb.com:8070/api";// 线下
//	public static String NEW_URL = "http://api.qqqdb.com:80/api";// 线上
	public static String NEW_URL = "http://192.168.1.164:8070";// 线上
	// log
	public static boolean isDebug = false;


	public final static int TAKE_PICTURE = 1;// 拍照
	public static int CHOOSE_PICTURE = 2;// 选照片

	public static final int TYPE_SIGN0 = 0;
	public static final int TYPE_SIGN1 = 1;


	public static final String SELECTMODE_SINGLE = "single";
	public static final String SELECTMODE_MULTI = "multi";

	public static int SENDTYPE = 100;
	public static final String QRCODE_MAKEFRENDS = "qrcode_makefrends";
	public static final String QRCODE_SIGN = "qrcode_sign";

	/**
	 * 环信添加
	 */
	public static final String NEW_FRIENDS_USERNAME = "item_new_friends";
	public static final String GROUP_USERNAME = "item_groups";
	public static final String CHAT_ROOM = "item_chatroom";
	public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
	public static final String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";
	public static final String MESSAGE_TYPE = "msgtype";
	public static final String MESSAGE_ATTR_SYSTEM_MESSAGE = "msg_ext";
	public static final String ACCOUNT_REMOVED = "account_removed";
	// 环信扩展获取好友位置（透传消息action）
	public static final String GET_FRIEND_LOCATION_ACTION = "get_friend_location_action";

	public static final String broadmsg_uid = "admin_broadcast_send"; // 发送端
	public static final String feedback_uid = "admin_feedback";
	public static final String admin = "admin"; // 系统消息跳转不能回复显示为系统消息

	public static final String admin_broadcast = "admin_broadcast";
	public static final String admin_group = "admin_group";// 系统建群群组id
	public static final String SPLIT = "66split88";// 好友邀请、群申请分隔符

	/**
	 * 所有关注的透传消息刷新UI广播通知
	 */
	public static final String FOCUS_REFRESH_UI_BROADCAST_ACTION = "focus_refresh_ui_broadcast_action"; // 所有关注的透传消息刷新UI广播通知

	public static String PhoneListName = "phoneList";

	/**
	 * 获取是否debug模式
	 */
	public static boolean getIsDebug() {
		// if (THE_CURRENT_STATE == PRODUCTION) {
		// return false;
		// } else {
		// return true;
		// }
		return isDebug;
	}

	/**
	 * 获取请求http url
	 */
	public static String getUrl() {
		return NEW_URL;
	}

	/**
	 * 获取请求https url
	 */
	public static String getPayUrl() {
		return NEW_URL;
	}

	/**
	 * 获取幻信KEY
	 */
	public static String getHXKey() {
		return "qdb#qiandaobao";
	}
	
	public static String getUserAgent(Context context) {
		// qdb/mobsaas;android/4.22;ver/2.0.45
		String userAgent = "qdb/agent;android/";
		String androidSdkVersion = android.os.Build.VERSION.RELEASE + ";";
		String appVersion = "ver/" + PackageInfoUtil.getAppVersion(context);
		return userAgent + androidSdkVersion + appVersion;
	}

}
