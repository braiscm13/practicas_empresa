package com.opentach.client.remotevehicle.modules.remotedownload;

import java.util.Hashtable;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.DataField;
import com.utilmize.client.gui.AbstractValueChangeListener;
import com.utilmize.client.gui.buttons.IUFormComponent;

public class IMRemoteDownloadCfgDetailTypeValueChangeListener extends AbstractValueChangeListener {

	@FormComponent(attr = "RDV_UNIT_SN")
	private DataField rdvUnitField;

	public IMRemoteDownloadCfgDetailTypeValueChangeListener(IUFormComponent formComponent, Hashtable params) throws Exception {
		super(formComponent, params);
	}

	@Override
	public void valueChanged(ValueEvent evt) {
		this.rdvUnitField.setVisible("V".equals(evt.getNewValue()));
	}

}
