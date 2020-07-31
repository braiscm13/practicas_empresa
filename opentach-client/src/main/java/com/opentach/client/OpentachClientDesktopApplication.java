package com.opentach.client;

import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpentachClientDesktopApplication extends OpentachClientApplication {
	private static final Logger logger = LoggerFactory.getLogger(OpentachClientDesktopApplication.class);

	public OpentachClientDesktopApplication(Hashtable p) throws Exception {
		super(p);
	}

	@Override
	public void show() {
		super.show();
	}

}
