package com.opentach.adminclient.modules.files;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Hashtable;

import javax.swing.AbstractButton;
import javax.swing.JDialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.Form;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.MonitorProvider;
import com.opentach.client.modules.files.IMDescargaTGD;
import com.opentach.client.util.download.DownloadMonitor;
import com.opentach.common.OpentachFieldNames;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;

public class IMListadoFicherosViewFileActionListener extends AbstractActionListenerButton {

	private static final Logger	logger		= LoggerFactory.getLogger(IMListadoFicherosViewFileActionListener.class);
	@FormComponent(attr = IMListadoFicheros.EFICHEROS)
	private Table				tFilePend;

	protected JDialog			chartdlg	= null;
	protected Form				chartform	= null;

	public IMListadoFicherosViewFileActionListener() throws Exception {
		super();
	}

	public IMListadoFicherosViewFileActionListener(Hashtable params) throws Exception {
		super(params);
	}

	public IMListadoFicherosViewFileActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public IMListadoFicherosViewFileActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		// Descargo todos los ficheros seleccionados de la tabla.
		Table tbl = this.tFilePend;

		if (tbl != null) {
			int[] selectedidx = tbl.getSelectedRows();
			if ((selectedidx == null) || (selectedidx.length != 1)) {
				this.getForm().message("M_SELECCIONE_FICHERO", Form.WARNING_MESSAGE);
			} else {
				String dir = System.getProperty("user.dir");
				Number idfile = (Number) tbl.getRowValue(selectedidx[0], OpentachFieldNames.IDFILE_FIELD);
				// String nombProcess = (String)
				// tbl.getValorFila(selectedidx[i], rename ? NOMBPROCESS_COL:
				// NOMB_COL);
				String nombProcess = null;
				try {
					nombProcess = (String) tbl.getRowValue(selectedidx[0], OpentachFieldNames.FILENAME_PROCESSED_FIELD);
				} catch (IllegalArgumentException e) {
				}
				String tipo = (String) tbl.getRowValue(selectedidx[0], OpentachFieldNames.TYPE_FIELD);
				if (dir != null) {
					File file;
					try {
						// file=File.createTempFile(nombProcess, ".tmp");
						// file.deleteOnExit();
						file = new File(dir + File.separator + nombProcess);
						DownloadMonitor dwm = ((MonitorProvider) this.getReferenceLocator()).getDownloadMonitor();
						dwm.descargarFichero(idfile, tipo, null, file, this.getForm());
						// FIXME: y esto???
						synchronized (dwm.getLock()) {
							dwm.getLock().wait(5000);
						}
						if (this.chartdlg == null) {
							this.chartform = this.getFormManager().getFormCopy("formDescargaTGDTaco.xml");
							this.chartform.getInteractionManager().setInitialState();
							this.chartdlg = ((IMListadoFicheros) this.getInteractionManager()).getFormDialog(this.chartform, false);
							this.chartdlg.setSize(800, 600);
						}
						((IMDescargaTGD) this.chartform.getInteractionManager()).processFile(file);
						this.chartdlg.setVisible(true);
						file.delete();

					} catch (Exception e) {
						MessageManager.getMessageManager().showExceptionMessage(e, IMListadoFicherosViewFileActionListener.logger);
					}
				}
			}
		}
	}

}
