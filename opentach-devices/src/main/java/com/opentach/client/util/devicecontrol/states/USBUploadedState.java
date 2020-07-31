package com.opentach.client.util.devicecontrol.states;

import java.awt.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.client.util.devicecontrol.AbstractDeviceController;
import com.opentach.client.util.devicecontrol.StateFactory.StateFactoryType;


public class USBUploadedState extends BasicState {

	private static final Logger	logger	= LoggerFactory.getLogger(USBUploadedState.class);

	public USBUploadedState(AbstractDeviceController controller) {
		super(controller);
	}

	@Override
	public IState executeActionEvent(ActionEvent event) {
		USBUploadedState.logger.info("Execute actionEvent: {}", event);
		this.getController().setTimerToValue(AbstractDeviceController.PRINTMILLIS);
		return this.getState(StateFactoryType.PRINT);
	}
}