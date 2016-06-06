package com.qdb.agent.converter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qdb.agent.utils.Logger;
import com.qdb.agent.utils.MyLog;

public class JsonMessageConverter implements MessageConverter {
	private String TAG = "JsonMessageConverter";

	@Override
	public Map<String, Object> convertStringToMap(String str) {
		Logger.d(TAG, str);
		try {
			return convertJSONObjectToMap(new JSONObject(str));
		} catch (JSONException e) {
			MyLog.i(TAG, str);
			MyLog.e(TAG, e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String convertMapToString(Map<String, Object> map) {
		return convertMapToJSONObject(map).toString();
	}

	@SuppressWarnings("rawtypes")
	private Map<String, Object> convertJSONObjectToMap(JSONObject obj) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();

		for (Iterator keys = obj.keys(); keys.hasNext();) {
			String key = (String) keys.next();
			try {
				Object value = obj.get(key);
				if (value instanceof JSONArray) {
					value = convertJSONArrayToList((JSONArray) value);
				}
				map.put(key, value);
			} catch (JSONException e) {
				// ignore
			}
		}

		return map;
	}

	@SuppressWarnings("unchecked")
	private JSONObject convertMapToJSONObject(Map<String, Object> map) {
		JSONObject obj = new JSONObject();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			Object value = entry.getValue();
			if (value instanceof List) {
				value = convertListToJSONArray((List<Map<String, Object>>) value);
			}
			try {
				obj.put(entry.getKey(), value);
			} catch (JSONException e) {
				// ignore
			}
		}
		return obj;
	}

	private Object convertJSONArrayToList(JSONArray array) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0, n = array.length(); i < n; i++) {
			try {
				list.add(convertJSONObjectToMap(array.getJSONObject(i)));
			} catch (JSONException e) {
				return array.toString();
			}
		}
		return list;

	}

	private JSONArray convertListToJSONArray(List<Map<String, Object>> list) {
		JSONArray array = new JSONArray();
		for (Map<String, Object> i : list) {
			array.put(convertMapToJSONObject(i));
		}
		return array;
	}

}
