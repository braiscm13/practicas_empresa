package com.opentach.client.util.devicecontrol.states;

import java.awt.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.client.util.devicecontrol.AbstractDeviceController;
import com.opentach.client.util.devicecontrol.StateFactory.StateFactoryType;

public class USBNotFoundState extends BasicState {

	private static final Logger	logger	= LoggerFactory.getLogger(USBNotFoundState.class);

	public USBNotFoundState(AbstractDeviceController controller) {
		super(controller);
	}

	@Override
	public IState executeActionEvent(ActionEvent event) {
		USBNotFoundState.logger.info("Execute actionEvent: {}", event);
		return this.getState(StateFactoryType.INIT);
	}

	@Override
	public void handle() {
		USBNotFoundState.logger.info("handle Error USB not found");
	}
}