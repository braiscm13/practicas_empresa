package com.opentach.server.sessionstatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.sessionstatus.AbstractStatusDto;
import com.opentach.common.sessionstatus.ISessionStatusService;
import com.opentach.server.IOpentachServerLocator;
import com.utilmize.server.services.UAbstractService;

public class SessionStatusService extends UAbstractService implements ISessionStatusService {

	private static final Logger						logger				= LoggerFactory.getLogger(SessionStatusService.class);


	private final Map<Integer, AbstractStatusDto>	cacheStatus;

	private final StatisticsManager					statisticsManager;
	private final List<ISessionStatusListener>		listeners;

	public SessionStatusService(int port, EntityReferenceLocator erl, Hashtable hconfig) throws Exception {
		super(port, erl, hconfig);
		this.listeners = new ArrayList<>();
		this.cacheStatus = new HashMap<Integer, AbstractStatusDto>();
		this.statisticsManager = new StatisticsManager((IOpentachServerLocator) this.getLocator());
	}

	public void setSessionIdLastAccessTime(long time, int sessionId) {
		AbstractStatusDto dto = this.cacheStatus.get(sessionId);
		if (dto != null) {
			dto.setLastAccessTime(new Date(time));
		}
	}

	@Override
	public void updateSessionStatus(AbstractStatusDto status, int sessionId) throws Exception {
		status.setPingTime(new Date());
		AbstractStatusDto removed = this.cacheStatus.get(status.getSessionId());
		if (removed != null) {
			status.setLastAccessTime(removed.getLastAccessTime());
		}
		this.cacheStatus.put(status.getSessionId(), status);
		if (!status.isSaved()) {
			this.statisticsManager.logOpenSession(status);
		}
		this.statisticsManager.updateStatistics(status);
		for (ISessionStatusListener listener : this.listeners) {
			listener.onUpdateSessionStatus(status, sessionId);
		}
	}

	public void freeServerResources() {
		for (ISessionStatusListener listener : this.listeners) {
			listener.onFreeServerResources();
		}
	}

	@Override
	public void check() {}

	@Override
	public EntityResult getSessionStatus(int sessionId) throws Exception {
		Vector<Object> status = new Vector<Object>(this.cacheStatus.values());
		EntityResult er = new EntityResult();
		EntityResultTools.initEntityResult(er, "STATUS");
		er.put("STATUS", status);
		return er;
	}

	public void clearSessions() {
		this.statisticsManager.clearSessions();
	}

	public void onSessionStarted(int id) {
		// do nothing just right
	}

	public void onSessionFinished(int sessionId) {
		AbstractStatusDto statusFinished = this.cacheStatus.get(sessionId);
		for (ISessionStatusListener listener : this.listeners) {
			listener.onSessionFinished(sessionId);
		}

		try {
			this.statisticsManager.logCloseSession(statusFinished);
		} catch (Exception error) {
			SessionStatusService.logger.error(null, error);
		}

	}

	public void addSessionStatusListener(ISessionStatusListener listener) {
		this.listeners.add(listener);
	}
}
