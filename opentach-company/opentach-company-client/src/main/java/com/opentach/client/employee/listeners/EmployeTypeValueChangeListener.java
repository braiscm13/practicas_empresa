package com.opentach.client.employee.listeners;

import java.util.Hashtable;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.InteractionManagerModeEvent;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.DataField;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.company.beans.EmployeeType;
import com.utilmize.client.gui.AbstractValueChangeListener;
import com.utilmize.client.gui.buttons.IUFormComponent;

public class EmployeTypeValueChangeListener extends AbstractValueChangeListener {

	@FormComponent(attr = "TYPE")
	DataField	employeeType;

	@FormComponent(attr = OpentachFieldNames.IDCONDUCTOR_FIELD)
	DataField	idconductor;

	public EmployeTypeValueChangeListener(IUFormComponent formComponent, Hashtable params) throws Exception {
		super(formComponent, params);
	}

	@Override
	public void valueChanged(ValueEvent e) {
		this.updateEmployeTypeLogic();
	}

	@Override
	public void interactionManagerModeChanged(InteractionManagerModeEvent e) {
		super.interactionManagerModeChanged(e);
		this.updateEmployeTypeLogic();
	}

	private void updateEmployeTypeLogic() {
		boolean employeeHasTachograph = true;
		if (this.employeeType.getValue() != null) {
			final int employeeTypeValue = ((Number) this.employeeType.getValue()).intValue();
			employeeHasTachograph = employeeTypeValue == EmployeeType.DRIVER_WITH_TACHOGRAPH.toId();
		}

		String idFieldCaption = ApplicationManager.getTranslation(employeeHasTachograph ? "EMPLOYEEDATA.IDIS5B" : "EMPLOYEEDATA.IDISID");
		String idFieldToolTip = ApplicationManager.getTranslation(employeeHasTachograph ? "EMPLOYEEDATA.IDIS5BTIP" : "EMPLOYEEDATA.IDISIDTIP");

		this.idconductor.setEnabled(employeeHasTachograph && (this.getInteractionManager().getCurrentMode() == InteractionManager.INSERT));
		this.idconductor.setToolTipText(idFieldToolTip);
		this.idconductor.getLabelComponent().setText(idFieldCaption);
		this.idconductor.getLabelComponent().setToolTipText(idFieldToolTip);
	}
}