package com.opentach.client.modules.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Form;
import com.ontimize.gui.manager.IFormManager;
import com.opentach.client.modules.IMRoot;

public class IMCartaAdvComInfracciones extends IMRoot {

	private static final Logger logger = LoggerFactory.getLogger(IMCartaAdvComInfracciones.class);

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.setUpdateMode();
		if (this.cgContract != null) {
			this.cgContract.setValue(this.getActiveContract());
		}
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
	}

}
