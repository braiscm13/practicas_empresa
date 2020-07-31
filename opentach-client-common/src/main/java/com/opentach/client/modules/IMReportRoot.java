package com.opentach.client.modules;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Types;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.container.DataComponentGroup;
import com.ontimize.gui.field.CheckDataField;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.gui.field.RadioButtonDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.AbstractOpentachClientLocator;
import com.opentach.client.comp.ExtendedApplicationManager;
import com.opentach.client.comp.action.AbstractAutoSelectDriverVehicleValueChangeListener;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.i18n.ITranslationService;
import com.opentach.common.report.IReportService;
import com.opentach.common.report.util.JRPropertyManager;
import com.opentach.common.report.util.JRReportDescriptor;
import com.opentach.common.util.DateUtil;
import com.opentach.common.util.ResourceManager;
import com.utilmize.client.fim.FIMUtils;
import com.utilmize.client.gui.field.UDurationMaskDataField;
import com.utilmize.client.gui.field.table.UExpansibleTable;
import com.utilmize.client.report.JRDialogViewer;
import com.utilmize.tools.report.JRReportUtil;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * Gestor de interacion padre para todos los formularios que generan informes. Permite asociar por cada informe una entidad y una tabla dentro del formulario para la consulta de
 * datos en el propio formulario antes de la generación del informe. Es posible configurarlo para que los valores dle informe se establezcan por codigo y no se necesrio mostrar la
 * tabla.
 *
 * @author Pablo Dorgambide Avilés
 * @version 1.0
 */
public class IMReportRoot extends IMDataRoot {

	private static final Logger		logger			= LoggerFactory.getLogger(IMReportRoot.class);

	protected String				tablename;
	protected HashMap				informeEntidad;
	protected String				nombInforme;
	protected String				filtergroupname	= "FILTER";
	protected String				bandcfgname		= "BANDCFG";
	private final OperationThread	infoInforme		= null;
	protected String				entidadtbl		= null;

	public static final String		BASEREPORTPATH	= "com/opentach/client/xml/reports/";
	protected static final String	ULTIMOS_DIAS	= "ULTIMOS_DIAS";
	protected static final String	ULTIMA_SEMANA	= "ULTIMA_SEMANA";
	protected static final String	NUM_DIAS		= "NUM_DIAS";

	public IMReportRoot() {
		super();
	}

	public IMReportRoot(HashMap informeEntidad, String nombInforme, String tabla) {
		this();
		this.informeEntidad = informeEntidad;
		this.nombInforme = nombInforme;
		this.tablename = tabla;
		this.entidadtbl = tabla;
		// Inserto la tabla a la cadena de sequencia como el ultimo eslabon.
		this.fieldsChain.add(tabla);
	}

	public IMReportRoot(String entidad, String pathInforme) {
		this(new HashMap(), pathInforme, entidad);
	}

	public void setEntidadesInformes(HashMap informeEntidad) {
		this.informeEntidad = informeEntidad;
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		Button btn = f.getButton("btnInforme");
		if (btn != null) {
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					IMReportRoot.this.doOnBuildReport();
				}
			});
		}
		f.setModifiable(OpentachFieldNames.FILTERFECINI, false);
		f.setModifiable(OpentachFieldNames.FILTERFECFIN, false);
		List<DataField> filterfields = this.getFilterFields();
		ValueChangeListener filterlist = new ValueChangeListener() {

			@Override
			public void valueChanged(ValueEvent e) {
				try {
					if (e.getType() == ValueEvent.USER_CHANGE) {
						if (IMReportRoot.this.checkRequiredVisibleDataFields(false)) {
							if (e.getSource() instanceof DateDataField) {
								if (((e.getNewValue() != null) && !e.getNewValue().equals(e.getOldValue())) || ((e.getNewValue() == null) && (e.getOldValue() != null))) {
									if (IMReportRoot.this.checkRequiredVisibleDataFields(false)) {
										IMReportRoot.this.doOnQuery();
									}
								}
							} else {
								if (IMReportRoot.this.checkRequiredVisibleDataFields(false)) {
									if ((IMReportRoot.this.managedForm.getDataFieldReference(OpentachFieldNames.FILTERFECINI) != null) && (IMReportRoot.this.managedForm
											.getDataFieldReference(OpentachFieldNames.FILTERFECFIN) != null)) {
										Date dini = (Date) IMReportRoot.this.managedForm.getDataFieldValue(OpentachFieldNames.FILTERFECINI);
										Date dfin = (Date) IMReportRoot.this.managedForm.getDataFieldValue(OpentachFieldNames.FILTERFECFIN);
										if ((dini == null) || (dfin == null) || dini.after(dfin) || dini.before(IMRoot.BEGININGDATE)) {
											if ((dini != null) && (dfin != null) && dini.after(dfin)) {
												IMReportRoot.this.managedForm.message("M_ERROR_DATE_RANGE", Form.WARNING_MESSAGE);
											}
											return;
										}
									}
									IMReportRoot.this.refreshTables();
									IMReportRoot.this.refreshChart();
								}
							}
						}
					}

				} catch (

						Exception ex) {
					MessageManager.getMessageManager().showExceptionMessage(ex, IMReportRoot.logger);
				}
			}
		};
		for (DataField fld : filterfields) {
			fld.addValueChangeListener(filterlist);
		}

		if (this.cgContract != null) {
			((DataField) this.cgContract).addValueChangeListener(new AutoSelectDriverVehicleValueChangeListener());
		}
		ActionListener radiobl = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Number nofdays = (Number) IMReportRoot.this.managedForm.getDataFieldValue(IMReportRoot.NUM_DIAS);
					if (((Button) e.getSource()).getAttribute().equals(IMReportRoot.ULTIMA_SEMANA)) {
						nofdays = Integer.valueOf(7);
					} else if (((Button) e.getSource()).getAttribute().equals(IMReportRoot.ULTIMOS_DIAS)) {
						nofdays = Integer.valueOf(28);
					}
					Date dend = new Date();
					Date dini = DateUtil.addDays(dend, -nofdays.intValue());
					IMReportRoot.this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECINI, dini);
					IMReportRoot.this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECFIN, dend);
					IMReportRoot.this.doOnQuery();

				} catch (Exception ex) {
					MessageManager.getMessageManager().showExceptionMessage(ex, IMReportRoot.logger);
				}
			}
		};
		Button ultimasemana = this.managedForm.getButton(IMReportRoot.ULTIMA_SEMANA);
		if (ultimasemana != null) {
			ultimasemana.addActionListener(radiobl);
		}
		// add a listener to if the user changed the selected radio, the dates
		// will be recalculated
		RadioButtonDataField grb = (RadioButtonDataField) this.managedForm.getDataFieldReference(IMReportRoot.ULTIMOS_DIAS);
		if (grb != null) {
			grb.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChanged(ValueEvent e) {
					try {
						RadioButtonDataField grb = (RadioButtonDataField) IMReportRoot.this.managedForm.getDataFieldReference(IMReportRoot.ULTIMOS_DIAS);
						if (grb != null) {
							if (grb.isSelected()) {
								if (e.getType() == ValueEvent.USER_CHANGE) {
									Date dEnd = new Date();
									IMReportRoot.this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECINI, DateUtil.addDays(dEnd, -28));
									IMReportRoot.this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECFIN, dEnd);
									IMReportRoot.this.managedForm.disableDataField(OpentachFieldNames.FILTERFECINI);
									IMReportRoot.this.managedForm.disableDataField(OpentachFieldNames.FILTERFECFIN);
									if ((IMReportRoot.this.managedForm.getDataFieldReference("FILTERMINFIN") != null) && (IMReportRoot.this.managedForm
											.getDataFieldReference("FILTERMINFIN") instanceof UDurationMaskDataField)) {
										((UDurationMaskDataField) IMReportRoot.this.managedForm.getDataFieldReference("FILTERMINFIN")).setValue("23:59");
									}
									IMReportRoot.this.doOnQuery();
								}
							}
						}

					} catch (Exception ex) {
						MessageManager.getMessageManager().showExceptionMessage(ex, IMReportRoot.logger);
					}
				}
			});
		}

		// add a listener to if the user changed the selected radio, the dates
		// will be recalculated
		RadioButtonDataField grb2 = (RadioButtonDataField) this.managedForm.getDataFieldReference("RANGO_FECHA");
		if (grb2 != null) {
			grb2.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChanged(ValueEvent e) {
					RadioButtonDataField grb2 = (RadioButtonDataField) IMReportRoot.this.managedForm.getDataFieldReference("RANGO_FECHA");
					if (grb2 != null) {
						if (grb2.isSelected()) {
							if (e.getType() == ValueEvent.USER_CHANGE) {
								IMReportRoot.this.managedForm.deleteDataField(OpentachFieldNames.FILTERFECINI);
								IMReportRoot.this.managedForm.deleteDataField(OpentachFieldNames.FILTERFECFIN);
								IMReportRoot.this.managedForm.enableDataField(OpentachFieldNames.FILTERFECINI);
								IMReportRoot.this.managedForm.enableDataField(OpentachFieldNames.FILTERFECFIN);
							}
						}
					}
				}
			});
		}
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.managedForm.enableButtons();
		for (int i = 0; i < 6; i++) {
			DataField c = (DataField) this.managedForm.getDataFieldReference("ENABLE." + i);
			this.setFilterEnabled(i, (c == null) ? true : false);
		}

		String[] defchecked = { "VAL.1a", "VAL.1b", "VAL.1c", "VAL.1d", "VAL.1e", "VAL.1f", "VAL.1g", "VAL.1h" };
		for (int i = 0; i < defchecked.length; i++) {
			DataField c = (DataField) this.managedForm.getDataFieldReference(defchecked[i]);
			if ((c != null) && (c instanceof CheckDataField)) {
				CheckDataField check = (CheckDataField) c;
				check.setValue(Integer.valueOf(1));
			}
		}
		// Por defecto el filtro de fechas debe aparecer los últimos 28 días
		Date d = new Date();
		this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECINI, DateUtil.addDays(d, -28));
		this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECFIN, d);
		// Activo los campos de configuración de visibilidad de Bandas.
		Map<String, Object> bands = this.getBandVisibility();
		for (String key : bands.keySet()) {
			this.managedForm.enableDataField(key);
		}
		this.managedForm.disableDataField(OpentachFieldNames.FILTERFECINI);
		this.managedForm.disableDataField(OpentachFieldNames.FILTERFECFIN);
	}

	/**
	 * <br> Recorre todos los grupos de componentes de datos con estructura de nombre 'filtergroupname.idx' y retorna el hash con los valores busqueda que permiten filtrar la
	 * consulta SQL. Dentro del grupo de componentes debe existir <ul> <li>Un componente con el id de la columna sobre la que se filtrará attr='COLUMN.indice' <li>Un componente con
	 * el valor mínimo attr='MINVAL.indice' <li>Un componente con el valor máximo attr='MINVAL.indice' <li>Un componente con el valor exacto attr='VAL.indice' </ul> Si se desea
	 * fijar alguno de los valores debe ir seguido su attr de = y el valor fijado. Así por ejemplo un filtro que actua siempre sobre la columna HORA tendrá como attr en su grupo o
	 * componente COLUMN.1=HORA
	 *
	 * @return Hashtable con clave la columna y valor el ValorBusqueda determinado por el filtro.
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected Hashtable getFilterValues() {
		Object key, minval, maxval, val;
		Hashtable rtn = new Hashtable();
		int i = 1;
		DataComponentGroup filtergroup;
		while ((filtergroup = (DataComponentGroup) this.managedForm.getElementReference(this.filtergroupname + "." + i)) != null) {
			Vector vals = new Vector();
			Boolean bperiodos = false;
			Vector attrs = filtergroup.getAttributes();
			boolean enable = true;
			key = minval = maxval = val = null;
			for (Enumeration enumeration = attrs.elements(); enumeration.hasMoreElements();) {
				String attr = (String) enumeration.nextElement();
				String defaultval = null;
				if (attr.indexOf("=") != -1) {
					defaultval = attr.substring(attr.indexOf("=") + 1);
					attr = attr.substring(0, attr.indexOf("="));
				}
				DataField campo = (DataField) this.managedForm.getDataFieldReference(attr);
				if ((campo != null) && campo.isEnabled()) {
					if (attr.startsWith("COLUMN." + i)) {
						key = campo.getValue();
						if (key == null) {
							key = this.getValue(campo, defaultval);
						}
					} else if (attr.startsWith("MINVAL." + i)) {
						minval = campo.getValue();
						if (minval == null) {
							minval = this.getValue(campo, defaultval);
						}
					} else if (attr.startsWith("MAXVAL." + i)) {
						maxval = campo.getValue();
						if (maxval == null) {
							maxval = this.getValue(campo, defaultval);
						} else if (maxval instanceof Date) {
							Date d = (Date) maxval;
							maxval = DateUtil.truncToEnd(d);
						}
					} else if (attr.startsWith("VAL." + i)) {
						// Los campos check no dan el valor,si estan
						// seleccionados se toman del texto sin
						// traducir.
						if (campo instanceof CheckDataField) {
							CheckDataField chk = (CheckDataField) campo;
							if (chk.isEnabled() && chk.isSelected()) {
								Vector t = chk.getTextsToTranslate();
								val = t.get(t.size() > 2 ? 1 : 0);
								if (!"PERIODOS_TRABAJO".equals(val)) {
									val = this.getValueActivity(val);
								}
							} else {
								val = null;
							}
						} else {
							val = campo.getValue();
							if (val == null) {
								val = this.getValue(campo, defaultval);
							}
						}
						if (val != null) {
							if ("PERIODOS_TRABAJO".equals(val)) {
								//								vals.add("COMIENZO_MARCADO_POR_INSERCION_DE_LA_TARJETA");
								//								vals.add("FIN_MARCADO_POR_LA_EXTRACCION_DE_LA_TARJETA");
								//								vals.add("COMIENZO_MARCADO_MANUALMENTE");
								//								vals.add("FIN_MARCADO_MANUALMENTE");
								//								vals.add("COMIENZO_MARCADO_POR_LA_VU");
								//								vals.add("FIN_MARCADO_POR_LA_VU");
								bperiodos = true;
							} else {
								vals.add(val);
							}
						}
					}
				}
			}
			if ((key != null) && enable) {
				if (bperiodos) {
					rtn.put("TPPERIODO", new SearchValue(SearchValue.NOT_NULL,null ));
				}
				if (!vals.isEmpty()) {
					rtn.put(key, new SearchValue(SearchValue.OR, vals));
				} else if ((minval != null) && (maxval != null)) {
					Vector v = new Vector();
					v.add(minval);
					v.add(maxval);
					rtn.put(key, new SearchValue(SearchValue.BETWEEN, v));
				} else if (maxval != null) { // only less condition.
					rtn.put(key, new SearchValue(SearchValue.LESS_EQUAL, maxval));
				} else if (minval != null) { // only less condition.
					rtn.put(key, new SearchValue(SearchValue.MORE_EQUAL, minval));
				}
			}
			i++; // Next filter group.
		}
		// Para la columna seleccionada establezco los rangos de busqueda.
		return rtn;
	}

	protected Object getValueActivity(Object var) {
		if ("ACTIVITY_TYPE_DRIVING".equals(var)) {
			return ITranslationService.ACTIVITY_TYPE_DRIVING;
		} else if ("ACTIVITY_TYPE_REST".equals(var)) {
			return ITranslationService.ACTIVITY_TYPE_REST;
		} else if ("ACTIVITY_TYPE_WORK".equals(var)) {
			return ITranslationService.ACTIVITY_TYPE_WORK;
		} else if ("ACTIVITY_TYPE_AVAILABLE".equals(var)) {
			return ITranslationService.ACTIVITY_TYPE_AVAILABLE;
		} else if ("ACTIVITY_TYPE_INDETERMINATE".equals(var)) {
			return ITranslationService.ACTIVITY_TYPE_INDETERMINATE;
		}
		return var;
	}

	protected void doOnBuildReport() {
		if (this.checkRequiredVisibleDataFields(true)) {
			return;
		}
		// Consulto la tabla visible.
		List<Table> tbls = FIMUtils.getTables(this.managedForm);
		Table tbl = null;
		for (Iterator<Table> iter = tbls.iterator(); iter.hasNext();) {
			tbl = iter.next();
			if (tbl.isShowing()) {
				break;
			}
		}
		if ((tbl != null) && tbl.isShowing()) {
			Hashtable<String, Object> values = (Hashtable<String, Object>) tbl.getValue();

			if (!values.isEmpty()) {
				String tblname = tbl.getEntityName();
				JDialog reportdlg = this.tbl_reportdlg.get(tbl.getAttribute());
				Form reportform = this.tbl_reportform.get(tbl.getAttribute());
				if (reportdlg == null) {
					String formname = this.tbl_report_LUT.get(tblname);
					reportform = this.formManager.getFormCopy(formname);
					reportform.getInteractionManager().setInitialState();
					reportdlg = this.getFormDialog(reportform, true);
					this.tbl_reportdlg.put(tblname, reportdlg);
					this.tbl_reportform.put(tblname, reportform);
				}

				reportform.getInteractionManager().setInitialState();
				// Inserto los valors de las claves y desactivo elcampo.
				for (Iterator<String> iter2 = this.keys.iterator(); iter2.hasNext();) {
					String ck = iter2.next();
					reportform.setDataFieldValue(ck, this.managedForm.getDataFieldValue(ck));
					reportform.disableDataField(ck);
				}
				// coloco los valores de los filtros.
				reportform.setDataFieldValue(OpentachFieldNames.FILTERFECINI, this.managedForm.getDataFieldValue(OpentachFieldNames.FILTERFECINI));
				reportform.setDataFieldValue(OpentachFieldNames.FILTERFECFIN, this.managedForm.getDataFieldValue(OpentachFieldNames.FILTERFECFIN));

				((IMReportRoot) reportform.getInteractionManager()).setReportData(values, false);
				reportdlg.setVisible(true);
			} else {
				this.managedForm.message("M_NO_EXISTEN_DATOS_INFORME", Form.INFORMATION_MESSAGE);
			}
		}
	}

	protected List<DataField> getFilterFields() {
		ArrayList<DataField> rtn = new ArrayList<DataField>();
		int i = 1;
		DataComponentGroup filtergroup;
		while ((filtergroup = (DataComponentGroup) this.managedForm.getElementReference(this.filtergroupname + "." + i)) != null) {
			Vector attrs = filtergroup.getAttributes();
			for (Enumeration enumeration = attrs.elements(); enumeration.hasMoreElements();) {
				String attr = (String) enumeration.nextElement();
				DataField campo = (DataField) this.managedForm.getDataFieldReference(attr);
				if ((campo != null) && campo.isEnabled()) {
					rtn.add(campo);
				}
			}
			i++;
		}
		return rtn;
	}

	protected Object getValue(DataField campo, String defaultval) {
		Object val = null;
		try {
			if (defaultval != null) {
				int clas = campo.getSQLDataType();
				if (clas == Types.ARRAY) {
					val = defaultval;
				} else if (clas == Types.VARCHAR) {
					val = defaultval;
				} else if (clas == Types.INTEGER) {
					val = Integer.valueOf(defaultval);
				} else if (clas == Types.BOOLEAN) {
					val = Boolean.valueOf(defaultval);
				} else if (clas == Types.DATE) {
					val = DateFormat.getDateInstance().parseObject(defaultval);
				} else if (clas == Types.TIMESTAMP) {
					val = DateFormat.getInstance().parseObject(defaultval);
				} else if (clas == Types.TIME) {
					val = DateFormat.getTimeInstance().parseObject(defaultval);
				}
			}
			return val;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	protected String getReportName() {
		Locale locale = ApplicationManager.getLocale();
		String reportfile = IMReportRoot.BASEREPORTPATH + locale + "/" + this.nombInforme;
		if (!reportfile.endsWith("xml")) {
			reportfile += ".xml";
		}
		return reportfile;
	}

	protected void setFilterEnabled(int filter, boolean val) {
		DataComponentGroup g = this.managedForm.getGroupReference("FILTER." + filter);
		if (g == null) {
			return;
		}
		for (Iterator iter = g.getAttributes().iterator(); iter.hasNext();) {
			String attr = (String) iter.next();
			if (attr.indexOf("=") != -1) {
				attr = attr.substring(0, attr.indexOf("="));
			}
			if (val) {
				this.managedForm.enableDataField(attr);
			} else {
				this.managedForm.disableDataField(attr);
			}
		}
	}

	/**
	 * Get the report band visibility configuration, Allowing to show/hide repotr bands/group. Report must has a field named "SHOW_BANDNAME" and its value will be YES when the band
	 * will be visible.
	 *
	 * @return
	 */
	protected Map<String, Object> getBandVisibility() {
		Map<String, Object> bands = new HashMap<String, Object>();
		int i = 1;
		DataComponentGroup filtergroup;
		while ((filtergroup = (DataComponentGroup) this.managedForm.getElementReference(this.bandcfgname + "." + i)) != null) {
			Vector attrs = filtergroup.getAttributes();
			for (Enumeration enumeration = attrs.elements(); enumeration.hasMoreElements();) {
				String attr = (String) enumeration.nextElement();
				DataField campo = (DataField) this.managedForm.getDataFieldReference(attr);
				if (campo != null) {
					bands.put(attr, campo.getValue());
				}
			}
			i++;
		}
		return bands;
	}

	@Override
	protected void refreshTable(String tablename) {
		if (this.checkQuery()) {
			if ((this.crbUltimosDias != null) && this.crbUltimosDias.isSelected()) {
				this.ultimosDatos();
			}
			super.refreshTable(tablename);
		}
	}

	public void setReportData(Hashtable cv, boolean hidetable) {
		Table tblInforme = (Table) this.managedForm.getDataFieldReference(this.tablename);
		if (tblInforme != null) {
			tblInforme.setValue(cv);
			tblInforme.setEnabled(!cv.isEmpty() ? true : false);
			if (hidetable) {
				tblInforme.setVisible(false);
			}
		}
	}

	@Override
	public void setQueryInsertMode() {
		super.setQueryInsertMode();
		this.managedForm.enableDataField("ULTIMOS_DIAS");
		this.managedForm.enableDataField("RANGO_FECHA");
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		RadioButtonDataField crb2 = (RadioButtonDataField) this.managedForm.getDataFieldReference(IMReportRoot.ULTIMOS_DIAS);
		if (crb2 != null) {
			if (crb2.isSelected()) {
				this.managedForm.disableDataField(OpentachFieldNames.FILTERFECINI);
				this.managedForm.disableDataField(OpentachFieldNames.FILTERFECFIN);
			}
		}
	}

	protected void createLocalReports(final String urljr, final String entityName, final EntityResult resEntity, final String title, final String delegCol, String dscr) {

		OperationThread opth = new OperationThread("REPORT") {
			@Override
			public void run() {
				this.hasStarted = true;
				JasperPrint jp = null;
				try {
					final Map<String, Object> params = IMReportRoot.this.getParams(title, delegCol);
					EntityResult result = IMReportRoot.this.getMainEntityResult(entityName, resEntity);
					if ("EIncidentes".equals(entityName)) {
						result = EntityResultTools.doSort(result, "TIPO_DSCR_INC");
					}
					Locale locale = (Locale) params.get("locale");
					if (locale == null) {
						locale = new Locale("es", "ES");
					}
					params.put(JRParameter.REPORT_LOCALE, locale);

					// params.put(JRParameter.REPORT_CONNECTION, );
					ResourceBundle rb = ResourceManager.getBundle(locale);
					if (rb != null) {
						params.put(JRParameter.REPORT_RESOURCE_BUNDLE, rb);
					}
					jp = JRReportUtil.fillReport(JRReportUtil.getJasperReport(urljr), new Hashtable<String, Object>(params), result);

				} catch (Exception e) {
					IMReportRoot.logger.error(null, e);
				} finally {
					this.hasFinished = true;
					this.res = jp;
				}
			}
		};
		JFrame jd = (JFrame) SwingUtilities.getWindowAncestor(this.managedForm);
		ExtendedApplicationManager.proccessOperation(jd, opth, 500);
		JasperPrint jp = (JasperPrint) opth.getResult();
		if ((jp != null) && (jp.getPages() != null) && (jp.getPages().size() > 0)) {
			JRDialogViewer jv = JRDialogViewer.getJasperViewer(ApplicationManager.getTranslation(dscr), jd, jp);
			jv.setVisible(true);
		} else {
			this.managedForm.message("M_NO_SE_HAN_ENCONTRADO_DATOS", Form.WARNING_MESSAGE);
		}
	}

	public void createReports(int num_informe, final String entityName, final EntityResult resEntity) throws Exception {
		try {
			ApplicationManager.getApplication().getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			JRPropertyManager jpm = new JRPropertyManager("com/opentach/reports/reports.properties");
			final JRReportDescriptor jrd = jpm.getDataMap().get(Integer.valueOf(num_informe));
			final Map<String, Object> params = this.getParams(jrd.getDscr(), jrd.getDelegCol());

			final JFrame jd = (JFrame) SwingUtilities.getWindowAncestor(this.managedForm);
			if (jrd != null) {
				final List lReports = jrd.getLReports();
				if ((lReports == null) || (lReports.size() == 0)) {
					try {
						final String urljr = jrd.getUrl().substring(0, jrd.getUrl().length() - 7);

						OperationThread opth = new OperationThread("REPORT") {
							@Override
							public void run() {
								this.hasStarted = true;
								JasperPrint jp = null;
								try {
									EntityResult result = IMReportRoot.this.getMainEntityResult(entityName, resEntity);
									if ("EIncidentes".equals(entityName)) {
										result = EntityResultTools.doSort(result, new String[] { "MATRICULA", "TIPO_DSCR_INC" });
									}
									Locale locale = (Locale) params.get("locale");
									if (locale == null) {
										params.put(JRParameter.REPORT_LOCALE, ApplicationManager.getLocale() == null ? new Locale("es", "ES") : ApplicationManager.getLocale());
									}

									// params.put(JRParameter.REPORT_CONNECTION,
									// );
									ResourceBundle rb = ResourceManager.getBundle(locale !=null ? locale : ApplicationManager.getLocale());
									if (rb != null) {
										params.put(JRParameter.REPORT_RESOURCE_BUNDLE, rb);
									}
									jp = JRReportUtil.fillReport(JRReportUtil.getJasperReport(urljr), new Hashtable<String, Object>(params), result);

								} catch (Exception e) {
									IMReportRoot.logger.error(null, e);
								} finally {
									this.hasFinished = true;
									this.res = jp;
								}
							}
						};
						ExtendedApplicationManager.proccessOperation(jd, opth, 500);
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
									Table tb = (Table) IMReportRoot.this.managedForm.getElementReference(entityName);

									for (int i = 0; i < lReports.size(); i++) {
										JRReportDescriptor jru = (JRReportDescriptor) lReports.get(i);
										urljr = jru.getUrl();
										Map<String, Object> p = params;
										if (i != 0) {
											p = IMReportRoot.this.getParams(jru.getDscr(), jru.getDelegCol());
										}
										AbstractOpentachClientLocator ocl = (AbstractOpentachClientLocator) IMReportRoot.this.getReferenceLocator();
										JasperPrint jp = ocl.getRemoteService(IReportService.class).fillReport(urljr, p, jru.getMethodAfter(), jru.getMethodBefore(),
												new EntityResult((Hashtable) tb.getValue()), ocl.getSessionId());
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
					} catch (

							Exception e) {
						e.printStackTrace();
					}
				}
			}
		} finally {
			ApplicationManager.getApplication().getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	protected Map<String, Object> getParams(String title, String delegCol) throws Exception {
		return new Hashtable<String, Object>();
	}

	protected EntityResult getMainEntityResult(final String entityName, final EntityResult resEntity) {
		EntityResult result;
		if (resEntity == null) {
			Table tb = (Table) this.managedForm.getElementReference(entityName);
			if (tb instanceof UExpansibleTable) {
				result = new EntityResult(((UExpansibleTable) tb).getOriginalValue());
			} else {
				result = new EntityResult((Hashtable) tb.getValue());
			}
		} else {
			result = resEntity;
		}
		return result;
	}

	private final class AutoSelectDriverVehicleValueChangeListener extends AbstractAutoSelectDriverVehicleValueChangeListener {
		public AutoSelectDriverVehicleValueChangeListener() {
			super(IMReportRoot.this);
		}

		@Override
		protected void onChange() throws Exception {
			IMReportRoot.this.refreshTables();
			IMReportRoot.this.refreshChart();

		}
	}
}
