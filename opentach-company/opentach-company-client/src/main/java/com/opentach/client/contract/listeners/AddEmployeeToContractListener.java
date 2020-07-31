package com.opentach.client.contract.listeners;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import javax.swing.JDialog;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.table.Table;
import com.opentach.common.OpentachFieldNames;
import com.utilmize.client.gui.buttons.AbstractTableRelatedListener;
import com.utilmize.client.gui.buttons.UButton;

public class AddEmployeeToContractListener extends AbstractTableRelatedListener {

	@FormComponent(attr = "EConductorCont")
	private final Table	tbConductores	= null;

	private Form		fInsertar		= null;
	private JDialog		jInsertar		= null;

	public AddEmployeeToContractListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.ensureDetailForm();
		this.fInsertar.getInteractionManager().setInsertMode();
		this.fInsertar.setDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD, this.getForm().getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD));
		this.fInsertar.setDataFieldValue(OpentachFieldNames.CIF_FIELD, this.getForm().getDataFieldValue(OpentachFieldNames.CIF_FIELD));
		this.fInsertar.setDataFieldValue("TIPO", this.getForm().getDataFieldValue("TIPO"));
		this.fInsertar.setDataFieldValue("USUARIO_ALTA", this.getForm().getDataFieldValue("USUARIO_ALTA"));
		this.fInsertar.setDataFieldValue("F_ALTA", this.getForm().getDataFieldValue("F_ALTA"));
		this.jInsertar.setVisible(true);
		this.tbConductores.refreshInThread(0);
	}

	private void ensureDetailForm() {
		if (this.jInsertar != null) {
			return;
		}
		this.fInsertar = this.getFormManager().getFormCopy("formConductorCont.xml");
		this.jInsertar = this.fInsertar.putInModalDialog(ApplicationManager.getTranslation("CONDUCTOR_CONT"), this.getForm());
		this.jInsertar.setIconImage(ApplicationManager.getApplication().getFrame().getIconImage());
	}

}
