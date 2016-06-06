package com.qdb.agent.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.text.DecimalFormat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

/**
 * 文件工具�?
 * 
 * 
 */
public class FileUtil {
	private static String TAG = "FileUtil";

	/**
	 * 获取目录名称
	 * 
	 * @param url
	 * @return FileName
	 */
	public static String getFileName(String url) {
		int lastIndexStart = url.lastIndexOf("/");
		if (lastIndexStart != -1) {
			return url.substring(lastIndexStart + 1, url.length());
		} else {
			return new Timestamp(System.currentTimeMillis()).toString();
		}
	}

	/**
	 * 判断SD卡是否存�?
	 * 
	 * @return boolean
	 */
	public static boolean checkSDCard() {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	public static String getLogCachePath(Context context) {
		String path = getPhoneMemoryPath(context) + File.separator + "MobsaasAgent" + File.separator + "logcache";
		createNewDir(path);
		return path;
	}

	public static String getDownloadCachePath(Context context) {
		String path = getPhoneMemoryPath(context) + File.separator + "MobsaasAgent" + File.separator + "download";
		createNewDir(path);
		return path;
	}

	public static String getDbPath(Context context) {
		String path = getPhoneMemoryPath(context) + File.separator + "MobsaasAgent" + File.separator + "database" + File.separator;
		createNewDir(path);
		return path;
	}

	public static String getMediaCachePath(Context context) {
		String path = getPhoneMemoryPath(context) + File.separator + "MobsaasAgent" + File.separator + "cache";
		createNewDir(path);
		return path;
	}

	public static String getFileCachePath(Context context) {
		String path = getPhoneMemoryPath(context) + File.separator + "MobsaasAgent" + File.separator + "file";
		createNewDir(path);
		return path;
	}

	public static String getPicLocPath(Context context) {
		String path = getPhoneMemoryPath(context) + File.separator + "MobsaasAgent" + File.separator + "sjbimg/";
		createNewDir(path);
		return path;
	}

	public static void createNewDir(String dir) {
		if (null == dir) {
			return;
		}
		File f = new File(dir);
		if (!f.exists()) {
			String[] pathSeg = dir.split(File.separator);
			String path = "";
			for (String temp : pathSeg) {
				if (TextUtils.isEmpty(temp)) {
					path += File.separator;
					continue;
				} else {
					path += temp + File.separator;
				}
				File tempPath = new File(path);
				if (tempPath.exists() && !tempPath.isDirectory()) {
					tempPath.delete();
				}
				tempPath.mkdirs();
			}
		} else {
			if (!f.isDirectory()) {
				f.delete();
				f.mkdirs();
			}
		}
	}

	/**
	 * 获取手机可用的存储路径， SD可用且大�?5M
	 * 
	 */
	public static String getPhoneMemoryPath(Context c) {
		String sdStatus = Environment.getExternalStorageState();
		boolean sdCardExist = sdStatus.equals(android.os.Environment.MEDIA_MOUNTED);

		if (TextUtils.isEmpty(sdStatus)) {
			return c.getFilesDir().getAbsolutePath();
		}

		if (!sdCardExist) {
			long phoneSpace = getDataStorageAvailableSpace();
			return c.getFilesDir().getAbsolutePath();
		}

		try {
			long sdcardSpace = 0;
			try {
				sdcardSpace = getSDcardAvailableSpace();
			} catch (Exception e) {
				Log.d(TAG, "error1:" + e.getMessage());
			}
			if (sdcardSpace >= 5) {
				return getSDCardPath(c);
			}

			long phoneSpace = getDataStorageAvailableSpace();
			if (phoneSpace >= 5) {
				return c.getFilesDir().getAbsolutePath();
			}
			Log.d(TAG, String.format("get storage space, phone: %d, sdcard: %d", (int) (phoneSpace / 1024 / 1024), (int) (sdcardSpace / 1024 / 1024)));
		} catch (Exception e) {
			Log.d(TAG, "error3:" + e.getMessage());
		}

		return c.getFilesDir().getAbsolutePath();
	}

	/**
	 * 获取手机内部可用空间大小
	 * 
	 * @return
	 */
	public static long getDataStorageAvailableSpace() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	/**
	 * 获取手机内置SD卡可用空间大�?
	 * 
	 */
	public static long getSDcardAvailableSpace() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File path = Environment.getExternalStorageDirectory();
			if (path == null) {
				return 0;
			}
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			return availableBlocks * blockSize; // "Byte"
		} else {
			return 0;
		}
	}

	/**
	 * 获取手机内置SD卡路�?
	 * 
	 */
	public static String getSDCardPath(Context c) {
		File sdDir = null;
		String sdStatus = Environment.getExternalStorageState();
		boolean sdCardExist = sdStatus.equals(android.os.Environment.MEDIA_MOUNTED);

		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();
			return sdDir.toString();
		}
		return "";
	}

	/**
	 * 获取文件的大�?
	 * 
	 * @param fileSize
	 *            文件的大�?
	 * @return
	 */
	public static String FormetFileSize(int fileSize) {// 转换文件大小
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileSize < 1024) {
			fileSizeString = df.format((double) fileSize) + "B";
		} else if (fileSize < 1048576) {
			fileSizeString = df.format((double) fileSize / 1024) + "K";
		} else if (fileSize < 1073741824) {
			fileSizeString = df.format((double) fileSize / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileSize / 1073741824) + "G";
		}
		return fileSizeString;
	}

	public static boolean isValidAttach(String path, boolean inspectSize) {
		if (!isExistsFile(path) || getFileSize(path) == 0) {
			MyLog.e(TAG, "isValidAttach: file is not exist, or size is 0");
			return false;
		}
		if (inspectSize && getFileSize(path) > 2 * 1024 * 1024) {
			MyLog.e(TAG, "file size is too large");
			return false;
		}
		return true;
	}

	public static boolean isExistsFile(String filepath) {
		try {
			if (TextUtils.isEmpty(filepath)) {
				return false;
			}
			File file = new File(filepath);
			return file.exists();
		} catch (Exception e) {
			// e.printStackTrace();
			Log.d(TAG, "the file is not exists file path is: " + filepath);
			return false;
		}
	}

	public static int getFileSize(String filepath) {
		try {
			if (TextUtils.isEmpty(filepath)) {
				return -1;
			}
			File file = new File(filepath);
			return (int) file.length();
		} catch (Exception e) {
			return -1;
		}
	}

	public static void copy(String src, String dest) {
		if (TextUtils.isEmpty(src) || TextUtils.isEmpty(dest)) {
			return;
		}
		InputStream is = null;
		OutputStream os = null;
		File out = new File(dest);
		if (!out.getParentFile().exists()) {
			out.getParentFile().mkdirs();
		}
		try {
			is = new BufferedInputStream(new FileInputStream(src));
			os = new BufferedOutputStream(new FileOutputStream(dest));
			byte[] b = new byte[256];
			int len = 0;
			try {
				while ((len = is.read(b)) != -1) {
					os.write(b, 0, len);
				}
				os.flush();
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						Log.e(TAG, e.getMessage());
					}
				}
			}
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.getMessage());
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
				}
			}
		}
	}

	public static boolean getPhoto(Bitmap bitmap, String mDstPath) {
		if (bitmap == null) {
			return false;
		}

		File file = new File(mDstPath);
		FileOutputStream b = null;
		try {
			file.createNewFile();
			b = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文�?
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (b != null) {
					b.flush();
					b.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static String getFileMD5(InputStream in) {
		MessageDigest digest = null;
		byte buffer[] = new byte[1024];
		int len;
		try {
			digest = MessageDigest.getInstance("MD5");
			digest.reset();
			while ((len = in.read(buffer, 0, 1024)) != -1) {
				digest.update(buffer, 0, len);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		BigInteger bigInt = new BigInteger(1, digest.digest());
		return bigInt.toString(16);
	}

	public static Bitmap revitionImageSize(Context context, String path) throws IOException {
		if (!FileUtil.isValidAttach(path, false)) {
			return null;
		}
		String fPath = path;
		int fileSize = getFileSize(fPath);
		String firstSize = FormetFileSize(getFileSize(fPath));
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		Bitmap bitmap = null;
		Matrix matrix = new Matrix();
		while (true) {
			if ((options.outWidth >> i <= 1000) && (options.outHeight >> i <= 1000)) {
				in = new BufferedInputStream(new FileInputStream(new File(path)));
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				matrix.setRotate(readPictureDegree(fPath));
				bitmap = BitmapFactory.decodeStream(in, null, options);
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
				break;
			}
			i += 1;
		}
		FileUtil.getPhoto(bitmap, path);
		int fileSize2 = getFileSize(path);
		if (fileSize > fileSize2) {
			String secondSize = FormetFileSize(getFileSize(path));
			ToastUtil.showMessage(context, "原图片：" + firstSize + "   压缩后：" + secondSize);
		}
		return bitmap;
	}

	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}
}
