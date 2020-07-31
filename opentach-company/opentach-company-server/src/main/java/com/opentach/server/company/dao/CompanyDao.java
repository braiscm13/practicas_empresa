package com.opentach.server.company.dao;

import java.util.Map;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.opentach.common.company.naming.CompanyNaming;
import com.opentach.server.dao.AbstractOpentachJdbcDao;
import com.opentach.server.dao.autofill.AutoFillHelper.AUTOFILL;
import com.opentach.server.dao.autofill.IAutoFillDao;

@Repository(value = "CompanyDao")
@Lazy
@ConfigurationFile(configurationFile = "company-dao/CompanyDao.xml", configurationFilePlaceholder = "company-dao/placeholders.properties")
public class CompanyDao extends AbstractOpentachJdbcDao implements IAutoFillDao {

	public CompanyDao() {
		super();
	}

	@Override
	public Map<String, String> getColumnsAutoFillInsert() {
		Map<?, ?> kv = MapTools.keysvalues(//
				CompanyNaming.USUARIO_ALTA, AUTOFILL.GETUSER.toString(), //
				CompanyNaming.F_ALTA, AUTOFILL.GETCDATETIME.toString()//
		);
		return (Map<String, String>) kv;
	}

	@Override
	public Map<String, String> getColumnsAutoFillUpdate() {
		Map<?, ?> kv = MapTools.keysvalues(//
				CompanyNaming.USUARIO_MODIF, AUTOFILL.GETUSER.toString(), //
				CompanyNaming.F_MODIF, AUTOFILL.GETCDATETIME.toString()//
		);
		return (Map<String, String>) kv;
	}
}