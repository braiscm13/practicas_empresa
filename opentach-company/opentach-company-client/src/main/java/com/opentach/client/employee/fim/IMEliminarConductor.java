package com.opentach.client.employee.fim;

import com.ontimize.gui.Form;
import com.ontimize.gui.manager.IFormManager;
import com.opentach.client.modules.IMRoot;

public class IMEliminarConductor extends IMRoot {

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.managedForm.enableButtons();
		this.managedForm.enableDataFields();
	}

}