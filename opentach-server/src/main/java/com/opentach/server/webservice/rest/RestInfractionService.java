package com.opentach.server.webservice.rest;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.jws.WebParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.xml.ws.WebServiceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.tools.DateTools;
import com.opentach.common.user.Company;
import com.opentach.server.OpentachServerLocator;
import com.opentach.server.webservice.utils.OpentachWSException;
import com.opentach.ws.common.rest.beans.RestLogginRequest;
import com.opentach.ws.common.rest.beans.RestLogginResponse;
import com.utilmize.tools.exception.UException;

public class RestInfractionService {

	private static final Logger	logger	= LoggerFactory.getLogger(RestInfractionService.class);

	@Resource
	private WebServiceContext	context;

	////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////// DRIVER /////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////

	@POST
	@Path("/searchInfractions")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	public RestLogginResponse searchInfractions(@WebParam(name = "loggin") final RestLogginRequest request) throws OpentachWSException {
		try {
			return (RestLogginResponse) new OntimizeSessionConnectionTemplate(request.getUsrLogin(), request.getUsrPsswd()) {

				@Override
				protected Object doTask(Connection con) throws UException {
					try {
						return RestInfractionService.this.searchInfractions(request, this.getSessionId());
					} catch (Exception ex) {
						throw new UException(ex);
					}
				}

			}.execute(OpentachServerLocator.getLocator(), true);
		} catch (Exception e) {
			RestInfractionService.logger.error(e.getMessage());
			return new RestLogginResponse(-1);
		}
	}

	private Object searchInfractions(RestLogginRequest logginRequest, int sessionId) throws Exception {
		Company company = RestServiceUtils.getCompany(logginRequest, sessionId);

		// Filters
		Calendar cal = Calendar.getInstance();
		cal.setTime(DateTools.firstMilisecond(new Date()));
		Date timeNow = cal.getTime();
		Date filterDate = RestServiceUtils.getFilterDate();

		String cif = company.getCif();
		Object activeContract = company.getActiveContract();

		Object idConductor = logginRequest.getMap().get("IDCONDUCTOR");
		if ((idConductor != null) &&
				(
						((idConductor instanceof Number) && (((Number)idConductor).intValue() == -1))
						||
						((idConductor instanceof String) && ((String)idConductor).equals("-1")
								))){
			idConductor = null;
		}
		return new RestLogginResponse(sessionId, this.getDriverInfractions(cif, activeContract, filterDate, timeNow, idConductor, sessionId));
	}

	private HashMap<Object, Object> getDriverInfractions(String cif, Object activeContract, Date filterDate, Date timeNow, Object idConductor, int sessionId) throws Exception {
		HashMap<Object, Object> data = new HashMap<>();

		data.putAll(RestServiceUtils.getDriverInfractionData(cif, activeContract, filterDate, timeNow, idConductor, sessionId));

		return data;
	}

}