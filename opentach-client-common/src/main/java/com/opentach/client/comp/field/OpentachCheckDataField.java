package com.opentach.client.comp.field;

import java.util.Hashtable;

import com.utilmize.client.gui.field.UCheckDataField;

public class OpentachCheckDataField extends UCheckDataField {

	public OpentachCheckDataField(Hashtable parametros) {
		super(parametros);
	}

	@Override
	public void setValue(Object valor) {
		super.setValue(valor);
		this.valueSave = this.getValue();
	}

}
