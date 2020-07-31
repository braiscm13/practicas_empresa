package com.opentach.client.modules.admin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.field.Label;
import com.ontimize.gui.manager.IFormManager;
import com.opentach.client.OpentachClientLocator;
import com.opentach.common.user.IUserData;
import com.utilmize.client.fim.UBasicFIM;

public class IMAcercaDeGestor extends UBasicFIM {


	private static final Logger	logger	= LoggerFactory.getLogger(IMAcercaDeGestor.class);
	private Label				lDemo	= null;

	@Override
	public void registerInteractionManager(Form form, IFormManager formManager) {
		super.registerInteractionManager(form, formManager);
		this.lDemo = (Label) form.getElementReference("demo");
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		if (this.lDemo != null) {
			OpentachClientLocator ocl = (OpentachClientLocator) this.formManager.getReferenceLocator();
			try {
				IUserData ud = ocl.getUserData();
				if (ud != null) {
					Date dMax = ud.getDMaxLogin();
					if (dMax != null) {
						DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
						String sMax = df.format(dMax);
						this.lDemo.setText(ApplicationManager.getTranslation("M_DEMO_MENU_EXPIRED_WARN_TEXT",
								ApplicationManager.getApplicationBundle(), new Object[] { sMax }));
					}
				}
			} catch (Exception e) {
				IMAcercaDeGestor.logger.error(null, e);
			}
		}
	}

}
