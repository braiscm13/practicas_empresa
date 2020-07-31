package com.opentach.server.remotevehicle.dao;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.opentach.server.dao.AbstractOpentachJdbcDao;

@Repository(value = "RemoteProviderDao")
@Lazy
@ConfigurationFile(configurationFile = "remotevehicle-dao/RemoteProviderDao.xml", configurationFilePlaceholder = "remotevehicle-dao/placeholders.properties")
public class RemoteProviderDao extends AbstractOpentachJdbcDao {
	public RemoteProviderDao() {
		super();
	}

}
