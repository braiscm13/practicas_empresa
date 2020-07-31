package com.opentach.server.proxy.checktrucker.restcontrollers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.opentach.ws.common.rest.beans.IRestResponse;
import com.opentach.ws.common.rest.beans.RestOptionsSend;
import com.opentach.ws.common.rest.beans.RestResponseOptions;
import com.opentach.ws.common.rest.beans.RestResponseQuestions;
import com.opentach.ws.common.rest.beans.RestResponseSurveys;
import com.opentach.ws.common.rest.beans.RestSurveyRequest;
import com.opentach.ws.common.rest.beans.RestSurveysRequest;

@RestController
@RequestMapping("openservices/services")
public class CheckTruckerRestController {
	private static final String	E_DNI_NOT_EXISTS		= "E_DNI_NOT_EXISTS";

	private static final String	E_GETTING_SURVEYS		= "E_GETTING_SURVEYS";

	private static final String	E_SEND_OPTIONS_SURVEY	= "E_SEND_OPTIONS_SURVEY";

	private static final Logger	logger					= LoggerFactory.getLogger(CheckTruckerRestController.class);

	@Value("${app.redirections.checkTruckerService}")
	private List<String>		redirections;

	@RequestMapping(value = "/restSurveyService/getSurveys",
			method = RequestMethod.POST,
			consumes = { MediaType.APPLICATION_JSON_VALUE },
			produces = { MediaType.APPLICATION_JSON_VALUE })
	public RestResponseSurveys getSurveys(@RequestBody RestSurveysRequest request) {
		return this.doSend(RestResponseSurveys.class, request, "/restSurveyService/getSurveys", new RestResponseSurveys(-1, CheckTruckerRestController.E_GETTING_SURVEYS));
	}

	@RequestMapping(value = "/restSurveyService/getQuestions",
			method = RequestMethod.POST,
			consumes = { MediaType.APPLICATION_JSON_VALUE },
			produces = { MediaType.APPLICATION_JSON_VALUE })
	public RestResponseQuestions getQuestions(@RequestBody RestSurveyRequest survey) {
		return this.doSend(RestResponseQuestions.class, survey, "/restSurveyService/getQuestions", new RestResponseQuestions(-1, CheckTruckerRestController.E_GETTING_SURVEYS));
	}

	@RequestMapping(value = "/restSurveyService/sendOptions",
			method = RequestMethod.POST,
			consumes = { MediaType.APPLICATION_JSON_VALUE },
			produces = { MediaType.APPLICATION_JSON_VALUE })
	public RestResponseOptions sendOptionsSurvey(@RequestBody RestOptionsSend survey) {
		return this.doSend(RestResponseOptions.class, survey, "/restSurveyService/sendOptions", new RestResponseOptions(-1, CheckTruckerRestController.E_SEND_OPTIONS_SURVEY));
	}

	private <T extends IRestResponse, Q> T doSend(Class<T> responseClass, Q request, String path, T defaultError) {
		Map<String, T> errors = new HashMap<>();
		for (String serverUrl : this.redirections) {
			try {
				RestTemplate restTemplate = new RestTemplate();
				ResponseEntity<T> response = restTemplate.postForEntity(serverUrl + path, request, responseClass);
				if (!HttpStatus.OK.equals(response.getStatusCode())) {
					CheckTruckerRestController.logger.error(serverUrl + path + ":" + response.getStatusCodeValue() + ":" + response.getBody());
					errors.put(defaultError.getCodeDesc(), defaultError);
				} else {
					if (response.getBody().getCode() < 0) {
						errors.put(response.getBody().getCodeDesc(), response.getBody());
					} else {
						return response.getBody();
					}
				}
			} catch (Exception err) {
				CheckTruckerRestController.logger.error(null, err);
			}
		}
		return this.chooseError(errors, defaultError);
	}

	private <T> T chooseError(Map<String, T> errors, T defaultError) {
		if (!errors.isEmpty()) {
			if (errors.containsKey(CheckTruckerRestController.E_DNI_NOT_EXISTS)) {
				return errors.get(CheckTruckerRestController.E_DNI_NOT_EXISTS);
			} else if (errors.containsKey(CheckTruckerRestController.E_SEND_OPTIONS_SURVEY)) {
				return errors.get(CheckTruckerRestController.E_SEND_OPTIONS_SURVEY);
			} else if (errors.containsKey(CheckTruckerRestController.E_GETTING_SURVEYS)) {
				return errors.get(CheckTruckerRestController.E_GETTING_SURVEYS);
			} else {
				return errors.entrySet().iterator().next().getValue();
			}
		}
		return defaultError;
	}
}
