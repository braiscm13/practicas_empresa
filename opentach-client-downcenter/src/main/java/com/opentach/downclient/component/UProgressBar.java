package com.opentach.downclient.component;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JProgressBar;

import com.ontimize.gui.Form;
import com.ontimize.gui.field.AccessForm;
import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.field.IdentifiedElement;
import com.ontimize.jee.common.tools.ParseUtilsExtended;

public class UProgressBar extends JProgressBar implements FormComponent, AccessForm, IdentifiedElement {
	protected boolean	expand	= false;
	protected String	attr;

	public UProgressBar(Hashtable parameters) throws Exception {
		super();
		this.init(parameters);
	}

	@Override
	public void init(Hashtable parameters) throws Exception {
		this.attr = (String) parameters.get("attr");
		int max = ParseUtilsExtended.getInteger((String) parameters.get("max"), 100);
		int min = ParseUtilsExtended.getInteger((String) parameters.get("min"), 0);
		boolean indeterminate = ParseUtilsExtended.getBoolean((String) parameters.get("indeterminate"), false);
		if (!indeterminate) {
			this.setMaximum(max);
			this.setMinimum(min);
		}
		boolean expand = ParseUtilsExtended.getBoolean((String) parameters.get("expand"), true);
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
	public void setParentForm(Form form) {}

	@Override
	public Object getConstraints(LayoutManager parentLayout) {
		return new GridBagConstraints(GridBagConstraints.RELATIVE, GridBagConstraints.RELATIVE, 1, 1, 1.0, 0, GridBagConstraints.CENTER,
				this.expand ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
	}

	@Override
	public void initPermissions() {}

	@Override
	public boolean isRestricted() {
		return false;
	}

	@Override
	public Object getAttribute() {
		return this.attr;
	}

}
