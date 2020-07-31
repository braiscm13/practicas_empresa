package com.opentach.client.modules.report;

import com.ontimize.gui.Form;
import com.ontimize.gui.manager.IFormManager;
import com.opentach.client.modules.IMReportRoot;

public class IMCartasPorteADRConsulta extends IMReportRoot {

	public IMCartasPorteADRConsulta() {
		super("waybill.EWaybill", "informe_adr");
		this.setDateTags(new TimeStampDateTags("WAY_DATE"));
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
	}

	// protected Pair<Date, Date> computeFilterDates() {
	// Date fecFin = (Date) this.managedForm.getDataFieldValue("FILTERFECFIN");
	// Date fecIni = (Date) this.managedForm.getDataFieldValue("FILTERFECINI");
	// fecFin = new Date(fecFin.getTime() + (24 * 3600000));
	// return new Pair<>(fecIni, fecFin);
	// }
	//
	// @Override
	// protected Hashtable<String, Object> getParams(String title, String delegCol) {
	// Hashtable<String, Object> mParams = new Hashtable<String, Object>();
	// UReferenceDataField cCif = (UReferenceDataField) this.CIF;
	// String cif = (String) cCif.getValue();
	// Hashtable<String, Object> htRow = cCif.getCodeValues(cif);
	// String empresa = (String) htRow.get("NOMB");
	//
	// UReferenceDataField cCifCertif = (UReferenceDataField) this.managedForm.getDataFieldReference("CIF_CERTIFICADO");
	// if (cCifCertif != null) {
	// String sCifCertif = (String) cCifCertif.getValue();
	// if (sCifCertif != null) {
	// String empresaCert = (String) cCifCertif.getCodeValues(sCifCertif).get("NOMB");
	// mParams.put("empresa", empresaCert);
	// }
	//
	// }
	//
	// if (!mParams.containsKey("empresa")) {
	// mParams.put("empresa", empresa);
	//
	// }
	//
	// String cgContrato = (String) this.managedForm.getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD);
	// cgContrato = this.checkContratoFicticio(cgContrato);
	// // String numTarj =
	// // (String)this.managedForm.getDataFieldValue(OpentachFieldNames.NUMTARJETA_FIELD);
	// String numTarj = "";
	// BigDecimal idnumTarj = (BigDecimal) this.managedForm.getDataFieldValue(OpentachFieldNames.NUMTARJETA_FIELD);
	//
	// Pair<Date, Date> filterDates = this.computeFilterDates();
	// Date fecIni = filterDates.getFirst();
	// Date fecFin = filterDates.getSecond();
	//
	// mParams.put("numreq", cgContrato);
	// mParams.put("title", title);
	// mParams.put("f_informe", new Timestamp(fecFin.getTime()));
	// mParams.put("f_inicio", new Timestamp(fecIni.getTime()));
	// mParams.put("f_fin", new Timestamp(fecFin.getTime()));
	// mParams.put("locale", ApplicationManager.getLocale());
	// String sNumTarj = "";
	//
	// if (idnumTarj != null) {
	// Hashtable<String, Object> rcd = ((UReferenceDataField) this.managedForm.getDataFieldReference(OpentachFieldNames.NUMTARJETA_FIELD))
	// .getValuesToCode(idnumTarj);
	// numTarj += (String) rcd.get("NUM_TARJETA") + (String) rcd.get("INDICE_CONS") + (String) rcd.get("INDICE_RENOV")
	// + (String) rcd.get("INDICE_SUST");
	// mParams.put("tarjeta", numTarj);
	// sNumTarj = " and ( num_tarj = '" + numTarj + "' ";
	// }
	//
	// if (sNumTarj.length() > 0) {
	// sNumTarj = sNumTarj + " or num_tarj = '0000' ) ";
	// }
	// mParams.put("numtarj", sNumTarj);
	// String aux = " and ( idconductor = ";
	// Object oconductor = this.managedForm.getDataFieldValue("IDCONDUCTOR");
	// Vector<String> vDrivers = new Vector<String>();
	// if (oconductor == null) {
	// mParams.put("idconductor", "");
	// } else if (oconductor instanceof String) {
	// aux += "'" + oconductor + "' ) ";
	// mParams.put("idconductor", aux);
	// vDrivers.add((String) oconductor);
	// } else if (oconductor instanceof SearchValue) {
	// Vector<Object> v = (Vector<Object>) ((SearchValue) this.managedForm.getDataFieldValue("IDCONDUCTOR")).getValue();
	// for (int i = 0; i < v.size(); i++) {
	// Object idDriver = v.get(i);
	// if (i == 0) {
	// aux += "'" + idDriver + "'";
	// } else {
	// aux += " idconductor = '" + idDriver + "'";
	// }
	// if ((i + 1) != v.size()) {
	// aux += " or ";
	// }
	// vDrivers.add((String) idDriver);
	// }
	// aux += " ) ";
	// if (aux.length() > 22) {
	// mParams.put("idconductor", aux);
	// }
	// }
	//
	// if (!vDrivers.isEmpty()) {
	// mParams.put("DRIVER_FILTER", vDrivers);
	// }
	//
	// int i = 1;
	// DataComponentGroup filtergroup;
	// while ((filtergroup = (DataComponentGroup) this.managedForm.getElementReference(this.bandcfgname + "." + i)) != null) {
	// Vector<Object> attrs = filtergroup.getAttributes();
	// for (Enumeration<Object> enumeration = attrs.elements(); enumeration.hasMoreElements();) {
	// String attr = (String) enumeration.nextElement();
	// DataField campo = (DataField) this.managedForm.getDataFieldReference(attr);
	// if (campo != null) {
	// mParams.put(attr, ((Integer) campo.getValue()).intValue() == 0 ? false : true);
	// }
	// }
	// i++;
	// }
	//
	// i = 1;
	//
	// String auxFilter = " ( DSCR_ACT = ";
	// String auxAct = " ( DSCR_ACT = ";
	// while ((filtergroup = (DataComponentGroup) this.managedForm.getElementReference(this.filtergroupname + "." + i)) != null) {
	// Vector<Object> attrs = filtergroup.getAttributes();
	// int j = 0;
	// int k = 0;
	// for (Enumeration<Object> enumeration = attrs.elements(); enumeration.hasMoreElements();) {
	// String attr = (String) enumeration.nextElement();
	// DataField campo = (DataField) this.managedForm.getDataFieldReference(attr);
	// if (campo != null) {
	// boolean b = ((Integer) campo.getValue()).intValue() == 0 ? false : true;
	// if (b) {
	// if (k == 0) {
	// auxFilter += "'" + this.v.get(j - 1) + "'";
	// auxAct += "'" + this.v.get(j - 1) + "'";
	// k++;
	// } else {
	// auxFilter += " OR DSCR_ACT = '" + this.v.get(j - 1) + "'";
	// auxAct += " OR DSCR_ACT = '" + this.v.get(j - 1) + "'";
	// }
	// if (j == 6) {
	// auxFilter += " OR DSCR_ACT like '%COMIENZO_MARCADO_POR_INSERCION_DE_LA_TARJETA%'";
	// auxFilter += " OR DSCR_ACT like '%FIN_MARCADO_POR_LA_EXTRACCION_DE_LA_TARJETA%'";
	// auxFilter += " OR DSCR_ACT like '%COMIENZO_MARCADO_MANUALMENTE%'";
	// auxFilter += " OR DSCR_ACT like '%FIN_MARCADO_MANUALMENTE%'";
	// auxFilter += " OR DSCR_ACT like '%COMIENZO_MARCADO_POR_LA_VU%'";
	// auxFilter += " OR DSCR_ACT like '%FIN_MARCADO_POR_LA_VU%'";
	// }
	// }
	// }
	// j++;
	// }
	// i++;
	// }
	// auxFilter += " ) AND ";
	// auxAct += " ) AND ";
	//
	// mParams.put("pDSCR_ACT", auxAct);
	// mParams.put("pDSCR_FILTER", auxFilter);
	// return mParams;
	// }
	//
	// @Override
	// @SuppressWarnings("unchecked")
	// public void refreshTables() {
	// try {
	// if (this.comprobacionFechasFiltro(false)) {
	// Hashtable<String, Object> cvfiltro = this.getFilterValues();
	// List<Table> tbs = FIMUtils.getTables(this.managedForm);
	// if (tbs.isEmpty()) {
	// return;
	// }
	// for (Iterator<TimeStampDateTags> iterator = this.datetags.iterator(); iterator.hasNext();) {
	// TimeStampDateTags dt = iterator.next();
	// ArrayList<String> dtags = new ArrayList<String>();
	// if (dt.dateinitag != null) {
	// dtags.add(dt.dateinitag);
	// }
	// if (dt.datefintag != null) {
	// dtags.add(dt.datefintag);
	// }
	// Map<String, Object> av = this.getDateFilterValues(dtags);
	// if (av != null) {
	// cvfiltro.putAll(av);
	// }
	// }
	// ReferenceLocator buscador = (ReferenceLocator) this.managedForm.getFormManager().getReferenceLocator();
	// Table tblvisible = null;
	// // Ordeno las tablas para consultar primero la tabla visible
	// for (Iterator<Table> iter = tbs.iterator(); iter.hasNext();) {
	// Table tb = iter.next();
	// if (tb.isShowing()) {
	// tblvisible = tb;
	// break;
	// }
	// }
	// if (tblvisible != null) {
	// // tbs.clear();
	// tbs.remove(tblvisible);
	// tbs.add(0, tblvisible);
	// }
	// for (Iterator<Table> iter = tbs.iterator(); iter.hasNext();) {
	// try {
	// Table tb = iter.next();
	// Vector<String> keys = tb.getParentKeys();
	// Hashtable<String,Object> cv = new Hashtable<String,Object>();
	// if (cvfiltro != null) {
	// // añado al claves valores los parent keys de la
	// // tabla
	// cv.putAll(cvfiltro);
	// for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
	// String key = iterator.next();
	// if (cvfiltro.get(key) != null) {
	// cv.put(key, cvfiltro.get(key));
	// // Si el valor no esta en el filtro...
	// } else if (this.managedForm.getDataFieldValue(key) != null) {
	// cv.put(key, this.managedForm.getDataFieldValue(key));
	// }
	// }
	// Object extCond = cvfiltro.get(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY);
	// if (extCond != null) {
	// cv.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, extCond);
	// }
	// }
	// try {
	// if (this.managedForm.getDataFieldReference(OpentachFieldNames.NUMTARJETA_FIELD) != null) {
	// if (this.managedForm.getDataFieldValue(OpentachFieldNames.NUMTARJETA_FIELD) != null) {
	// String numTarj = "";
	// BigDecimal idnumTarj = (BigDecimal) this.managedForm.getDataFieldValue(OpentachFieldNames.NUMTARJETA_FIELD);
	// if (idnumTarj != null) {
	// Hashtable<String,Object> rcd = ((UReferenceDataField) this.managedForm
	// .getDataFieldReference(OpentachFieldNames.NUMTARJETA_FIELD)).getValuesToCode(idnumTarj);
	// numTarj += (String) rcd.get("NUM_TARJETA") + (String) rcd.get("INDICE_CONS")
	// + (String) rcd.get("INDICE_RENOV") + (String) rcd.get("INDICE_SUST");
	//
	// BasicExpression bs = new SQLStatementBuilder.BasicExpression(new BasicField("NUM_TARJ"),
	// BasicOperator.EQUAL_OP, numTarj);
	// BasicExpression bs2 = new SQLStatementBuilder.BasicExpression(new BasicField("NUM_TARJ"),
	// BasicOperator.EQUAL_OP, "0000");
	// BasicExpression bs3 = new SQLStatementBuilder.BasicExpression(bs, BasicOperator.OR_OP, bs2);
	//
	// if (cv.containsKey(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY)) {
	// BasicExpression bs4 = new SQLStatementBuilder.BasicExpression(
	// cv.get(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY),
	// BasicOperator.AND_OP, bs3);
	// cv.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, bs4);
	// } else {
	// cv.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, bs3);
	// }
	//
	// if (cv.get("NUM_TARJ") != null) {
	// cv.remove("NUM_TARJ");
	// }
	//
	// } else {
	//
	// BasicExpression bs = new SQLStatementBuilder.BasicExpression(new BasicField("NUM_TARJ"),
	// BasicOperator.NULL_OP, null);
	// BasicExpression bs2 = new SQLStatementBuilder.BasicExpression(new BasicField("NUM_TARJ"),
	// BasicOperator.EQUAL_OP, "0000");
	// BasicExpression bs3 = new SQLStatementBuilder.BasicExpression(bs, BasicOperator.OR_OP, bs2);
	//
	// if (cv.containsKey(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY)) {
	//
	// BasicExpression bs4 = new SQLStatementBuilder.BasicExpression(
	// cv.get(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY),
	// BasicOperator.AND_OP, bs3);
	// cv.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, bs4);
	// } else {
	// cv.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, bs3);
	// }
	//
	// if (cv.get("NUM_TARJ") != null) {
	// cv.remove("NUM_TARJ");
	// }
	//
	// }
	// }
	// }
	//
	// Entity ent = buscador.getEntityReference(tb.getEntityName());
	// Vector<Object> v = tb.getAttributeList();
	// EntityResult res = ent.query(cv, v, buscador.getSessionId());
	// if ((res.getCode() == EntityResult.OPERATION_SUCCESSFUL) && (res.calculateRecordNumber() > 0)) {
	// if (tb != null) {
	// int fila = tb.getSelectedRow();
	// Hashtable<Object,Object> key = null;
	// if (fila >= 0) {
	// Vector<Object> vk = tb.getKeys();
	// Hashtable<String,Object> datosFila = tb.getRowData(fila);
	// key = new Hashtable<Object,Object>();
	// for (int i = 0; i < vk.size(); i++) {
	// Object ok = vk.elementAt(i);
	// Object ov = datosFila.get(ok);
	// if (ov != null) {
	// key.put(ok, ov);
	// }
	// }
	// }
	// tb.setValue(res);
	// if ((fila >= 0) && (key != null)) {
	// fila = tb.getRowForKeys(key);
	// if (fila >= 0) {
	// tb.setSelectedRow(fila);
	// }
	// }
	// tb.setEnabled((res.calculateRecordNumber() > 0) ? true : false);
	// }
	// } else if (res.getCode() == EntityResult.OPERATION_WRONG) {
	// this.managedForm.message(res.getMessage(), Form.WARNING_MESSAGE);
	// tb.setValue(null);
	// } else {
	// tb.setValue(null);
	// }
	// } catch (NullPointerException nex) {
	//
	// } catch (Throwable err) {
	// // IMDataRoot.logger.log(Level.SEVERE,
	// // err.getMessage(), err);
	// ;
	// }
	// } catch (Exception e) {
	// // IMDataRoot.logger.log(Level.SEVERE, e.getMessage(),
	// // e);
	// ;
	// }
	// }
	// }
	// } catch (Exception e1) {
	// // IMDataRoot.logger.log(Level.SEVERE, e1.getMessage(), e1);
	// ;
	// }
	// }

}
