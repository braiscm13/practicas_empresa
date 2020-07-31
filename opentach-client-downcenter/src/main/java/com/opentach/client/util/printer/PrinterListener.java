package com.opentach.client.util.printer;

import java.util.EventListener;

public interface PrinterListener extends EventListener {

	public void printAction(PrintEvent e);

}
