package com.opentach.adminclient.modules.admin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import javax.swing.JTextField;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.Form;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.gui.field.RadioButtonDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.opentach.common.util.DateUtil;
import com.utilmize.client.fim.UBasicFIM;

public class IMMobileFileExtraction extends UBasicFIM {

	@FormComponent(attr = "EMobileFileExtraction")
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
				IMMobileFileExtraction.this.refreshTable();
			}
		});

		final RadioButtonDataField cdfUltimosDias = (RadioButtonDataField) formulario.getDataFieldReference("ULTIMOS_DIAS");
		final RadioButtonDataField cdfRangoFecha = (RadioButtonDataField) formulario.getDataFieldReference("RANGO_FECHA");
		cdfUltimosDias.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChanged(ValueEvent e) {
				if (cdfUltimosDias.isSelected() && (e.getType() == ValueEvent.USER_CHANGE)) {
					Date dFin = new Date();
					IMMobileFileExtraction.this.dfFecFin.setValue(dFin);
					IMMobileFileExtraction.this.dfFecIni.setValue(DateUtil.addDays(dFin, -15));
					IMMobileFileExtraction.this.dfFecIni.setEnabled(false);
					IMMobileFileExtraction.this.dfFecFin.setEnabled(false);
					IMMobileFileExtraction.this.dfFecIni.setRequired(false);
					IMMobileFileExtraction.this.dfFecFin.setRequired(false);
				}
			}
		});
		cdfRangoFecha.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChanged(ValueEvent e) {
				if (cdfRangoFecha.isSelected() && (e.getType() == ValueEvent.USER_CHANGE)) {
					IMMobileFileExtraction.this.dfFecFin.setValue(null);
					IMMobileFileExtraction.this.dfFecIni.setValue(null);
					IMMobileFileExtraction.this.dfFecIni.setEnabled(true);
					IMMobileFileExtraction.this.dfFecFin.setEnabled(true);
					IMMobileFileExtraction.this.dfFecIni.setRequired(true);
					IMMobileFileExtraction.this.dfFecFin.setRequired(true);
				}
			}
		});
		ValueChangeListener vcl = new ValueChangeListener() {
			@Override
			public void valueChanged(ValueEvent ve) {
				if (ve.getType() == ValueEvent.USER_CHANGE) {
					IMMobileFileExtraction.this.refreshTable();
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
					IMMobileFileExtraction.this.refreshTable();
				}
			}
		};

		((DataField) this.managedForm.getElementReference("COMPANY")).addFocusListener(fl);
		((DataField) this.managedForm.getElementReference("VEHICLE")).addFocusListener(fl);
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
		this.managedForm.setDataFieldValue("DOWNLOADTIME", sValue);
	}
}
