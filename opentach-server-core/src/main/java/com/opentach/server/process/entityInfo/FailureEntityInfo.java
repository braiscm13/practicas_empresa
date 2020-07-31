package com.opentach.server.process.entityInfo;

import java.util.Map;

import com.opentach.common.tacho.data.Fallo;

public class FailureEntityInfo extends AbstractClassEntityInfo<Fallo> {

	public FailureEntityInfo(Class<?> clazz, String autonumericCol, String userCol, String timeStampCol, String tabla, Map<String, String> columnsfields) {
		super(clazz, autonumericCol, userCol, timeStampCol, tabla, columnsfields);
	}

	@Override
	public boolean passFilter(Fallo bean) {
		if ((bean.getTpFallo() == null) || (bean.getTpFallo() < 0)) {
			return false;
		}
		if (bean.getTpFallo() > 64) {
			if ((bean.getTpFallo() != 128) && (bean.getTpFallo() < 500)) {
				return false;
			} else if (bean.getTpFallo() > 513) {
				return false;
			}
		}
		return true;
	}
}
