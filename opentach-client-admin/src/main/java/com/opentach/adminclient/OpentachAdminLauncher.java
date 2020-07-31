package com.opentach.adminclient;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.report.ReportManager;
import com.ontimize.xml.DefaultXMLParametersManager;
import com.opentach.client.OpentachLauncher;
import com.opentach.client.report.CustomDynamicJasperEngine;
import com.opentach.client.util.OpentachApplicationLauncher;
import com.opentach.common.OpentachFieldNames;
import com.utilmize.client.tools.FxUtils;

import javafx.application.Platform;

public final class OpentachAdminLauncher {

	private static final Logger	logger	= LoggerFactory.getLogger(OpentachAdminLauncher.class);

	private OpentachAdminLauncher() {
		super();
	}

	public static String[] configureSystemProperties(String[] args) {
		List<String> arguments = new ArrayList<String>();
		for (int i = 0; i < args.length; i++) {
			String value = args[i];
			if (value != null) {
				value = value.trim();
				if (value.startsWith("-D") && (value.length() > 2) && (value.indexOf("=") > 0)) {
					String inputP = value.substring(2);
					StringTokenizer token = new StringTokenizer(inputP, "=");
					if (token.hasMoreTokens()) {
						String propertyKey = token.nextToken();
						if (token.hasMoreTokens()) {
							String propertyValue = token.nextToken();
							System.setProperty(propertyKey, propertyValue);
						}
					}
				} else {
					if (!"ignore".equals(value)) {
						arguments.add(value);
					}
				}
			}
		}
		return arguments.toArray(new String[arguments.size()]);
	}

	public static void checkLibraries() {
		new Thread("Check Libraries") {
			@Override
			public void run() {
				super.run();
				try {
					// com.ontimize.report.ReportManager.createReportEngine();
					// Ontimize lanza esto en un hilo y me impide cambiarlo, de ahí el sleep
					Thread.sleep(4000);
					ReportManager.registerNewReportEngine(new CustomDynamicJasperEngine());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public static void main(String[] args) throws Exception {
		System.setProperty("com.ontimize.util.rmitunneling.httpsession", "true");
		Platform.setImplicitExit(false);
		FxUtils.ensureFX();
		// StatisticsRecollector.getInstance().start();
		args = OpentachAdminLauncher.configureSystemProperties(args);

		OpentachLauncher.setOntimizeSettings();

		OpentachAdminLauncher.checkLibraries();
		DefaultXMLParametersManager.setXMLDefaultParameterFile("ontimize-configuration-parameters-ui.xml");

		try {
			if ((args == null) || (args.length == 0)) {
				args = new String[] { "labels-admin.xml", "clientapplication-admin.xml" };
				OpentachAdminLauncher.logger.info("Using default lauch parameters: {} , {}", args[0], args[1]);
			}

			System.setProperty(OpentachFieldNames.APP_CODE, OpentachFieldNames.APP_CODE_OPENTACH);
			OpentachApplicationLauncher.main(args);
			UIManager.getDefaults().put("List.foreground", new Color(0x335971));
			UIManager.getDefaults().put("List.selectionBackground", new Color(0x335971));
		} catch (Exception ex) {
			OpentachAdminLauncher.logger.error(null, ex);
			throw ex;
		}
	}

}
