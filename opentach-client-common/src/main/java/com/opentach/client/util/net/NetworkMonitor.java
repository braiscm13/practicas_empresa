package com.opentach.client.util.net;

import javax.swing.event.EventListenerList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.client.util.UserInfoProvider;
import com.opentach.client.util.net.NetworkEvent.NetworkEventType;
import com.opentach.common.sessionstatus.ISessionStatusService;

public class NetworkMonitor {

	private static final Logger		logger	= LoggerFactory.getLogger(NetworkMonitor.class);
	private final Thread			th;
	private final UserInfoProvider	ocl;
	private final EventListenerList	nmlList;
	private transient boolean		boot;
	private transient boolean		status;

	public NetworkMonitor(final UserInfoProvider ocl, final int checkTime) {
		this.boot = true;
		this.status = true;
		this.ocl = ocl;
		this.nmlList = new EventListenerList();
		this.th = new Thread(NetworkMonitor.class.getName()) {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(checkTime);
					} catch (InterruptedException e) {
						NetworkMonitor.logger.error(null, e);
					}
					NetworkMonitor.this.check();
				}
			}
		};
	}

	public void start() {
		this.th.start();
	}

	private void check() {
		boolean newStatus = false;
		try {
			this.ocl.getRemoteService(ISessionStatusService.class).check();
			newStatus = true;
		} catch (Exception error) {
			NetworkMonitor.logger.error(null, error);
			newStatus = false;
		}

		if (this.boot || (newStatus != this.status)) {
			this.boot = false;
			final NetworkEventType net = newStatus ? NetworkEventType.NETWORK_UP : NetworkEventType.NETWORK_DOWN;
			final NetworkEvent ne = new NetworkEvent(this, net);
			this.fireNetworkEvent(ne);
		}
		this.status = newStatus;
	}

	private void fireNetworkEvent(NetworkEvent ne) {
		final NetworkMonitorListener[] nmla = this.nmlList.getListeners(NetworkMonitorListener.class);
		for (NetworkMonitorListener nml : nmla) {
			nml.networkStatusChange(ne);
		}
	}

	public void addNetworkMonitorListener(NetworkMonitorListener nml) {
		if (nml != null) {
			this.nmlList.add(NetworkMonitorListener.class, nml);
		}
	}

	public boolean getStatus() {
		return this.status;
	}
}
