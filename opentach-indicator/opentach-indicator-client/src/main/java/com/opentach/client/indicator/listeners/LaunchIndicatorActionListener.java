package com.opentach.client.indicator.listeners;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import javax.swing.AbstractButton;
import javax.swing.JOptionPane;

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
import com.opentach.common.indicator.services.IIndicatorService;
import com.utilmize.client.gui.buttons.AbstractUpdateModeActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;

public class LaunchIndicatorActionListener extends AbstractUpdateModeActionListenerButton {

	/** The CONSTANT logger */
	private static final Logger	logger	= LoggerFactory.getLogger(LaunchIndicatorActionListener.class);

	@FormComponent(attr = "IND_ID")
	protected DataField			indIdField;
	@FormComponent(attr = "ojee.IndicatorService.indicatorExecution")
	protected Table				executionsTable;

	public LaunchIndicatorActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	public LaunchIndicatorActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			Object indId = this.indIdField.getValue();
			CheckingTools.failIfNull(indId, "E_MANDATORY__IND_ID");

			boolean save = JOptionPane.OK_OPTION == MessageManager.getMessageManager().showMessage(this.getForm(), "Q_SAVE_INDICATOR_RESULT", MessageType.QUESTION, new Object[0]);
			BeansFactory.getBean(IIndicatorService.class).executeIndicator(indId, save);

			MessageManager.getMessageManager().showMessage(this.getForm(), "M_CORRECT_EXECUTION", MessageType.INFORMATION, new Object[0]);
		} catch (Exception err) {
			MessageManager.getMessageManager().showExceptionMessage(err, this.getForm(), LaunchIndicatorActionListener.logger);
		}
		this.executionsTable.refreshInThread(0);
	}

	@Override
	protected boolean getEnableValueToSet() {
		return this.isUpdateModeValidToEnable && (InteractionManager.UPDATE == this.getForm().getInteractionManager().getCurrentMode());
	}
}