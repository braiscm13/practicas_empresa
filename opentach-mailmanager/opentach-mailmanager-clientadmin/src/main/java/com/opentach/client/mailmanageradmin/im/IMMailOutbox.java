package com.opentach.client.mailmanageradmin.im;

import java.util.Calendar;
import java.util.Date;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.field.DataField;
import com.ontimize.jee.common.tools.DateTools;
import com.utilmize.client.fim.advanced.UBasicFIMSearch;
import com.utilmize.client.gui.buttons.advanced.USearchPanel;

public class IMMailOutbox extends UBasicFIMSearch {

	@FormComponent(attr = "MAI_SENT_DATE_FROM")
	private DataField		dateFrom;
	@FormComponent(attr = "searchPanel")
	private USearchPanel	searchPanel;

	public IMMailOutbox() {
		super();
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.dateFrom.setValue(DateTools.truncate(DateTools.add(new Date(), Calendar.MONTH, -1)));
		this.searchPanel.getSearchButton().doClick();
	}

}
