package com.opentach.client.remotevehicle.modules.remotefleet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Form;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.ThreadTools;

/**
 * Thread that update barrier status into UI.
 */
public class IMRemoteFleetManagementThreadRefreshFleetStatus extends Thread {
	private static final Logger	logger			= LoggerFactory.getLogger(IMRemoteFleetManagementThreadRefreshFleetStatus.class);

	public static final long	REFRESH_PERIOD	= 2 * 60l * 1000l;
	private final Form			form;

	public IMRemoteFleetManagementThreadRefreshFleetStatus(Form form) {
		super("TreadRefreshFleetStatus");
		this.form = form;
	}

	@Override
	public void run() {
		super.run();

		while (true) {
			try {
				this.updateStatus();
			} catch (Exception ex) {
				IMRemoteFleetManagementThreadRefreshFleetStatus.logger.error("E_UPDATING_BARRIER_STATUS", ex);
			} finally {
				ThreadTools.sleep(IMRemoteFleetManagementThreadRefreshFleetStatus.REFRESH_PERIOD);
			}
		}
	}

	protected void updateStatus() {
		if (!this.form.isShowing()) {
			return;
		}
		if (this.form.getDataFieldValue("CIF") != null) {
			((Table) this.form.getElementReference("ojee.RemoteVehicleManagementService.downloadVehicleConfig")).refreshInThread(0);
		}
	}
}