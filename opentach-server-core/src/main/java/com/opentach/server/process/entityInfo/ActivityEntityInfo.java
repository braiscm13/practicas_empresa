package com.opentach.server.process.entityInfo;

import java.util.Map;

import com.opentach.common.tacho.data.Actividad;

public class ActivityEntityInfo extends AbstractClassEntityInfo<Actividad> {

	private final String	UNKNOWN_PLATE	= "???????????";
	private final String	UNKNOWN_DRIVER	= "XXXXXXXXX";

	public ActivityEntityInfo(Class<?> clazz, String autonumericCol, String userCol, String timeStampCol, String tabla, Map<String, String> columnsfields) {
		super(clazz, autonumericCol, userCol, timeStampCol, tabla, columnsfields);
	}

	@Override
	public boolean passFilter(Actividad bean) {
		if (this.UNKNOWN_PLATE.equals(bean.getMatricula()) && this.UNKNOWN_DRIVER.equals(bean.getIdConductor())) {
			return false;
		}
		if ((bean.getTpActividad() == null) || (bean.getTpActividad().intValue() < 1) || (bean.getTpActividad().intValue() > 5)) {
			return false;
		}
		return true;
	}

}
