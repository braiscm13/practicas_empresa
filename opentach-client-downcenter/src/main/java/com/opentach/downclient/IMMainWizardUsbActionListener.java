package com.opentach.downclient;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import com.opentach.client.MonitorProvider;
import com.opentach.client.util.usbkey.USBKeyMonitor;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;

public class IMMainWizardUsbActionListener extends AbstractActionListenerButton {


	public IMMainWizardUsbActionListener(IUFormComponent formComponent, Hashtable params) throws Exception {
		super(null, formComponent, params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		MonitorProvider mp = (MonitorProvider) this.getReferenceLocator();
		USBKeyMonitor usbmon = mp.getUSBKeyMonitor();
		usbmon.downloadUSBKeyFiles(null);
	}

}
