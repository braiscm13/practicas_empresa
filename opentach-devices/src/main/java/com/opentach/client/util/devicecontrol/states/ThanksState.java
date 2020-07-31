package com.opentach.client.util.devicecontrol.states;

import java.awt.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.client.util.devicecontrol.AbstractDeviceController;
import com.opentach.client.util.devicecontrol.StateFactory.StateFactoryType;

public class ThanksState extends EndState {

	private static final Logger	logger	= LoggerFactory.getLogger(ThanksState.class);
	public ThanksState(AbstractDeviceController controller) {
		super(controller);
	}

	@Override
	public IState executeActionEvent(ActionEvent event) {
		ThanksState.logger.info("Execute actionEvent: {}", event);
		boolean networkUp = this.getController().getNetworkStatus();
		if (networkUp) {
			return this.getState(StateFactoryType.INIT);
		}
		return this.getState(StateFactoryType.UNAVAILABLE);
	}

	@Override
	public void handle() {
		ThanksState.logger.info("handle Gracias por usar este servicio");
		super.handle();
		this.getController().setTimerToValue(AbstractDeviceController.PAUSEMILLIS);
		this.getController().thanks();
	}
}