package com.opentach.server.proxy.checktrucker.restcontrollers;

import java.util.List;

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

import com.opentach.ws.common.rest.beans.RestLogginRequest;
import com.opentach.ws.common.rest.beans.RestLogginResponse;

@RestController
@RequestMapping("openservices/services")
public class CheckTruckerGraphicsRestController {
	private static final Logger	logger	= LoggerFactory.getLogger(CheckTruckerGraphicsRestController.class);

	@Value("${app.redirections.checkTruckerService}")
	private List<String>		redirections;

	@RequestMapping(value = "/restGraphicsSurveyService/searchOkVSKo",
			method = RequestMethod.POST,
			consumes = { MediaType.APPLICATION_JSON_VALUE },
			produces = { MediaType.APPLICATION_JSON_VALUE })
	public RestLogginResponse searchGraphicSurveysOkVSKo(@RequestBody final RestLogginRequest request) {
		return this.doSend(request, "/restGraphicsSurveyService/searchOkVSKo");
	}

	@RequestMapping(value = "/restGraphicsSurveyService/searchOkKoBySurvey",
			method = RequestMethod.POST,
			consumes = { MediaType.APPLICATION_JSON_VALUE },
			produces = { MediaType.APPLICATION_JSON_VALUE })
	public RestLogginResponse searchGraphicSurveysOkKoBySurvey(@RequestBody final RestLogginRequest request) {
		return this.doSend(request, "/restGraphicsSurveyService/searchOkKoBySurvey");
	}

	@RequestMapping(value = "/restGraphicsSurveyService/searchDriversOkKo",
			method = RequestMethod.POST,
			consumes = { MediaType.APPLICATION_JSON_VALUE },
			produces = { MediaType.APPLICATION_JSON_VALUE })
	public RestLogginResponse searchGraphicSurveysDriverOkKo(@RequestBody final RestLogginRequest request) {
		return this.doSend(request, "/restGraphicsSurveyService/searchDriversOkKo");
	}

	private <Q> RestLogginResponse doSend(Q request, String path) {
		for (String serverUrl : this.redirections) {
			try {
				RestTemplate restTemplate = new RestTemplate();
				ResponseEntity<RestLogginResponse> response = restTemplate.postForEntity(serverUrl + path, request, RestLogginResponse.class);
				if (!HttpStatus.OK.equals(response.getStatusCode())) {
					CheckTruckerGraphicsRestController.logger.error(serverUrl + path + ":" + response.getStatusCodeValue() + ":" + response.getBody());
				} else if (response.getBody().getSessionId() >= 0) {
					return response.getBody();
				}
			} catch (Exception err) {
				CheckTruckerGraphicsRestController.logger.error(null, err);
			}
		}
		return new RestLogginResponse(-1);
	}
}
