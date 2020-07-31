package com.opentach.client.labor.modules.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Form;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.modules.IMReportRoot;

public class IMInformeAgreements extends IMReportRoot {

	private static final Logger logger = LoggerFactory.getLogger(IMInformeAgreements.class);

	public IMInformeAgreements() {
		super("ELaborAgreement", "");
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.doOnQuery(false);
	}

	@Override
	public void doOnQuery(final boolean alert) {
		try {
			this.refreshTables();
		} catch (Exception error) {
			MessageManager.getMessageManager().showExceptionMessage(error, IMInformeAgreements.logger);
		}
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
	}

}
