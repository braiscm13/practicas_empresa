package com.opentach.client.modules;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.db.SQLStatementBuilder;
import com.ontimize.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.db.SQLStatementBuilder.BasicField;
import com.ontimize.db.SQLStatementBuilder.BasicOperator;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.DataNavigationEvent;
import com.ontimize.gui.Form;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.field.Chart;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.gui.field.HourDateDataField;
import com.ontimize.gui.field.RadioButtonDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.MessageType;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.locator.ReferenceLocator;
import com.opentach.client.comp.ExtendedApplicationManager;
import com.opentach.client.comp.GraficaExtColor;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.util.DateUtil;
import com.utilmize.client.fim.FIMUtils;
import com.utilmize.client.gui.field.reference.UReferenceDataField;

/**
 * Gestor de interacion padre para todos los formularios que generan informes. Permite asociar por cada informe una entidad y una tabla dentro del
 * formulario para la consulta de datos en el propio formulario antes de la generación del informe.
 *
 * @author Pablo Dorgambide Avilés
 * @version 1.0
 */

public class IMDataRoot extends IMRoot {

	private static final Logger			logger			= LoggerFactory.getLogger(IMDataRoot.class);
	public static final String			ULTIMOS_15_DIAS	= "ULTIMOS_15_DIAS";
	protected static final String		ULTIMA_SEMANA	= "ULTIMA_SEMANA";
	public static final String			ULTIMOS_DIAS	= "ULTIMOS_DIAS";

	protected static final String		INC_DAYS		= "INC_DIAS";
	protected static final String		DEC_DAYS		= "DEC_DIAS";
	protected static final String		NUM_DIAS		= "NUM_DIAS";
	public static final String			RANGO_FECHA		= "RANGO_FECHA";


	protected List<TimeStampDateTags>	datetags;
	boolean								flagRefreshForm	= true;

	protected String					chartEntity;
	protected String					dateEntity;
	protected List<String>				keys;

	protected Map<String, String>		tbl_report_LUT;
	protected Map<String, JDialog>		tbl_reportdlg;
	protected Map<String, Form>			tbl_reportform;

	protected boolean					updatedata		= false;
	protected RadioButtonDataField		crbUltimosDias	= null;
	protected RadioButtonDataField		crbRangoFechas	= null;

	// Claves padre de las tablas.
	public void setTableParentKeys(List<String> keys) {
		this.keys = keys;
	}

	// Etiquetas de la fecha de inicio de las tablas.
	public void addDateTags(TimeStampDateTags tag) {
		if (this.datetags == null) {
			this.datetags = new ArrayList<TimeStampDateTags>();
		}
		this.datetags.add(tag);
	}

	// Etiquetas de la fecha de inicio de las tablas.
	public void setDateTags(TimeStampDateTags tag) {
		if (this.datetags == null) {
			this.datetags = new ArrayList<TimeStampDateTags>();
		}
		this.datetags.clear();
		this.datetags.add(tag);

	}

	public List<TimeStampDateTags> getDateTags() {
		return this.datetags;
	}

	/**
	 * Nombres de las columnas que se utilizarán para filtrar por rango de fechas. Las fechas de los filtros se utiilzaran para contruir el SQL para
	 * cada una de los TimeStapDateTags. Si el elemento consta de las dos columans se contruira un BasicExpresion para trare los datos --- CONTENIDOS
	 * O QUE CAIGAN --- dentro del rango de fechas.
	 *
	 * @author Pablo Dorgambide
	 * @company www.imatia.com
	 * @email pdorgambide@imatia.com
	 * @date: 19/12/2008
	 */
	public class TimeStampDateTags {
		public String	dateinitag, datefintag;

		public TimeStampDateTags(String dateini) {
			this(dateini, null);
		}

		public TimeStampDateTags(String dateini, String datefin) {
			this.dateinitag = dateini;
			this.datefintag = datefin;
		}
	}

	/**
	 * Establece la relación entre el tab visible y el formulario detalle que se abrirá para mostrar el informe.
	 *
	 * @param val
	 */
	public void setTableReportMap(Map<String, String> val) {
		this.tbl_report_LUT = val;
		if (val != null) {
			this.tbl_reportdlg = new HashMap<String, JDialog>();
			this.tbl_reportform = new HashMap<String, Form>();
		} else {
			this.tbl_reportdlg = null;
			this.tbl_reportform = null;
		}
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		f.setModifiable(OpentachFieldNames.FILTERFECINI, false);
		f.setModifiable(OpentachFieldNames.FILTERFECFIN, false);
		f.setModifiable(IMDataRoot.NUM_DIAS, false);
		this.crbUltimosDias = (RadioButtonDataField) f.getDataFieldReference(IMDataRoot.ULTIMOS_DIAS);
		this.crbRangoFechas = (RadioButtonDataField) f.getDataFieldReference(IMDataRoot.RANGO_FECHA);

		Button bRefresh = f.getButton("btnRefrescar");
		if (bRefresh != null) {
			bRefresh.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						IMDataRoot.this.doOnQuery(true);
					} catch (Exception ex) {
						MessageManager.getMessageManager().showExceptionMessage(ex, IMDataRoot.logger);
					}
				}
			});
		}

		ActionListener radiobl = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Number nofdays = (Number) IMDataRoot.this.managedForm.getDataFieldValue(IMDataRoot.NUM_DIAS);
					if (((Button) e.getSource()).getAttribute().equals(IMDataRoot.ULTIMA_SEMANA)) {
						nofdays = Integer.valueOf(7);
					} else if (((Button) e.getSource()).getAttribute().equals(IMDataRoot.ULTIMOS_15_DIAS)) {
						nofdays = Integer.valueOf(28);
					}
					Date dend = new Date();
					Date dini = DateUtil.addDays(dend, -nofdays.intValue());
					IMDataRoot.this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECINI, dini);
					IMDataRoot.this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECFIN, dend);
					IMDataRoot.this.doOnQuery();
				} catch (Exception ex) {
					MessageManager.getMessageManager().showExceptionMessage(ex, IMDataRoot.logger);
				}
			}
		};
		Button ultimasemana = this.managedForm.getButton(IMDataRoot.ULTIMOS_15_DIAS);
		if (ultimasemana != null) {
			ultimasemana.addActionListener(radiobl);
		}
		ultimasemana = this.managedForm.getButton(IMDataRoot.ULTIMA_SEMANA);
		if (ultimasemana != null) {
			ultimasemana.addActionListener(radiobl);
		}
		ActionListener incdeclist = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Button rb = (Button) e.getSource();
					Date ini = (Date) IMDataRoot.this.managedForm.getDataFieldValue(OpentachFieldNames.FILTERFECINI);
					Date fin = (Date) IMDataRoot.this.managedForm.getDataFieldValue(OpentachFieldNames.FILTERFECFIN);
					Number nofdays = (Number) IMDataRoot.this.managedForm.getDataFieldValue(IMDataRoot.NUM_DIAS);
					if (nofdays != null) {
						if ((ini != null) && (fin != null)) {
							String attr = (String) rb.getAttribute();
							if (attr.indexOf("DEC") != -1) {
								Date dstart = DateUtil.addDays(ini, -nofdays.intValue());
								IMDataRoot.this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECINI, dstart);
								IMDataRoot.this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECFIN, ini);
							} else {
								Date dstart = DateUtil.addDays(fin, nofdays.intValue());
								IMDataRoot.this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECINI, fin);
								IMDataRoot.this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECFIN, dstart);
							}
							IMDataRoot.this.doOnQuery();
						}
					}
				} catch (Exception ex) {
					MessageManager.getMessageManager().showExceptionMessage(ex, IMDataRoot.logger);
				}
			}
		};

		String[] btnl = { IMDataRoot.INC_DAYS, IMDataRoot.DEC_DAYS };
		for (int i = 0; (btnl != null) && (i < btnl.length); i++) {
			Button btns = this.managedForm.getButton(btnl[i]);
			if (btns != null) {
				btns.addActionListener(incdeclist);
			}
		}

		try {
			((DataField) this.managedForm.getDataFieldReference(IMDataRoot.NUM_DIAS)).addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChanged(ValueEvent e) {
					if (((e.getNewValue() != null) && !e.getNewValue().equals(e.getOldValue())) || ((e.getNewValue() == null) && (e.getOldValue() != null))) {
						IMDataRoot.this.managedForm.enableButton(IMDataRoot.INC_DAYS);
						IMDataRoot.this.managedForm.enableButton(IMDataRoot.DEC_DAYS);
					}
				}
			});
		} catch (NullPointerException nex) {
		}
		DateDataField cmpFecIni = (DateDataField) f.getDataFieldReference(OpentachFieldNames.FILTERFECINI);
		DateDataField cmpFecFin = (DateDataField) f.getDataFieldReference(OpentachFieldNames.FILTERFECFIN);
		ValueChangeListener feclist = new ValueChangeListener() {
			@Override
			public void valueChanged(ValueEvent e) {
				try {
					if (e.getType() == ValueEvent.USER_CHANGE) {
						if (((e.getNewValue() != null) && !e.getNewValue().equals(e.getOldValue())) || ((e.getNewValue() == null) && (e.getOldValue() != null))) {
							if (IMDataRoot.this.flagRefreshForm) {
								IMDataRoot.this.doOnQuery();
							}
						}
					}
				} catch (Exception ex) {
					MessageManager.getMessageManager().showExceptionMessage(ex, IMDataRoot.logger);
				}
			}
		};
		if (cmpFecIni != null) {
			cmpFecIni.addValueChangeListener(feclist);
		}
		if (cmpFecFin != null) {
			cmpFecFin.addValueChangeListener(feclist);
		}
		// RAFA
		if (this.cgContract != null) {
			((DataField) this.cgContract).addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChanged(ValueEvent e) {
					try {
						if (e.getType() == ValueEvent.USER_CHANGE) {
							IMDataRoot.this.doOnQuery();
						}
					} catch (Exception ex) {
						MessageManager.getMessageManager().showExceptionMessage(ex, IMDataRoot.logger);
					}
				}
			});
		}



		// No considerar las modificaciones en el filtro de fecha como modif.
		// del registro
		this.setValueChangedEventListener(OpentachFieldNames.FILTERFECINI, false);
		f.setCheckModifiedDataField(OpentachFieldNames.FILTERFECINI, false);
		this.setValueChangedEventListener(OpentachFieldNames.FILTERFECFIN, false);
		f.setCheckModifiedDataField(OpentachFieldNames.FILTERFECFIN, false);
		this.setValueChangedEventListener(IMDataRoot.NUM_DIAS, false);
		f.setCheckModifiedDataField(IMDataRoot.NUM_DIAS, false);
		this.setValueChangedEventListener(IMDataRoot.ULTIMOS_DIAS, false);
		f.setCheckModifiedDataField(IMDataRoot.ULTIMOS_DIAS, false);
		this.setValueChangedEventListener(IMDataRoot.RANGO_FECHA, false);
		f.setCheckModifiedDataField(IMDataRoot.RANGO_FECHA, false);
		// TODO: not null
		// add a listener to if the user changed the selected radio, the dates
		// will be recalculated
		final RadioButtonDataField grb = (RadioButtonDataField) f.getDataFieldReference(IMDataRoot.ULTIMOS_DIAS);
		if (grb != null) {
			grb.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChanged(ValueEvent e) {
					if ((grb != null) && grb.isSelected() && (e.getType() == ValueEvent.USER_CHANGE)) {
						try {
							IMDataRoot.this.doOnQuery();
						} catch (Exception ex) {
							MessageManager.getMessageManager().showExceptionMessage(ex, IMDataRoot.logger);
						}
					}
				}
			});
		}
		// add a listener to if the user changed the selected radio, the dates
		// will be recalculated
		final RadioButtonDataField grb2 = (RadioButtonDataField) this.managedForm.getDataFieldReference("RANGO_FECHA");
		if (grb2 != null) {
			grb2.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChanged(ValueEvent e) {
					if ((grb2 != null) && grb2.isSelected() && (e.getType() == ValueEvent.USER_CHANGE)) {
						IMDataRoot.this.managedForm.deleteDataField(OpentachFieldNames.FILTERFECINI);
						IMDataRoot.this.managedForm.deleteDataField(OpentachFieldNames.FILTERFECFIN);
						IMDataRoot.this.managedForm.enableDataField(OpentachFieldNames.FILTERFECINI);
						IMDataRoot.this.managedForm.enableDataField(OpentachFieldNames.FILTERFECFIN);
					}
				}
			});
		}
	}



	@Override
	public void setQueryMode() {
		super.setQueryMode();
		this.managedForm.enableDataField(OpentachFieldNames.FILTERFECINI);
		this.managedForm.enableDataField(OpentachFieldNames.FILTERFECFIN);
	}

	/**
	 * Construye el cv que deben enviarse en función de las fechas. <ul> <li>Si existe un columna como clave (FECNINI) se enviará <ul> <li>Si esta
	 * establecida unicamente la fecha inicio: FECINI > fechainiciofiltro <li>Si esta establecida unicamente la fecha fin: FECINI < fechafinfiltro
	 * <li>Si estan establecida ambas fechas del filtro: fechainiciofiltro < FECINI < fechafinfiltro </ul> <li>Si se reciben dos columnas como claves
	 * (FECINI-FECFIN) <ul> <li>Si esta establecida unicamente la fecha inicio: FECINI > fechainiciofiltro, FECFIN>fechainicio <li>Si esta establecida
	 * unicamente la fecha fin: FECINI < fechainiciofiltro && FECFIN < fechainicio <li>Si esta establecida ambas fechas : Un expresión key con la
	 * expresión que permita recuprar todos los registros que caigan dentro del rango (contenidos totalmente o que empiecen o finalicen dentro del
	 * rango) </ul> </ul>
	 *
	 * @param keys
	 * @return
	 */
	protected Map<String, Object> getDateFilterValues(List<String> keys) {
		SearchValue vb = null;
		try {
			if ((keys == null) || keys.isEmpty()) {
				return new Hashtable<String, Object>();
			}
			DateDataField cf = (DateDataField) this.managedForm.getDataFieldReference(OpentachFieldNames.FILTERFECINI);
			if (cf != null) {
				Date selectedfecini = (Date) cf.getDateValue();
				cf = (DateDataField) this.managedForm.getDataFieldReference(OpentachFieldNames.FILTERFECFIN);
				Date selectedfecfin = (Date) cf.getDateValue();
				if (!(this.managedForm.getDataFieldReference(OpentachFieldNames.FILTERFECFIN) instanceof HourDateDataField)) {
					selectedfecfin = new Timestamp(DateUtil.truncToEnd(selectedfecfin).getTime());
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
			}
		} catch (Exception e) {
			IMDataRoot.logger.error(null, e);
		}
		return null;
	}

	protected BasicExpression buildExpression(String colFecIni, String colFecFin, Date initDate, Date endDate) {
		BasicExpression exprtn = null;
		Timestamp initTime = new Timestamp(initDate.getTime());
		Timestamp endTime = new Timestamp(endDate.getTime());

		BasicField bfID = new BasicField(colFecIni);
		BasicField bfED = new BasicField(colFecFin);

		BasicOperator boLE = (BasicOperator) BasicOperator.LESS_EQUAL_OP;
		BasicOperator boL = (BasicOperator) BasicOperator.LESS_OP;
		BasicOperator boME = (BasicOperator) BasicOperator.MORE_EQUAL_OP;
		BasicOperator boM = (BasicOperator) BasicOperator.MORE_OP;
		BasicOperator boAND = (BasicOperator) BasicOperator.AND_OP;
		BasicOperator boOR = (BasicOperator) BasicOperator.OR_OP;

		// ______InitTime ----------------------------- EndTime
		// (A) Fechas de inicio superiores a InitTime e inferiroes a EndTime.
		// ___________________bfID --------- bfEd
		// _________________________________bfID ------------------ bfEd
		// (B) Fecha fin caiga dentro del rango.
		// bfID --------- bfEd
		// (C) Fecha Inicio antes de InitTIme y Fecha fin posterior
		// bfID ----------------------------------------------------------------
		// bfEd
		// CONDICION TOTAL: (D)=(A) OR (B) OR (C)
		BasicExpression exprA3 = null;
		BasicExpression exprB3 = null;
		if (colFecIni != null) {
			BasicExpression exprA1 = new BasicExpression(bfID, boME, initTime);
			BasicExpression exprA2 = new BasicExpression(bfID, boLE, endTime);
			exprA3 = new BasicExpression(exprA1, boAND, exprA2);
			exprtn = exprA3;
		}
		if (colFecFin != null) {
			BasicExpression exprB1 = new BasicExpression(bfED, boME, initTime);
			BasicExpression exprB2 = new BasicExpression(bfED, boLE, endTime);
			exprB3 = new BasicExpression(exprB1, boAND, exprB2);
			exprtn = exprB3;
		}
		if ((colFecIni != null) && (colFecFin != null)) {
			BasicExpression exprC1 = new BasicExpression(bfID, boL, initTime);
			BasicExpression exprC2 = new BasicExpression(bfED, boM, endTime);
			BasicExpression exprC3 = new BasicExpression(exprC1, boAND, exprC2);
			BasicExpression exprD = new BasicExpression(exprA3, boOR, exprB3);
			BasicExpression exprE = new BasicExpression(exprD, boOR, exprC3);
			exprtn = exprE;
		}
		return exprtn;
	}

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
				} else {
					MessageManager.getMessageManager().showMessage(this.managedForm, res.getMessage(),
							res.getCode() == EntityResult.OPERATION_WRONG ? MessageType.ERROR : MessageType.WARNING, res.getMessageParameter(), false);
				}
			}
		} catch (Exception e) {
			IMDataRoot.logger.error(null, e);
		}
	}

	protected boolean comprobacionFechasFiltro(boolean showalert) {
		DateDataField fldini = (DateDataField) this.managedForm.getDataFieldReference(OpentachFieldNames.FILTERFECINI);
		DateDataField fldfin = (DateDataField) this.managedForm.getDataFieldReference(OpentachFieldNames.FILTERFECFIN);
		if ((fldini != null) && (fldfin != null)) {
			Date ini = (Date) fldini.getDateValue();
			Date fin = (Date) fldfin.getDateValue();
			if ((ini == null) || (fin == null)) {
				if (showalert) {
					this.managedForm.message(ApplicationManager.getTranslation("M_INSERTE_FECHAS_FILTRO_BUSQUEDA"), Form.WARNING_MESSAGE);
				}
				return false;
			} else if (ini.after(fin)) {
				if (showalert) {
					this.managedForm.message(ApplicationManager.getTranslation("M_FECHAS_FILTRO_BUSQUEDA_INCORRECTAS"), Form.WARNING_MESSAGE);
				}
				return false;
			}
		}
		return true;
	}

	protected Hashtable<String, Object> getFilterValues() {
		return new Hashtable<String, Object>();
	}

	@Override
	protected void refreshTables(Vector<Table> toRefreshTables) throws Exception {
		if (toRefreshTables.size() > 0) {
			this.doOnQuery();
		}
	}

	public void refreshTables() throws Exception {
		if (this.comprobacionFechasFiltro(false)) {
			Hashtable<String, Object> cvfiltro = this.getFilterValues();
			List<Table> tbs = FIMUtils.getTables(this.managedForm);
			if (tbs.isEmpty()) {
				return;
			}
			if (this.datetags != null) {
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
				tbs.remove(tblvisible);
				tbs.add(0, tblvisible);
			}
			for (Iterator<Table> iter = tbs.iterator(); iter.hasNext();) {
				Table tb = iter.next();
				Vector<Object> keys = tb.getParentKeys();
				Hashtable<String, Object> cv = new Hashtable<String, Object>();
				if (cvfiltro != null) {
					// añado al claves valores los parent keys de la
					// tabla
					cv.putAll(cvfiltro);
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
				if (this.managedForm.getDataFieldReference("NUM_TARJ") != null) {
					if (this.managedForm.getDataFieldValue("NUM_TARJ") != null) {
						String numTarj = (String) this.managedForm.getDataFieldValue(OpentachFieldNames.NUMTARJETA_FIELD);
						if (numTarj != null) {
							Map<String, Object> rcd = ((UReferenceDataField) this.managedForm
									.getDataFieldReference(OpentachFieldNames.NUMTARJETA_FIELD)).getValuesToCode(numTarj);
							numTarj += (String) rcd.get("INDICE_CONS") + (String) rcd.get("INDICE_RENOV") + (String) rcd.get("INDICE_SUST");
							cv.put("NUM_TARJ", numTarj);
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
							Hashtable<Object, Object> datosFila = tb.getRowData(fila);
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
			}
		}
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.hideTreeNode();
		this.managedForm.enableButton("bdownload");
	}

	protected void doOnUpdateMode() {
		this.managedForm.enableButton("bdownload");
		this.managedForm.enableButton("btnRefrescar");
		this.managedForm.enableButton("btnInforme");
		this.managedForm.enableButton(IMDataRoot.ULTIMA_SEMANA);
		this.managedForm.enableButton("PRIMERA_SEMANA");
		this.managedForm.enableButton(IMDataRoot.ULTIMOS_15_DIAS);
		if (this.managedForm.getDataFieldValue(IMDataRoot.NUM_DIAS) != null) {
			this.managedForm.enableButton(IMDataRoot.INC_DAYS);
			this.managedForm.enableButton(IMDataRoot.DEC_DAYS);
		}
		this.managedForm.enableDataField(IMDataRoot.NUM_DIAS);
		this.managedForm.enableDataField(OpentachFieldNames.FILTERFECINI);
		this.managedForm.enableDataField(OpentachFieldNames.FILTERFECFIN);
		this.managedForm.setRequired(OpentachFieldNames.FILTERFECINI, true);
		this.managedForm.setRequired(OpentachFieldNames.FILTERFECFIN, true);
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		this.doOnUpdateMode();
		this.managedForm.enableButton("bdownload");
		if ((this.crbUltimosDias != null) && this.crbUltimosDias.isSelected()) {
			this.managedForm.disableDataField(OpentachFieldNames.FILTERFECINI);
			this.managedForm.disableDataField(OpentachFieldNames.FILTERFECFIN);
		}
	}

	@Override
	public void setInsertMode() {
		super.setInsertMode();
		this.managedForm.disableDataField(OpentachFieldNames.FILTERFECINI);
		this.managedForm.disableDataField(OpentachFieldNames.FILTERFECFIN);
		this.managedForm.setRequired(OpentachFieldNames.FILTERFECINI, false);
		this.managedForm.setRequired(OpentachFieldNames.FILTERFECFIN, false);
		this.managedForm.enableButton("bdownload");
	}

	@Override
	public void dataChanged(DataNavigationEvent e) {
		try {
			// Desactivo para que los campos fecha no lancen multiples consultas al
			// cambiar de dato.
			this.flagRefreshForm = false;
			super.dataChanged(e);
			this.doOnQuery();
			// Vuelvo a activar el refresco del formulario
			this.flagRefreshForm = true;
		} catch (Exception ex) {
			MessageManager.getMessageManager().showExceptionMessage(ex, IMDataRoot.logger);
		}
	}

	public void refreshChart() {
		if (this.chartEntity == null) {
			return;
		}
		Table tbl = (Table) this.managedForm.getDataFieldReference(this.chartEntity);
		if (tbl != null) {
			Hashtable<String, Object> cv = (Hashtable<String, Object>) tbl.getValue();
			Vector<Object> v = this.managedForm.getDataComponents();
			for (Iterator<Object> iter = v.iterator(); iter.hasNext();) {
				Object item = iter.next();
				if (item instanceof Chart) {
					((Chart) item).setValue(cv);
				} else if (item instanceof GraficaExtColor) {
					((GraficaExtColor) item).setValue(cv);
				}
			}
		}
	}

	public JDialog getFormDialog(Form f, boolean pack) {
		JDialog dlg = f.putInModalDialog();
		if (dlg instanceof EJDialog) {
			((EJDialog) dlg).setAutoPackOnOpen(pack);
		}
		f.deleteDataFields();
		dlg.validate();
		dlg.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		ApplicationManager.center(dlg);
		return dlg;
	}

	public String getChartEntity() {
		return this.chartEntity;
	}

	public void setChartEntity(String chartEntity) {
		this.chartEntity = chartEntity;
	}

	@SuppressWarnings("unchecked")
	protected void ultimosDatos() {
		if ((this.crbUltimosDias != null) && this.crbUltimosDias.isSelected()) {
			this.managedForm.disableDataField(OpentachFieldNames.FILTERFECINI);
			this.managedForm.disableDataField(OpentachFieldNames.FILTERFECFIN);
			try {
				Hashtable av = new Hashtable();
				DataField idCond = (DataField) this.managedForm.getDataFieldReference(OpentachFieldNames.IDCONDUCTOR_FIELD);
				String fieldname = OpentachFieldNames.IDCONDUCTOR_FIELD;
				if (idCond == null) {
					fieldname = OpentachFieldNames.MATRICULA_FIELD;
					idCond = (DataField) this.managedForm.getDataFieldReference(fieldname);
				}
				if (idCond == null) {
					fieldname = OpentachFieldNames.IDORIGEN_FIELD;
					idCond = (DataField) this.managedForm.getDataFieldReference(fieldname);
				}
				Object idConfFilter = idCond == null ? null : idCond.getValue();
				if ((idConfFilter != null) && (idConfFilter instanceof SearchValue)) {
					List options = (List) ((SearchValue) idConfFilter).getValue();
					if (options.size() == 1) {
						idConfFilter = options.get(0);
					}
				}
				if (idConfFilter != null) {
					av.put(fieldname, idConfFilter);
				}
				UReferenceDataField CIF = (UReferenceDataField) this.managedForm.getDataFieldReference("CIF");
				if ((this.cgContract==null) || (this.cgContract.getValue() == null) || (idConfFilter == null) || (CIF.getValue() == null)) {
					return;
				}
				if ((this.dateEntity != null) && (idConfFilter instanceof String)) {
					Entity ufecha = this.formManager.getReferenceLocator().getEntityReference(this.dateEntity);
					int session = this.formManager.getReferenceLocator().getSessionId();
					av.put(OpentachFieldNames.CIF_FIELD, CIF.getValue());
					av.put(OpentachFieldNames.NUMREQ_FIELD, this.cgContract.getValue());
					Vector v = new Vector(0);
					Date end = null;
					try {
						EntityResult res = ufecha.query(av, v, session);
						if (res != null) {
							Hashtable a = res.getRecordValues(0);
							end = (Date) a.get(OpentachFieldNames.FECFIN_FIELD);
						}
					} catch (Exception e) {
						IMDataRoot.logger.error(null, e);
					}
					if (end == null) {
						end = new Date();
					}
					end = DateUtil.truncToEnd(end);
					int nofdays = 28;
					if (end != null) {
						Date ini = DateUtil.addDays(end, -nofdays);
						this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECINI, ini);
						this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECFIN, end);
					}
				}
				// Si no existe entidad para recupearr la fecha del ultimo dato
				// establezco los ultimos 28
				// dias.
				else {
					Calendar c = Calendar.getInstance();
					Number nofdays = (Number) this.managedForm.getDataFieldValue(IMDataRoot.NUM_DIAS);
					if (nofdays == null) {
						nofdays = Integer.valueOf(28);
					}
					Date d = new Date();
					c.setTime(d);
					c.add(Calendar.DATE, -nofdays.intValue());
					this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECINI, c.getTime());
					this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECFIN, d);
				}
			} catch (Exception e) {
				IMDataRoot.logger.error(null, e);
			}
		}
		if ((this.crbRangoFechas != null) && this.crbRangoFechas.isSelected()) {
			this.managedForm.enableDataField(OpentachFieldNames.FILTERFECINI);
			this.managedForm.enableDataField(OpentachFieldNames.FILTERFECFIN);
		}
	}

	public void doOnQuery() throws Exception {
		this.doOnQuery(false);
	}

	public void doOnQuery(final boolean alert) {

		OperationThread infoInforme = new OperationThread(ApplicationManager.getTranslation("Generando_informe")) {

			@Override
			public void run() {
				try {
					this.hasStarted = true;

					IMDataRoot.this.ultimosDatos();
					if (IMDataRoot.this.checkRequiredVisibleDataFields(alert)) {
						if ((IMDataRoot.this.managedForm.getDataFieldReference(OpentachFieldNames.FILTERFECINI) != null) && (IMDataRoot.this.managedForm.getDataFieldReference(OpentachFieldNames.FILTERFECFIN)!= null)) {
							Date dini = (Date) IMDataRoot.this.managedForm.getDataFieldValue(OpentachFieldNames.FILTERFECINI);
							Date dfin = (Date) IMDataRoot.this.managedForm.getDataFieldValue(OpentachFieldNames.FILTERFECFIN);
							if ((dini == null) || (dfin == null) || dini.after(dfin) || dini.before(IMRoot.BEGININGDATE)) {
								if ((dini != null) && (dfin != null) && dini.after(dfin)) {
									IMDataRoot.this.managedForm.message("M_ERROR_DATE_RANGE", Form.WARNING_MESSAGE);
								}
								return;
							}
						}
						IMDataRoot.this.refreshTables();
						IMDataRoot.this.refreshChart();
					}
					this.hasFinished = true;
				} catch (Exception ex) {
					MessageManager.getMessageManager().showExceptionMessage(ex, IMDataRoot.logger);
				}
			}
		};

		Window windowAncestor = SwingUtilities.getWindowAncestor(this.managedForm);
		if (windowAncestor instanceof Frame) {
			ExtendedApplicationManager.proccessOperation((Frame) windowAncestor, infoInforme, 50);
		} else {
			ExtendedApplicationManager.proccessOperation((Dialog) windowAncestor, infoInforme, 50);
		}

	}

	public void setDateEntity(String dateEntity) {
		this.dateEntity = dateEntity;
	}
}
