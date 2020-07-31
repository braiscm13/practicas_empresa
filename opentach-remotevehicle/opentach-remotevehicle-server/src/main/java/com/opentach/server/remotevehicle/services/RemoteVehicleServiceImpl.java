package com.opentach.server.remotevehicle.services;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.SearchValue;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.DateTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.ObjectTools;
import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import com.ontimize.jee.server.dao.One2OneDaoHelper;
import com.ontimize.jee.server.dao.One2OneDaoHelper.One2OneType;
import com.ontimize.jee.server.dao.One2OneDaoHelper.OneToOneSubDao;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import com.opentach.common.remotevehicle.RemoteVehicleNaming;
import com.opentach.common.remotevehicle.exceptions.RemoteVehicleException;
import com.opentach.common.remotevehicle.provider.jaltest.StablishVehicleDownloadWarningJaltestException;
import com.opentach.common.remotevehicle.services.IRemoteVehicleManagementService;
import com.opentach.common.remotevehicle.services.IRemoteVehicleService;
import com.opentach.common.util.OpentachCheckingTools;
import com.opentach.server.remotevehicle.dao.RemoteCompanySetupDao;
import com.opentach.server.remotevehicle.dao.RemoteDownloadConfigDao;
import com.opentach.server.remotevehicle.dao.RemoteDownloadDriverConfigDao;
import com.opentach.server.remotevehicle.dao.RemoteDownloadVehicleConfigDao;
import com.opentach.server.remotevehicle.dao.RemoteLocationDao;
import com.opentach.server.remotevehicle.provider.IRemoteVehicleProvider;
import com.opentach.server.util.SecurityTools;
import com.opentach.server.util.UserInfoComponent;
import com.opentach.server.util.spring.AbstractSpringDelegate;

/**
 * The Class RemoteDownloadService.
 */
@Service("RemoteVehicleService")
public class RemoteVehicleServiceImpl extends AbstractSpringDelegate implements IRemoteVehicleService, InitializingBean {
	private static final String							ERROR_INVALID_REMOTEDOWNLOAD_CONFIGURATION	= "E_INVALID_REMOTEDOWNLOAD_CONFIGURATION";

	/** The Constant logger. */
	private static final Logger							logger										= LoggerFactory.getLogger(RemoteVehicleServiceImpl.class);

	/** The providers. */
	private final Map<Object, IRemoteVehicleProvider>	providers;

	@Autowired
	private UserInfoComponent							userInfo;

	@Autowired
	private IRemoteVehicleManagementService				remoteManagementService;
	@Autowired
	private RemoteCompanySetupDao						companySetupDao;

	@Autowired
	private RemoteDownloadConfigDao						downloadConfigDao;
	@Autowired
	private RemoteDownloadDriverConfigDao				downloadDriverConfigDao;
	@Autowired
	private RemoteDownloadVehicleConfigDao				downloadVehicleConfigDao;
	@Autowired
	private RemoteLocationDao							locationDao;
	@Autowired
	private DefaultOntimizeDaoHelper					daoHelper;
	@Autowired
	private One2OneDaoHelper							one2oneDaoHelper;
	@Autowired
	private ApplicationContext							context;

	/**
	 * Instantiates a new remote download service.
	 *
	 * @param port
	 *            the port
	 * @param locator
	 *            the locator
	 * @param params
	 *            the params
	 * @throws Exception
	 *             the exception
	 */
	public RemoteVehicleServiceImpl() throws Exception {
		super();
		this.providers = new HashMap<>();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.loadProviders();
	}

	/**
	 * Load providers.
	 *
	 * @throws Exception
	 *             the exception
	 */
	private void loadProviders() throws Exception {
		SecurityTools.establishAuthentication();
		EntityResult res = this.remoteManagementService.providerQuery(
				new HashMap<>(),
				Arrays.asList(RemoteVehicleNaming.RDP_ID, RemoteVehicleNaming.RDP_NAME, RemoteVehicleNaming.RDP_IMPL, RemoteVehicleNaming.RDP_SETUP));
		for (int i = 0, nrecord = res.calculateRecordNumber(); i < nrecord; i++) {
			Map<Object, Object> recordValues = res.getRecordValues(i);
			Object providerId = recordValues.get(RemoteVehicleNaming.RDP_ID);
			Object providerName = recordValues.get(RemoteVehicleNaming.RDP_NAME);
			String providerClazzName = (String) recordValues.get(RemoteVehicleNaming.RDP_IMPL);
			String providerSetup = (String) recordValues.get(RemoteVehicleNaming.RDP_SETUP);
			IRemoteVehicleProvider providerImpl = ParseUtilsExtended.getClazzInstance(providerClazzName, new Object[] {}, null);
			if (providerImpl == null) {
				RemoteVehicleServiceImpl.logger.error("Error instanciating remote provider {}/{} impl: {}", providerId, providerName, providerClazzName);
			} else {
				Properties prop = new Properties();
				if (providerSetup != null) {
					prop.load(new ByteArrayInputStream(providerSetup.getBytes()));
				}
				providerImpl.init(this.context, prop, providerId);
				RemoteVehicleServiceImpl.logger.info("Loaded provider {}/{} impl: {}", providerId, providerName, providerClazzName);
				this.providers.put(providerId, providerImpl);
			}
		}
	}

	/**
	 * Gets the remote download provider.
	 *
	 * @param providerId
	 *            the provider id
	 * @return the remote download provider
	 */
	public IRemoteVehicleProvider getRemoteDownloadProvider(Object providerId) {
		return this.providers.get(providerId);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public EntityResult createCompany(Map<String, Object> parameters, String companyId) throws RemoteVehicleException {
		this.userInfo.checkPermissions(companyId);
		// insertar en rdw_dfemp
		Map<String, Object> av = new HashMap<>(parameters);
		av.put(RemoteVehicleNaming.COM_ID, companyId);
		EntityResult res = this.daoHelper.insert(this.companySetupDao, av);
		Object ccfId = res.get(RemoteVehicleNaming.CCF_ID);
		// intentar el registro en el servidor
		try {
			EntityResult er = this.daoHelper.query(this.companySetupDao, EntityResultTools.keysvalues(RemoteVehicleNaming.CCF_ID, ccfId),
					Arrays.asList(RemoteVehicleNaming.CCF_ID, RemoteVehicleNaming.RDP_ID, RemoteVehicleNaming.COM_ID, RemoteVehicleNaming.CCF_STATUS), "default");
			CheckingTools.checkValidEntityResult(er, "E_MORE_THAN_ONE_RECORD", true, true, new Object[] {});
			this.registerCompany(er.getRecordValues(0));
		} catch (Exception error) {
			RemoteVehicleServiceImpl.logger.error(null, error);
			res.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
			res.setMessage(error.getMessage());
		}
		return res;
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public EntityResult updateCompany(Map<String, Object> parameters, Object registryId) throws RemoteVehicleException {
		// no se puede cambiar el cif
		parameters.remove("COM_ID");
		// consultar estado actual
		Hashtable<Object, Object> kv = EntityResultTools.keysvalues(RemoteVehicleNaming.CCF_ID, registryId);
		EntityResult oldState = this.daoHelper.query(this.companySetupDao, kv,
				Arrays.asList(RemoteVehicleNaming.CCF_ID, RemoteVehicleNaming.RDP_ID, RemoteVehicleNaming.COM_ID, RemoteVehicleNaming.CCF_STATUS));
		OpentachCheckingTools.checkValidEntityResult(oldState, RemoteVehicleException.class, "E_MORE_THAN_ONE_RECORD", true, true, new Object[] {});
		String companyId = (String) ((List) oldState.get(RemoteVehicleNaming.COM_ID)).get(0);
		this.userInfo.checkPermissions(companyId);
		// actualizar en rdw_dfemp
		Hashtable<String, Object> av = new Hashtable<>(parameters);

		EntityResult res = this.daoHelper.update(this.companySetupDao, av, kv);
		EntityResult newState = this.daoHelper.query(this.companySetupDao, kv,
				Arrays.asList(RemoteVehicleNaming.CCF_ID, RemoteVehicleNaming.RDP_ID, RemoteVehicleNaming.COM_ID, RemoteVehicleNaming.CCF_STATUS));
		OpentachCheckingTools.checkValidEntityResult(newState, RemoteVehicleException.class, "E_MORE_THAN_ONE_RECORD", true, true, new Object[] {});

		Object oldRdpId = ((List) oldState.get(RemoteVehicleNaming.RDP_ID)).get(0);
		Object newRdpId = ((List) newState.get(RemoteVehicleNaming.RDP_ID)).get(0);
		if (!ObjectTools.safeIsEquals(oldRdpId, newRdpId)) {
			this.daoHelper.update(this.companySetupDao, EntityResultTools.keysvalues(RemoteVehicleNaming.CCF_STATUS, "PE"), kv);
			newState = this.daoHelper.query(this.companySetupDao, kv,
					Arrays.asList(RemoteVehicleNaming.CCF_ID, RemoteVehicleNaming.RDP_ID, RemoteVehicleNaming.COM_ID, RemoteVehicleNaming.CCF_STATUS));
		}

		try {
			this.registerCompany(newState.getRecordValues(0));
		} catch (Exception error) {
			RemoteVehicleServiceImpl.logger.error(null, error);
			res.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
			res.setMessage(error.getMessage());
		}
		return res;
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public EntityResult configureVehicle(boolean active, Object providerId, RemoteDownloadPeriod period, Long hour, String companyId, String vehicleId, String unitSn)
			throws RemoteVehicleException {
		this.userInfo.checkPermissions(companyId);
		return this.configureSource(true, active, providerId, period, hour, companyId, vehicleId, unitSn);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public EntityResult configureDriver(boolean active, Object providerId, RemoteDownloadPeriod period, Long hour, String companyId, String driverId)
			throws RemoteVehicleException {
		this.userInfo.checkPermissions(companyId);
		return this.configureSource(false, active, providerId, period, hour, companyId, driverId, null);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public void requestForceDriverDownload(String companyId, List<String> driverIds) throws RemoteVehicleException {
		this.userInfo.checkPermissions(companyId);
		for (String driverId : driverIds) {
			this.requestForceSourceDownload(false, companyId, driverId, null, null);
		}
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public void requestForceVehicleDownload(String companyId, List<String> vehiclesIds, Date from, Date to) throws RemoteVehicleException {
		this.userInfo.checkPermissions(companyId);
		for (String vehicleId : vehiclesIds) {
			this.requestForceSourceDownload(true, companyId, vehicleId, from, to);
		}
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public boolean isRemoteDownloadActive() throws RemoteVehicleException {
		if (this.userInfo.isAdmin()) {
			return true;
		}
		Map<Object, Object> kv = EntityResultTools.keysvalues(//
				RemoteVehicleNaming.CCF_STATUS, "OK", //
				RemoteVehicleNaming.COM_ID, new SearchValue(SearchValue.IN, new Vector<>(this.userInfo.getCompaniesList()))//
				);
		EntityResult query = this.daoHelper.query(this.companySetupDao, kv, Arrays.asList(RemoteVehicleNaming.COM_ID));
		return query.calculateRecordNumber() > 0;
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public EntityResult queryVehicleLocations(String cif, String plate, Date from, Date to) throws RemoteVehicleException {
		CheckingTools.failIf(cif == null, "remotevehicle.E_CIF_MANDATORY");
		this.userInfo.checkPermissions(cif);
		CheckingTools.failIf(plate == null, "remotevehicle.E_PLATE_MANDATORY");
		CheckingTools.failIf(DateTools.countDaysBetween(from, to) > IRemoteVehicleService.MAX_DAYS, "remotevehicle.E_TOO_MANY_DAYS");

		Map<Object, Object> kv = EntityResultTools.keysvalues(//
				RemoteVehicleNaming.COM_ID, cif, //
				RemoteVehicleNaming.VEH_ID, plate, //
				RemoteVehicleNaming.LOC_DATE, new SearchValue(SearchValue.BETWEEN, new Vector<>(Arrays.asList(new Object[] { from, to }))));
		return this.daoHelper.query(this.locationDao, kv,
				Arrays.asList(RemoteVehicleNaming.LOC_ID, RemoteVehicleNaming.LOC_LONGITUDE, RemoteVehicleNaming.LOC_LATITUDE, RemoteVehicleNaming.LOC_DATE),
				Arrays.asList(RemoteVehicleNaming.LOC_DATE));
	}

	private void registerCompany(Map<String, Object> parameters) throws Exception {
		Object ccfId = parameters.get(RemoteVehicleNaming.CCF_ID);
		Object rdpId = parameters.get(RemoteVehicleNaming.RDP_ID);
		String comId = (String) parameters.get(RemoteVehicleNaming.COM_ID);
		String ccfStatus = (String) parameters.get(RemoteVehicleNaming.CCF_STATUS);

		if (rdpId == null) {
			RemoteVehicleServiceImpl.logger.warn("No existe RDP_ID para {}", ccfId);
			return;
		}
		if (comId == null) {
			RemoteVehicleServiceImpl.logger.warn("No existe COM_ID para {}", ccfId);
			return;
		}
		if ("OK".equals(ccfStatus)) {
			RemoteVehicleServiceImpl.logger.debug("Ya hay registro para {}", ccfId);
			return;
		}
		this.getRemoteDownloadProvider(rdpId).createCompany(comId);
		Hashtable<Object, Object> avUp = EntityResultTools.keysvalues(RemoteVehicleNaming.CCF_STATUS, "OK");
		Hashtable<Object, Object> kvUp = EntityResultTools.keysvalues(RemoteVehicleNaming.CCF_ID, ccfId);
		this.daoHelper.update(this.companySetupDao, avUp, kvUp);
	}

	private EntityResult configureSource(boolean vehicle, boolean active, Object providerId, RemoteDownloadPeriod period, Long hour, String companyId, Object srcId,
			String vehicleUnitSn) {
		EntityResult res;
		try {
			if (vehicle) {
				this.userInfo.checkVehiclePermissions(companyId, (String) srcId);
			} else {
				this.userInfo.checkDriverPermissions(companyId, (String) srcId);
			}

			OntimizeJdbcDaoSupport dao = vehicle ? this.downloadVehicleConfigDao : this.downloadDriverConfigDao;
			EntityResult erOld = dao.query(
					EntityResultTools.keysvalues(vehicle ? RemoteVehicleNaming.VEH_ID : RemoteVehicleNaming.DRV_ID, srcId, RemoteVehicleNaming.COM_ID, companyId),
					EntityResultTools.attributes(RemoteVehicleNaming.RDW_ID, RemoteVehicleNaming.RDW_ACTIVE, RemoteVehicleNaming.RDP_ID), null, "default");
			int nregs = erOld.calculateRecordNumber();
			Object rdwId = null;
			if (nregs == 0) {
				// insertar
				Map<Object, Object> attributes = EntityResultTools.keysvalues( //
						RemoteVehicleNaming.RDW_ACTIVE, active ? "S" : "N", //
								RemoteVehicleNaming.RDP_ID, providerId, //
								RemoteVehicleNaming.RDW_PERIOD, period.toString(), //
								RemoteVehicleNaming.RDW_HOUR, hour, //
								vehicle ? RemoteVehicleNaming.RDV_ID : RemoteVehicleNaming.RDD_ID, rdwId, //
										vehicle ? RemoteVehicleNaming.VEH_ID : RemoteVehicleNaming.DRV_ID, srcId, //
												RemoteVehicleNaming.COM_ID, companyId, // 1
												RemoteVehicleNaming.RDV_UNIT_SN, vehicleUnitSn //
						);
				res = this.one2oneDaoHelper.insert(this.daoHelper, this.downloadConfigDao,
						Arrays.asList(new OneToOneSubDao(dao, vehicle ? RemoteVehicleNaming.RDV_ID : RemoteVehicleNaming.RDD_ID)), attributes, One2OneType.DIRECT);
			} else if (nregs == 1) {
				// actualizar
				Map<Object, Object> recordValues = erOld.getRecordValues(0);
				Map<Object, Object> attributes = EntityResultTools.keysvalues( //
						RemoteVehicleNaming.RDW_ACTIVE, active ? "S" : "N", //
								RemoteVehicleNaming.RDP_ID, providerId, //
								RemoteVehicleNaming.RDW_PERIOD, //
								period.toString(), //
								RemoteVehicleNaming.RDW_HOUR, hour, //
								RemoteVehicleNaming.RDV_UNIT_SN, vehicleUnitSn //
						);
				// TODO ver si hay que desregistrarse del provider anterior en caso de cambio
				rdwId = recordValues.get("RDW_ID");
				res = this.one2oneDaoHelper.update(this.daoHelper,
						this.downloadConfigDao,
						Arrays.asList(new OneToOneSubDao(dao, vehicle ? RemoteVehicleNaming.RDV_ID : RemoteVehicleNaming.RDD_ID)), attributes,
						EntityResultTools.keysvalues(RemoteVehicleNaming.RDW_ID, rdwId, vehicle ? RemoteVehicleNaming.RDV_ID : RemoteVehicleNaming.RDD_ID, rdwId),
						One2OneType.DIRECT);
			} else {
				throw new RemoteVehicleException();
			}
			if (providerId != null) {
				if (vehicle) {
					// TODO consultar el tipo de vehiculo
					try {
						this.getRemoteDownloadProvider(providerId).configureVehicle(active, period, hour, companyId, (String) srcId, vehicleUnitSn, "M");
					} catch (StablishVehicleDownloadWarningJaltestException err) {
						RemoteVehicleServiceImpl.logger.warn(null, err);
						res.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
						res.setMessage("W_SETTING_DOWNLOAD_PERIOD");
					}
				} else {
					this.getRemoteDownloadProvider(providerId).configureDriver(active, period, hour, companyId, (String) srcId);
				}
			}

		} catch (Exception error) {
			throw new RuntimeException(error.getMessage(), error);
		}
		return res;
	}

	private void requestForceSourceDownload(boolean vehicle, String companyId, String srcId, Date from, Date to) throws RemoteVehicleException {
		if (vehicle) {
			this.userInfo.checkVehiclePermissions(companyId, srcId);
		} else {
			this.userInfo.checkDriverPermissions(companyId, srcId);
		}

		OntimizeJdbcDaoSupport dao = vehicle ? this.downloadVehicleConfigDao : this.downloadDriverConfigDao;
		EntityResult erOld = dao.query(EntityResultTools.keysvalues(vehicle ? RemoteVehicleNaming.VEH_ID : RemoteVehicleNaming.DRV_ID, srcId, RemoteVehicleNaming.COM_ID, companyId,
				RemoteVehicleNaming.RDW_ACTIVE, "S", RemoteVehicleNaming.RDV_ID, new SearchValue(SearchValue.NOT_NULL,
						null)),
				EntityResultTools.attributes(RemoteVehicleNaming.RDW_ID, RemoteVehicleNaming.RDW_ACTIVE, RemoteVehicleNaming.RDP_ID), null, "default");

		OpentachCheckingTools.checkValidEntityResult(erOld, RemoteVehicleException.class, RemoteVehicleServiceImpl.ERROR_INVALID_REMOTEDOWNLOAD_CONFIGURATION, true, true,
				new Object[] { srcId });
		Object providerId = erOld.getRecordValues(0).get(RemoteVehicleNaming.RDP_ID);
		if (vehicle) {
			this.getRemoteDownloadProvider(providerId).requestForceVehicleDownload(companyId, srcId, from, to);
		} else {
			this.getRemoteDownloadProvider(providerId).requestForceDriverDownload(companyId, srcId);
		}
	}

}