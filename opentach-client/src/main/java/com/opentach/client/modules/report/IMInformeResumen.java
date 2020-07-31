package com.opentach.client.modules.report;

import com.ontimize.gui.Form;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.manager.IFormManager;
import com.opentach.client.modules.IMRoot;
import com.opentach.common.OpentachFieldNames;

public abstract class IMInformeResumen extends IMRoot {

	protected Button	bInforme2	= null;

	public IMInformeResumen() {
		super();
		this.fieldsChain.clear();
		this.fieldsChain.add(OpentachFieldNames.CIF_FIELD);
		this.fieldsChain.add(OpentachFieldNames.CG_CONTRATO_FIELD);
		this.fieldsChain.add(OpentachFieldNames.IDCONDUCTOR_FIELD);
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.setUpdateMode();
		this.setUserData();
		this.managedForm.enableButton("btnInforme2");
		this.managedForm.enableDataField("MINVAL.2");
		this.managedForm.enableDataField("MAXVAL.2");
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.bInforme2 = f.getButton("btnInforme2");
		this.cgContract = f.getDataFieldReference(OpentachFieldNames.CG_CONTRATO_FIELD);

	}

}
