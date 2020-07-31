package com.opentach.server.checktrucker.webservice.rest;

import java.sql.Connection;
import java.util.Arrays;
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

import com.ontimize.db.EntityResult;
import com.ontimize.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.db.SQLStatementBuilder.BasicField;
import com.ontimize.db.SQLStatementBuilder.BasicOperator;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.MapTools;
import com.opentach.common.user.Company;
import com.opentach.server.AbstractOpentachServerLocator;
import com.opentach.server.checktrucker.services.SurveyReportService;
import com.opentach.server.webservice.rest.OntimizeSessionConnectionTemplate;
import com.opentach.server.webservice.rest.RestServiceUtils;
import com.opentach.server.webservice.utils.OpentachWSException;
import com.opentach.ws.common.rest.beans.RestLogginRequest;
import com.opentach.ws.common.rest.beans.RestLogginResponse;
import com.utilmize.tools.exception.UException;

public class RestGraphicsSurveyService {

	private static final Logger	logger	= LoggerFactory.getLogger(RestGraphicsSurveyService.class);

	@Resource
	private WebServiceContext	context;

	////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////// Graphic Surveys ////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////

	@POST
	@Path("/searchOkVSKo")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	public RestLogginResponse searchGraphicSurveysOkVSKo(@WebParam(name = "loggin") final RestLogginRequest request) throws OpentachWSException {
		try {
			return (RestLogginResponse) new OntimizeSessionConnectionTemplate(request.getUsrLogin(), request.getUsrPsswd()) {

				@Override
				protected Object doTask(Connection con) throws UException {
					try {
						int sessionId = this.getSessionId();
						HashMap<Object, Object> dataOkVSKoForCompany = RestGraphicsSurveyService.this.getDataOkVSKoForCompany(request, sessionId);
						return new RestLogginResponse(sessionId, dataOkVSKoForCompany);
					} catch (Exception ex) {
						throw new UException(ex);
					}
				}

			}.execute(AbstractOpentachServerLocator.getLocator(), true);
		} catch (Exception e) {
			RestGraphicsSurveyService.logger.error(e.getMessage());
			return new RestLogginResponse(-1);
		}
	}

	@POST
	@Path("/searchOkKoBySurvey")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	public RestLogginResponse searchGraphicSurveysOkKoBySurvey(@WebParam(name = "loggin") final RestLogginRequest request) throws OpentachWSException {
		try {
			return (RestLogginResponse) new OntimizeSessionConnectionTemplate(request.getUsrLogin(), request.getUsrPsswd()) {

				@Override
				protected Object doTask(Connection con) throws UException {
					try {
						int sessionId = this.getSessionId();
						HashMap<Object, Object> dataOkVSKoForCompany = RestGraphicsSurveyService.this.getDataOkKoForCompanyBySurvey(request, sessionId);
						return new RestLogginResponse(sessionId, dataOkVSKoForCompany);
					} catch (Exception ex) {
						throw new UException(ex);
					}
				}

			}.execute(AbstractOpentachServerLocator.getLocator(), true);
		} catch (Exception e) {
			RestGraphicsSurveyService.logger.error(e.getMessage());
			return new RestLogginResponse(-1);
		}
	}

	@POST
	@Path("/searchDriversOkKo")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	public RestLogginResponse searchGraphicSurveysDriverOkKo(@WebParam(name = "loggin") final RestLogginRequest request) throws OpentachWSException {
		try {
			return (RestLogginResponse) new OntimizeSessionConnectionTemplate(request.getUsrLogin(), request.getUsrPsswd()) {

				@Override
				protected Object doTask(Connection con) throws UException {
					try {
						int sessionId = this.getSessionId();
						HashMap<Object, Object> dataDriverOkKoForCompany = RestGraphicsSurveyService.this.getDataDriversForCompany(request, sessionId);
						return new RestLogginResponse(sessionId, dataDriverOkKoForCompany);
					} catch (Exception ex) {
						throw new UException(ex);
					}
				}

			}.execute(AbstractOpentachServerLocator.getLocator(), true);
		} catch (Exception e) {
			RestGraphicsSurveyService.logger.error(e.getMessage());
			return new RestLogginResponse(-1);
		}
	}

	protected HashMap<Object, Object> getDataOkVSKoForCompany(RestLogginRequest request, int sessionId) throws Exception {
		HashMap<Object, Object> data = new HashMap<>();
		Company company = RestServiceUtils.getCompany(request, sessionId);
		SurveyReportService surveyReportService = RestServiceUtils.getLocator().getService(SurveyReportService.class);

		BasicExpression filter = this.getCIFFilterCorrectVsWrong(company.getCif());
		EntityResult erSurveyReport = surveyReportService.getCompanyCorrectVSWrong(filter, null, sessionId);

		CheckingTools.failIf(erSurveyReport.calculateRecordNumber() == 0, "E_NO_DATA_FOUND");

		Hashtable<String, Object> recordValues1 = erSurveyReport.getRecordValues(0);

		Number valueCorrect = (Number) recordValues1.get("CORRECT");
		Number valueWrong = (Number) recordValues1.get("WRONG");

		List<String> xVals = Arrays.asList("sur.CORRECT", "sur.WRONG");
		List<Number> yVals = Arrays.asList(valueCorrect, valueWrong);

		/* Driver data */
		MapTools.safePut(data, "xVals", xVals);
		MapTools.safePut(data, "yVals", yVals);
		MapTools.safePut(data, "total", valueCorrect.intValue() + valueWrong.intValue());
		return data;
	}

	protected HashMap<Object, Object> getDataOkKoForCompanyBySurvey(RestLogginRequest request, int sessionId) throws Exception {
		HashMap<Object, Object> data = new HashMap<>();
		Company company = RestServiceUtils.getCompany(request, sessionId);
		SurveyReportService surveyReportService = RestServiceUtils.getLocator().getService(SurveyReportService.class);

		BasicExpression filter = this.getCIFFilter(company.getCif());
		EntityResult erSurveyReport = surveyReportService.getCompanyBySurveyCorrectWrong(filter, null, sessionId);

		CheckingTools.failIf(erSurveyReport.calculateRecordNumber() == 0, "E_NO_DATA_FOUND");

		List<String> xAxis = (List<String>) erSurveyReport.get("XAXIS");
		List<Number> yAxis = (List<Number>) erSurveyReport.get("YAXIS");
		List<String> serie = (List<String>) erSurveyReport.get("SERIE");

		/* Driver data */
		MapTools.safePut(data, "xVals", xAxis);
		MapTools.safePut(data, "yVals", yAxis);
		MapTools.safePut(data, "serieVals", serie);
		return data;
	}

	protected HashMap<Object, Object> getDataDriversForCompany(RestLogginRequest request, int sessionId) throws Exception {
		HashMap<Object, Object> data = new HashMap<>();
		Company company = RestServiceUtils.getCompany(request, sessionId);
		SurveyReportService surveyReportService = RestServiceUtils.getLocator().getService(SurveyReportService.class);

		BasicExpression filter = this.getCIFFilter(company.getCif());
		EntityResult erSurveyReport = surveyReportService.getCompanyCorrectWrongDrivers(filter, null, sessionId);

		CheckingTools.failIf(erSurveyReport.calculateRecordNumber() == 0, "E_NO_DATA_FOUND");

		/* Driver data */
		MapTools.safePut(data, "DATA", erSurveyReport);
		return data;
	}

	private BasicExpression getCIFFilter(String cif) {
		BasicExpression b1 = new BasicExpression(new BasicField("con.CIF"), BasicOperator.EQUAL_OP, cif);
		BasicExpression b2 = new BasicExpression(new BasicField("pers.CIF"), BasicOperator.EQUAL_OP, cif);
		return new BasicExpression(b1, BasicOperator.OR_OP, b2);
	}

	private BasicExpression getCIFFilterCorrectVsWrong(String cif) {
		return new BasicExpression(new BasicField("CIF"), BasicOperator.EQUAL_OP, cif);
	}
}