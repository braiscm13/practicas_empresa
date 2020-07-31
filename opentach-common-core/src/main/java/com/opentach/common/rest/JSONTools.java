package com.opentach.common.rest;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JSONTools {

	public static final String	FORMAT_DATE		= "yyyy-MM-dd";
	public static final String	FORMAT_DATETIME	= "yyyy-MM-dd'T'HH:mm:ss'Z'";

	private JSONTools() {
		super();
	}

	public static Map<String, String> jsonToMap(final String t) throws IOException {
		final ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(t, new TypeReference<HashMap<String, String>>() {});
	}

	public static String mapToJson(final Map<?, ?> map) throws JsonProcessingException {
		if (!map.isEmpty()) {
			return new ObjectMapper().writeValueAsString(map);
		}
		return null;
	}

	public static Date str2date(String date) throws ParseException {
		if (date == null) {
			return null;
		}
		return new SimpleDateFormat(JSONTools.FORMAT_DATE).parse(date);
	}

	public static Date str2dateTime(String date) throws ParseException {
		if (date==null) {
			return null;
		}
		return new SimpleDateFormat(JSONTools.FORMAT_DATETIME).parse(date);
	}

	public static String date2Str(Date date) {
		if (date == null) {
			return null;
		}
		return new SimpleDateFormat(JSONTools.FORMAT_DATE).format(date);
	}

	public static String dateTime2Str(Date date) {
		if (date == null) {
			return null;
		}
		return new SimpleDateFormat(JSONTools.FORMAT_DATETIME).format(date);
	}
}
