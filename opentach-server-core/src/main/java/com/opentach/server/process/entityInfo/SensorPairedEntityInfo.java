package com.opentach.server.process.entityInfo;

import java.util.Map;

import com.opentach.common.tacho.data.SensorPairedVU;

public class SensorPairedEntityInfo extends AbstractClassEntityInfo<SensorPairedVU> {

	public SensorPairedEntityInfo(Class<?> clazz, String autonumericCol, String userCol, String timeStampCol, String tabla, Map<String, String> columnsfields) {
		super(clazz, autonumericCol, userCol, timeStampCol, tabla, columnsfields);
	}

	@Override
	public boolean passFilter(SensorPairedVU bean) {
		return true;
	}

}
