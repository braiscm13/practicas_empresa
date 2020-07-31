package com.opentach.client.modules.files;

import com.ontimize.gui.Form;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.ReferenceFieldAttribute;
import com.ontimize.gui.manager.IFormManager;
import com.opentach.client.modules.IMDataRoot;
import com.opentach.common.OpentachFieldNames;

public class IMFicheroTGD extends IMDataRoot {

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		ValueChangeListener vcl = new ValueChangeListener() {
			@Override
			public void valueChanged(ValueEvent e) {
				if (e.getType() == ValueEvent.USER_CHANGE) {
					DataField src = (DataField) e.getSource();
					String attr = ((ReferenceFieldAttribute) src.getAttribute()).getAttr();
					if (attr.equals(OpentachFieldNames.IDCONDUCTOR_FIELD)) {
						IMFicheroTGD.this.managedForm.setDataFieldValue("TIPO", "TC");
						IMFicheroTGD.this.managedForm.setDataFieldValue("MATRICULA", null);
					} else {
						IMFicheroTGD.this.managedForm.setDataFieldValue("TIPO", "VU");
						IMFicheroTGD.this.managedForm.setDataFieldValue(OpentachFieldNames.IDCONDUCTOR_FIELD, null);
					}
					IMFicheroTGD.this.managedForm.setDataFieldValue(OpentachFieldNames.IDORIGEN_FIELD, src.getValue());
				}
			}
		};

		DataField cmp = (DataField) f.getDataFieldReference(OpentachFieldNames.IDCONDUCTOR_FIELD);
		if (cmp != null) {
			cmp.addValueChangeListener(vcl);
		}
		cmp = (DataField) f.getDataFieldReference(OpentachFieldNames.MATRICULA_FIELD);
		if (cmp != null) {
			cmp.addValueChangeListener(vcl);
		}
	}

	@Override
	public void setInsertMode() {
		super.setInsertMode();
		this.managedForm.enableDataFields();
	}
}
