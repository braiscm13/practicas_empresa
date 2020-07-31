package com.opentach.client.modules.notices;

import com.ontimize.gui.Form;
import com.ontimize.gui.field.DataComponent;
import com.ontimize.gui.field.HTMLDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.util.notice.INoticeSystem;
import com.utilmize.client.fim.UBasicFIM;

public class IMNoticesSend extends UBasicFIM {

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		DataComponent cData = f.getDataFieldReference(INoticeSystem.NOTICE_SUBJECT);
		if ((cData != null) && (cData instanceof HTMLDataField)) {
			((HTMLDataField) cData).setButtonPanelVisible(false);
		}

	}

}
