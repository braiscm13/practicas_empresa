package com.opentach.client.util.devicecontrol.states;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.client.util.devicecontrol.AbstractDeviceController;

public abstract class EndState extends AbstractState {

	private static final Logger	logger	= LoggerFactory.getLogger(EndState.class);
	public EndState(AbstractDeviceController controller) {
		super(controller);
	}

	@Override
	public void handle() {
		EndState.logger.info("handle endstate");
		this.getController().restartUploadMonitor();
	}
}