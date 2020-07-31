package com.opentach.client.util.devicecontrol;

import com.opentach.client.MonitorProvider;
import com.opentach.client.util.printer.TicketPrinter;

public interface IDownCenterMonitorProvider extends MonitorProvider {
	public TicketPrinter getTicketPrinter();

}
