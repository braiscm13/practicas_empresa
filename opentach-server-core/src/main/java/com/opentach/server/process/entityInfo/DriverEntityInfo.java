package com.opentach.server.process.entityInfo;

import java.util.Map;

import com.opentach.common.tacho.data.Conductor;

public class DriverEntityInfo extends AbstractClassEntityInfo<Conductor> {

	public DriverEntityInfo(Class<?> clazz, String autonumericCol, String userCol, String timeStampCol, String tabla, Map<String, String> columnsfields) {
		super(clazz, autonumericCol, userCol, timeStampCol, tabla, columnsfields);
	}

	@Override
	public boolean passFilter(Conductor bean) {
		return true;
	}

}
