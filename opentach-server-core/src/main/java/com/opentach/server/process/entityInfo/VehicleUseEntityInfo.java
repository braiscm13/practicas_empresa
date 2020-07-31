package com.opentach.server.process.entityInfo;

import java.util.Map;

import com.ontimize.jee.common.tools.ObjectTools;
import com.opentach.common.tacho.data.UsoVehiculo;

public class VehicleUseEntityInfo extends AbstractClassEntityInfo<UsoVehiculo> {

	public VehicleUseEntityInfo(Class<?> clazz, String autonumericCol, String userCol, String timeStampCol, String tabla, Map<String, String> columnsfields) {
		super(clazz, autonumericCol, userCol, timeStampCol, tabla, columnsfields);
	}

	@Override
	public boolean passFilter(UsoVehiculo bean) {
		if (bean.getKmFin() < 0) {
			return false;
		}
		if ((bean.getProcedencia() != null) && !ObjectTools.isIn(bean.getProcedencia(), "DA", "TC", "VU")) {
			return false;
		}
		return true;
	}

}
