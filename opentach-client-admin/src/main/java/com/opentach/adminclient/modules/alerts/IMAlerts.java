package com.opentach.adminclient.modules.alerts;

import com.ontimize.gui.Form;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.locator.EntityReferenceLocator;
import com.utilmize.client.fim.UBasicFIM;

public class IMAlerts extends UBasicFIM {

	protected Table						alertsTable	= null;
	protected EntityReferenceLocator	buscador	= null;

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.buscador = this.formManager.getReferenceLocator();
		this.alertsTable = (Table) this.managedForm.getElementReference("EAlerts");
	}

	@Override
	public void setQueryInsertMode() {
		super.setQueryInsertMode();
		this.inicializaFromulario();
		if (this.alertsTable != null) {
			this.alertsTable.refresh();
		}
	}

	protected void inicializaFromulario() {
		this.managedForm.enableButton("pauseAlert");
		this.managedForm.enableButton("resumeAlert");
		this.managedForm.enableDataField("group");
		if (this.alertsTable != null) {
			this.alertsTable.setEnabled(true);
		}
	}

}
