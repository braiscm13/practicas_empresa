package com.opentach.client.modules.report;

import java.util.HashMap;

import com.ontimize.gui.Form;
import com.opentach.client.modules.IMReportRoot;

public class IMInformeDriversNotDownloadingAgents extends IMReportRoot {


	public IMInformeDriversNotDownloadingAgents() {
		super("EInformeDriversNotDownloading", "informe_vehicles_not_downloading");
		HashMap<String, String> mEntityReport = new HashMap<String, String>();
		mEntityReport.put("EInformeDriversNotDownloading", "EInformeDriversNotDownloading");
		this.setEntidadesInformes(mEntityReport);
	}

	@Override
	public void doOnQuery(final boolean alert) {
		if (this.managedForm.existEmptyRequiredDataField()) {
			if (alert) {
				this.managedForm.message("Establezca filtro de búsqueda.", Form.INFORMATION_MESSAGE);
			}
		} else if (this.checkQuery()) {
			this.refreshTable(this.tablename);
		}
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.setUpdateMode();
		this.doOnQuery(false);
	}

}

