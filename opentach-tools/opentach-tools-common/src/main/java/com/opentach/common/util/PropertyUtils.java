package com.opentach.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PropertyUtils {

	private static final Logger	logger	= LoggerFactory.getLogger(PropertyUtils.class);

	private PropertyUtils() {
		super();
	}

	public static Properties loadProperties(String string) {
		Properties properties = new Properties();
		try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(string)) {
			if (is != null) {
				properties.load(is);
			}

		} catch (IOException error) {
			PropertyUtils.logger.error(null, error);
		}
		return properties;
	}
}
