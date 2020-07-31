package com.opentach.adminclient.modules.reports;

import java.util.HashMap;
import java.util.List;

import com.ontimize.gui.Form;
import com.ontimize.gui.field.DataField;
import com.opentach.client.modules.IMReportRoot;

public class IMInformeAgentCompaniesNotUsed extends IMReportRoot {


	public IMInformeAgentCompaniesNotUsed() {
		super("EInformeAgentCompaniesNotUsed", "informe_agent_companies_not_used");
		HashMap<String, String> mEntityReport = new HashMap<String, String>();
		mEntityReport.put("EInformeAgentCompaniesNotUsed", "EInformeAgentCompaniesNotUsed");
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

	@Override
	protected void ultimosDatos() {
		// Not cal supper to avoid nullpi from parent IM
	}

	@Override
	protected List<DataField> getFilterFields() {
		List<DataField> filterFields = super.getFilterFields();
		filterFields.add((DataField) this.managedForm.getDataFieldReference("AGENTE"));
		return filterFields;
	}

}
