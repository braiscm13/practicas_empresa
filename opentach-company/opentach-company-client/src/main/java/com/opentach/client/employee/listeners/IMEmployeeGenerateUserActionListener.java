package com.opentach.client.employee.listeners;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import javax.swing.AbstractButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.locator.ReferenceLocator;
import com.opentach.common.employee.naming.EmployeeNaming;
import com.opentach.common.employee.services.IConductoresEmp;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;

public class IMEmployeeGenerateUserActionListener extends AbstractActionListenerButton {

	private static final Logger logger = LoggerFactory.getLogger(IMEmployeeGenerateUserActionListener.class);

	public IMEmployeeGenerateUserActionListener() throws Exception {
		super();
	}

	public IMEmployeeGenerateUserActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public IMEmployeeGenerateUserActionListener(Hashtable params) throws Exception {
		super(params);
	}

	public IMEmployeeGenerateUserActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		try {
			ReferenceLocator referenceLocator = (ReferenceLocator) this.getFormManager().getReferenceLocator();
			IConductoresEmp entity = (IConductoresEmp) referenceLocator.getEntityReference(EmployeeNaming.ENTITY);
			int sessionId = referenceLocator.getSessionId();

			String cif = (String) this.getForm().getDataFieldValue(EmployeeNaming.CIF);
			String employee = this.getForm().getDataFieldText(EmployeeNaming.IDCONDUCTOR);
			String firstName = this.getForm().getDataFieldText(EmployeeNaming.NOMBRE);
			String secondName = this.getForm().getDataFieldText(EmployeeNaming.APELLIDOS);
			String userName = entity.generateWebUserName(sessionId, cif, employee, firstName, secondName);

			this.getForm().setDataFieldValue("WEB_USER", userName);
		} catch (Exception exception) {
			IMEmployeeGenerateUserActionListener.logger.error("EMPLOYEEDATA.EXCEPTION", exception);
		}
	}
}
