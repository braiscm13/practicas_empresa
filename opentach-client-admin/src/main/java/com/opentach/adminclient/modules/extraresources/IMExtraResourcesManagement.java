package com.opentach.adminclient.modules.extraresources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Form;
import com.ontimize.gui.manager.IFormManager;
import com.utilmize.client.fim.UBasicFIM;

public class IMExtraResourcesManagement extends UBasicFIM {

	private static final Logger	logger		= LoggerFactory.getLogger(IMExtraResourcesManagement.class);


	@Override
	public void registerInteractionManager(Form form, IFormManager formManager) {
		super.registerInteractionManager(form, formManager);
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.managedForm.setDataFieldValue("documentId", Integer.valueOf(-1));
		this.setUpdateMode();

	}


}
