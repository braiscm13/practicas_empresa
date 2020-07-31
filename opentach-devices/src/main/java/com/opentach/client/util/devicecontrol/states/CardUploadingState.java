package com.opentach.client.util.devicecontrol.states;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.client.util.devicecontrol.AbstractDeviceController;
import com.opentach.client.util.devicecontrol.StateFactory.StateFactoryType;
import com.opentach.client.util.upload.UploadEvent;
import com.opentach.client.util.upload.UploadEvent.UploadEventType;

public class CardUploadingState extends BasicState {

	private static final Logger	logger	= LoggerFactory.getLogger(CardUploadingState.class);
	public CardUploadingState(AbstractDeviceController controller) {
		super(controller);
	}

	@Override
	public IState executeUploadEvent(UploadEvent event) {
		CardUploadingState.logger.info("Execute uploadEvent {}", event);
		UploadEventType uet = event.getType();
		if (uet == UploadEventType.FILE_UPLOAD_END) {
			this.getController().uploadFinished(event.getFiles());
			if (this.getController().isWithPrintBranch()) {
				this.getController().setTimerToValue(AbstractDeviceController.PAUSEMILLIS);
				return this.getState(StateFactoryType.CARDUPLOADED);
			}
			boolean networkUp = this.getController().getNetworkStatus();
			if (networkUp) {
				return this.getState(StateFactoryType.INIT);
			}
			return this.getState(StateFactoryType.UNAVAILABLE);
		} else if ((uet == UploadEventType.CONTRACT_ERROR) || (uet == UploadEventType.DRIVER_VEH_ERROR) || (uet == UploadEventType.FILE_UPLOAD_ERROR)) {
			this.getController().setTimerToValue(AbstractDeviceController.PAUSEMILLIS);
			final boolean isDown = (uet == UploadEventType.FILE_UPLOAD_ERROR);
			this.getController().uploadError(isDown, event.getFiles());
			return this.getState(StateFactoryType.CARDUPLOADERROR);
		} else {
			return super.executeUploadEvent(event);
		}
	}
}