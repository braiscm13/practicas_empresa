package com.opentach.common.surveys;

import java.rmi.Remote;

import com.ontimize.db.EntityResult;
import com.ontimize.db.SQLStatementBuilder.BasicExpression;
import com.opentach.common.sessionstatus.ISessionStatusReportService.GroupingTime;

public interface ISurveyReportService extends Remote {

	/** The Constant ID. */
	final static String ID = "SurveyReportService";

	EntityResult getGlobalCorrectWrong(BasicExpression queryFilter, GroupingTime grouping, int sessionId) throws Exception;

	EntityResult getGlobalWithProvinceCorrectWrong(BasicExpression queryFilter, GroupingTime grouping, int sessionId) throws Exception;

	EntityResult getGlobalCorrectWrongBySurvey(BasicExpression queryFilter, GroupingTime grouping, int sessionId) throws Exception;

	EntityResult getGlobalWithProvinceCorrectWrongBySurvey(BasicExpression queryFilter, GroupingTime grouping, int sessionId) throws Exception;

	EntityResult getGlobalCorrectWrongByAutonomy(BasicExpression queryFilter, GroupingTime grouping, int sessionId) throws Exception;

	EntityResult getCompanyCorrectWrongDrivers(BasicExpression queryFilter, GroupingTime grouping, int sessionId) throws Exception;

	EntityResult getQuestionsOneSurvey(BasicExpression queryFilter, GroupingTime grouping, int sessionId) throws Exception;

	EntityResult getCompanyCorrectVSWrong(BasicExpression queryFilter, GroupingTime grouping, int sessionId) throws Exception;

	EntityResult getCompanyBySurveyCorrectWrong(BasicExpression queryFilter, GroupingTime grouping, int sessionId) throws Exception;
}
