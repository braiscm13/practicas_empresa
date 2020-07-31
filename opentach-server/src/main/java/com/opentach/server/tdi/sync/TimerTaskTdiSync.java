package com.opentach.server.tdi.sync;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.TimerTask;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.jee.common.tools.Chronometer;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.opentach.common.company.naming.CompanyNaming;
import com.opentach.server.AbstractOpentachServerLocator;
import com.opentach.server.OpentachServerLocator;

/**
 * The Class TdiAutoFlota.
 */
public class TimerTaskTdiSync extends TimerTask {

	private static final Logger logger = LoggerFactory.getLogger(TimerTaskTdiSync.class);

	@Override
	public void run() {
		Chronometer chrono = new Chronometer().start();
		try {
			TimerTaskTdiSync.logger.info("Start TDI sync");
			// 1.- Query TDI companies
			// 2.- For each company
			// 2.1.- Query Opentach drivers
			// 2.2.- Query TDI drivers
			// 2.3.- Compare by 5b
			// 2.4.- Create into TDI new drivers
			List<Object> companiesCif = this.queryTdiCompanies();
			for (Object cif : companiesCif) {
				this.syncCompanyDrivers(cif);
			}
		} catch (Exception ex) {
			TimerTaskTdiSync.logger.error(null, ex);
		}
		TimerTaskTdiSync.logger.info("Sync finished in {} ms", chrono.stopMs());
	}

	private void syncCompanyDrivers(Object cif) {
		try {
			TdiCompanySettings settings = TdiCompanySettingsFactory.getInstance().getSettings((String) cif);
			if (settings == null) {
				TimerTaskTdiSync.logger.error("TDI company {} is missconfigured", cif);
				return;
			}
			TimerTaskTdiSync.logger.info("Quering tdi drivers for company {}", cif);
			List<TdiDriverQueryBean> tdiDrivers = this.queryTdiDrivers(settings);
			EntityResult opentachDrivers = this.queryOpentachDrivers(cif);
			if (opentachDrivers.calculateRecordNumber() == 0) {
				TimerTaskTdiSync.logger.warn("No opentach drives in company {}", cif);
				return;
			}
			List<TdiDriverAddBean> driversToAddIntoTdi = this.compareDrivers(tdiDrivers, opentachDrivers);
			this.addDriversIntoTdi(driversToAddIntoTdi, settings);
		} catch (Exception error) {
			TimerTaskTdiSync.logger.error("Error syncing company {}", cif, error);
		}
	}

	private void addDriversIntoTdi(List<TdiDriverAddBean> driversToAddIntoTdi, TdiCompanySettings settings) {
		TimerTaskTdiSync.logger.info("{} drivers will be added to Tdi", driversToAddIntoTdi.size());
		for (TdiDriverAddBean driver : driversToAddIntoTdi) {
			try {
				this.addDriverIntoTdi(driver, settings);
			} catch (Exception ex) {
				TimerTaskTdiSync.logger.error("Error creating tdi driver {} for company {}", driver.getId(), settings.getCif());
			}
		}
	}

	private RestResponse addDriverIntoTdi(TdiDriverAddBean driver, TdiCompanySettings settings)
			throws ClientProtocolException, IOException, IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, URISyntaxException {
		TimerTaskTdiSync.logger.info("Adding tdi driver {} for company {}", driver.getDriverid(), settings.getCif());
		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			driver.setGroupid(settings.getGroupid());

			BeanInfo beanInfo = Introspector.getBeanInfo(driver.getClass());
			URIBuilder builder = new URIBuilder().setScheme("http").setHost(settings.getIp()).setPort(Integer.valueOf(settings.getPort())).setPath("/adddriver");
			for (PropertyDescriptor property : beanInfo.getPropertyDescriptors()) {
				Object value = property.getReadMethod().invoke(driver);
				String name = property.getName();
				if (!"class".equals(name) && (value != null)) {
					builder.setParameter(name, String.valueOf(value));
				}
			}
			HttpGet httpRequest = new HttpGet(builder.build());
			ObjectMapper mapper = new ObjectMapper();
			// HttpPost httpRequest = new HttpPost(builder.build());
			// String jsonInString = mapper.writeValueAsString(this.parameters);
			// StringEntity requestEntity = new StringEntity(jsonInString, ContentType.APPLICATION_JSON);
			// httpRequest.setEntity(requestEntity);

			try (CloseableHttpResponse rawResponse = httpClient.execute(httpRequest);) {
				TimerTaskTdiSync.logger.info("Response code: {}", rawResponse.getStatusLine().toString());
				RestResponse response = mapper.readValue(rawResponse.getEntity().getContent(), RestResponse.class);
				TimerTaskTdiSync.logger.info("Response op_name: {}, op_response: {}", response.getOpName(), response.getOpResponse());
				return response;
			}
		}
	}

	private List<TdiDriverAddBean> compareDrivers(List<TdiDriverQueryBean> tdiDrivers, EntityResult opentachDrivers) {

		int nrecords = opentachDrivers.calculateRecordNumber();
		if ((tdiDrivers != null) && !tdiDrivers.isEmpty()) {
			for (int i = 0; i < nrecords; i++) {
				String idConductor = ((List<String>) opentachDrivers.get("IDCONDUCTOR")).get(i);
				if (this.tdiHasId(idConductor, tdiDrivers)) {
					opentachDrivers.deleteRecord(i);
					i--;
					nrecords--;
				}
			}
		}

		nrecords = opentachDrivers.calculateRecordNumber();
		List<TdiDriverAddBean> driversToAddIntoTdi = new ArrayList<TdiDriverAddBean>();
		for (int i = 0; i < nrecords; i++) {
			Hashtable<?, ?> opentachRecord = opentachDrivers.getRecordValues(i);
			TdiDriverAddBean bean = new TdiDriverAddBean();
			bean.setId((String) opentachRecord.get("IDCONDUCTOR"));
			bean.setDrivername((String) opentachRecord.get("NOMBRE"));
			bean.setDriversurname((String) opentachRecord.get("APELLIDOS"));
			bean.setDriverdni((String) opentachRecord.get("DNI"));
			bean.setTlf1((String) opentachRecord.get("MOVIL"));
			bean.setInactive("0");
			driversToAddIntoTdi.add(bean);
		}
		return driversToAddIntoTdi;
	}

	private boolean tdiHasId(String idConductor, List<TdiDriverQueryBean> tdiDrivers) {
		for (TdiDriverQueryBean bean : tdiDrivers) {
			if (bean.getId1().toUpperCase().contains(idConductor.toUpperCase())) {
				return true;
			}
		}
		return false;
	}

	private EntityResult queryOpentachDrivers(Object cif) throws Exception {
		Entity eDrivers = AbstractOpentachServerLocator.getLocator().getEntityReferenceFromServer("EConductoresEmp");
		return eDrivers.query(EntityResultTools.keysvalues("CIF", cif, "DURMIENTE", "N"),
				EntityResultTools.attributes("DNI", "NOMBRE", "APELLIDOS", "MOVIL", "EMAIL", "IDCONDUCTOR"), TableEntity.getEntityPrivilegedId(eDrivers));
	}

	private List<TdiDriverQueryBean> queryTdiDrivers(TdiCompanySettings settings) throws ClientProtocolException, IOException, URISyntaxException {
		// int offset = 0;
		// List<TdiDriverQueryBean> res = new ArrayList<TdiDriverQueryBean>();
		// for (int i = 0; i < 50; i++) { // marcamos un tope
		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			URIBuilder builder = new URIBuilder().setScheme("http").setHost(settings.getIp()).setPort(Integer.valueOf(settings.getPort())).setPath("/listdrivers");
			// builder.addParameter("offset", offset + "");
			HttpGet httpRequest = new HttpGet(builder.build());
			ObjectMapper mapper = new ObjectMapper();
			try (CloseableHttpResponse rawResponse = httpClient.execute(httpRequest);) {
				TimerTaskTdiSync.logger.info("Response code: {}", rawResponse.getStatusLine().toString());
				List<TdiDriverQueryBean> partialRes = mapper.readValue(rawResponse.getEntity().getContent(), TdiListdriversResponse.class).getDrivers();
				return partialRes;
				// if ((partialRes == null) || (partialRes.size() == 0)) {
				// return res;
				// }
				// offset += partialRes.size();
				// res.addAll(partialRes);
			}
		}
		// }
		// return res;
	}

	private List<Object> queryTdiCompanies() throws Exception {
		OpentachServerLocator locator = (OpentachServerLocator) AbstractOpentachServerLocator.getLocator();
		Entity eCompanies = locator.getEntityReferenceFromServer(CompanyNaming.ENTITY);
		EntityResult er = eCompanies.query(EntityResultTools.keysvalues("TDI", "S"), EntityResultTools.attributes("CIF"), TableEntity.getEntityPrivilegedId(eCompanies));
		if (er.calculateRecordNumber() == 0) {
			TimerTaskTdiSync.logger.warn("No company with TDI enabled found");
			return new ArrayList<>();
		}
		return (List<Object>) er.get("CIF");
	}

}
