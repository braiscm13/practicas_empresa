package com.opentach.client.comp.questionary;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.field.IdentifiedElement;
import com.ontimize.util.ParseUtils;

public class OQuestion extends Question implements FormComponent, IdentifiedElement {

	/** The attr. */
	private String	attr;

	public OQuestion(Hashtable<?, ?> parameters) throws Exception {
		super();
		this.init(parameters);
	}

	@Override
	public void setComponentLocale(Locale l) {}

	@Override
	public void setResourceBundle(ResourceBundle resourceBundle) {}

	@Override
	public Vector getTextsToTranslate() {
		return null;
	}

	@Override
	public void init(Hashtable parameters) throws Exception {
		this.attr = ParseUtils.getString((String) parameters.get("attr"), null);
		this.setDisableChecks(ParseUtils.getBoolean((String) parameters.get("disableChecks"), true));
	}

	@Override
	public Object getConstraints(LayoutManager parentLayout) {
		return new GridBagConstraints(-1, -1, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
	}

	@Override
	public void initPermissions() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isRestricted() {
		return false;
	}

	@Override
	public Object getAttribute() {
		return this.attr;
	}
}
