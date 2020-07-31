package com.opentach.client.dms.transfermanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * This class implements all funcionality to manage downloaders. It saves a list with all downloaders and methods to know status and list all of them.
 *
 */
public class DmsTransfererManagerFactory {

	private static final Logger				logger					= LoggerFactory.getLogger(DmsTransfererManagerFactory.class);

	// The unique instance of this class
	private static IDmsTransfererManager	sInstance				= null;

	/**
	 * Get an instance of this class
	 *
	 * @return the instance of this class
	 */
	public static IDmsTransfererManager getInstance() {
		if (DmsTransfererManagerFactory.sInstance == null) {
			DmsTransfererManagerFactory.sInstance = new DmsTransfererManagerDefault();
			DmsTransfererManagerFactory.sInstance.init();
		}
		return DmsTransfererManagerFactory.sInstance;
	}
}
