package com.opentach.adminclient.modules.cdo;

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

public class IMCDONewsSaveActionListener extends AbstractActionListenerButton {

	private static final Logger	logger	= LoggerFactory.getLogger(IMCDONewsSaveActionListener.class);

	public IMCDONewsSaveActionListener() throws Exception {
		super();
	}

	public IMCDONewsSaveActionListener(Hashtable params) throws Exception {
		super(params);
	}

	public IMCDONewsSaveActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public IMCDONewsSaveActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			Hashtable dataFieldValues = this.getForm().getDataFieldValues(false);
			dataFieldValues.remove("CREATION_DATE");
			Object idCdoNew = dataFieldValues.remove("IDCDONEW");
			Hashtable<Object, Object> keys = EntityResultTools.keysvalues("IDCDONEW", idCdoNew);
			EntityResult res = this.getEntity("ECDONews").update(dataFieldValues, keys,
					this.getSessionId());
			CheckingTools.checkValidEntityResult(res);
			this.getForm().setStatusBarText(BasicInteractionManager.S_CORRECT_UPDATE, 3000);

			// Reload data in table
			Table table = (Table) this.getForm().getElementReference("ECDONews");
			table.refreshInThread(0);
		} catch (Exception error) {
			MessageManager.getMessageManager().showExceptionMessage(error, IMCDONewsSaveActionListener.logger);
		}
	}

}
