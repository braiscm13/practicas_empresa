package com.opentach.server.process.entityInfo;

import java.util.Map;

import com.opentach.common.tacho.data.RegistroKmVehiculo;

public class KmVehicleRecordEntityInfo extends AbstractClassEntityInfo<RegistroKmVehiculo> {

	public KmVehicleRecordEntityInfo(Class<?> clazz, String autonumericCol, String userCol, String timeStampCol, String tabla, Map<String, String> columnsfields) {
		super(clazz, autonumericCol, userCol, timeStampCol, tabla, columnsfields);
	}

	@Override
	public boolean passFilter(RegistroKmVehiculo bean) {
		return true;
	}

}
