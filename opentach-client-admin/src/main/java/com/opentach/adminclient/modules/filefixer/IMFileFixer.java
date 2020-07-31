package com.opentach.adminclient.modules.filefixer;

import com.utilmize.client.fim.UBasicFIM;

/**
 * The Class IMUsuDel.
 */
public class IMFileFixer extends UBasicFIM {

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.setUpdateMode();
		this.managedForm.enableButtons();
	}
}
