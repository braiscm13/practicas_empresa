package com.opentach.client.util.devicecontrol.states;

import java.awt.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.client.util.devicecontrol.AbstractDeviceController;
import com.opentach.client.util.devicecontrol.StateFactory.StateFactoryType;

public class CardUploadErrorState extends EndState {

	private static final Logger	logger	= LoggerFactory.getLogger(CardUploadErrorState.class);

	public CardUploadErrorState(AbstractDeviceController controller) {
		super(controller);
	}

	@Override
	public IState executeActionEvent(ActionEvent e) {
		// this.getController().setTimerToValue(AbstractDeviceController.PRINTMILLIS);
		this.getController().setTimerToValue(0);
		return this.getState(StateFactoryType.INIT);
	}

	@Override
	public void handle() {
		CardUploadErrorState.logger.warn("Error upload");
	}
}