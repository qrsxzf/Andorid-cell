package com.qdb.agent.http;

public class HttpRspObject {
	private String mStatus = "-1";
	private String mErrMsg="";
	private String mRspBody = "";//返回包体
	private String mStrMsgID = ""; //用于一个页面多个消息处理区分
	private String mRequestObj = ""; //用户请求的值作为输入
	private Object mRspObj = null;

	
	public String getmRequestObj() {
		return mRequestObj;
	}
	public void setmRequestObj(String mRequestObj) {
		this.mRequestObj = mRequestObj;
	}
	public String getStrMsgID() {
		return mStrMsgID;
	}
	public void setStrMsgID(String mStrMsgID) {
		this.mStrMsgID = mStrMsgID;
	}
	public String getRspBody() {
		return mRspBody;
	}
	public void setRspBody(String mRspBody) {
		this.mRspBody = mRspBody;
	}
	public String getStatus() {
		return mStatus;
	}
	public void setStatus(String mStatus) {
		this.mStatus = mStatus;
	}
	public String getErrMsg() {
		return mErrMsg;
	}
	public void setErrMsg(String mErrMsg) {
		this.mErrMsg = mErrMsg;
	}
	public Object getRspObj() {
		return mRspObj;
	}
	public void setRspObj(Object mRspObj) {
		this.mRspObj = mRspObj;
	}


}
