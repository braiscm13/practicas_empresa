package com.opentach.client.comp;

import java.util.Hashtable;

import com.ontimize.gui.field.HourDateDataField;

public class CampoFechaHoraNoEditable extends HourDateDataField {

	public CampoFechaHoraNoEditable(Hashtable<String, Object> parametros) {
		super(parametros);
	}

	@Override
	public void setEnabled(boolean activ) {

		super.setEnabled(activ);
		this.dataField.setEnabled(false);
	}
}
