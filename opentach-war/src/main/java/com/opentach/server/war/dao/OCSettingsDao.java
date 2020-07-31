package com.opentach.server.war.dao;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;

@Repository(value = "OCSettingsDao")
@Lazy
@ConfigurationFile(configurationFile = "setup-dao/OCSettingsDao.xml", configurationFilePlaceholder = "setup-dao/placeholders.properties")
public class OCSettingsDao extends OntimizeJdbcDaoSupport {

	public OCSettingsDao() {
		super();
	}
}
