package com.opentach.server.dao;

import java.util.Map;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.opentach.common.naming.UserNaming;
import com.opentach.server.dao.autofill.AutoFillHelper.AUTOFILL;
import com.opentach.server.dao.autofill.IAutoFillDao;

@Repository(value = "UserDao")
@Lazy
@ConfigurationFile(configurationFile = "core-dao/UserDao.xml", configurationFilePlaceholder = "core-dao/placeholders.properties")
public class UserDao extends AbstractOpentachJdbcDao implements IAutoFillDao {

	public UserDao() {
		super();
	}

	@Override
	public Map<String, String> getColumnsAutoFillInsert() {
		Map<?, ?> kv = MapTools.keysvalues(//
				UserNaming.F_ALTA, AUTOFILL.GETCDATETIME.toString()//
		);
		return (Map<String, String>) kv;
	}

	@Override
	public Map<String, String> getColumnsAutoFillUpdate() {
		return null;
	}
}
