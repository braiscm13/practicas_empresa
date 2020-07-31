package com.opentach.client.modules.data;

import javax.swing.JOptionPane;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.manager.IFormManager;
import com.opentach.client.modules.OpentachBasicInteractionManager;
import com.opentach.client.util.DriverUtil;
import com.opentach.common.OpentachFieldNames;

public class IMFirmanteEmp extends OpentachBasicInteractionManager {

	private Button	bLetraNIF	= null;

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.bLetraNIF = f.getButton("calculaletranif");
		DataField cmpDNI = (DataField) f.getDataFieldReference(OpentachFieldNames.DNI_FIELD);
		cmpDNI.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChanged(ValueEvent e) {
				if (e.getType() == ValueEvent.USER_CHANGE) {
					String nv = (String) e.getNewValue();
					if ((nv != null) && (nv.length() > 0)) {
						IMFirmanteEmp.this.bLetraNIF.setEnabled(true);
					} else {
						IMFirmanteEmp.this.bLetraNIF.setEnabled(false);
					}
				}
			}
		});
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		if (this.bLetraNIF != null) {
			this.bLetraNIF.setEnabled(true);
		}
	}

	@Override
	public void setInsertMode() {
		super.setInsertMode();
		if (this.bLetraNIF != null) {
			this.bLetraNIF.setEnabled(true);
		}
	}

	@Override
	public boolean checkUpdate() {
		try {
			if (DriverUtil.checkValidCIFNIF(this.managedForm, (String) this.managedForm.getDataFieldValue(OpentachFieldNames.DNI_FIELD), false)) {
				return super.checkUpdate();
			}
			else {
				String msg = ApplicationManager.getTranslation("VERIFIQUE_DATOS_CONDUCTOR", this.managedForm.getResourceBundle());
				int rtn = JOptionPane.showConfirmDialog(this.managedForm.getParent(), msg);
				if (rtn == JOptionPane.YES_OPTION) {
					return super.checkInsert();
				} else {
					return false;
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean checkInsert() {
		try {
			if (DriverUtil.checkValidCIFNIF(this.managedForm, (String) this.managedForm.getDataFieldValue("NIF"), false)) {

				return super.checkInsert();
			} else {
				String msg = ApplicationManager.getTranslation("VERIFIQUE_DATOS_CONDUCTOR", this.managedForm.getResourceBundle());
				int rtn = JOptionPane.showConfirmDialog(this.managedForm.getParent(), msg);
				if (rtn == JOptionPane.YES_OPTION) {
					return super.checkInsert();
				} else {
					return false;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

}
