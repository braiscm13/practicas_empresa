package com.opentach.client.modules.files;

import java.util.Date;

import com.ontimize.gui.Form;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.opentach.client.modules.data.util.DescargasCellRenderer;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.util.DateUtil;

public class IMDownloadFicherosTGD extends IMFicherosTGD {

	public IMDownloadFicherosTGD() {
		super();
		this.setDateTags(new TimeStampDateTags("F_ALTA"));
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		Table tbFicherosSubidos = (Table) this.managedForm.getElementReference("EFicherosSubidos");

		if (tbFicherosSubidos != null) {
			tbFicherosSubidos.setCellRendererColorManager(new DescargasCellRenderer());
		}

		Date end = new Date();
		this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECINI, DateUtil.addDays(end, -15));
		this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECFIN, end);
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.setUpdateMode();
	}
}
