package com.qdb.agent.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class DensityUtil {

	/**
	 * 根据手机的分辨率�? dp 的单�? 转成�? px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率�? px(像素) 的单�? 转成�? dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	/**
	 * 适配分辨�?
	 * @param context
	 * @return
	 */
	public static boolean getMetric(Activity context) {
		boolean flag = false;
		DisplayMetrics display = new DisplayMetrics();
		 context.getWindowManager().getDefaultDisplay().getMetrics(display);
		if (display.widthPixels == 480 && display.heightPixels == 800) {
			flag = false;
		} else if (display.widthPixels == 540 && display.heightPixels == 960) {
			flag = true;
		}
		return flag;
	}
	
	/**地球半径*/
    private static final double EARTH_RADIUS = 6378137;

    private static double rad(double d){
        return d * Math.PI / 180.0;
    }
	     
     /** *//**
      * 根据两点间经纬度坐标（double值），计算两点间距离，单位为�?
      * @param lng1
      * @param lat1
      * @param lng2
      * @param lat2
      * @return
      */
    public static double getDistance(double lng1, double lat1, double lng2, double lat2){
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) + 
         Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }
    
}
