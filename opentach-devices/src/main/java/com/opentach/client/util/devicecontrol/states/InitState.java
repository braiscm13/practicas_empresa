package com.opentach.client.util.devicecontrol.states;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.client.util.devicecontrol.AbstractDeviceController;
import com.opentach.client.util.devicecontrol.StateFactory.StateFactoryType;
import com.opentach.client.util.net.NetworkEvent;
import com.opentach.client.util.net.NetworkEvent.NetworkEventType;
import com.opentach.client.util.usbkey.USBEvent;
import com.opentach.client.util.usbkey.USBEvent.USBEventType;
import com.opentach.model.scard.CardEvent;
import com.opentach.model.scard.CardEvent.CardEventType;

public class InitState extends BasicState {

	protected static final int	MAXMILLIS	= 240000;
	private static final Logger	logger	= LoggerFactory.getLogger(InitState.class);

	public InitState(AbstractDeviceController controller) {
		super(controller);
	}

	@Override
	public IState executeCardEvent(CardEvent e) {
		CardEventType cet = e.getType();
		if (cet == CardEventType.CARD_INSERTED) {
			InitState.logger.info("Card inserted");
			this.getController().setTimerToValue(InitState.MAXMILLIS);
			return this.getState(StateFactoryType.CARDINSERTED);
		}
		return super.executeCardEvent(e);
	}

	@Override
	public IState executeUSBEvent(USBEvent e) {
		USBEventType uet = e.getType();
		if (uet == USBEventType.USB_DOWNLOAD_START) {
			InitState.logger.info("USB inserted");
			this.getController().setTimerToValue(InitState.MAXMILLIS);
			return this.getState(StateFactoryType.USBSTART);
		} else if (uet == USBEventType.USB_NOT_FOUND) {
			InitState.logger.info("USB not found");
			this.getController().onUsbNotFound();
			return this.getState(StateFactoryType.USB_NOT_FOUND);
		}
		return super.executeUSBEvent(e);
	}

	@Override
	public IState executeNetworkEvent(NetworkEvent e) {
		if (e.getType() == NetworkEventType.NETWORK_DOWN) {
			return this.getState(StateFactoryType.UNAVAILABLE);
		}
		return super.executeNetworkEvent(e);
	}

	@Override
	public void handle() {
		InitState.logger.info("Init state");
		this.getController().init();
		this.getController().setTimerToValue(0);
		this.getController().setAutoDownloadCard(true);
		this.getController().setAutoDownloadKey(true);
		this.getController().setCardDownloading(false);
		this.getController().setKeyDownloading(false);
	}

}