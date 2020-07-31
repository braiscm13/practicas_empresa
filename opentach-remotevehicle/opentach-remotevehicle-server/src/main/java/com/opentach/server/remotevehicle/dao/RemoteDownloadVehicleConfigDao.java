package com.opentach.server.remotevehicle.dao;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.opentach.server.dao.AbstractOpentachJdbcDao;

@Repository(value = "RemoteDownloadVehicleConfigDao")
@Lazy
@ConfigurationFile(configurationFile = "remotevehicle-dao/RemoteDownloadVehicleConfigDao.xml", configurationFilePlaceholder = "remotevehicle-dao/placeholders.properties")
public class RemoteDownloadVehicleConfigDao extends AbstractOpentachJdbcDao {
	public RemoteDownloadVehicleConfigDao() {
		super();
	}


}
