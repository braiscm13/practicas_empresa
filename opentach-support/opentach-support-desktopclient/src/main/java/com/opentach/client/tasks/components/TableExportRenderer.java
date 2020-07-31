package com.opentach.client.tasks.components;

import java.util.Hashtable;

import com.ontimize.gui.Application;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.report.DefaultReportDialog;
import com.utilmize.client.gui.field.table.UTable;
import com.utilmize.client.report.UReportUtils;

public class TableExportRenderer extends UTable {

	public TableExportRenderer(Hashtable params) throws Exception {
		super(params);
	}

	@Override
	public void showCustomReportsWindow() {
		this.showCustomReportsWindow(null);
	}

	@Override
	public void showCustomReportsWindow(String configuration) {
		if (this.isEmpty()) {
			return;
		}

		// TODO change the values to show using the renderers if exist
		Hashtable hData = /* Table.renderReportValues ? */(Hashtable) this.getValueToReport() /* : (Hashtable) this.getShownValue() */;
		Application ap = (this.parentForm != null) ? this.parentForm.getFormManager().getApplication() : ApplicationManager.getApplication();
		if (this.ru == null) {
			this.ru = new UReportUtils(this.createTableModel(hData, this.reportCols), null, this.getResourceBundle(), null, this.entity, this.getUser(),
					this.getCustomReportPreferenceKey(), ap.getPreferences());
		} else {
			this.ru.setModel(this.createTableModel(hData, this.reportCols));
		}
		this.ru.setResourceBundle(this.getResourceBundle());
		DefaultReportDialog reportDialog = this.ru.createDefaultDialog(this, this.lInfoFilter.getText());
		if (this.dynamicTable) {
			if (reportDialog.getLoadButton() != null) {
				reportDialog.getLoadButton().setVisible(false);
			}
			if (reportDialog.getSaveButton() != null) {
				reportDialog.getSaveButton().setVisible(false);
			}
		}
		this.ru.showDefaultReportDialog(reportDialog, configuration);
	}

}
