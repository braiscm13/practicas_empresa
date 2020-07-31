package com.opentach.client.util.devicecontrol.states;

import java.util.EventObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.client.util.devicecontrol.AbstractDeviceController;
import com.opentach.client.util.printer.PrintEvent;
import com.opentach.client.util.screenreport.ReportEvent;

public abstract class AbstractDownCenterState extends AbstractState {

	private static final Logger	logger	= LoggerFactory.getLogger(AbstractDownCenterState.class);

	public AbstractDownCenterState(AbstractDeviceController controller) {
		super(controller);
	}


	@Override
	public IState execute(EventObject e) {
		if (e instanceof PrintEvent) {
			return this.executePrintEvent((PrintEvent) e);
		} else if (e instanceof ReportEvent) {
			return this.executeReportEvent((ReportEvent) e);
		} else {
			return super.execute(e);
		}
	}

	public IState executePrintEvent(PrintEvent e) {
		AbstractDownCenterState.logger.debug("Ignoring PrintEvent  {}", e);
		return this;
	}

	public IState executeReportEvent(ReportEvent e) {
		AbstractDownCenterState.logger.debug("Ignoring ReportEvent  {}", e);
		return this;
	}
}
