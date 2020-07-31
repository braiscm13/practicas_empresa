package com.opentach.adminclient.sessionstatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.client.sessionstatus.StatisticsRecollector;
import com.opentach.client.util.UserInfoProvider;
import com.opentach.client.util.sessionstatus.AbstractSessionStatusManager;
import com.opentach.common.sessionstatus.AdminClientStatusDto;
import com.utilmize.tools.VersionUtils;

public class AdminClientSessionStatusManager extends AbstractSessionStatusManager<AdminClientStatusDto> {
	private static final Logger			logger	= LoggerFactory.getLogger(AdminClientSessionStatusManager.class);

	public AdminClientSessionStatusManager(UserInfoProvider userInfoProvider, String user) {
		super(userInfoProvider, user);
	}

	@Override
	protected AdminClientStatusDto createStatusDto() {
		AdminClientStatusDto adminClientStatusDto = new AdminClientStatusDto(StatisticsRecollector.getInstance().getStatistics());
		adminClientStatusDto.setVersion(VersionUtils.getVersion(AdminClientSessionStatusManager.class));
		return adminClientStatusDto;
	}

	@Override
	protected void updateStatus() throws Exception {
		super.updateStatus();
		StatisticsRecollector.getInstance().onStatusUpdate();
	}

}
