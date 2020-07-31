package com.opentach.server.war.dao;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;

@Repository(value = "OCLoginProfilesDao")
@Lazy
@ConfigurationFile(configurationFile = "setup-dao/OCLoginProfilesDao.xml", configurationFilePlaceholder = "setup-dao/placeholders.properties")
public class OCLoginProfilesDao extends OntimizeJdbcDaoSupport {
	public OCLoginProfilesDao() {
		super();
	}
}
