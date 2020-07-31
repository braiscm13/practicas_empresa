package com.opentach.client.modules.data;

import com.ontimize.gui.Form;
import com.ontimize.gui.manager.IFormManager;
import com.opentach.client.modules.IMReportRoot;

public class IMQueryRestricciones extends IMReportRoot {


	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);

	}
	public IMQueryRestricciones() {
		super();
		this.setDateTags(new TimeStampDateTags("F_INICIAL"));
		this.tablename = "ERestricciones";
	}

	@Override
	public void doOnQuery(final boolean alert) {
		if (this.managedForm.existEmptyRequiredDataField()) {
			this.managedForm.message("Establezca filtro de búsqueda.", Form.INFORMATION_MESSAGE);
		} else if (this.checkQuery()) {
			this.refreshTable(this.tablename);
		}
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.setUpdateMode();
	}

}
