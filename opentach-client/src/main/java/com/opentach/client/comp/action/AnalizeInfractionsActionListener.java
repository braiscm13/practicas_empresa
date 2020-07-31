package com.opentach.client.comp.action;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Types;
import java.text.DateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.BasicInteractionManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.container.DataComponentGroup;
import com.ontimize.gui.field.CheckDataField;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.opentach.client.comp.ExtendedApplicationManager;
import com.opentach.client.modules.chart.IMGraficaCond;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.util.DateUtil;

public class AnalizeInfractionsActionListener implements ActionListener, OpentachFieldNames {
	protected static String		filtergroupname	= "FILTER";
	private static final String	EINFORME_INFRAC	= "EInformeInfrac";

	private Form				FInsertarV		= null;
	private JDialog				JInsertarV		= null;
	private boolean 			naturaleza		= false;

	public AnalizeInfractionsActionListener(boolean natur) {
		super();
		naturaleza = natur;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		final Form managedForm = (Form) SwingUtilities.getAncestorOfClass(Form.class, (Component) e.getSource());
		final IFormManager formManager = managedForm.getFormManager();
		final InteractionManager interactionManager = managedForm.getInteractionManager();
		final Table tblInforme = (Table) managedForm.getDataFieldReference(AnalizeInfractionsActionListener.EINFORME_INFRAC);
		final boolean hasTable = tblInforme != null;
		if (!this.checkEmptyRequiredDataFields(managedForm)) {
			return;
		}
		if (hasTable) {
			tblInforme.deleteData();
			tblInforme.setEnabled(true);
		}
		OperationThread op = new OperationThread() {
			@Override
			public String getDescription() {
				return ApplicationManager.getTranslation("Analizando");
			}

			@Override
			public void run() {
				this.hasStarted = true;
				this.res = null;
				try {
					Entity ent = formManager.getReferenceLocator().getEntityReference(AnalizeInfractionsActionListener.EINFORME_INFRAC);
					if (ent != null) {
						Hashtable<Object,Object> ht = AnalizeInfractionsActionListener.this.getFilterValues(managedForm);
						ht.putAll(managedForm.getDataFieldValues(false));
						if (naturaleza) {
							ht.put("F_DESCARGA", true);
						}
						Vector<Object> v = hasTable ? tblInforme.getCurrentColumns() : new Vector<Object>();
						AnalizeInfractionsActionListener.this.addToVectorIfNotExists(v, "EXTERNAL_EMPLOYEE_ID");
						AnalizeInfractionsActionListener.this.addToVectorIfNotExists(v, OpentachFieldNames.DNI_FIELD);
						AnalizeInfractionsActionListener.this.addToVectorIfNotExists(v, OpentachFieldNames.NAME_FIELD);
						AnalizeInfractionsActionListener.this.addToVectorIfNotExists(v, OpentachFieldNames.SURNAME_FIELD);
						AnalizeInfractionsActionListener.this.addToVectorIfNotExists(v, "NUM_TRJ_CONDU");
						AnalizeInfractionsActionListener.this.addToVectorIfNotExists(v, "TIPO_DSCR");
						AnalizeInfractionsActionListener.this.addToVectorIfNotExists(v, "DSCR_TIPO_INFRAC");
						AnalizeInfractionsActionListener.this.addToVectorIfNotExists(v, "TIPO");
						AnalizeInfractionsActionListener.this.addToVectorIfNotExists(v, "FECHORAINI");
						AnalizeInfractionsActionListener.this.addToVectorIfNotExists(v, "FECHORAFIN");
						AnalizeInfractionsActionListener.this.addToVectorIfNotExists(v, "NATURALEZA");
						AnalizeInfractionsActionListener.this.addToVectorIfNotExists(v, "FADES");
						AnalizeInfractionsActionListener.this.addToVectorIfNotExists(v, "EXCON");
						AnalizeInfractionsActionListener.this.addToVectorIfNotExists(v, "TCP");
						AnalizeInfractionsActionListener.this.addToVectorIfNotExists(v, "TDP");

						EntityResult er = ent.query(ht, v, formManager.getReferenceLocator().getSessionId());
						if ((er != null) && (er.calculateRecordNumber() == 0) && (er.getCode() == EntityResult.OPERATION_SUCCESSFUL)) {
							this.res = "M_NO_HAY_INFRACCIONES";
						} else if ((er != null) && (er.getCode() == EntityResult.OPERATION_SUCCESSFUL)) {
							this.res = er;
						} else if (er != null) {
							if (er.getMessage().startsWith("E_MAX_DRIVERS_LIMIT_")) {
								String s = er.getMessage();
								String aux = s.substring("E_MAX_DRIVERS_LIMIT_".length(), s.length());
								managedForm.message(ApplicationManager.getTranslation("E_MAX_DRIVERS_LIMIT",
										ApplicationManager.getApplicationBundle(), new Object[] { aux }), Form.ERROR_MESSAGE);

							} else {
								throw new Exception(er.getMessage());
							}
						} else {
							throw new Exception("M_ERROR_INFRACCIONES");
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
					this.res = e;
				}
				this.hasFinished = true;
			}

		};
		Window window = SwingUtilities.getWindowAncestor(managedForm);
		if (window instanceof JFrame) {
			ExtendedApplicationManager.proccessOperation((JFrame) window, op, 50);
		} else {
			ExtendedApplicationManager.proccessOperation((JDialog) window, op, 50);
		}
		Object result = op.getResult();
		if (result instanceof String) {
			managedForm.message("M_NO_HAY_INFRACCIONES", Form.MESSAGE);
		} else if (result instanceof Throwable) {
			managedForm.message(((Throwable) result).getMessage(), Form.ERROR_MESSAGE);
		} else if (result instanceof EntityResult) {
			EntityResult er = (EntityResult) result;
			if (tblInforme != null) {
				if (!naturaleza) {
				tblInforme.setValue(er);
				} else {
					EntityResult res = new EntityResult(er.getOrderColumns());
					int cont = er.calculateRecordNumber();
					Hashtable registro = new Hashtable();
					for (int i = 0; i<cont ; i++ ) {
						registro = er.getRecordValues(i);
						if (registro.containsKey("NATURALEZA") && "MG".equals(registro.get("NATURALEZA"))) {
							res.addRecord(registro);
						}
					}
					tblInforme.setValue(res);
				}
			}
			if (interactionManager instanceof IMGraficaCond) {
				((IMGraficaCond) interactionManager).setInfracciones(er);
			}
			if ("GRAFICA_COND".equals(managedForm.getFormTitle())) {
				if (this.JInsertarV == null) {
					this.FInsertarV = formManager.getFormCopy("formGraficaCondInfrac.xml");
					this.JInsertarV = this.FInsertarV.putInModalDialog();
					this.JInsertarV.setTitle(ApplicationManager.getTranslation("GRAFICA_COND"));

				}
				this.FInsertarV.getInteractionManager().setUpdateMode();
				Table tbinfrac = (Table) this.FInsertarV.getDataFieldReference(AnalizeInfractionsActionListener.EINFORME_INFRAC);
				if (tbinfrac != null) {
					tbinfrac.setValue(er);
				}
				this.JInsertarV.setVisible(true);
			}
		}

	}

	private void addToVectorIfNotExists(Vector<Object> v, String field) {
		if ((v != null) && !v.contains(field)) {
			v.add(field);
		}
	}

	public boolean checkEmptyRequiredDataFields(Form managedForm) {
		Vector<Object> emptyRequiredDataField = managedForm.getEmptyRequiredDataField();
		if ((emptyRequiredDataField != null) && (emptyRequiredDataField.size() > 0)) {
			managedForm.message(this.getEmptyRequiredFieldsMessage(managedForm, emptyRequiredDataField), Form.WARNING_MESSAGE);
			return false;
		} else {
			return true;
		}
	}

	protected String getEmptyRequiredFieldsMessage(Form managedForm, Vector<Object> emptyFields) {
		StringBuffer translation = new StringBuffer(ApplicationManager.getTranslation(BasicInteractionManager.M_FILL_ALL_REQUIRED_FIELDS,
				managedForm.getResourceBundle()));
		boolean isHTML = false;

		if (translation.toString().toLowerCase().endsWith("</html>")) {
			isHTML = true;
			translation.delete(translation.length() - 7, translation.length());
		}

		translation.append(": ");

		String firstText = emptyFields.get(0).toString();
		FormComponent elementReference = managedForm.getElementReference(firstText);
		if ((elementReference instanceof DataField) && (((DataField) elementReference).getLabelText() != null)) {
			firstText = ((DataField) elementReference).getLabelText();
		}

		translation.append(ApplicationManager.getTranslation(firstText, managedForm.getResourceBundle()));
		for (int i = 1; i < emptyFields.size(); i++) {
			translation.append(", ");
			String emptyFieldAttr = emptyFields.get(i).toString();
			elementReference = managedForm.getElementReference(emptyFieldAttr);
			if ((elementReference instanceof DataField) && (((DataField) elementReference).getLabelText() != null)) {
				emptyFieldAttr = ((DataField) elementReference).getLabelText();
			}
			translation.append(ApplicationManager.getTranslation(emptyFieldAttr, managedForm.getResourceBundle()));
		}
		if (isHTML) {
			translation.append("</html>");
		}
		return translation.toString();

	}

	/**
	 * <br>
	 * Recorre todos los grupos de componentes de datos con estructura de nombre
	 * 'filtergroupname.idx' y retorna el hash con los valores busqueda que
	 * permiten filtrar la consulta SQL. Dentro del grupo de componentes debe
	 * existir
	 * <ul>
	 * <li>Un componente con el id de la columna sobre la que se filtrará
	 * attr='COLUMN.indice'
	 * <li>Un componente con el valor mínimo attr='MINVAL.indice'
	 * <li>Un componente con el valor máximo attr='MINVAL.indice'
	 * <li>Un componente con el valor exacto attr='VAL.indice'
	 * </ul>
	 * Si se desea fijar alguno de los valores debe ir seguido su attr de = y el
	 * valor fijado. Así por ejemplo un filtro que actua siempre sobre la
	 * columna HORA tendrá como attr en su grupo o componente COLUMN.1=HORA
	 *
	 * @return Hashtable con clave la columna y valor el ValorBusqueda
	 *         determinado por el filtro.
	 */
	@SuppressWarnings("unchecked")
	protected Hashtable<Object,Object> getFilterValues(Form managedForm) {
		Object key, minval, maxval, val;
		Hashtable<Object,Object> rtn = new Hashtable<Object,Object>();
		int i = 1;
		DataComponentGroup filtergroup;
		while ((filtergroup = (DataComponentGroup) managedForm.getElementReference(AnalizeInfractionsActionListener.filtergroupname + "." + i)) != null) {
			Vector<Object> vals = new Vector<Object>();
			Vector<Object> attrs = filtergroup.getAttributes();
			boolean enable = true;
			key = minval = maxval = val = null;
			for (Enumeration<Object> enumeration = attrs.elements(); enumeration.hasMoreElements();) {
				String attr = (String) enumeration.nextElement();
				String defaultval = null;
				if (attr.indexOf("=") != -1) {
					defaultval = attr.substring(attr.indexOf("=") + 1);
					attr = attr.substring(0, attr.indexOf("="));
				}
				DataField campo = (DataField) managedForm.getDataFieldReference(attr);
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
								Vector<Object> t = chk.getTextsToTranslate();
								val = t.get(t.size() > 2 ? 1 : 0);
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
								vals.add("COMIENZO_MARCADO_POR_INSERCION_DE_LA_TARJETA");
								vals.add("FIN_MARCADO_POR_LA_EXTRACCION_DE_LA_TARJETA");
								vals.add("COMIENZO_MARCADO_MANUALMENTE");
								vals.add("FIN_MARCADO_MANUALMENTE");
								vals.add("COMIENZO_MARCADO_POR_LA_VU");
								vals.add("FIN_MARCADO_POR_LA_VU");

							}
							vals.add(val);
						}
					}
				}
			}
			if ((key != null) && enable) {
				if (!vals.isEmpty()) {
					rtn.put(key, new SearchValue(SearchValue.OR, vals));
				} else if ((minval != null) && (maxval != null)) {
					Vector<Object> v = new Vector<Object>();
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

}
