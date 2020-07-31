package com.opentach.server.process.entityInfo;

import java.util.Map;

import com.opentach.common.tacho.data.RegistroKmConductor;

public class KmDriverRecordEntityInfo extends AbstractClassEntityInfo<RegistroKmConductor> {

	public KmDriverRecordEntityInfo(Class<?> clazz, String autonumericCol, String userCol, String timeStampCol, String tabla, Map<String, String> columnsfields) {
		super(clazz, autonumericCol, userCol, timeStampCol, tabla, columnsfields);
	}

	@Override
	public boolean passFilter(RegistroKmConductor bean) {
		return true;
	}

}
