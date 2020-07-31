package com.opentach.server.remotevehicle.provider.opentach;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

public class RemoteDownloadServletRequestListener implements ServletRequestListener {

	@Override
	public void requestDestroyed(ServletRequestEvent sre) {

	}

	@Override
	public void requestInitialized(ServletRequestEvent sre) {
		// use to enable httpsession in websocket configuration
		((HttpServletRequest) sre.getServletRequest()).getSession();
	}

}
