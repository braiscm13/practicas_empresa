package com.opentach.adminclient.modules.reports;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Form;
import com.ontimize.gui.field.DataField;
import com.opentach.client.modules.IMReportRoot;

public class IMInformeIncidencesByWeek extends IMReportRoot {

	private static final Logger logger = LoggerFactory.getLogger(IMInformeIncidencesByWeek.class);

	public IMInformeIncidencesByWeek() {
		super("EInformeIncidencesByWeek", "informe_incidencias_semanales");
		HashMap<String, String> mEntityReport = new HashMap<String, String>();
		mEntityReport.put("EInformeIncidencesByWeek", "EInformeIncidencesByWeek");
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
	}

	@Override
	protected List<DataField> getFilterFields() {
		List<DataField> filterFields = super.getFilterFields();
		filterFields.add((DataField) this.managedForm.getDataFieldReference("USUARIO_CREATOR"));
		return filterFields;
	}
}
