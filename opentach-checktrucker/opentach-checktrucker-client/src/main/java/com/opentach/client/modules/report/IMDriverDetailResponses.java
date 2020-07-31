package com.opentach.client.modules.report;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.Form;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.opentach.client.comp.render.SurveyResponsesCellRendererColorManager;
import com.utilmize.client.fim.UBasicFIM;
import com.utilmize.client.gui.field.reference.UReferenceDataField;

public class IMDriverDetailResponses extends UBasicFIM {

	@FormComponent(attr = "CIF")
	protected UReferenceDataField	cif;
	@FormComponent(attr = "IDCONDUCTOR")
	protected UReferenceDataField	idConductor;
	@FormComponent(attr = "IDPERSONAL")
	protected UReferenceDataField	idPersonal;
	@FormComponent(attr = "ID_SURVEY")
	protected UReferenceDataField	idSurvey;
	@FormComponent(attr = "ESurDNIDetailResponses")
	protected Table					table;

	@Override
	public void registerInteractionManager(Form form, IFormManager formManager) {
		super.registerInteractionManager(form, formManager);
		this.table.setCellRendererColorManager(new SurveyResponsesCellRendererColorManager());
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.setUpdateMode();
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		this.cif.setEnabled(false);
		this.idConductor.setEnabled(false);
		this.idSurvey.setEnabled(false);
		this.idPersonal.setEnabled(false);
	}

}
