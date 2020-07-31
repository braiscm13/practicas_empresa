package com.opentach.adminclient.modules.surveys.cards;

import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.gui.Form;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.opentach.client.AbstractOpentachClientLocator;
import com.opentach.common.surveys.ISurveyReportService;
import com.utilmize.client.gui.chart.pie.PieChartJFxComponent;

public class QuestionsSurveyCorrectVsWrongCard extends QuestionsAbstractCard<PieChartJFxComponent> {

	private static final Logger logger = LoggerFactory.getLogger(QuestionsSurveyCorrectVsWrongCard.class);

	public QuestionsSurveyCorrectVsWrongCard(AbstractOpentachClientLocator locator, Form form, int xSize, int ySize) {
		super(locator, form, xSize, ySize);
	}

	@Override
	public void refresh() {
		try {
			ISurveyReportService surveyReportService = this.getSurveyReportService();
			BasicExpression surveyFilter = this.getSurveyFilter();
			if (surveyFilter != null) {
				EntityResult er = surveyReportService.getGlobalCorrectWrong(surveyFilter, null, this.getSessionId());
				this.getView().setData(er);
			} else {
				this.getView().setData(new EntityResult());
			}
		} catch (Exception error) {
			QuestionsSurveyCorrectVsWrongCard.logger.error(null, error);
		}
	}

	@Override
	protected PieChartJFxComponent buildViewComponent() {

		Hashtable<Object, Object> parameters = EntityResultTools.keysvalues(//
				"provider", " ", //
				"titleColumnName", "TITLE", //
				"valueColumnName", "VALUE", //
				"charttitle", "sur.QuestionsCorrectVSWrong", //
				"legendvisible", "false");
		return new PieChartJFxComponent(parameters);
	}
}
