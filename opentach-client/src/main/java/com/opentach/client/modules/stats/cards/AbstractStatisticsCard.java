package com.opentach.client.modules.stats.cards;

import javax.swing.JComponent;

import com.ontimize.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.db.SQLStatementBuilder.ExtendedSQLConditionValuesProcessor;
import com.ontimize.gui.Form;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.client.OpentachClientLocator;
import com.opentach.common.sessionstatus.ISessionStatusReportService;
import com.opentach.common.sessionstatus.ISessionStatusReportService.GroupingTime;
import com.utilmize.client.gui.cardboard.AbstractCard;
import com.utilmize.tools.exception.UException;

public abstract class AbstractStatisticsCard<T extends JComponent> extends AbstractCard<T> {

	public AbstractStatisticsCard(EntityReferenceLocator locator, Form form, int xSize, int ySize) {
		super(locator, form, xSize, ySize);
	}

	public ISessionStatusReportService getStatsReportService() throws UException {
		try {
			return this.getOpentachLocator().getRemoteService(ISessionStatusReportService.class);
		} catch (Exception ex) {
			throw new UException(ex);
		}
	}

	protected GroupingTime getGroupingTime() {
		String value = (String) this.getForm().getDataFieldValue("GROUPING_TIME");
		return GroupingTime.fromString(value);
	}

	public BasicExpression getQueryFilter() {
		return (BasicExpression) this.getForm().getDataFieldValue(ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY);
	}

	public OpentachClientLocator getOpentachLocator() {
		return (OpentachClientLocator) this.getLocator();
	}

	@Override
	public void clear() {}
}
