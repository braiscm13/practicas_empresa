package com.opentach.server.webservice.rest;

import java.sql.Connection;
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

import com.opentach.common.user.Company;
import com.opentach.server.OpentachServerLocator;
import com.opentach.server.webservice.utils.OpentachWSException;
import com.opentach.ws.common.rest.beans.RestLogginRequest;
import com.opentach.ws.common.rest.beans.RestLogginResponse;
import com.utilmize.tools.exception.UException;

public class RestLoginService {

	private static final Logger	logger	= LoggerFactory.getLogger(RestLoginService.class);

	@Resource
	private WebServiceContext	context;

	////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////// LOGGING ////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////

	@POST
	@Path("/validateUserAndStartSession")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	public RestLogginResponse validateUserAndStartSession(@WebParam(name = "loggin") final RestLogginRequest request) throws OpentachWSException {
		try {
			return (RestLogginResponse) new OntimizeSessionConnectionTemplate(request.getUsrLogin(), request.getUsrPsswd()) {

				@Override
				protected Object doTask(Connection con) throws UException {
					try {
						return RestLoginService.this.validateUserAndStartSessionInternal(request, this.getSessionId());
					} catch (Exception ex) {
						throw new UException(ex);
					}
				}

			}.execute(OpentachServerLocator.getLocator(), true);
		} catch (Exception e) {
			RestLoginService.logger.error(null, e);
			return new RestLogginResponse(-1);
		}
	}

	private Object validateUserAndStartSessionInternal(RestLogginRequest logginRequest, int sessionId) throws Exception {
		// By default to keep compatiblity we return data from first company
		List<Company> companies = RestServiceUtils.getCompanies(sessionId);
		return new RestLogginResponse(sessionId, RestCompanyService.getCompanyDataForUser(companies.get(0), logginRequest.getMap() == null, sessionId));
	}

}