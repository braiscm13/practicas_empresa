package com.opentach.common.user;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.opentach.common.user.IUserData;

public class AllUserInfo implements Serializable {

	private static final long			serialVersionUID	= 4;

	private final List<IUserData>		lSessions;
	private final Map<String, Boolean>	mStatus;

	public AllUserInfo(List<IUserData> lSessions, Map<String, Boolean> mStatus) {
		this.lSessions = lSessions;
		this.mStatus = mStatus;
	}

	public List<IUserData> getLSessions() {
		return this.lSessions;
	}

	public Map<String, Boolean> getMStatus() {
		return this.mStatus;
	}
}
