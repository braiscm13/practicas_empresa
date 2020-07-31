package com.opentach.adminclient.modules.files;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import javax.swing.AbstractButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.Form;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.OpentachClientLocator;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.process.ITachoFileProcessService;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.tasks.USwingWorker;
import com.utilmize.client.gui.tasks.WorkerStatusInfo;

public class IMListadoFicherosNowActionListener extends AbstractActionListenerButton {

	private static final Logger	logger		= LoggerFactory.getLogger(IMListadoFicherosNowActionListener.class);

	@FormComponent(attr = IMListadoFicheros.EFICHEROS)
	private Table				tFilePend;

	public IMListadoFicherosNowActionListener() throws Exception {
		super();
	}

	public IMListadoFicherosNowActionListener(Hashtable params) throws Exception {
		super(params);
	}

	public IMListadoFicherosNowActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public IMListadoFicherosNowActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		int[] filas = this.tFilePend.getSelectedRows();
		if (filas.length > 1) {
			this.getForm().message("M_AVISO_DEBE_SELECCIONAR_SOLO_UNA_FILA", Form.WARNING_MESSAGE);
		} else if (filas.length > 0) {
			final Number idFile = (Number) this.tFilePend.getRowKey(filas[0], OpentachFieldNames.IDFILE_FIELD);

			new USwingWorker<Void, Void>() {

				@Override
				protected Void doInBackground() throws Exception {
					this.fireStatusUpdate(new WorkerStatusInfo("Procesando fichero....", null, null));
					ITachoFileProcessService processService = ((OpentachClientLocator) IMListadoFicherosNowActionListener.this
							.getReferenceLocator()).getRemoteService(ITachoFileProcessService.class);
					processService.processNow(idFile, IMListadoFicherosNowActionListener.this.getSessionId());
					return null;
				}
				@Override
				protected void done() {
					try {
						this.uget();
						((IMListadoFicheros) IMListadoFicherosNowActionListener.this.getInteractionManager()).consultar();
					} catch (Throwable error) {
						// IMListadoFicherosNowActionListener.this.getForm().message("M_ERROR_PROCESADO_AHORA", Form.ERROR_MESSAGE);
						MessageManager.getMessageManager().showExceptionMessage(error, IMListadoFicherosNowActionListener.logger);
					}
				}
			}.executeOperation(this.getForm());
		}
	}
}
