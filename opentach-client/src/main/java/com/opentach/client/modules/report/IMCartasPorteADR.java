package com.opentach.client.modules.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Form;
import com.ontimize.gui.manager.IFormManager;
import com.opentach.client.modules.IMRoot;

public class IMCartasPorteADR extends IMRoot {

	private static final Logger logger = LoggerFactory.getLogger(IMCartasPorteADR.class);

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.setUpdateMode();
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		this.managedForm.getButton("calculaletranif").setEnabled(true);
		this.managedForm.getButton("calculaletranif2").setEnabled(true);
		this.managedForm.getButton("calculaletranif3").setEnabled(true);
		this.managedForm.getButton("calculaletranif4").setEnabled(true);
	}

}
