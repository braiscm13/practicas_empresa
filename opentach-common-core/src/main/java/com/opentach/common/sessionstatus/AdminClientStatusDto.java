package com.opentach.common.sessionstatus;

import java.util.List;


public class AdminClientStatusDto extends ClientStatusDto {

	public AdminClientStatusDto(List<StatisticsDto> statistics) {
		super(statistics);
	}

	@Override
	public String getApp() {
		return "ADMIN_CLIENT";
	}

}
