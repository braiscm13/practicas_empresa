package com.opentach.server.indicator.dao;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;

@Repository(value = "IndicatorExecutionDao")
@Lazy
@ConfigurationFile(configurationFile = "indicator-dao/IndicatorExecutionDao.xml", configurationFilePlaceholder = "indicator-dao/placeholders.properties")
public class IndicatorExecutionDao extends OntimizeJdbcDaoSupport {

	public IndicatorExecutionDao() {
		super();
	}
}