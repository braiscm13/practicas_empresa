package com.opentach.adminclient.modules.files;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.AbstractButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.Form;
import com.ontimize.gui.table.Table;
import com.opentach.client.OpentachClientLocator;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.contract.ITachoFileContractService;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;

public class IMListadoFicherosAssignContractActionListener extends AbstractActionListenerButton {

	private static final Logger	logger	= LoggerFactory.getLogger(IMListadoFicherosAssignContractActionListener.class);

	@FormComponent(attr = IMListadoFicheros.EFICHEROS)
	private Table	tFilePend;


	public IMListadoFicherosAssignContractActionListener() throws Exception {
		super();
	}

	public IMListadoFicherosAssignContractActionListener(Hashtable params) throws Exception {
		super(params);
	}

	public IMListadoFicherosAssignContractActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public IMListadoFicherosAssignContractActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		int[] filas = this.tFilePend.getSelectedRows();
		if (filas.length == 0) {
			return;
		}
		List<Number> lIDFiles = new ArrayList<Number>();
		for (int i = 0; i < filas.length; i++) {
			lIDFiles.add((Number) this.tFilePend.getRowKey(filas[i], OpentachFieldNames.IDFILE_FIELD));
		}
		try {
			OpentachClientLocator bref = (OpentachClientLocator) this.getReferenceLocator();
			bref.getRemoteService(ITachoFileContractService.class).tryToAssignContract(lIDFiles, bref.getSessionId());
		} catch (Exception e) {
			this.getForm().message("M_ERROR_ASIGNACION", Form.ERROR_MESSAGE);
		} finally {
			try {
				((IMListadoFicheros) this.getInteractionManager()).doOnQuery();
			} catch (Exception e) {
				IMListadoFicherosAssignContractActionListener.logger.error(null, e);
			}
		}
	}

}
