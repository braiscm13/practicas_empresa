package com.opentach.client.remotevehicle.modules.remotedownload;

import java.util.Hashtable;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.container.Column;
import com.ontimize.gui.field.Label;
import com.utilmize.client.gui.AbstractValueChangeListener;
import com.utilmize.client.gui.buttons.IUFormComponent;

public class IMRemoteDownloadCfgRequestDownloadTypeValueChangeListener extends AbstractValueChangeListener {

	@FormComponent(attr = "LABEL_DRIVER")
	private Label	labelDriver;
	@FormComponent(attr = "LABEL_VEHICLE")
	private Label	labelVehicle;
	@FormComponent(attr = "datos")
	private Column	radioGroup;

	public IMRemoteDownloadCfgRequestDownloadTypeValueChangeListener(IUFormComponent formComponent, Hashtable params) throws Exception {
		super(formComponent, params);
	}

	@Override
	public void valueChanged(ValueEvent evt) {
		boolean isVehicle = "V".equals(evt.getNewValue());
		this.labelDriver.setVisible(!isVehicle);
		this.labelVehicle.setVisible(isVehicle);
		this.radioGroup.setVisible(isVehicle);

	}

}
