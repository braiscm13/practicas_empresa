package com.opentach.client.modules.stats.cards;

import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.Form;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.opentach.client.OpentachClientLocator;
import com.utilmize.client.gui.chart.bar.BarChartJFxComponent;
import com.utilmize.tools.exception.UException;

public class FilesUploadsCard extends AbstractStatisticsCard<BarChartJFxComponent> {

	private static final Logger logger = LoggerFactory.getLogger(FilesUploadsCard.class);

	public FilesUploadsCard(OpentachClientLocator locator, Form form, int xSize, int ySize) {
		super(locator, form, xSize, ySize);
	}

	@Override
	public void refresh() throws UException {
		try {
			EntityResult er = this.getStatsReportService().getFilesUploads(this.getQueryFilter(), this.getGroupingTime(), this.getSessionId());
			this.getView().setData(er);
		} catch (Exception ex) {
			throw new UException(ex);
		}
	}

	@Override
	protected BarChartJFxComponent buildViewComponent() {

		Hashtable<Object, Object> parameters = EntityResultTools.keysvalues(//
				"provider", " ", //
				"xAxisColumnName", "XAXIS", //
				"yAxisColumnName", "YAXIS", //
				"serieColumnName", "SERIE", //
				"charttitle", "sta.FilesUploadsCard", //
				"xlabel", "sta.Periodo", //
				"ylabel", "sta.NumFiles", //
				"legendvisible", "true");
		return new BarChartJFxComponent(parameters);
	}
}
