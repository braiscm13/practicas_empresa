package com.opentach.server.process.entityInfo;

import java.util.Map;

import com.ontimize.jee.common.tools.ObjectTools;
import com.opentach.common.tacho.data.Control;

public class ControlEntityInfo extends AbstractClassEntityInfo<Control> {
	private static Object[] VALID_VALUES = new Object[] { 0, 16, 32, 48, 64, 80, 96, 112, 128, 144, 160, 176, 192, 208, 224, 240 };

	public ControlEntityInfo(Class<?> clazz, String autonumericCol, String userCol, String timeStampCol, String tabla, Map<String, String> columnsfields) {
		super(clazz, autonumericCol, userCol, timeStampCol, tabla, columnsfields);
	}


	@Override
	public boolean passFilter(Control bean) {
		if ((bean.getTpControl() == null) || !ObjectTools.isIn(bean.getTpControl(), ControlEntityInfo.VALID_VALUES)) {
			return false;
		}
		return true;
	}

}
