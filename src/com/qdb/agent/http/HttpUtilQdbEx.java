package com.qdb.agent.http;

import java.util.Map;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.qdb.agent.R;
import com.qdb.agent.comm.Constant;
import com.qdb.agent.converter.JsonMessageConverter;
import com.qdb.agent.converter.MessageConverter;
import com.qdb.agent.utils.DeviceUtil;
import com.qdb.agent.utils.Logger;
import com.qdb.agent.utils.MyLog;
import com.qdb.agent.utils.NetWorkTool;
import com.qdb.agent.utils.SharedPreferencesUtil;
import com.qdb.agent.utils.StringUtil;
import com.qdb.agent.utils.ToastUtil;

public class HttpUtilQdbEx {

	static String TAG = "HttpUtilQdbEx";
	private static HttpUtilQdbEx instance;
	protected static AsyncHttpClient client = new AsyncHttpClient();
	protected static SyncHttpClient syncClient = new SyncHttpClient();
	protected static MessageConverter converter = new JsonMessageConverter();

	public static HttpUtilQdbEx getInstance() {
		if (instance == null) {
			instance = new HttpUtilQdbEx();
		}
		return instance;

	}

	private void OnHttpReqResult(String errcode, String errMsg, String rspBody, Map<String, Object> response, String tag) {
		HttpRspObject httpResult = new HttpRspObject();
		httpResult.setRspBody(rspBody);
		httpResult.setStatus(errcode);
		httpResult.setErrMsg(errMsg);
		httpResult.setRspObj(response);
		EventBus.getDefault().post(httpResult, tag);
	}

	public static void newPost(Context context, String url, JSONObject jsonObject, AsyncHttpResponseHandler res) {
		try {
			Logger.i(TAG, "newPost url:" + url + "  jsonObject:" + jsonObject);
			StringEntity stringEntity = null;
			client.setTimeout(60000);
			client.setConnectTimeout(60000);
			client.setUserAgent(Constant.getUserAgent(context));
			String entity = jsonObject.toString();
			stringEntity = new StringEntity(entity, "utf-8");
			stringEntity.setContentEncoding("utf-8");
			stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			client.addHeader("sn", StringUtil.Md5(DeviceUtil.getDevice() + DeviceUtil.getIMEI(context)));
			client.addHeader("mno", SharedPreferencesUtil.readPhoneNum(context));
			client.post(context, Constant.NEW_URL + url, stringEntity, "application/json", res);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void newPostHttpReq(final Context context, final String url, JSONObject jsonObject, final String subscriberTag) {
		if (!NetWorkTool.isNetworkAvailable(context)) {
			OnHttpReqResult(ErrCode.err_nonet, context.getString(R.string.net_err), null, null, subscriberTag);
			return;
		}
		newPost(context, url, jsonObject, new BaseJsonHttpResponseHandler<Map<String, Object>>() {
			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Map<String, Object> errorResponse) {
				OnHttpReqResult(ErrCode.err_server, context.getString(R.string.connect_failure), rawJsonData, null, subscriberTag);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Map<String, Object> response) {
				if (response != null) {
					Logger.i(TAG, "newPostHttpReq doHttpReq onSuccess url" + url);
					// Logger.i(TAG,
					// "doHttpReq onSuccess url"+url+"     map:"+response.toString());
					String resid = response.get("resid").toString();
					String resmsg = response.get("resmsg").toString();
					if (resid.equals("0")) {
						OnHttpReqResult(resid, resmsg, rawJsonResponse, response, subscriberTag);
					} else if (resid.equals("-404")) {
						ToastUtil.showMessage(context, resmsg);
					} else {
						// 错误结果有2,3的情况需要带回返回值
						OnHttpReqResult(resid, resmsg, rawJsonResponse, response, subscriberTag);
					}
				} else {
					OnHttpReqResult(ErrCode.err_rspnull, context.getString(R.string.data_exception), rawJsonResponse, response, subscriberTag);
				}
			}

			@Override
			protected Map<String, Object> parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
				Logger.i(TAG, "newPostHttpReq url: " + url + "      parseResponse:" + rawJsonData);
				MyLog.d(subscriberTag, rawJsonData);
				return converter.convertStringToMap(rawJsonData);
			}
		});
	}
	
	public void newGet(Context context, String url, RequestParams params, AsyncHttpResponseHandler res) {
		Logger.i(TAG, "newGet url:" + url + "  params:" + params.toString());
		client.setTimeout(10000);
		client.setConnectTimeout(10000);
		client.setUserAgent(Constant.getUserAgent(context));
		params.setUseJsonStreamer(true);
		client.addHeader("sn", StringUtil.Md5(DeviceUtil.getDevice() + DeviceUtil.getIMEI(context)));
		client.addHeader("mno", SharedPreferencesUtil.readPhoneNum(context));
		client.get(context, Constant.NEW_URL + url, params, res);
	}

	public void newGetHttpReq(final Context context, final String url, RequestParams params, final String subscriberTag) {

		if (!NetWorkTool.isNetworkAvailable(context)) {
			OnHttpReqResult(ErrCode.err_nonet, context.getString(R.string.net_err), null, null, subscriberTag);
			return;
		}
		newGet(context, url, params, new BaseJsonHttpResponseHandler<Map<String, Object>>() {
			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Map<String, Object> errorResponse) {
				OnHttpReqResult(ErrCode.err_server, context.getString(R.string.connect_failure), rawJsonData, null, subscriberTag);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Map<String, Object> response) {
				if (response != null) {
					Logger.i(TAG, "doHttpReq onSuccess url" + url);
					// Logger.i(TAG,
					// "doHttpReq onSuccess url"+url+"     map:"+response.toString());
					String resid = response.get("resid").toString();
					String resmsg = response.get("resmsg").toString();
					if (resid.equals("0")) {
						if (url.equals(UrlConstantQdb.MODULES)) {// 保存主界面模块
							SharedPreferencesUtil.saveMainModules(context, rawJsonResponse);
						}
						OnHttpReqResult(resid, resmsg, rawJsonResponse, response, subscriberTag);
					} else if (resid.equals("-404")) {
						ToastUtil.showMessage(context, resmsg);
					} else {
						// 错误结果有2,3的情况需要带回返回值
						OnHttpReqResult(resid, resmsg, rawJsonResponse, response, subscriberTag);
					}
				} else {
					OnHttpReqResult(ErrCode.err_rspnull, context.getString(R.string.data_exception), rawJsonResponse, response, subscriberTag);
				}
			}

			@Override
			protected Map<String, Object> parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
				Logger.i(TAG, "newGetHttpReq url: " + url + "      parseResponse:" + rawJsonData);
				MyLog.d(subscriberTag, rawJsonData);
				return converter.convertStringToMap(rawJsonData);
			}
		});
	}

}
