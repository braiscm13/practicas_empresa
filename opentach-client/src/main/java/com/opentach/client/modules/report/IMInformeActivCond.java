package com.opentach.client.modules.report;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.db.SQLStatementBuilder;
import com.ontimize.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.db.SQLStatementBuilder.BasicField;
import com.ontimize.db.SQLStatementBuilder.BasicOperator;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.container.DataComponentGroup;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.Pair;
import com.ontimize.locator.ReferenceLocator;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.comp.ExtendedApplicationManager;
import com.opentach.client.modules.IMReportRoot;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.report.IReportService;
import com.opentach.common.report.util.JRPropertyManager;
import com.opentach.common.report.util.JRReportDescriptor;
import com.utilmize.client.fim.FIMUtils;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.utilmize.client.report.JRDialogViewer;

import net.sf.jasperreports.engine.JasperPrint;

public class IMInformeActivCond extends IMReportRoot {

	private static final Logger	logger	= LoggerFactory.getLogger(IMInformeActivCond.class);

	Vector<Object>				v		= new Vector<Object>();

	public IMInformeActivCond() {

		super("EInformeActivPeriodosCond", "informe_activ_cond");
		this.dateEntity = "EUFecha";
		HashMap<String, String> mEntityReport = new HashMap<String, String>();
		mEntityReport.put("informe_activ_cond.xml", "EInformeActivPeriodosCond");
		this.setEntidadesInformes(mEntityReport);
		this.setDateTags(new TimeStampDateTags(OpentachFieldNames.FECINI_FIELD));

		this.v.add(4);
		this.v.add(1);
		this.v.add(3);
		this.v.add(2);
		this.v.add(5);
		this.v.add(6);

		//		this.v.add("CONDUCCION");
		//		this.v.add("PAUSA/DESCANSO");
		//		this.v.add("TRABAJO");
		//		this.v.add("DISPONIBILIDAD");
		//		this.v.add("INDEFINIDA");
		//		this.v.add("PERIODOS_TRABAJO");
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.managedForm.setAdvancedQueryMode(OpentachFieldNames.IDCONDUCTOR_FIELD, true);

		Button bt = f.getButton("btnInforme2");
		bt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				IMInformeActivCond.this.createReports();
			}
		});
	}

	protected void createReports() {
		if (!this.checkRequiredVisibleDataFields(true)) {
			return;
		}
		JRPropertyManager jpm = new JRPropertyManager("com/opentach/reports/reports.properties");

		final JRReportDescriptor jrd = jpm.getDataMap().get(31);

		final Map<String, Object> params = this.getParams(jrd.getDscr(), jrd.getDelegCol());
		final JFrame jd = (JFrame) SwingUtilities.getWindowAncestor(this.managedForm);
		final OpentachClientLocator ocl = (OpentachClientLocator) this.formManager.getReferenceLocator();
		if (jrd != null) {
			final List<JRReportDescriptor> lReports = jrd.getLReports();
			if ((lReports == null) || (lReports.size() == 0)) {
				try {
					final String urljr = jrd.getUrl();
					OperationThread opth = new OperationThread() {
						@Override
						public void run() {
							this.hasStarted = true;
							JasperPrint jp = null;
							try {
								jp = ocl.getRemoteService(IReportService.class).fillReport(urljr, params, jrd.getMethodAfter(), jrd.getMethodBefore(), null, ocl.getSessionId());
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

	protected Pair<Date, Date> computeFilterDates() {
		Date fecFin = (Date) this.managedForm.getDataFieldValue("FILTERFECFIN");
		Date fecIni = (Date) this.managedForm.getDataFieldValue("FILTERFECINI");
		fecFin = new Date(fecFin.getTime() + (24 * 3600000));
		return new Pair<>(fecIni, fecFin);
	}

	@Override
	protected Hashtable<String, Object> getParams(String title, String delegCol) {
		Hashtable<String, Object> mParams = new Hashtable<String, Object>();
		UReferenceDataField cCif = (UReferenceDataField) this.CIF;
		String cif = (String) cCif.getValue();
		Map<String, Object> htRow = cCif.getCodeValues(cif);
		String empresa = (String) htRow.get("NOMB");

		UReferenceDataField cCifCertif = (UReferenceDataField) this.managedForm.getDataFieldReference("CIF_CERTIFICADO");
		if (cCifCertif != null) {
			String sCifCertif = (String) cCifCertif.getValue();
			if (sCifCertif != null) {
				String empresaCert = (String) cCifCertif.getCodeValues(sCifCertif).get("NOMB");
				mParams.put("empresa", empresaCert);
			}

		}

		if (!mParams.containsKey("empresa")) {
			mParams.put("empresa", empresa);

		}

		String cgContrato = (String) this.managedForm.getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD);
		cgContrato = this.checkContratoFicticio(cgContrato);
		// String numTarj =
		// (String)this.managedForm.getDataFieldValue(OpentachFieldNames.NUMTARJETA_FIELD);
		String numTarj = "";
		BigDecimal idnumTarj = (BigDecimal) this.managedForm.getDataFieldValue(OpentachFieldNames.NUMTARJETA_FIELD);

		Pair<Date, Date> filterDates = this.computeFilterDates();
		Date fecIni = filterDates.getFirst();
		Date fecFin = filterDates.getSecond();

		mParams.put("numreq", cgContrato);
		mParams.put("title", ApplicationManager.getTranslation(title));
		mParams.put("f_informe", new Timestamp(fecFin.getTime()));
		mParams.put("f_inicio", new Timestamp(fecIni.getTime()));
		Calendar d = Calendar.getInstance();
		d.setTime(fecFin);
		if ((this instanceof IMInformeActivCondMacron)){
			d.set(Calendar.HOUR_OF_DAY, +24);
			d.set(Calendar.MINUTE, 0);
			d.set(Calendar.SECOND, 0);
		}
		mParams.put("f_fin", new Timestamp(d.getTime().getTime()));
		mParams.put("locale", ApplicationManager.getLocale());
		String sNumTarj = "";

		if (idnumTarj != null) {
			Map<String, Object> rcd = ((UReferenceDataField) this.managedForm.getDataFieldReference(OpentachFieldNames.NUMTARJETA_FIELD)).getValuesToCode(idnumTarj);
			numTarj += (String) rcd.get("NUM_TARJETA") + (String) rcd.get("INDICE_CONS") + (String) rcd.get("INDICE_RENOV") + (String) rcd.get("INDICE_SUST");
			mParams.put("tarjeta", numTarj);
			sNumTarj = " and (  num_tarj = '" + numTarj + "' ";
		}

		if (sNumTarj.length() > 0) {
			sNumTarj = sNumTarj + " or num_tarj = '0000' ) ";
		}
		mParams.put("numtarj", sNumTarj);
		String aux = " and ( idconductor = ";
		Object oconductor = this.managedForm.getDataFieldValue("IDCONDUCTOR");
		Vector<String> vDrivers = new Vector<String>();
		if (oconductor == null) {
			mParams.put("idconductor", "");
		} else if (oconductor instanceof String) {
			aux += "'" + oconductor + "' ) ";
			mParams.put("idconductor", aux);
			vDrivers.add((String) oconductor);
		} else if (oconductor instanceof SearchValue) {
			Vector<Object> v = (Vector<Object>) ((SearchValue) this.managedForm.getDataFieldValue("IDCONDUCTOR")).getValue();
			for (int i = 0; i < v.size(); i++) {
				Object idDriver = v.get(i);
				if (i == 0) {
					aux += "'" + idDriver + "'";
				} else {
					aux += " idconductor = '" + idDriver + "'";
				}
				if ((i + 1) != v.size()) {
					aux += "  or ";
				}
				vDrivers.add((String) idDriver);
			}
			aux += " ) ";
			if (aux.length() > 22) {
				mParams.put("idconductor", aux);
			}
		}

		if (!vDrivers.isEmpty()) {
			mParams.put("DRIVER_FILTER", vDrivers);
		}

		int i = 1;
		DataComponentGroup filtergroup;
		while ((filtergroup = (DataComponentGroup) this.managedForm.getElementReference(this.bandcfgname + "." + i)) != null) {
			Vector<Object> attrs = filtergroup.getAttributes();
			for (Enumeration<Object> enumeration = attrs.elements(); enumeration.hasMoreElements();) {
				String attr = (String) enumeration.nextElement();
				DataField campo = (DataField) this.managedForm.getDataFieldReference(attr);
				if (campo != null) {
					mParams.put(attr, ((Integer) campo.getValue()).intValue() == 0 ? false : true);
				}
			}
			i++;
		}

		i = 1;


		String auxFilter = " ( TPACTIVIDAD = ";
		String auxAct = " ( TPACTIVIDAD = ";
		while ((filtergroup = (DataComponentGroup) this.managedForm.getElementReference(this.filtergroupname + "." + i)) != null) {
			Vector<Object> attrs = filtergroup.getAttributes();
			int j = 0;
			int k = 0;
			for (Enumeration<Object> enumeration = attrs.elements(); enumeration.hasMoreElements();) {
				String attr = (String) enumeration.nextElement();
				DataField campo = (DataField) this.managedForm.getDataFieldReference(attr);
				if (campo != null) {
					boolean b = ((Integer) campo.getValue()).intValue() == 0 ? false : true;
					if (b) {
						if (k == 0) {
							auxFilter += "'" + this.v.get(j - 1) + "'";
							auxAct += "'" + this.v.get(j - 1) + "'";
							k++;
						} else {
							auxFilter += " OR TPACTIVIDAD = '" + this.v.get(j - 1) + "'";
							auxAct += " OR TPACTIVIDAD = '" + this.v.get(j - 1) + "'";
						}
						if (j == 6) {
							auxFilter += " OR TPPERIODO IS NOT NULL";
						}
					}
				}
				j++;
			}
			i++;
		}
		auxFilter += " ) AND ";
		auxAct += " ) AND ";

		mParams.put("pDSCR_ACT", auxAct);
		mParams.put("pDSCR_FILTER", auxFilter);
		return mParams;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void refreshTables() {
		if (this.comprobacionFechasFiltro(false)) {
			Hashtable<String, Object> cvfiltro = this.getFilterValues();
			List<Table> tbs = FIMUtils.getTables(this.managedForm);
			if (tbs.isEmpty()) {
				return;
			}
			for (Iterator<TimeStampDateTags> iterator = this.datetags.iterator(); iterator.hasNext();) {
				TimeStampDateTags dt = iterator.next();
				ArrayList<String> dtags = new ArrayList<String>();
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
			ReferenceLocator buscador = (ReferenceLocator) this.managedForm.getFormManager().getReferenceLocator();
			Table tblvisible = null;
			// Ordeno las tablas para consultar primero la tabla visible
			for (Iterator<Table> iter = tbs.iterator(); iter.hasNext();) {
				Table tb = iter.next();
				if (tb.isShowing()) {
					tblvisible = tb;
					break;
				}
			}
			if (tblvisible != null) {
				// tbs.clear();
				tbs.remove(tblvisible);
				tbs.add(0, tblvisible);
			}
			for (Iterator<Table> iter = tbs.iterator(); iter.hasNext();) {
				try {
					Table tb = iter.next();
					Vector<String> keys = tb.getParentKeys();
					Hashtable<String, Object> cv = new Hashtable<String, Object>();
					if (cvfiltro != null) {
						// añado al claves valores los parent keys de la
						// tabla
						cv.putAll(cvfiltro);
						for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
							String key = iterator.next();
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
					if (this.managedForm.getDataFieldReference(OpentachFieldNames.NUMTARJETA_FIELD) != null) {
						if (this.managedForm.getDataFieldValue(OpentachFieldNames.NUMTARJETA_FIELD) != null) {
							String numTarj = "";
							BigDecimal idnumTarj = (BigDecimal) this.managedForm.getDataFieldValue(OpentachFieldNames.NUMTARJETA_FIELD);
							if (idnumTarj != null) {
								Map<String, Object> rcd = ((UReferenceDataField) this.managedForm.getDataFieldReference(OpentachFieldNames.NUMTARJETA_FIELD))
										.getValuesToCode(idnumTarj);
								numTarj += (String) rcd.get("NUM_TARJETA") + (String) rcd.get("INDICE_CONS") + (String) rcd.get("INDICE_RENOV") + (String) rcd.get("INDICE_SUST");

								BasicExpression bs = new SQLStatementBuilder.BasicExpression(new BasicField("NUM_TARJ"), BasicOperator.EQUAL_OP, numTarj);
								BasicExpression bs2 = new SQLStatementBuilder.BasicExpression(new BasicField("NUM_TARJ"), BasicOperator.EQUAL_OP, "0000");
								BasicExpression bs3 = new SQLStatementBuilder.BasicExpression(bs, BasicOperator.OR_OP, bs2);

								if (cv.containsKey(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY)) {
									BasicExpression bs4 = new SQLStatementBuilder.BasicExpression(cv.get(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY),
											BasicOperator.AND_OP, bs3);
									cv.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, bs4);
								} else {
									cv.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, bs3);
								}

								if (cv.get("NUM_TARJ") != null) {
									cv.remove("NUM_TARJ");
								}

							} else {

								BasicExpression bs = new SQLStatementBuilder.BasicExpression(new BasicField("NUM_TARJ"), BasicOperator.NULL_OP, null);
								BasicExpression bs2 = new SQLStatementBuilder.BasicExpression(new BasicField("NUM_TARJ"), BasicOperator.EQUAL_OP, "0000");
								BasicExpression bs3 = new SQLStatementBuilder.BasicExpression(bs, BasicOperator.OR_OP, bs2);

								if (cv.containsKey(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY)) {

									BasicExpression bs4 = new SQLStatementBuilder.BasicExpression(cv.get(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY),
											BasicOperator.AND_OP, bs3);
									cv.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, bs4);
								} else {
									cv.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, bs3);
								}

								if (cv.get("NUM_TARJ") != null) {
									cv.remove("NUM_TARJ");
								}

							}
						}
					}

					Entity ent = buscador.getEntityReference(tb.getEntityName());
					Vector<Object> v = tb.getAttributeList();
					EntityResult res = ent.query(cv, v, buscador.getSessionId());
					if ((res.getCode() == EntityResult.OPERATION_SUCCESSFUL) && (res.calculateRecordNumber() > 0)) {
						if (tb != null) {
							int fila = tb.getSelectedRow();
							Hashtable<Object, Object> key = null;
							if (fila >= 0) {
								Vector<Object> vk = tb.getKeys();
								Hashtable<String, Object> datosFila = tb.getRowData(fila);
								key = new Hashtable<Object, Object>();
								for (int i = 0; i < vk.size(); i++) {
									Object ok = vk.elementAt(i);
									Object ov = datosFila.get(ok);
									if (ov != null) {
										key.put(ok, ov);
									}
								}
							}
							tb.setValue(res);
							if ((fila >= 0) && (key != null)) {
								fila = tb.getRowForKeys(key);
								if (fila >= 0) {
									tb.setSelectedRow(fila);
								}
							}
							tb.setEnabled((res.calculateRecordNumber() > 0) ? true : false);
						}
					} else if (res.getCode() == EntityResult.OPERATION_WRONG) {
						this.managedForm.message(res.getMessage(), Form.WARNING_MESSAGE);
						tb.setValue(null);
					} else {
						tb.setValue(null);
					}
				} catch (Throwable error) {
					IMInformeActivCond.logger.error(null, error);
				}
			}
		}
	}

}
