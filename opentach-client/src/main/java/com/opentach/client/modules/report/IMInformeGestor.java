package com.opentach.client.modules.report;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.locator.EntityReferenceLocator;
import com.ontimize.locator.ReferenceLocator;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.comp.ExtendedApplicationManager;
import com.opentach.client.modules.IMRoot;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.activities.IInfractionService;
import com.opentach.common.activities.IInfractionService.EngineAnalyzer;
import com.opentach.common.company.naming.CompanyNaming;
import com.opentach.common.report.IOpentachReportService;
import com.utilmize.client.gui.field.reference.UReferenceDataField;

public class IMInformeGestor extends IMRoot {

	private Button	bInformeSend	= null;
	private Button	bInforme		= null;
	private Button	bInformeMovil	= null;

	public IMInformeGestor() {
		super();
		this.fieldsChain.clear();
		this.fieldsChain.add(OpentachFieldNames.CIF_FIELD);
		this.fieldsChain.add(OpentachFieldNames.CG_CONTRATO_FIELD);
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.bInformeSend = f.getButton("btnInformeSend");
		this.cgContract = f.getDataFieldReference(OpentachFieldNames.CG_CONTRATO_FIELD);
		if (this.bInformeSend != null) {
			this.bInformeSend.addActionListener(new ActionListener() {
				@Override
				@SuppressWarnings("unchecked")
				public void actionPerformed(ActionEvent e) {
					// obtengo el correo electrónico de la empresa
					if (!IMInformeGestor.this.checkRequiredVisibleDataFields(true)) {
						return;
					}
					Vector vq = new Vector(1);
					vq.add("EMAIL");
					Hashtable htAv = new Hashtable(1);
					htAv.put(OpentachFieldNames.CIF_FIELD, IMInformeGestor.this.managedForm.getDataFieldValue(OpentachFieldNames.CIF_FIELD));
					try {
						ReferenceLocator b = (ReferenceLocator) IMInformeGestor.this.formManager.getReferenceLocator();
						Entity eDFemp = b.getEntityReference(CompanyNaming.ENTITY);
						EntityResult res = eDFemp.query(htAv, vq, b.getSessionId());
						if (res.getCode() == EntityResult.OPERATION_SUCCESSFUL) {
							Hashtable htFila = res.getRecordValues(0);
							String sCorreo = JOptionPane.showInputDialog(ApplicationManager.getTranslation("M_INSERTE_CORREO_ELECTRONICO"),
									htFila.get("EMAIL"));
							if (sCorreo != null) {
								IMInformeGestor.this.accBotInforme(true, sCorreo);
							}
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});
		}
		this.bInforme = f.getButton("btnInforme");
		if (this.bInforme != null) {
			this.bInforme.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					IMInformeGestor.this.accBotInforme(false, null);
				}
			});
		}

		this.bInformeMovil = f.getButton("bInformeMovil");
		if (this.bInformeMovil != null) {
			this.bInformeMovil.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					IMInformeGestor.this.accBotInformeMovil(false, null);
				}
			});
		}
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.setUpdateMode();
		if (this.managedForm.getButton("bInformeMovil") != null) {
			this.managedForm.getButton("bInformeMovil").setEnabled(true);
		}
	}

	private void accBotInforme(boolean send, String sCorreo) {
		if (!this.checkRequiredVisibleDataFields(true)) {
			return;
		}
		this.createReports(send, sCorreo);
	}

	private void accBotInformeMovil(boolean send, String sCorreo) {
		try {
			final EntityReferenceLocator brefs = this.formManager.getReferenceLocator();
			final Entity eInfMovil = brefs.getEntityReference("EInformeExpressMovil");
			OperationThread opth = new OperationThread() {
				@Override
				public void run() {
					this.hasStarted = true;
					try {
						//						String idfichero = "98868";
						// ((IGeneraInformeExpressMovil)
						// eInfMovil).querySendEmail(idfichero,
						// brefs.getSessionId());
						// res = null;
					} catch (Exception e) {
						e.printStackTrace();
						this.res = e;
					} finally {
						this.hasFinished = true;
					}
				}
			};
			ExtendedApplicationManager.proccessOperation((JDialog) SwingUtilities.getWindowAncestor(this.managedForm), opth, 1000);
			Exception e = (Exception) opth.getResult();
			if (e != null) {
				if ("M_NO_SE_HAN_ENCONTRADO_DATOS".equals(e.getMessage())) {
					this.managedForm.message("M_NO_SE_HAN_ENCONTRADO_DATOS", Form.ERROR_MESSAGE);
				} else {
					this.managedForm.message("M_PETICION_ERROR", Form.ERROR_MESSAGE);
				}
			} else {
				this.managedForm.message("M_PETICION_ACEPTADA", Form.INFORMATION_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		this.managedForm.enableButton("btnInforme");
		this.managedForm.enableButton("btnInformeSend");
		this.managedForm.enableDataField("F_INFORME");
	}

	private void createReports(final boolean send, final String sCorreo) {
		try {
			final OpentachClientLocator ocl = (OpentachClientLocator) this.formManager.getReferenceLocator();
			UReferenceDataField cCif = (UReferenceDataField) this.CIF;
			final String cif = (String) cCif.getValue();
			final Map htRow = cCif.getCodeValues(cif);
			final String empresa = (String) htRow.get("NOMB");
			final String sLocale = (String) htRow.get("LOCALE");
			final Locale locale = new Locale(sLocale);
			final String cgContrato = (String) this.managedForm.getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD);
			final Date fInforme = (Date) this.managedForm.getDataFieldValue("F_INFORME");
			final EngineAnalyzer analyzer = (EngineAnalyzer) this.managedForm.getDataFieldValue(IInfractionService.ENGINE_ANALYZER);
			OperationThread opth = new OperationThread() {
				@Override
				public void run() {
					this.hasStarted = true;
					try {
						ocl.getRemoteService(IOpentachReportService.class).generateManagementReport(cif, empresa, cgContrato, sCorreo, fInforme, analyzer,
								locale, send,
								ocl.getSessionId());
						this.res = null;
					} catch (Exception e) {
						e.printStackTrace();
						this.res = e;
					} finally {
						this.hasFinished = true;
					}
				}
			};
			ExtendedApplicationManager.proccessOperation((JDialog) SwingUtilities.getWindowAncestor(this.managedForm), opth, 1000);
			Exception e = (Exception) opth.getResult();
			if (e != null) {
				if ("M_NO_SE_HAN_ENCONTRADO_DATOS".equals(e.getMessage())) {
					this.managedForm.message("M_NO_SE_HAN_ENCONTRADO_DATOS", Form.ERROR_MESSAGE);
				} else {
					this.managedForm.message("M_PETICION_ERROR", Form.ERROR_MESSAGE);
				}
			} else {
				this.managedForm.message("M_PETICION_ACEPTADA", Form.INFORMATION_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.managedForm.message("M_PETICION_ERROR", Form.ERROR_MESSAGE);
		}
	}
}
