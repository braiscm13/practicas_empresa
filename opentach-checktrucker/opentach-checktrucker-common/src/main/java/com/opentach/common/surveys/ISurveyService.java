package com.opentach.common.surveys;

import java.rmi.Remote;
import java.util.List;

public interface ISurveyService extends Remote {
	String ID = "SurveyService";

	List<SurveyTO> getSurveys(final String uuid) throws Exception;

	QuestionaryTO getSurvey(final Number idSurvey) throws Exception;

	List<QuestionTO> getQuestions(final List<Number> lQuestions) throws Exception;

	Number createSurvey(final String title, final Object surveyExpirationDate, final List<QuestionTO> questions, int sessionId) throws Exception;

	Number updateQuestions(final Number idSurvey, final String title, final Object surveyExpirationDate, final List<QuestionTO> questions, int sessionId) throws Exception;

	Number deleteSurvey(final Number idSurvey, int sessionId) throws Exception;

	ResultsSurvey sendResponses(final Number idSurvey, final String dni, final String uuid, final List<Number> options) throws Exception;
}