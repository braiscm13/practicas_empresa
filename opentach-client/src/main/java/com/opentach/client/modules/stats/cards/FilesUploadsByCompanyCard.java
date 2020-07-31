package com.opentach.client.modules.stats.cards;

import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.opentach.client.OpentachClientLocator;
import com.utilmize.client.gui.Row;
import com.utilmize.client.gui.field.table.UTable;
import com.utilmize.tools.exception.UException;

public class FilesUploadsByCompanyCard extends AbstractStatisticsCard<Row> {

	private static final Logger	logger	= LoggerFactory.getLogger(FilesUploadsByCompanyCard.class);
	private Table				table;

	public FilesUploadsByCompanyCard(OpentachClientLocator locator, Form form, int xSize, int ySize) {
		super(locator, form, xSize, ySize);
	}

	@Override
	public void refresh() throws UException {
		try {
			EntityResult er = this.getStatsReportService().getFilesUploadsByCompany(this.getQueryFilter(), this.getGroupingTime(), this.getSessionId());
			this.table.setValue(er);
		} catch (Exception ex) {
			throw new UException(ex);
		}
	}

	@Override
	protected Row buildViewComponent() {

		Hashtable<Object, Object> parametersTable = EntityResultTools.keysvalues(//
				"entity", "none", //
				"keys", "none", //
				"cols", "EMPRESA;'TC';'VU'", //
				"controlsvisible", "no"//
				);
		Hashtable<Object, Object> parametersRow = EntityResultTools.keysvalues(//
				"title", "sta.FilesUploadsByCompanyCard", //
				"expand", "yes"//
				);
		try {
			this.table = new UTable(parametersTable);
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
