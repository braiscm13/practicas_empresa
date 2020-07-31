package com.opentach.client.employee.listeners;

import java.util.Hashtable;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.DataField;
import com.opentach.common.OpentachFieldNames;
import com.utilmize.client.gui.AbstractValueChangeListener;
import com.utilmize.client.gui.buttons.IUFormComponent;

public class OnSetValueSetToIdOrigenValueChangeListener extends AbstractValueChangeListener {

	@FormComponent(attr = OpentachFieldNames.IDORIGEN_FIELD)
	protected DataField idOrigenField;

	public OnSetValueSetToIdOrigenValueChangeListener(IUFormComponent formComponent, Hashtable params) throws Exception {
		super(formComponent, params);
	}

	@Override
	public void valueChanged(ValueEvent e) {
		this.idOrigenField.setValue(e.getNewValue());
	}

}