package com.opentach.server.process.entityInfo;

import java.util.Map;

import com.opentach.common.tacho.data.Calibrado;

public class CalibratedEntityInfo extends AbstractClassEntityInfo<Calibrado> {

	public CalibratedEntityInfo(Class<?> clazz, String autonumericCol, String userCol, String timeStampCol, String tabla, Map<String, String> columnsfields) {
		super(clazz, autonumericCol, userCol, timeStampCol, tabla, columnsfields);
	}

	@Override
	public boolean passFilter(Calibrado bean) {
		if ((bean.getTpCalibrado() == null) || (bean.getTpCalibrado() < 1) || (bean.getTpCalibrado() > 4)) {
			return false;
		}
		return true;
	}

}
