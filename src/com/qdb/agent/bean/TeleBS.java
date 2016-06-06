package com.qdb.agent.bean;

import java.util.List;

import android.net.wifi.WifiInfo;
import android.telephony.NeighboringCellInfo;

public class TeleBS {
	private int mcc;
	private int mnc;
	private int lac;
	private int cellid;
	private String mncType;
	private List<NeighboringCellInfo> infoLists;
	private WifiInfo info;

	public int getMcc() {
		return mcc;
	}
	
	

	public int getMnc() {
		return mnc;
	}



	public void setMnc(int mnc) {
		this.mnc = mnc;
	}



	public void setMcc(int mcc) {
		this.mcc = mcc;
	}

	public int getLac() {
		return lac;
	}

	public void setLac(int lac) {
		this.lac = lac;
	}

	public int getCellid() {
		return cellid;
	}

	public void setCellid(int cellid) {
		this.cellid = cellid;
	}

	public String getMncType() {
		return mncType;
	}

	public void setMncType(String mncType) {
		this.mncType = mncType;
	}

	public List<NeighboringCellInfo> getInfoLists() {
		return infoLists;
	}

	public void setInfoLists(List<NeighboringCellInfo> infoLists) {
		this.infoLists = infoLists;
	}

	public WifiInfo getInfo() {
		return info;
	}

	public void setInfo(WifiInfo info) {
		this.info = info;
	}

}
