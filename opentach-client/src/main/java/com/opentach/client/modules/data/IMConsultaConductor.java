package com.opentach.client.modules.data;

import com.ontimize.gui.Form;
import com.ontimize.gui.manager.IFormManager;
import com.opentach.client.modules.IMDataRoot;

public class IMConsultaConductor extends IMDataRoot	 {

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
	}
	
	@Override
	public void setInitialState() {
		super.setInitialState();
		setUpdateMode();
	}
	
	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		managedForm.enableDataField("IDCONDUCTOR");
		managedForm.enableDataField("EXTERNAL_EMPLOYEE_ID");
	}
}
