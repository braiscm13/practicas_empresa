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

public class IMEmployeeGeneratePasswordActionListener extends AbstractActionListenerButton {

	private static final Logger logger = LoggerFactory.getLogger(IMEmployeeGeneratePasswordActionListener.class);

	public IMEmployeeGeneratePasswordActionListener() throws Exception {
		super();
	}

	public IMEmployeeGeneratePasswordActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public IMEmployeeGeneratePasswordActionListener(Hashtable params) throws Exception {
		super(params);
	}

	public IMEmployeeGeneratePasswordActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		try {
			ReferenceLocator referenceLocator = (ReferenceLocator) this.getFormManager().getReferenceLocator();
			IConductoresEmp entity = (IConductoresEmp) referenceLocator.getEntityReference(EmployeeNaming.ENTITY);
			String pass = entity.generateWebPassword(8);

			this.getForm().setDataFieldValue("WEB_PASSWORD", pass);
		} catch (Exception exception) {
			IMEmployeeGeneratePasswordActionListener.logger.error("EMPLOYEEDATA.EXCEPTION", exception);
		}
	}
}
