package com.opentach.client.util.usbkey;

import java.util.EventListener;

public interface USBListener extends EventListener {

	public void usbStatusChange(USBEvent ce);

}
