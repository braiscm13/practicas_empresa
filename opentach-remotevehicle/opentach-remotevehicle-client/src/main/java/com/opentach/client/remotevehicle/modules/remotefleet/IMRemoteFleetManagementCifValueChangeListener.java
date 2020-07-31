package com.opentach.client.remotevehicle.modules.remotefleet;

import java.util.Hashtable;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.ObjectTools;
import com.utilmize.client.gui.AbstractValueChangeListener;
import com.utilmize.client.gui.buttons.IUFormComponent;

public class IMRemoteFleetManagementCifValueChangeListener extends AbstractValueChangeListener {

	@FormComponent(attr = "ojee.RemoteVehicleManagementService.downloadVehicleConfig")
	private Table table;

	public IMRemoteFleetManagementCifValueChangeListener(IUFormComponent formComponent, Hashtable params) throws Exception {
		super(formComponent, params);
	}

	@Override
	public void valueChanged(ValueEvent evt) {
		if (evt.getNewValue() == null) {
			this.table.deleteData();
		} else if (!ObjectTools.safeIsEquals(evt.getOldValue(), evt.getNewValue())) {
			this.table.refreshInThread(0);
		}
	}

}
