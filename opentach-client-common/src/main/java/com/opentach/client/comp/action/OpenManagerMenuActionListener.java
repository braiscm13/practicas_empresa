package com.opentach.client.comp.action;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Application;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.IParameterItem;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.utilmize.client.gui.menu.AbstractActionListenerMenuItem;

public class OpenManagerMenuActionListener extends AbstractActionListenerMenuItem {

	private static final Logger	logger	= LoggerFactory.getLogger(OpenManagerMenuActionListener.class);

	protected String			managerName;

	public OpenManagerMenuActionListener(Hashtable params) throws Exception {
		super(params);
	}

	@Override
	protected void init(Hashtable params) throws Exception {
		super.init(params);
		this.managerName = ParseUtilsExtended.getString((String) params.get("manager"), null);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Cursor cursor = null;
		try {
			cursor = ((Component) this.getApplication()).getCursor();
			((Component) this.getApplication()).setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			IFormManager formManager = this.getFormManager(this.managerName);
			if (formManager != null) {
				formManager.load();
				this.getApplication().showFormManagerContainer(this.managerName);
				String formName = this.getFormName(this.managerName);
				if ((formName != null) && (formManager != null)) {
					formManager.showForm(formName);
				}
			} else {
				OpenManagerMenuActionListener.logger.warn("form manager {} not found", this.managerName);
			}
		} finally {
			if ((cursor != null) && (this.getApplication() instanceof Component)) {
				((Component) this.getApplication()).setCursor(cursor);
			}
		}
	}

	private Application getApplication() {
		return ApplicationManager.getApplication();
	}

	protected String getFormName(Object source) {
		if (source instanceof IParameterItem) {
			return ((IParameterItem) source).getFormName();
		}
		return null;
	}

}
