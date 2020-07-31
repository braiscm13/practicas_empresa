package com.opentach.client.modules.data;

import com.ontimize.gui.Form;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.manager.IFormManager;
import com.opentach.client.modules.OpentachBasicInteractionManager;

public class IMPersonalEmp extends OpentachBasicInteractionManager {

	private Button	bLetraNIF	= null;

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.bLetraNIF = f.getButton("calculaletranif");
		//		DataField cmpDNI = (DataField) f.getDataFieldReference(OpentachFieldNames.DNI_FIELD);
		//		cmpDNI.addValueChangeListener(new ValueChangeListener() {
		//			@Override
		//			public void valueChanged(ValueEvent e) {
		//				if (e.getType() == ValueEvent.USER_CHANGE) {
		//					String nv = (String) e.getNewValue();
		//					if ((nv != null) && (nv.length() > 0)) {
		//						IMPersonalEmp.this.bLetraNIF.setEnabled(true);
		//						if (IMPersonalEmp.this.getCurrentMode() == InteractionManager.INSERT) {
		//							if (DriverUtil.checkValidCIFNIF(IMPersonalEmp.this.managedForm, nv, false)
		//									&& (IMPersonalEmp.this.managedForm.getDataFieldValue("IDPERSONAL") == null)) {
		//								IMPersonalEmp.this.managedForm.setDataFieldValue("IDPERSONAL", "E" + nv + "0000");
		//							}
		//						}
		//					} else {
		//						IMPersonalEmp.this.bLetraNIF.setEnabled(false);
		//					}
		//				}
		//			}
		//		});
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		if (this.bLetraNIF != null) {
			this.bLetraNIF.setEnabled(true);
		}
		//		this.managedForm.getDataFieldReference("IDPERSONAL").setEnabled(false);
	}

	@Override
	public void setInsertMode() {
		super.setInsertMode();
		if (this.bLetraNIF != null) {
			this.bLetraNIF.setEnabled(true);
		}
	}

	@Override
	public boolean checkInsert() {
		try {
			//			if (DriverUtil.checkValidCIFNIF(this.managedForm, (String) this.managedForm.getDataFieldValue(OpentachFieldNames.DNI_FIELD), false)) {
			//				if (this.managedForm.getDataFieldValue("IDPERSONAL") == null) {
			//					this.managedForm.setDataFieldValue("IDPERSONAL", "E" + this.managedForm.getDataFieldValue(OpentachFieldNames.DNI_FIELD) + "0000");
			//				}
			//				return super.checkInsert();
			//			} else {
			return super.checkInsert();
			//			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

}
