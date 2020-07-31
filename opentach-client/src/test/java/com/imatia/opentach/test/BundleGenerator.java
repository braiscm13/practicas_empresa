package com.imatia.opentach.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Vector;

public class BundleGenerator {

	private final static String					OUTPUT_PATH				= "C:\\Users\\lorena.sobral\\Desktop\\opentach\\OPENTACH_BUNDLES\\";
	private final static String					BASE_PATH				= "D:\\EclipseKepler\\workspace2\\Opentach\\";
	
	private final static String					BASE_PATH_CLIENT		= BundleGenerator.BASE_PATH + "opentach-client\\src\\main\\resources\\com\\opentach\\client\\i18n";
	private final static String					BASE_PATH_LABOR			= BundleGenerator.BASE_PATH + "opentach-labor\\opentach-labor-client\\src\\main\\resources\\i18n-labor";
	private final static String					BASE_PATH_SUPPORT		= BundleGenerator.BASE_PATH + "opentach-support\\opentach-support-desktopclient\\src\\main\\resources\\i18n-tasks";
	private final static String					BASE_PATH_REMOTEVEHICLE	= BundleGenerator.BASE_PATH + "opentach-remotevehicle\\opentach-remotevehicle-client\\src\\main\\resources\\i18n-remotevehicle";
	private final static String					BASE_PATH_DMS			= BundleGenerator.BASE_PATH + "opentach-dms\\opentach-dms-client\\src\\main\\resources\\dms-i18n";
	private final static String					BASE_PATH_CLIENT_ADMIN			= BundleGenerator.BASE_PATH + "opentach-client-admin\\src\\main\\resources\\i18n-admin";
	private final static String					BASE_PATH_CLIENT_DOWNLOAD		= BundleGenerator.BASE_PATH + "opentach-client-downcenter\\src\\main\\resources\\downcenter-i18n";
	private final static String					BASE_PATH_CLIENT_COMMON_SERVER	= BundleGenerator.BASE_PATH + "opentach-common\\src\\main\\resources\\com\\opentach\\server\\i18n";
	private final static String					BASE_PATH_CLIENT_COMMON_UTIL	= BundleGenerator.BASE_PATH + "opentach-common\\src\\main\\resources\\com\\opentach\\util\\i18n";
	private final static String					BASE_PATH_LABOR_ADMIN			= BundleGenerator.BASE_PATH + "opentach-labor\\opentach-labor-clientadmin\\src\\main\\resources\\i18n-admin";
	
	
		
	private final static Map<String, String>	modePath;
	static {
		modePath = new HashMap<>();
		BundleGenerator.modePath.put("client", BundleGenerator.BASE_PATH_CLIENT);
		BundleGenerator.modePath.put("dms", BundleGenerator.BASE_PATH_DMS);
		BundleGenerator.modePath.put("labor", BundleGenerator.BASE_PATH_LABOR);
		BundleGenerator.modePath.put("remotevehicle", BundleGenerator.BASE_PATH_REMOTEVEHICLE);
		BundleGenerator.modePath.put("support", BundleGenerator.BASE_PATH_SUPPORT);
		
		BundleGenerator.modePath.put("client_admin", BundleGenerator.BASE_PATH_CLIENT_ADMIN);
		BundleGenerator.modePath.put("client_download", BundleGenerator.BASE_PATH_CLIENT_DOWNLOAD);
		BundleGenerator.modePath.put("common_server", BundleGenerator.BASE_PATH_CLIENT_COMMON_SERVER);
		BundleGenerator.modePath.put("common_util", BundleGenerator.BASE_PATH_CLIENT_COMMON_UTIL);
		BundleGenerator.modePath.put("labor_admin", BundleGenerator.BASE_PATH_LABOR_ADMIN);
	}

	private final static String	BUNDLE_FILE		= "bundle";
	private final static String	LANG_DEFAULT	= "";
	private final static String	LANG_ES			= "_es_ES";
	private final static String	LANG_IT			= "_pt_PT";

	public static void main(String[] args) {
		for (Entry<String, String> entry : BundleGenerator.modePath.entrySet()) {

			try {
				// join default + es
				Properties propEs = new SortedProperties();
				BundleGenerator.loadBundle(propEs, entry.getValue(), BundleGenerator.LANG_DEFAULT);
				BundleGenerator.loadBundle(propEs, entry.getValue(), BundleGenerator.LANG_ES);

				// load it
				Properties propIt = new SortedProperties();
				BundleGenerator.loadBundle(propIt, entry.getValue(), BundleGenerator.LANG_IT);

				// Add missing to it
				int notFound = 0;
				int found = 0;
				Enumeration<Object> en = propEs.keys();
				while (en.hasMoreElements()) {
					Object key = en.nextElement();
					if (!propIt.containsKey(key)) {
						System.out.println(key);
						notFound++;
						propIt.put(key, propEs.get(key));
					} else {
						found++;
					}
				}
				System.out.println(notFound + " record missed in " + entry.getKey());
				System.out.println(found + " records found in " + entry.getKey());

				BundleGenerator.saveBundle(propIt, BundleGenerator.LANG_IT, entry.getKey());
				BundleGenerator.saveBundle(propEs, BundleGenerator.LANG_ES, entry.getKey());

			} catch (Exception err) {
				err.printStackTrace();
			}
		}

	}

	private static void saveBundle(Properties prop, String lang, String subFolder) throws FileNotFoundException, IOException {
		File folder = new File(BundleGenerator.OUTPUT_PATH + subFolder);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		try (FileOutputStream os = new FileOutputStream(new File(folder, BundleGenerator.BUNDLE_FILE + lang + ".properties"))) {
			prop.store(os, "");
		}
	}

	private static void loadBundle(Properties prop, String basePathClient, String lang) throws IOException {
		File file = new File(basePathClient, BundleGenerator.BUNDLE_FILE + lang + ".properties");
		if (!file.exists()) {
			System.err.println("Fichero " + file + " no existe");
			return;
		}
		try (FileInputStream fis = new FileInputStream(file)) {
			prop.load(fis);
		}
	}

	public static class SortedProperties extends Properties {
		@Override
		public Enumeration keys() {
			Enumeration keysEnum = super.keys();
			Vector<String> keyList = new Vector<String>();
			while (keysEnum.hasMoreElements()) {
				keyList.add((String) keysEnum.nextElement());
			}
			Collections.sort(keyList);
			return keyList.elements();
		}
	}

}
