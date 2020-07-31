package com.opentach.adminclient.modules.files;

import java.awt.Dialog.ModalExclusionType;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Hashtable;

import javax.swing.AbstractButton;
import javax.swing.JDialog;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.viewer.FIMMain;
import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.Form;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.MonitorProvider;
import com.opentach.client.util.download.DownloadMonitor;
import com.opentach.common.OpentachFieldNames;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.tasks.USwingWorker;

public class IMListadoFicherosDetailFileActionListener extends AbstractActionListenerButton {

	private static final Logger	logger		= LoggerFactory.getLogger(IMListadoFicherosDetailFileActionListener.class);
	@FormComponent(attr = IMListadoFicheros.EFICHEROS)
	private Table				tFilePend;

	public IMListadoFicherosDetailFileActionListener() throws Exception {
		super();
	}

	public IMListadoFicherosDetailFileActionListener(Hashtable params) throws Exception {
		super(params);
	}

	public IMListadoFicherosDetailFileActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public IMListadoFicherosDetailFileActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		// Descargo todos los ficheros seleccionados de la tabla.

		int[] selectedidx = this.tFilePend.getSelectedRows();
		if ((selectedidx == null) || (selectedidx.length != 1)) {
			this.getForm().message("M_SELECCIONE_FICHERO", Form.WARNING_MESSAGE);
		} else {
			String dir = System.getProperty("user.dir");
			Number idfile = (Number) this.tFilePend.getRowValue(selectedidx[0], OpentachFieldNames.IDFILE_FIELD);
			String nombProcess = (String) this.tFilePend.getRowValue(selectedidx[0], OpentachFieldNames.FILENAME_PROCESSED_FIELD);
			String tipo = (String) this.tFilePend.getRowValue(selectedidx[0], OpentachFieldNames.TYPE_FIELD);

			new USwingWorker<File, Void>() {
				@Override
				protected File doInBackground() throws Exception {
					File file = new File(dir, nombProcess);
					DownloadMonitor dwm = ((MonitorProvider) IMListadoFicherosDetailFileActionListener.this.getReferenceLocator())
							.getDownloadMonitor();
					dwm.descargarFichero(idfile, tipo, null, file, IMListadoFicherosDetailFileActionListener.this.getForm());
					synchronized (dwm.getLock()) {
						dwm.getLock().wait(15000);
					}
					return file;
				}

				@Override
				protected void done() {
					try {
						File file = this.uget();
						IMListadoFicherosDetailFileActionListener.this.processFile(tipo, file, nombProcess);
						file.delete();
					} catch (Throwable e) {
						MessageManager.getMessageManager().showExceptionMessage(e, IMListadoFicherosDetailFileActionListener.logger);
					}

				}
			}.executeOperation(this.getForm());
		}
	}

	protected void processFile(String tipo, File file, String nombProcess) {
		Form fileForm = this.getFormManager().getFormCopy("formMain.form");
		JDialog dialog = fileForm.putInModalDialog(this.getForm());
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		// dialog.setModal(false);
		dialog.setModalityType(ModalityType.MODELESS);
		dialog.setModalExclusionType(ModalExclusionType.NO_EXCLUDE);
		dialog.setSize(1300, 950);
		dialog.setTitle(nombProcess);
		((FIMMain) fileForm.getInteractionManager()).loadFile(file);
		dialog.setVisible(true);
	}

}
