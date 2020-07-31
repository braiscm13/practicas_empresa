package com.opentach.server.webservice.rest;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.SearchValue;
import com.ontimize.jee.common.tools.DateTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.MapTools;
import com.opentach.common.user.Company;
import com.opentach.common.user.IUserData;
import com.opentach.server.AbstractOpentachServerLocator;
import com.opentach.ws.common.rest.beans.RestLogginRequest;

public class RestServiceUtils {

	public static AbstractOpentachServerLocator getLocator() {
		return AbstractOpentachServerLocator.getLocator();
	}

	public static Entity getEntity(String entityName) {
		return RestServiceUtils.getLocator().getEntityReferenceFromServer(entityName);
	}

	/*
	 * Old version, it only supports one company
	 * */
	public static Company getCompany(int sessionId) throws Exception {
		IUserData userData = RestServiceUtils.getLocator().getUserData(sessionId);
		if (userData != null) {
			List<Company> companies = userData.getAllCompanies();
			if ((companies != null) && (companies.size() > 0)) {
				return companies.get(0);
			}
		}
		throw new Exception("E_NO_COMPANY");
	}

	public static List<Company> getCompanies(int sessionId) throws Exception {
		IUserData userData = RestServiceUtils.getLocator().getUserData(sessionId);
		if (userData != null) {
			List<Company> companies = userData.getAllCompanies();
			if ((companies != null) && (companies.size() > 0)) {
				return companies;
			}
		}
		throw new Exception("E_NO_COMPANIES");
	}

	public static Company getCompany(Object cif, int sessionId) throws Exception {
		IUserData userData = RestServiceUtils.getLocator().getUserData(sessionId);
		if (userData != null) {
			List<Company> companies = userData.getAllCompanies();
			for (Company company : companies) {
				if (company.getCif().equals(cif)) {
					return company;
				}
			}
		}
		throw new Exception("E_NO_COMPANY");
	}

	public static Company getCompany(RestLogginRequest logginRequest, int sessionId) throws Exception {
		if ((logginRequest.getMap() == null) || !logginRequest.getMap().containsKey("CIF")) {
			return RestServiceUtils.getCompany(sessionId);
		} else {
			return RestServiceUtils.getCompany(logginRequest.getMap().get("CIF"), sessionId);
		}
	}

	public static Date getFilterDateNow() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(DateTools.firstMilisecond(new Date()));
		return cal.getTime();
	}

	public static Date getFilterDate() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(RestServiceUtils.getFilterDateNow());
		cal.add(Calendar.DAY_OF_YEAR, -28);
		return cal.getTime();
	}

	public static HashMap<Object, Object> getVehicleIncidentData(String cif, Object activeContract, Date filterDate, Object matricula, int sessionId) throws Exception {
		Entity entityIncidentes = RestServiceUtils.getEntity("EIncidentes");
		Hashtable<Object, Object> kv = EntityResultTools.keysvalues("CIF", cif, "CG_CONTRATO", activeContract, "FECINI", new SearchValue(SearchValue.MORE_EQUAL, filterDate));
		MapTools.safePut(kv, "MATRICULA", matricula);
		EntityResult erIncident = entityIncidentes.query(kv, EntityResultTools.attributes("TIPO", "TIPO_DSCR_INC"), sessionId);

		List<Object> xVals = new ArrayList<>();
		List<String> xValsNames = new ArrayList<>();
		ArrayList<Float> yVals = new ArrayList<>();
		for (int i = 0; i <= (erIncident.calculateRecordNumber() - 1); i++) {
			Hashtable recordValues = erIncident.getRecordValues(i);
			Object obType = recordValues.get("TIPO");
			if (xVals.contains(obType)) {
				int indexToReplace = xVals.indexOf(obType);
				Float oldValue = yVals.get(indexToReplace);
				yVals.remove(indexToReplace);
				yVals.add(indexToReplace, oldValue + 1);
			} else {
				xVals.add(obType);
				String dscrInc = (String) recordValues.get("TIPO_DSCR_INC");
				dscrInc = dscrInc.replace("_", " ").toLowerCase();
				xValsNames.add(dscrInc);
				yVals.add((float) 1);
			}
		}
		HashMap<Object, Object> incidentData = new HashMap<>();
		incidentData.put("xVals", xValsNames);
		incidentData.put("yVals", yVals);

		HashMap<Object, Object> res = new HashMap<>();
		int totalIncident = erIncident.calculateRecordNumber();
		if (totalIncident > 0) {
			if (matricula == null) {
				MapTools.newMap(res, "TOTAL_INCIDENT", totalIncident, "INCIDENT_DATA", incidentData);
				MapTools.safePut(res, "INCIDENT", totalIncident);
			} else {
				MapTools.newMap(res, "VEHICLE_TOTAL_INCIDENT", totalIncident, "VEHICLE_INCIDENT_DATA", incidentData);
			}
		}
		return res;
	}

	public static HashMap<Object, Object> getVehicleNextCalibradoData(String cif, Object activeContract, Object matricula, int sessionId) throws Exception {
		Entity entityProxCalibrado = RestServiceUtils.getEntity("EInformeProxCalibrado");
		Hashtable<Object, Object> kv = EntityResultTools.keysvalues("CIF", cif, "CG_CONTRATO", activeContract);
		MapTools.safePut(kv, "MATRICULA", matricula);
		EntityResult erProxCalibradoTmp = entityProxCalibrado.query(kv, EntityResultTools.attributes("MATRICULA", "DSCR", "FEC_PROXIMO", "NOMBRE_EMPRESA"), sessionId);
		EntityResult erProxCalibrado = new EntityResult();

		for (int i = 0; i <= (erProxCalibradoTmp.calculateRecordNumber() - 1); i++) {
			Hashtable recordValues = erProxCalibradoTmp.getRecordValues(i);
			Object fecProx = recordValues.get("FEC_PROXIMO");
			try {
				Date parse = new SimpleDateFormat("dd/MM/yyyy").parse((String) fecProx);
				recordValues.put("FEC_PROXIMO", parse);
				erProxCalibrado.addRecord(recordValues);
			} catch (Exception ex) {
			}
		}
		HashMap<Object, Object> res = new HashMap<>();
		if (erProxCalibrado.calculateRecordNumber() > 0) {
			erProxCalibrado = EntityResultTools.doSort(erProxCalibrado, "FEC_PROXIMO");
			Hashtable recordValues = erProxCalibrado.getRecordValues(0);
			if (matricula == null) {
				MapTools.safePut(res, "renovation_tach_value", recordValues.get("MATRICULA"));
				MapTools.safePut(res, "renovation_tach_date", new SimpleDateFormat("dd/MM/yyyy").format(recordValues.get("FEC_PROXIMO")));
			} else {
				MapTools.safePut(res, "RENOVATION_VEHICLE_TACH_VALUE", recordValues.get("MATRICULA"));
				MapTools.safePut(res, "RENOVATION_VEHICLE_TACH_DATE", new SimpleDateFormat("dd/MM/yyyy").format(recordValues.get("FEC_PROXIMO")));
			}
		}
		return res;
	}

	public static HashMap<Object, Object> getDriverInfractionData(String cif, Object activeContract, Date fromTime, Date toTime, Object driverId, int sessionId) throws Exception {
		HashMap<Object, Object> res = new HashMap<>();

		Entity entityInformeInfrac = RestServiceUtils.getEntity("EInformeInfrac");

		int leve = 0;
		int grave = 0;
		int muyGrave = 0;
		Hashtable<Object, Object> kv = EntityResultTools.keysvalues("CIF", cif, "CG_CONTRATO", activeContract, "FILTERFECINI", fromTime, "FILTERFECFIN", toTime);
		MapTools.safePut(kv, "IDCONDUCTOR", driverId);
		EntityResult erInfrac = entityInformeInfrac.query(kv,
				EntityResultTools.attributes("CG_CONTRATO", "NUMREQ", "IDCONDUCTOR", "DNI", "NOMBRE", "APELLIDOS", "IDDELEGACION", "DSCR_COND", "CIF", "NOMEMP", "FECHORAINI",
						"FECHORAFIN", "NATURALEZA", "IMPORTE", "BARE1", "BARE2", "BARE34", "BARE56", "COD_BAREMO", "LEVE", "GRAVE", "MUYGRAVE", "TIPO", "TIPO_DSCR",
						"DSCR_TIPO_INFRAC", "TCP", "EXCON", "TDP", "FADES"),
				sessionId);
		if (erInfrac.calculateRecordNumber() > 0) {

			for (String attr : Arrays.asList("FECHORAINI", "FECHORAFIN")) {
				erInfrac.put(attr, RestServiceUtils.getFormatDate((Vector) erInfrac.get(attr)));
			}

			leve += RestServiceUtils.calculateOccurences((Vector<Number>) erInfrac.get("LEVE"));
			grave += RestServiceUtils.calculateOccurences((Vector<Number>) erInfrac.get("GRAVE"));
			muyGrave += RestServiceUtils.calculateOccurences((Vector<Number>) erInfrac.get("MUYGRAVE"));
			HashMap<Object, Object> infractionData = new HashMap<>();
			if ((leve + grave + muyGrave) > 0) {
				infractionData.put("xVals", Arrays.asList("leve", "grave", "muy grave"));
				infractionData.put("yVals", Arrays.asList((float) leve, (float) grave, (float) muyGrave));
			}

			int totalInfraction = leve + grave + muyGrave;
			if (driverId == null) {
				MapTools.safePut(res, "TOTAL_INFRACTION", totalInfraction);
				MapTools.safePut(res, "INFRACTION_DATA", infractionData);
				MapTools.safePut(res, "INFRACTION", totalInfraction);

				EntityResult doGroup = EntityResultTools.doGroup(erInfrac, new String[] { "IDCONDUCTOR", "NOMBRE", "APELLIDOS", "DNI" });
				MapTools.safePut(res, "INFRACTION_GROUP_DATA", EntityResultTools.doSort(doGroup, "APELLIDOS"));
				MapTools.safePut(res, "INFRACTION_COMPLETE_DATA", erInfrac);
			} else {
				MapTools.safePut(res, "DRIVER_TOTAL_INFRACTION", totalInfraction);
				MapTools.safePut(res, "DRIVER_INFRACTION_DATA", infractionData);
				MapTools.safePut(res, "DRIVER_INFRACTION_COMPLETE_DATA", erInfrac);
			}
		}
		return res;
	}

	public static Vector getFormatDate(Vector dates) {
		Vector vector = new Vector<>(dates.size());
		int i = 0;
		for (Object date : dates) {
			vector.add(i++, RestServiceUtils.getFormatDate(date));
		}
		return vector;
	}

	public static HashMap<Object, Object> getDriverExpiredDateTarj(String cif, Object activeContract, Object idConductor, int sessionId) throws Exception {
		Entity entityConductoresEmp = RestServiceUtils.getEntity("EConductoresEmp");
		Hashtable<Object, Object> kv = EntityResultTools.keysvalues("CIF", cif, "CG_CONTRATO", activeContract);
		MapTools.safePut(kv, "IDCONDUCTOR", idConductor);
		EntityResult erExpiredDateTarjTmp = entityConductoresEmp.query(kv,
				EntityResultTools.attributes("IDCONDUCTOR", "CIF", "DNI", "NOMBRE", "APELLIDOS", "EMAIL", "EXPIRED_DATE_TRJCONDU", "NOMBRE_EMPRESA"), sessionId);
		EntityResult erExpiredDateTarj = new EntityResult();

		for (int i = 0; i <= (erExpiredDateTarjTmp.calculateRecordNumber() - 1); i++) {
			Hashtable recordValues = erExpiredDateTarjTmp.getRecordValues(i);
			Object fecProx = recordValues.get("EXPIRED_DATE_TRJCONDU");
			if (fecProx instanceof String) {
				try {
					Date parse = new SimpleDateFormat("dd/MM/yyyy").parse((String) fecProx);
					recordValues.put("EXPIRED_DATE_TRJCONDU", parse);
					erExpiredDateTarj.addRecord(recordValues);
				} catch (Exception ex) {
				}
			} else if (fecProx != null) {
				erExpiredDateTarj.addRecord(recordValues);
			}
		}

		HashMap<Object, Object> res = new HashMap<>();
		if (erExpiredDateTarj.calculateRecordNumber() > 0) {
			erExpiredDateTarj = EntityResultTools.doSort(erExpiredDateTarj, "EXPIRED_DATE_TRJCONDU");
			Hashtable recordValues = erExpiredDateTarj.getRecordValues(0);
			Date expirationDate = (Date) recordValues.get("EXPIRED_DATE_TRJCONDU");
			if (idConductor == null) {
				MapTools.safePut(res, "next_card_expiration_value", recordValues.get("IDCONDUCTOR"));
				MapTools.safePut(res, "next_card_expiration_date", new SimpleDateFormat("dd/MM/yyyy").format(expirationDate));
			} else {
				long countDaysBetween;
				try {
					countDaysBetween = DateTools.countDaysBetween(new Date(), expirationDate);
				} catch (IllegalArgumentException e) {
					// It's expired
					countDaysBetween = DateTools.countDaysBetween(expirationDate, new Date());
				}
				MapTools.safePut(res, "DRIVER_CARD_EXPIRATION_DATE", RestServiceUtils.getFormatDate(expirationDate));
				MapTools.safePut(res, "DRIVER_CARD_EXPIRATION_DAYS", countDaysBetween);
			}

		}
		return res;
	}

	public static String getFormatDate(Object date) {
		String fDate = "";
		if (date instanceof Timestamp) {
			fDate = new SimpleDateFormat("dd/MM/yyyy").format(date);
		} else if (date instanceof Date) {
			fDate = new SimpleDateFormat("dd/MM/yyyy").format(date);
		}
		return fDate;
	}

	public static int calculateOccurences(Vector<Number> numbers) {
		int occurences = 0;
		for (Number number : numbers) {
			occurences += number.equals(1) ? 1 : 0;
		}
		return occurences;
	}

}
