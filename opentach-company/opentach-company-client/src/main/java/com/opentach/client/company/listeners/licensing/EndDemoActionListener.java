package com.opentach.client.company.listeners.licensing;

import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.tools.Pair;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.common.company.exception.LicenseException;
import com.utilmize.client.gui.buttons.UButton;

public class EndDemoActionListener extends AbstractLicenseActionListener {

	/** The CONSTANT logger */
	private static final Logger logger = LoggerFactory.getLogger(EndDemoActionListener.class);

	public EndDemoActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			// Check prerequisites (company email, duration?, ...)
			if (!this.confirmAction("Q_SURE_END_COMPANY_DEMO")) {
				return;
			}
			if (!this.checkCompanyEmail()) {
				return;
			}
			Pair<Date, Date> dates = this.askForDates(true, 0);
			if (dates == null) {
				return;
			}

			this.getLicenseService().endDemo(this.getCIF(), this.getLicenseAttr(), dates.getSecond());
			this.reloadRecordData();
		} catch (LicenseException err) {
			MessageManager.getMessageManager().showExceptionMessage(err, this.getForm(), EndDemoActionListener.logger, "E_FINISHING_DEMO");
		}
	}

	@Override
	protected boolean getEnableValueToSet() {
		return super.getEnableValueToSet() && this.isDemoActive();
	}

}
