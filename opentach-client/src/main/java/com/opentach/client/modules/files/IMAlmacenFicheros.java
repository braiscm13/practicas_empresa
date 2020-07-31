package com.opentach.client.modules.files;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.model.TachoFile;
import com.ontimize.db.NullValue;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ExtendedOperationThread;
import com.ontimize.gui.Form;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.opentach.client.MonitorProvider;
import com.opentach.client.modules.IMRoot;
import com.opentach.client.util.SoundManager;
import com.opentach.client.util.StoreLogParser;
import com.opentach.client.util.upload.UploadMonitor;
import com.opentach.common.OpentachFieldNames;

public class IMAlmacenFicheros extends IMRoot {

	private static final Logger	logger				= LoggerFactory.getLogger(IMAlmacenFicheros.class);

	private static final String	FICHERO_RECIENTE	= "FICHERO_RECIENTE";
	private static final String	SINCRONIZADO		= "SINCRONIZADO";
	private static final String	DESCARGADO_DEL_CSD	= "DESCARGADO_DEL_CSD";
	private static final String	ENVIADO_AL_CSD		= "ENVIADO_AL_CSD";
	private static final String	FILE_COL			= "FICHERO";
	private static final String	TBL_LOCAL_FILES		= "EAlmacenLocal";

	protected MonitorProvider	mp;

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.mp = (MonitorProvider) gf.getReferenceLocator();
		Button btn = f.getButton("btnRefrescar");
		if (btn != null) {
			ActionListener[] al = btn.getActionListeners();
			for (int i = 0; (al != null) && (i < al.length); i++) {
				btn.removeActionListener(al[i]);
			}

			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					IMAlmacenFicheros.this.refrescarInfoLocalFiles();
				}
			});
		}

		btn = f.getButton("btnUpload");
		if (btn != null) {
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					ExtendedOperationThread exop = new ExtendedOperationThread(ApplicationManager.getTranslation("M_PROCESANDO_DATOS")) {
						@Override
						public void run() {
							this.hasStarted = true;
							try {
								IMAlmacenFicheros.this.enviarFicherosSeleccionados();
							} finally {
								this.hasFinished = true;
							}
						}
					};
					ApplicationManager.proccessOperation(exop, 50);
				}
			});
		}
		btn = f.getButton("btnDelete");
		if (btn != null) {
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					IMAlmacenFicheros.this.borrarFicherosSeleccionados();
					IMAlmacenFicheros.this.refrescarInfoLocalFiles();
				}
			});
		}
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.refrescarInfoLocalFiles();
		this.managedForm.enableButtons();
	}

	@SuppressWarnings("unchecked")
	public void refrescarInfoLocalFiles() {
		try {
			Hashtable<String,Object> cv = new Hashtable<String,Object>();
			String[] tablename = new String[] { IMAlmacenFicheros.TBL_LOCAL_FILES };
			File[] store = new File[] { this.mp.getFileStore().getTGDStore() };
			for (int i = 0; i < store.length; i++) {
				File str = store[i];
				Vector<Object> fl = new Vector<Object>();
				Vector<Object> fk = new Vector<Object>();
				Vector<Object> fd = new Vector<Object>();
				Vector<Object> fdd = new Vector<Object>();
				Vector<Object> fu = new Vector<Object>();
				Vector<Object> origen = new Vector<Object>();
				Vector<Object> obs = new Vector<Object>();
				cv.clear();

				final String obsenv = ApplicationManager.getTranslation(IMAlmacenFicheros.ENVIADO_AL_CSD);
				final String obsrec = ApplicationManager.getTranslation(IMAlmacenFicheros.DESCARGADO_DEL_CSD);
				final String obssinc = ApplicationManager.getTranslation(IMAlmacenFicheros.SINCRONIZADO);
				final String obsrecent = ApplicationManager.getTranslation(IMAlmacenFicheros.FICHERO_RECIENTE);

				Map<String, String> uploadfiles = StoreLogParser.getUploadedFiles(store[i]);
				for (String file : uploadfiles.keySet()) {
					TachoFile tgdfile = null;
					try {
						tgdfile = TachoFile.readTachoFile(new File(str.getAbsolutePath() + File.separator + file));
					} catch (Exception err) {
						IMAlmacenFicheros.logger.error(null, err);
						continue;
					}
					fl.add(file);
					fd.add(new NullValue(Types.DATE));
					fu.add(uploadfiles.get(file));
					origen.add(tgdfile.getOwnerId());
					fk.add(tgdfile.getTypeDescription());
					fdd.add(tgdfile.getExtractTime(null));
					obs.add(obsenv);
				}

				Map<String,String> downloadfiles = StoreLogParser.getDownloadedFiles(store[i]);
				for (Iterator<String> iter = downloadfiles.keySet().iterator(); iter.hasNext();) {
					String file = iter.next();
					if (!fl.contains(file)) {
						TachoFile tgdfile = null;
						try {
							tgdfile = TachoFile.readTachoFile(new File(str.getAbsolutePath() + File.separator + file));
						} catch (Exception err) {
							IMAlmacenFicheros.logger.error(null, err);
							continue;
						}
						fl.add(file);
						fu.add(new NullValue(Types.DATE));
						fd.add(downloadfiles.get(file));
						origen.add(tgdfile.getOwnerId());
						fk.add(tgdfile.getTypeDescription());
						fdd.add(tgdfile.getExtractTime(null));
						obs.add(obsrec);
					} else {
						fd.set(fl.indexOf(file), new Date());
						obs.set(fl.indexOf(file), obssinc);
					}
				}

				Map<String,String> recentfiles = StoreLogParser.getRecentFiles(store[i]);
				for (Iterator<String> iter = recentfiles.keySet().iterator(); iter.hasNext();) {
					String file = iter.next();
					if (!fl.contains(file)) {
						TachoFile tgdfile = null;
						try {
							tgdfile = TachoFile.readTachoFile(new File(str.getAbsolutePath() + File.separator + file));
						} catch (Exception err) {
							IMAlmacenFicheros.logger.error(null, err);
							continue;
						}
						fl.add(file);
						fu.add(new NullValue(Types.DATE));
						fd.add(new NullValue(Types.DATE));
						origen.add(tgdfile.getOwnerId());
						fk.add(tgdfile.getTypeDescription());
						fdd.add(tgdfile.getExtractTime(null));
						obs.add(obsrecent);
					}
				}

				cv.put("FICHERO", fl);
				cv.put(OpentachFieldNames.IDORIGEN_FIELD, origen);
				cv.put(OpentachFieldNames.TYPE_FIELD, fk);
				cv.put("F_DATA_DOWNLOAD", fdd);
				cv.put("F_FILE_DOWNLOAD", fd);
				cv.put("F_FILE_UPLOAD", fu);
				cv.put("OBSR", obs);

				Table tbl = (Table) this.managedForm.getDataFieldReference(tablename[i]);
				tbl.setValue(cv);
				tbl.setEnabled(!tbl.isEmpty());
				// formularioGestionado.setValorCampo(tablename[i], new
				// ResultEntidad(cv));
				// tblTCremote.setEnabled((res.calculaNumeroRegistros() > 0));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void borrarFicherosSeleccionados() {
		int rtn = JOptionPane.showConfirmDialog(this.managedForm, ApplicationManager.getTranslation("M_BORRAR_FICHEROS_SELECCIONADOS"), "",
				JOptionPane.YES_NO_OPTION);
		if (rtn == JOptionPane.YES_OPTION) {
			Table tbl = (Table) this.managedForm.getDataFieldReference(IMAlmacenFicheros.TBL_LOCAL_FILES);
			int[] fs = tbl.getSelectedRows();
			String store = this.mp.getFileStore().getTGDStore().getAbsolutePath();
			List<String> filesname = new ArrayList<String>();
			for (int i : fs) {
				filesname.add((String) tbl.getRowValue(i, IMAlmacenFicheros.FILE_COL));
			}
			for (String fn : filesname) {
				File f = new File(store + File.separator + fn);
				f.delete();
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void enviarFicherosSeleccionados() {
		Table tbl = (Table) this.managedForm.getDataFieldReference(IMAlmacenFicheros.TBL_LOCAL_FILES);
		int[] fs = tbl.getSelectedRows();
		if (fs.length > 0) {
			String store = this.mp.getFileStore().getTGDStore().getAbsolutePath();
			List<String> filenames = new ArrayList<String>();
			for (int i = 0; (fs != null) && (i < fs.length); i++) {
				filenames.add((String) tbl.getRowValue(fs[i], IMAlmacenFicheros.FILE_COL));
			}
			if (this.checkRequiredVisibleDataFields(true)) {
				Hashtable<String,Object> cv = new Hashtable<String,Object>();
				if (this.managedForm.getDataFieldValue(OpentachFieldNames.CIF_FIELD) != null) {
					cv.put(OpentachFieldNames.CIF_FIELD, this.managedForm.getDataFieldValue(OpentachFieldNames.CIF_FIELD));
				}
				if (this.managedForm.getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD) != null) {
					cv.put(OpentachFieldNames.CG_CONTRATO_FIELD, this.managedForm.getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD));
				}
				UploadMonitor upm = ((MonitorProvider) this.formManager.getReferenceLocator()).getUploadMonitor();
				upm.sendFiles(filenames, store, IMAlmacenFicheros.FILE_COL, cv, this.managedForm, true, SoundManager.uriSonido);
			}
		} else {
			this.managedForm.message("M_SELECCIONE_FICHEROS_ENVIAR", Form.INFORMATION_MESSAGE);
		}
	}

}
