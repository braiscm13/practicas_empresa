package com.opentach.client.util.devicecontrol.states;

import java.awt.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.client.util.devicecontrol.AbstractDeviceController;
import com.opentach.client.util.devicecontrol.StateFactory.StateFactoryType;


public class USBUploadErrorState extends EndState {

	private static final Logger	logger	= LoggerFactory.getLogger(USBUploadErrorState.class);
	public USBUploadErrorState(AbstractDeviceController controller) {
		super(controller);
	}

	@Override
	public IState executeActionEvent(ActionEvent event) {
		USBUploadErrorState.logger.info("Execute actionEvent: {}", event);
		// this.getController().setTimerToValue(AbstractDeviceController.PRINTMILLIS);
		this.getController().setTimerToValue(0);
		return this.getState(StateFactoryType.INIT);
	}

	@Override
	public void handle() {
		USBUploadErrorState.logger.info("handle Error upload");
		this.getController().setTimerToValue(AbstractDeviceController.PAUSEMILLIS);
	}
}