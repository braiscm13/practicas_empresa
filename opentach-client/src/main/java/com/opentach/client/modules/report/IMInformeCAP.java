package com.opentach.client.modules.report;

import java.util.HashMap;

import com.ontimize.gui.Form;
import com.opentach.client.modules.IMReportRoot;

public class IMInformeCAP extends IMReportRoot {

	public IMInformeCAP() {
		super("EInformeCAP", "informe_cap");
		HashMap<String, String> mEntityReport = new HashMap<String, String>();
		this.setDateTags(new TimeStampDateTags("F_DESCARGA_DATOS"));
		mEntityReport.put("informe_cap", "EInformeCAP");
		this.setEntidadesInformes(mEntityReport);
	}

	@Override
	public void doOnQuery(final boolean alert) {
		if (this.managedForm.existEmptyRequiredDataField()) {
			this.managedForm.message("Establezca filtro de búsqueda.", Form.INFORMATION_MESSAGE);
		} else if (this.checkQuery()) {
			this.refreshTable(this.tablename);
		}
	}

}
