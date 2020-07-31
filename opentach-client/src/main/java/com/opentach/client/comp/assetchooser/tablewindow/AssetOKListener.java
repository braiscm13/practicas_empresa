package com.opentach.client.comp.assetchooser.tablewindow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.opentach.client.comp.assetchooser.AssetComboDataField;

public class AssetOKListener implements ActionListener {

	protected AssetTableWindow		tableWindow;
	protected AssetComboDataField	parentDataField;

	public AssetOKListener(AssetTableWindow tableWindow, AssetComboDataField parentDataField) {
		this.tableWindow = tableWindow;
		this.parentDataField = parentDataField;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.tableWindow.getSelectedProvider().onTableSelection();
	}

}
