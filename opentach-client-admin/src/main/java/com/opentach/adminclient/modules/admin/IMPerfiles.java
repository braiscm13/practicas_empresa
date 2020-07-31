package com.opentach.adminclient.modules.admin;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.Form;
import com.ontimize.gui.field.MaskDataField;
import com.ontimize.gui.field.TextDataField;
import com.ontimize.gui.manager.IFormManager;
import com.utilmize.client.fim.UBasicFIM;

/**
 * The Class IMPerfiles.
 */
public class IMPerfiles extends UBasicFIM {

	/** The c nivel. */
	@FormComponent(attr = "NIVEL_CD")
	private final MaskDataField	cNivel	= null;

	/** The app. */
	@FormComponent(attr = "APP")
	private final TextDataField	app		= null;

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.gui.BasicInteractionManager#registerInteractionManager(com.ontimize.gui.Form, com.ontimize.gui.manager.IFormManager)
	 */
	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.gui.BasicInteractionManager#setUpdateMode()
	 */
	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		if (this.cNivel != null) {
			this.cNivel.setEnabled(false);
		}
		if (this.app != null) {
			this.app.setEnabled(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.gui.BasicInteractionManager#setInsertMode()
	 */
	@Override
	public void setInsertMode() {
		super.setInsertMode();
		if (this.app != null) {
			this.app.setValue("OPENTACH");
		}
	}
}
