package com.opentach.adminclient.modules.news;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import javax.swing.AbstractButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.BasicInteractionManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;

public class IMNewsSaveActionListener extends AbstractActionListenerButton {

	private static final Logger	logger	= LoggerFactory.getLogger(IMNewsSaveActionListener.class);

	public IMNewsSaveActionListener() throws Exception {
		super();
	}

	public IMNewsSaveActionListener(Hashtable params) throws Exception {
		super(params);
	}

	public IMNewsSaveActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public IMNewsSaveActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			Hashtable dataFieldValues = this.getForm().getDataFieldValues(false);
			dataFieldValues.remove("CREATION_DATE");
			Object idCdoNew = dataFieldValues.remove("IDNEW");
			Hashtable<Object, Object> keys = EntityResultTools.keysvalues("IDNEW", idCdoNew);
			EntityResult res = this.getEntity("ENews").update(dataFieldValues, keys,
					this.getSessionId());
			CheckingTools.checkValidEntityResult(res);
			this.getForm().setStatusBarText(BasicInteractionManager.S_CORRECT_UPDATE, 3000);

			// Reload data in table
			Table table = (Table) this.getForm().getElementReference("ENews");
			table.refreshInThread(0);
		} catch (Exception error) {
			MessageManager.getMessageManager().showExceptionMessage(error, IMNewsSaveActionListener.logger);
		}
	}

}
