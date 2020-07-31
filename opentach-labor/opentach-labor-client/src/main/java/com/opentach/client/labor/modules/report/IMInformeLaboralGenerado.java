package com.opentach.client.labor.modules.report;

import com.ontimize.gui.Form;
import com.ontimize.gui.manager.IFormManager;
import com.opentach.client.modules.IMReportRoot;
import com.opentach.common.OpentachFieldNames;

public class IMInformeLaboralGenerado extends IMReportRoot {

	public IMInformeLaboralGenerado() {
		super("ELaborReportWarehouse", "informe_laboral");
		this.dateEntity = "EUFecha";
		this.setDateTags(new TimeStampDateTags("REP_F_ALTA"));
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.managedForm.setAdvancedQueryMode(OpentachFieldNames.IDCONDUCTOR_FIELD, true);
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.managedForm.enableDataField(OpentachFieldNames.FILTERFECINI);
		this.managedForm.enableDataField(OpentachFieldNames.FILTERFECFIN);
	}

	protected void createReports() {
	}

}
