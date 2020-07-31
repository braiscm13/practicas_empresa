package com.opentach.adminclient.modules.files;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import javax.swing.AbstractButton;
import javax.swing.JDialog;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.Form;
import com.ontimize.gui.table.Table;
import com.opentach.common.OpentachFieldNames;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;

public class IMListadoFicherosViewTraceActionListener extends AbstractActionListenerButton {

	@FormComponent(attr = IMListadoFicheros.EFICHEROS)
	private Table	tFilePend;

	private JDialog	jdReg	= null;
	private Form	fReg	= null;

	public IMListadoFicherosViewTraceActionListener() throws Exception {
		super();
	}

	public IMListadoFicherosViewTraceActionListener(Hashtable params) throws Exception {
		super(params);
	}

	public IMListadoFicherosViewTraceActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public IMListadoFicherosViewTraceActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		int[] filas = this.tFilePend.getSelectedRows();
		if (filas.length == 0) {
			return;
		} else if (filas.length > 1) {
			this.getForm().message("M_AVISO_DEBE_SELECCIONAR_SOLO_UNA_FILA", Form.WARNING_MESSAGE);
			return;
		}
		Number idFile = (Number) this.tFilePend.getRowKey(filas[0], OpentachFieldNames.IDFILE_FIELD);
		if (this.jdReg == null) {
			this.fReg = this.getFormManager().getFormCopy("formRegistroOperacionesFichero.xml");
			this.jdReg = this.fReg.putInModalDialog();
		}
		this.fReg.getInteractionManager().setInitialState();
		this.fReg.setDataFieldValue(OpentachFieldNames.IDFILE_FIELD, idFile);
		((Table) this.fReg.getElementReference("EFicherosRegistro")).refresh();
		this.jdReg.setLocationRelativeTo(null);
		this.jdReg.setVisible(true);
	}

}
