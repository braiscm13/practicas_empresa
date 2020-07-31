/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. */
package com.ontimize.report.engine.dynamicjasper5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>
 * Ontimize Reports system constants.
 * <p>
 * Jasper Reports System.
 *
 * @author Imatia Innovation S.L.
 * @since 18/09/2008
 */
public class ReportProperty {

	private static final Logger logger = LoggerFactory.getLogger(ReportProperty.class);

	// System Debug.

	public static boolean DEBUG = ReportProperty.checkDebug();
	public static boolean DEBUG_TIMES = ReportProperty.checkDebugTimes();

	protected static final String NAME_PACKAGE = "com.ontimize.reports";
	protected static final String NAME_DEBUG = "DEBUG";
	protected static final String NAME_DEBUG_TIMES = "DEBUG_TIMES";

	public static boolean checkDebug() {
		return ReportProperty.checkDebug(null);
	}

	public static boolean checkDebug(String key) {
		if (key == null) {
			key = ReportProperty.NAME_PACKAGE + "." + ReportProperty.NAME_DEBUG;
		}
		String property = System.getProperty(key);
		if (property == null) {
			return false;
		}
		return property.equalsIgnoreCase("yes") || property.equalsIgnoreCase("true");
	}

	public static boolean checkDebugTimes() {
		return ReportProperty.checkDebugTimes(null);
	}

	public static boolean checkDebugTimes(String key) {
		if (key == null) {
			key = ReportProperty.NAME_PACKAGE + "." + ReportProperty.NAME_DEBUG_TIMES;
		}
		String property = System.getProperty(key);
		if (property == null) {
			return false;
		}
		return property.equalsIgnoreCase("yes") || property.equalsIgnoreCase("true");
	}

	public static void log(Object object) {
		if (ReportProperty.DEBUG) {
			ReportProperty.logger.debug("{}", object);
		}
	}

	public static void log(Throwable t) {
		ReportProperty.logger.error(t.getMessage(), t);
	}

	public static void log(String message) {
		if (ReportProperty.DEBUG) {
			ReportProperty.logger.debug("{}", message);
		}
	}

	public static void log(String message, long startTime) {
		if (ReportProperty.DEBUG_TIMES) {
			long end = System.currentTimeMillis();
			StringBuilder s = new StringBuilder();
			s.append(end - startTime);
			s.append(message);
			ReportProperty.logger.trace("{}", s);
		}
	}

	// Jasper Report Files.

	public static final String JAR_EXTENSION = "jar";
	public static final String ZIP_EXTENSION = "zip";
	public static final String FILTER_DESCRIPTION = "Packed archive (*.zip | *.jar)";
}
