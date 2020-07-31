package com.opentach.client.util.devicecontrol.states;

import java.awt.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.client.util.devicecontrol.AbstractDeviceController;
import com.opentach.client.util.devicecontrol.StateFactory.StateFactoryType;

public abstract class BasicState extends AbstractState {

	private static final Logger	logger	= LoggerFactory.getLogger(BasicState.class);
	public BasicState(AbstractDeviceController controller) {
		super(controller);
	}

	@Override
	public IState executeActionEvent(ActionEvent e) {
		BasicState.logger.info("execute ActionEvent in BasicState->gotoinit");
		return this.getState(StateFactoryType.INIT);
	}


	@Override
	public void handle() {

	}
}