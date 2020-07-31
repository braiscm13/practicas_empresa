package com.opentach.server.process.entityInfo;

import java.util.Map;

import com.opentach.common.tacho.data.IdentificacionVU;

public class VuIdentificationEntityInfo extends AbstractClassEntityInfo<IdentificacionVU> {

	public VuIdentificationEntityInfo(Class<?> clazz, String autonumericCol, String userCol, String timeStampCol, String tabla, Map<String, String> columnsfields) {
		super(clazz, autonumericCol, userCol, timeStampCol, tabla, columnsfields);
	}

	@Override
	public boolean passFilter(IdentificacionVU bean) {
		return true;
	}

}
