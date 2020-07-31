package com.opentach.client.employee.listeners;

import java.util.Hashtable;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.DataField;
import com.ontimize.jee.common.tools.StringTools;
import com.opentach.client.util.DriverUtil;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.company.beans.EmployeeType;
import com.utilmize.client.gui.AbstractValueChangeListener;
import com.utilmize.client.gui.buttons.IUFormComponent;

public class AutocompleteIdConductorFromNIFValueChangeListener extends AbstractValueChangeListener {

	@FormComponent(attr = OpentachFieldNames.IDCONDUCTOR_FIELD)
	protected DataField idConductorField;

	@FormComponent(attr = OpentachFieldNames.DNI_FIELD)
	protected DataField	cifField;

	@FormComponent(attr = "TYPE")
	protected DataField	employeeTypeField;

	public AutocompleteIdConductorFromNIFValueChangeListener(IUFormComponent formComponent, Hashtable params) throws Exception {
		super(formComponent, params);
	}

	@Override
	public void valueChanged(ValueEvent e) {
		if (this.getInteractionManager().getCurrentMode() != InteractionManager.INSERT) {
			return; // Do not change IDCONDUCTOR_FIELD never after inserted
		}
		if (this.getUBasicInteractionManager().isEstablishingFormValues()/* e.getType() != ValueEvent.USER_CHANGE */) {
			return; // Ignore events when inserting detail
		}

		final String nv = (String) this.cifField.getValue();
		if (StringTools.isEmpty(nv)) {
			return; // ignore when empty CIF
		}

		// Differ from two cases:
		final boolean employeeHasTachograph = (this.employeeTypeField.getValue() == null) || (((Number) this.employeeTypeField.getValue())
				.intValue() == EmployeeType.DRIVER_WITH_TACHOGRAPH.toId());
		if (employeeHasTachograph) {
			// A) "DRIVER WITH TACOGRAPH": (5B available to insert, only autocompleted for spanish driver when valid NIF)
			if (DriverUtil.checkValidCIFNIF(this.getForm(), nv, false)) {
				this.idConductorField.setValue("E" + nv + "0000");
			}
		} else {
			// B) "DRIVER WITHOUT TACOGRAPH": (5B automatic, autocompleted always)
			this.idConductorField.setValue("_" + StringTools.replicate("0", 9 - nv.length()) + nv + "____");
		}
	}
}