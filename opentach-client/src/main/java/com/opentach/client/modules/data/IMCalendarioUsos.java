package com.opentach.client.modules.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Form;
import com.ontimize.gui.manager.IFormManager;
import com.opentach.client.modules.IMDataRoot;
import com.opentach.common.OpentachFieldNames;

public class IMCalendarioUsos extends IMDataRoot {

	private static final Logger	logger	= LoggerFactory.getLogger(IMCalendarioUsos.class);

	public IMCalendarioUsos() {
		super();
	}


	@Override
	public void registerInteractionManager(Form f, IFormManager fm) {
		super.registerInteractionManager(f, fm);
		this.addDateTags(new TimeStampDateTags(OpentachFieldNames.FECINI_FIELD));
	}

	@Override
	public void setInitialState() {
		this.setUpdateMode();
		this.managedForm.getButton("brefresh").setEnabled(true);
		this.managedForm.getButton("reportCalendarioUsos").setEnabled(true);

	}

}
