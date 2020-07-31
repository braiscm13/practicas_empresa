package com.opentach.client.util.devicecontrol.states;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.client.util.devicecontrol.AbstractDeviceController;
import com.opentach.client.util.devicecontrol.StateFactory.StateFactoryType;
import com.opentach.model.scard.CardEvent;
import com.opentach.model.scard.CardEvent.CardEventType;

public class CardCopyingState extends BasicState {

	private static final Logger	logger	= LoggerFactory.getLogger(CardCopyingState.class);
	public CardCopyingState(AbstractDeviceController controller) {
		super(controller);
	}

	@Override
	public IState executeCardEvent(CardEvent event) {
		CardCopyingState.logger.info("Executing cardEvent {}", event);
		CardEventType cet = event.getType();
		if (cet == CardEventType.CARD_DOWNLOAD_END) {
			CardCopyingState.logger.info("Card downloading end");
			this.getController().endCardDownload();
			File f = event.getFile();
			try {
				this.getController().uploadFile(f);
			} catch (Exception ex) {
				CardCopyingState.logger.error(null, ex);
				this.getController().setTimerToValue(AbstractDeviceController.PAUSEMILLIS);
				return this.getState(StateFactoryType.CARDERROR);
			}
			return this.getState(StateFactoryType.CARDUPLOADING);
		} else if (cet == CardEventType.CARD_ERROR) {
			this.getController().setTimerToValue(AbstractDeviceController.PAUSEMILLIS);
			this.getController().cardError(event.getMessage());
			return this.getState(StateFactoryType.CARDERROR);
		} else if (cet == CardEventType.CARD_REMOVED) {
			this.getController().setTimerToValue(AbstractDeviceController.PAUSEMILLIS);
			return this.getState(StateFactoryType.CARDERROR);
		} else if (cet == CardEventType.CARD_DOWNLOAD_PROGRESS) {
			this.getController().downloadProgress(event.getMessage(), event.getMsgParameters(), event.getCurrentPart(), event.getTotalParts());
			return null;
		} else {
			return super.executeCardEvent(event);
		}
	}
}