package com.opentach.client.util.devicecontrol;

import java.util.HashMap;
import java.util.Map;

import com.opentach.client.util.devicecontrol.states.IState;

public abstract class StateFactory {

	public enum StateFactoryType {
		INIT, CARDINSERTED, CARDCOPYING, CARDERROR, CARDUPLOADING, CARDUPLOADERROR, CARDUPLOADED, USBSTART, USBEND, USBERROR, USBUPLOADING, USBUPLOADERROR, USBUPLOADED, PRINT, THANKS, REPORTGENERATING, REPORTVIEWING, UNAVAILABLE, USB_NOT_FOUND
	}

	protected Map<StateFactoryType, IState>	mStates;

	public StateFactory(AbstractDeviceController controller) {
		this.mStates = new HashMap<StateFactoryType, IState>();
		this.fillStateFactory(controller);
	}

	protected abstract void fillStateFactory(AbstractDeviceController controller);

	public IState getState(StateFactoryType type) {
		return this.mStates.get(type);
	}

	public void registerState(StateFactoryType type, IState state) {
		this.mStates.put(type, state);
	}
}