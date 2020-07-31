package com.opentach.client.modules.files;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.model.TachoFile;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ExtendedOperationThread;
import com.ontimize.gui.Form;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.opentach.client.MonitorProvider;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.modules.IMRoot;
import com.opentach.client.util.SoundManager;
import com.opentach.client.util.TGDFileInfo;
import com.opentach.client.util.upload.UploadMonitor;
import com.opentach.common.ITGDFileConstants;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.tacho.TachoFileStore;
import com.opentach.common.tacho.TachoFileWrapper;
import com.opentach.common.user.IUserData;

public class IMCargaFicheros extends IMRoot implements ITGDFileConstants {

	private static final Logger				logger	= LoggerFactory.getLogger(IMCargaFicheros.class);
	protected Map<String, TableFileInfo>	selectedTachoFiles;
	protected Table							tblFicherosTGD;
	private JFileChooser					chooser;
	private static final int				NUMMAX	= System.getProperty("NUMMAX_FILES_TOSEND") != null ? Integer.parseInt(System.getProperty("NUMMAX_FILES_TOSEND")) : 100;

	public IMCargaFicheros() {
		super();
		this.selectedTachoFiles = new HashMap<>();
		this.fieldsChain.clear();
		this.fieldsChain.add(OpentachFieldNames.CIF_FIELD);
		this.fieldsChain.add(OpentachFieldNames.CG_CONTRATO_FIELD);
		this.fieldsChain.add(ITGDFileConstants.FILE_ENTITY);
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		Button b = this.managedForm.getButton("bdownload");
		b.setVisible(false);
		this.tblFicherosTGD = (Table) this.managedForm.getDataFieldReference("FicherosSeleccionados");
		Button btnenviar = this.managedForm.getButton("btnUpload");
		if (btnenviar != null) {
			btnenviar.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					ExtendedOperationThread exop = new ExtendedOperationThread(ApplicationManager.getTranslation("M_PROCESANDO_DATOS")) {
						@Override
						public void run() {
							this.hasStarted = true;
							try {
								int[] selected = IMCargaFicheros.this.tblFicherosTGD.getSelectedRows();
								List<TachoFileWrapper> files2Send = new ArrayList<>();
								if (selected.length > 0) {
									this.progressDivisions = selected.length;
									long begin = System.currentTimeMillis();
									for (int i = 0; (selected != null) && (i < selected.length); i++) {
										this.currentPosition++;
										String filename = ((String) IMCargaFicheros.this.tblFicherosTGD.getRowValue(selected[i], OpentachFieldNames.FILENAME_FIELD));
										files2Send.add(IMCargaFicheros.this.selectedTachoFiles.get(filename).tgdf);
										IMCargaFicheros.this.selectedTachoFiles.remove(filename);
										this.estimatedTimeLeft = (int) ((((System
												.currentTimeMillis() - begin) / ((double) this.currentPosition)) * (selected.length - this.currentPosition)));
									}
									IMCargaFicheros.this.enviarFicheros(files2Send, "FICHERO");
									IMCargaFicheros.this.actualizarTablaFicheros();
								} else {
									this.res = "M_SELECCIONE_FICHEROS_ENVIAR";
								}
							} catch (Exception e) {
								IMCargaFicheros.logger.error(null, e);
								this.res = e.getMessage();
							} finally {
								this.hasFinished = true;
							}
						}
					};

					ApplicationManager.proccessOperation(exop, 50);
					Object res = exop.getResult();
					if (res instanceof String) {
						IMCargaFicheros.this.managedForm.message((String) res, Form.INFORMATION_MESSAGE);
					}
				}
			});
		}
		// Seleccionar Files.
		Button bSelecTGD = this.managedForm.getButton("btnSelecTGD");
		ActionListener selectlist = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO: Comprobación cif usuario != null && cg_contrato == null
				try {
					IUserData user = ((OpentachClientLocator) IMCargaFicheros.this.formManager.getReferenceLocator()).getUserData();
					if ((user.getCIF() != null) && (user.getActiveContract(user.getCIF()) == null)) {
						IMCargaFicheros.this.managedForm.message("", Form.INFORMATION_MESSAGE);
						return;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					return;
				}
				TachoFileStore fileStore = ((MonitorProvider) IMCargaFicheros.this.formManager.getReferenceLocator()).getFileStore();
				String store = fileStore.getTGDStore() != null ? fileStore.getTGDStore().getAbsolutePath() : System.getProperty("user.home");
				String title = ApplicationManager.getTranslation("SELECCIONAR");

				if (IMCargaFicheros.this.chooser == null) {
					IMCargaFicheros.this.chooser = new JFileChooser(store);
					IMCargaFicheros.this.chooser.setDialogTitle(title);
					IMCargaFicheros.this.chooser.setToolTipText(title);
					IMCargaFicheros.this.chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					IMCargaFicheros.this.chooser.setMultiSelectionEnabled(true);
					IMCargaFicheros.this.chooser.addPropertyChangeListener(new PropertyChangeListener() {

						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							if (JFileChooser.SELECTED_FILES_CHANGED_PROPERTY.equals(evt.getPropertyName())) {
								File[] files = IMCargaFicheros.this.chooser.getSelectedFiles();
								if (files.length > IMCargaFicheros.NUMMAX) {
									IMCargaFicheros.this.chooser.setLocale(ApplicationManager.getLocale());
									IMCargaFicheros.this.chooser.updateUI();
									IMCargaFicheros.this.chooser.setSelectedFiles(null);
									IMCargaFicheros.this.managedForm.message(ApplicationManager.getTranslation("M_ERROR_MAX_SELECTED", ApplicationManager.getApplicationBundle(),
											new Object[] { IMCargaFicheros.NUMMAX }), Form.ERROR_MESSAGE);
								}
							}
						}
					});
				}
				IMCargaFicheros.this.chooser.setLocale(ApplicationManager.getLocale());
				IMCargaFicheros.this.chooser.updateUI();
				IMCargaFicheros.this.chooser.setApproveButtonText(ApplicationManager.getTranslation("SELECCIONAR", IMCargaFicheros.this.managedForm.getResourceBundle()));
				int returnVal = IMCargaFicheros.this.chooser.showOpenDialog(((JButton) e.getSource()).getParent());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					final File[] files = IMCargaFicheros.this.chooser.getSelectedFiles();
					ExtendedOperationThread opth = new ExtendedOperationThread(ApplicationManager.getTranslation("Cargando_Ficheros_")) {
						@Override
						public void run() {
							this.hasStarted = true;
							this.progressDivisions = files.length;
							try {
								long begin = System.currentTimeMillis();
								List<String> corruptedfiles = new ArrayList<String>();
								for (File file : files) {
									this.currentPosition++;
									try {
										TachoFile tgdf = TachoFile.readTachoFile(file);
										String computeFileName = tgdf.computeFileName(null,TachoFile.FILENAME_FORMAT_SPAIN, null);
										Date extractDate = tgdf.getExtractTime(null);
										String owner = tgdf.getOwnerId();
										IMCargaFicheros.this.selectedTachoFiles.put(computeFileName,
												new TableFileInfo(file.getName(), computeFileName, new TachoFileWrapper(tgdf, file), extractDate, owner));
									} catch (Exception err) {
										corruptedfiles.add(file.getName());
									}
									this.estimatedTimeLeft = (int) ((((System
											.currentTimeMillis() - begin) / ((double) this.currentPosition)) * (files.length - this.currentPosition)));
								}
								IMCargaFicheros.this.actualizarTablaFicheros();
								if (corruptedfiles.size() > 0) {
									this.hasFinished = true;
									IMCargaFicheros.this.showCorruptedFilesInfo(corruptedfiles);
								}
							} finally {
								this.hasFinished = true;
							}
						}
					};
					ApplicationManager.proccessOperation((JDialog) SwingUtilities.getWindowAncestor(IMCargaFicheros.this.managedForm), opth, 50);
				}
			}
		};

		if (bSelecTGD != null) {
			bSelecTGD.addActionListener(selectlist);
		}
		// Seleccionar Files.
		Button bDeleteTGD = this.managedForm.getButton("btnDeleteTGD");
		ActionListener deletefiles = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				IMCargaFicheros.this.selectedTachoFiles.clear();
				Table tbl = IMCargaFicheros.this.tblFicherosTGD;
				int[] srows = tbl.getSelectedRows();
				for (int i = 0; (srows != null) && (i < srows.length); i++) {
					String filename = (String) tbl.getRowValue(srows[i], OpentachFieldNames.FILENAME_FIELD);
					try {
						IMCargaFicheros.this.selectedTachoFiles.remove(filename);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					tbl.deleteRow(srows[i]);
				}
				// actualizarTablaFicheros();
			}
		};

		if (bDeleteTGD != null) {
			bDeleteTGD.addActionListener(deletefiles);
		}
	}

	protected void enviarFicheros(List<TachoFileWrapper> files, String colname) {
		for (TachoFileWrapper file : files) {
			this.adjuntar(this.managedForm, file.getFile());
		}
	}

	private final void showCorruptedFilesInfo(List<String> files) {
		if (!files.isEmpty()) {
			JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this.managedForm), ApplicationManager.getTranslation("Informacion"));
			dlg.setModal(true);
			dlg.getContentPane().setLayout(new BorderLayout());
			dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			JLabel info = new JLabel(ApplicationManager.getTranslation("Info_Listado_Ficheros_Corruptos"));
			info.setPreferredSize(new Dimension(350, 40));
			dlg.getContentPane().add(info, BorderLayout.NORTH);
			JList<Object> list = new JList(new Vector<String>(files));
			JScrollPane scp = new JScrollPane(list);
			dlg.getContentPane().add(scp);
			dlg.pack();
			dlg.setLocationRelativeTo(this.managedForm);
			dlg.setVisible(true);
		}
	}

	@SuppressWarnings("unchecked")
	protected void actualizarTablaFicheros() {
		Table tbl = this.tblFicherosTGD;
		if (tbl != null) {
			tbl.resetFilter();
			final Vector<Object> idx = new Vector<Object>();
			final Vector<Object> name = new Vector<Object>();
			final Vector<Object> fname = new Vector<Object>();
			final Vector<Object> idorigen = new Vector<Object>();
			final Vector<Object> extractdate = new Vector<Object>();
			final Vector<Object> kind = new Vector<Object>();
			int i = 0;
			for (TableFileInfo info : this.selectedTachoFiles.values()) {
				try {
					idx.add(Integer.valueOf(i++));

					name.add(info.computedName);
					String fn = info.originalName;
					try {
						fn = fn.substring(fn.lastIndexOf(File.separator) + 1);
					} catch (StringIndexOutOfBoundsException sex) {
					}
					fname.add(fn);
					extractdate.add(info.extractDate);
					kind.add(info.tgdf.getTachofile().getTypeDescription());
					idorigen.add(info.owner);
				} catch (Exception ex) {
					IMCargaFicheros.logger.error(null, ex);
				}
			}
			Hashtable<String, Object> cv = new Hashtable<String, Object>();
			cv.put(OpentachFieldNames.FILENAME_FIELD, name);
			cv.put(OpentachFieldNames.IDORIGEN_FIELD, idorigen);
			cv.put("NOMBORIG", fname);
			cv.put("F_DESCARGA", extractdate);
			cv.put(OpentachFieldNames.TYPE_FIELD, kind);
			tbl.setValue(cv);
			if (!tbl.isEmpty()) {
				this.tblFicherosTGD.setEnabled(true);
				this.managedForm.enableButton("btnDeleteTGD");
			} else {
				this.tblFicherosTGD.setEnabled(false);
				this.managedForm.disableButton("btnDeleteTGD");
			}
		}
	}

	private void adjuntar(Form f, File file) {
		Hashtable<String, Object> cv = new Hashtable<String, Object>();
		cv.putAll(f.getDataFieldValues(false));
		UploadMonitor upm = ((MonitorProvider) this.formManager.getReferenceLocator()).getUploadMonitor();
		upm.sendTGDFiles(Arrays.asList(new TGDFileInfo(file)), ITGDFileConstants.FILE_FIELD, cv, f, true, SoundManager.uriSonido);
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.setUpdateMode();
		this.managedForm.enableButtons();
		this.managedForm.disableButton("btnDeleteTGD");
	}

	static class TableFileInfo {
		String				originalName;
		String				computedName;
		TachoFileWrapper	tgdf;
		Date				extractDate;
		String				owner;

		public TableFileInfo(String originalName, String computedName, TachoFileWrapper tgdf, Date extractDate, String owner) {
			super();
			this.originalName = originalName;
			this.computedName = computedName;
			this.tgdf = tgdf;
			this.extractDate = extractDate;
			this.owner = owner;
		}

	}
}
