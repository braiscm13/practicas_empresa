package com.opentach.client.util.devicecontrol.states;

import java.awt.event.ActionEvent;

import com.opentach.client.util.devicecontrol.AbstractDeviceController;
import com.opentach.client.util.devicecontrol.StateFactory.StateFactoryType;
import com.opentach.model.scard.CardEvent;

public class CardUploadedState extends BasicState {

	public CardUploadedState(AbstractDeviceController controller) {
		super(controller);
	}

	@Override
	public IState executeActionEvent(ActionEvent e) {
		this.getController().setTimerToValue(AbstractDeviceController.PRINTMILLIS);
		return this.getState(StateFactoryType.PRINT);
	}

	@Override
	public IState executeCardEvent(CardEvent e) {
		return super.executeCardEvent(e);
	}
}