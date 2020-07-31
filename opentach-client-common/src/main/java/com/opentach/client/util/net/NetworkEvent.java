package com.opentach.client.util.net;

import java.util.EventObject;

public class NetworkEvent extends EventObject {

	public static enum NetworkEventType {
		NETWORK_UP, NETWORK_DOWN
	}

	private final NetworkEventType	type;

	public NetworkEvent(Object source, NetworkEventType type) {
		super(source);
		this.type = type;
	}

	public NetworkEventType getType() {
		return this.type;
	}

	@Override
	public String toString() {
		return String.format("%s [type=%s]", this.getClass().getName(), this.type);
	}
}
