package com.opentach.server;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.db.UserEntity;
import com.ontimize.gui.ClientWatch;
import com.ontimize.jee.common.tools.DateTools;
import com.ontimize.util.alerts.AlertManager;
import com.ontimize.util.alerts.DefaultAlertManager;
import com.ontimize.util.alerts.IAlertSystem;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.user.IUserData;
import com.opentach.server.activities.InfractionService;
import com.opentach.server.entities.Usuario;
import com.opentach.server.sessionstatus.SessionStatusService;
import com.opentach.server.tdi.sync.TimerTaskTdiSync;
import com.opentach.server.util.DBUtils;

public class OpentachServerLocator extends AbstractOpentachServerLocator implements IAlertSystem {

	private static final Logger					logger					= LoggerFactory.getLogger(OpentachServerLocator.class);
	private static final List<String>			LEVELS_BLOCKED_MAIN_APP	= Arrays.asList(IUserData.NIVEL_TDI);

	private AlertManager						alertManager			= null;
	private String								appName					= null;

	public OpentachServerLocator(int port, Hashtable<String, Object> params) throws Exception {
		super(port, params);

		String sEnableAlerts = (String) params.get("enablealerts");
		if ((sEnableAlerts != null) && Boolean.valueOf(sEnableAlerts).booleanValue()) {
			try {
				DefaultAlertManager.initializeManager(this);
				this.alertManager = DefaultAlertManager.getAlertManager();
			} catch (Exception e) {
				OpentachServerLocator.logger.error(null, e);
			}
		}

		// AppName
		this.appName = (String) params.get("ApplicationName");
		System.setProperty(OpentachFieldNames.APP_CODE, OpentachFieldNames.APP_CODE_OPENTACH);

		// start TDI sync
		this.launchTaskTDISync();
	}

	private void launchTaskTDISync() {
		Timer timer = new Timer("TdiSync");
		TimerTask tt = new TimerTaskTdiSync();
		timer.schedule(tt, this.getNightDelay(), DateTools.MILLISECONDS_PER_DAY);// 24 hours
		// timer.schedule(tt, 0, DateTools.MILLISECONDS_PER_DAY);// 24 hours
	}


	@Override
	public int startSession(String user, String password, ClientWatch cw) throws Exception {
		return this.startSession(user, password, cw, false);
	}

	public int startSessionWS(String user, String password, ClientWatch cw) throws Exception {
		return this.startSession(user, password, cw, true);
	}

	protected int startSession(String user, String password, ClientWatch cw, boolean fromWS) throws Exception {
		int id = -1;
		if ((password != null) && (password.length() < 16)) {
			try {
				Usuario.checkExpired(user, this);
				id = super.startSession(user, password, cw);
				if (id >= 0) {
					if (!fromWS && OpentachServerLocator.LEVELS_BLOCKED_MAIN_APP.contains(AbstractOpentachServerLocator.locator.getUserData(id).getLevel())) {
						try {
							this.endSession(id);
						} catch (Exception error) {
							OpentachServerLocator.logger.info("Error closing invalid session", error);
						}
						throw new Exception("E_UNAUTHORIZED_USER");
					}
					this.onSessionStarted(user, id);
				}
			} catch (Exception e) {
				OpentachServerLocator.logger.error(null, e);
				if (id > 0) {
					try {
						this.endSession(id);
					} catch (Exception err) {
						OpentachServerLocator.logger.error("session already closed?", err);
					}
				}
				throw e;
			}
		}
		return id;
	}

	@Override
	public void endSession(int sessionID) throws Exception {
		this.userDataCache.remove(sessionID);
		this.getService(SessionStatusService.class).onSessionFinished(sessionID);
		super.endSession(sessionID);
	}

	@Override
	public void freeServerResources() {
		// stop file processing
		try {
			this.getService(InfractionService.class).shutdown();
		} catch (Exception e) {
			OpentachServerLocator.logger.error(null, e);
		}
		try {
			this.getService(SessionStatusService.class).freeServerResources();
		} catch (Exception e) {
			OpentachServerLocator.logger.error(null, e);
		}
		super.freeServerResources();
	}

	@Override
	public String[] getAlertGroups() {
		return this.alertManager.getAlertGroups();
	}

	@Override
	public EntityResult getAlertsGroupData(String group) {
		return this.alertManager.getAlertsGroupData(group);
	}

	@Override
	public EntityResult getAllAlertsData() throws RemoteException {
		return this.alertManager.getAllAlertsData();
	}

	@Override
	public boolean pauseAlert(String name, String group) throws RemoteException {
		return this.alertManager.pauseAlert(name, group);
	}

	@Override
	public boolean resumeAlert(String name, String group) throws RemoteException {
		return this.alertManager.resumeAlert(name, group);
	}

	@Override
	public EntityResult getAlertData(String taskName, String taskGroup, String cronName, String cronGroup) throws RemoteException {
		return this.alertManager.getAlertData(taskName, taskGroup, cronName, cronGroup);
	}

	@Override
	public void updateAlertConfiguration(Hashtable alertConfiguration) {
		this.alertManager.updateAlertConfiguration(alertConfiguration);
	}

	public String getApplicationName() {
		return this.appName;
	}

	@Override
	protected IUserData getUserDataFromDB(int sessionID) throws Exception {
		return DBUtils.getUserDataFromDB(this, sessionID);
	}

	@Override
	public boolean supportChangePassword(String user, int sessionId) throws Exception {
		if (sessionId < 0) {
			return false;
		}

		String loginEntityName = this.getLoginEntityName(sessionId);
		UserEntity eUserEntity = (UserEntity) this.getEntityReferenceFromServer(loginEntityName);

		if (!eUserEntity.isPeriodical()) {
			return false;
		}
		Vector<String> av = new Vector<>();
		av.add(eUserEntity.getPasswordLastUpdateColumn());
		Hashtable<String, Object> kv = new Hashtable<>();
		kv.put((String) eUserEntity.getKeys().get(0), user);
		EntityResult eResult = eUserEntity.query(kv, av, sessionId);
		Hashtable uResult = eResult.getRecordValues(0);
		Timestamp lastPasswordUpdate = (Timestamp) uResult.get(eUserEntity.getPasswordLastUpdateColumn());
		if (lastPasswordUpdate == null) {
			lastPasswordUpdate = new Timestamp(System.currentTimeMillis());
		}
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		long diff = (currentTime.getTime() - lastPasswordUpdate.getTime());
		return (diff > eUserEntity.getPeriod());
	}
}
