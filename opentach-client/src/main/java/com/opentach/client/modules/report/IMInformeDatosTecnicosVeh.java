package com.opentach.client.modules.report;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.comp.ExtendedApplicationManager;
import com.opentach.client.modules.IMReportRoot;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.report.IReportService;
import com.opentach.common.report.util.JRPropertyManager;
import com.opentach.common.report.util.JRReportDescriptor;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.utilmize.client.report.JRDialogViewer;

import net.sf.jasperreports.engine.JasperPrint;

public class IMInformeDatosTecnicosVeh extends IMReportRoot {

	private static final String	pNUMREQ		= "numreq";
	private static final String	pCIF		= "cif";
	private static final String	pEMPRESA	= "empresa";
	private static final String	pTITLE		= "title";
	private static final String	pFINFORME	= "f_informe";

	private Button				bInforme	= null;

	public IMInformeDatosTecnicosVeh() {
		super("EInformeDatosTecnicosVeh", "informe_datos_tecnicos");
		this.dateEntity = "EUFecha";
		HashMap<String, String> mEntityReport = new HashMap<String, String>();
		mEntityReport.put("informe_datos_tecnicos", "EInformeDatosTecnicosVeh");
		this.setEntidadesInformes(mEntityReport);
		this.setDateTags(new TimeStampDateTags("F_ALTA"));

	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.bInforme = f.getButton("btnInforme");
		this.cgContract = f.getDataFieldReference(OpentachFieldNames.CG_CONTRATO_FIELD);
		if (this.bInforme != null) {
			this.bInforme.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					IMInformeDatosTecnicosVeh.this.accBotInforme();
				}
			});
		}
	}

	@Override
	public void setInitialState() {

		super.setInitialState();
		this.managedForm.enableButton("btnInforme2");
	}

	private void accBotInforme() {
		if (!this.checkRequiredVisibleDataFields(true)) {
			return;
		}
		this.createReports();
	}

	@Override
	protected Map<String, Object> getParams(String title, String delegCol) {
		Map<String, Object> mParams = new HashMap<String, Object>();
		UReferenceDataField cCif = (UReferenceDataField) this.CIF;
		String cif = (String) cCif.getValue();
		Map htRow = cCif.getCodeValues(cif);
		String empresa = (String) htRow.get("NOMB");
		String cgContrato = (String) this.managedForm.getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD);
		cgContrato = this.checkContratoFicticio(cgContrato);
		Date fecFin = (Date) this.managedForm.getDataFieldValue("FILTERFECFIN");
		Date fecIni = (Date) this.managedForm.getDataFieldValue("FILTERFECINI");
		mParams.put(IMInformeDatosTecnicosVeh.pNUMREQ, cgContrato);
		mParams.put(IMInformeDatosTecnicosVeh.pCIF, cif);
		mParams.put(IMInformeDatosTecnicosVeh.pEMPRESA, empresa);
		mParams.put(IMInformeDatosTecnicosVeh.pTITLE, title);
		mParams.put("f_inicio", new Timestamp(fecIni.getTime()));
		mParams.put("f_fin", new Timestamp(fecFin.getTime() + (24 * 3600000)));
		mParams.put(IMInformeDatosTecnicosVeh.pFINFORME, new Timestamp(fecFin.getTime()));
		mParams.put("TIPO_INFORME", "1");
		return mParams;
	}

	protected void buildReport(String report, Map<String, Object> groupcfg, Hashtable cv) {

	}

	private void createReports() {

		JRPropertyManager jpm = new JRPropertyManager("com/opentach/reports/reports.properties");
		final JRReportDescriptor jrd = jpm.getDataMap().get(23);
		final Map<String, Object> params = this.getParams(jrd.getDscr(), jrd.getDelegCol());

		params.put("numreq", this.managedForm.getDataFieldValue("CG_CONTRATO"));
		params.put("f_ini", this.managedForm.getDataFieldValue("FILTERFECINI"));

		DateDataField cf = (DateDataField) this.managedForm.getDataFieldReference(OpentachFieldNames.FILTERFECFIN);
		Date fFin = (Date) cf.getDateValue();
		Calendar cal = Calendar.getInstance();
		cal.setTime(fFin);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		fFin = new Timestamp(cal.getTime().getTime());
		params.put("f_fin", fFin);
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
								Table tb = (Table) IMInformeDatosTecnicosVeh.this.managedForm.getElementReference("EInformeDatosTecnicosVeh");
								EntityResult res = new EntityResult((Hashtable) tb.getValue());
								Hashtable av = new Hashtable<String, Object>(params);
								jp = ocl.getRemoteService(IReportService.class).fillReport(urljr, av, jrd.getMethodAfter(), jrd.getMethodBefore(),
										res, ocl.getSessionId());
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
										p = IMInformeDatosTecnicosVeh.this.getParams(jru.getDscr(), jru.getDelegCol());
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
}
