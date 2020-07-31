package com.opentach.client.comp;

import java.util.Hashtable;
import java.util.Locale;

import javax.swing.ImageIcon;

import com.ontimize.gui.container.Row;
import com.ontimize.gui.images.ImageManager;

public class LocaleImageRow extends Row {
	protected static final String	IMAGENAME	= "imagename";
	protected static final String	IMAGEPATH	= "imagepath";

	protected String				imagename;
	protected String				imagepath;

	public LocaleImageRow(Hashtable params) {
		super(params);
	}

	@Override
	public void init(Hashtable params) {
		super.init(params);
		this.imagename = (String) params.get(LocaleImageRow.IMAGENAME);
		this.imagepath = (String) params.get(LocaleImageRow.IMAGEPATH);
	}

	@Override
	public void setComponentLocale(Locale l) {
		super.setComponentLocale(l);
		if ((this.imagename != null) && (this.imagepath != null)) {
			final String ipath = this.imagepath + l + "/" + this.imagename;
			ImageIcon ic = ImageManager.getIcon(ipath);
			if (ic != null) {
				this.setBackgroundImage(ic.getImage());
			}
		}
	}
}
