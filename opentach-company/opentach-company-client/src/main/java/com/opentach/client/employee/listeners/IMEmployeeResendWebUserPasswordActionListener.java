package com.opentach.client.employee.listeners;

import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.stream.Stream;

import javax.swing.AbstractButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.tools.MessageType;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.locator.ReferenceLocator;
import com.opentach.common.employee.naming.EmployeeNaming;
import com.opentach.common.employee.services.IConductoresEmp;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;

public class IMEmployeeResendWebUserPasswordActionListener extends AbstractActionListenerButton {

	private static final Logger logger = LoggerFactory.getLogger(IMEmployeeResendWebUserPasswordActionListener.class);

	public IMEmployeeResendWebUserPasswordActionListener() throws Exception {
		super();
	}

	public IMEmployeeResendWebUserPasswordActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public IMEmployeeResendWebUserPasswordActionListener(Hashtable params) throws Exception {
		super(params);
	}

	public IMEmployeeResendWebUserPasswordActionListener(UButton button, Hashtable params) throws Exception {
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
			String webUser = this.getForm().getDataFieldText(EmployeeNaming.WEB_USER).trim();
			String webPass = this.getForm().getDataFieldText(EmployeeNaming.WEB_PASSWORD).trim();
			String email = this.getForm().getDataFieldText(EmployeeNaming.EMAIL).trim();

			if (Stream.of(webUser, webPass, email).anyMatch(string -> (string == null) || string.isEmpty())) {
				MessageManager.getMessageManager().showMessage( //
						this.getForm(), //
						"EMPLOYEEDATA.NO_WEB_USER_PASS_EMAIL_DEFINED", //
						MessageType.WARNING, //
						new Object[] {}); //

			} else {
				entity.emailWebAuth(cif, employee, sessionId);
				MessageManager.getMessageManager().showMessage( //
						this.getForm(), //
						"EMPLOYEEDATA.WEB_ACCESS_PARAMETERS_SENT_TO_USER", //
						MessageType.INFORMATION, //
						new Object[] {}); //
			}
		} catch (Exception exception) {
			IMEmployeeResendWebUserPasswordActionListener.logger.error("EMPLOYEEDATA.EXCEPTION", exception);
		}

	}

}
