package com.opentach.client.util.devicecontrol.states;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.client.util.devicecontrol.AbstractDeviceController;
import com.opentach.client.util.devicecontrol.StateFactory.StateFactoryType;
import com.opentach.model.scard.CardEvent;
import com.opentach.model.scard.CardEvent.CardEventType;


public class CardInsertedState extends BasicState {

	private static final Logger	logger	= LoggerFactory.getLogger(CardInsertedState.class);
	public CardInsertedState(AbstractDeviceController controller) {
		super(controller);
	}

	@Override
	public IState executeCardEvent(CardEvent e) {
		CardEventType cet = e.getType();
		this.getController().setAutoDownloadCard(false);
		this.getController().setAutoDownloadKey(false);
		if (cet == CardEventType.CARD_DOWNLOAD_START) {
			CardInsertedState.logger.info("Card downloading start");
			this.getController().startCardDownload();
			return this.getState(StateFactoryType.CARDCOPYING);
		} else if (cet == CardEventType.CARD_ERROR) {
			this.getController().setTimerToValue(AbstractDeviceController.PAUSEMILLIS);
			this.getController().cardError(e.getMessage());
			return this.getState(StateFactoryType.CARDERROR);
		} else if (cet == CardEventType.CARD_REMOVED) {
			this.getController().setTimerToValue(AbstractDeviceController.PAUSEMILLIS);
			this.getController().cardError(e.getMessage());
			return this.getState(StateFactoryType.CARDERROR);
		} else {
			return super.executeCardEvent(e);
		}
	}

	@Override
	public void handle() {
		this.getController().pauseUploadMonitor();
		this.getController().setCardDownloading(true);
	}
}