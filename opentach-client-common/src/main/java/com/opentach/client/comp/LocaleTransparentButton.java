package com.opentach.client.comp;

import java.util.Hashtable;
import java.util.Locale;

import javax.swing.ImageIcon;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.images.ImageManager;

public class LocaleTransparentButton extends TransparentButton {

	protected static final String	IMAGENAME	= "imagename";
	protected static final String	IMAGEPATH	= "imagepath";

	protected String				imagename;
	protected String				imagepath;

	public LocaleTransparentButton(Hashtable params) {
		super(params);
	}

	@Override
	public void init(Hashtable params) {
		super.init(params);
		this.imagename = (String) params.get(LocaleTransparentButton.IMAGENAME);
		this.imagepath = (String) params.get(LocaleTransparentButton.IMAGEPATH);
		final Locale appLocale = ApplicationManager.getLocale();
		this.setComponentLocale(appLocale);
	}

	@Override
	public void setComponentLocale(Locale l) {
		super.setComponentLocale(l);
		if ((this.imagename != null) && (this.imagepath != null)) {
			final String ipath = this.imagepath + l + "/" + this.imagename;
			ImageIcon ic = ImageManager.getIcon(ipath);
			if (ic != null) {
				this.setIcon(ic);
			}
		}
	}

}
