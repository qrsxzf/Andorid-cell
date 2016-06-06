package com.qdb.agent.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class LogUtil {
	public static File file = null;
	public static RandomAccessFile fWriter = null;
	public static Context mContext = null;
	private static int MAX_LOG_SIZE = 1 * 1024 * 1024;

	public static boolean LOG_LEVEL = false;

	@SuppressLint("SimpleDateFormat")
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 用于格式化日�?,作为日志文件名的�?部分

	public synchronized static void init(Context c) {
		mContext = c;
		LOG_LEVEL = SharedPreferencesUtil.readisAddUseLog(mContext) == 0 ? false : true;
		System.out.println("LOG_LEVEL:" + LOG_LEVEL);
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			try {
				String newLog = FileUtil.getLogCachePath(c) + File.separator + "mobsaaslog.log";
				file = new File(newLog);
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}

				boolean isFileExist = file.exists();
				if (fWriter != null) {
					fWriter.close();
				}

				fWriter = new RandomAccessFile(file, "rws");
				if (isFileExist) {
					fWriter.seek(file.length());
				}

				MyLog.getInstance();
				MyLog.initLog();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void appendLog(File file, String content, int level) {
		try {
			if (file == null || !file.exists()) {
				return;
			}
			// String time = format.format(new Date());
			StringBuffer sb = new StringBuffer();
			// sb.append(time);
			// sb.append("\t ");
			// sb.append(level == 1 ? "i" : level == 2 ? "w" : "e");
			// sb.append("\t");
			sb.append(content);
			sb.append("\r\n");

			final byte[] data = sb.toString().getBytes();
			fWriter.write(data);

		} catch (Exception e) {
			Log.e("MyLog", "log output exception,maybe the log file is not exists");
		} finally {
			if (file != null && file.length() >= MAX_LOG_SIZE) {
				init(mContext);
				return;
			}
		}
	}

	public static void destroy() {
		file = null;
	}

}
