package com.qdb.agent.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class ToastUtil {
	private static Handler handler = new Handler(Looper.getMainLooper());

	private static Toast toast = null;

	private static Object synObj = new Object();

	public static void showMessage( Context context,  String msg) {
		showMessage(context, msg, Toast.LENGTH_SHORT);
	}

	public static void showMessage(final Context context, final int msg) {
		showMessage(context, msg, Toast.LENGTH_SHORT);
	}

	public static void showMessage(final Context context,
			final CharSequence msg, final int len) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				synchronized (synObj) { // 加上同步是为了每个toast只要有机会显示出来
					if (toast != null) {
//						toast.cancel();
						toast.setText(msg);
						toast.setDuration(len);
					} else {
						toast = Toast.makeText(context, msg, len);
					}
					toast.show();
				}
			}
		});
	}

	public static void showMessage(final Context context, final int msg,
			final int len) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				synchronized (synObj) {
					if (toast != null) {
//						toast.cancel();
						toast.setText(msg);
						toast.setDuration(len);
					} else {
						toast = Toast.makeText(context, msg, len);
					}
					toast.show();
				}
			}
		});
	}
}
