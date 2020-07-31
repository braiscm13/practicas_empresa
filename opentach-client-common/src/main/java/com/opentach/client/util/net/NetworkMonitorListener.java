package com.opentach.client.util.net;

import java.util.EventListener;

public interface NetworkMonitorListener extends EventListener {

	public void networkStatusChange(NetworkEvent ne);

}
