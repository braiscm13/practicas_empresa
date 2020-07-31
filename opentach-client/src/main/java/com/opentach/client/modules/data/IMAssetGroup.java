package com.opentach.client.modules.data;

import com.ontimize.gui.Form;
import com.ontimize.gui.manager.IFormManager;
import com.utilmize.client.fim.UBasicFIM;

public class IMAssetGroup extends UBasicFIM {

	@Override
	public void registerInteractionManager(Form form, IFormManager formManager) {
		super.registerInteractionManager(form, formManager);
		this.setStayInRecordAfterInsert(true);
	}
}
