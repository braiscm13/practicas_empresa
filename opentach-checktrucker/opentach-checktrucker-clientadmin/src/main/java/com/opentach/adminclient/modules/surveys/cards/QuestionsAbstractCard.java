package com.opentach.adminclient.modules.surveys.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JComponent;

import com.ontimize.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.db.SQLStatementBuilder.BasicField;
import com.ontimize.db.SQLStatementBuilder.BasicOperator;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.jee.common.tools.BasicExpressionTools;
import com.ontimize.jee.common.tools.BasicExpressionTools.BetweenDateFilter;
import com.ontimize.jee.common.tools.BasicExpressionTools.SimpleFieldFilter;
import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.opentach.client.AbstractOpentachClientLocator;
import com.opentach.common.surveys.ISurveyReportService;
import com.utilmize.client.gui.cardboard.AbstractCard;

public abstract class QuestionsAbstractCard<T extends JComponent> extends AbstractCard<T> {

	public QuestionsAbstractCard(AbstractOpentachClientLocator locator, Form form, int xSize, int ySize) {
		super(locator, form, xSize, ySize);
	}

	protected ISurveyReportService getSurveyReportService() throws Exception {
		return this.getOpentachLocator().getRemoteService(ISurveyReportService.class);
	}

	public BasicExpression getDateFilter() {
		List<Object> filterFields = new ArrayList<>();
		List<BetweenDateFilter> dateRangeFilterFields = new ArrayList<>();
		List<String> infoList = ParseUtilsExtended.getStringList("sta.DATE_FROM:sta.DATE_TO:F_INI", ":", Collections.emptyList());
		dateRangeFilterFields.add(new BetweenDateFilter(infoList.get(0), infoList.get(1), infoList.get(2)));
		filterFields.addAll(dateRangeFilterFields);
		BasicExpression basicExpression = BasicExpressionTools.completeExpresionFromKeys(null, this.getForm(), filterFields);
		return basicExpression;
	}

	public BasicExpression getDateAndProvince() {
		List<Object> filterFields = new ArrayList<>();
		// Date
		List<BetweenDateFilter> dateRangeFilterFields = new ArrayList<>();
		List<String> infoList = ParseUtilsExtended.getStringList("sta.DATE_FROM:sta.DATE_TO:F_INI", ":", Collections.emptyList());
		dateRangeFilterFields.add(new BetweenDateFilter(infoList.get(0), infoList.get(1), infoList.get(2)));
		filterFields.addAll(dateRangeFilterFields);
		// Province
		List<SimpleFieldFilter> simpleFilterFields = new ArrayList<>();
		Hashtable<String, String> tokensAt = ApplicationManager.getTokensAt("PROVINCES", ";", ":");
		for (Entry<String, String> entry : tokensAt.entrySet()) {
			simpleFilterFields.add(new SimpleFieldFilter(entry.getKey(), entry.getValue()));
		}
		filterFields.addAll(simpleFilterFields);
		BasicExpression basicExpression = BasicExpressionTools.completeExpresionFromKeys(null, this.getForm(), filterFields);
		return basicExpression;
	}

	public BasicExpression getCompanyFilter() {
		// List<Object> filterFields = new ArrayList<>();
		if (this.getForm().getDataFieldValue("CIF") == null) {
			return null;
		}
		List<SimpleFieldFilter> simpleFilterFields = new ArrayList<>();
		Hashtable<String, String> tokensAt = ApplicationManager.getTokensAt("CIF", ";", ":");
		for (Entry<String, String> entry : tokensAt.entrySet()) {
			simpleFilterFields.add(new SimpleFieldFilter(entry.getKey(), entry.getValue()));
		}
		// filterFields.addAll(simpleFilterFields);
		// BasicExpression basicExpression = BasicExpressionTools.completeExpresionFromKeys(null, this.getForm(), filterFields);
		BasicExpression b1 = new BasicExpression(new BasicField("con.CIF"), BasicOperator.EQUAL_OP, this.getForm().getDataFieldValue("CIF"));
		BasicExpression b2 = new BasicExpression(new BasicField("pers.CIF"), BasicOperator.EQUAL_OP, this.getForm().getDataFieldValue("CIF"));
		BasicExpression basicExpression = new BasicExpression(b1, BasicOperator.OR_OP, b2);
		return basicExpression;
	}

	public BasicExpression getSurveyFilter() {
		List<Object> filterFields = new ArrayList<>();
		List<SimpleFieldFilter> simpleFilterFields = new ArrayList<>();
		Hashtable<String, String> tokensAt = ApplicationManager.getTokensAt("ID_SURVEY", ";", ":");
		for (Entry<String, String> entry : tokensAt.entrySet()) {
			simpleFilterFields.add(new SimpleFieldFilter(entry.getKey(), entry.getValue()));
		}
		filterFields.addAll(simpleFilterFields);
		BasicExpression basicExpression = BasicExpressionTools.completeExpresionFromKeys(null, this.getForm(), filterFields);
		return basicExpression;
	}

	@Override
	public void clear() {

	}

	public AbstractOpentachClientLocator getOpentachLocator() {
		return (AbstractOpentachClientLocator) this.getLocator();
	}

}
