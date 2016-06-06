package com.qdb.agent.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.qdb.agent.R;


public class DownloadUtil {

	private Context mContext;
	private String mSavePath;
	private String mFileName;
	private File mApkFile;
	private int mUpdateProgress;
	private long mProcessUpdateTime;
	private boolean cancelUpdate = false;
	private Notification mDownloadNotification;
	private NotificationManager mDownloadNotificationManager;
	private static final int DOWNLOAD = 1001;
	private static final int DOWNLOAD_FINISH = 1002;

	private static DownloadUtil downloadUtil;
	private String TAG = "DownloadUtil";

	public static DownloadUtil getInstance(Context mContext) {
		if (downloadUtil == null) {
			downloadUtil = new DownloadUtil(mContext);
		}
		return downloadUtil;
	}

	public DownloadUtil(Context mContext) {
		this.mContext = mContext;
	}

	private void startDownload(String downloadUrl) {
		try {
			Looper.prepare();
			// 判断SD卡是否存在，并且是否具有读写权限
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				mSavePath = FileUtil.getDownloadCachePath(mContext);
				URL url = new URL(downloadUrl);
				mFileName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
				// 创建连接
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.connect();
				// 获取文件大小
				int length = conn.getContentLength();
				Logger.d(TAG, "length:" + length);
				// 创建输入流
				InputStream is = conn.getInputStream();

				mApkFile = new File(mSavePath, mFileName);
				// 判断文件目录是否存在
				if (mApkFile.exists()) {
					Logger.d(TAG, "mApkFile.delete() success");
					mApkFile.delete();
				}

				FileOutputStream fos = new FileOutputStream(mApkFile);
				int count = 0;
				// 缓存
				byte buf[] = new byte[1024];
				mProcessUpdateTime = System.currentTimeMillis();
				// 写入到文件中
				do {
					int numread = is.read(buf);
					count += numread;
					// 计算进度条位置
					mUpdateProgress = (int) (((float) count / length) * 100);
					long currentTime = System.currentTimeMillis();
					if (currentTime - mProcessUpdateTime > 500) {// 防止UI更新太频繁
						mProcessUpdateTime = currentTime;
						// 更新进度
						Message msg = mDownloadHandler.obtainMessage();
						msg.what = DOWNLOAD;
						msg.arg1 = mUpdateProgress;
						mDownloadHandler.sendMessage(msg);
					}

					if (numread <= 0) {
						// 下载完成
						cancelUpdate = true;
						Message doneMsg = mDownloadHandler.obtainMessage();
						doneMsg.what = DOWNLOAD_FINISH;
						mDownloadHandler.sendMessage(doneMsg);
						break;
					}
					// 写入文件
					fos.write(buf, 0, numread);
				} while (!cancelUpdate);
				fos.close();
				is.close();
			} else {
				Toast.makeText(mContext, mContext.getString(R.string.sdcard_error), Toast.LENGTH_SHORT).show();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Handler mDownloadHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case DOWNLOAD:
				int rate = msg.arg1;
				if (rate < 100) {
					// 更新进度
					RemoteViews contentView = mDownloadNotification.contentView;
					contentView.setTextViewText(R.id.rate, rate + "%");
					contentView.setProgressBar(R.id.progress, 100, rate, false);
				} else {
					// 下载完毕后变换通知形式
					mDownloadNotification.flags = Notification.FLAG_AUTO_CANCEL;
					mDownloadNotification.contentView = null;
					mDownloadNotification.setLatestEventInfo(mContext, "下载完成", "文件已下载完毕", null);
				}
				mDownloadNotificationManager.notify(0, mDownloadNotification);
				break;
			case DOWNLOAD_FINISH:
				// 取消通知
				mDownloadNotificationManager.cancel(0);
				Utility.installApk(mContext, mApkFile);
				break;
			}
		};
	};

	public void download(final String downloadUrl, int id) {
		mDownloadNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

		int icon = id;
		CharSequence tickerText = "开始下载";
		long when = System.currentTimeMillis();
		NotificationCompat.Builder builder = new Builder(mContext);
		mDownloadNotification = builder.build();
		mDownloadNotification.when = when;
		mDownloadNotification.tickerText = tickerText;
		mDownloadNotification.icon = icon;

		// 放置在"正在运行"栏目中
		mDownloadNotification.flags = Notification.FLAG_ONGOING_EVENT;

		RemoteViews contentView = new RemoteViews(mContext.getPackageName(), R.layout.download_notification_layout);
		contentView.setTextViewText(R.id.fileName, mContext.getResources().getString(R.string.app_name));
		contentView.setImageViewResource(R.id.imageView_notification, id);
		mDownloadNotification.contentView = contentView;

		// intent为null,表示点击通知时不跳转
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, new Intent(), 0);
		// 指定内容意图
		mDownloadNotification.contentIntent = contentIntent;

		builder.setContent(contentView).setSmallIcon(icon).setTicker(tickerText).setContentIntent(contentIntent).setAutoCancel(true)
				.setDefaults(Notification.FLAG_ONLY_ALERT_ONCE);
		mDownloadNotificationManager.notify(0, mDownloadNotification);

		new Thread() {
			public void run() {
				// http://m.dwuliu.com/apk/bill.apk
				// http://m.dwuliu.com/apk/car.apk
				Logger.d(TAG, "downloadUrl:" + downloadUrl);
				startDownload(downloadUrl);
			};
		}.start();
	}
}
