package com.opentach.adminclient.modules.surveys;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.table.Table;
import com.utilmize.client.fim.UBasicFIM;

public class IMSurveys extends UBasicFIM {
	@FormComponent(attr = "ESurveys")
	private Table eSurveys;

	public IMSurveys() {
		super();
	}

	@Override
	public void setInitialState() {
		this.setUpdateMode();
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		this.eSurveys.refreshInThread(0);
	}

}
