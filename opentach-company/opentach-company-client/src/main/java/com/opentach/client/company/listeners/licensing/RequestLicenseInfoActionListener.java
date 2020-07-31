package com.opentach.client.company.listeners.licensing;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.tools.MessageType;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.common.company.exception.LicenseException;
import com.utilmize.client.gui.buttons.UButton;

public class RequestLicenseInfoActionListener extends AbstractLicenseActionListener {

	/** The CONSTANT logger */
	private static final Logger logger = LoggerFactory.getLogger(RequestLicenseInfoActionListener.class);

	public RequestLicenseInfoActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			// Send an email to Openservices identifying company requesting more info about this license
			this.getLicenseService().requestLicenseInfo(this.getCIF(), this.getLicenseAttr());
			// Notify user that Openservices will contact them as soon as possible
			MessageManager.getMessageManager().showMessage(this.getForm(), "M_REQUESTING_MORE_LICENSE_INFO_OK", MessageType.INFORMATION, new Object[] {});
		} catch (LicenseException err) {
			MessageManager.getMessageManager().showExceptionMessage(err, this.getForm(), RequestLicenseInfoActionListener.logger, "E_REQUESTING_MORE_LICENSE_INFO");
		}
	}
}
