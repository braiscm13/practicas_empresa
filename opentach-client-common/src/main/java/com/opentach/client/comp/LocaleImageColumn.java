package com.opentach.client.comp;

import java.util.Hashtable;
import java.util.Locale;

import javax.swing.ImageIcon;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.container.Column;
import com.ontimize.gui.images.ImageManager;

/**
 * To change the image with the locale
 *
 * @author rafael.lopez
 */
public class LocaleImageColumn extends Column {

	protected static final String	IMAGENAME	= "imagename";
	protected static final String	IMAGEPATH	= "imagepath";

	protected String				imagename;
	protected String				imagepath;

	public LocaleImageColumn(Hashtable params) {
		super(params);
	}

	@Override
	public void init(Hashtable params) {
		super.init(params);
		this.imagename = (String) params.get(LocaleImageColumn.IMAGENAME);
		this.imagepath = (String) params.get(LocaleImageColumn.IMAGEPATH);
		this.setComponentLocale(ApplicationManager.getApplication().getLocale());
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
