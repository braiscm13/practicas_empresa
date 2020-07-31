package com.opentach.server.services;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import com.opentach.common.companies.IAssetGroupService;
import com.opentach.common.company.naming.CompanyNaming;
import com.opentach.common.exception.OpentachException;
import com.opentach.server.dao.AssetGroupDao;
import com.opentach.server.dao.DriverGroupDao;
import com.opentach.server.dao.VehicleGroupDao;
import com.opentach.server.util.UserInfoComponent;

@Service("AssetGroupService")
public class AssetGroupServiceImpl implements IAssetGroupService {

	@Autowired
	private DefaultOntimizeDaoHelper	daoHelper;
	@Autowired
	private AssetGroupDao				assetGroupDao;
	@Autowired
	private VehicleGroupDao				vehicleGroupDao;
	@Autowired
	private DriverGroupDao				driverGroupDao;
	@Autowired
	private UserInfoComponent			userInfo;

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public EntityResult assetGroupQuery(Map<?, ?> keysValues, List<?> attributes) {
		this.userInfo.checkPermissions((String) keysValues.get(CompanyNaming.COM_ID));
		return this.daoHelper.query(this.assetGroupDao, keysValues, attributes);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public EntityResult assetGroupUpdate(Map<?, ?> attributesValues, Map<?, ?> keysValues) {
		this.userInfo.checkPermissions((String) keysValues.get(CompanyNaming.COM_ID));
		return this.daoHelper.update(this.assetGroupDao, attributesValues, keysValues);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public EntityResult assetGroupInsert(Map<?, ?> attributesValues) {
		this.userInfo.checkPermissions((String) attributesValues.get(CompanyNaming.COM_ID));
		return this.daoHelper.insert(this.assetGroupDao, attributesValues);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public EntityResult assetGroupDelete(Map<?, ?> keysValues) {
		this.userInfo.checkPermissions((String) keysValues.get(CompanyNaming.COM_ID));
		return this.daoHelper.delete(this.assetGroupDao, keysValues);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public EntityResult vehicleGroupQuery(Map<?, ?> keysValues, List<?> attributes) {
		this.userInfo.checkPermissions((String) keysValues.get(CompanyNaming.COM_ID));
		return this.daoHelper.query(this.vehicleGroupDao, keysValues, attributes, Arrays.asList(CompanyNaming.VEH_ID));
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public EntityResult vehicleGroupUpdate(Map<?, ?> attributesValues, Map<?, ?> keysValues) {
		this.userInfo.checkPermissions((String) keysValues.get(CompanyNaming.COM_ID));
		return this.daoHelper.update(this.vehicleGroupDao, attributesValues, keysValues);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public EntityResult vehicleGroupInsert(Map<?, ?> attributesValues) {
		this.userInfo.checkPermissions((String) attributesValues.get(CompanyNaming.COM_ID));
		Object vehId = attributesValues.get(CompanyNaming.VEH_ID);
		if (vehId instanceof List) {
			return this.vehicleGroupInsertBatch(attributesValues);
		}
		return this.daoHelper.insert(this.vehicleGroupDao, attributesValues);
	}

	private EntityResult vehicleGroupInsertBatch(Map<?, ?> attributesValues) {
		List<Object> vehIdList = (List) attributesValues.get(CompanyNaming.VEH_ID);
		Map<String, Object>[] batch = new Map[vehIdList.size()];
		for (int i = 0; i < batch.length; i++) {
			Map<String, Object> record = new HashMap<>();
			record.putAll((Map<String, Object>) attributesValues);
			record.put(CompanyNaming.VEH_ID, vehIdList.get(i));
			batch[i] = record;
		}
		this.vehicleGroupDao.insertBatch(batch);
		return new EntityResult();
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public EntityResult vehicleGroupDelete(Map<?, ?> keysValues) {
		this.userInfo.checkPermissions((String) keysValues.get(CompanyNaming.COM_ID));
		return this.daoHelper.delete(this.vehicleGroupDao, keysValues);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public EntityResult driverGroupQuery(Map<?, ?> keysValues, List<?> attributes) {
		this.userInfo.checkPermissions((String) keysValues.get(CompanyNaming.COM_ID));
		return this.daoHelper.query(this.driverGroupDao, keysValues, attributes, Arrays.asList(CompanyNaming.APELLIDOS), "detail");
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public EntityResult driverGroupUpdate(Map<?, ?> attributesValues, Map<?, ?> keysValues) {
		this.userInfo.checkPermissions((String) keysValues.get(CompanyNaming.COM_ID));
		return this.daoHelper.update(this.driverGroupDao, attributesValues, keysValues);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public EntityResult driverGroupInsert(Map<?, ?> attributesValues) {
		this.userInfo.checkPermissions((String) attributesValues.get(CompanyNaming.COM_ID));
		Object drvId = attributesValues.get(CompanyNaming.DRV_ID);
		if (drvId instanceof List) {
			return this.driverGroupInsertBatch(attributesValues);
		}
		return this.daoHelper.insert(this.driverGroupDao, attributesValues);
	}

	private EntityResult driverGroupInsertBatch(Map<?, ?> attributesValues) {
		List<Object> drvIdList = (List) attributesValues.get(CompanyNaming.DRV_ID);
		Map<String, Object>[] batch = new Map[drvIdList.size()];
		for (int i = 0; i < batch.length; i++) {
			Map<String, Object> record = new HashMap<>();
			record.putAll((Map<String, Object>) attributesValues);
			record.put(CompanyNaming.DRV_ID, drvIdList.get(i));
			batch[i] = record;
		}
		this.driverGroupDao.insertBatch(batch);
		return new EntityResult();
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public EntityResult driverGroupDelete(Map<?, ?> keysValues) {
		this.userInfo.checkPermissions((String) keysValues.get(CompanyNaming.COM_ID));
		return this.daoHelper.delete(this.driverGroupDao, keysValues);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public EntityResult availableVehicleGroupQuery(Map<?, ?> keysValues, List<?> attributes) throws OpentachException {
		this.userInfo.checkPermissions((String) keysValues.get(CompanyNaming.COM_ID));
		return this.daoHelper.query(this.vehicleGroupDao, keysValues, attributes, "available");
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public EntityResult availableDriverGroupQuery(Map<?, ?> keysValues, List<?> attributes) throws OpentachException {
		this.userInfo.checkPermissions((String) keysValues.get(CompanyNaming.COM_ID));
		return this.daoHelper.query(this.driverGroupDao, keysValues, attributes, "available");
	}
}
