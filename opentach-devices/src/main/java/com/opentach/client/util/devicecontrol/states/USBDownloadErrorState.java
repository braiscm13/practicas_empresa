package com.opentach.client.util.devicecontrol.states;

import java.awt.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.client.util.devicecontrol.AbstractDeviceController;
import com.opentach.client.util.devicecontrol.StateFactory.StateFactoryType;

public class USBDownloadErrorState extends EndState {

	private static final Logger	logger	= LoggerFactory.getLogger(USBDownloadErrorState.class);

	public USBDownloadErrorState(AbstractDeviceController controller) {
		super(controller);
	}

	@Override
	public IState executeActionEvent(ActionEvent event) {
		USBDownloadErrorState.logger.info("Execute actionEvent: {}", event);
		boolean networkUp = this.getController().getNetworkStatus();
		if (networkUp) {
			return this.getState(StateFactoryType.INIT);
		}
		return this.getState(StateFactoryType.UNAVAILABLE);
	}

	@Override
	public void handle() {
		USBDownloadErrorState.logger.info("handle Error USB");
	}
}