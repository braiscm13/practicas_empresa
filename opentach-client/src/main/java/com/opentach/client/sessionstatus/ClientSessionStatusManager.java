package com.opentach.client.sessionstatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.client.util.UserInfoProvider;
import com.opentach.client.util.sessionstatus.AbstractSessionStatusManager;
import com.opentach.common.sessionstatus.ClientStatusDto;
import com.utilmize.tools.VersionUtils;

public class ClientSessionStatusManager extends AbstractSessionStatusManager<ClientStatusDto> {
	private static final Logger		logger		= LoggerFactory.getLogger(ClientSessionStatusManager.class);

	public ClientSessionStatusManager(UserInfoProvider userInfoProvider, String user) {
		super(userInfoProvider, user);
	}

	@Override
	protected ClientStatusDto createStatusDto() {
		ClientStatusDto clientStatusDto = new ClientStatusDto(StatisticsRecollector.getInstance().getStatistics());
		clientStatusDto.setVersion(VersionUtils.getVersion(ClientSessionStatusManager.class));
		return clientStatusDto;
	}

	@Override
	protected void updateStatus() throws Exception {
		super.updateStatus();
		StatisticsRecollector.getInstance().onStatusUpdate();
	}

}