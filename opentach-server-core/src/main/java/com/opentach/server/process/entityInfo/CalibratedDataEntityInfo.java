package com.opentach.server.process.entityInfo;

import java.util.Map;

import com.opentach.common.tacho.data.CalibradoDataVU;

public class CalibratedDataEntityInfo extends AbstractClassEntityInfo<CalibradoDataVU> {

	public CalibratedDataEntityInfo(Class<?> clazz, String autonumericCol, String userCol, String timeStampCol, String tabla, Map<String, String> columnsfields) {
		super(clazz, autonumericCol, userCol, timeStampCol, tabla, columnsfields);
	}

	@Override
	public boolean passFilter(CalibradoDataVU bean) {
		return true;
	}

}
