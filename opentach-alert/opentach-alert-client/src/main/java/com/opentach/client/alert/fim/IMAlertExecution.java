package com.opentach.client.alert.fim;

import com.ontimize.gui.Form;
import com.ontimize.gui.manager.IFormManager;
import com.utilmize.client.fim.advanced.UDowndateFIM;

public class IMAlertExecution extends UDowndateFIM {

	@Override
	public void registerInteractionManager(Form form, IFormManager formManager) {
		super.registerInteractionManager(form, formManager);
	}
	@Override
	public boolean isDowndatedRecord() {
		return true;
	}

	@Override
	public String getDownDateField() {
		return null;
	}
}
