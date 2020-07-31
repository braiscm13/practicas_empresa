package com.opentach.client.remotevehicle;

import com.ontimize.gui.Application;
import com.opentach.client.AbstractOpentachClientLocator;
import com.opentach.client.remotevehicle.provider.opentach.CompanyCardRemoteAuthenticationCardListener;
import com.opentach.client.util.IApplicationListener;
import com.opentach.model.scard.SmartCardMonitor;

public class RemoteVehicleApplicationListener implements IApplicationListener {

	public RemoteVehicleApplicationListener() {
		super();
	}

	@Override
	public void onApplicationBuilt(Application application) {
		((AbstractOpentachClientLocator) application.getReferenceLocator()).getLocalService(SmartCardMonitor.class)
		.addCardListener(new CompanyCardRemoteAuthenticationCardListener());
	}

}
