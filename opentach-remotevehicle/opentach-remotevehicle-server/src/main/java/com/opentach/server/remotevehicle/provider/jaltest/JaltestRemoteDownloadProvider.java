package com.opentach.server.remotevehicle.provider.jaltest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.ontimize.db.EntityResult;
import com.ontimize.db.TransactionalEntity;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.StringTools;
import com.opentach.common.company.naming.CompanyNaming;
import com.opentach.common.remotevehicle.RemoteVehicleNaming;
import com.opentach.common.remotevehicle.exceptions.RemoteVehicleException;
import com.opentach.common.remotevehicle.provider.jaltest.StablishVehicleDownloadWarningJaltestException;
import com.opentach.common.remotevehicle.services.IRemoteVehicleService.RemoteDownloadPeriod;
import com.opentach.common.util.OpentachCheckingTools;
import com.opentach.server.remotevehicle.dao.RemoteCompanySetupDao;
import com.opentach.server.remotevehicle.provider.IRemoteVehicleProvider;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.JaltestInvoker;
import com.opentach.server.util.AbstractDelegate;
import com.opentach.server.util.spring.ILocatorReferencer;

public class JaltestRemoteDownloadProvider extends AbstractDelegate implements IRemoteVehicleProvider {

	private JaltestInvoker				invoker;
	private Object						providerId;
	private JaltestRemoteLocationWorker	locationWorker;
	private ApplicationContext			context;

	public JaltestRemoteDownloadProvider() {
		super(null);

	}

	@Override
	public void init(ApplicationContext context, Properties prop, Object providerId) {
		this.invoker = new JaltestInvoker(prop.getProperty("endpoint"), prop.getProperty("secretKey"), prop.getProperty("apiKey"));
		this.setLocator(context.getBean(ILocatorReferencer.class).getLocator());
		this.providerId = providerId;
		this.context = context;
		this.locationWorker = new JaltestRemoteLocationWorker(this, context);
	}

	public Object getProviderId() {
		return this.providerId;
	}

	public JaltestInvoker getInvoker() {
		return this.invoker;
	}

	@Override
	public void createCompany(String companyId) throws RemoteVehicleException {
		try {
			Connection con = DataSourceUtils.getConnection(this.context.getBean(DataSource.class));
			// query company/contract info
			TransactionalEntity eDfEmp = this.getEntity(CompanyNaming.ENTITY);
			Hashtable<Object, Object> kv = new Hashtable<>();
			kv.put("CIF", companyId);
			Vector<String> av = EntityResultTools.attributes("NOMB", "CG_PROV", "CG_MUNI", "CG_POBL", "CG_POSTAL", "TELF", "EMAIL");
			EntityResult res = eDfEmp.query(kv, av, this.getEntityPrivilegedId(eDfEmp), con);
			OpentachCheckingTools.checkValidEntityResult(res, RemoteVehicleException.class, "E_MORE_THAN_ONE_RECORD", true, true, new Object[] {});
			String name = (String) ((List) res.get("NOMB")).get(0);
			String cgProv = (String) ((List) res.get("CG_PROV")).get(0);
			String cgPostal = (String) ((List) res.get("CG_POSTAL")).get(0);
			String address = StringTools.concat(cgProv, cgPostal);
			String phone = (String) ((List) res.get("TELF")).get(0);
			String email = (String) ((List) res.get("EMAIL")).get(0);

			TransactionalEntity eEmpreREq = this.getEntity("EEmpreReq");
			av = EntityResultTools.attributes("NUMREQ", "F_CONTRATO", "FECFIND", "MAX_VEH");
			res = eEmpreREq.query(kv, av, this.getEntityPrivilegedId(eEmpreREq), con);
			OpentachCheckingTools.checkValidEntityResult(res, RemoteVehicleException.class, "E_MORE_THAN_ONE_RECORD", true, true, new Object[] {});
			String contractCode = (String) ((List) res.get("NUMREQ")).get(0);
			Date contractCreationDate = (Date) ((List) res.get("F_CONTRATO")).get(0);
			Date contractExpirationDate = (Date) ((List) res.get("FECFIND")).get(0);
			String contractHolder = name;

			this.invoker.createFleet(name, companyId, address, phone, email, contractCode, contractCreationDate, contractExpirationDate, contractHolder);
			// Si se crea bien hay que emparejar la tarjeta de empresa
			String generateCompanyKeys = this.invoker.generateCompanyKeys(companyId);

			RemoteCompanySetupDao companyDao = this.context.getBean(RemoteCompanySetupDao.class);
			kv = EntityResultTools.keysvalues("COM_ID", companyId, "RDP_ID", this.providerId);
			av = EntityResultTools.attributes("CCF_ID", "CCF_EXTRA_INFO");
			res = companyDao.query(kv, av, null, "default");
			OpentachCheckingTools.checkValidEntityResult(res, RemoteVehicleException.class, "E_MORE_THAN_ONE_RECORD", true, true, new Object[] {});
			String ccfExtraInfo = (String) ((List) res.get("CCF_EXTRA_INFO")).get(0);
			Object ccfId = ((List) res.get("CCF_ID")).get(0);
			Properties prop = new Properties();
			if (ccfExtraInfo != null) {
				prop.load(new ByteArrayInputStream(ccfExtraInfo.getBytes()));
			}
			if (generateCompanyKeys != null) {
				prop.setProperty("companyKey", String.valueOf(generateCompanyKeys));
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			prop.store(baos, "");
			ccfExtraInfo = new String(baos.toByteArray());
			companyDao.update(EntityResultTools.keysvalues(RemoteVehicleNaming.CCF_EXTRA_INFO, ccfExtraInfo), EntityResultTools.keysvalues(RemoteVehicleNaming.CCF_ID, ccfId));
		} catch (RemoteVehicleException err) {
			throw err;
		} catch (Exception err) {
			throw new RemoteVehicleException(err);
		}
	}

	@Override
	public void configureVehicle(boolean active, RemoteDownloadPeriod period, long hour, String companyId, String vehicleId, String vehicleUnitSn, String vehicleType)
			throws RemoteVehicleException {
		this.invoker.createVehicle(companyId, vehicleType, vehicleId);
		this.invoker.setupVehicleTelematicUnit(companyId, vehicleUnitSn, vehicleId);
		try {
			this.invoker.stablishVehicleDownloadPeriod(companyId, vehicleId, true);
		} catch (Exception err) {
			throw new StablishVehicleDownloadWarningJaltestException(err);
		}

	}

	@Override
	public void configureDriver(boolean active, RemoteDownloadPeriod period, long hour, String companyId, String driverId) throws RemoteVehicleException {
		try {
			Connection con = DataSourceUtils.getConnection(this.context.getBean(DataSource.class));
			TransactionalEntity eCond = this.getEntity("EConductoresEmp");
			EntityResult er = eCond.query(EntityResultTools.keysvalues("CIF", companyId, "IDCONDUCTOR", driverId),
					EntityResultTools.attributes("DNI", "NOMBRE", "APELLIDOS", "EXPIRED_DATE_TRJCONDU"), this.getEntityPrivilegedId(eCond), con);
			OpentachCheckingTools.checkValidEntityResult(er, RemoteVehicleException.class, "E_ONLY_ONE_RECORD", true, true, new Object[] {});
			Map<Object, Object> record = er.getRecordValues(0);
			this.invoker.createDriver(companyId, (String) record.get("DNI"), (String) record.get("NOMBRE"), (String) record.get("APELLIDOS"), null, driverId,
					(Date) record.get("EXPIRED_DATE_TRJCONDU"));
		} catch (RemoteVehicleException err) {
			throw err;
		} catch (Exception err) {
			throw new RemoteVehicleException(err);
		}
	}

	@Override
	public void requestForceDriverDownload(String companyId, String driverId) throws RemoteVehicleException {
		Integer startDriverDownload = this.invoker.startDriverDownload(companyId, driverId);
	}

	@Override
	public void requestForceVehicleDownload(String companyId, String plateNumber, Date from, Date to) throws RemoteVehicleException {
		Integer startVehicleDownload = this.invoker.startVehicleDownload(companyId, plateNumber, from, to);
	}

}
