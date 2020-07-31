package com.opentach.client.modules.report;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.manager.IFormManager;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.comp.ExtendedApplicationManager;
import com.opentach.client.modules.IMRoot;
import com.opentach.client.report.components.JRComboReport;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.report.IReportService;
import com.opentach.common.report.util.JRReportDescriptor;
import com.opentach.common.user.IUserData;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.utilmize.client.report.JRDialogViewer;
import com.utilmize.tools.report.JRReportUtil;

import net.sf.jasperreports.engine.JasperPrint;

public class IMInformeSelec extends IMRoot {

	private static final Logger	logger		= LoggerFactory.getLogger(IMInformeSelec.class);

	// Report parameter names
	public static final String	pNUMREQ		= "numreq";
	public static final String	pCIF		= "cif";
	public static final String	pEMPRESA	= "empresa";
	public static final String	pTITLE		= "title";
	public static final String	pFINFORME	= "f_informe";
	public static final String	pSQLDELEG	= "sqldeleg";

	private Button				bInforme	= null;
	private JRComboReport		cmbReport	= null;
	private JFileChooser		jfc			= null;

	public IMInformeSelec() {
		super();
		this.fieldsChain.clear();
		this.fieldsChain.add(OpentachFieldNames.CIF_FIELD);
		this.fieldsChain.add(OpentachFieldNames.CG_CONTRATO_FIELD);
		this.fieldsChain.add(OpentachFieldNames.IDCONDUCTOR_FIELD);
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.bInforme = f.getButton("btnInforme");
		this.cmbReport = (JRComboReport) f.getDataFieldReference("SELECTOR");
		this.cgContract = f.getDataFieldReference(OpentachFieldNames.CG_CONTRATO_FIELD);
		if (this.bInforme != null) {
			this.bInforme.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					IMInformeSelec.this.accBotInforme();
				}
			});
		}
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.setUpdateMode();
		this.managedForm.enableButton("btnInforme");
		this.managedForm.enableDataField("MAXVAL.2");
		this.managedForm.setDataFieldValue("MAXVAL.2", new Date());
	}

	private void accBotInforme() {
		if (!this.checkRequiredVisibleDataFields(true)) {
			return;
		}
		this.createReports();
	}

	protected Map<String, Object> getParams(String title, String delegCol) {
		Map<String, Object> mParams = new HashMap<String, Object>();
		UReferenceDataField cCif = (UReferenceDataField) this.CIF;
		String cif = (String) cCif.getValue();
		Map<String, Object> htRow = cCif.getCodeValues(cif);
		String empresa = (String) htRow.get("NOMB");
		String cgContrato = (String) this.managedForm.getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD);
		cgContrato = this.checkContratoFicticio(cgContrato);
		Date fecFin = (Date) this.managedForm.getDataFieldValue("MAXVAL.2");
		mParams.put(IMInformeSelec.pNUMREQ, cgContrato);
		mParams.put(IMInformeSelec.pCIF, cif);
		mParams.put(IMInformeSelec.pEMPRESA, empresa);
		mParams.put(IMInformeSelec.pTITLE, title);
		mParams.put(IMInformeSelec.pFINFORME, new Timestamp(fecFin == null ? new Date().getTime() : fecFin.getTime()));
		this.putSqlDelegParam(mParams, title, delegCol);
		if ("Informe_resumen_de_actividades".equals(title) || "Informe_resumen_de_infracciones".equals(title) || "Informe_resumen_sobre_las_actividades_realizadas_por_los_vehiculos_y_conductores_de_la_empresa"
				.equals(title)) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(fecFin);
			cal.add(Calendar.DATE, -30);

			mParams.put("f_desde", new Timestamp(cal.getTimeInMillis()));
			mParams.put("f_hasta", new Timestamp(fecFin.getTime() + (24 * 3600000)));
		} else if ("Informe_resumen_de_usos_de_vehiculo".equals(title) || "Informe_resumen_de_Incidentes_y_eventos_de_tacografo".equals(title)) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(fecFin);
			cal.add(Calendar.DATE, -90);

			mParams.put("f_desde", new Timestamp(cal.getTimeInMillis()));
			mParams.put("f_hasta", new Timestamp(fecFin.getTime() + (24 * 3600000)));
		}
		return mParams;
	}

	private void putSqlDelegParam(Map<String, Object> mParams, String title, String delegCol) {

		StringBuffer sb = new StringBuffer();
		IUserData du = null;
		final OpentachClientLocator ocl = (OpentachClientLocator) this.formManager.getReferenceLocator();
		try {

			du = ocl.getUserData();
			String prefix = "";
			if ("Informe_resumen_de_Incidentes_y_eventos_de_tacografo".equals(title)//
					|| "Informe_resumen_sobre_las_incidencias_detectadas_en_el_periodo".equals(title)//
					) {
				prefix = "cdvehiculos_emp.";
			} else if ("Informe_resumen_de_actividades".equals(title)//
					|| "Informe_resumen_de_infracciones".equals(title)//
					|| "Informe_resumen_sobre_las_actividades_realizadas_por_los_vehiculos_y_conductores_de_la_empresa".equals(title)//
					) {
				prefix = "CDCONDUCTORES_EMP.";
			} else if ("Informe_resumen_de_descargas".equals(title)) {
				prefix = "CDVEMPRE_REQ_REALES.";
			}

			Object selectedCif = this.managedForm.getDataFieldValue(OpentachFieldNames.CIF_FIELD);
			if (selectedCif == null) {
				if ((du.getDelegList() != null) && (!du.getDelegList().isEmpty())) {
					sb.append("  AND ").append(" ( ");
					Map<String, Number> l = du.getDelegList();
					Set<String> values = l.keySet();
					Iterator<String> itr = values.iterator();
					while (itr.hasNext()) {
						Object key = itr.next();
						Object value = l.get(key);

						sb.append(" ( ").append(prefix).append("CIF = '").append(key).append("' ");
						if ((value == null) || (((Number) value).intValue() == -1)) {
							// sb.append(" IS NULL )");
							sb.append(")");
						} else {
							sb.append("AND IDDELEGACION").append(" =  '").append(value).append("' )");
						}
						if (itr.hasNext()) {
							sb.append(" OR ");
						} else {
							sb.append(" ) ");
						}
					}
				}
			} else {
				sb.append("  AND ").append(" ( ");
				sb.append(" ( ").append(prefix).append("CIF = '").append(selectedCif).append("' ");
				if ((du.getDelegList() != null) && (du.getDelegList().get(selectedCif) != null)) {
					Number value = du.getDelegList().get(selectedCif);
					if ((value == null) || (value.intValue() == -1)) {
						sb.append(")");
					} else {
						sb.append("' AND IDDELEGACION").append(" =  '").append(value).append("' )");
					}
				} else {
					sb.append(" ) ");
				}
				sb.append(" ) ");
			}

			mParams.put(IMInformeSelec.pSQLDELEG, sb.toString());
			if (!"".equals(prefix)) {
				mParams.put(IMInformeSelec.pSQLDELEG + "_base", sb.toString().replaceAll(prefix, ""));
			}

		} catch (Exception e) {
			IMInformeSelec.logger.error(null, e);
		}
	}

	private void createReports() {
		final JRReportDescriptor jrd = (JRReportDescriptor) this.cmbReport.getValue();
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
								IMInformeSelec.logger.error(null, e);
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
										p = IMInformeSelec.this.getParams(jru.getDscr(), jru.getDelegCol());
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
}
