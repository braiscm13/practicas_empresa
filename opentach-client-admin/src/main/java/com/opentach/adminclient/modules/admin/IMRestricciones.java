package com.opentach.adminclient.modules.admin;

import java.util.Calendar;
import java.util.Date;

import com.ontimize.gui.Form;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.CheckDataField;
import com.ontimize.gui.field.HourDateDataField;
import com.ontimize.gui.manager.IFormManager;
import com.opentach.client.modules.IMRoot;

public class IMRestricciones extends IMRoot {

	public IMRestricciones() {
		super();
	}

	@Override
	public void registerInteractionManager(Form form, final IFormManager formManager) {
		super.registerInteractionManager(form, formManager);
		
		CheckDataField cdf = (CheckDataField) managedForm.getDataFieldReference("TODOS_LOS_DIAS");
		cdf.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChanged(ValueEvent e) {
				if (e.getNewValue().equals("S")) {
					Calendar c = Calendar.getInstance();
					c.set(c.get(Calendar.YEAR), 0, 1, 8, 0,0);
					Calendar cFinal = Calendar.getInstance();
					cFinal.set(c.get(Calendar.YEAR), 11, 31, 22, 0,0);
					((HourDateDataField)managedForm.getDataFieldReference("F_INICIAL")).setValue(c.getTime());
					((HourDateDataField)managedForm.getDataFieldReference("F_FINAL")).setValue(cFinal.getTime());
					((HourDateDataField)managedForm.getDataFieldReference("F_INICIAL")).setEnabled(false);
					((HourDateDataField)managedForm.getDataFieldReference("F_FINAL")).setEnabled(false);
				}else {
					((HourDateDataField)managedForm.getDataFieldReference("F_INICIAL")).setValue(null);
					((HourDateDataField)managedForm.getDataFieldReference("F_FINAL")).setValue(null);
					((HourDateDataField)managedForm.getDataFieldReference("F_INICIAL")).setEnabled(true);
					((HourDateDataField)managedForm.getDataFieldReference("F_FINAL")).setEnabled(true);
				}
			}
		});
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.setQueryInsertMode();
	}
}
