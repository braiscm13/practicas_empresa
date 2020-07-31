package com.opentach.common.util;

import java.util.Locale;
import java.util.ResourceBundle;

import com.ontimize.gui.i18n.ExtendedPropertiesBundle;

public final class ResourceManager {

	private static String	resources	= "com.opentach.server.i18n.bundle";

	private ResourceManager() {}

	public static void setResourceBundleBaseName(String res) {
		ResourceManager.resources = res;
	}

	public static final ResourceBundle getBundle(Locale locale) {
		return ResourceBundle.getBundle(ResourceManager.resources, locale);
	}

	public static final String translate(Locale l, String key) {
		if (ResourceManager.resources != null) {
			try {
				ResourceBundle ep = ExtendedPropertiesBundle.getExtendedBundle(ResourceManager.resources, l);
				return ep.getString(key);
			} catch (Exception ex) {
				return key;
			}
		}
		return key;
	}
}
