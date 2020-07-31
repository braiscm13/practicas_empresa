package com.opentach.client.modules.stats.cards;

import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.Form;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.opentach.client.OpentachClientLocator;
import com.utilmize.client.gui.chart.pie.PieChartJFxComponent;
import com.utilmize.tools.exception.UException;

public class FilesUploadsByTypeCard extends AbstractStatisticsCard<PieChartJFxComponent> {

	private static final Logger logger = LoggerFactory.getLogger(FilesUploadsByTypeCard.class);

	public FilesUploadsByTypeCard(OpentachClientLocator locator, Form form, int xSize, int ySize) {
		super(locator, form, xSize, ySize);
	}

	@Override
	public void refresh() throws UException {
		try {
			EntityResult er = this.getStatsReportService().getFilesUploadsByType(this.getQueryFilter(), null, this.getSessionId());
			this.getView().setData(er);
		} catch (Exception ex) {
			throw new UException(ex);
		}
	}

	@Override
	protected PieChartJFxComponent buildViewComponent() {

		Hashtable<Object, Object> parameters = EntityResultTools.keysvalues(//
				"provider", " ", //
				"titleColumnName", "TITLE", //
				"valueColumnName", "VALUE", //
				"charttitle", "sta.FilesUploadsByTypeCard", //
				"legendvisible", "false");
		return new PieChartJFxComponent(parameters);
	}
}
