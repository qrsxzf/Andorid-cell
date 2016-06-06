
package com.qdb.agent.utils;

import com.qdb.agent.comm.Constant;

import android.util.Log;

/**
 * Logger 
 * 
 */
public class Logger {

	public static boolean DEBUG = Constant.getIsDebug();

	public static void i(String tag ,String message) {
		if (DEBUG) {
			Log.i(tag, message);
		}
	}

	public static void e(String tag ,String message) {
		if (DEBUG) {
			Log.e(tag, message);
		}
	}

	public static void d(String tag ,String message) {
		if (DEBUG) {
			Log.d(tag, message);
		}
	}

	public static void w(String tag ,String message) {
		if (DEBUG) {
			Log.w(tag, message);
		}
	}

	public static void w(String tag ,String message, Throwable tr) {
		if (DEBUG) {
			Log.w(tag, message, tr);
		}
	}


	public static void openLog() {
		DEBUG = true;
	}

	public static void closeLog() {
		DEBUG = false;
	}

}
