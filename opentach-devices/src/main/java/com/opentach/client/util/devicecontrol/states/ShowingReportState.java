package com.opentach.client.util.devicecontrol.states;

import java.awt.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.client.util.devicecontrol.AbstractDeviceController;
import com.opentach.client.util.devicecontrol.StateFactory.StateFactoryType;

public class ShowingReportState extends BasicState {

	private static final Logger	logger	= LoggerFactory.getLogger(ShowingReportState.class);

	public ShowingReportState(AbstractDeviceController controller) {
		super(controller);
	}

	@Override
	public IState executeActionEvent(ActionEvent event) {
		ShowingReportState.logger.info("Execute actionEvent: {}", event);
		this.getController().setTimerToValue(AbstractDeviceController.PAUSEMILLIS);
		return this.getState(StateFactoryType.THANKS);
	}

	@Override
	public void handle() {
		ShowingReportState.logger.info("handle Showing report");
	}

}