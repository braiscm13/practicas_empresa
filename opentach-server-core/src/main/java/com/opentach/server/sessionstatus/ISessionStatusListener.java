package com.opentach.server.sessionstatus;

import com.opentach.common.sessionstatus.AbstractStatusDto;

public interface ISessionStatusListener {

	void onUpdateSessionStatus(AbstractStatusDto status, int sessionId);

	void onFreeServerResources();

	void onSessionFinished(int sessionId);

}
