package com.opentach.adminclient.modules.files;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.AbstractButton;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.Form;
import com.ontimize.gui.table.Table;
import com.opentach.client.OpentachClientLocator;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.process.ITachoFileProcessService;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;

public class IMListadoFicherosPriorityActionListener extends AbstractActionListenerButton {

	@FormComponent(attr = IMListadoFicheros.EFICHEROS)
	private Table	tFilePend;

	public IMListadoFicherosPriorityActionListener() throws Exception {
		super();
	}

	public IMListadoFicherosPriorityActionListener(Hashtable params) throws Exception {
		super(params);
	}

	public IMListadoFicherosPriorityActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public IMListadoFicherosPriorityActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		int[] filas = this.tFilePend.getSelectedRows();
		if (filas.length > 0) {
			List<Number> lIdFicheros = new ArrayList<Number>();
			for (int i = 0; i < filas.length; i++) {
				lIdFicheros.add((Number) this.tFilePend.getRowKey(filas[i], OpentachFieldNames.IDFILE_FIELD));
			}
			try {
				ITachoFileProcessService processService = ((OpentachClientLocator) this.getReferenceLocator())
						.getRemoteService(ITachoFileProcessService.class);
				processService.addPriorityFiles(lIdFicheros, this.getSessionId());
			} catch (Exception e) {
				this.getForm().message("M_ERROR_ENVIO_PRIORIDAD", Form.ERROR_MESSAGE);
			}
		}
	}

}
