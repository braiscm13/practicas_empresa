package com.opentach.client.modules.data;

import java.awt.Color;
import java.awt.Font;
import java.util.Hashtable;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.ObjectDataField;
import com.utilmize.client.gui.AbstractValueChangeListener;
import com.utilmize.client.gui.buttons.IUFormComponent;

public class IMEmpresaIsDemoValueChangeListener extends AbstractValueChangeListener {

	@FormComponent(attr = "IS_DEMO")
	private ObjectDataField	isDemoField;

	public IMEmpresaIsDemoValueChangeListener(IUFormComponent formComponent, Hashtable params) throws Exception {
		super(formComponent, params);
	}

	@Override
	public void valueChanged(ValueEvent event) {
		this.isDemoField.setVisible("S".equals(this.isDemoField.getValue()));
		this.isDemoField.getLabelComponent().setForeground(Color.decode("#b70000"));
		this.isDemoField.getLabelComponent().setFont(this.isDemoField.getLabelComponent().getFont().deriveFont(Font.BOLD));
	}
}
