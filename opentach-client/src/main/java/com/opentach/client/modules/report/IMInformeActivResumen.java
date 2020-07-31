package com.opentach.client.modules.report;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.manager.IFormManager;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.comp.ExtendedApplicationManager;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.report.IReportService;
import com.opentach.common.report.util.JRPropertyManager;
import com.opentach.common.report.util.JRReportDescriptor;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.utilmize.client.report.JRDialogViewer;
import com.utilmize.tools.report.JRReportUtil;

import net.sf.jasperreports.engine.JasperPrint;

public class IMInformeActivResumen extends IMInformeResumen {

	private JFileChooser	jfc	= null;

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {

		super.registerInteractionManager(f, gf);
		if (this.bInforme2 != null) {
			this.bInforme2.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					IMInformeActivResumen.this.accBotInforme2();
				}
			});
		}
	}

	private void accBotInforme2() {
		if (!this.checkRequiredVisibleDataFields(true)) {
			return;
		}
		this.createReports();
	}

	private void createReports() {
		JRPropertyManager jpm = new JRPropertyManager("com/opentach/reports/reports.properties");

		final JRReportDescriptor jrd = jpm.getDataMap().get(10);

		final Map<String, Object> params = this.getParams(jrd.getDscr(), jrd.getDelegCol());
		final JDialog jd = (JDialog) SwingUtilities.getWindowAncestor(this.managedForm);
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
								jp = ocl.getRemoteService(IReportService.class).fillReport(urljr, params, jrd.getMethodAfter(),
										jrd.getMethodBefore(), null, ocl.getSessionId());
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
										p = IMInformeActivResumen.this.getParams(jru.getDscr(), jru.getDelegCol());
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
					FileOutputStream fos = null;
					if ((jpList != null) && (jpList.size() > 1)) {
						if (this.jfc == null) {
							this.jfc = new JFileChooser();
						}
						this.jfc.setLocale(ApplicationManager.getLocale());
						this.jfc.updateUI();
						this.jfc.setSelectedFile(new File(jrd.getName() + ".pdf"));
						int returnVal = this.jfc.showSaveDialog(this.managedForm);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							File f = this.jfc.getSelectedFile();
							try {
								fos = new FileOutputStream(f);
								JRReportUtil.exportToPDF(jpList, fos);
							} catch (Exception e) {
								e.printStackTrace();
								throw e;
							} finally {
								if (fos != null) {
									try {
										fos.close();
									} catch (Exception e) {
									}
								}
							}
						}
					} else {
						this.managedForm.message("M_NO_SE_HAN_ENCONTRADO_DATOS", Form.WARNING_MESSAGE);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected Map<String, Object> getParams(String title, String delegCol) {
		Map<String, Object> mParams = new HashMap<String, Object>();
		UReferenceDataField cCif = (UReferenceDataField) this.CIF;
		String cif = (String) cCif.getValue();
		Map htRow = cCif.getCodeValues(cif);
		String empresa = (String) htRow.get("NOMB");
		String cgContrato = (String) this.managedForm.getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD);
		cgContrato = this.checkContratoFicticio(cgContrato);
		Date fecFin = (Date) this.managedForm.getDataFieldValue("MAXVAL.2");
		Date fecIni = (Date) this.managedForm.getDataFieldValue("MINVAL.2");
		mParams.put("numreq", cgContrato);
		mParams.put("cif", cif);
		mParams.put("empresa", empresa);
		mParams.put("title", title);
		// mParams.put("f_informe", new Timestamp(fecFin.getTime()));
		mParams.put("sqldeleg", "");
		mParams.put("f_desde", new Timestamp(fecIni.getTime()));
		mParams.put("f_hasta", new Timestamp(fecFin.getTime() + (24 * 3600000)));
		return mParams;
	}
}
