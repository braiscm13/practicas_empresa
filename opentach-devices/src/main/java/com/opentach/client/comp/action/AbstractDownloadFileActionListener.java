package com.opentach.client.comp.action;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.model.TachoFile;
import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.opentach.client.MonitorProvider;
import com.opentach.client.util.download.IFileRenamer;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.tacho.TachoFileStore;
import com.opentach.common.util.concurrent.PoolExecutors;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;

public abstract class AbstractDownloadFileActionListener extends AbstractActionListenerButton {

	private static final Logger				logger	= LoggerFactory.getLogger(AbstractDownloadFileActionListener.class);

	private static final ExecutorService	exs		= PoolExecutors.newFixedThreadPool("AbstractDownloadFileActionListener", 1);

	public AbstractDownloadFileActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable<?, ?> params) throws Exception {
		super(button, formComponent, params);
	}

	public AbstractDownloadFileActionListener(Hashtable<?, ?> params) throws Exception {
		super(params);
	}

	public AbstractDownloadFileActionListener(UButton button, Hashtable<?, ?> params) throws Exception {
		super(button, params);
	}

	protected void doOnDownloadFile(String tblFicheros, final String cgContrato, boolean rename) {
		// Descargo todos los ficheros seleccionados de la tabla.
		Table tbl = (Table) this.getForm().getDataFieldReference(tblFicheros);
		boolean askoverwrite = true;
		EntityResult selectedRowData = new EntityResult(tbl.getSelectedRowData());
		int nregs = selectedRowData.calculateRecordNumber();
		if (nregs == 0) {
			this.getForm().message("M_SELECCIONE_FICHERO", Form.WARNING_MESSAGE);
			return;
		}
		boolean existefichero = false;
		for (Object ob : (Vector<?>) selectedRowData.get(OpentachFieldNames.IDFILE_FIELD)) {
			if (ob != null) {
				existefichero = true;
				break;
			}
		}
		if (!existefichero) {
			return;
		}

		final MonitorProvider mp = (MonitorProvider) this.getReferenceLocator();

		File dir = this.askOutputFolder();
		if (dir == null) {
			return;
		}
		final List<DownloadFileParams> filesToDownload = new ArrayList<DownloadFileParams>();
		String outputFormat = this.askOutputFormat();
		if (outputFormat == null) {
			return;
		}

		for (int i = 0; i < nregs; i++) {
			Hashtable<?, ?> data = selectedRowData.getRecordValues(i);
			Object idfile = data.get(OpentachFieldNames.IDFILE_FIELD);
			if (idfile != null) {
				String nombProcess = (String) data.get(OpentachFieldNames.FILENAME_PROCESSED_FIELD);
				String nombOrig = (String) data.get(OpentachFieldNames.FILENAME_FIELD);
				String tipo = (String) data.get(OpentachFieldNames.TYPE_FIELD);

				File file = new File(dir, nombOrig != null ? nombOrig : nombProcess);
				if (askoverwrite && file.exists()) {
					String[] buttons = { " Si ", " Si a todo ", " No ", " Cancelar " };
					String msg = ApplicationManager.getTranslation("M_FICHERO_EXISTE_CONTINUAR");
					int rc = JOptionPane.showOptionDialog(null, msg, "Información", JOptionPane.INFORMATION_MESSAGE, 0, null, buttons, buttons[2]);
					if ((rc == -1) || (rc == 3)) { // CANCEL
						// OPERATION
						return;
					} else if (rc == 1) { // OVERWRITE
						// WITHOUT ASK
						// AGAIN
						askoverwrite = false;
					} else if (rc == 2) {
						// NOT OVERWRITE THIS
						continue;
					}
				}
				DownloadFileParams dfp = new DownloadFileParams(mp, idfile, tipo, cgContrato, file, outputFormat, nombOrig, nombProcess, rename);
				filesToDownload.add(dfp);
			}
		}
		this.descagarFicheros(filesToDownload);
	}

	private File askOutputFolder() {
		final MonitorProvider mp = (MonitorProvider) this.getReferenceLocator();
		TachoFileStore store = mp.getFileStore();
		String dir = store.getTGDStore() != null ? store.getTGDStore().getAbsolutePath() : null;

		JFileChooser chooser = new JFileChooser(dir);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setMultiSelectionEnabled(false);

		String msg2 = ApplicationManager.getTranslation("M_SELECCIONE_DIRECTORIO");
		chooser.setDialogTitle(msg2);
		chooser.setToolTipText(msg2);
		chooser.setApproveButtonText(ApplicationManager.getTranslation("T_GUARDAR"));
		chooser.setLocale(ApplicationManager.getLocale());
		chooser.updateUI();

		int returnVal = chooser.showSaveDialog(ApplicationManager.getApplication().getFrame());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		}
		return null;
	}

	private String askOutputFormat() {
		String smotivo = "TGD";
		JPanel panel = new JPanel(new BorderLayout());
		JPanel panel2 = new JPanel(new GridLayout(5, 1, 20, 20));
		panel2.add(new JLabel(ApplicationManager.getTranslation("SELECCIONE_FORMATO")));

		String localeValue = ApplicationManager.getApplication().getLocale().toString();
		final JComboBox<String> tipoEstado = new JComboBox<String>(
				new String[] { ApplicationManager.getTranslation("TGD"), ApplicationManager.getTranslation("DDD"), ApplicationManager.getTranslation("A1B") });

		try {
			Entity ent = ApplicationManager.getApplication().getReferenceLocator().getEntityReference("EPreferenciasServidor");
			EntityResult res = ent.query(EntityResultTools.keysvalues("NOMBRE", "Formato.Fichero." + localeValue), EntityResultTools.attributes("VALOR"),
					ApplicationManager.getApplication().getReferenceLocator().getSessionId());
			CheckingTools.checkValidEntityResult(res, "ERROR_GETTING_FORMATO", true, true, new Object[] {});
			String formato = (String) res.getRecordValues(0).get("VALOR");
			switch (formato) {
				case "TGD":
					tipoEstado.setSelectedItem(ApplicationManager.getTranslation("TGD"));
					break;
				case "DDD":
					tipoEstado.setSelectedItem(ApplicationManager.getTranslation("DDD"));
					break;
				case "A1B":
					tipoEstado.setSelectedItem(ApplicationManager.getTranslation("A1B"));
					break;
				default:
					tipoEstado.setSelectedItem(ApplicationManager.getTranslation("TGD"));
					break;
			}
		} catch (Exception err) {
			AbstractDownloadFileActionListener.logger.warn(null, err);
			tipoEstado.setSelectedItem(ApplicationManager.getTranslation("TGD"));
		}
		panel2.add(tipoEstado);
		panel.add(panel2, BorderLayout.CENTER);
		// elige el mensaje que le quiere enviar
		int resp = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(this.getForm()), panel, "Seleccionar formato fichero", JOptionPane.OK_CANCEL_OPTION);
		if (resp != JOptionPane.OK_OPTION) {
			return null;
		}

		int nrespuestas = tipoEstado.getSelectedIndex();

		if (nrespuestas == 0) {
			smotivo = "TGD";
		} else if (nrespuestas == 1) {
			smotivo = "DDD";
		} else {
			smotivo = "A1B";
		}
		return smotivo;
	}

	protected void descagarFicheros(List<DownloadFileParams> params) {
		for (DownloadFileParams dfp : params) {
			AbstractDownloadFileActionListener.exs.submit(dfp);
		}
	}

	public class DownloadFileParams implements Runnable {

		private final Object			idFile;
		private final String			tipo;
		private final File				file;
		private final String			contrato;
		private final MonitorProvider	mp;
		private final String			outputFormat;
		private final boolean			rename;
		private final String			nombProcess;
		private final String			nombOrig;

		public DownloadFileParams(MonitorProvider mp, Object idFile, String type, String contract, File file, String outputFormat, String nombOrig, String nombProcess,
				boolean rename) {
			this.mp = mp;
			this.idFile = idFile;
			this.tipo = type;
			this.contrato = contract;
			this.file = file;
			this.outputFormat = outputFormat;
			this.rename = rename;
			this.nombOrig = nombOrig;
			this.nombProcess = nombProcess;

		}

		public Object getIdFile() {
			return this.idFile;
		}

		public String getType() {
			return this.tipo;
		}

		public File getFile() {
			return this.file;
		}

		public String getContrato() {
			return this.contrato;
		}

		@Override
		public void run() {
			this.mp.getDownloadMonitor().descargarFichero(this.idFile, this.tipo, this.contrato, this.file, AbstractDownloadFileActionListener.this.getForm(), new IFileRenamer() {

				@Override
				public String renameFile(File currentLocation, String originalName, String destinationName) {
					// TODO quitar el check de rename en los formularios
					String nomb = null;
					if (DownloadFileParams.this.nombProcess != null) {
						nomb = DownloadFileParams.this.nombProcess;
					} else if (DownloadFileParams.this.nombOrig != null) {
						nomb = DownloadFileParams.this.nombOrig;
					}
					byte format = TachoFile.FILENAME_FORMAT_SPAIN;
					if ("DDD".equals(DownloadFileParams.this.outputFormat)) {
						format = TachoFile.FILENAME_FORMAT_EUROPE;
					} else if ("A1B".equals(DownloadFileParams.this.outputFormat)) {
						format = TachoFile.FILENAME_FORMAT_FRANCE;
					}
					try {
						TachoFile tachoFile = TachoFile.readTachoFile(currentLocation);
						return tachoFile.computeFileName(nomb, format, null);
					} catch (Exception error) {
						AbstractDownloadFileActionListener.logger.error(null, error);
					}
					return originalName;

				}
			});
		}

	}

	public static final String TBLFICHEROS = "EInformeFicherosTGD";

	public static void installListener(Form managedForm) {
		// TODO hacer esto bien
		Button bFormDownload = managedForm.getButton("bdownload");
		if (bFormDownload != null) {
			bFormDownload.setVisible(false);
		}
		Button btn = managedForm.getButton("btnDownload");
		if ((btn != null) && (bFormDownload != null)) {
			try {
				ActionListener al = new AbstractDownloadFileActionListener((UButton) btn, new Hashtable<Object, Object>()) {

					@Override
					public void actionPerformed(ActionEvent e) {
						String scgContrato = (String) managedForm.getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD);
						this.doOnDownloadFile(AbstractDownloadFileActionListener.TBLFICHEROS, scgContrato, false);
					}
				};
				btn.addActionListener(al);
				bFormDownload.addActionListener(al);
			} catch (Exception e1) {
				AbstractDownloadFileActionListener.logger.error(null, e1);
			}
			final Table tblFicheros = (Table) managedForm.getDataFieldReference(AbstractDownloadFileActionListener.TBLFICHEROS);
			ListSelectionListener tablelist = new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					if (tblFicheros.getSelectedRows().length > 0) {
						managedForm.enableButton("btnDownload");
					} else {
						managedForm.disableButton("btnDownload");
					}
				}
			};
			tblFicheros.getJTable().getSelectionModel().addListSelectionListener(tablelist);
		}
	}
}
