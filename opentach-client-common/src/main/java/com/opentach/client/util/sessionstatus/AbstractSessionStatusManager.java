package com.opentach.client.util.sessionstatus;

import java.rmi.RemoteException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.client.util.UserInfoProvider;
import com.opentach.common.sessionstatus.AbstractStatusDto;
import com.opentach.common.sessionstatus.ISessionStatusService;
import com.opentach.common.user.IUserData;

public abstract class AbstractSessionStatusManager<T extends AbstractStatusDto> extends Thread {
	private static final Logger		logger		= LoggerFactory.getLogger(AbstractSessionStatusManager.class);
	public final static long		INTERVAL	= 20000;

	private final UserInfoProvider	userInfoProvider;
	private final T					status;

	public AbstractSessionStatusManager(UserInfoProvider userInfoProvider, String user) {
		super("SessionStatusManager");
		this.userInfoProvider = userInfoProvider;
		this.setDaemon(true);
		this.status = this.createStatusDto();
		this.status.setSessionId(userInfoProvider.getSessionId());
		try {
			IUserData userData = userInfoProvider.getUserData();
			this.status.setLevel(userData.getLevelDscr());
			StringBuilder sb = new StringBuilder();
			if (userData.getCompaniesList() != null) {
				for (String name : userData.getCompanyNameList()) {
					sb.append(name).append(", ");
				}
			}
			this.status.setCompanies(sb.toString());
		} catch (Exception error) {
			AbstractSessionStatusManager.logger.error(null, error);
		}
		this.status.setStartupTime(new Date());
		this.status.setUser(user);
	}

	@Override
	public void run() {
		super.run();
		while (true) {
			try {
				this.updateStatus();
			} catch (RemoteException ex) {
				AbstractSessionStatusManager.logger.error("E_UNNACCESSIBLE_SERVER", ex);
			} catch (Exception error) {
				AbstractSessionStatusManager.logger.error(null, error);
			} finally {
				try {
					Thread.sleep(AbstractSessionStatusManager.INTERVAL);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	protected void updateStatus() throws Exception {
		this.status.setCurrentDate(new Date());
		this.userInfoProvider.getRemoteService(ISessionStatusService.class).updateSessionStatus(this.status, this.userInfoProvider.getSessionId());
		this.status.setSaved(true);
	}

	protected abstract T createStatusDto();

	public UserInfoProvider getUserInfoProvider() {
		return this.userInfoProvider;
	}

	public T getStatus() {
		return this.status;
	}
}