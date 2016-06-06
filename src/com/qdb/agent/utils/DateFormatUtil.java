package com.qdb.agent.utils;

import android.annotation.SuppressLint;
import android.text.format.DateFormat;

@SuppressLint("SimpleDateFormat")
public class DateFormatUtil {

	public static String FORMAT = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 格式化时�?
	 * 
	 * @param datetime
	 * @return
	 */
	public static String format(long datetime, String format) {
		String datetimeStr = null;
		if (datetime != 0) {
			datetimeStr = DateFormat.format("yyyy:MM:dd kk:mm:ss", datetime)
					.toString();
		}
		return datetimeStr;
	}

	public static String formatSecond(String second) {
		String time = "0�?";
		if (!StringUtil.isBlank(second) && StringUtil.num(second)) {
			Double s = Double.parseDouble(second);
			String format;
			Object[] array;
			Integer hours = (int) (s / (60 * 60));
			Integer minutes = (int) (s / 60 - hours * 60);
			Integer seconds = (int) (s - minutes * 60 - hours * 60 * 60);
			if (hours > 0) {
				format = "%1$,d�?%2$,d�?%3$,d�?";
				array = new Object[] { hours, minutes, seconds };
			} else if (minutes > 0) {
				format = "%1$,d�?%2$,d�?";
				array = new Object[] { minutes, seconds };
			} else {
				format = "%1$,d�?";
				array = new Object[] { seconds };
			}
			time = String.format(format, array);
		}

		return time;

	}

}
