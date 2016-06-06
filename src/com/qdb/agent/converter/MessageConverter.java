package com.qdb.agent.converter;

import java.util.Map;

public interface MessageConverter {

	public Map<String, Object> convertStringToMap(String str);

	public String convertMapToString(Map<String, Object> map);

}
