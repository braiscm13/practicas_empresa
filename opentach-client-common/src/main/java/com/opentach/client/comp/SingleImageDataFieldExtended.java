package com.opentach.client.comp;

import java.util.Hashtable;

import javax.swing.JLabel;

import com.utilmize.client.gui.field.USingleImageDataField;

public class SingleImageDataFieldExtended extends USingleImageDataField {

	public SingleImageDataFieldExtended(Hashtable param) {
		super(param);
	}
	
	@Override
	public void setValue(Object value) {
		if (value == null){
			((JLabel) this.dataField).setIcon(getEmptyImage());
		}else {
			super.setValue(value);
		}
	}
}
