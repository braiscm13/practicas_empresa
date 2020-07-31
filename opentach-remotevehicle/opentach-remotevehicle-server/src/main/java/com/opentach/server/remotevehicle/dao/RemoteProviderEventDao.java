package com.opentach.server.remotevehicle.dao;

import java.util.Map;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.opentach.common.remotevehicle.RemoteVehicleNaming;
import com.opentach.server.dao.AbstractOpentachJdbcDao;
import com.opentach.server.dao.autofill.AutoFillHelper.AUTOFILL;
import com.opentach.server.dao.autofill.IAutoFillDao;

@Repository(value = "RemoteProviderEventDao")
@Lazy
@ConfigurationFile(configurationFile = "remotevehicle-dao/RemoteProviderEventDao.xml", configurationFilePlaceholder = "remotevehicle-dao/placeholders.properties")
public class RemoteProviderEventDao extends AbstractOpentachJdbcDao implements IAutoFillDao {
	public RemoteProviderEventDao() {
		super();
	}

	@Override
	public Map<String, String> getColumnsAutoFillInsert() {
		Map<?, ?> kv = MapTools.keysvalues(//
				RemoteVehicleNaming.RPE_CREATION_DATE, AUTOFILL.GETCDATETIME.toString()//
				);
		return (Map<String, String>) kv;
	}

	@Override
	public Map<String, String> getColumnsAutoFillUpdate() {
		Map<?, ?> kv = MapTools.keysvalues(//
				);
		return (Map<String, String>) kv;
	}
}
