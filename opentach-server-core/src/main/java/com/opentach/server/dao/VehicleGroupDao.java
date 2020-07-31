package com.opentach.server.dao;


import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
@Repository(value = "VehicleGroupDao")
@Lazy
@ConfigurationFile(configurationFile = "core-dao/VehicleGroupDao.xml", configurationFilePlaceholder = "core-dao/placeholders.properties")
public class VehicleGroupDao extends AbstractOpentachJdbcDao {
	public VehicleGroupDao() {
		super();
	}
}
