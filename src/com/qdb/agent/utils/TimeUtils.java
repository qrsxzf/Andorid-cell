package com.qdb.agent.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.content.Context;
import android.text.format.DateUtils;
import android.text.format.Time;

/**
 * TimeUtils
 * 
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-8-24
 */
public class TimeUtils {

	public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat DATE_FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * long time to string
	 * 
	 * @param timeInMillis
	 * @param dateFormat
	 * @return
	 */
	public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
		return dateFormat.format(new Date(timeInMillis));
	}

	/**
	 * long time to string, format is {@link #DEFAULT_DATE_FORMAT}
	 * 
	 * @param timeInMillis
	 * @return
	 */
	public static String getTime(long timeInMillis) {
		return getTime(timeInMillis, DEFAULT_DATE_FORMAT);
	}

	/**
	 * get current time in milliseconds
	 * 
	 * @return
	 */
	public static long getCurrentTimeInLong() {
		return System.currentTimeMillis();
	}

	/**
	 * get current time in milliseconds, format is {@link #DEFAULT_DATE_FORMAT}
	 * 
	 * @return
	 */
	public static String getCurrentTimeInString() {
		return getTime(getCurrentTimeInLong());
	}

	/**
	 * get current time in milliseconds
	 * 
	 * @return
	 */
	public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {
		return getTime(getCurrentTimeInLong(), dateFormat);
	}

	/**
	 * 比较两个时间的大小
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static boolean compare(String time1, String time2) {
		if (changeToLong(time1+":00") - changeToLong(time2+":00") >= 0) {
			return false;
		}
		return true;
	}

	/**
	 * 获取当前时间
	 * 
	 * @return 日期格式：yyyy-mm-dd hh:mm:ss
	 */
	public static String getCurrentTime() {
		Date d = new Date(System.currentTimeMillis());
		SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return mSimpleDateFormat.format(d);
	}

	public static String getCurrentTime2() {
		Date d = new Date(System.currentTimeMillis());
		SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return mSimpleDateFormat.format(d);
	}

	/**
	 * 单数不补零
	 * 
	 * @param timeStr
	 * @param day
	 * @return
	 */
	public static String getDateSolo(int day) {
		long time = System.currentTimeMillis();
		time += day * 1000 * 3600 * 24l;
		java.util.Date date = new java.util.Date(time);
		// datestr = "2012-8-17 14:33:00";
		SimpleDateFormat df = new SimpleDateFormat("M月d日");
		return df.format(date);
	}

	public static long changeToLong(String datestr) {
		if (datestr == null || datestr.equals(""))
			return -1;
		// datestr = "2012-8-17 14:33:00";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.sql.Date olddate = null;
		long date = 0;
		try {
			df.setLenient(false);
			olddate = new java.sql.Date(df.parse(datestr).getTime());
			date = df.parse(datestr).getTime();

		} catch (ParseException e) {
			// throw new RuntimeException("日期转换错误");
			return System.currentTimeMillis();
		}
		return date;
	}

	/**
	 * long型转换成日期(存库用)
	 * 
	 * @param date
	 * @return
	 */
	public static String toDate(long date) {
		if (date != 0) { // kk:mm:ss
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return sdf.format(new Date(date));
			// return DateFormat.format("yyyy-MM-dd kk:mm:ss", date).toString();
		}
		return null;
	}
	public static String toDate3(long date) {
		if (date != 0) { // kk:mm
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			return sdf.format(new Date(date));
		}
		return null;
	}

	public static String toDate2(long date) {
		if (date != 0) { // kk:mm:ss
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			return sdf.format(new Date(date));
			// return DateFormat.format("yyyy-MM-dd kk:mm:ss", date).toString();
		}
		return null;
	}

	/**
	 * long型转换成日期(只获取yyyy年MM月dd日)
	 * 
	 * @param date
	 * @return
	 */
	public static String longToDate(long date) {
		if (date != 0) { // kk:mm:ss
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
			return sdf.format(new Date(date));
			// return DateFormat.format("yyyy-MM-dd kk:mm:ss", date).toString();
		}
		return null;
	}

	/**
	 * long型转换成日期
	 * 
	 * @param date
	 * @param format
	 *            格式
	 * @return
	 */
	public static String toDate(long date, String format) {
		if (date != 0) {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.format(new Date(date));
		}
		return null;
	}

	// 9/25 9月25日
	public static String toDateFormat(String oldTime) {
		String newTime = "";
		newTime = oldTime.replaceAll("日", "");
		newTime = newTime.replace("月", "/");
		if (newTime.equals("刚刚"))
			newTime = "今天";
		if (newTime.contains(":")) // 11:40
			newTime = "今天";
		return newTime;
	}

	/**
	 * long型转换成日期(通话记录显示)
	 * 
	 * @param date
	 * @return
	 */
	public static String toRecordDate(long date) {
		Calendar cal = Calendar.getInstance(Locale.CHINA);
		int Year = cal.get(Calendar.YEAR);
		int Month = cal.get(Calendar.MONTH);
		int Day = cal.get(Calendar.DAY_OF_MONTH);
		cal.set(Calendar.YEAR, Year);
		cal.set(Calendar.MONTH, Month);
		cal.set(Calendar.DAY_OF_MONTH, Day);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		long today = cal.getTimeInMillis();
		int NewDay = Day - 1;
		cal.set(Calendar.YEAR, Year);
		cal.set(Calendar.MONTH, Month);
		cal.set(Calendar.DAY_OF_MONTH, NewDay);
		long yesterday = cal.getTimeInMillis();
		cal.set(Calendar.YEAR, Year);
		cal.set(Calendar.MONTH, Month);
		cal.set(Calendar.DAY_OF_MONTH, Day + 1);
		long tomorrow = cal.getTimeInMillis();
		cal.set(Calendar.YEAR, Year);
		cal.set(Calendar.MONTH, Month);
		cal.set(Calendar.DAY_OF_MONTH, Day - 8);
		long lastWeek = cal.getTimeInMillis();
		if (date != 0) { // kk:mm:ss
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String dateString = sdf.format(new Date(date));
			String backString = "";
			if (date > tomorrow) {
				backString = dateString.substring(5);
			} else if (date > today) {
				SimpleDateFormat sdftoday = new SimpleDateFormat("a HH:mm");
				backString = sdftoday.format(new Date(date));
			} else if (date > yesterday) {
				// context.getString(R.string.call_record_yesterday)
				backString = "昨天" + " " + dateString.substring(11);
			} else if (date > lastWeek) {
				Calendar calWeek = Calendar.getInstance(Locale.CHINA);
				calWeek.setTimeInMillis(date);
				backString = setWeek(calWeek.get(Calendar.DAY_OF_WEEK)) + " " + dateString.substring(11);
			} else {
				backString = dateString.substring(5);
			}

			String result = "";
			if (backString.contains("AM")) {
				result = backString.replace("AM", "上午");
			} else if (backString.contains("PM")) {
				result = backString.replace("PM", "下午");
			} else {
				result = backString;
			}
			return result;
			// return DateFormat.format("yyyy-MM-dd kk:mm:ss", date).toString();
		}
		return null;
	}

	/** 判断日期是否为今天 */
	public static boolean isToday(long date) {
		if (date <= 0) {
			return false;
		}
		Calendar cal = Calendar.getInstance(Locale.CHINA);
		int Year = cal.get(Calendar.YEAR);
		int Month = cal.get(Calendar.MONTH);
		int Day = cal.get(Calendar.DAY_OF_MONTH);
		cal.set(Calendar.YEAR, Year);
		cal.set(Calendar.MONTH, Month);
		cal.set(Calendar.DAY_OF_MONTH, Day);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		long today = cal.getTimeInMillis();
		if (date != 0) { // kk:mm:ss
			if (date > today) {
				return true;
			}
		}
		return false;
	}

	private static String setWeek(int week) {
		switch (week) {
		case Calendar.SUNDAY:
			return "周日";
		case Calendar.MONDAY:
			return "周一";
		case Calendar.TUESDAY:
			return "周二";
		case Calendar.WEDNESDAY:
			return "周三";
		case Calendar.THURSDAY:
			return "周四";
		case Calendar.FRIDAY:
			return "周五";
		case Calendar.SATURDAY:
			return "周六";
		default:
			return "";
		}
	}

	/**
	 * long型转换成日期(通话记录显示)
	 * 
	 * @param date
	 * @return
	 */
	public static String[] toRecordDates(long date) {
		String[] returnDates = new String[2];
		Calendar cal = Calendar.getInstance(Locale.CHINA);
		int Year = cal.get(Calendar.YEAR);
		int Month = cal.get(Calendar.MONTH);
		int Day = cal.get(Calendar.DAY_OF_MONTH);
		cal.set(Calendar.YEAR, Year);
		cal.set(Calendar.MONTH, Month);
		cal.set(Calendar.DAY_OF_MONTH, Day);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		long today = cal.getTimeInMillis();
		int NewDay = Day - 1;
		cal.set(Calendar.YEAR, Year);
		cal.set(Calendar.MONTH, Month);
		cal.set(Calendar.DAY_OF_MONTH, NewDay);
		long yesterday = cal.getTimeInMillis();
		cal.set(Calendar.YEAR, Year);
		cal.set(Calendar.MONTH, Month);
		cal.set(Calendar.DAY_OF_MONTH, Day + 1);
		long tomorrow = cal.getTimeInMillis();
		if (date != 0) { // kk:mm:ss
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String dateString = sdf.format(new Date(date));
			String backString = "";
			if (date > tomorrow) {
				// backString=dateString.substring(5);
				returnDates[0] = dateString.substring(5, 10);
				returnDates[1] = dateString.substring(11);
			} else if (date > today) {
				//
				// backString=MyApplication.getInstance().getString(R.string.info_data_today)
				// +" "+dateString.substring(11);
				returnDates[0] = "今天";
				returnDates[1] = dateString.substring(11);
			} else if (date > yesterday) {
				returnDates[0] = "昨天";
				returnDates[1] = dateString.substring(11);
				// backString=MyApplication.getInstance().getString(R.string.info_data_yesterday)
				// +" "+dateString.substring(11);
			} else {
				// backString=dateString.substring(5);
				returnDates[0] = dateString.substring(5, 10);
				returnDates[1] = dateString.substring(11);
			}

			return returnDates;
			// return DateFormat.format("yyyy-MM-dd kk:mm:ss", date).toString();
		}
		return null;
	}

	/**
	 * long型转换成日期(存库用)
	 * 
	 * @param date
	 * @return
	 */
	public static String toServiceVisitDate(long date) {
		if (date != 0) { // kk:mm:ss
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			return sdf.format(new Date(date));
			// return DateFormat.format("yyyy-MM-dd kk:mm:ss", date).toString();
		}
		return null;
	}

	/**
	 * 转换成通话记录显示的形式
	 * 
	 * @param date
	 * @return
	 * @throws java.text.ParseException
	 */
	public static String setDateToShow(String date) {
		String showDate = null;
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				Date dt2 = sdf.parse(date);
				// 继续转换得到秒数的long型
				// long lTime = dt2.getTime() / 1000;
				SimpleDateFormat sdf_show = new SimpleDateFormat("yyyy-MM-dd");
				showDate = sdf_show.format(dt2);
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return showDate;
	}

	public static String time2Date(String date) {
		String showDate = null;
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				Date dt2 = sdf.parse(date);
				// 继续转换得到秒数的long型
				// long lTime = dt2.getTime() / 1000;
				SimpleDateFormat sdf_show = new SimpleDateFormat("yyyy年MM月dd日");
				showDate = sdf_show.format(dt2);
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return showDate;
	}

	public static String setDateToShow2(String date) {
		String showDate = "";
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				Date dt2 = sdf.parse(date);
				// 继续转换得到秒数的long型
				// long lTime = dt2.getTime() / 1000;
				SimpleDateFormat sdf_show = new SimpleDateFormat("yyyy-MM-dd");
				showDate = sdf_show.format(dt2);
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return showDate;
	}

	/**
	 * 
	 * @param datestr
	 *            日期字符串
	 * @param day
	 *            相对天数，为正数表示之后，为负数表示之前
	 * @return 指定日期字符串n天之前或者之后的日期
	 */
	public static Calendar getBeforeAfterDate(String datestr, int day) {
		// datestr = "2012-8-17";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		java.sql.Date olddate = null;
		try {
			df.setLenient(false);
			olddate = new java.sql.Date(df.parse(datestr).getTime());
		} catch (ParseException e) {
			throw new RuntimeException("日期转换错误");
		}
		Calendar cal = new GregorianCalendar();
		cal.setTime(olddate);

		int Year = cal.get(Calendar.YEAR);
		int Month = cal.get(Calendar.MONTH);
		int Day = cal.get(Calendar.DAY_OF_MONTH);

		int NewDay = Day + (day * 7);

		cal.set(Calendar.YEAR, Year);
		cal.set(Calendar.MONTH, Month);
		cal.set(Calendar.DAY_OF_MONTH, NewDay);

		// PrintLog(5, "结束日期", "datetime:" + getTaskDateFormat(cal));
		// PrintLog(5, "计算日期", "cal.getTimeInMillis():" +
		// cal.getTimeInMillis());
		// return new java.sql.Date(cal.getTimeInMillis());
		// return getTaskDateFormat(cal);
		return cal;
	}

	/**
	 * 通话时间 格式00:00:00
	 * 
	 * @param duration
	 * @return
	 */
	public static String getCallDurationShow(long duration) {
		if (duration / 60 == 0) {
			String second = "00";

			if (duration < 10) {
				second = "0" + duration;
			} else {
				second = duration + "";
			}
			return "00:" + second;
		} else {
			String minute = "00";
			String second = "00";
			String hour = "00";
			if ((duration / 60) < 10) {
				minute = "0" + (duration / 60);
			} else {
				if ((duration / 60) > 59) {
					if ((duration / 3600) < 10) {
						hour = "0" + (duration / 3600);
					} else {
						hour = (duration / 3600) + "";
					}

					if ((duration / 60) % 60 < 10) {
						minute = "0" + (duration / 60) % 60;
					} else {
						minute = (duration / 60) % 60 + "";
					}

				} else {
					minute = (duration / 60) + "";
				}
			}
			if ((duration % 60) < 10) {
				second = "0" + (duration % 60);
			} else {
				second = (duration % 60) + "";
			}
			if (hour.equals("00")) {
				return minute + ":" + second;
			} else {
				return hour + ":" + minute + ":" + second;
			}
		}
	}

	public static String formatTimeStampStringExtend(Context context, long when) {
		if (when != 0L) {
			Time then = new Time();
			then.set(when);
			Time now = new Time();
			now.setToNow();

			int format_flags = DateUtils.FORMAT_NO_NOON_MIDNIGHT | DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_CAP_AMPM;

			if (then.year != now.year) {
				format_flags |= DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE;
			} else if (then.yearDay != now.yearDay) {
				// If it is from a different day than today, show only the date.
				if ((now.yearDay - then.yearDay) == 1) {
					return "昨天";
				} else {
					format_flags |= DateUtils.FORMAT_SHOW_DATE;
				}
			} else if ((now.toMillis(false) - then.toMillis(false)) < 60000) {
				return "刚刚";
			} else {
				format_flags |= DateUtils.FORMAT_SHOW_TIME;
			}

			return DateUtils.formatDateTime(context, when, format_flags);
		}
		return null;
	}

	public static String getFormatStringTime(String time) {
		long changeToLong = changeToLong(time);
		return toDate(changeToLong);
	}

	public static String getFormatStringTime2(String time) {
		long changeToLong = changeToLong(time);
		return toDate2(changeToLong);
	}
	public static String getFormatStringTime3(String time) {
		long changeToLong = changeToLong(time);
		return toDate3(changeToLong);
	}
}
