package com.opentach.server.remotevehicle.services;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.SearchValue;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.remotevehicle.exceptions.RemoteVehicleException;
import com.opentach.common.remotevehicle.services.IRemoteVehicleManagementService;
import com.opentach.server.remotevehicle.dao.RemoteCompanyResponsableDao;
import com.opentach.server.remotevehicle.dao.RemoteCompanySetupDao;
import com.opentach.server.remotevehicle.dao.RemoteDownloadConfigDao;
import com.opentach.server.remotevehicle.dao.RemoteDownloadDriverConfigDao;
import com.opentach.server.remotevehicle.dao.RemoteDownloadVehicleConfigDao;
import com.opentach.server.remotevehicle.dao.RemoteProviderDao;
import com.opentach.server.util.UserInfoComponent;
import com.opentach.server.util.spring.AbstractSpringDelegate;

@Service("RemoteVehicleManagementService")
public class RemoteVehicleManagementServiceImpl extends AbstractSpringDelegate implements IRemoteVehicleManagementService {

	@Autowired
	private DefaultOntimizeDaoHelper		daoHelper;
	@Autowired
	private RemoteCompanyResponsableDao		companyResponsableDao;
	@Autowired
	private RemoteCompanySetupDao			companySetupDao;
	@Autowired
	private RemoteDownloadConfigDao			downloadConfigDao;
	@Autowired
	private RemoteDownloadDriverConfigDao	downloadDriverConfigDao;
	@Autowired
	private RemoteDownloadVehicleConfigDao	downloadVehicleConfigDao;
	@Autowired
	private RemoteProviderDao				providerDao;
	@Autowired
	private UserInfoComponent				userInfo;

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public EntityResult downloadConfigAllQuery(Map<?, ?> keysValues, List<?> attributes) throws RemoteVehicleException {
		if (!((Map<Object, Object>) keysValues).containsKey(OpentachFieldNames.CIF_FIELD) && !this.userInfo.isAdmin()) {
			((Map<Object, Object>) keysValues).put(OpentachFieldNames.CIF_FIELD, new SearchValue(SearchValue.IN, this.userInfo.getCompaniesList()));
		}
		attributes.removeAll(Arrays.asList("BUTTON_FORCE_DOWNLOAD", "BUTTON_SEE_DOWNLOADS"));
		return this.daoHelper.query(this.downloadConfigDao, keysValues, attributes);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public EntityResult downloadDriverConfigQuery(Map<?, ?> keysValues, List<?> attributes) throws RemoteVehicleException {
		return this.daoHelper.query(this.downloadDriverConfigDao, keysValues, attributes);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public EntityResult downloadVehicleConfigQuery(Map<?, ?> keysValues, List<?> attributes) throws RemoteVehicleException {
		this.userInfo.checkPermissions((String) keysValues.get(OpentachFieldNames.CIF_FIELD));
		return this.daoHelper.query(this.downloadVehicleConfigDao, keysValues, attributes);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public EntityResult providerQuery(Map<?, ?> keysValues, List<?> attributes) throws RemoteVehicleException {
		return this.daoHelper.query(this.providerDao, keysValues, attributes);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public EntityResult companySetupQuery(Map<?, ?> keysValues, List<?> attributes) throws RemoteVehicleException {
		return this.daoHelper.query(this.companySetupDao, keysValues, attributes);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public EntityResult companyResponsableQuery(Map<?, ?> keysValues, List<?> attributes) throws RemoteVehicleException {
		return this.daoHelper.query(this.companyResponsableDao, keysValues, attributes);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public EntityResult companyResponsableInsert(Map<?, ?> attributes) throws RemoteVehicleException {
		return this.daoHelper.insert(this.companyResponsableDao, attributes);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public EntityResult companyResponsableUpdate(Map<?, ?> attributes, Map<?, ?> keysValues) throws RemoteVehicleException {
		return this.daoHelper.update(this.companyResponsableDao, attributes, keysValues);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public EntityResult companyResponsableDelete(Map<?, ?> keysValues) throws RemoteVehicleException {
		return this.daoHelper.delete(this.companyResponsableDao, keysValues);
	}
}
