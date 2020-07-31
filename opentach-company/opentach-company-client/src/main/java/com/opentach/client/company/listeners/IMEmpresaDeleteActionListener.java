package com.opentach.client.company.listeners;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import javax.swing.AbstractButton;
import javax.swing.JDialog;

import com.ontimize.gui.Form;
import com.ontimize.gui.FormManager;
import com.ontimize.gui.InteractionManager;
import com.opentach.common.OpentachFieldNames;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UFormHeaderButton;

public class IMEmpresaDeleteActionListener extends AbstractActionListenerButton {

	private Form		fEliminar;
	private JDialog		dEliminar;

	public IMEmpresaDeleteActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public IMEmpresaDeleteActionListener(UFormHeaderButton button, Hashtable params) throws Exception {
		super(button, button, params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (this.dEliminar == null) {
			this.fEliminar = this.getForm().getFormManager().getFormCopy("formEliminarEmpresa.xml");
			this.fEliminar.getInteractionManager().setInitialState();
			this.dEliminar = this.fEliminar.putInModalDialog("ELIMINAR_EMPRESA", this.getForm());
		}
		this.fEliminar.setDataFieldValue(OpentachFieldNames.CIF_FIELD, this.getForm().getDataFieldValue(OpentachFieldNames.CIF_FIELD));
		this.fEliminar.setDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD, this.getForm().getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD));

		this.dEliminar.setVisible(true);

		if ((this.getForm().getFormManager() instanceof FormManager) && (((FormManager) this.getForm().getFormManager()).getTree() != null)) {
			((FormManager) this.getForm().getFormManager()).getTree().updatePath(this.getForm().getAssociatedTreePath().getParentPath());
		}

		this.getForm().getButton("cancel").doClick();
	}

	@Override
	protected boolean getEnableValueToSet() {
		return (this.getInteractionManager().getCurrentMode() == InteractionManager.UPDATE);
	}

}
