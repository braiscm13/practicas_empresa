package com.opentach.server.company.dao;

import java.util.Map;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.opentach.common.company.naming.CompanyNaming;
import com.opentach.common.company.naming.CompanyUserNaming;
import com.opentach.server.dao.AbstractOpentachJdbcDao;
import com.opentach.server.dao.autofill.AutoFillHelper.AUTOFILL;
import com.opentach.server.dao.autofill.IAutoFillDao;

@Repository(value = "CompanyUserDao")
@Lazy
@ConfigurationFile(configurationFile = "company-dao/CompanyUserDao.xml", configurationFilePlaceholder = "company-dao/placeholders.properties")
public class CompanyUserDao extends AbstractOpentachJdbcDao implements IAutoFillDao {

	public CompanyUserDao() {
		super();
	}

	@Override
	public Map<String, String> getColumnsAutoFillInsert() {
		Map<?, ?> kv = MapTools.keysvalues(//
				CompanyUserNaming.USUARIO_ALTA, AUTOFILL.GETUSER.toString(), //
				CompanyNaming.F_ALTA, AUTOFILL.GETCDATETIME.toString()//
				);
		return (Map<String, String>) kv;
	}

	@Override
	public Map<String, String> getColumnsAutoFillUpdate() {
		return null;
	}
}