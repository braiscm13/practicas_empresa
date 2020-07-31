package com.opentach.server.dao;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;

@Repository(value = "ServerRoleDao")
@Lazy
@ConfigurationFile(configurationFile = "core-dao/ServerRoleDao.xml", configurationFilePlaceholder = "core-dao/placeholders.properties")
public class ServerRoleDao extends OntimizeJdbcDaoSupport {

	public ServerRoleDao() {
		super();
	}
}
