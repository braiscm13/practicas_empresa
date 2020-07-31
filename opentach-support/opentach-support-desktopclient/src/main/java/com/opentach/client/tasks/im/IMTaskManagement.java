package com.opentach.client.tasks.im;

import java.util.Calendar;
import java.util.Date;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.jee.common.tools.DateTools;
import com.utilmize.client.fim.advanced.UBasicFIMSearch;

public class IMTaskManagement extends UBasicFIMSearch{

	@FormComponent(attr = "TSK_CREATION_DATE_FROM")
	protected DateDataField fromcreationDateField;

	@Override
	public void setInitialState() {
		super.setInitialState();

		this.setDefaultFilters();
		this.autoRefresh();
		this.managedForm.enableDataFields();
		this.managedForm.getDataFieldReference("TSK_IS_NEXT_RENEWAL").setEnabled(true);
	}

	protected void setDefaultFilters() {
		this.fromcreationDateField.setValue(DateTools.add(new Date(), Calendar.DAY_OF_YEAR, -10));
	}
}

