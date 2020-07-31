package com.opentach.server.webservice.rest;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

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
import com.ontimize.gui.SearchValue;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.DateTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.MapTools;
import com.opentach.common.user.Company;
import com.opentach.server.OpentachServerLocator;
import com.opentach.server.webservice.utils.OpentachWSException;
import com.opentach.ws.common.rest.beans.RestLogginRequest;
import com.opentach.ws.common.rest.beans.RestLogginResponse;
import com.utilmize.tools.PhotoUtils;
import com.utilmize.tools.exception.UException;

public class RestDriverService {

	private static final Logger	logger	= LoggerFactory.getLogger(RestDriverService.class);

	@Resource
	private WebServiceContext	context;

	////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////// DRIVER /////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////

	@POST
	@Path("/searchInfoDriver")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	public RestLogginResponse searchInfoDriver(@WebParam(name = "loggin") final RestLogginRequest request) throws OpentachWSException {
		try {
			return (RestLogginResponse) new OntimizeSessionConnectionTemplate(request.getUsrLogin(), request.getUsrPsswd()) {

				@Override
				protected Object doTask(Connection con) throws UException {
					try {
						return RestDriverService.this.searchInfoDriver(request, this.getSessionId());
					} catch (Exception ex) {
						throw new UException(ex);
					}
				}

			}.execute(OpentachServerLocator.getLocator(), true);
		} catch (Exception e) {
			RestDriverService.logger.error(e.getMessage());
			return new RestLogginResponse(-1);
		}
	}

	@POST
	@Path("/searchOtherInfoDriver")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	public RestLogginResponse searchOtherInfoDriver(@WebParam(name = "loggin") final RestLogginRequest request) throws OpentachWSException {
		try {
			return (RestLogginResponse) new OntimizeSessionConnectionTemplate(request.getUsrLogin(), request.getUsrPsswd()) {

				@Override
				protected Object doTask(Connection con) throws UException {
					try {
						return RestDriverService.this.searchOtherInfoDriver(request, this.getSessionId());
					} catch (Exception ex) {
						throw new UException(ex);
					}
				}

			}.execute(OpentachServerLocator.getLocator(), true);
		} catch (Exception e) {
			RestDriverService.logger.error(e.getMessage());
			return new RestLogginResponse(-1);
		}
	}

	@POST
	@Path("/searchInterestedInfoDriver")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	public RestLogginResponse searchInterestedInfoDriver(@WebParam(name = "loggin") final RestLogginRequest request) throws OpentachWSException {
		try {
			return (RestLogginResponse) new OntimizeSessionConnectionTemplate(request.getUsrLogin(), request.getUsrPsswd()) {

				@Override
				protected Object doTask(Connection con) throws UException {
					try {
						return RestDriverService.this.searchInterestedInfoDriver(request, this.getSessionId());
					} catch (Exception ex) {
						throw new UException(ex);
					}
				}

			}.execute(OpentachServerLocator.getLocator(), true);
		} catch (Exception e) {
			RestDriverService.logger.error(e.getMessage());
			return new RestLogginResponse(-1);
		}
	}

	@POST
	@Path("/searchInfractionDriver")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	public RestLogginResponse searchInfractionDriver(@WebParam(name = "loggin") final RestLogginRequest request) throws OpentachWSException {
		try {
			return (RestLogginResponse) new OntimizeSessionConnectionTemplate(request.getUsrLogin(), request.getUsrPsswd()) {

				@Override
				protected Object doTask(Connection con) throws UException {
					try {
						return RestDriverService.this.searchInfractionDriver(request, this.getSessionId());
					} catch (Exception ex) {
						throw new UException(ex);
					}
				}

			}.execute(OpentachServerLocator.getLocator(), true);
		} catch (Exception e) {
			RestDriverService.logger.error(e.getMessage());
			return new RestLogginResponse(-1);
		}
	}

	@POST
	@Path("/searchSummaryDriver")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	public RestLogginResponse searchSummaryDriver(@WebParam(name = "loggin") final RestLogginRequest request) throws OpentachWSException {
		try {
			return (RestLogginResponse) new OntimizeSessionConnectionTemplate(request.getUsrLogin(), request.getUsrPsswd()) {

				@Override
				protected Object doTask(Connection con) throws UException {
					try {
						return RestDriverService.this.searchSummaryDriver(request, this.getSessionId());
					} catch (Exception ex) {
						throw new UException(ex);
					}
				}

			}.execute(OpentachServerLocator.getLocator(), true);
		} catch (Exception e) {
			RestDriverService.logger.error(e.getMessage());
			return new RestLogginResponse(-1);
		}
	}

	private Object searchInfoDriver(RestLogginRequest logginRequest, int sessionId) throws Exception {
		Object idConductor = this.checkDriver(logginRequest);
		return new RestLogginResponse(sessionId, this.getInfoDataForDriver(idConductor, sessionId));
	}

	private Object searchOtherInfoDriver(RestLogginRequest logginRequest, int sessionId) throws Exception {
		Object idConductor = this.checkDriver(logginRequest);
		return new RestLogginResponse(sessionId, this.getOtherDataForDriver(idConductor, sessionId));
	}

	private Object searchInterestedInfoDriver(RestLogginRequest logginRequest, int sessionId) throws Exception {
		Object idConductor = this.checkDriver(logginRequest);

		Company company = RestServiceUtils.getCompany(logginRequest, sessionId);
		String cif = company.getCif();
		Object activeContract = company.getActiveContract();

		// Filters
		Date filterDate = RestServiceUtils.getFilterDate();

		return new RestLogginResponse(sessionId, this.getInterestedDataForDriver(cif, activeContract, filterDate, idConductor, sessionId));
	}

	private Object searchInfractionDriver(RestLogginRequest logginRequest, int sessionId) throws Exception {
		Object idConductor = this.checkDriver(logginRequest);

		Company company = RestServiceUtils.getCompany(logginRequest, sessionId);
		String cif = company.getCif();
		Object activeContract = company.getActiveContract();

		// Filters
		Calendar cal = Calendar.getInstance();
		cal.setTime(DateTools.firstMilisecond(new Date()));
		Date timeNow = cal.getTime();
		Date filterDate = RestServiceUtils.getFilterDate();

		return new RestLogginResponse(sessionId, this.getInfractionForDriver(cif, activeContract, filterDate, timeNow, idConductor, sessionId));
	}

	private Object searchSummaryDriver(RestLogginRequest logginRequest, int sessionId) throws Exception {
		Object idConductor = this.checkDriver(logginRequest);

		Company company = RestServiceUtils.getCompany(logginRequest, sessionId);
		String cif = company.getCif();
		Object activeContract = company.getActiveContract();

		// Filters
		Date filterDate = RestServiceUtils.getFilterDate();

		return new RestLogginResponse(sessionId, this.getSummaryForDriver(cif, activeContract, filterDate, idConductor, sessionId));
	}

	private Object checkDriver(RestLogginRequest logginRequest) {
		Object idConductor = logginRequest.getMap().get("IDCONDUCTOR");
		CheckingTools.failIfNull(idConductor, "E_NO_DRIVER");
		return idConductor;
	}

	private HashMap<Object, Object> getInfoDataForDriver(Object idConductor, int sessionId) throws Exception {
		HashMap<Object, Object> data = new HashMap<>();

		Entity entityConductoresEmp = RestServiceUtils.getEntity("EConductoresEmp");
		EntityResult erConductoresEmp = entityConductoresEmp.query(EntityResultTools.keysvalues("IDCONDUCTOR", idConductor),
				EntityResultTools.attributes("DNI", "NOMBRE", "APELLIDOS", "CIF", "F_ALTA", "USUARIO_ALTA", "MOVIL", "EMAIL", "IDCONDUCTOR", "OBSR", "IDDELEGACION", "DURMIENTE",
						"NOMBRECOMPLETO", "F_BAJA", "EXPIRED_DATE_TRJCONDU", "NOMBRE_EMPRESA", "F_NAC", "PHOTO", "ENVIAR_A"),
				sessionId);
		CheckingTools.failIf(erConductoresEmp.calculateRecordNumber() == 0, "E_NO_DRIVER_FOUND");

		Hashtable recordValues = erConductoresEmp.getRecordValues(0);
		Object driverName = recordValues.get("NOMBRE");
		Object driverSurname = recordValues.get("APELLIDOS");
		Object driverDni = recordValues.get("DNI");
		String driverBirthdate = RestServiceUtils.getFormatDate(recordValues.get("F_NAC"));
		String driverUpDate = RestServiceUtils.getFormatDate(recordValues.get("F_ALTA"));

		/* Driver data */
		MapTools.safePut(data, "DRIVER_NAME", driverName);
		MapTools.safePut(data, "DRIVER_SURNAMES", driverSurname);
		MapTools.safePut(data, "DRIVER_NIF", driverDni);
		MapTools.safePut(data, "DRIVER_BIRTHDAY", driverBirthdate);
		MapTools.safePut(data, "DRIVER_UP_DATE", driverUpDate);
		MapTools.safePut(data, "DRIVER_5B", idConductor);
		return data;
	}

	private HashMap<Object, Object> getOtherDataForDriver(Object idConductor, int sessionId) throws Exception {
		HashMap<Object, Object> data = new HashMap<>();

		Entity entityConductoresEmp = RestServiceUtils.getEntity("EConductoresEmp");
		EntityResult erConductoresEmp = entityConductoresEmp.query(EntityResultTools.keysvalues("IDCONDUCTOR", idConductor),
				EntityResultTools.attributes("DNI", "NOMBRE", "APELLIDOS", "CIF", "F_ALTA", "USUARIO_ALTA", "MOVIL", "EMAIL", "IDCONDUCTOR", "OBSR", "IDDELEGACION", "DURMIENTE",
						"NOMBRECOMPLETO", "F_BAJA", "EXPIRED_DATE_TRJCONDU", "NOMBRE_EMPRESA", "F_NAC", "PHOTO", "ENVIAR_A"),
				sessionId);
		CheckingTools.failIf(erConductoresEmp.calculateRecordNumber() == 0, "E_NO_DRIVER_FOUND");

		PhotoUtils.fixPhoto(erConductoresEmp, "PHOTO", 640, 640, null);

		Hashtable recordValues = erConductoresEmp.getRecordValues(0);

		MapTools.safePut(data, "DRIVER_IMAGE", recordValues.get("PHOTO"));
		MapTools.safePut(data, "DRIVER_PHONE", recordValues.get("MOVIL"));
		MapTools.safePut(data, "DRIVER_EMAIL", recordValues.get("EMAIL"));
		/* days from las download */
		data.putAll(this.getDriverDaysFromLastDownload(idConductor, sessionId));

		return data;
	}

	private HashMap<Object, Object> getInterestedDataForDriver(String cif, Object activeContract, Date fromDate, Object idConductor, int sessionId) throws Exception {
		HashMap<Object, Object> data = new HashMap<>();

		/* information of interest */
		data.putAll(RestServiceUtils.getDriverExpiredDateTarj(cif, activeContract, idConductor, sessionId));
		/* vehicles used */
		data.putAll(this.getDriverVehiclesUsed(activeContract, fromDate, idConductor, sessionId));

		return data;
	}

	private HashMap<Object, Object> getInfractionForDriver(String cif, Object activeContract, Date filterDate, Date timeNow, Object idConductor, int sessionId) throws Exception {
		HashMap<Object, Object> data = new HashMap<>();

		data.putAll(RestServiceUtils.getDriverInfractionData(cif, activeContract, filterDate, timeNow, idConductor, sessionId));

		return data;
	}

	private HashMap<Object, Object> getSummaryForDriver(String cif, Object activeContract, Date time28DaysBefore, Object idConductor, int sessionId) throws Exception {
		HashMap<Object, Object> res = new HashMap<>();

		Entity entityInformeActivCond = RestServiceUtils.getEntity("EInformeActivCond");

		EntityResult erInformeActivCond = entityInformeActivCond.query(
				EntityResultTools.keysvalues("CIF", cif, "CG_CONTRATO", activeContract, "FECINI",
						new SearchValue(SearchValue.BETWEEN,
								new Vector(Arrays.asList(new Object[] { new Timestamp(time28DaysBefore.getTime()), new Timestamp(System.currentTimeMillis()) }))),
						"IDCONDUCTOR", idConductor),
				EntityResultTools.attributes("FECINI", "FECFIN", "DSCR_ACT", "MINUTOS", "MATRICULA", "RANURA_DSCR", "PROCEDENCIA", "ORIGEN_DSCR", "FUERA_AMBITO", "TRANS_TREN"),
				sessionId);

		if (erInformeActivCond.calculateRecordNumber() > 0) {
			Vector<String> dscActs = (Vector<String>) erInformeActivCond.get("DSCR_ACT");
			Vector<Integer> minutes = (Vector<Integer>) erInformeActivCond.get("MINUTOS");

			List<Object> xVals = new ArrayList<>();
			List<String> xValsNames = new ArrayList<>();
			ArrayList<Double> yVals = new ArrayList<>();
			int i = 0;
			Double totalSummary = 0d;
			for (String act : dscActs) {
				Double hours = minutes.get(i).doubleValue() / 60;
				if (xVals.contains(act)) {
					int indexToReplace = xVals.indexOf(act);
					Double oldValue = yVals.get(indexToReplace);
					yVals.remove(indexToReplace);
					yVals.add(indexToReplace, oldValue + hours);
				} else {
					xVals.add(act);
					String dscrInc = act.replace("_", " ").toLowerCase();
					xValsNames.add(dscrInc);
					yVals.add(hours);
				}
				totalSummary += hours;
				i++;
			}
			HashMap<Object, Object> driverSummary = new HashMap<>();
			driverSummary.put("xVals", xValsNames);
			driverSummary.put("yVals", yVals);

			MapTools.safePut(res, "DRIVER_SUMMARY_TOTAL", totalSummary.intValue());
			MapTools.safePut(res, "DRIVER_SUMMARY_DATA", driverSummary);
		}
		return res;
	}

	private HashMap<Object, Object> getDriverVehiclesUsed(Object activeContract, Date fromDate, Object idConductor, int sessionId) throws Exception {
		HashMap<Object, Object> res = new HashMap<>();
		Entity entityInformeUsoVehiculoConductor = RestServiceUtils.getEntity("EInformeUsoVehiculoConductor");
		EntityResult erInformeUsoVehiculoConductor = entityInformeUsoVehiculoConductor.query(
				EntityResultTools.keysvalues("CG_CONTRATO", activeContract, "FECINI", new SearchValue(SearchValue.MORE_EQUAL, fromDate), "IDCONDUCTOR", idConductor),
				EntityResultTools.attributes("MATRICULA"), sessionId);
		MapTools.safePut(res, "DRIVERS_VEHICULES_DATA", EntityResultTools.doGroup(erInformeUsoVehiculoConductor, new String[] { "MATRICULA" }));
		return res;
	}

	private HashMap<Object, Object> getDriverDaysFromLastDownload(Object idConductor, int sessionId) throws Exception {
		HashMap<Object, Object> res = new HashMap<>();
		Entity entityInformePendDescarga = RestServiceUtils.getEntity("EInformePendDescarga");
		EntityResult erInformPendDescarga = entityInformePendDescarga.query(EntityResultTools.keysvalues("IDORIGEN", idConductor, "TIPO", BigDecimal.ONE),
				EntityResultTools.attributes("IDORIGEN", "DSCR", "TIPO", "F_DESCARGA_DATOS", "DIAS"), sessionId);
		if (erInformPendDescarga.calculateRecordNumber() > 0) {
			MapTools.safePut(res, "DRIVER_DAYS_SINCE_LAST_DOWNLOAD", erInformPendDescarga.getRecordValues(0).get("DIAS"));
		}
		return res;
	}

}