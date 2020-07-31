package com.opentach.client.comp;

import java.util.Hashtable;

import com.utilmize.client.gui.field.UHourDateDataField;

public class TimezoneHourDateDataField extends UHourDateDataField {

	public TimezoneHourDateDataField(Hashtable parametros) {
		super(parametros);
	}

	@Override
	public void setValue(Object value) {
		value = TimezoneDateCellRenderer.addaptValueToSystemTimezone(value);
		super.setValue(value);
	}

}
