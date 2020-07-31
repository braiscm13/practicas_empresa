package com.opentach.client.modules.report;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.db.EntityResultUtils;
import com.ontimize.db.SQLStatementBuilder;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.locator.ReferenceLocator;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.comp.ExtendedApplicationManager;
import com.opentach.client.modules.IMReportRoot;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.report.util.JRPropertyManager;
import com.opentach.common.report.util.JRReportDescriptor;
import com.opentach.common.util.ResourceManager;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.utilmize.client.report.JRDialogViewer;
import com.utilmize.tools.report.JRReportUtil;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;

public class IMInformeResumenTiposInfracciones extends IMReportRoot {

	public IMInformeResumenTiposInfracciones() {
		super("EInformeResumenTiposInfracciones", "informe_resumen_tipos_infracciones");
		HashMap<String, String> mEntityReport = new HashMap<String, String>();
		mEntityReport.put("EInformeResumenTiposInfracciones", "EInformeResumenTiposInfracciones");
		this.setEntidadesInformes(mEntityReport);
		//		this.dateEntity = "EUFecha";
		//		this.setDateTags(new TimeStampDateTags(OpentachFieldNames.FECINI_FIELD,OpentachFieldNames.FECFIN_FIELD));
	}

	@Override
	public void doOnQuery(final boolean alert) {
		if (this.managedForm.existEmptyRequiredDataField()) {
			if (alert) {
				this.managedForm.message("Establezca filtro de búsqueda.", Form.INFORMATION_MESSAGE);
			}
		} else if (this.checkQuery()) {
			this.refreshTable(this.tablename);
		}
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.refreshTable("EInformeResumenTiposInfracciones");
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {

		super.registerInteractionManager(f, gf);
		Button bt = f.getButton("btnInforme2");
		bt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				IMInformeResumenTiposInfracciones.this.createReports();
			}
		});
	}

	protected void createReports() {

		JRPropertyManager jpm = new JRPropertyManager("com/opentach/reports/reports.properties");

		final JRReportDescriptor jrd = jpm.getDataMap().get(44);

		final Map<String, Object> params = this.getParams(jrd.getDscr(), jrd.getDelegCol());
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
								Hashtable<String, Object> mParams = new Hashtable<String, Object>();
								mParams.putAll(params);
								mParams.put("TIPO_INFORME", "1");
								mParams.put(JRParameter.REPORT_LOCALE, Locale.getDefault());
								ResourceBundle rb = ResourceManager.getBundle(Locale.getDefault());
								if (rb != null) {
									mParams.put(JRParameter.REPORT_RESOURCE_BUNDLE, rb);
								}

								jp = JRReportUtil.fillReport(JRReportUtil.getJasperReport(urljr), mParams,
										(EntityResult) ((Table) IMInformeResumenTiposInfracciones.this.managedForm.getDataFieldReference("EInformeResumenTiposInfracciones"))
										.getValue());
								//								jp = ocl.getRemoteService(IReportService.class).fillReport(urljr, params, jrd.getMethodAfter(),
								//										jrd.getMethodBefore(), (EntityResult)((Table)IMInformeResumenTiposInfracciones.this.managedForm.getDataFieldReference("EInformeResumenTiposInfracciones")).getValue(), ocl.getSessionId());
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
			}
		}
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
		mParams.put("numreq", cgContrato);
		mParams.put("cif", cif);
		mParams.put("empresa", empresa);
		mParams.put("title", ApplicationManager.getTranslation(title));
		// mParams.put("f_informe", new Timestamp(fecFin.getTime()));
		mParams.put("sqldeleg", "");
		mParams.put("f_inicio", new Timestamp(fecIni.getTime()));
		mParams.put("f_fin", new Timestamp(fecFin.getTime()));

		EntityResult tableValue = (EntityResult) ((Table) this.managedForm.getElementReference("EInformeResumenTiposInfracciones")).getValue();
		int dataIndex = tableValue.calculateRecordNumber() - 1;
		mParams.put("TIPOS_INFRAC", new JRTableModelDataSource(EntityResultUtils.createTableModel((EntityResult) ((Vector) tableValue.get("TIPOS_INFRAC")).get(dataIndex))));
		mParams.put("HECHOS_SANCIONABLES",
				new JRTableModelDataSource(EntityResultUtils.createTableModel((EntityResult) ((Vector) (tableValue).get("HECHOS_SANCIONABLES")).get(dataIndex))));

		return mParams;
	}


	@Override
	@SuppressWarnings("unchecked")
	protected void refreshTable(String tablename) {
		try {
			Hashtable<String, Object> cvfiltro = this.getFilterValues();
			if (this.datetags != null) {
				for (TimeStampDateTags dt : this.datetags) {
					List<String> dtags = new ArrayList<String>();
					if (dt.dateinitag != null) {
						dtags.add(dt.dateinitag);
					}
					if (dt.datefintag != null) {
						dtags.add(dt.datefintag);
					}
					Map<String, Object> av = this.getDateFilterValues(dtags);
					if (av != null) {
						cvfiltro.putAll(av);
					}
				}
			}
			ReferenceLocator buscador = (ReferenceLocator) this.managedForm.getFormManager().getReferenceLocator();
			Table tb = (Table) this.managedForm.getDataFieldReference(tablename);
			Vector<Object> keys = tb.getParentKeys();
			Hashtable<String, Object> cv = new Hashtable<String, Object>();
			if (cvfiltro != null) {
				cv.putAll(cvfiltro);
				// añado al claves valores los parent keys de la tabla
				for (Iterator<Object> iterator = keys.iterator(); iterator.hasNext();) {
					String key = (String) iterator.next();
					if (cvfiltro.get(key) != null) {
						cv.put(key, cvfiltro.get(key));
						// Si el valor no esta en el filtro...
					} else if (this.managedForm.getDataFieldValue(key) != null) {
						cv.put(key, this.managedForm.getDataFieldValue(key));
					}
				}
				Object extCond = cvfiltro.get(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY);
				if (extCond != null) {
					cv.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, extCond);
				}
			}
			Entity ent = buscador.getEntityReference(tb.getEntityName());
			if (ent != null) {
				Vector<Object> v = tb.getAttributeList();
				EntityResult res = ent.query(cv, v, buscador.getSessionId());
				if (res.getCode() == EntityResult.OPERATION_SUCCESSFUL) {
					if (tb != null) {
						tb.setValue(res);
						tb.setEnabled((res.calculateRecordNumber() > 0) ? true : false);
					}
				}
			}
		} catch (Exception e) {
		}
	}

}
