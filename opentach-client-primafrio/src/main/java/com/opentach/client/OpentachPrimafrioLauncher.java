package com.opentach.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.client.sessionstatus.StatisticsRecollector;
import com.opentach.client.util.OpentachApplicationLauncher;
import com.opentach.common.OpentachFieldNames;

public final class OpentachPrimafrioLauncher {

	private static final Logger logger = LoggerFactory.getLogger(OpentachPrimafrioLauncher.class);

	private OpentachPrimafrioLauncher() {
		super();
	}

	public static void main(String[] args) throws Exception {
		System.setProperty("com.ontimize.util.rmitunneling.httpsession", "true");
		StatisticsRecollector.getInstance().start();
		args = OpentachLauncher.configureSystemProperties(args);

		OpentachLauncher.setOntimizeSettings();


		try {
			if ((args == null) || (args.length == 0)) {
				args = new String[] { "com/opentach/client/labels.xml", "clientapplication_primafrio.xml" };
				OpentachPrimafrioLauncher.logger.info("Using default lauch parameters: {} , {}", args[0], args[1]);
			}
			System.setProperty(OpentachFieldNames.APP_CODE, OpentachFieldNames.APP_CODE_OPENTACH);
			OpentachApplicationLauncher.main(args);
		} catch (Exception ex) {
			OpentachPrimafrioLauncher.logger.error(null, ex);
			throw ex;
		}
	}

}
