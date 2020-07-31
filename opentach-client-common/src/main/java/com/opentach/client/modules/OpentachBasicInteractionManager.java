package com.opentach.client.modules;

import com.ontimize.gui.Form;
import com.ontimize.gui.container.CollapsiblePanel;
import com.ontimize.gui.manager.IFormManager;
import com.opentach.client.util.VATValidator;
import com.opentach.common.OpentachFieldNames;
import com.utilmize.client.fim.UBasicFIM;

//com.utilmize.client.fim.UBasicFIM
public class OpentachBasicInteractionManager extends UBasicFIM implements OpentachFieldNames {

	protected static final VATValidator	vatv		= new VATValidator();

	@Override
	public void registerInteractionManager(Form form, IFormManager formManager) {
		super.registerInteractionManager(form, formManager);

		CollapsiblePanel cp = (CollapsiblePanel) form.getElementReference("filaexc");
		if (cp != null) {
			cp.setVisible(false);
		}
	}

	protected boolean checkValidCIFNIF(boolean mensaje) {
		String val = (String) this.managedForm.getDataFieldValue(OpentachFieldNames.CIF_FIELD);
		boolean nifcifCorrecto = false;
		if (val != null) {
			nifcifCorrecto = OpentachBasicInteractionManager.vatv.validate(val);
		}
		if (mensaje && !nifcifCorrecto) {
			return this.managedForm.question("M_NIF_CIF_INCORRECTO");
		}
		return nifcifCorrecto;
	}

}
