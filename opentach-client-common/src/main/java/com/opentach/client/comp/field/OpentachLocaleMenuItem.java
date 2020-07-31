package com.opentach.client.comp.field;

import java.util.Hashtable;

import javax.swing.Icon;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.i18n.LocaleMenuItem;

public class OpentachLocaleMenuItem extends LocaleMenuItem {

	public OpentachLocaleMenuItem(Hashtable parameters) {
		super(parameters);
	}

	@Override
	public void init(Hashtable params) {
		super.init(params);
		String sIcon = (String) params.get("icon");
		if (sIcon != null) {
			Icon icon = ApplicationManager.getIcon(sIcon);
			this.setIcon(icon);
		}
	}
}
