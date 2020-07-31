package com.opentach.client.modules.report;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.ListSelectionModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.DataFile;
import com.ontimize.gui.Form;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.locator.EntityReferenceLocator;
import com.ontimize.util.remote.BytesBlock;
import com.opentach.client.modules.IMReportRoot;
import com.opentach.common.OpentachFieldNames;

public class IMConsultaCertifActiv extends IMReportRoot {
	private final static Logger	logger		= LoggerFactory.getLogger(IMConsultaCertifActiv.class);

	protected Table				tblactiv	= null;

	public IMConsultaCertifActiv() {
		super();
		this.tablename = "ECertifActividades";
		this.fieldsChain.add(this.tablename);
		this.setDateTags(new TimeStampDateTags("F_ALTA"));
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		Button bdelete = f.getButton("delete_certif");
		bdelete.setIcon(ImageManager.getIcon(ImageManager.CANCEL));
		bdelete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				IMConsultaCertifActiv.this.tblactiv = (Table) IMConsultaCertifActiv.this.managedForm
						.getDataFieldReference(IMConsultaCertifActiv.this.tablename);
				if (IMConsultaCertifActiv.this.tblactiv.getSelectedRows().length > 0) {
					int[] certifs = IMConsultaCertifActiv.this.tblactiv.getSelectedRows();
					EntityReferenceLocator erl = IMConsultaCertifActiv.this.formManager.getReferenceLocator();
					Entity eFich;
					try {
						eFich = erl.getEntityReference("ECertifActividades");
						Hashtable<String, Object> av = new Hashtable<String, Object>();

						for (int i = 0; i < certifs.length; i++) {
							av.put("IDCERTIFICADO", IMConsultaCertifActiv.this.tblactiv.getRowData(certifs[i]).get("IDCERTIFICADO"));
							eFich.delete(av, erl.getSessionId());
						}
						IMConsultaCertifActiv.this.tblactiv.refresh();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		this.tblactiv = (Table) this.managedForm.getDataFieldReference(this.tablename);
		if (this.tblactiv != null) {
			this.tblactiv.addButtonToControls(bdelete);
			f.remove(bdelete);
			this.tblactiv.getJTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			this.tblactiv.getJTable().addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						int row = IMConsultaCertifActiv.this.tblactiv.getJTable().rowAtPoint(e.getPoint());
						IMConsultaCertifActiv.this.openCertif(row);
					}
				}
			});
		}

	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.managedForm.setAdvancedQueryMode(OpentachFieldNames.IDORIGEN_FIELD, true);
	}

	private DataFile getDataFile(final Object idcert) {
		OperationThread opth = new OperationThread() {
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				this.hasStarted = true;
				try {
					EntityReferenceLocator erl = IMConsultaCertifActiv.this.formManager.getReferenceLocator();
					Entity eFich = erl.getEntityReference("ECertifActividades");
					Hashtable<String, Object> kv = new Hashtable<String, Object>();
					kv.put("IDCERTIFICADO", idcert);
					Vector<Object> vq = new Vector<Object>(1);
					vq.addElement("FICH_CERTIF");
					EntityResult er = eFich.query(kv, vq, erl.getSessionId());
					if ((er.getCode() == EntityResult.OPERATION_SUCCESSFUL) && (er.calculateRecordNumber() == 1)) {
						Hashtable<String, Object> htData = er.getRecordValues(0);
						this.res = htData.get("FICH_CERTIF");
					}
				} catch (Exception e) {
					e.printStackTrace();
					this.res = e;
				} finally {
					this.hasFinished = true;
				}
			}
		};
		ApplicationManager.proccessNotCancelableOperation(ApplicationManager.getApplication().getFrame(), opth, 50);
		Object result = opth.getResult();
		if (result instanceof DataFile) {
			return (DataFile) result;
		}
		return null;
	}

	private void openCertif(int row) {
		Hashtable<String, Object> hfila = this.tblactiv.getRowData(row);
		if (hfila != null) {
			Object idf = this.tblactiv.getRowKey(row, "IDCERTIFICADO");
			DataFile df = this.getDataFile(idf);
			if (df != null) {
				FileOutputStream fOut = null;
				BufferedOutputStream bOut = null;
				try {
					String nombre = (String) hfila.get("NOMBRE");
					if (nombre.length() < 3) {
						nombre += "aux";
					}
					File fTemporal = File.createTempFile(nombre, ".pdf");
					fOut = new FileOutputStream(fTemporal.toString(), true);
					bOut = new BufferedOutputStream(fOut);
					BytesBlock bb = df.getBytesBlock();
					bOut.write(bb.getBytes());
					bOut.flush();
					if (fTemporal != null) {
						Desktop.getDesktop().open(fTemporal);
					}
				} catch (Exception ex) {
					IMConsultaCertifActiv.logger.error("putBytes: Cancelando fichero adjunto por error", ex);
				} finally {
					if (bOut != null) {
						try {
							bOut.close();
						} catch (Exception eh) {
						}
					}
					if (fOut != null) {
						try {
							fOut.close();
						} catch (Exception eh) {
						}
					}
				}
			}
		} else {
			this.managedForm.message("M_SELECCIONE_FILA", Form.ERROR_MESSAGE);
		}
	}
}
