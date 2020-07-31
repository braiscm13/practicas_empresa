package com.opentach.server.alert.dao;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;

@Repository(value = "AlertExecutionDao")
@Lazy
@ConfigurationFile(configurationFile = "alert-dao/AlertExecutionDao.xml", configurationFilePlaceholder = "indicator-dao/placeholders.properties")
public class AlertExecutionDao extends OntimizeJdbcDaoSupport {

	public AlertExecutionDao() {
		super();
	}
}