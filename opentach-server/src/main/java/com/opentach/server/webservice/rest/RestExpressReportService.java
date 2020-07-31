package com.opentach.server.webservice.rest;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
import com.opentach.server.report.ReportService;
import com.opentach.server.webservice.utils.OpentachWSException;
import com.opentach.ws.common.rest.beans.RestDriverLogginRequest;
import com.opentach.ws.common.rest.beans.RestLogginResponse;
import com.utilmize.tools.exception.UException;

public class RestExpressReportService {

	private static final Logger	logger	= LoggerFactory.getLogger(RestExpressReportService.class);

	@Resource
	private WebServiceContext	context;

	////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////// EXPRESS REPORT /////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////

	@POST
	@Path("/expressMobileReport")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	public RestLogginResponse expressMobileReport(@WebParam(name = "logginAndDriver") final RestDriverLogginRequest request) throws OpentachWSException {
		try {
			return (RestLogginResponse) new OntimizeSessionConnectionTemplate(request.getUsrLogin(), request.getUsrPsswd()) {

				@Override
				protected RestLogginResponse doTask(Connection con) throws UException {
					try {
						Company company = RestServiceUtils.getCompany(request, this.getSessionId());
						Date analizeFrom = RestServiceUtils.getFilterDate();
						Date analizeTo = DateTools.lastMilisecond(RestServiceUtils.getFilterDateNow());

						ReportService serviceImpl = RestServiceUtils.getLocator().getService(ReportService.class);
						Map<String, Object> expressReportData = serviceImpl.generateMobileExpressReport(company.getCif(), request.getIdDriver(), analizeFrom, analizeTo);

						HashMap<Object, Object> res = new HashMap<>();
						res.putAll(expressReportData);
						return new RestLogginResponse(this.getSessionId(), res);
					} catch (Exception ex) {
						throw new UException("E_GETTING_EXPRESS_MOBILE_REPORT_DATA", ex);
					}
				}

			}.execute(RestServiceUtils.getLocator(), true);
		} catch (Exception e) {
			RestExpressReportService.logger.error(null, e);
			return new RestLogginResponse(-1);
		}
	}

}