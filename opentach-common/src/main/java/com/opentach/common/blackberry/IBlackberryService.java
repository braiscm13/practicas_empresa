package com.opentach.common.blackberry;

import java.rmi.Remote;
import java.util.Vector;

public interface IBlackberryService extends Remote {
	/** The Constant ID. */
	final static String	ID	= "BlackberryService";

	public void sendPushBlackberry(Vector v, String sms) throws Exception;

}