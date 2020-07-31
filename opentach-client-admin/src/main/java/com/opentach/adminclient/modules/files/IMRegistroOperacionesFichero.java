package com.opentach.adminclient.modules.files;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.Form;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.BooleanCellRenderer;
import com.ontimize.gui.table.Table;
import com.ontimize.util.notice.BundleCellRenderer;
import com.opentach.client.modules.IMDataRoot;

public class IMRegistroOperacionesFichero extends IMDataRoot {

	protected static final String	TBLFICHEROS	= "EFicherosRegistro";

	@FormComponent(attr = IMRegistroOperacionesFichero.TBLFICHEROS)
	protected Table					tbRegistro	= null;

	@Override
	public void registerInteractionManager(Form formulario, IFormManager gestorForms) {
		super.registerInteractionManager(formulario, gestorForms);
		if (this.tbRegistro != null) {
			this.tbRegistro.setRendererForColumn("TIPO", new BundleCellRenderer());
			this.tbRegistro.setRendererForColumn("ANALIZAR", new BooleanCellRenderer());
			this.tbRegistro.setRendererForColumn("PDA", new BooleanCellRenderer());
		}
	}

	@Override
	public void setInitialState() {
		this.setUpdateMode();
		if (this.tbRegistro != null) {
			this.tbRegistro.setEnabled(true);
		}
	}
}
