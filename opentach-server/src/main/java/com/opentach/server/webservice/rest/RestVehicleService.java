package com.opentach.server.webservice.rest;

import java.math.BigDecimal;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.MapTools;
import com.opentach.common.user.Company;
import com.opentach.server.OpentachServerLocator;
import com.opentach.server.webservice.utils.OpentachWSException;
import com.opentach.ws.common.rest.beans.RestLogginRequest;
import com.opentach.ws.common.rest.beans.RestLogginResponse;
import com.utilmize.tools.exception.UException;

public class RestVehicleService {

	private static final Logger	logger	= LoggerFactory.getLogger(RestVehicleService.class);

	@Resource
	private WebServiceContext	context;

	////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////// VEHICLE /////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////

	@POST
	@Path("/searchInfoVehicle")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	public RestLogginResponse searchInfoVehicle(@WebParam(name = "loggin") final RestLogginRequest request) throws OpentachWSException {
		try {
			return (RestLogginResponse) new OntimizeSessionConnectionTemplate(request.getUsrLogin(), request.getUsrPsswd()) {

				@Override
				protected Object doTask(Connection con) throws UException {
					try {
						return RestVehicleService.this.searchInfoVehicle(request, this.getSessionId());
					} catch (Exception ex) {
						throw new UException(ex);
					}
				}

			}.execute(OpentachServerLocator.getLocator(), true);
		} catch (Exception e) {
			RestVehicleService.logger.error(e.getMessage());
			return new RestLogginResponse(-1);
		}
	}

	@POST
	@Path("/searchInterestedInfoVehicle")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	public RestLogginResponse searchInterestedInfoVehicle(@WebParam(name = "loggin") final RestLogginRequest request) throws OpentachWSException {
		try {
			return (RestLogginResponse) new OntimizeSessionConnectionTemplate(request.getUsrLogin(), request.getUsrPsswd()) {

				@Override
				protected Object doTask(Connection con) throws UException {
					try {
						return RestVehicleService.this.searchInterestedInfoVehicle(request, this.getSessionId());
					} catch (Exception ex) {
						throw new UException(ex);
					}
				}

			}.execute(OpentachServerLocator.getLocator(), true);
		} catch (Exception e) {
			RestVehicleService.logger.error(e.getMessage());
			return new RestLogginResponse(-1);
		}
	}

	@POST
	@Path("/searchInfractionVehicle")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	public RestLogginResponse searchInfractionVehicle(@WebParam(name = "loggin") final RestLogginRequest request) throws OpentachWSException {
		try {
			return (RestLogginResponse) new OntimizeSessionConnectionTemplate(request.getUsrLogin(), request.getUsrPsswd()) {

				@Override
				protected Object doTask(Connection con) throws UException {
					try {
						return RestVehicleService.this.searchInfractionVehicle(request, this.getSessionId());
					} catch (Exception ex) {
						throw new UException(ex);
					}
				}

			}.execute(OpentachServerLocator.getLocator(), true);
		} catch (Exception e) {
			RestVehicleService.logger.error(e.getMessage());
			return new RestLogginResponse(-1);
		}
	}

	@POST
	@Path("/searchFaultVehicle")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	public RestLogginResponse searchFaultVehicle(@WebParam(name = "loggin") final RestLogginRequest request) throws OpentachWSException {
		try {
			return (RestLogginResponse) new OntimizeSessionConnectionTemplate(request.getUsrLogin(), request.getUsrPsswd()) {

				@Override
				protected Object doTask(Connection con) throws UException {
					try {
						return RestVehicleService.this.searchFaultVehicle(request, this.getSessionId());
					} catch (Exception ex) {
						throw new UException(ex);
					}
				}

			}.execute(OpentachServerLocator.getLocator(), true);
		} catch (Exception e) {
			RestVehicleService.logger.error(e.getMessage());
			return new RestLogginResponse(-1);
		}
	}

	@POST
	@Path("/searchKMVehicle")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	public RestLogginResponse searchKMVehicle(@WebParam(name = "loggin") final RestLogginRequest request) throws OpentachWSException {
		try {
			return (RestLogginResponse) new OntimizeSessionConnectionTemplate(request.getUsrLogin(), request.getUsrPsswd()) {

				@Override
				protected Object doTask(Connection con) throws UException {
					try {
						return RestVehicleService.this.searchKMVehicle(request, this.getSessionId());
					} catch (Exception ex) {
						throw new UException(ex);
					}
				}

			}.execute(OpentachServerLocator.getLocator(), true);
		} catch (Exception e) {
			RestVehicleService.logger.error(e.getMessage());
			return new RestLogginResponse(-1);
		}
	}

	@POST
	@Path("/searchActivitySummaryVehicle")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	public RestLogginResponse searchActivitySummaryVehicle(@WebParam(name = "loggin") final RestLogginRequest request) throws OpentachWSException {
		try {
			return (RestLogginResponse) new OntimizeSessionConnectionTemplate(request.getUsrLogin(), request.getUsrPsswd()) {

				@Override
				protected Object doTask(Connection con) throws UException {
					try {
						return RestVehicleService.this.searchActivitySummaryVehicle(request, this.getSessionId());
					} catch (Exception ex) {
						throw new UException(ex);
					}
				}

			}.execute(OpentachServerLocator.getLocator(), true);
		} catch (Exception e) {
			RestVehicleService.logger.error(e.getMessage());
			return new RestLogginResponse(-1);
		}
	}

	@POST
	@Path("/searchDriverSummaryVehicle")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	public RestLogginResponse searchDriverSummaryVehicle(@WebParam(name = "loggin") final RestLogginRequest request) throws OpentachWSException {
		try {
			return (RestLogginResponse) new OntimizeSessionConnectionTemplate(request.getUsrLogin(), request.getUsrPsswd()) {

				@Override
				protected Object doTask(Connection con) throws UException {
					try {
						return RestVehicleService.this.searchDriverSummaryVehicle(request, this.getSessionId());
					} catch (Exception ex) {
						throw new UException(ex);
					}
				}

			}.execute(OpentachServerLocator.getLocator(), true);
		} catch (Exception e) {
			RestVehicleService.logger.error(e.getMessage());
			return new RestLogginResponse(-1);
		}
	}

	private Object searchInfoVehicle(RestLogginRequest logginRequest, int sessionId) throws Exception {
		Object matricula = this.checkPlate(logginRequest);

		Company company = RestServiceUtils.getCompany(logginRequest, sessionId);
		return new RestLogginResponse(sessionId, this.getInfoDataForVehicle(company.getCif(), matricula, sessionId));
	}

	private Object searchInterestedInfoVehicle(RestLogginRequest logginRequest, int sessionId) throws Exception {
		Object matricula = this.checkPlate(logginRequest);

		Company company = RestServiceUtils.getCompany(logginRequest, sessionId);
		return new RestLogginResponse(sessionId,
				this.getInterestedDataForVehicle(company.getCif(), company.getActiveContract(), matricula, RestServiceUtils.getFilterDate(), sessionId));
	}

	private Object searchInfractionVehicle(RestLogginRequest logginRequest, int sessionId) throws Exception {
		Object matricula = this.checkPlate(logginRequest);

		Company company = RestServiceUtils.getCompany(logginRequest, sessionId);
		return new RestLogginResponse(sessionId,
				this.getInfractionDataForVehicle(company.getCif(), company.getActiveContract(), RestServiceUtils.getFilterDate(), matricula, sessionId));
	}

	private Object searchFaultVehicle(RestLogginRequest logginRequest, int sessionId) throws Exception {
		Object matricula = this.checkPlate(logginRequest);

		Company company = RestServiceUtils.getCompany(logginRequest, sessionId);
		return new RestLogginResponse(sessionId,
				this.getFaultDataForVehicle(company.getCif(), company.getActiveContract(), RestServiceUtils.getFilterDate(), matricula, sessionId));
	}

	private Object searchKMVehicle(RestLogginRequest logginRequest, int sessionId) throws Exception {
		Object matricula = this.checkPlate(logginRequest);

		Company company = RestServiceUtils.getCompany(logginRequest, sessionId);
		return new RestLogginResponse(sessionId, this.getKMDataForVehicle(company.getCif(), company.getActiveContract(), RestServiceUtils.getFilterDate(), matricula, sessionId));
	}

	private Object searchActivitySummaryVehicle(RestLogginRequest logginRequest, int sessionId) throws Exception {
		Object matricula = this.checkPlate(logginRequest);

		Company company = RestServiceUtils.getCompany(logginRequest, sessionId);
		return new RestLogginResponse(sessionId,
				this.getActivitySummaryForVehicle(company.getCif(), company.getActiveContract(), RestServiceUtils.getFilterDate(), matricula, sessionId));
	}

	private Object searchDriverSummaryVehicle(RestLogginRequest logginRequest, int sessionId) throws Exception {
		Object matricula = this.checkPlate(logginRequest);

		Company company = RestServiceUtils.getCompany(logginRequest, sessionId);
		return new RestLogginResponse(sessionId,
				this.getDriverSummaryForVehicle(company.getCif(), company.getActiveContract(), RestServiceUtils.getFilterDate(), matricula, sessionId));
	}

	private Object checkPlate(RestLogginRequest logginRequest) {
		Object matricula = logginRequest.getMap().get("MATRICULA");
		CheckingTools.failIfNull(matricula, "E_NO_PLATE");
		return matricula;
	}

	private HashMap<Object, Object> getInfoDataForVehicle(String cif, Object matricula, int sessionId) throws Exception {
		HashMap<Object, Object> data = new HashMap<>();

		Entity entityVehiculosEmp = RestServiceUtils.getEntity("EVehiculosEmp");
		EntityResult erVehiculosEmp = entityVehiculosEmp.query(EntityResultTools.keysvalues("CIF", cif, "MATRICULA", matricula),
				EntityResultTools.attributes("MATRICULA", "DSCR", "DURMIENTE", "IDVEHICLETYPE"), sessionId);
		CheckingTools.failIf(erVehiculosEmp.calculateRecordNumber() == 0, "E_NO_PLATE_FOUND");

		Hashtable recordValues = erVehiculosEmp.getRecordValues(0);
		Object vehicleDescription = recordValues.get("DSCR");
		Object vehicleSleeping = recordValues.get("DURMIENTE");
		Object idVehicleType = recordValues.get("IDVEHICLETYPE");
		if ("M".equals(idVehicleType)) {
			idVehicleType = "MERCANCIA";
		} else if ("P".equals(idVehicleType)) {
			idVehicleType = "PERSONAS";
		}

		/* VEHICLE DATA */
		MapTools.safePut(data, "VEHICLE_NAME", matricula);
		MapTools.safePut(data, "VEHICLE_TYPE", idVehicleType);
		MapTools.safePut(data, "VEHICLE_DESCRIPTION", vehicleDescription);
		MapTools.safePut(data, "VEHICLE_SLEEPING", vehicleSleeping);
		return data;
	}

	private HashMap<Object, Object> getInterestedDataForVehicle(String cif, Object activeContract, Object matricula, Date time28DaysBefore, int sessionId) throws Exception {
		HashMap<Object, Object> data = new HashMap<>();

		/**/
		// renovation_tach_value
		// renovation_tach_date
		data.putAll(RestServiceUtils.getVehicleNextCalibradoData(cif, activeContract, matricula, sessionId));

		/* VEHICLE USED */
		data.putAll(this.getVehicleUsesData(activeContract, matricula, time28DaysBefore, sessionId));

		return data;
	}

	private HashMap<Object, Object> getVehicleUsesData(Object activeContract, Object matricula, Date time28DaysBefore, int sessionId) throws Exception {
		HashMap<Object, Object> res = new HashMap<>();
		Entity entityInformeUsoVehiculo = RestServiceUtils.getEntity("EInformeUsoVehiculoVehiculo");
		EntityResult erInformeUsoVehiculo = entityInformeUsoVehiculo.query(
				EntityResultTools.keysvalues("CG_CONTRATO", activeContract, "MATRICULA", matricula, "FECHA", new SearchValue(SearchValue.MORE_EQUAL, time28DaysBefore)),
				EntityResultTools.attributes("NOMBRE", "APELLIDOS"), sessionId);
		MapTools.safePut(res, "VEHICLE_DRIVERS_DATA", EntityResultTools.doGroup(erInformeUsoVehiculo, new String[] { "NOMBRE", "APELLIDOS", "IDCONDUCTOR" }));
		return res;
	}

	private HashMap<Object, Object> getInfractionDataForVehicle(String cif, Object activeContract, Date filterDate, Object matricula, int sessionId) throws Exception {
		HashMap<Object, Object> data = new HashMap<>();
		/* VEHICLE INCIDENT */
		data.putAll(RestServiceUtils.getVehicleIncidentData(cif, activeContract, filterDate, matricula, sessionId));
		return data;
	}

	private HashMap<Object, Object> getFaultDataForVehicle(String cif, Object activeContract, Date filterDate, Object matricula, int sessionId) throws Exception {
		Entity entityFallos = RestServiceUtils.getEntity("EFallos");
		EntityResult erFallos = entityFallos.query(
				EntityResultTools.keysvalues("CG_CONTRATO", activeContract, "MATRICULA", matricula, "FECINI", new SearchValue(SearchValue.MORE_EQUAL, filterDate)),
				EntityResultTools.attributes("TPFALLO", "DSCR"), sessionId);
		HashMap<Object, Object> faultData = this.processFaultData(erFallos);
		int totalFallos = erFallos.calculateRecordNumber();
		HashMap<Object, Object> res = new HashMap<>();
		if (totalFallos > 0) {
			MapTools.safePut(res, "VEHICLE_TOTAL_FAULT", totalFallos);
			MapTools.safePut(res, "VEHICLE_FAULT_DATA", faultData);
		}
		return res;
	}

	// TODO fix Exception
	private HashMap<Object, Object> getKMDataForVehicle(String cif, Object activeContract, Date time28DaysBefore, Object matricula, int sessionId) throws Exception {
		HashMap<Object, Object> res = new HashMap<>();
		Entity entityKmVehiculo = RestServiceUtils.getEntity("EKmVehiculo");
		EntityResult erKmVehiculo = entityKmVehiculo.query(
				EntityResultTools.keysvalues("CG_CONTRATO", activeContract, "MATRICULA", matricula, "FECHA", new SearchValue(SearchValue.MORE_EQUAL, time28DaysBefore)),
				EntityResultTools.attributes("FECHA", "KM"), sessionId);

		if (erKmVehiculo.calculateRecordNumber() > 0) {
			HashMap<Object, Object> kmVehicleData = new HashMap<>();

			ArrayList<String> xVals = new ArrayList<>();
			ArrayList<Float> yVals = new ArrayList<>();
			Float lastKm = null;
			for (int i = 0; i <= (erKmVehiculo.calculateRecordNumber() - 1); i++) {
				Hashtable recordValues = erKmVehiculo.getRecordValues(i);

				xVals.add(new SimpleDateFormat("dd/MM/yyyy").format(recordValues.get("FECHA")));
				float currentKm = ((BigDecimal) recordValues.get("KM")).floatValue();

				float diffKm = lastKm == null ? 0f : currentKm - lastKm;
				lastKm = currentKm;

				yVals.add(diffKm);
			}

			kmVehicleData.put("xVals", xVals);
			kmVehicleData.put("yVals", yVals);
			MapTools.safePut(res, "VEHICLE_KM_DATA", kmVehicleData);
		}
		return res;
	}

	private HashMap<Object, Object> getActivitySummaryForVehicle(String cif, Object activeContract, Date time28DaysBefore, Object matricula, int sessionId) throws Exception {
		HashMap<Object, Object> res = new HashMap<>();
		Entity entityInformeActivCondVehiculo = RestServiceUtils.getEntity("EInformeActivCondVehiculo");
		EntityResult erInformeActivCondVehiculo = entityInformeActivCondVehiculo.query(EntityResultTools.keysvalues("CIF", cif, "CG_CONTRATO", activeContract, "MATRICULA",
				matricula, "FECINI", new SearchValue(SearchValue.MORE_EQUAL, time28DaysBefore)), EntityResultTools.attributes("DNI", "NOMBRE", "APELLIDOS", "DSCR_ACT", "MINUTOS"),
				sessionId);

		if (erInformeActivCondVehiculo.calculateRecordNumber() > 0) {
			Vector<String> dscActs = (Vector<String>) erInformeActivCondVehiculo.get("DSCR_ACT");
			Vector<BigDecimal> minutes = (Vector<BigDecimal>) erInformeActivCondVehiculo.get("MINUTOS");

			List<Object> xVals = new ArrayList<>();
			List<String> xValsNames = new ArrayList<>();
			ArrayList<Double> yVals = new ArrayList<>();
			int i = 0;
			Double totalSummary = 0d;
			for (String act : dscActs) {
				Double hours = (double) (minutes.get(i).doubleValue() / 60);
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

			MapTools.safePut(res, "VEHICLE_SUMMARY_TOTAL", totalSummary.intValue());
			MapTools.safePut(res, "VEHICLE_SUMMARY_DATA", driverSummary);
		}
		return res;
	}

	// TODO fix Exception
	private HashMap<Object, Object> getDriverSummaryForVehicle(String cif, Object activeContract, Date time28DaysBefore, Object matricula, int sessionId) throws Exception {
		HashMap<Object, Object> res = new HashMap<>();
		Entity entityInformeActivCondVehiculo = RestServiceUtils.getEntity("EInformeActivCondVehiculo");
		EntityResult erInformeActivCondVehiculo = entityInformeActivCondVehiculo.query(EntityResultTools.keysvalues("CIF", cif, "CG_CONTRATO", activeContract, "MATRICULA",
				matricula, "FECINI", new SearchValue(SearchValue.MORE_EQUAL, time28DaysBefore)), EntityResultTools.attributes("DNI", "NOMBRE", "APELLIDOS", "DSCR_ACT", "MINUTOS"),
				sessionId);

		if (erInformeActivCondVehiculo.calculateRecordNumber() > 0) {
			Vector<String> dnis = (Vector<String>) erInformeActivCondVehiculo.get("DNI");
			Vector<String> names = (Vector<String>) erInformeActivCondVehiculo.get("NOMBRE");
			Vector<String> surnames = (Vector<String>) erInformeActivCondVehiculo.get("APELLIDOS");
			Vector<BigDecimal> minutes = (Vector<BigDecimal>) erInformeActivCondVehiculo.get("MINUTOS");

			List<Object> xVals = new ArrayList<>();
			List<String> xValsNames = new ArrayList<>();
			ArrayList<Double> yVals = new ArrayList<>();
			int i = 0;
			Double totalSummary = 0d;
			for (String dni : dnis) {
				Double hours = (double) (minutes.get(i).doubleValue() / 60);
				if (xVals.contains(dni)) {
					int indexToReplace = xVals.indexOf(dni);
					Double oldValue = yVals.get(indexToReplace);
					yVals.remove(indexToReplace);
					yVals.add(indexToReplace, oldValue + hours);
				} else {
					xVals.add(dni);
					xValsNames.add(names.get(i) + ", " + surnames.get(i));
					yVals.add(hours);
				}
				totalSummary += hours;
				i++;
			}
			HashMap<Object, Object> vehicleDriversSummary = new HashMap<>();
			vehicleDriversSummary.put("xVals", xValsNames);
			vehicleDriversSummary.put("yVals", yVals);

			MapTools.safePut(res, "VEHICLE_DRIVERS_SUMMARY_TOTAL", totalSummary.intValue());
			MapTools.safePut(res, "VEHICLE_DRIVERS_SUMMARY_DATA", vehicleDriversSummary);
		}
		return res;
	}

	private HashMap<Object, Object> processFaultData(EntityResult erFault) {
		HashMap<Object, Object> incidentData = new HashMap<>();
		List<Object> xVals = new ArrayList<>();
		List<String> xValsNames = new ArrayList<>();
		ArrayList<Float> yVals = new ArrayList<>();
		if (erFault.calculateRecordNumber() > 0) {
			for (int i = 0; i <= (erFault.calculateRecordNumber() - 1); i++) {
				Hashtable recordValues = erFault.getRecordValues(i);
				Object obType = recordValues.get("TPFALLO");
				if (xVals.contains(obType)) {
					int indexToReplace = xVals.indexOf(obType);
					Float oldValue = yVals.get(indexToReplace);
					yVals.remove(indexToReplace);
					yVals.add(indexToReplace, oldValue + 1);
				} else {
					xVals.add(obType);
					String dscrInc = (String) recordValues.get("DSCR");
					dscrInc = dscrInc.replace("_", " ").toLowerCase();
					xValsNames.add(dscrInc);
					yVals.add((float) 1);
				}
			}
			incidentData.put("xVals", xValsNames);
			incidentData.put("yVals", yVals);
			return incidentData;
		}
		return null;
	}

}