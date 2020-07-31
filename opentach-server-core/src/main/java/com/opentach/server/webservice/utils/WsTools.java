package com.opentach.server.webservice.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class WsTools {

	public static final String FORMAT_DATETIME = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	private WsTools() {
		super();
	}

	public static String date2ws(Date date) {
		if (date == null) {
			return null;
		}
		return new SimpleDateFormat(WsTools.FORMAT_DATETIME).format(date);
	}

	public static Date ws2date(String str) throws OpentachWSException {
		try {
			return new SimpleDateFormat(WsTools.FORMAT_DATETIME).parse(str);
		} catch (ParseException err) {
			throw new OpentachWSException(-1, "Invalid date format.");
		}
	}

}
