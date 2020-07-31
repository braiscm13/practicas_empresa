package com.opentach.client.util.devicecontrol.states;

import java.awt.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.client.util.devicecontrol.AbstractDeviceController;
import com.opentach.client.util.devicecontrol.StateFactory.StateFactoryType;
import com.opentach.client.util.net.NetworkEvent;
import com.opentach.client.util.net.NetworkEvent.NetworkEventType;

public class UnavailableState extends AbstractState {
	protected static final int	NETWORKCHECKMILLIS	= 10000;

	private static final Logger	logger	= LoggerFactory.getLogger(UnavailableState.class);

	public UnavailableState(AbstractDeviceController controller) {
		super(controller);
	}

	@Override
	public IState executeNetworkEvent(NetworkEvent event) {
		UnavailableState.logger.info("Execute networkEvent: {}", event);
		NetworkEventType net = event.getType();
		if (net == NetworkEventType.NETWORK_UP) {
			return this.getState(StateFactoryType.INIT);
		}
		return super.executeNetworkEvent(event);
	}

	@Override
	public IState executeActionEvent(ActionEvent event) {
		UnavailableState.logger.info("Execute actionEvent: {}", event);
		boolean networkUp = this.getController().getNetworkStatus();
		if (networkUp) {
			return this.getState(StateFactoryType.INIT);
		}
		return this.getState(StateFactoryType.UNAVAILABLE);
	}

	@Override
	public void handle() {
		UnavailableState.logger.info("handle Service unavailable");
		this.getController().setTimerToValue(UnavailableState.NETWORKCHECKMILLIS);
		this.getController().setAutoDownloadCard(false);
		this.getController().setAutoDownloadKey(false);
		this.getController().unavailableService();
	}
}