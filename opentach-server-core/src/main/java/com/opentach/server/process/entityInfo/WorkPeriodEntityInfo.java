package com.opentach.server.process.entityInfo;

import java.util.Map;

import com.ontimize.jee.common.tools.ObjectTools;
import com.opentach.common.tacho.data.PeriodoTrabajo;

public class WorkPeriodEntityInfo extends AbstractClassEntityInfo<PeriodoTrabajo> {

	public WorkPeriodEntityInfo(Class<?> clazz, String autonumericCol, String userCol, String timeStampCol, String tabla, Map<String, String> columnsfields) {
		super(clazz, autonumericCol, userCol, timeStampCol, tabla, columnsfields);
	}

	@Override
	public boolean passFilter(PeriodoTrabajo bean) {
		if ((bean.getProcedencia() != null) && !ObjectTools.isIn(bean.getProcedencia(), "DA", "TC", "VU")) {
			return false;
		}
		if ((bean.getTpPeriodo() == null) || (bean.getTpPeriodo().intValue() < 0) || (bean.getTpPeriodo().intValue() > 5)) {
			return false;
		}

		if ((bean.getTpPeriodo() == null) || (bean.getTpPeriodo().intValue() < 0) || (Integer.valueOf(bean.getRegion()).intValue() > 17)) {
			return false;
		}

		return true;
	}

}
