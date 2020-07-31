package com.opentach.server.distributor.dao;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;

@Repository(value = "DistributorDao")
@Lazy
@ConfigurationFile(configurationFile = "distributor-dao/DistributorDao.xml", configurationFilePlaceholder = "distributor-dao/placeholders.properties")
public class DistributorDao extends OntimizeJdbcDaoSupport {

	public DistributorDao() {
		super();
	}
}