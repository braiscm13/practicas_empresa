package com.opentach.client.employee.listeners;

import java.util.Hashtable;

import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.DataField;
import com.utilmize.client.gui.AbstractValueChangeListener;
import com.utilmize.client.gui.buttons.IUFormComponent;

/**
 * Add this listener to both a "minutes" data field and its "hours" counterpart (or vice versa) to automatically update both fields.
 *
 * Both field must have equivalent attr's, except for the substrings "HOURS" and "MINUTES", e.g.: "MAX_HOURS_JOB", "MAX_MINUTES_JOB". Useful to create
 * forms with input values in hours while storing database values in minutes (or the other way around).
 *
 * Bear in mind: the initialization order of both fields within its parent form is important (the field without a DB value stored should be
 * initialized FIRST). To force proper initialization order, the "setvalueorder" attribute of the parent form may be used.
 *
 * @author Imatia
 *
 */
public class HoursToMinutesValueChangeListener extends AbstractValueChangeListener {
	private static final String	HOURS	= "HOURS";
	private static final String	MINUTES	= "MINUTES";

	public HoursToMinutesValueChangeListener(IUFormComponent formComponent, Hashtable params) throws Exception {
		super(formComponent, params);
	}

	private boolean equalValues(Integer objectA, Integer objectB) {
		return ((objectA == null) && (objectB == null)) || ((objectA != null) && objectA.equals(objectB));
	}

	@Override
	public void valueChanged(ValueEvent event) {
		if (event == null) {
			return;
		}

		DataField srcDataField = (DataField) event.getSource();

		String srcAttr = (String) srcDataField.getAttribute();
		String dstAttr = null;
		boolean hour2min = false;

		if (srcAttr.contains(HoursToMinutesValueChangeListener.MINUTES)) {
			dstAttr = srcAttr.replace(HoursToMinutesValueChangeListener.MINUTES, HoursToMinutesValueChangeListener.HOURS);
		} else if (srcAttr.contains(HoursToMinutesValueChangeListener.HOURS)) {
			dstAttr = srcAttr.replace(HoursToMinutesValueChangeListener.HOURS, HoursToMinutesValueChangeListener.MINUTES);
			hour2min = true;
		}

		if (dstAttr != null) {
			DataField dstDataField = (DataField) this.getForm().getDataFieldReference(dstAttr);
			if (dstDataField != null) {
				Integer srcValue = null;
				Integer newDstValue = null;
				Integer oldDstValue = dstDataField.getValue() == null ? null : ((Number) dstDataField.getValue()).intValue();

				if (event.getNewValue() != null) {
					srcValue = ((Number) event.getNewValue()).intValue();
					newDstValue = hour2min ? srcValue * 60 : srcValue / 60;
				}

				if (!this.equalValues(newDstValue, oldDstValue)) {
					dstDataField.setValue(newDstValue);
				}
			}
		}
	}
}
