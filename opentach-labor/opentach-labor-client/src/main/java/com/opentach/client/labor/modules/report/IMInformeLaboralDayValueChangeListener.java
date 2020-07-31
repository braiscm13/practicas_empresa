package com.opentach.client.labor.modules.report;

import java.awt.event.FocusEvent;
import java.util.Hashtable;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.field.HourDateDataField;
import com.ontimize.gui.field.IntegerDataField;
import com.ontimize.gui.field.TextComboDataField;
import com.ontimize.jee.common.tools.DateTools;
import com.ontimize.jee.common.tools.ObjectTools;
import com.utilmize.client.gui.AbstractFocusChangeListener;
import com.utilmize.client.gui.buttons.IUFormComponent;

public class IMInformeLaboralDayValueChangeListener extends AbstractFocusChangeListener {

	@FormComponent(attr = "MAJ_DAY_YEAR")
	private IntegerDataField	yearField;
	@FormComponent(attr = "MAJ_DAY_MONTH")
	private TextComboDataField	monthField;
	@FormComponent(attr = "MAJ_DAY_DAY")
	private IntegerDataField	dayField;

	@FormComponent(attr = "MAJ_BEGINDATE")
	private HourDateDataField	beginDateField;
	@FormComponent(attr = "MAJ_ENDDATE")
	private HourDateDataField	endDateField;

	public IMInformeLaboralDayValueChangeListener(IUFormComponent formComponent, Hashtable params) throws Exception {
		super(formComponent, params);
	}

	@Override
	public void focusGained(FocusEvent e) {

	}

	@Override
	public void focusLost(FocusEvent e) {
		if (!ObjectTools.isIn(null, this.yearField.getValue(), this.monthField.getValue(), this.dayField.getValue())) {
			if (this.beginDateField.getValue() == null) {
				this.beginDateField.setValue(DateTools.createCalendar(this.yearField.getNumericalValue().intValue(), Integer.valueOf((String) this.monthField.getValue()) - 1,
						this.dayField.getNumericalValue().intValue()).getTime());
			}
			if (this.endDateField.getValue() == null) {
				this.endDateField.setValue(DateTools.createCalendar(this.yearField.getNumericalValue().intValue(), Integer.valueOf((String) this.monthField.getValue()) - 1,
						this.dayField.getNumericalValue().intValue()).getTime());
			}
		}
	}
}
