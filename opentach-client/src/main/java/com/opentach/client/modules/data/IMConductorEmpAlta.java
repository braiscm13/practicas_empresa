package com.opentach.client.modules.data;

import com.ontimize.gui.Form;
import com.ontimize.gui.manager.IFormManager;

public class IMConductorEmpAlta extends IMConductorEmp {

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.setInsertMode();
	}
}
