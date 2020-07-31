package com.opentach.client.remotevehicle.comp.action;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import com.ontimize.jee.desktopclient.spring.BeansFactory;
import com.opentach.client.comp.action.AbstractConditionOpenPubliManagerListener;
import com.opentach.common.remotevehicle.services.IRemoteVehicleService;

public class RemoteVehicleOpenPubliManagerListener extends AbstractConditionOpenPubliManagerListener {

	public RemoteVehicleOpenPubliManagerListener(Hashtable params) throws Exception {
		super(params);
	}

	@Override
	protected boolean hastToShowPubli(ActionEvent evt) throws Exception {
		return !BeansFactory.getBean(IRemoteVehicleService.class).isRemoteDownloadActive();
	}

}
