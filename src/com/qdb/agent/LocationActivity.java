package com.qdb.agent;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.qdb.agent.receiver.CdmaCell;
import com.qdb.agent.receiver.GsmCell;
import com.qdb.agent.utils.FileUtil;
import com.qdb.agent.utils.SharedPreferencesUtil;
import com.qdb.agent.utils.TimeUtils;
import com.qdb.agent.utils.ToastUtil;

/**
 * 
 * @author hj 1 判断是否连接wifi，还是运营商基站 2.如果连接的是wifi, 取wifi的基站
 *         3.如果没有连接wifi，如下面代码：取运营商基站
 */
public class LocationActivity extends Activity {
	private TelephonyManager telephonyManager;
	private PhoneStateListener phoneStateListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		InitPhoneStateListener();
		telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CELL_LOCATION);
		if (telephonyManager.getCellLocation() != null) {
			// 获取当前基站信息
			phoneStateListener.onCellLocationChanged(telephonyManager.getCellLocation());
		}
	}

	private int lastSignal;
	private int lastCid;
	private List<GsmCell> gsmCells = new ArrayList<GsmCell>();
	private List<CdmaCell> cdmaCells = new ArrayList<CdmaCell>();

	/** 初始化PhoneStateListener */
	private void InitPhoneStateListener() {
		phoneStateListener = new PhoneStateListener() {
			@Override
			public void onCellLocationChanged(CellLocation location) {
				if (location instanceof GsmCellLocation) {// gsm网络
					GsmCell gsmCell = new GsmCell();
					gsmCell.lac = ((GsmCellLocation) location).getLac();
					gsmCell.cid = ((GsmCellLocation) location).getCid();
					lastCid = ((GsmCellLocation) location).getCid();
					/** 获取mcc，mnc */
					String mccMnc = telephonyManager.getNetworkOperator();
					if (mccMnc != null && mccMnc.length() >= 5) {
						gsmCell.mcc = mccMnc.substring(0, 3);
						gsmCell.mnc = mccMnc.substring(3, 5);
					}
					gsmCell.signal = lastSignal;
					gsmCell.time = System.currentTimeMillis();
					if (gsmCell.lac != -1 && gsmCell.cid != -1) {
						gsmCells.add(0, gsmCell);
					}
					// 获取相邻基站信息
					List<NeighboringCellInfo> neighboringList = telephonyManager.getNeighboringCellInfo();
					for (NeighboringCellInfo ni : neighboringList) {
						GsmCell gb = new GsmCell();
						gb.mnc = mccMnc.substring(3, 5);
						gb.lac = ni.getLac();
						gb.cid = ni.getCid();
						gb.signal = -133 + 2 * ni.getRssi();
						gb.time = System.currentTimeMillis();
						gsmCells.add(gb);
					}
				} else {// 其他CDMA等网络
					try {
						CdmaCellLocation cdma = (CdmaCellLocation) location;
						CdmaCell cdmaCell = new CdmaCell();
						cdmaCell.stationId = cdma.getBaseStationId() >= 0 ? cdma.getBaseStationId() : cdmaCell.stationId;
						lastCid = cdma.getBaseStationId() >= 0 ? cdma.getBaseStationId() : cdmaCell.stationId;
						cdmaCell.networkId = cdma.getNetworkId() >= 0 ? cdma.getNetworkId() : cdmaCell.networkId;
						cdmaCell.systemId = cdma.getSystemId() >= 0 ? cdma.getSystemId() : cdmaCell.systemId;
						/** 获取mcc，mnc */
						String mccMnc = telephonyManager.getNetworkOperator();
						if (mccMnc != null && mccMnc.length() >= 5) {
							cdmaCell.mcc = mccMnc.substring(0, 3);
							cdmaCell.mnc = mccMnc.substring(3, 5);
						}
						cdmaCell.signal = lastSignal;
						cdmaCell.time = System.currentTimeMillis();
						int lat = cdma.getBaseStationLatitude();
						int lon = cdma.getBaseStationLongitude();
						if (lat < Integer.MAX_VALUE && lon < Integer.MAX_VALUE) {
							cdmaCell.lat = lat;
							cdmaCell.lon = lon;
						}
						if (cdmaCell.stationId != -1 && cdmaCell.networkId != -1 && cdmaCell.systemId != -1) {
							cdmaCells.add(0, cdmaCell);
						}
						List<NeighboringCellInfo> neighboringList = telephonyManager.getNeighboringCellInfo();
						for (NeighboringCellInfo ni : neighboringList) {
							CdmaCell cdmaBean = new CdmaCell();
							cdmaBean.systemId = cdmaCell.systemId;
							cdmaBean.lac = ni.getLac();
							cdmaBean.cellid = ni.getCid();
							cdmaBean.signal = -113 + 2 * ni.getRssi();
							cdmaCells.add(cdmaBean);
						}
					} catch (Exception classnotfoundexception) {
					}
				}// end CDMA网络
				int cid = SharedPreferencesUtil.getCid(LocationActivity.this);
				int signal = SharedPreferencesUtil.getSignal(LocationActivity.this);
				if (lastCid != cid && lastSignal != 0) {
					writeCidLog("listen", lastCid, lastSignal);
				} else if (Math.abs(signal - lastSignal) > 30) {
					writeCidLog("listen", lastCid, lastSignal);
				}
				SharedPreferencesUtil.saveCid(LocationActivity.this, lastCid);
				SharedPreferencesUtil.saveSignal(LocationActivity.this, lastSignal);
				ToastUtil.showMessage(LocationActivity.this, "lbs监听信号中.....lastCid=" + lastCid);
				super.onCellLocationChanged(location);
			}// end onCellLocationChanged

			@Override
			public void onServiceStateChanged(ServiceState serviceState) {
				super.onServiceStateChanged(serviceState);
			}

			@Override
			public void onSignalStrengthsChanged(SignalStrength signalStrength) {
				int asu = signalStrength.getGsmSignalStrength();
				lastSignal = -113 + 2 * asu; // 信号强度
				ToastUtil.showMessage(LocationActivity.this, "lbs信号强度改变-----------lastCid=" + lastCid);
				int cid = SharedPreferencesUtil.getCid(LocationActivity.this);
				int signal = SharedPreferencesUtil.getSignal(LocationActivity.this);
				if (lastCid != cid && lastSignal != 0) {
					writeCidLog("signalChanged", lastCid, lastSignal);
				} else if (Math.abs(signal - lastSignal) > 30) {
					writeCidLog("signalChanged", lastCid, lastSignal);
				}
				SharedPreferencesUtil.saveCid(LocationActivity.this, lastCid);
				SharedPreferencesUtil.saveSignal(LocationActivity.this, lastSignal);
				super.onSignalStrengthsChanged(signalStrength);
			}
		};
	}// end InitPhoneStateListener

	File file = null;

	private void writeCidLog(String type, int cid, int signalStrength) {
		String log = FileUtil.getLogCachePath(this) + File.separator + "cid.txt";
		file = new File(log);
		JSONObject obj = new JSONObject();
		try {
			obj.put("time", TimeUtils.getCurrentTime());
			obj.put("cid", cid);
			obj.put("type", type);
			obj.put("signal", signalStrength);
			writeLog(file, obj.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private int MAX_LOG_SIZE = 50 * 1024;

	RandomAccessFile fWriter = null;

	private void writeLog(File _file, String content) {
		try {
			try {
				if (!_file.getParentFile().exists()) {
					_file.getParentFile().mkdirs();
				}
				boolean isFileExist = _file.exists();
				if (fWriter != null) {
					fWriter.close();
				}
				fWriter = new RandomAccessFile(_file, "rws");
				if (isFileExist) {
					fWriter.seek(_file.length());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			StringBuffer sb = new StringBuffer();
			sb.append(content);
			sb.append(",");
			final byte[] data = sb.toString().getBytes();
			fWriter.write(data);

		} catch (Exception e) {
			Log.e("MyLog", "log output exception,maybe the log file is not exists");
		} finally {
			if (_file != null && _file.length() >= MAX_LOG_SIZE) {
				if (_file.exists())
					_file.delete();
				return;
			}
		}
	}
}
