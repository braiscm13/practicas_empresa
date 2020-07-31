package com.opentach.server.process.entityInfo;

import java.util.Map;

import com.opentach.common.tacho.data.Tarjeta;

public class CardEntityInfo extends AbstractClassEntityInfo<Tarjeta> {

	public CardEntityInfo(Class<?> clazz, String autonumericCol, String userCol, String timeStampCol, String tabla, Map<String, String> columnsfields) {
		super(clazz, autonumericCol, userCol, timeStampCol, tabla, columnsfields);
	}

	@Override
	public boolean passFilter(Tarjeta bean) {
		return true;
	}

}
