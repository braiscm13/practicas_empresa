package com.opentach.client.alert.listeners;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import javax.swing.AbstractButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.MessageType;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.jee.desktopclient.spring.BeansFactory;
import com.opentach.common.alert.naming.AlertNaming;
import com.opentach.common.alert.services.IAlertService;
import com.utilmize.client.gui.buttons.AbstractUpdateModeActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;

public class LaunchAlertActionListener extends AbstractUpdateModeActionListenerButton {

	/** The CONSTANT logger */
	private static final Logger	logger	= LoggerFactory.getLogger(LaunchAlertActionListener.class);

	@FormComponent(attr = "ALR_ID")
	protected DataField			alrIdField;
	@FormComponent(attr = "ojee.AlertService.alertExecution")
	protected Table				executionsTable;

	public LaunchAlertActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	public LaunchAlertActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			boolean asynch = (e.getModifiers() & ActionEvent.CTRL_MASK) > 0;
			Object alrId = this.getAlrId();
			CheckingTools.failIfNull(alrId, "E_MANDATORY_ALR_ID");
			if (asynch) {
				BeansFactory.getBean(IAlertService.class).executeAlertAsynch(alrId);
				MessageManager.getMessageManager().showMessage(this.getForm(), "M_ALERT_REQUESTED_BACKGROUND", MessageType.INFORMATION, new Object[0]);
			} else {
				BeansFactory.getBean(IAlertService.class).executeAlert(alrId);
				MessageManager.getMessageManager().showMessage(this.getForm(), "M_ALERT_EXECUTED", MessageType.INFORMATION, new Object[0]);
			}
		} catch (Exception err) {
			MessageManager.getMessageManager().showExceptionMessage(err, this.getForm(), LaunchAlertActionListener.logger);
		}
		if (this.executionsTable != null) {
			this.executionsTable.refreshInThread(0);
		}
	}

	private Object getAlrId() {
		if (this.alrIdField != null) {
			// From detail form
			return this.alrIdField.getValue();
		} else if (this.executionsTable != null) {
			// From table
			if (this.executionsTable.getSelectedRowsNumber() == 1) {
				return this.executionsTable.getSelectedRowData().get(AlertNaming.ALR_ID);
			}
		}
		return null;
	}

	@Override
	protected boolean getEnableValueToSet() {
		return this.isUpdateModeValidToEnable && (InteractionManager.UPDATE == this.getForm().getInteractionManager().getCurrentMode());
	}
}