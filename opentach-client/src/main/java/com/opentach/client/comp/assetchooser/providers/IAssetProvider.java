package com.opentach.client.comp.assetchooser.providers;

public interface IAssetProvider {

	String getId();

	String getRadioButtonText();

	void onProviderSelected();

	void onTableSelection();

	void onRefreshRequested() throws Exception;

	void configureAdvancedQueryMode();
}
