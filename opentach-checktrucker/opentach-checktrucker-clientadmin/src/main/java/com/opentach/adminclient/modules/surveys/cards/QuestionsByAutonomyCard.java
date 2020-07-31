package com.opentach.adminclient.modules.surveys.cards;

import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.Form;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.opentach.client.AbstractOpentachClientLocator;
import com.opentach.common.surveys.ISurveyReportService;
import com.utilmize.client.gui.chart.bar.BarChartJFxComponent;

public class QuestionsByAutonomyCard extends QuestionsAbstractCard<BarChartJFxComponent> {

	private static final Logger logger = LoggerFactory.getLogger(QuestionsByAutonomyCard.class);

	public QuestionsByAutonomyCard(AbstractOpentachClientLocator locator, Form form, int xSize, int ySize) {
		super(locator, form, xSize, ySize);
	}

	@Override
	public void refresh() {
		try {
			ISurveyReportService surveyReportService = this.getSurveyReportService();
			EntityResult er = surveyReportService.getGlobalCorrectWrongByAutonomy(this.getDateFilter(), null, this.getSessionId());
			this.getView().setData(er);
		} catch (Exception error) {
			QuestionsByAutonomyCard.logger.error(null, error);
		}
	}

	@Override
	protected BarChartJFxComponent buildViewComponent() {

		Hashtable<Object, Object> parameters = EntityResultTools.keysvalues(//
				"provider", " ", //
				"xAxisColumnName", "XAXIS", //
				"yAxisColumnName", "YAXIS", //
				"serieColumnName", "SERIE", //
				"charttitle", "sur.TitleQuestionsByAutonomy", //
				"xlabel", "sur.Autonomies", //
				"ylabel", "sur.NumberQuestions", //
				"legendvisible", "true");
		return new BarChartJFxComponent(parameters);
	}
}
