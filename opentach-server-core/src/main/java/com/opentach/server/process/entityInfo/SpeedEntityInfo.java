package com.opentach.server.process.entityInfo;

import java.util.Map;

import com.opentach.common.tacho.data.Velocidad;

public class SpeedEntityInfo extends AbstractClassEntityInfo<Velocidad> {

	public SpeedEntityInfo(Class<?> clazz, String autonumericCol, String userCol, String timeStampCol, String tabla, Map<String, String> columnsfields) {
		super(clazz, autonumericCol, userCol, timeStampCol, tabla, columnsfields);
	}

	@Override
	public boolean passFilter(Velocidad bean) {
		return true;
	}

}
