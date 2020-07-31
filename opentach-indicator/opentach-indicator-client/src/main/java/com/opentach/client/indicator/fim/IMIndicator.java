package com.opentach.client.indicator.fim;

import java.util.Date;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.DataNavigationEvent;
import com.ontimize.gui.Form;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.jee.common.tools.ReflectionTools;
import com.utilmize.client.fim.FIMUtils;
import com.utilmize.client.fim.UBasicFIM;

public class IMIndicator extends UBasicFIM {

	@FormComponent(attr = "FILTERFECINI")
	protected DateDataField	fecIni;
	@FormComponent(attr = "FILTERFECFIN")
	protected DateDataField	fecFin;

	@Override
	public void registerInteractionManager(Form form, IFormManager formManager) {
		super.registerInteractionManager(form, formManager);
		this.setStayInRecordAfterInsert(true);
		this.setAfterUpdate(true);

		FIMUtils.setEventsOnFieldEnabled(this.managedForm, "FEC_FILTER", false);
		FIMUtils.setEventsOnFieldEnabled(this.managedForm, (String) this.fecIni.getAttribute(), false);
		FIMUtils.setEventsOnFieldEnabled(this.managedForm, (String) this.fecFin.getAttribute(), false);
	}

	@Override
	public void dataChanged(DataNavigationEvent e) {
		super.dataChanged(e);

		// Set default filters (first one in silent mode to not fire two refresh events)
		boolean events = (boolean) ReflectionTools.getFieldValue(this.fecIni, "fireValueEvents");
		ReflectionTools.setFieldValue(this.fecIni, "fireValueEvents", false);
		this.fecIni.setValue(new Date());
		ReflectionTools.setFieldValue(this.fecIni, "fireValueEvents", events);
		this.fecFin.setValue(new Date());
	}
}
