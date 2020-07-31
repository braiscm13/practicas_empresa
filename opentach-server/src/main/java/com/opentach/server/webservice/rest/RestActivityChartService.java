package com.opentach.server.webservice.rest;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
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
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.user.Company;
import com.opentach.server.webservice.utils.OpentachWSException;
import com.opentach.ws.common.rest.beans.RestLogginRequest;
import com.opentach.ws.common.rest.beans.RestLogginResponse;
import com.utilmize.tools.exception.UException;

public class RestActivityChartService {

	private static final Logger	logger	= LoggerFactory.getLogger(RestActivityChartService.class);
	@Resource
	private WebServiceContext	context;

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@POST
	@Path("/activityChartDriver")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	public RestLogginResponse activityChartDriver(@WebParam(name = "loggin") final RestLogginRequest request) throws OpentachWSException {
		try {
			return (RestLogginResponse) new OntimizeSessionConnectionTemplate(request.getUsrLogin(), request.getUsrPsswd()) {

				@Override
				protected Object doTask(Connection con) throws UException {
					try {
						return RestActivityChartService.this.activityChartDriver(request, this.getSessionId());
					} catch (Exception ex) {
						throw new UException(ex);
					}
				}

			}.execute(RestServiceUtils.getLocator(), true);
		} catch (Exception e) {
			RestActivityChartService.logger.error(null, e);
			return new RestLogginResponse(-1);
		}

	}

	public RestLogginResponse activityChartDriver(RestLogginRequest request, int sessionId) throws Exception {
		Company company = RestServiceUtils.getCompany(request, sessionId);

		Entity ent = RestServiceUtils.getEntity("EInformeActivCond");
		Object idConductor = request.getMap().get("IDCONDUCTOR");
		Object activeContract = company.getActiveContract();
		CheckingTools.failIfNull(idConductor, "E_NO_DRIVER");
		// Filters
		Calendar cal = Calendar.getInstance();
		cal.setTime(DateTools.firstMilisecond(new Date()));
		Date timeNow = cal.getTime();
		Date filterDate = RestServiceUtils.getFilterDate();

		Hashtable<String, Object> cv = new Hashtable<>();
		cv.put(OpentachFieldNames.NUMREQ_FIELD, activeContract);
		cv.put(OpentachFieldNames.IDCONDUCTOR_FIELD, idConductor);
		Vector<Object> v = new Vector<>(Arrays.asList(new Object[] { filterDate, timeNow }));
		cv.put(OpentachFieldNames.FECINI_FIELD, new SearchValue(SearchValue.BETWEEN, v));
		cv.put(OpentachFieldNames.FECFIN_FIELD, new SearchValue(SearchValue.BETWEEN, v));

		// para que traiga los periodos tb
		cv.put("PERIODOS", "1");

		EntityResult er = ent.query(cv, EntityResultTools.attributes("TPACTIVIDAD", "FECINI", "FECFIN", "MATRICULA", "MINUTOS", "TRANS_TREN", "ORIGEN", "REGIMEN",
				"ESTADO_TRJ_RANURA", "FUERA_AMBITO", "IDCONDUCTOR", "NUMREQ", "DSCR_ACT"), sessionId);
		HashMap<Object, Object> res = new HashMap<>();
		if (er.calculateRecordNumber() > 0) {
			EntityResult ractiv = (EntityResult) ((Vector) er.get("ACTIVIDADES")).get(0);
			EntityResult periodosTrabajo = (EntityResult) ((Vector) er.get("PERIODOS")).get(0);
			res.put("ACTIVIDADES", ractiv);
			res.put("PERIODOS", periodosTrabajo);
		}
		// Infracciones
		Entity entityInformeInfrac = RestServiceUtils.getEntity("EInformeInfrac");
		Hashtable<Object, Object> kv = EntityResultTools.keysvalues("CIF", company.getCif(), "CG_CONTRATO", activeContract, "FILTERFECINI", filterDate, "FILTERFECFIN", timeNow,
				"IDCONDUCTOR", idConductor);
		EntityResult erInfrac = entityInformeInfrac.query(kv,
				EntityResultTools.attributes("MUYGRAVE", "LEVE", "GRAVE", "TIPO", "FECHORAINI", "FECHORAFIN", "TDP", "TCP", "EXCON", "FADES", "NATURALEZA"), sessionId);
		res.put("INFRACTIONS", erInfrac);
		return new RestLogginResponse(sessionId, res);
	}
}
