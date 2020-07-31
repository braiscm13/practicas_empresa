package com.opentach.common.util;

import java.util.Vector;

public final class StringUtils {
	private StringUtils() {
		super();
	}

	public static String vectorToCommaSeparated(Vector<Object> vIdDrivers) {
		StringBuilder idsConductor = new StringBuilder();
		for (int i = 0; i < vIdDrivers.size(); i++) {
			idsConductor = idsConductor.append(vIdDrivers.elementAt(i));
			if (i != (vIdDrivers.size() - 1)) {
				idsConductor = idsConductor.append(",");
			}
		}
		return idsConductor.toString();
	}

	public static String trimToSize(String srcId, int length) {
		if (srcId == null) {
			return null;
		}
		return srcId.substring(0, Math.min(srcId.length(), length));
	}

}
