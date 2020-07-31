package com.opentach.adminclient.modules.setup;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import javax.swing.AbstractButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.BasicInteractionManager;
import com.ontimize.gui.field.TextComboDataField;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.field.UFxHtmlEditorDataField;

public class IMVersionNewsEditorSaveListener extends AbstractActionListenerButton {

	private static final Logger	logger	= LoggerFactory.getLogger(IMVersionNewsEditorSaveListener.class);
	@FormComponent(attr = "CONTENT")
	UFxHtmlEditorDataField		htmlEditor;
	@FormComponent(attr = "locale")
	TextComboDataField			localeCombo;

	public IMVersionNewsEditorSaveListener() throws Exception {
		super();
	}

	public IMVersionNewsEditorSaveListener(Hashtable params) throws Exception {
		super(params);
	}

	public IMVersionNewsEditorSaveListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public IMVersionNewsEditorSaveListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			Object localeValue = this.localeCombo.getValue();
			if (localeValue == null) {
				throw new Exception("CHOOSE_LOCALE");
			}
			String html = (String) this.htmlEditor.getValue();
			EntityResult res = this.getEntity("EPreferenciasServidor").update(EntityResultTools.keysvalues("VALOR", html),
					EntityResultTools.keysvalues("NOMBRE", "Version.News." + localeValue), this.getSessionId());
			CheckingTools.checkValidEntityResult(res);
			this.getForm().setStatusBarText(BasicInteractionManager.S_CORRECT_UPDATE, 3000);
		} catch (Exception ex) {
			MessageManager.getMessageManager().showExceptionMessage(ex, this.getForm(), IMVersionNewsEditorSaveListener.logger);
		}
	}

}
