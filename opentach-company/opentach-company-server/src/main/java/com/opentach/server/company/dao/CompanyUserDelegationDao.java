package com.opentach.server.company.dao;

import java.util.Map;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.opentach.common.company.naming.CompanyDelegationNaming;
import com.opentach.server.dao.AbstractOpentachJdbcDao;
import com.opentach.server.dao.autofill.AutoFillHelper.AUTOFILL;
import com.opentach.server.dao.autofill.IAutoFillDao;

@Repository(value = "CompanyUserDelegationDao")
@Lazy
@ConfigurationFile(configurationFile = "company-dao/CompanyUserDelegationDao.xml", configurationFilePlaceholder = "company-dao/placeholders.properties")
public class CompanyUserDelegationDao extends AbstractOpentachJdbcDao implements IAutoFillDao {

	public CompanyUserDelegationDao() {
		super();
	}

	@Override
	public Map<String, String> getColumnsAutoFillInsert() {
		Map<?, ?> kv = MapTools.keysvalues(//
				CompanyDelegationNaming.USUARIO_ALTA, AUTOFILL.GETUSER.toString(), //
				CompanyDelegationNaming.F_ALTA, AUTOFILL.GETCDATETIME.toString()//
				);
		return (Map<String, String>) kv;
	}

	@Override
	public Map<String, String> getColumnsAutoFillUpdate() {
		return null;
	}
}