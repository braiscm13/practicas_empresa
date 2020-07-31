package com.opentach.adminclient.modules.reports;

import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.InteractionManagerModeEvent;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.OpentachClientLocator;
import com.opentach.common.activities.IInfractionService;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.UButton;

public class IMInformeFullInfractionsGenerateListener extends AbstractActionListenerButton {

	private static final Logger	logger	= LoggerFactory.getLogger(IMInformeFullInfractionsGenerateListener.class);

	@FormComponent(attr = "sta.FECHORAINI")
	private DateDataField		fecHoraIni;
	@FormComponent(attr = "sta.FECHORAFIN")
	private DateDataField		fecHoraFin;

	public IMInformeFullInfractionsGenerateListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			this.fecHoraIni.setAdvancedQueryMode(false);
			this.fecHoraFin.setAdvancedQueryMode(false);
			((OpentachClientLocator) this.getReferenceLocator()).getRemoteService(IInfractionService.class).doFullInfractionReport((Date) this.fecHoraIni.getValue(),
					(Date) this.fecHoraFin.getValue(), this.getSessionId());
		} catch (Exception ex) {
			MessageManager.getMessageManager().showExceptionMessage(ex, IMInformeFullInfractionsGenerateListener.logger);
		}
	}

	@Override
	public void interactionManagerModeChanged(InteractionManagerModeEvent interactionmanagermodeevent) {
		super.interactionManagerModeChanged(interactionmanagermodeevent);
	}

}
