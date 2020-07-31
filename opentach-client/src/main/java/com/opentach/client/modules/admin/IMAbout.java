package com.opentach.client.modules.admin;

import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.BasicInteractionManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.manager.IFormManager;
import com.opentach.client.comp.field.HTMLHelpField;
import com.utilmize.tools.VersionUtils;

public class IMAbout extends BasicInteractionManager {

	@FormComponent(attr = "ruta_info_about")
	private HTMLHelpField	helpField;

	public IMAbout() {
		super();
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		Map<String, String> dictionary = new HashMap<>();
		dictionary.put("#APP_VERSION#", VersionUtils.getVersion(this.getClass()));
		this.helpField.setDictionary(dictionary);
	}

	@Override
	public void setInitialState() {
		this.setUpdateMode();
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		this.managedForm.disableDataFields();
	}

	public void mouseClicked(MouseEvent e) {
		try {
			Desktop.getDesktop().browse(new URI("http://www.opentach.com"));
		} catch (Exception e1) {
		}
	}
}
