package com.opentach.adminclient.modules.admin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.gui.field.RadioButtonDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.opentach.common.smartphonevipcodes.ISmartphoneVipCodeService;
import com.opentach.common.util.DateUtil;
import com.utilmize.client.fim.UBasicFIM;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;

public class IMMobileVipCodes extends UBasicFIM {

	@FormComponent(attr = "ESmartphoneVipCodes")
	protected Table			table		= null;
	@FormComponent(attr = "FILTERFECINI")
	protected DateDataField	dfFecIni	= null;
	@FormComponent(attr = "FILTERFECFIN")
	protected DateDataField	dfFecFin	= null;
	@FormComponent(attr = "btnRefrescar")
	protected Button		bRefresh	= null;

	@Override
	public void registerInteractionManager(Form formulario, IFormManager gestorForms) {
		super.registerInteractionManager(formulario, gestorForms);
		this.bRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				IMMobileVipCodes.this.refreshTable();
			}
		});

		final RadioButtonDataField cdfUltimosDias = (RadioButtonDataField) formulario.getDataFieldReference("ULTIMOS_DIAS");
		final RadioButtonDataField cdfRangoFecha = (RadioButtonDataField) formulario.getDataFieldReference("RANGO_FECHA");
		cdfUltimosDias.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChanged(ValueEvent e) {
				if (cdfUltimosDias.isSelected() && (e.getType() == ValueEvent.USER_CHANGE)) {
					Date dFin = new Date();
					IMMobileVipCodes.this.dfFecFin.setValue(dFin);
					IMMobileVipCodes.this.dfFecIni.setValue(DateUtil.addDays(dFin, -15));
					IMMobileVipCodes.this.dfFecIni.setEnabled(false);
					IMMobileVipCodes.this.dfFecFin.setEnabled(false);
					IMMobileVipCodes.this.dfFecIni.setRequired(false);
					IMMobileVipCodes.this.dfFecFin.setRequired(false);
				}
			}
		});
		cdfRangoFecha.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChanged(ValueEvent e) {
				if (cdfRangoFecha.isSelected() && (e.getType() == ValueEvent.USER_CHANGE)) {
					IMMobileVipCodes.this.dfFecFin.setValue(null);
					IMMobileVipCodes.this.dfFecIni.setValue(null);
					IMMobileVipCodes.this.dfFecIni.setEnabled(true);
					IMMobileVipCodes.this.dfFecFin.setEnabled(true);
					IMMobileVipCodes.this.dfFecIni.setRequired(true);
					IMMobileVipCodes.this.dfFecFin.setRequired(true);
				}
			}
		});
		this.table = (Table) formulario.getElementReference("ESmartphoneVipCodes");
		ValueChangeListener vcl = new ValueChangeListener() {
			@Override
			public void valueChanged(ValueEvent ve) {
				if (ve.getType() == ValueEvent.USER_CHANGE) {
					IMMobileVipCodes.this.refreshTable();
				}
			}
		};
		this.dfFecFin.addValueChangeListener(vcl);
		this.dfFecIni.addValueChangeListener(vcl);
		FocusAdapter fl = new FocusAdapter() {
			Object	old	= null;

			@Override
			public void focusGained(FocusEvent e) {
				super.focusGained(e);
				this.old = ((JTextField) e.getSource()).getText();
			}

			@Override
			public void focusLost(FocusEvent e) {
				Object current = ((JTextField) e.getSource()).getText();
				if (!this.old.equals(current)) {
					IMMobileVipCodes.this.refreshTable();
				}
			}
		};

		((DataField) this.managedForm.getElementReference("CODE")).addFocusListener(fl);
	}

	public Table getTable() {
		return this.table;
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.setUpdateMode();
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		if (this.table != null) {
			this.table.setEnabled(true);
		}
		Date dFin = new Date();
		this.dfFecFin.setValue(dFin);
		this.dfFecIni.setValue(DateUtil.addDays(dFin, -15));
		this.dfFecIni.setEnabled(false);
		this.dfFecFin.setEnabled(false);
		this.bRefresh.setEnabled(true);
		this.managedForm.enableButton("generatevipcodes");
	}

	private void refreshTable() {
		try {
			this.computeDateFilter();
			this.table.refresh();
		} catch (Exception e) {
			e.printStackTrace();
			this.table.deleteData();
		}
	}

	private void computeDateFilter() throws Exception {
		Date d1 = (Date) this.dfFecIni.getValue();
		Date d2 = (Date) this.dfFecFin.getValue();
		if ((d1 == null) || (d2 == null)) {
			throw new Exception("NO_INFORMATION");
		}
		d2 = new Date(d2.getTime() + (24 * 60 * 60 * 1000));
		SearchValue sValue = new SearchValue(SearchValue.BETWEEN, new Vector(Arrays.asList(new Object[] { d1, d2 })));
		this.managedForm.setDataFieldValue("ACQUISITION_DATE", sValue);
	}

	public static class ListenerGenerateVipCodes extends AbstractActionListenerButton {

		public ListenerGenerateVipCodes() throws Exception {
			super();
		}

		public ListenerGenerateVipCodes(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
			super(button, formComponent, params);
		}

		public ListenerGenerateVipCodes(UButton button, Hashtable params) throws Exception {
			super(button, params);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			Number numCodes = Integer.valueOf(0);
			while ((numCodes != null) && (numCodes.intValue() == 0)) {
				try {
					String response = JOptionPane.showInputDialog(ApplicationManager.getTranslation("NUM_CODES_TO_GENERATE"));
					if (response == null) {
						return;
					}
					numCodes = Integer.parseInt(response);
				} catch (Exception ex) {
					// do nothing
				}
			}
			if (numCodes != null) {
				final int numToGenerate = numCodes.intValue();
				OperationThread ot = new OperationThread("GENERATING_CODES") {
					@Override
					public void run() {
						this.hasStarted = true;
						try {
							((ISmartphoneVipCodeService) ListenerGenerateVipCodes.this.getEntity("ESmartphoneVipCodes")).generateCodes(numToGenerate);
						} catch (Exception ex) {
							this.res = ex;
						} finally {
							this.hasFinished = true;
						}
					};
				};
				ApplicationManager.proccessNotCancelableOperation(this.getForm(), ot, 0);
				if (ot.getResult() instanceof Throwable) {
					this.getForm().message("ERROR_GENERATING_CODES", Form.ERROR_MESSAGE);
				} else {
					((IMMobileVipCodes) this.getInteractionManager()).getTable().refreshInThread(0);
					this.getForm().message("GENERATION_CODES_FINISHED", Form.INFORMATION_MESSAGE);
				}

			}
		}

	}
}
