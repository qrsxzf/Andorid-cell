package com.qdb.agent.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.qdb.agent.comm.Constant;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * UncaughtException处理�?,当程序发生Uncaught异常的时�?,由该类来接管程序,并记录发送错误报�?.
 * 
 */
public class MyLog extends LogUtil implements UncaughtExceptionHandler, Runnable {
	private static Thread.UncaughtExceptionHandler mDefaultHandler;// 系统默认的UncaughtException处理�?
	private static MyLog crashLog;
	private Map<String, String> info = new HashMap<String, String>();// 用来存储设备信息和异常信�?

	public final static boolean LOG_YES = Constant.getIsDebug();// 是否�?启操作LOG
	public final static boolean ANDROID_LOG = Constant.getIsDebug();// 是否�?启系统LOG
	public static volatile boolean sendUDP = Constant.getIsDebug();// 是否发�?�LOG到控制台
	private static String packageName = "";

	/** 获取CrashHandler实例 */
	public static MyLog getInstance() {
		if (crashLog == null) {
			crashLog = new MyLog();
			mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();// 获取系统默认的UncaughtException处理�?
			Thread.setDefaultUncaughtExceptionHandler(crashLog);// 设置该CrashHandler为程序的默认处理�?
			
			if(LOG_YES){
				packageName = android.os.Build.MODEL + "  " + android.os.Build.BRAND
						+ "---Mobsaas---";
			}else{
				packageName = "";
			}
		}
		return crashLog;
	}

	static synchronized void initLog() {
		if (LOG_LEVEL || LOG_YES) {
			String broadip = "230.24.11.19";
			setBroadCastIP(broadip);
			new Thread(crashLog).start();
		}
	}

	public static void setBroadCastIP(String gip) {
		try {
			ip = InetAddress.getByName(gip);
		} catch (Exception ex) {
		}
	}

	/**
	 * 当UncaughtException发生时会转入该重写的方法来处�?
	 */
	public void uncaughtException(Thread thread, Throwable ex) {
		// 收集设备参数信息
		collectDeviceInfo(mContext);
		// 保存日志文件
		saveCrashInfoFile(ex);
		mDefaultHandler.uncaughtException(thread, ex);
	}

	/**
	 * 收集设备参数信息
	 * 
	 * @param context
	 */
	public void collectDeviceInfo(Context context) {
		try {
			PackageManager pm = context.getPackageManager();// 获得包管理器
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
					PackageManager.GET_ACTIVITIES);// 得到该应用的信息，即主Activity
			if (pi != null) {
				String versionName = pi.versionName == null ? "null" : pi.versionName;
				info.put("versionName", versionName);
				info.put("brand", android.os.Build.BRAND);
				info.put("model", android.os.Build.MODEL);
				info.put("versionname", android.os.Build.VERSION.RELEASE);
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

	}

	private void saveCrashInfoFile(Throwable ex) {
		StringBuffer sb = new StringBuffer();
		sb.append(formatter.format(new Date()) + "*****" + packageName + " ERROR   ");
		sb.append("application of abnormal exit!" + "\r\n");
		for (Map.Entry<String, String> entry : info.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + " = " + value + "\r\n");
		}

		Writer writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		ex.printStackTrace(pw);
		Throwable cause = ex.getCause();
		// 循环�?把所有的异常信息写入writer�?
		while (cause != null) {
			cause.printStackTrace(pw);
			cause = cause.getCause();
		}
		pw.close();// 记得关闭
		String result = writer.toString();
		sb.append(result);
		msgs.add(sb.toString());
		// appendLog(file, sb.toString(), 4);
	}

	// Log
	private final static LinkedList<String> msgs = new LinkedList<String>();
	private static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public final static int port = 6564;
	private static volatile boolean endLog = false;
	private static InetAddress ip;
	private static MulticastSocket sendSocket;

	@Override
	public void run() {
		if (LOG_LEVEL || LOG_YES) {
			try {
				if (sendUDP && sendSocket == null) {
					try {
						sendSocket = new MulticastSocket();
					} catch (Throwable ex) {
						return;
					}
				}
				for (; !endLog;) {
					String data;
					synchronized (msgs) {
						data = msgs.poll();
					}
					if (data == null) {
						try {
							Thread.sleep(100);
						} catch (Exception e) {
						}
					} else {
						try {
							appendLog(file, data, 4);
							if (sendUDP) {
								sendSocket.send(new DatagramPacket(data.getBytes(),
										data.getBytes().length, ip, port));
							}
						} catch (Exception ex) {
						}
					}
				}

			} catch (Throwable e) {
				e("Log", e);
			}
			if(sendSocket!=null){
				sendSocket.close();
				sendSocket = null;
				endLog = false;
			}
		}

	}

	public static void closeLog() {
		if (LOG_LEVEL || LOG_YES) {
			i("CrashHandlerLog", "closeLog");
			int i = 10;
			while (--i > 0 && msgs.size() > 0) {
				try {
					Thread.sleep(300);
				} catch (InterruptedException ex) {
				}
			}
			if (fWriter != null) {
				try {
					fWriter.close();
				} catch (Exception e) {
				}
			}
			endLog = true;
		}
	}

	public static void e(String TAG, Throwable e) {
		if (LOG_LEVEL || LOG_YES) {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			PrintStream out = new PrintStream(bout);
			e.printStackTrace(out);
			out.close();
			e(TAG, new String(bout.toByteArray()));
		}
	}

	public static void e(String TAG, String msg) {
		if (LOG_LEVEL || LOG_YES) {
			synchronized (msgs) {
				msgs.add(formatter.format(new Date()) + "*****" + packageName + " ERROR " + TAG
						+ " :" + msg + "\n");
			}
		}
		if (ANDROID_LOG) {
			android.util.Log.e(TAG, msg);
		}
	}

	public static void i(String TAG, String msg) {
		if (LOG_LEVEL || LOG_YES) {
			synchronized (msgs) {
				msgs.add(formatter.format(new Date()) + "*****" + packageName + " INFO " + TAG
						+ " :" + msg + "\n");
			}
		}
		if (ANDROID_LOG) {
			android.util.Log.i(TAG, msg);
		}
	}

	public static void w(String TAG, String msg) {
		if (LOG_LEVEL || LOG_YES) {
			synchronized (msgs) {
				msgs.add(formatter.format(new Date()) + "*****" + packageName + " WARNNING " + TAG
						+ " :" + msg + "\n");
			}
		}
		if (ANDROID_LOG) {
			android.util.Log.i(TAG, msg);
		}
	}

	public static void d(String TAG, String msg) {
		if (LOG_LEVEL || LOG_YES) {
			synchronized (msgs) {
				msgs.add(formatter.format(new Date()) + "*****" + packageName + " DEBUG " + TAG
						+ " :" + msg + "\n");
			}
		}
		if (ANDROID_LOG) {
			android.util.Log.i(TAG, msg);
		}
	}

}
