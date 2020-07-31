package com.opentach.server.checktrucker.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.db.SQLStatementBuilder.ExtendedSQLConditionValuesProcessor;
import com.ontimize.jee.common.tools.BasicExpressionTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.Trio;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.sessionstatus.ISessionStatusReportService.GroupingTime;
import com.opentach.common.surveys.ISurveyReportService;
import com.opentach.server.util.StatementBuilderHelper;
import com.opentach.server.webservice.rest.RestServiceUtils;
import com.utilmize.server.services.UAbstractService;

public class SurveyReportService extends UAbstractService implements ISurveyReportService {

	private static final Logger		logger				= LoggerFactory.getLogger(SurveyReportService.class);
	private static final String		DEFAULT_NONE		= "NONE";

	private static final String		COLUMN_NAME			= "NAME";

	private static final String		COLUMN_ID			= "ID";

	private static final String		COLUMN_PARENT_ID	= "PARENT_ID";
	private HashMap<String, String>	cache;

	public SurveyReportService(int port, EntityReferenceLocator erl, Hashtable hconfig) throws Exception {
		super(port, erl, hconfig);
		this.loadCache();
	}

	@Override
	public EntityResult getGlobalCorrectWrong(BasicExpression queryFilter, GroupingTime grouping, int sessionId) throws Exception {
		BasicExpressionTools.renameField(queryFilter, "F_INI", "sr.SURVEY_RESPONSE_DATE");
		BasicExpressionTools.renameField(queryFilter, "ID_SURVEY", "sr.ID_SURVEY");
		List<Trio<String, String, BasicExpression>> preFilter = Arrays.asList(new Trio<String, String, BasicExpression>("#OTHER_FILTER#", " AND ",
				BasicExpressionTools.copyWithoutFields(queryFilter, ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY)));
		return StatementBuilderHelper.doQuery(this.getLocator(), "sql/surveyReportService/globalCorrectVSWrong", queryFilter, grouping, preFilter,
				Collections.EMPTY_LIST);
	}

	@Override
	public EntityResult getGlobalWithProvinceCorrectWrong(BasicExpression queryFilter, GroupingTime grouping, int sessionId) throws Exception {
		BasicExpressionTools.renameField(queryFilter, "F_INI", "sr.SURVEY_RESPONSE_DATE");
		BasicExpressionTools.renameField(queryFilter, "ID_SURVEY", "sr.ID_SURVEY");
		BasicExpressionTools.renameField(queryFilter, "PROVINCES", "emp.CG_PROV");
		List<Trio<String, String, BasicExpression>> preFilter = Arrays.asList(new Trio<String, String, BasicExpression>("#OTHER_FILTER#", " AND ",
				BasicExpressionTools.copyWithoutFields(queryFilter, ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY)));
		return StatementBuilderHelper.doQuery(this.getLocator(), "sql/surveyReportService/globalProvinceCorrectVSWrong", queryFilter, grouping,
				preFilter, Collections.EMPTY_LIST);
	}

	@Override
	public EntityResult getGlobalCorrectWrongBySurvey(BasicExpression queryFilter, GroupingTime grouping, int sessionId) throws Exception {
		BasicExpressionTools.renameField(queryFilter, "F_INI", "sr.SURVEY_RESPONSE_DATE");
		return StatementBuilderHelper.doQuery(this.getLocator(), "sql/surveyReportService/globalSurveyCorrectWrong", queryFilter, grouping);
	}

	@Override
	public EntityResult getGlobalWithProvinceCorrectWrongBySurvey(BasicExpression queryFilter, GroupingTime grouping, int sessionId)
			throws Exception {
		BasicExpressionTools.renameField(queryFilter, "F_INI", "sr.SURVEY_RESPONSE_DATE");
		BasicExpressionTools.renameField(queryFilter, "PROVINCES", "emp.CG_PROV");
		return StatementBuilderHelper.doQuery(this.getLocator(), "sql/surveyReportService/globalProvinceSurveyCorrectWrong", queryFilter, grouping);
	}

	@Override
	public EntityResult getGlobalCorrectWrongByAutonomy(BasicExpression queryFilter, GroupingTime grouping, int sessionId) throws Exception {
		BasicExpressionTools.renameField(queryFilter, "F_INI", "sr.SURVEY_RESPONSE_DATE");
		EntityResult res = StatementBuilderHelper.doQuery(this.getLocator(), "sql/surveyReportService/globalAutonomyCorrectWrong", queryFilter,
				grouping);
		this.translateAutonomyColumn(res);
		return EntityResultTools.doSort(res, new String[] { "XAXIS" });

	}

	@Override
	public EntityResult getCompanyCorrectWrongDrivers(BasicExpression queryFilter, GroupingTime grouping, int sessionId) throws Exception {
		BasicExpressionTools.renameField(queryFilter, "CIF", "con.CIF");
		BasicExpressionTools.renameField(queryFilter, "IDCONDUCTOR", "sre.IDCONDUCTOR");
		BasicExpressionTools.renameField(queryFilter, "IDPERSONAL", "srp.IDPERSONAL");
		EntityResult res = StatementBuilderHelper.doQuery(this.getLocator(), "sql/surveyReportService/companyDriversCorrectWrong", queryFilter,
				grouping);
		if ((res != null) && !res.isEmpty()) {
			Vector<Object> surDates = (Vector<Object>) res.get("SUR_DATE");
			for (int i = 0; i < surDates.size(); i++) {
				String dateString = RestServiceUtils.getFormatDate(surDates.get(i));
				surDates.set(i, dateString);
			}
			return EntityResultTools.doSort(res, new String[] { "SUR_DATE" });
		} else {
			return res;
		}
	}

	@Override
	public EntityResult getQuestionsOneSurvey(BasicExpression queryFilter, GroupingTime grouping, int sessionId) throws Exception {
		BasicExpressionTools.renameField(queryFilter, "ID_SURVEY", "sr.ID_SURVEY");
		EntityResult res = StatementBuilderHelper.doQuery(this.getLocator(), "sql/surveyReportService/surveyCorrectWrong", queryFilter, grouping);
		return res;
	}

	@Override
	public EntityResult getCompanyCorrectVSWrong(BasicExpression queryFilter, GroupingTime grouping, int sessionId) throws Exception {
		EntityResult res = StatementBuilderHelper.doQuery(this.getLocator(), "sql/surveyReportService/companyCorrectVSWrong", queryFilter, grouping);
		return res;
	}

	@Override
	public EntityResult getCompanyBySurveyCorrectWrong(BasicExpression queryFilter, GroupingTime grouping, int sessionId) throws Exception {
		EntityResult res = StatementBuilderHelper.doQuery(this.getLocator(), "sql/surveyReportService/companyBySurveyCorrectWrong", queryFilter,
				grouping);
		return res;
	}

	private void translateAutonomyColumn(EntityResult res) throws Exception {
		if ((res != null) && !res.isEmpty()) {
			int nRows = res.calculateRecordNumber();
			for (int i = nRows - 1; i >= 0; i--) {
				String autonomy = ((Vector<String>) res.get("XAXIS")).get(i);
				String translateAuto = autonomy;
				if (this.cache.containsKey(autonomy)) {
					translateAuto = this.cache.get(autonomy);
					((Vector<String>) res.get("XAXIS")).set(i, translateAuto);
				} else if ("CE".equals(autonomy)) {
					((Vector<String>) res.get("XAXIS")).set(i, "Ceuta");
				} else if ("ML".equals(autonomy)) {
					((Vector<String>) res.get("XAXIS")).set(i, "Melilla");
				} else if ("NO_AUTONOMY".equals(autonomy)) {
					((Vector<String>) res.get("XAXIS")).set(i, "Sin autonomía");
				}

			}
		}
	}

	private void loadCache() throws IOException {
		this.cache = new HashMap<String, String>();
		try (InputStream is = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("com/opentach/server/entities/prop/ETachoRegionsData.properties")) {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (!line.isEmpty()) {
					String[] split = line.split("\\=");
					if (split.length == 2) {
						this.cache.put(split[0], split[1]);
					} else if (split.length == 3) {
						this.cache.put(split[1], split[2]);
					} else {
						SurveyReportService.logger.warn("Line with more than 2 =");
					}
				}
			}
		}
	}

}
