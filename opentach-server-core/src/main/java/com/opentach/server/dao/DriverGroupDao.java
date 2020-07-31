package com.opentach.server.dao;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.ontimize.jee.server.dao.common.ConfigurationFile;

@Repository(value = "DriverGroupDao")
@Lazy
@ConfigurationFile(configurationFile = "core-dao/DriverGroupDao.xml", configurationFilePlaceholder = "core-dao/placeholders.properties")
public class DriverGroupDao extends AbstractOpentachJdbcDao {
	public DriverGroupDao() {
		super();
	}
}
