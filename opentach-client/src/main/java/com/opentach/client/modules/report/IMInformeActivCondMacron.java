package com.opentach.client.modules.report;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.db.SQLStatementBuilder;
import com.ontimize.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.gui.Form;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.jee.common.tools.DateTools;
import com.ontimize.jee.common.tools.Pair;
import com.opentach.client.comp.CampoFechaNoEditable;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.util.DateUtil;
import com.utilmize.client.gui.field.UDurationMaskDataField;

public class IMInformeActivCondMacron extends IMInformeActivCond {

	private static final Logger	logger	= LoggerFactory.getLogger(IMInformeActivCondMacron.class);
	@FormComponent(attr = "FILTERMININI")
	UDurationMaskDataField		filterMinIni;
	@FormComponent(attr = "FILTERFECINI")
	CampoFechaNoEditable		filterFecIni;
	@FormComponent(attr = "FILTERMINFIN")
	UDurationMaskDataField		filterMinFin;
	@FormComponent(attr = "FILTERFECFIN")
	CampoFechaNoEditable		filterFecFin;

	public IMInformeActivCondMacron() {
		super();
	}


	@Override
	protected Pair<Date, Date> computeFilterDates() {
		Date fecIni = (Date) this.managedForm.getDataFieldValue("FILTERFECINI");
		Date fecFin = (Date) this.managedForm.getDataFieldValue("FILTERFECFIN");
		Long durationIni = (Long) this.managedForm.getDataFieldValue("FILTERMININI");
		Long durationFin = (Long) this.managedForm.getDataFieldValue("FILTERMINFIN");
		Calendar calFecFin = Calendar.getInstance();
		calFecFin.setTime(fecFin);
		Calendar calFecIni = Calendar.getInstance();
		calFecIni.setTime(fecIni);
		if (durationIni != null) {
			calFecIni.setTime(DateTools.add(fecIni, Calendar.MILLISECOND, durationIni.intValue()));
		}
		if (durationFin != null) {
			calFecFin.setTime(DateTools.add(fecFin, Calendar.MILLISECOND, durationFin.intValue()));
		}
		return new Pair<>(calFecIni.getTime(), calFecFin.getTime());
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		f.setFormTitle("informeleymacron");
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.filterMinIni.setVisible(true);
		this.filterMinIni.setEnabled(true);
		this.filterMinIni.setRequired(true);
		this.filterFecIni.getLabelComponent().setVisible(false);
		this.filterMinFin.setVisible(true);
		this.filterMinFin.setEnabled(true);
		this.filterMinFin.setRequired(true);
		this.filterFecFin.getLabelComponent().setVisible(false);
		this.filterMinIni.setValue("00:00");
		this.filterMinFin.setValue("23:59");
	}

	@Override
	protected Map<String, Object> getDateFilterValues(List<String> keys) {
		SearchValue vb = null;
		try {
			if ((keys == null) || keys.isEmpty()) {
				return new Hashtable<String, Object>();
			}
			Date selectedfecini = (Date) this.managedForm.getDataFieldValue(OpentachFieldNames.FILTERFECINI);
			Date selectedfecfin = (Date) this.managedForm.getDataFieldValue(OpentachFieldNames.FILTERFECFIN);
			if (selectedfecfin != null) {
				Long duration = (Long) this.managedForm.getDataFieldValue("FILTERMINFIN");
				if (duration != null) {
					selectedfecfin = DateTools.add(selectedfecfin, Calendar.MILLISECOND, (int) duration.longValue());
				} else {
					selectedfecfin = new Timestamp(DateUtil.truncToEnd(selectedfecfin).getTime());
				}
			}
			if (selectedfecini != null) {
				Long duration = (Long) this.managedForm.getDataFieldValue("FILTERMININI");
				if (duration != null) {
					selectedfecini = DateTools.add(selectedfecini, Calendar.MILLISECOND, (int) duration.longValue());
				}
			}
			if ((selectedfecfin != null) && (selectedfecini != null)) {
				if (keys.size() == 1) {
					Vector<Object> v = new Vector<Object>(2);
					v.add(selectedfecini);
					v.add(selectedfecfin);
					vb = new SearchValue(SearchValue.BETWEEN, v);
				}
				// Dos columnas y dos fechas especificando el ragno
				// Se tiene que construir una expresción que permita
				// retornar
				if (keys.size() == 2) {
					BasicExpression vbe = this.buildExpression(keys.get(0), keys.get(1), selectedfecini, selectedfecfin);
					Map<String, Object> rtn = new Hashtable<String, Object>();
					rtn.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, vbe);
					return rtn;
				}
			} else if (selectedfecfin != null) { // only less condition.
				vb = new SearchValue(SearchValue.LESS_EQUAL, selectedfecfin);
			} else if (selectedfecini != null) { // only less condition.
				vb = new SearchValue(SearchValue.MORE_EQUAL, selectedfecini);
			}
			if (vb != null) {
				Map<String, Object> rtn = new Hashtable<String, Object>();
				for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
					String key = iter.next();
					rtn.put(key, vb);
				}
				return rtn;
			}
		} catch (Exception e) {
			IMInformeActivCondMacron.logger.error(null, e);
		}
		return null;
	}

}
