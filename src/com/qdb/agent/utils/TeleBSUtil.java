package com.qdb.agent.utils;

import java.util.List;

import org.json.JSONException;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

import com.qdb.agent.bean.TeleBS;
import com.qdb.agent.converter.JsonMessageConverter;
import com.qdb.agent.converter.MessageConverter;

/*
 *获取手机基站信息
 * */
public class TeleBSUtil {

	public static String TAG = "TeleBSUtil";

	// 通过TelephonyManager 获取lac:mcc:mnc:cell-id（基站信息）的解释：
	// MCC，Mobile Country Code，移动国家代码（中国的为460）；
	// MNC，Mobile Network Code，移动网络号码（中国移动为0，中国联通为1，中国电信为2）；
	// LAC，Location Area Code，位置区域码；
	// CID，Cell Identity，基站编号；
	// BSSS，Base station signal strength，基站信号强度

	/**
	 * 获取wifi列表
	 * 
	 * @param context
	 * @return
	 */
	public static List<ScanResult> getScanResult(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		List<ScanResult> list = wifiManager.getScanResults();
		return list;
	}

	/**
	 * 获取手机基站信息
	 * 
	 * @throws JSONException
	 */
	public static TeleBS getGSMCellLocationInfo(Context context) {
		TeleBS teleBS = new TeleBS();
		TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String operator = manager.getNetworkOperator();
//		Log.e(TAG, "getGSMCellLocationInfo operator:" + operator);
		/** 通过operator获取 MCC 和MNC */
		try {

			if(!StringUtil.isBlank(operator)){
				int mcc = Integer.parseInt(operator.substring(0, 3));
				int mnc = Integer.parseInt(operator.substring(3));
//				Log.e(TAG, "getGSMCellLocationInfo mcc:" + mcc + "  mnc中国移动为0，中国联通为1，中国电信为2 :" + mnc);
				/** 通过GsmCellLocation获取中国移动和联通 LAC 和cellID */
				/** 通过CdmaCellLocation获取中国电信 LAC 和cellID */
				String mncType = "";
				int lac = 0;
				int cellid = 0;
				if (mnc == 0 || mnc == 1) {// 移动和联通
					mncType = "gsm";
					GsmCellLocation location = (GsmCellLocation) manager.getCellLocation();
					if (location == null) {
						ToastUtil.showMessage(context, "获取基站信息失败");
						return null;
					}
					lac = location.getLac();
					cellid = location.getCid();
				} else {// 电信
					mncType = "cdma";
					CdmaCellLocation location = (CdmaCellLocation) manager.getCellLocation();
					if (location == null) {
						ToastUtil.showMessage(context, "获取基站信息失败");
						return null;
					}
					lac = location.getNetworkId();
					cellid = location.getBaseStationId();
					cellid /= 16;
				}
				teleBS.setCellid(cellid);
				teleBS.setLac(lac);
				teleBS.setMcc(mcc);
				teleBS.setMncType(mncType);
			}else{
				teleBS.setCellid(0);
				teleBS.setLac(0);
				teleBS.setMcc(0);
				teleBS.setMncType("");
			}
			/** 通过getNeighboringCellInfo获取BSSS */
			List<NeighboringCellInfo> infoLists = manager.getNeighboringCellInfo();
			// 获取路由器wifi的mac地址
			String wserviceName = Context.WIFI_SERVICE;
			WifiManager wm = (WifiManager) context.getSystemService(wserviceName);
			WifiInfo info = wm.getConnectionInfo();
			teleBS.setInfo(info);
			teleBS.setInfoLists(infoLists);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return teleBS;
	}

	// 以下内容是把得到的信息组合成json体，然后发送给我的服务器，获取经纬度信息
	protected static MessageConverter converter = new JsonMessageConverter();

}
