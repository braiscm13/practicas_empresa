package com.opentach.client.comp.calendar;

import java.util.Hashtable;

import javax.swing.AbstractButton;

import com.ontimize.db.NullValue;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.CheckDataField;

public class CheckDataField2 extends CheckDataField {

	public CheckDataField2(Hashtable parameters) {
		super(parameters);
	}

	@Override
	public void setValue(Object value) {
		/* 449 */this.setInnerListenerEnabled(false);
		/* 450 */Object previousValue = this.getValue();
		/* 451 */if ((value != null) && !(value instanceof NullValue)) {
			/* 452 */if (value instanceof Number) {
				/* 453 */if (((Number) value).intValue() != 0) {
					/* 454 */((AbstractButton) this.dataField).setSelected(true);
				} else {
					/* 456 */((AbstractButton) this.dataField).setSelected(false);
				}
				/* 458 */this.valueSave = this.getValue();
				/* 459 */this.fireValueChanged(this.valueSave, previousValue, ValueEvent.PROGRAMMATIC_CHANGE);
			} else
			/* 462 */if (value instanceof Boolean) {
				/* 463 */if (((Boolean) value).booleanValue()) {
					/* 464 */((AbstractButton) this.dataField).setSelected(true);
				} else {
					/* 466 */((AbstractButton) this.dataField).setSelected(false);
				}
				/* 468 */this.valueSave = this.getValue();
				/* 469 */this.fireValueChanged(this.valueSave, previousValue, ValueEvent.PROGRAMMATIC_CHANGE);
			} else
			/* 470 */if (value instanceof String) {
				/* 471 */if (value.equals(CheckDataField.YES)) {
					/* 472 */((AbstractButton) this.dataField).setSelected(true);
				} else {
					/* 474 */((AbstractButton) this.dataField).setSelected(false);
				}
				this.valueSave = this.getValue();
				this.fireValueChanged(this.valueSave, previousValue, ValueEvent.PROGRAMMATIC_CHANGE);
			} else {
				this.deleteData();
			}
		} else {
			this.deleteData();
		}
		this.setInnerListenerEnabled(true);
	}
}
