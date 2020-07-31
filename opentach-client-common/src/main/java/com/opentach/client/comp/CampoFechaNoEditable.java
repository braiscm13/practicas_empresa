package com.opentach.client.comp;

import java.util.Hashtable;

import com.ontimize.gui.field.DateDataField;

public class CampoFechaNoEditable extends DateDataField {

	public CampoFechaNoEditable(Hashtable<String, Object> parametros) {
		super(parametros);
	}

	@Override
	public void setEnabled(boolean activ) {

		super.setEnabled(activ);
		this.dataField.setEnabled(false);
	}
}
