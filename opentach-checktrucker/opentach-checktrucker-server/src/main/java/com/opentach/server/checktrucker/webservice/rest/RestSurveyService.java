package com.opentach.server.checktrucker.webservice.rest;

import java.math.BigDecimal;
import java.util.ArrayList;
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

import com.opentach.common.surveys.OptionTO;
import com.opentach.common.surveys.QuestionTO;
import com.opentach.common.surveys.QuestionaryTO;
import com.opentach.common.surveys.ResultsSurvey;
import com.opentach.common.surveys.SurveyTO;
import com.opentach.server.AbstractOpentachServerLocator;
import com.opentach.server.checktrucker.services.SurveyService;
import com.opentach.server.checktrucker.webservice.utils.OpentachNoDriverException;
import com.opentach.server.webservice.utils.OpentachWSException;
import com.opentach.ws.common.rest.beans.Option;
import com.opentach.ws.common.rest.beans.Question;
import com.opentach.ws.common.rest.beans.QuestionList;
import com.opentach.ws.common.rest.beans.RestOptionsSend;
import com.opentach.ws.common.rest.beans.RestResponseOptions;
import com.opentach.ws.common.rest.beans.RestResponseQuestions;
import com.opentach.ws.common.rest.beans.RestResponseSurveys;
import com.opentach.ws.common.rest.beans.RestSurveyRequest;
import com.opentach.ws.common.rest.beans.RestSurveysRequest;
import com.opentach.ws.common.rest.beans.SendOptionsResponse;
import com.opentach.ws.common.rest.beans.SendOptionsResponseList;
import com.opentach.ws.common.rest.beans.Survey;
import com.opentach.ws.common.rest.beans.SurveyList;

public class RestSurveyService {

	private static final Logger	logger	= LoggerFactory.getLogger(RestSurveyService.class);

	@Resource
	private WebServiceContext	context;

	@POST
	@Path("/getSurveys")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	public RestResponseSurveys getSurveys(@WebParam(name = "dni") RestSurveysRequest request) throws OpentachWSException {
		try {
			SurveyService entity = AbstractOpentachServerLocator.getLocator().getService(SurveyService.class);
			List<SurveyTO> lSurveyOt = entity.getSurveys(request.getDni());
			RestResponseSurveys result = new RestResponseSurveys(this.convertEntityResultToSurveyList(lSurveyOt));
			return result;
		} catch (Exception err) {
			if (err.getCause() instanceof OpentachNoDriverException) {
				RestSurveyService.logger.error("E_DNI_NOT_EXISTS", err);
				return new RestResponseSurveys(-1, "E_DNI_NOT_EXISTS");
			}
			RestSurveyService.logger.error("E_GETTING_SURVEYS", err);
			return new RestResponseSurveys(-1, "E_GETTING_SURVEYS");
		}
	}

	@POST
	@Path("/getQuestions")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	public RestResponseQuestions getQuestions(@WebParam(name = "idSurvey") RestSurveyRequest survey) throws OpentachWSException {
		try {
			SurveyService entity = AbstractOpentachServerLocator.getLocator().getService(SurveyService.class);
			QuestionaryTO resQuery = entity.getSurvey(new BigDecimal(survey.getIdSurvey()));
			RestResponseQuestions result = new RestResponseQuestions(this.convertQuestionatyTOToQuestionList(resQuery));
			return result;
		} catch (Exception e) {
			RestSurveyService.logger.error("E_GETTING_SURVEYS", e);
			return new RestResponseQuestions(-1, "E_GETTING_SURVEYS");
		}
	}

	@POST
	@Path("/sendOptions")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	public RestResponseOptions sendOptionsSurvey(@WebParam(name = "sendOptions") RestOptionsSend survey) throws OpentachWSException {
		try {
			SurveyService eSurvey = AbstractOpentachServerLocator.getLocator().getService(SurveyService.class);
			String idSurvey = survey.getIdSurvey();
			if (idSurvey == null) {
				RestSurveyService.logger.error("E_NO_ID_SURVEY");
				throw new OpentachWSException(-1, "E_SEND_OPTIONS_SURVEY");
			}
			String dni = survey.getDni();
			if (dni == null) {
				RestSurveyService.logger.error("E_NO_DNI");
				throw new OpentachWSException(-1, "E_SEND_OPTIONS_SURVEY");
			}
			String uuid = survey.getUuid();
			if (uuid == null) {
				RestSurveyService.logger.error("E_NO_UUID");
				throw new OpentachWSException(-1, "E_SEND_OPTIONS_SURVEY");
			}

			ResultsSurvey resultSurvey = eSurvey.sendResponses(new BigDecimal(idSurvey), dni, uuid, survey.getOptions());

			RestResponseOptions result = this.convertResultsSurveyToResponseOptionsSended(resultSurvey);
			return result;
		} catch (Exception e) {
			RestSurveyService.logger.error("E_SEND_OPTIONS_SURVEY", e);
			return new RestResponseOptions(-1, "E_SEND_OPTIONS_SURVEY");
		}
	}

	private RestResponseOptions convertResultsSurveyToResponseOptionsSended(ResultsSurvey resultSurvey) {
		SendOptionsResponseList sendOptionsResponseList = new SendOptionsResponseList();
		sendOptionsResponseList.addSendOptionsResponse(new SendOptionsResponse(resultSurvey.getCorrect(), resultSurvey.getWrong()));
		return new RestResponseOptions(sendOptionsResponseList);
	}

	private QuestionList convertQuestionatyTOToQuestionList(QuestionaryTO resQuery) {
		QuestionList questionList = new QuestionList();
		for (QuestionTO questionTO : resQuery.getQuestions()) {
			List<Option> lOptions = new ArrayList<>();
			for (OptionTO optionTO : questionTO.getOptions()) {
				Option option = new Option(optionTO.getIdOption().toString(), optionTO.getOptionText());
				lOptions.add(option);
			}
			Question question = new Question(questionTO.getId().toString(), questionTO.getTitle());
			question.setOptions(lOptions);
			questionList.addQuestion(question);
		}
		return questionList;
	}

	private SurveyList convertEntityResultToSurveyList(List<SurveyTO> resQuery) throws Exception {
		SurveyList surveyList = new SurveyList();
		for (SurveyTO survey : resQuery) {
			Survey station = this.buildSurvey(survey);
			if (station != null) {
				surveyList.addSurvey(station);
			}
		}
		return surveyList;
	}

	private Survey buildSurvey(SurveyTO survey) {
		Number id = survey.getId();
		String name = survey.getName();
		Integer correct = survey.getCorrectQuestions();
		Integer total = survey.getTotalQuestions();
		return new Survey(id.toString(), name, correct, total);
	}

}