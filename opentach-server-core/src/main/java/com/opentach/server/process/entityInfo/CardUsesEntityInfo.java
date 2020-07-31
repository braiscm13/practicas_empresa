package com.opentach.server.process.entityInfo;

import java.util.Map;

import com.opentach.common.tacho.data.UsoTarjeta;

public class CardUsesEntityInfo extends AbstractClassEntityInfo<UsoTarjeta> {

	public CardUsesEntityInfo(Class<?> clazz, String autonumericCol, String userCol, String timeStampCol, String tabla, Map<String, String> columnsfields) {
		super(clazz, autonumericCol, userCol, timeStampCol, tabla, columnsfields);
	}

	@Override
	public boolean passFilter(UsoTarjeta bean) {
		return true;
	}

}
