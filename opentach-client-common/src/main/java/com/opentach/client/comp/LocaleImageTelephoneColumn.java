package com.opentach.client.comp;

import java.util.Hashtable;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import com.ontimize.gui.Form;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.client.util.UserInfoProvider;
import com.opentach.common.user.IUserData;

//si el nivel es 12 el telefono es 902
//si no es el móvil
public class LocaleImageTelephoneColumn extends LocaleImageColumn {

	private static final Logger		logger			= Logger.getLogger(LocaleImageTelephoneRow.class.getName());
	private static final String		IMAGERNAME902	= "background_top_902.png";
	private static final String		IMAGERNAMEMOV	= "background_top_mov.png";

	protected EntityReferenceLocator	locator;

	public LocaleImageTelephoneColumn(Hashtable params) {
		super(params);
	}

	@Override
	public void setParentForm(Form f) {
		super.setParentForm(f);
		this.locator = f.getFormManager().getReferenceLocator();
	}

	@Override
	public void setComponentLocale(Locale l) {
		super.setComponentLocale(l);
		if (this.imagepath != null) {
			try {
				if (this.locator != null) {
					IUserData uData = ((UserInfoProvider) this.locator).getUserData();
					String ipath = null;
					if (IUserData.NIVEL_DOWNLOADTPD4.equals(uData.getLevel())) {
						ipath = this.imagepath + l + "/" + LocaleImageTelephoneColumn.IMAGERNAME902;
					} else {
						ipath = this.imagepath + l + "/" + LocaleImageTelephoneColumn.IMAGERNAMEMOV;
					}
					ImageIcon ic = ImageManager.getIcon(ipath);
					if (ic != null) {
						this.setBackgroundImage(ic.getImage());
					}
				}
			} catch (Exception e) {
				LocaleImageTelephoneColumn.logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}
}
