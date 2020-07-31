package com.opentach.common.sessionstatus;


import java.rmi.Remote;

import com.ontimize.db.EntityResult;

public interface ISessionStatusService extends Remote {

	/** The Constant ID. */
	final static String	ID	= "SessionStatusService";

	void updateSessionStatus(AbstractStatusDto status, int sessionId) throws Exception;

	void check() throws Exception;

	EntityResult getSessionStatus(int sessionId) throws Exception;

	// public void setUserAlive(int sessionID) throws Exception;
}
