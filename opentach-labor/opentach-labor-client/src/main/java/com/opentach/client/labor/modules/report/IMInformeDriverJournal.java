package com.opentach.client.labor.modules.report;

import java.util.Date;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.Form;
import com.ontimize.gui.field.CheckDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.jee.common.tools.Pair;
import com.opentach.client.modules.IMDataRoot;
import com.opentach.common.OpentachFieldNames;

public class IMInformeDriverJournal extends IMDataRoot {

	@FormComponent(attr = "NO_COMPUTE_EXTRA_TIME")
	private CheckDataField chekExtraTime;

	public IMInformeDriverJournal() {
		super();
		this.dateEntity = "EUFecha";
		this.setDateTags(new TimeStampDateTags(OpentachFieldNames.FECINI_FIELD));
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.managedForm.setAdvancedQueryMode(OpentachFieldNames.IDCONDUCTOR_FIELD, true);
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECINI, null);
		this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECFIN, null);
		this.managedForm.enableDataField(OpentachFieldNames.FILTERFECINI);
		this.managedForm.enableDataField(OpentachFieldNames.FILTERFECFIN);
		this.managedForm.enableDataField("REPORT_TYPE");
		this.chekExtraTime.setValue(true);

	}

	protected void createReports() {}

	public Pair<Date, Date> computeFilterDates() {
		Date fecFin = (Date) this.managedForm.getDataFieldValue("FILTERFECFIN");
		Date fecIni = (Date) this.managedForm.getDataFieldValue("FILTERFECINI");
		return new Pair<>(fecIni, fecFin);
	}
}
