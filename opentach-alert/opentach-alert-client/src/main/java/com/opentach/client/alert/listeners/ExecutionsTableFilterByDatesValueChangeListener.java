package com.opentach.client.alert.listeners;

import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.jee.common.tools.DateTools;
import com.ontimize.jee.common.tools.ObjectTools;
import com.utilmize.client.gui.AbstractValueChangeListener;
import com.utilmize.client.gui.buttons.IUFormComponent;

public class ExecutionsTableFilterByDatesValueChangeListener extends AbstractValueChangeListener {

	@FormComponent(attr = "FILTERFECINI")
	protected DateDataField	fecIni;
	@FormComponent(attr = "FILTERFECFIN")
	protected DateDataField	fecFin;
	@FormComponent(attr = "FEC_FILTER")
	protected DataField		fecFilter;

	public ExecutionsTableFilterByDatesValueChangeListener(IUFormComponent formComponent, Hashtable params) throws Exception {
		super(formComponent, params);
	}

	@Override
	public void valueChanged(ValueEvent e) {
		if (ObjectTools.safeIsEquals(e.getOldValue(), e.getNewValue())) {
			return;
		}
		// Simple filter from two attrs in form (since - to) to single db column
		Object fromDate = this.fecIni.getValue();
		Object toDate = this.fecFin.getValue();
		if (toDate != null) {
			toDate = DateTools.lastMilisecond((Date) toDate);
		}

		SearchValue toSet = null;
		if ((fromDate != null) && (toDate != null)) {
			toSet = new SearchValue(SearchValue.BETWEEN, new Vector<>(Arrays.asList(new Object[] { fromDate, toDate })));
		} else if (fromDate != null) {
			toSet = new SearchValue(SearchValue.MORE_EQUAL, fromDate);
		} else if (toDate != null) {
			toSet = new SearchValue(SearchValue.LESS_EQUAL, toDate);
		}
		// Limit "useless" events
		if (!ObjectTools.safeIsEquals(this.fecFilter.getValue(), toSet)) {
			this.fecFilter.setValue(toSet);
		}
	}
}