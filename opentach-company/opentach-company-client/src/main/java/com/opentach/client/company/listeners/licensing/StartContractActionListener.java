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

public class StartContractActionListener extends AbstractLicenseActionListener {

	/** The CONSTANT logger */
	private static final Logger logger = LoggerFactory.getLogger(StartContractActionListener.class);

	public StartContractActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			// Check prerequisites (company email, duration?, ...)
			if (!this.checkCompanyContract()) {
				return;
			}
			if (!this.checkCompanyEmail()) {
				return;
			}
			Pair<Date, Date> dates = this.askForDates(false, 365);
			if (dates == null) {
				return;
			}

			this.getLicenseService().startContract(this.getCIF(), this.getLicenseAttr(), dates.getFirst(), dates.getSecond());
			this.reloadRecordData();
		} catch (LicenseException err) {
			MessageManager.getMessageManager().showExceptionMessage(err, this.getForm(), StartContractActionListener.logger, "E_STARTING_CONTRACT");
		}
	}

	@Override
	protected boolean getEnableValueToSet() {
		return super.getEnableValueToSet() && !this.isLicensed();
	}

}
