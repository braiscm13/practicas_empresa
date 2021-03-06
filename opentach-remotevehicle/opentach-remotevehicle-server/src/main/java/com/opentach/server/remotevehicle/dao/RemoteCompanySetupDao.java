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

@Repository(value = "RemoteCompanySetupDao")
@Lazy
@ConfigurationFile(configurationFile = "remotevehicle-dao/RemoteCompanySetupDao.xml", configurationFilePlaceholder = "remotevehicle-dao/placeholders.properties")
public class RemoteCompanySetupDao extends AbstractOpentachJdbcDao implements IAutoFillDao {
	public RemoteCompanySetupDao() {
		super();
	}

	@Override
	public Map<String, String> getColumnsAutoFillInsert() {
		Map<?, ?> kv = MapTools.keysvalues(//
				RemoteVehicleNaming.CCF_CREATE_USER, AUTOFILL.GETUSER.toString(), //
				RemoteVehicleNaming.CCF_CREATE_DATE, AUTOFILL.GETCDATETIME.toString()//
				);
		return (Map<String, String>) kv;
	}

	@Override
	public Map<String, String> getColumnsAutoFillUpdate() {
		Map<?, ?> kv = MapTools.keysvalues(//
				RemoteVehicleNaming.CCF_UPDATE_USER, AUTOFILL.GETUSER.toString(), //
				RemoteVehicleNaming.CCF_UPDATE_DATE, AUTOFILL.GETCDATETIME.toString()//
				);
		return (Map<String, String>) kv;
	}
}
