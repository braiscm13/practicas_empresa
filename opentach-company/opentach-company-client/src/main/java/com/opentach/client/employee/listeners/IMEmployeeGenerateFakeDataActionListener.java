package com.opentach.client.employee.listeners;

import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.Hashtable;

import javax.swing.AbstractButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.field.DataField;
import com.ontimize.locator.ReferenceLocator;
import com.opentach.common.employee.naming.EmployeeNaming;
import com.opentach.common.employee.services.IConductoresEmp;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;

public class IMEmployeeGenerateFakeDataActionListener extends AbstractActionListenerButton {

	private static final Logger logger = LoggerFactory.getLogger(IMEmployeeGenerateFakeDataActionListener.class);

	public IMEmployeeGenerateFakeDataActionListener() throws Exception {
		super();
	}

	public IMEmployeeGenerateFakeDataActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public IMEmployeeGenerateFakeDataActionListener(Hashtable params) throws Exception {
		super(params);
	}

	public IMEmployeeGenerateFakeDataActionListener(UButton button, Hashtable params) throws Exception {
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
			DataField fakeDataSince = (DataField) this.getForm().getDataFieldReference("FAKEDATAFECINI");
			DataField fakeDataUntil = (DataField) this.getForm().getDataFieldReference("FAKEDATAFECFIN");

			Date since = (Date) fakeDataSince.getValue();
			Date until = (Date) fakeDataUntil.getValue();

			if ((since != null) && (until != null)) {
				entity.generateFakeData(sessionId, cif, employee, since, until);
			}
		} catch (Exception exception) {
			IMEmployeeGenerateFakeDataActionListener.logger.error("EMPLOYEEDATA.EXCEPTION", exception);
		}
	}
}
