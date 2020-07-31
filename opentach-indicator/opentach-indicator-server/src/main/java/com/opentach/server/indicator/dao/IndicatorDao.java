package com.opentach.server.indicator.dao;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;

@Repository(value = "IndicatorDao")
@Lazy
@ConfigurationFile(configurationFile = "indicator-dao/IndicatorDao.xml", configurationFilePlaceholder = "indicator-dao/placeholders.properties")
public class IndicatorDao extends OntimizeJdbcDaoSupport {

	public IndicatorDao() {
		super();
	}
}