package com.opentach.client.comp;

import java.awt.event.FocusListener;
import java.util.Hashtable;

import com.ontimize.gui.field.HourDateDataField;

public class CertifDateDataField extends HourDateDataField {

	public CertifDateDataField(Hashtable parameters) {
		super(parameters);
		FocusListener[] flarray = this.dataField.getFocusListeners();
		for (FocusListener fl : flarray) {
			this.dataField.removeFocusListener(fl);
		}
	}

	@Override
	public void setValue(Object value) {
		super.setValue(value);
		this.hourField.setText("");
	}

	@Override
	public void setValueFromComponent(Object componentValue) {
		super.setValueFromComponent(componentValue);
		this.hourField.setText("");
	}
}
