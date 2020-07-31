package com.opentach.client.util;

import com.ontimize.locator.PermissionReferenceLocator;

public interface IClientLocatorEventListener {
	void onSessionStarted(int sessionId, PermissionReferenceLocator locator);

	void onSessionClosed(int sessionId, PermissionReferenceLocator locator);
}
