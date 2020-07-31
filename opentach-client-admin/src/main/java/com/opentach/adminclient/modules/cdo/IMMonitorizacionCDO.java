package com.opentach.adminclient.modules.cdo;

import com.ontimize.gui.Form;
import com.ontimize.gui.manager.IFormManager;
import com.utilmize.client.fim.FIMUtils;
import com.utilmize.client.fim.UBasicFIM;

public class IMMonitorizacionCDO extends UBasicFIM {

	public IMMonitorizacionCDO() {
		super();
	}

	@Override
	public void registerInteractionManager(Form formulario, IFormManager gestorForms) {
		super.registerInteractionManager(formulario, gestorForms);
	}

	@Override
	public void setInitialState() {
		this.setUpdateMode();
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		this.managedForm.enableDataFields();
		FIMUtils.enableTables(this.managedForm, true);
		this.managedForm.setDataFieldValue("autorefreshTimeSessionMonitor", 20);
		this.managedForm.setDataFieldValue("autorefreshSessionMonitor", true);
	}

}
