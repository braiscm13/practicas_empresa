package com.opentach.client.comp;

import java.util.Hashtable;

import com.ontimize.gui.Form;
import com.utilmize.client.gui.form.UFormExt;

public class OpentachFormExt extends UFormExt {
	private Form parentForm;

	public OpentachFormExt(Hashtable parameters) throws Exception {
		super(parameters);
	}

	@Override
	public void init(Hashtable parameters) {
		super.init(parameters);
	}

	public Form getParentForm() {
		return this.parentForm;
	}

	public void setParentForm(Form parentForm) {
		this.parentForm = parentForm;
	}

}
