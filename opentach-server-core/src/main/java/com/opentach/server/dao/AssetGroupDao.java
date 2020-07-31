package com.opentach.server.dao;

import java.util.Map;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.opentach.common.company.naming.CompanyNaming;
import com.opentach.server.dao.autofill.AutoFillHelper.AUTOFILL;
import com.opentach.server.dao.autofill.IAutoFillDao;

@Repository(value = "AssetGroupDao")
@Lazy
@ConfigurationFile(configurationFile = "core-dao/AssetGroupDao.xml", configurationFilePlaceholder = "core-dao/placeholders.properties")
public class AssetGroupDao extends AbstractOpentachJdbcDao implements IAutoFillDao {
	public AssetGroupDao() {
		super();
	}

	@Override
	public Map<String, String> getColumnsAutoFillInsert() {
		Map<?, ?> kv = MapTools.keysvalues(//
				CompanyNaming.CAG_CREATION_USER, AUTOFILL.GETUSER.toString(), //
				CompanyNaming.CAG_CREATION_DATE, AUTOFILL.GETCDATETIME.toString()//
		);
		return (Map<String, String>) kv;
	}

	@Override
	public Map<String, String> getColumnsAutoFillUpdate() {
		Map<?, ?> kv = MapTools.keysvalues(//
		);
		return (Map<String, String>) kv;
	}
}
