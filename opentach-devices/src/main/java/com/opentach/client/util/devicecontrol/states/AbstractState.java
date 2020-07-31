package com.opentach.client.util.devicecontrol.states;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.client.util.devicecontrol.AbstractDeviceController;
import com.opentach.client.util.devicecontrol.StateFactory.StateFactoryType;
import com.opentach.client.util.net.NetworkEvent;
import com.opentach.client.util.upload.UploadEvent;
import com.opentach.client.util.usbkey.USBEvent;
import com.opentach.model.scard.CardEvent;

public abstract class AbstractState implements IState {

	private static final Logger				logger	= LoggerFactory.getLogger(AbstractState.class);

	private final AbstractDeviceController	controller;

	public AbstractState(AbstractDeviceController controller) {
		super();
		this.controller = controller;
	}

	protected IState getState(StateFactoryType type) {
		return this.controller.getStateFactory().getState(type);
	}

	public AbstractDeviceController getController() {
		return this.controller;
	}

	@Override
	public IState execute(EventObject e) {
		if (e instanceof CardEvent) {
			return this.executeCardEvent((CardEvent) e);
		} else if (e instanceof USBEvent) {
			return this.executeUSBEvent((USBEvent) e);
		} else if (e instanceof UploadEvent) {
			return this.executeUploadEvent((UploadEvent) e);
		} else if (e instanceof ActionEvent) {
			return this.executeActionEvent((ActionEvent) e);
		} else if (e instanceof NetworkEvent) {
			return this.executeNetworkEvent((NetworkEvent) e);
		}
		AbstractState.logger.error("event class {} not recogniced", e.getClass());
		return this;
	}

	public IState executeCardEvent(CardEvent e) {
		AbstractState.logger.debug("Ignoring CardEvent {}", e);
		return this;
	}

	public IState executeUSBEvent(USBEvent e) {
		AbstractState.logger.debug("Ignoring USBEvent {}", e);
		return this;
	}

	public IState executeUploadEvent(UploadEvent e) {
		AbstractState.logger.debug("Ignoring UploadEvent {}", e);
		return this;
	}

	public IState executeActionEvent(ActionEvent e) {
		AbstractState.logger.debug("Ignoring ActionEvent  {}", e);
		return this;
	}


	public IState executeNetworkEvent(NetworkEvent e) {
		AbstractState.logger.debug("Ignoring NetworkEvent  {}", e);
		return this;
	}

}