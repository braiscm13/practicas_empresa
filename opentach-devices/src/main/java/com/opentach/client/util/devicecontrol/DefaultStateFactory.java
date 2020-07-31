package com.opentach.client.util.devicecontrol;

import com.opentach.client.util.devicecontrol.states.CardCopyingState;
import com.opentach.client.util.devicecontrol.states.CardErrorState;
import com.opentach.client.util.devicecontrol.states.CardInsertedState;
import com.opentach.client.util.devicecontrol.states.CardUploadErrorState;
import com.opentach.client.util.devicecontrol.states.CardUploadedState;
import com.opentach.client.util.devicecontrol.states.CardUploadingState;
import com.opentach.client.util.devicecontrol.states.InitState;
import com.opentach.client.util.devicecontrol.states.ShowingReportState;
import com.opentach.client.util.devicecontrol.states.ThanksState;
import com.opentach.client.util.devicecontrol.states.USBDownloadErrorState;
import com.opentach.client.util.devicecontrol.states.USBDownloadState;
import com.opentach.client.util.devicecontrol.states.USBNotFoundState;
import com.opentach.client.util.devicecontrol.states.USBUploadErrorState;
import com.opentach.client.util.devicecontrol.states.USBUploadedState;
import com.opentach.client.util.devicecontrol.states.USBUploadingState;
import com.opentach.client.util.devicecontrol.states.UnavailableState;

public class DefaultStateFactory extends StateFactory {

	public DefaultStateFactory(AbstractDeviceController controller) {
		super(controller);
	}

	@Override
	protected void fillStateFactory(AbstractDeviceController controller) {
		// CARD
		this.mStates.put(StateFactoryType.INIT, new InitState(controller));
		this.mStates.put(StateFactoryType.CARDINSERTED, new CardInsertedState(controller));
		this.mStates.put(StateFactoryType.CARDCOPYING, new CardCopyingState(controller));
		this.mStates.put(StateFactoryType.CARDUPLOADING, new CardUploadingState(controller));
		this.mStates.put(StateFactoryType.CARDERROR, new CardErrorState(controller));
		this.mStates.put(StateFactoryType.CARDUPLOADERROR, new CardUploadErrorState(controller));
		this.mStates.put(StateFactoryType.CARDUPLOADED, new CardUploadedState(controller));
		this.mStates.put(StateFactoryType.THANKS, new ThanksState(controller));

		// USB
		this.mStates.put(StateFactoryType.USBSTART, new USBDownloadState(controller));
		this.mStates.put(StateFactoryType.USBUPLOADING, new USBUploadingState(controller));
		this.mStates.put(StateFactoryType.USBERROR, new USBDownloadErrorState(controller));
		this.mStates.put(StateFactoryType.USBUPLOADERROR, new USBUploadErrorState(controller));
		this.mStates.put(StateFactoryType.USBUPLOADED, new USBUploadedState(controller));
		this.mStates.put(StateFactoryType.USB_NOT_FOUND, new USBNotFoundState(controller));

		// REPORT
		this.mStates.put(StateFactoryType.REPORTVIEWING, new ShowingReportState(controller));

		// UNAVAILABLE
		this.mStates.put(StateFactoryType.UNAVAILABLE, new UnavailableState(controller));
	}
}