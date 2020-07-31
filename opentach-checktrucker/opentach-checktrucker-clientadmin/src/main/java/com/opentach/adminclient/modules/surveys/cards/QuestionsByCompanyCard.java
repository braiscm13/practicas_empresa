package com.opentach.adminclient.modules.surveys.cards;

import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.opentach.client.AbstractOpentachClientLocator;
import com.opentach.common.surveys.ISurveyReportService;
import com.utilmize.client.gui.Row;
import com.utilmize.client.gui.field.table.UTable;

public class QuestionsByCompanyCard extends QuestionsAbstractCard<Row> {
	private static final Logger	logger	= LoggerFactory.getLogger(QuestionsByAutonomyCard.class);
	private Table				table;

	public QuestionsByCompanyCard(AbstractOpentachClientLocator locator, Form form, int xSize, int ySize) {
		super(locator, form, xSize, ySize);
	}

	@Override
	public void refresh() {
		try {
			ISurveyReportService surveyReportService = this.getSurveyReportService();
			BasicExpression companyFilter = this.getCompanyFilter();
			if (companyFilter != null) {
				EntityResult er = surveyReportService.getCompanyCorrectWrongDrivers(companyFilter, null, this.getSessionId());
				this.table.setValue(er);
			} else {
				this.table.setValue(null);
			}
		} catch (Exception error) {
			QuestionsByCompanyCard.logger.error(null, error);
		}
	}

	@Override
	protected Row buildViewComponent() {

		Hashtable<Object, Object> parametersTable = EntityResultTools.keysvalues(//
				"entity", "none", //
				"keys", "none", //
				"cols", "IDCONDUCTOR;IDPERSONAL;CIF;NAME;ID_SURVEY;SURVEY_NAME;SUR_DATE;CORRECT;WRONG", //
				"visiblecols", "IDCONDUCTOR;IDPERSONAL;CIF;NAME;SURVEY_NAME;SUR_DATE;CORRECT;WRONG", //
				"rendertime", "SUR_DATE", //
				"controlsvisible", "no", //
				"form", "formDriverDetailResponses.xml", //
				"insertbutton", "no", //
				"detailformopener", "com.opentach.client.modules.report.listeners.DetailFormOpenerDataFromTable");
		Hashtable<Object, Object> parametersRow = EntityResultTools.keysvalues(//
				"title", "sur.Drivers", //
				"expand", "yes"//
				);
		try {
			this.table = new UTable(parametersTable);
			this.table.setParentForm(this.getForm());
			// Hashtable<String, Object> paramsHideTableButton = new Hashtable<String, Object>();
			// paramsHideTableButton.put("attr",
			// "changeviewbutton;copybutton;excelexportbutton;htmlexportbutton;insertbutton;printingbutton;chartbutton;defaultchartbutton;sumrowbutton;reportbutton;filtersavebutton;calculedcolsbutton");
			// HideTableButtonXml hideTableButton = new HideTableButtonXml(paramsHideTableButton);
			// this.table.add(hideTableButton, ((FormComponent) hideTableButton).getConstraints(this.table.getLayout()));
			Row row = new Row(parametersRow);
			row.add(this.table, this.table.getConstraints(row.getLayout()));
			row.setResourceBundle(ApplicationManager.getApplicationBundle());
			this.table.setResourceBundle(ApplicationManager.getApplicationBundle());
			return row;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
