package com.opentach.server.dao;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;

@Repository(value = "RoleDao")
@Lazy
@ConfigurationFile(configurationFile = "core-dao/RoleDao.xml", configurationFilePlaceholder = "core-dao/placeholders.properties")
public class RoleDao extends OntimizeJdbcDaoSupport {

	public RoleDao() {
		super();
	}
}