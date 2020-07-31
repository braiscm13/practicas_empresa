package com.opentach.server.process.entityInfo;

import java.util.Map;

import com.opentach.common.tacho.data.CompanyLocksDataVU;

public class CompanyLocksEntityInfo extends AbstractClassEntityInfo<CompanyLocksDataVU> {

	public CompanyLocksEntityInfo(Class<?> clazz, String autonumericCol, String userCol, String timeStampCol, String tabla, Map<String, String> columnsfields) {
		super(clazz, autonumericCol, userCol, timeStampCol, tabla, columnsfields);
	}

	@Override
	public boolean passFilter(CompanyLocksDataVU bean) {
		return true;
	}

}
