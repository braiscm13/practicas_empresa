package com.opentach.client.simpledownloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.tool.ILogger;

public class Launcher {

	// private static final Logger logger = LoggerFactory.getLogger(Launcher.class);

	public static void main(String[] args) {
		System.setProperty("org.slf4j.simpleLogger.logFile", "/Users/imatia/log.txt");
		// System.setProperty("org.slf4j.simpleLogger.logFile", "f:/log.txt");
		final Logger logger = LoggerFactory.getLogger(Launcher.class);
		com.imatia.tacho.tool.LoggerFactory.setLogger(new ILogger() {
			@Override
			public void log(String level, String value) {
				logger.info("[{}] {}", level, value);
			}

			@Override
			public void error(Throwable err) {
				logger.error(null, err);
			}

			@Override
			public void print(Throwable err) {
				this.error(err);
			}

			@Override
			public void println(String str) {
				this.log(ILogger.INFO, str);
			}
		});
		new SimpleDownloaderUI().startup();

	}

}
