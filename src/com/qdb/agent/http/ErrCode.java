package com.qdb.agent.http;

import android.content.Context;


public class ErrCode {

	public final static String err_fail = "-1"; //请求接口成功，但返回数据失败
	public final static String err_relogin = "-401";
	public final static String err_succ = "0"; //0 成功

	//1~100 	请求错误,属于客户端问题,必须修正的
	public final static String err_encode = "-100"; //编码错误
	public final static String err_server="-101";//网络错误
	public final static String err_json = "-102";
	public final static String err_nonet = "-103";
	public final static String err_rspnull = "-104";



	//201~200 服务器内部错误,属于服务器必须修正的,但未解决的.	
	//301~999 业务逻辑错误,特定接口的,会在接口中指定错误码
	//  <string name="err_server">获取数据失败,请确认服务器启动是否正常</string>

	public static String getErrMsg(Context context,String code)
	{
		String str ="unknownerr";

		if(code.equals(err_encode))
		{
			str= "UnsupportedEncodingException";
		}
		else if(code.equals(err_server))
		{
			str = "网络连接失败";
		}
		else if(code.equals(err_json))
		{
			str = "解析数据失败";
		}
		else if(code.equals(err_nonet))
		{
			str = "网络请求失败";
		}
		else if(code.equals(err_rspnull))
		{
			str = "获取数据失败,请确认服务器启动是否正常";
		}
		else
		{
			str = "获取数据失败,请确认服务器启动是否正常";
		}
		return str;
	}
}
