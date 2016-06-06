package com.qdb.agent.utils;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

public class StringUtil {

	/**
	 * 判断字符串是否为空
	 * 
	 * @param str
	 * @return true 空，false非空
	 */
	public static boolean isBlank(String str) {
		boolean b = false;
		if (str == null || "".equals(str) || "null".equals(str) || "NULL".equals(str) || "null.null".equals(str)) {
			b = true;
		} else {
			b = false;
		}
		return b;
	}

	/**
	 * 验证是否是邮箱
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		String str = "(?=^[\\w.@]{6,50}$)\\w+@\\w+(?:\\.[\\w]{2,3}){1,2}";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}

	/**
	 * 获取关键帧图片
	 * 
	 * @param video_url
	 * @param count
	 * @return
	 */
	public static String getCIFImageUrl(String video_url, int count) {
		if (video_url == null)
			return null;
		// getbasepath
		int index = video_url.lastIndexOf("/");
		String basepath = video_url.substring(0, index + 1);
		// getbasefilename
		String tmp = video_url.substring(index + 1);
		tmp = tmp.substring(0, tmp.lastIndexOf("."));

		if (count > 3)
			count = 0;
		return basepath + tmp + "_cif_" + count + ".jpg";
	}

	/**
	 * 替换字符
	 * 
	 * @param text
	 * @param value
	 *            要替换成的字符
	 * @return
	 */
	public static String replace(String text, String value) {
		String[] lines = text.split("&&P&&");
		Pattern pattern = Pattern.compile("\\*\\d+\\*");

		Map<String, String> maps = new HashMap<String, String>();
		for (String line : lines) {
			Matcher matcher = pattern.matcher(line);
			while (matcher.find()) {
				if (!maps.containsKey(matcher.group())) {
					String key = matcher.group();
					int len = Integer.parseInt(key.replace("*", ""));
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < len; i++) {
						sb.append(value);
					}
					maps.put(key, sb.toString());
				}
			}
		}
		for (String key : maps.keySet()) {
			text = text.replace(key, maps.get(key));
		}
		return text;
	}

	/**
	 * 处理空字符串
	 * 
	 * @param str
	 * @return String
	 */
	public static String doEmpty(String str) {
		return doEmpty(str, "");
	}

	/**
	 * 处理空字符串
	 * 
	 * @param str
	 * @param defaultValue
	 * @return String
	 */
	public static String doEmpty(String str, String defaultValue) {
		if (str == null || str.equalsIgnoreCase("null") || str.trim().equals("") || str.trim().equals("－请选择－")) {
			str = defaultValue;
		} else if (str.startsWith("null")) {
			str = str.substring(4, str.length());
		}
		return str.trim();
	}

	public static boolean isMobileNum(String mobiles) {
		Pattern p = Pattern.compile("^((13[0-9])|(14[0-9])|(17[0-9])|15([0-9])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	public static boolean isIdCard(String idCard) {
		// Pattern p = Pattern.compile("([0-9]{17}([0-9]|X|x))|([0-9]{15})");
		// Matcher m = p.matcher(idCard);
		return CheckIdCard.getInstance(idCard).validate();
	}

	public static boolean num(String str) {
		try {
			new BigDecimal(str);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean regex_Money(String str) {
		Pattern p = Pattern.compile("^\\d+\\.{1}\\d*$");
		Matcher m = p.matcher(str);

		Pattern p1 = Pattern.compile("^\\d+$");
		Matcher m2 = p1.matcher(str);

		return m.matches() || m2.matches();
	}

	public static boolean regexTopUpMoney(String str) {
		if (str.contains(".")) {
			String[] split = str.split("\\.");
			if (split.length > 1 && split[1].length() > 1)
				return true;
			return false;
		}
		return false;
	}

	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	public static boolean isDate(String str_input, String rDateFormat) {
		if (!isBlank(str_input)) {
			SimpleDateFormat formatter = new SimpleDateFormat(rDateFormat);
			formatter.setLenient(false);
			try {
				formatter.format(formatter.parse(str_input));
			} catch (Exception e) {
				return false;
			}
			return true;
		}
		return false;
	}

	public static Spanned formatTextColor(String content, String string) {
		if (string.equals("未处理")) {
			return Html.fromHtml(content + "<font color='red'>未处理</font>");
		} else if (string.equals("已处理")) {
			return Html.fromHtml(content + "<font color='green'>已处理</font>");
		} else if (string.equals("已关闭")) {
			return Html.fromHtml(content + "<font color='green'>已关闭</font>");
		}
		return null;
	}

	public static Spanned formatCommentTextColor(String name1, String name2) {
		return Html.fromHtml(name1 + "<font color='black'> 回复 </font>" + name2 + ":");
	}

	public static Spanned formatTextRed(String content) {
		return Html.fromHtml("<font color='red' size='50'>*</font>" + content);
	}

	public static Spanned formatTextRedColor(Context con, String str1, TextView view) {
		SpannableString span = new SpannableString(String.valueOf(view.getText()));
		span.setSpan(new ForegroundColorSpan(Color.RED), 0, str1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		span.setSpan(new AbsoluteSizeSpan(50), 0, str1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return span;
	}

	public static Spanned formatTextRedColor2(Context con, String str1, TextView view) {
		SpannableString span = new SpannableString(String.valueOf(view.getText()));
		span.setSpan(new ForegroundColorSpan(Color.RED), 0, str1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		span.setSpan(new AbsoluteSizeSpan(DensityUtil.dip2px(con, 16)), 0, str1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return span;
	}

	public static Spanned formatRedTextColor(String string) {
		return Html.fromHtml(string + "<font color='red'>*</font>");
	}

	public static String Md5(String XmlString) {
		StringBuffer result = new StringBuffer();
		byte[] data = XmlString.getBytes();

		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			byte[] re = md.digest(data);
			for (int i = 0; i < re.length; i++) {
				result.append(byteHEX(re[i]));
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return result.toString();
	}

	/*
	 * byteHEX()，用来把一个byte类型的数转换成十六进制的ASCII表示，
	 * 　因为java中的byte的toString无法实现这一点，我们又没有C语言中的 sprintf(outbuf,"%02X",ib)
	 */
	private static String byteHEX(byte ib) {
		char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] ob = new char[2];
		ob[0] = Digit[(ib >>> 4) & 0X0F];
		ob[1] = Digit[ib & 0X0F];
		String s = new String(ob);
		return s;
	}

	public static Date parse(String strDate, String user_format) {
		SimpleDateFormat df = new SimpleDateFormat(user_format);
		try {
			return df.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static long getLongByStr(String strDate) {
		Date date = parse(strDate, "yyyy-MM-dd");
		if (date == null) {
			return System.currentTimeMillis();
		}
		return date.getTime();
	}

	public static String formatDuring(long mss) {
		// long days = mss / (1000 * 60 * 60 * 24);
		// long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
		long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
		long seconds = (mss % (1000 * 60)) / 1000;
		// return days + "天" + hours + "时" + minutes + "分" + seconds + "秒";
		return minutes + "分" + seconds + "秒";
	}

	/**
	 * 支付宝支付返回结果
	 * 
	 * @param resultInfo
	 * @return
	 */
	public static String aLiPayOutTradeNo(String resultInfo) {
		String[] resultParams = resultInfo.split("&");
		String outTradeNo = "";
		for (String resultParam : resultParams) {
			if (resultParam.startsWith("out_trade_no")) {
				outTradeNo = gatValue(resultParam, "out_trade_no");
			}
		}
		return outTradeNo;
	}

	public static String gatValue(String content, String key) {
		String prefix = key + "=\"";
		return content.substring(content.indexOf(prefix) + prefix.length(), content.lastIndexOf("\""));
	}

	/**
	 * 公里和米进行转换
	 * 
	 * @param content
	 * @return
	 */
	public static String distanceJudgment(String content) {
		if (content == null || content.length() == 0)
			return "";
		Double distance = Double.parseDouble(content);
		if (distance > 1000) {
			double show = distance / 1000;

			return "与我距离约:" + formatDistance(show) + "公里";
		} else {
			return "与我距离约:" + formatDistance(distance) + "米";
		}

	}

	/**
	 * 保留一位小数
	 * 
	 * @param distance
	 * @return
	 */
	public static Float formatDistance(double distance) {
		BigDecimal b = new BigDecimal(distance);
		float f = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
		return f;
	}

	public static String getMyrole(String myrole) {
		String myroleStr = "";
		if (myrole == null)
			return myroleStr;
		if (myrole.equals("bill")) {
			myroleStr = "货主";
		} else if (myrole.equals("car")) {
			myroleStr = "车主";
		} else if (myrole.equals("driver")) {
			myroleStr = "司机";
		} else if (myrole.equals("forwarders")) {
			myroleStr = "货代";
		}
		return myroleStr;
	}

	/**
	 * 获取银行简码
	 * 
	 * @param postion
	 * @return
	 */
	public static String getBankCode(int postion) {
		String bankCode = "BOC";
		if (postion == 0) {
			bankCode = "BOC";
		} else if (postion == 1) {
			bankCode = "ABC";
		} else if (postion == 2) {
			bankCode = "ICBC";
		} else if (postion == 3) {
			bankCode = "CCB";
		} else if (postion == 4) {
			bankCode = "COMM";
		} else if (postion == 5) {
			bankCode = "PSBC";
		} else if (postion == 6) {
			bankCode = "CMB";
		} else if (postion == 7) {
			bankCode = "CMBC";
		} else if (postion == 8) {
			bankCode = "CITIC";
		} else if (postion == 9) {
			bankCode = "CEB";
		} else if (postion == 10) {
			bankCode = "HXB";
		} else if (postion == 11) {
			bankCode = "SPDB";
		} else if (postion == 12) {
			bankCode = "SDB";
		} else if (postion == 13) {
			bankCode = "CIB";
		} else if (postion == 14) {
			bankCode = "GDB";
		} else if (postion == 15) {// 渤海银行
			bankCode = "CBHB";
		} else if (postion == 16) {// 恒丰银行
			bankCode = "EGB";
		}
		return bankCode;
	}

	public static String getMeetingState(int state) {
		// status =0 未开始，1已开始，2已完成，-1作废
		String sta = "未开始";
		if (state == 0) {
			sta = "未开始";
		} else if (state == 1) {
			sta = "已开始";
		} else if (state == 2) {
			sta = "已完成";
		} else if (state == -1) {
			sta = "已作废";
		}
		return sta;
	}

	public static String convertStandardJSONString(String data_json) {
		data_json = data_json.replaceAll("\\\\r\\\\n", "");
		data_json = data_json.replace("\"{", "{");
		data_json = data_json.replace("}\",", "},");
		data_json = data_json.replace("}\"", "}");
		return data_json;
	}

}
