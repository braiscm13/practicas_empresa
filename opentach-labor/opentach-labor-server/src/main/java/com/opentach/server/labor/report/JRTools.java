package com.opentach.server.labor.report;

public class JRTools {

	public JRTools() {
	}

	public static String min2String(Number minutes) {
		if (minutes == null) {
			return "-";
		}
		return String.format("%02d", minutes.intValue() / 60) + ":" + String.format("%02d", minutes.intValue() % 60) + " h.";
	}

}
