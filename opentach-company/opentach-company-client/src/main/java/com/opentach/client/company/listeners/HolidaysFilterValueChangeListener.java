package com.opentach.client.company.listeners;

import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.InteractionManagerModeEvent;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.IntegerDataField;
import com.ontimize.gui.field.TextComboDataField;
import com.utilmize.client.gui.AbstractValueChangeListener;
import com.utilmize.client.gui.buttons.IUFormComponent;

public class HolidaysFilterValueChangeListener extends AbstractValueChangeListener {

	/** The CONSTANT logger */
	private static final Logger	logger	= LoggerFactory.getLogger(HolidaysFilterValueChangeListener.class);

	@FormComponent(attr = "SHOW_APPROVED")
	protected IntegerDataField	showApprovedHolidays;

	@FormComponent(attr = "SHOW_DISMISSED")
	protected IntegerDataField	showDismissedHolidays;

	public HolidaysFilterValueChangeListener(IUFormComponent formComponent, Hashtable params) throws Exception {
		super(formComponent, params);
	}

	@Override
	public void interactionManagerModeChanged(InteractionManagerModeEvent e) {
		super.interactionManagerModeChanged(e);
		if (e.getInteractionManagerMode() == InteractionManager.UPDATE) {
			((TextComboDataField) this.getFormComponent()).setSelected(0);
		}
	}

	@Override
	public void valueChanged(ValueEvent valueEvent) {
		String newValue = (String) valueEvent.getNewValue();
		if (newValue != null) {
			switch (newValue) {
				case "PENDING_APPROVAL_HOLIDAYS":
					this.showApprovedHolidays.setValue(0);
					this.showDismissedHolidays.setValue(0);
					break;
				case "APPROVED_HOLIDAYS":
					this.showApprovedHolidays.setValue(1);
					this.showDismissedHolidays.setValue(0);
					break;
				case "DISMISSED_HOLIDAYS":
					this.showApprovedHolidays.setValue(0);
					this.showDismissedHolidays.setValue(1);
					break;
				default:
					HolidaysFilterValueChangeListener.logger.warn("Unsupported holiday type selection");
			}
		}
	}
}