package com.opentach.server.process.entityInfo;

import java.util.Map;

import com.ontimize.jee.common.tools.ObjectTools;
import com.opentach.common.tacho.data.Incidente;

public class IncidencesEntityInfo extends AbstractClassEntityInfo<Incidente> {

	public IncidencesEntityInfo(Class<?> clazz, String autonumericCol, String userCol, String timeStampCol, String tabla, Map<String, String> columnsfields) {
		super(clazz, autonumericCol, userCol, timeStampCol, tabla, columnsfields);
	}

	@Override
	public boolean passFilter(Incidente bean) {

		if ((bean.getProcedencia() != null) && !ObjectTools.isIn(bean.getProcedencia(), "DA", "TC", "VU")) {
			return false;
		}
		if ((bean.getProposito() == null) || (bean.getProposito().intValue() < 0) || ((bean.getProposito().intValue() > 9) && (bean.getProposito() != 128))) {
			return false;
		}
		return true;
	}

}
