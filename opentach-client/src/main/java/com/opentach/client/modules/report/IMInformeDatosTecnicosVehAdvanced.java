package com.opentach.client.modules.report;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.db.EntityResultUtils;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.comp.ExtendedApplicationManager;
import com.opentach.client.modules.IMReportRoot;
import com.opentach.common.ITGDFileConstants;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.report.IReportService;
import com.opentach.common.report.util.JRPropertyManager;
import com.opentach.common.report.util.JRReportDescriptor;
import com.opentach.common.util.ResourceManager;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.utilmize.client.gui.tasks.USwingWorker;
import com.utilmize.client.report.JRDialogViewer;
import com.utilmize.tools.report.JRReportUtil;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;

public class IMInformeDatosTecnicosVehAdvanced extends IMReportRoot implements ITGDFileConstants {

	private static final Logger	logger			= LoggerFactory.getLogger(IMInformeDatosTecnicosVehAdvanced.class);
	static String[]				mandatoryFields	= new String[] { "CIF", "MATRICULA" };

	private static final String	pNUMREQ			= "numreq";
	private static final String	pCIF			= "cif";
	private static final String	pEMPRESA		= "empresa";
	private static final String	pTITLE			= "title";
	private static final String	pFINFORME		= "f_informe";

	public IMInformeDatosTecnicosVehAdvanced() {
		super();
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);

		ValueChangeListener changeValueListener = new ValueChangeListener() {
			@Override
			public void valueChanged(ValueEvent paramValueEvent) {
				if (paramValueEvent.getType() == ValueEvent.USER_CHANGE) {
					new USwingWorker<Map<String, EntityResult>, Void>() {

						@Override
						protected Map<String, EntityResult> doInBackground() throws Exception {
							//							if ((paramValueEvent.getSource() instanceof IdentifiedElement) && ((IdentifiedElement) paramValueEvent.getSource())
							//									.getAttribute().equals(OpentachFieldNames.CIF_FIELD)) {
							//								IMInformeDatosTecnicosVehAdvanced.this.updateContract();
							//							}

							return IMInformeDatosTecnicosVehAdvanced.this.updateStatics();
						}

						@Override
						protected void done() {
							super.done();
							try {
								// IMRoot.clearTables();
								Map<String, EntityResult> res = this.uget();
								for (Entry<String, EntityResult> entry : res.entrySet()) {
									((Table) IMInformeDatosTecnicosVehAdvanced.this.managedForm.getElementReference(entry.getKey())).setValue(entry.getValue());
								}

							} catch (Throwable ex) {
								MessageManager.getMessageManager().showExceptionMessage(ex, IMInformeDatosTecnicosVehAdvanced.logger);
							}
						}
					}.executeOperation(IMInformeDatosTecnicosVehAdvanced.this.managedForm);
				}
			}
		};

		for (String attr : IMInformeDatosTecnicosVehAdvanced.mandatoryFields) {
			((DataField) this.managedForm.getDataFieldReference(attr)).addValueChangeListener(changeValueListener);
		}

		Button b = this.managedForm.getButton("btnInforme");
		if (b != null) {
			b.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					IMInformeDatosTecnicosVehAdvanced.this.createReports();

				}
			});
		}
	}

	@Override
	public void setQueryMode() {
		// TODO Auto-generated method stub
		super.setQueryMode();
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.setUpdateMode();
		if (this.managedForm.getButton("btnInforme") != null) {
			this.managedForm.getButton("btnInforme").setEnabled(true);
		}
		for (String s : IMInformeDatosTecnicosVehAdvanced.mandatoryFields) {
			this.managedForm.enableDataField(s);
		}

		// ((HTMLHelpField) this.managedForm.getElementReference("HTMLINFO"))
		// .setText(ApplicationManager
		// .getTranslation("M_SEL_COMPANY_VEHICLE"));
	}

	// private void updateContract() {
	// try {
	// Object cif = IMInformeDatosTecnicosVehAdvanced.this.managedForm.getDataFieldValue("CIF");
	// if (cif == null) {
	// return;
	// }
	// ReferenceLocator b = (ReferenceLocator) ((FormManager) IMInformeDatosTecnicosVehAdvanced.this.formManager).getReferenceLocator();
	// Entity entidad = b.getEntityReference(((UReferenceDataField) IMInformeDatosTecnicosVehAdvanced.this.managedForm
	// .getDataFieldReference("CG_CONTRATO")).getEntity());
	//
	// Hashtable<String, Object> av = new Hashtable<String, Object>();
	// av.put("CIF", cif);
	// EntityResult res = entidad.query(av, new Vector(), b.getSessionId());
	// if (res.calculateRecordNumber() == 1) {
	// IMInformeDatosTecnicosVehAdvanced.this.cgContract.setValue(res.getRecordValues(0).get("CG_CONTRATO"));
	// IMInformeDatosTecnicosVehAdvanced.this.updateChainStatus(IMInformeDatosTecnicosVehAdvanced.this.fieldsChain
	// .get(IMInformeDatosTecnicosVehAdvanced.this.fieldsChain.indexOf("CG_CONTRATO")), false);
	// }
	// } catch (Exception e) {
	// IMInformeDatosTecnicosVehAdvanced.logger.error(null, e);
	// }
	// }

	@Override
	public void refreshTables() throws Exception {
		this.updateStatics();
	}

	private Map<String, EntityResult> updateStatics() throws Exception {

		for (String attr : IMInformeDatosTecnicosVehAdvanced.mandatoryFields) {
			if (this.managedForm.getDataFieldValue(attr) == null) {
				return new HashMap<String, EntityResult>();
			}
		}

		Object matricula = this.managedForm.getDataFieldValue("MATRICULA");
		EntityReferenceLocator loc = this.formManager.getReferenceLocator();
		int sesionId = loc.getSessionId();
		Entity eBloqueosEmpre = loc.getEntityReference("EBloqueosEmpresa");
		Entity eDatosTecnicos = loc.getEntityReference("EDatosTecnicos");
		Entity eDatosTecnicosCal = loc.getEntityReference("EDatosTecnicosCal");
		Entity eDatosTecnicosSensor = loc.getEntityReference("EDatosTecnicosSensor");

		Hashtable<String, Object> avfic = new Hashtable<String, Object>();
		avfic.put("MATRICULA", matricula);

		// TODO paralelizar
		// TODO ask for exact columns
		EntityResult resBloqueosEmpre = eBloqueosEmpre.query(avfic, new Vector<Object>(), sesionId);
		EntityResult resDatosTecnicos = eDatosTecnicos.query(avfic, new Vector<Object>(), sesionId);
		EntityResult resDatosTecnicosCal = eDatosTecnicosCal.query(avfic, new Vector<Object>(), sesionId);
		EntityResult resDatosTecnicosSensor = eDatosTecnicosSensor.query(avfic, new Vector<Object>(), sesionId);

		Map<String, EntityResult> res = new HashMap<String, EntityResult>();
		res.put("EDatosTecnicos", resDatosTecnicos);
		res.put("EDatosTecnicosSensor", resDatosTecnicosSensor);
		res.put("ECalibrationsDummy", resDatosTecnicosCal);
		res.put("ECompanyLocksDummy", resBloqueosEmpre);
		return res;
	}


	private void createReports() {

		JRPropertyManager jpm = new JRPropertyManager("com/opentach/reports/reports.properties");
		final JRReportDescriptor jrd = jpm.getDataMap().get(39);
		final Map<String, Object> params = this.getParams(jrd.getDscr(), jrd.getDelegCol());

		// params.put("numreq", this.managedForm.getDataFieldValue("CG_CONTRATO"));
		// params.put("f_ini", this.managedForm.getDataFieldValue("FILTERFECINI"));

		final JFrame jd = (JFrame) SwingUtilities.getWindowAncestor(this.managedForm);
		final OpentachClientLocator ocl = (OpentachClientLocator) this.formManager.getReferenceLocator();
		if (jrd != null) {
			final List lReports = jrd.getLReports();
			if ((lReports == null) || (lReports.size() == 0)) {
				try {
					final String urljr = jrd.getUrl();
					OperationThread opth = new OperationThread() {
						@Override
						public void run() {
							this.hasStarted = true;
							JasperPrint jp = null;
							try {

								for (String attr : IMInformeDatosTecnicosVehAdvanced.mandatoryFields) {
									if (IMInformeDatosTecnicosVehAdvanced.this.managedForm.getDataFieldValue(attr) == null) {
										throw new IllegalArgumentException();
									}
								}

								EntityResult resCalibrados = (EntityResult) ((Table) IMInformeDatosTecnicosVehAdvanced.this.managedForm
										.getElementReference("ECalibrationsDummy")).getValue();
								EntityResult resLocks = (EntityResult) ((Table) IMInformeDatosTecnicosVehAdvanced.this.managedForm
										.getElementReference("ECompanyLocksDummy")).getValue();
								EntityResult resDatosTecnicos = (EntityResult) ((Table) IMInformeDatosTecnicosVehAdvanced.this.managedForm
										.getElementReference("EDatosTecnicos")).getValue();
								EntityResult resDatosTecnicosSensor = (EntityResult) ((Table) IMInformeDatosTecnicosVehAdvanced.this.managedForm
										.getElementReference("EDatosTecnicosSensor")).getValue();

								Hashtable<String, Object> mParams = new Hashtable<String, Object>();
								mParams.putAll(params);
								mParams.put("DATASOURCE_CALIBRATION", new JRTableModelDataSource(EntityResultUtils.createTableModel(resCalibrados)));
								mParams.put("DATASOURCE_BLOQUEOS", new JRTableModelDataSource(EntityResultUtils.createTableModel(resLocks)));

								mParams.put("DATASOURCE_DATOS_TECNICOS",
										new JRTableModelDataSource(EntityResultUtils.createTableModel(resDatosTecnicos)));

								mParams.put("DATASOURCE_DATOS_TECNICOS_SENSOR",
										new JRTableModelDataSource(EntityResultUtils.createTableModel(resDatosTecnicosSensor)));

								Object matricula = IMInformeDatosTecnicosVehAdvanced.this.managedForm.getDataFieldValue("MATRICULA");
								mParams.put("matricula", matricula);
								mParams.put("TIPO_INFORME", "1");
								mParams.put("numreq", IMInformeDatosTecnicosVehAdvanced.this.managedForm.getDataFieldValue("CG_CONTRATO"));

								// mParams.put("DATASOURCE_RESUMEN", new JRTableModelDataSource(EntityResultUtils.createTableModel(er)));

								// jp = ocl.fillReport(urljr, mParams,
								// jrd.getMethodAfter(),
								// jrd.getMethodBefore(),new EntityResult());

								mParams.put(JRParameter.REPORT_LOCALE, Locale.getDefault());
								//
								// // params.put(JRParameter.REPORT_CONNECTION, );
								ResourceBundle rb = ResourceManager.getBundle(Locale.getDefault());
								if (rb != null) {
									mParams.put(JRParameter.REPORT_RESOURCE_BUNDLE, rb);
								}

								Hashtable<String, Object> av = new Hashtable<String, Object>();
								av.put("MATRICULA", matricula);
								EntityResult resPrincipal = new EntityResult();
								resPrincipal.addRecord(av);
								jp = JRReportUtil.fillReport(JRReportUtil.getJasperReport(urljr), mParams, resPrincipal);
								// jp = ocl.fillReport(urljr, mParams, jrd.getMethodAfter(), jrd.getMethodBefore(), null);

							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								this.hasFinished = true;
								this.res = jp;
							}
						}
					};
					ExtendedApplicationManager.proccessOperation(jd, opth, 1000);
					JasperPrint jp = (JasperPrint) opth.getResult();
					if ((jp != null) && (jp.getPages() != null) && (jp.getPages().size() > 0)) {
						JRDialogViewer jv = JRDialogViewer.getJasperViewer(ApplicationManager.getTranslation(jrd.getDscr()), jd, jp);
						jv.setVisible(true);
					} else {
						this.managedForm.message("M_NO_SE_HAN_ENCONTRADO_DATOS", Form.WARNING_MESSAGE);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				try {
					final List<JasperPrint> jpList = new ArrayList<JasperPrint>();
					OperationThread opth = new OperationThread() {
						@Override
						public void run() {
							this.hasStarted = true;
							try {
								String urljr = null;
								for (int i = 0; i < lReports.size(); i++) {
									JRReportDescriptor jru = (JRReportDescriptor) lReports.get(i);
									urljr = jru.getUrl();
									Map<String, Object> p = params;
									if (i != 0) {
										p = IMInformeDatosTecnicosVehAdvanced.this.getParams(jru.getDscr(), jru.getDelegCol());
									}
									JasperPrint jp = ocl.getRemoteService(IReportService.class).fillReport(urljr, p, jru.getMethodAfter(),
											jru.getMethodBefore(), null, ocl.getSessionId());
									if ((jp != null) && (jp.getPages() != null) && (jp.getPages().size() > 0)) {
										jpList.add(jp);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								this.hasFinished = true;
							}
						}
					};
					ExtendedApplicationManager.proccessOperation(jd, opth, 1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected Map<String, Object> getParams(String title, String delegCol) {
		Map<String, Object> mParams = new HashMap<String, Object>();
		UReferenceDataField cCif = (UReferenceDataField) this.managedForm.getDataFieldReference("CIF");
		String cif = (String) cCif.getValue();
		Map htRow = cCif.getCodeValues(cif);
		String empresa = (String) htRow.get("NOMB");
		String cgContrato = (String) this.managedForm.getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD);
		cgContrato = this.checkContratoFicticio(cgContrato);

		mParams.put(IMInformeDatosTecnicosVehAdvanced.pNUMREQ, cgContrato);
		mParams.put(IMInformeDatosTecnicosVehAdvanced.pCIF, cif);
		mParams.put(IMInformeDatosTecnicosVehAdvanced.pEMPRESA, empresa);
		mParams.put(IMInformeDatosTecnicosVehAdvanced.pTITLE, ApplicationManager.getTranslation(title));

		Locale locale = new Locale((String) htRow.get("LOCALE"));
		if (locale == null) {
			locale = new Locale("es", "ES");
		}
		mParams.put(JRParameter.REPORT_LOCALE, locale);

		ResourceBundle rb = ResourceManager.getBundle(locale);
		if (rb != null) {
			mParams.put(JRParameter.REPORT_RESOURCE_BUNDLE, rb);
		}
		return mParams;
	}

	@Override
	protected String checkContratoFicticio(String cgContrato) {
		try {
			EntityReferenceLocator loc = this.formManager.getReferenceLocator();
			int sesionId = loc.getSessionId();
			Entity eEmpreReq = loc.getEntityReference("EEmpreReq");

			Hashtable<String, Object> avfic = new Hashtable<String, Object>();
			avfic.put("CG_CONTRATO", cgContrato);

			EntityResult res = eEmpreReq.query(avfic, new Vector<Object>(), sesionId);
			if (res.calculateRecordNumber() > 0) {
				if ("S".equals(res.getRecordValues(0).get("FICTICIO"))) {
					Entity eCifEmpreReq = loc.getEntityReference("ECifEmpreReq");
					avfic = new Hashtable<String, Object>();
					avfic.put("CIF", res.getRecordValues(0).get("CIF"));
					EntityResult resCif = eCifEmpreReq.query(avfic, new Vector<Object>(), sesionId);
					if (resCif.calculateRecordNumber() > 0) {
						return (String) resCif.getRecordValues(0).get("CG_CONTRATO");
					}
				} else {
					return cgContrato;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
