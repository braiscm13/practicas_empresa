package com.opentach.server.sessionstatus;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.common.sessionstatus.AbstractStatusDto;
import com.opentach.common.user.ICDOUserData;
import com.opentach.common.user.IUserData;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.mail.MailComposer;
import com.opentach.server.mail.MailService;
import com.opentach.server.util.DBUtils;

public class AnonymousSessionControl implements ISessionStatusListener {

	private static final String	ANONYMOUSTIMECHECK	= "AnonymousTimeCheck";
	private static final String	ANONYMOUSMAXTICKS	= "AnonymousMaxTicks";

	private static final Logger	logger				= LoggerFactory.getLogger(AnonymousSessionControl.class);

	private class SessionInfo {
		private final String	user;
		private long			time;
		private boolean			warn;

		public SessionInfo(String user, long time) {
			this.user = user;
			this.time = time;
			this.warn = false;
		}

		public long getTime() {
			return this.time;
		}

		public void setTime(long time) {
			this.time = time;
		}

		public boolean isWarn() {
			return this.warn;
		}

		public void setWarn(boolean warn) {
			this.warn = warn;
		}

		public String getUser() {
			return this.user;
		}

		@Override
		public int hashCode() {
			return this.user.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof SessionInfo) {
				return this.user.equals(((SessionInfo) obj).user);
			}
			return super.equals(obj);
		}
	}

	private class SessionChecker extends Thread {

		private transient boolean	close;
		private final long			tickTime;
		private final int			maxTicks;

		public SessionChecker(final long tickTime, final int maxTicks) {
			super(SessionChecker.class.getName());
			this.setPriority(Thread.MIN_PRIORITY);
			this.tickTime = tickTime;
			this.maxTicks = maxTicks;
			this.close = false;
		}

		public void close() {
			this.close = true;
		}

		@Override
		public void run() {
			while (!this.close) {
				try {
					Thread.sleep(this.tickTime);
				} catch (InterruptedException e) {
					AnonymousSessionControl.logger.error(null, e);
				}
				final long currentTime = System.currentTimeMillis();
				Hashtable<Integer, SessionInfo> htData = new Hashtable<Integer, SessionInfo>(AnonymousSessionControl.this.mSessionTime);
				Set<String> sUsers = new HashSet<String>();
				for (int sessionID : htData.keySet()) {
					SessionInfo si = htData.get(sessionID);
					sUsers.add(si.getUser());
					if (((si.getTime() + (this.tickTime * this.maxTicks)) < currentTime) && !si.isWarn()) {
						// envío mail de caducada
						try {
							AnonymousSessionControl.this.sendMail(sessionID);
						} catch (Exception e) {
							AnonymousSessionControl.logger.error(null, e);
						} finally {
							si.setWarn(true);
						}
					}
				}
				// ahora envio mail a los no conectados
				Set<String> sMonit = DBUtils.getMonitorizedUsers(AnonymousSessionControl.this.ol);
				sMonit.removeAll(sUsers);
				final long time = System.currentTimeMillis();
				Map<String, SessionInfo> mData = null;
				// syncronizate!!
				synchronized (AnonymousSessionControl.this.mNotConnectedUsers) {
					for (String user : sMonit) {
						SessionInfo si = AnonymousSessionControl.this.mNotConnectedUsers.get(user);
						if (si == null) {
							si = new SessionInfo(user, time);
							AnonymousSessionControl.this.mNotConnectedUsers.put(user, si);
						}
					}
					mData = new HashMap<String, SessionInfo>(AnonymousSessionControl.this.mNotConnectedUsers);
				}
				for (SessionInfo si : mData.values()) {
					if (((si.getTime() + (this.tickTime * this.maxTicks)) < currentTime) && !si.isWarn()) {
						try {
							IUserData ud = DBUtils.getSimpleUserDataFromDB(AnonymousSessionControl.this.ol, si.getUser());
							String dscr = (ud instanceof ICDOUserData) ? ((ICDOUserData) ud).getDscr() : null;
							AnonymousSessionControl.this.sendMail(AnonymousSessionControl.this.ol.getService(MailService.class).getMailSuppAddress(), si.getUser(), dscr);
						} catch (Exception e) {
							AnonymousSessionControl.logger.error(null, e);
						} finally {
							si.setWarn(true);
						}
					}
				}
			}
		}
	}

	private final Map<Integer, SessionInfo>	mSessionTime;
	private final Map<String, SessionInfo>	mNotConnectedUsers;
	private SessionChecker					sessionChecker;
	private final IOpentachServerLocator	ol;

	public AnonymousSessionControl(final IOpentachServerLocator ol, Hashtable hconfig) {
		super();
		this.mSessionTime = new Hashtable<Integer, SessionInfo>();
		this.mNotConnectedUsers = new Hashtable<String, SessionInfo>();
		this.ol = ol;
		ol.getService(SessionStatusService.class).addSessionStatusListener(this);
		String sTickTime = (String) hconfig.get(AnonymousSessionControl.ANONYMOUSTIMECHECK);
		String sMaxTicks = (String) hconfig.get(AnonymousSessionControl.ANONYMOUSMAXTICKS);
		if ((sTickTime != null) && (sMaxTicks != null)) {
			int tickTime = -1;
			int maxTicks = -1;
			try {
				tickTime = Integer.parseInt(sTickTime);
			} catch (Exception e) {
			}
			try {
				maxTicks = Integer.parseInt(sMaxTicks);
			} catch (Exception e) {
			}
			if ((tickTime > 0) && (maxTicks > 0)) {
				this.sessionChecker = new SessionChecker(tickTime, maxTicks);
				this.start();
			} else {
				AnonymousSessionControl.logger.warn("anonymous session control not initalized");
			}
		} else {
			AnonymousSessionControl.logger.warn("anonymous session control not initalized");
		}

	}

	public void endSession(int sessionID) {
		this.mSessionTime.remove(sessionID);
	}

	private void sendMail(int sessionID) throws Exception {
		final String mailTo = this.ol.getService(MailService.class).getMailSuppAddress();
		if (mailTo != null) {
			IUserData ud = this.ol.getUserData(sessionID);
			if (ud != null) {
				String dscr = (ud instanceof ICDOUserData) ? ((ICDOUserData) ud).getDscr() : null;
				this.sendMail(mailTo, ud.getLogin(), dscr);
			}
		}
	}

	private void sendMail(String mailTo, String user, String dscr) throws Exception {
		if (mailTo != null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(CDODownMailComposer.INCIDATE, new Date());
			params.put(CDODownMailComposer.USER, user);
			if (dscr != null) {
				params.put(CDODownMailComposer.DSCR, dscr);
			}
			MailComposer mc = new CDODownMailComposer(mailTo, null, null, params);
			this.ol.getService(MailService.class).sendMail(mc);
		}
	}

	public Map<Integer, Boolean> getStatus() {
		Map<Integer, Boolean> mStatus = new HashMap<Integer, Boolean>();
		Map<Integer, SessionInfo> htData = new Hashtable<Integer, SessionInfo>(this.mSessionTime);
		for (int key : htData.keySet()) {
			SessionInfo si = htData.get(key);
			mStatus.put(key, !si.isWarn());
		}
		return mStatus;
	}

	public void start() {
		this.sessionChecker.start();
	}

	public void close() {
		this.sessionChecker.close();
		try {
			this.sessionChecker.join();
		} catch (InterruptedException e) {
			AnonymousSessionControl.logger.error(null, e);
		}
	}

	@Override
	public void onUpdateSessionStatus(AbstractStatusDto status, int sessionId) {
		if (this.ol.hasSession(sessionId)) {
			this.updateSession(sessionId);
		}
	}

	public void updateSession(int sessionID) {
		final long time = System.currentTimeMillis();
		SessionInfo si = this.mSessionTime.get(sessionID);
		if (si == null) {
			IUserData ud = null;
			try {
				ud = this.ol.getUserData(sessionID);
			} catch (Exception e) {

			}
			if (ud != null) {
				si = new SessionInfo(ud.getLogin(), time);
				this.mSessionTime.put(sessionID, si);
			} else {
				this.endSession(sessionID);
			}
		} else {
			si.setTime(time);
			si.setWarn(false);
		}
		if (si != null) {
			this.mNotConnectedUsers.remove(si.getUser());
		}
	}

	@Override
	public void onFreeServerResources() {
		this.close();
	}

	@Override
	public void onSessionFinished(int sessionId) {
		this.endSession(sessionId);
	}

}
