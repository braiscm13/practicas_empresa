package com.opentach.server.webservice.rest;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import javax.annotation.Resource;
import javax.jws.WebParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.xml.ws.WebServiceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.db.SQLStatementBuilder.BasicField;
import com.ontimize.db.SQLStatementBuilder.BasicOperator;
import com.ontimize.db.TableEntity;
import com.ontimize.gui.SearchValue;
import com.ontimize.jee.common.tools.DateTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.MapTools;
import com.opentach.common.sessionstatus.ISessionStatusReportService.GroupingTime;
import com.opentach.common.user.Company;
import com.opentach.server.OpentachServerLocator;
import com.opentach.server.util.StatementBuilderHelper;
import com.opentach.server.webservice.utils.OpentachWSException;
import com.opentach.ws.common.rest.beans.RestLogginRequest;
import com.opentach.ws.common.rest.beans.RestLogginResponse;
import com.utilmize.tools.exception.UException;

public class RestCompanyService {

	private static final Logger	logger	= LoggerFactory.getLogger(RestCompanyService.class);

	@Resource
	private WebServiceContext	context;

	////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////// DRIVER /////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////

	@POST
	@Path("/searchCompanyInfo")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	public RestLogginResponse searchCompanyInfo(@WebParam(name = "loggin") final RestLogginRequest request) throws OpentachWSException {
		try {
			return (RestLogginResponse) new OntimizeSessionConnectionTemplate(request.getUsrLogin(), request.getUsrPsswd()) {

				@Override
				protected Object doTask(Connection con) throws UException {
					try {
						return RestCompanyService.this.searchCompany(request, this.getSessionId());
					} catch (Exception ex) {
						throw new UException(ex);
					}
				}

			}.execute(OpentachServerLocator.getLocator(), true);
		} catch (Exception e) {
			RestCompanyService.logger.error(null, e);
			return new RestLogginResponse(-1);
		}
	}

	protected Object searchCompany(RestLogginRequest logginRequest, int sessionId) throws Exception {
		Company company = RestServiceUtils.getCompany(logginRequest, sessionId);
		return new RestLogginResponse(sessionId, RestCompanyService.getCompanyDataForUser(company, true, sessionId));
	}

	public static HashMap<Object, Object> getCompanyDataForUser(Company company, boolean companyInformation, int sessionId) throws Exception {
		HashMap<Object, Object> data = new HashMap<>();

		String cif = company.getCif();
		Object activeContract = company.getActiveContract();

		/* Login data */
		String login = RestServiceUtils.getLocator().getUserData(sessionId).getLogin();
		MapTools.safePut(data, "USER", login);

		/* SEARCH COMPANIES */
		MapTools.safePut(data, "CIF", cif);// CIF selected by default
		MapTools.safePut(data, "SEARCH_CIF_DATA", RestCompanyService.getCompaniesData(sessionId));

		/* SEARCH DRIVERS */
		MapTools.safePut(data, "SEARCH_DRIVERS_DATA", RestCompanyService.getSearchDriversData(cif, sessionId));
		/* SEARCH VEHICLES */
		MapTools.safePut(data, "SEARCH_VEHICLES_DATA", RestCompanyService.getSearchVehiclesData(cif, sessionId));

		if (companyInformation) {
			// Filters
			Calendar cal = Calendar.getInstance();
			cal.setTime(DateTools.firstMilisecond(new Date()));
			Date timeNow = cal.getTime();
			cal.add(Calendar.MONTH, -3);
			Date time3MonthsBefore = cal.getTime();
			Date filterDate = RestServiceUtils.getFilterDate();

			/* Company data */
			MapTools.safePut(data, "COMPANY_NAME", company.getName());
			MapTools.safePut(data, "COMPANY_CIF", cif);
			MapTools.safePut(data, "COMPANY_USER", login);
			/* Download Evolution */
			try {
				data.putAll(RestCompanyService.getDownloadEvolutionData(activeContract, timeNow, time3MonthsBefore, sessionId));
			} catch (Exception err) {
				RestCompanyService.logger.error(null, err);
			}
			/* Vehicles and drivers summary */
			try {
				data.putAll(RestCompanyService.getCarsData(cif, sessionId));
			} catch (Exception err) {
				RestCompanyService.logger.error(null, err);
			}
			try {
				data.putAll(RestCompanyService.getConductoresData(cif, sessionId));
			} catch (Exception err) {
				RestCompanyService.logger.error(null, err);
			}
			/* Infraction data */
			// data.putAll(RestServiceUtils.getDriverInfractionData(cif, activeContract, filterDate, timeNow, null, sessionId));
			/* Incident data */
			try {
				data.putAll(RestServiceUtils.getVehicleIncidentData(cif, activeContract, filterDate, null, sessionId));
			} catch (Exception err) {
				RestCompanyService.logger.error(null, err);
			}
			/* Drivers and vehicles downloads dates */
			try {
				data.putAll(RestCompanyService.getPendDownloadData(cif, activeContract, sessionId));
			} catch (Exception err) {
				RestCompanyService.logger.error(null, err);
			}
			/* information of interest */
			try {
				data.putAll(RestCompanyService.getRenovationDate(cif, sessionId));
			} catch (Exception err) {
				RestCompanyService.logger.error(null, err);
			}
			/* next tachograph setup */
			try {
				data.putAll(RestServiceUtils.getVehicleNextCalibradoData(cif, activeContract, null, sessionId));
			} catch (Exception err) {
				RestCompanyService.logger.error(null, err);
			}
			/* next card expiration */
			try {
				data.putAll(RestServiceUtils.getDriverExpiredDateTarj(cif, activeContract, null, sessionId));
			} catch (Exception err) {
				RestCompanyService.logger.error(null, err);
			}
			/* Quick summary */
			try {
				MapTools.safePut(data, "CONTROL", RestCompanyService.getQuickControl(activeContract, filterDate, sessionId));
			} catch (Exception err) {
				RestCompanyService.logger.error(null, err);
			}
			// cdtipo_control
			try {
				MapTools.safePut(data, "CAP", (float) 0);// FIXME ?
			} catch (Exception err) {
				RestCompanyService.logger.error(null, err);
			}
		}
		return data;
	}

	public static Object getCompaniesData(int sessionId) {
		EntityResult entityCompaniesUser = new EntityResult();
		EntityResultTools.initEntityResult(entityCompaniesUser, Arrays.asList("NOMBRE", "CIF"));
		try {
			for (Company company : RestServiceUtils.getCompanies(sessionId)) {
				entityCompaniesUser.addRecord(EntityResultTools.keysvalues("NOMBRE", company.getName(), "CIF", company.getCif()));
			}
		} catch (Exception e) {
			RestCompanyService.logger.warn(null, e);
		}
		return entityCompaniesUser;
	}

	public static Object getSearchVehiclesData(String cif, int sessionId) throws Exception {
		Entity entityVehiculosEmp = RestServiceUtils.getEntity("EVehiculosEmp");
		EntityResult res = entityVehiculosEmp.query(EntityResultTools.keysvalues("CIF", cif), EntityResultTools.attributes("MATRICULA", "DSCR"), sessionId);
		return EntityResultTools.doSort(res, "MATRICULA");
	}

	public static Object getSearchDriversData(String cif, int sessionId) throws Exception {
		Entity entityConductoresEmp = RestServiceUtils.getEntity("EConductoresEmp");
		EntityResult res = entityConductoresEmp.query(EntityResultTools.keysvalues("CIF", cif), EntityResultTools.attributes("DNI", "NOMBRE", "APELLIDOS", "IDCONDUCTOR"),
				sessionId);
		return EntityResultTools.doSort(res, "APELLIDOS");
	}

	public static HashMap<Object, Object> getRenovationDate(String cif, int sessionId) throws Exception {
		Entity entityCifEmpreReq = RestServiceUtils.getEntity("ECifEmpreReq");
		EntityResult erCifEmpreReq = entityCifEmpreReq.query(EntityResultTools.keysvalues("CIF", cif),
				EntityResultTools.attributes("CIF", "NUMREQ", "USUARIO_ALTA", "F_ALTA", "F_BAJA", "FECINID", "FECFIND"), sessionId);
		HashMap<Object, Object> res = new HashMap<>();
		if (erCifEmpreReq.calculateRecordNumber() > 0) {
			erCifEmpreReq = EntityResultTools.doSort(erCifEmpreReq, "FECFIND");
			res.put("renovation_date", new SimpleDateFormat("dd/MM/yyyy").format(erCifEmpreReq.getRecordValues(0).get("FECFIND")));
		}
		return res;
	}

	public static HashMap<Object, Object> getPendDownloadData(String cif, Object activeContract, int sessionId) throws Exception {
		Entity entityInformePendDescarga = RestServiceUtils.getEntity("EInformePendDescarga");
		EntityResult erInformPendDescarga = entityInformePendDescarga.query(EntityResultTools.keysvalues("CIF", cif, "CG_CONTRATO", activeContract),
				EntityResultTools.attributes("IDORIGEN", "DSCR", "TIPO", "F_DESCARGA_DATOS", "DIAS"), sessionId);

		erInformPendDescarga = EntityResultTools.doSort(erInformPendDescarga, "DIAS");

		HashMap<Object, Object> res = new HashMap<>();
		if (erInformPendDescarga.calculateRecordNumber() > 0) {
			List<String> xVehicleValues = new ArrayList<>();
			List<Long> yVehicleValues = new ArrayList<>();
			List<String> xDriverValues = new ArrayList<>();
			List<Long> yDriverValues = new ArrayList<>();

			for (int i = erInformPendDescarga.calculateRecordNumber() - 1; i >= 0; i--) {
				Hashtable recordValues = erInformPendDescarga.getRecordValues(i);

				Number days = (Number) recordValues.get("DIAS");
				String idorigen = (String) recordValues.get("IDORIGEN");
				String dscr = (String) recordValues.get("DSCR");
				Number tipo = (Number) recordValues.get("TIPO");
				if (tipo.intValue() == 0) {
					xVehicleValues.add(idorigen + (dscr != null ? ("    " + dscr) : ""));
					yVehicleValues.add(days != null ? days.longValue() : null);
				} else {
					xDriverValues.add(dscr);
					yDriverValues.add(days != null ? days.longValue() : null);
				}
			}

			HashMap<Object, Object> vehiclesData = new HashMap<>();
			vehiclesData.put("names", xVehicleValues);
			vehiclesData.put("values", yVehicleValues);

			HashMap<Object, Object> driversData = new HashMap<>();
			driversData.put("names", xDriverValues);
			driversData.put("values", yDriverValues);

			MapTools.newMap(res, //
					"DRIVERS_DATA", driversData, //
					"DRIVERS", ((List<?>) driversData.get("names")).size(), //
					"VEHICLES_DATA", vehiclesData, //
					"VEHICLES", ((List<?>) vehiclesData.get("names")).size());

		}

		return res;
	}

	public static HashMap<Object, Object> getConductoresData(String cif, int sessionId) throws Exception {
		Entity entityConductoresEmp = RestServiceUtils.getEntity("EConductoresEmp");
		EntityResult erConductores = entityConductoresEmp.query(EntityResultTools.keysvalues("CIF", cif), EntityResultTools.attributes("DURMIENTE"),
				TableEntity.getEntityPrivilegedId(entityConductoresEmp));
		return MapTools.newMap(new HashMap<>(), //
				"DRIVER", (float) erConductores.calculateRecordNumber(), //
				"COMMON",
				(float) EntityResultTools.dofilter((EntityResult) erConductores.clone(), EntityResultTools.keysvalues("DURMIENTE", new SearchValue(SearchValue.EQUAL, "N")))
				.calculateRecordNumber(), //
				"NO_COMMON",
				(float) EntityResultTools.dofilter(erConductores, EntityResultTools.keysvalues("DURMIENTE", new SearchValue(SearchValue.EQUAL, "S"))).calculateRecordNumber());

	}

	public static HashMap<Object, Object> getCarsData(String cif, int sessionId) throws Exception {
		Entity entityVehiculosEmp = RestServiceUtils.getEntity("EVehiculosEmp");
		EntityResult erVehiculos = entityVehiculosEmp.query(EntityResultTools.keysvalues("CIF", cif), EntityResultTools.attributes("F_BAJA"),
				TableEntity.getEntityPrivilegedId(entityVehiculosEmp));
		return MapTools.newMap(new HashMap<>(), //
				"CARS", (float) erVehiculos.calculateRecordNumber(), //
				"DOWN_DATES",
				(float) (EntityResultTools.dofilter(erVehiculos, EntityResultTools.keysvalues("F_BAJA", new SearchValue(SearchValue.NOT_NULL, null))).calculateRecordNumber()));
	}

	public static float getQuickControl(Object activeContract, Date time28DaysBefore, int sessionId) throws Exception {
		Entity entityControles = RestServiceUtils.getEntity("EControles");
		return entityControles.query(EntityResultTools.keysvalues("CG_CONTRATO", activeContract, "FEC_HORA", new SearchValue(SearchValue.MORE_EQUAL, time28DaysBefore)),
				EntityResultTools.attributes("NUMREQ"), sessionId).calculateRecordNumber();
	}

	public static HashMap<Object, Object> getDownloadEvolutionData(Object activeContract, Date timeNow, Date time3MonthsBefore, int sessionId) throws Exception {
		BasicExpression filterNumreq = new BasicExpression(new BasicField("CDVEMPRE_REQ_REALES.NUMREQ"), BasicOperator.EQUAL_OP, activeContract);
		BasicExpression filterBeginDate = new BasicExpression(new BasicField("CDFICHEROS.F_ALTA"), BasicOperator.MORE_EQUAL_OP, time3MonthsBefore);
		BasicExpression queryFilter = new BasicExpression(filterNumreq, BasicOperator.AND_OP, filterBeginDate);
		EntityResult er = StatementBuilderHelper.doQuery(RestServiceUtils.getLocator(), "sql/sessionstatus/filesUploads", queryFilter, GroupingTime.DAY);

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

		ArrayList<String> xVals = new ArrayList<>();
		ArrayList<Float> yVals1 = new ArrayList<>();
		ArrayList<Float> yVals2 = new ArrayList<>();

		Calendar cal = Calendar.getInstance();
		cal.setTime(time3MonthsBefore);
		while (cal.getTime().getTime() <= timeNow.getTime()) {
			xVals.add(format.format(cal.getTime()));
			yVals1.add(0f);
			yVals2.add(0f);
			cal.add(Calendar.DAY_OF_YEAR, 1);
		}
		int totalRecords = er.calculateRecordNumber();
		List typeList = (List) er.get("SERIE");
		List valueList = (List) er.get("YAXIS");
		List datesList = (List) er.get("XAXIS");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for (int i = 0; i < totalRecords; i++) {
			Date recordDate = DateTools.truncate(sdf.parse((String) datesList.get(i)));
			Number value = (Number) valueList.get(i);
			String type = (String) typeList.get(i);
			int idx = xVals.indexOf(format.format(recordDate));
			if ("TC".equals(type)) {
				yVals1.set(idx, value.floatValue());
			} else {
				yVals2.set(idx, value.floatValue());
			}
		}

		HashMap<Object, Object> downloadEvoData = MapTools.newMap(new HashMap<>(), "xVals", xVals, "yVals1", yVals1, "yVals2", yVals2);

		return MapTools.newMap(new HashMap<>(), "DOWNLOAD_EVOLUTION_DATA", downloadEvoData);
	}

}