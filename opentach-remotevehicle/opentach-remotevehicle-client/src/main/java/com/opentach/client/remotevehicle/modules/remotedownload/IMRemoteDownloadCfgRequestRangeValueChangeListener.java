package com.opentach.client.remotevehicle.modules.remotedownload;

import java.util.Hashtable;

import com.ontimize.gui.ValueEvent;
import com.utilmize.client.gui.AbstractValueChangeListener;
import com.utilmize.client.gui.buttons.IUFormComponent;

public class IMRemoteDownloadCfgRequestRangeValueChangeListener extends AbstractValueChangeListener {

	public IMRemoteDownloadCfgRequestRangeValueChangeListener(IUFormComponent formComponent, Hashtable params) throws Exception {
		super(formComponent, params);
	}

	@Override
	public void valueChanged(ValueEvent evt) {
		this.getUBasicInteractionManager().setDataFieldsEnable((Boolean) evt.getNewValue(), "FILTERFECINI", "FILTERFECFIN");
		this.getUBasicInteractionManager().deleteDataFields("FILTERFECINI", "FILTERFECFIN");
	}

}
