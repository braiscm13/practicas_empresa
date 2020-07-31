package com.opentach.server.mailmanager.dao;


import java.util.Map;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.opentach.common.mailmanager.MailManagerNaming;
import com.opentach.server.dao.AbstractOpentachJdbcDao;
import com.opentach.server.dao.autofill.AutoFillHelper.AUTOFILL;
import com.opentach.server.dao.autofill.IAutoFillDao;
@Repository(value = "MailMailDao")
@Lazy
@ConfigurationFile(configurationFile = "mailmanager-dao/MailMailDao.xml", configurationFilePlaceholder = "mailmanager-dao/placeholders.properties")
public class MailMailDao extends AbstractOpentachJdbcDao implements IAutoFillDao {
	public MailMailDao() {
		super();
	}

	@Override
	public Map<String, String> getColumnsAutoFillInsert() {
		Map<?, ?> kv = MapTools.keysvalues(//
				MailManagerNaming.MAI_CREATION_DATE, AUTOFILL.GETCDATETIME.toString()//
		);
		return (Map<String, String>) kv;
	}

	@Override
	public Map<String, String> getColumnsAutoFillUpdate() {
		return null;
	}

}
