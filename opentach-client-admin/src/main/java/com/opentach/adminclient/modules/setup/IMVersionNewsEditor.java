package com.opentach.adminclient.modules.setup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.Form;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.TextComboDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.utilmize.client.fim.UBasicFIM;
import com.utilmize.client.gui.field.UFxHtmlEditorDataField;

public class IMVersionNewsEditor extends UBasicFIM {

	private static final Logger	logger	= LoggerFactory.getLogger(IMVersionNewsEditor.class);

	@FormComponent(attr = "CONTENT")
	UFxHtmlEditorDataField		htmlEditor;
	@FormComponent(attr = "locale")
	TextComboDataField			localeCombo;
	@FormComponent(attr = "save")
	Button						saveButton;

	public IMVersionNewsEditor() {
		super();
	}

	@Override
	public void registerInteractionManager(Form form, IFormManager formManager) {
		super.registerInteractionManager(form, formManager);
		this.localeCombo.addValueChangeListener(new LocaleComboValueChangeListener());
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.setUpdateMode();
		this.loadContent();
	}

	private void loadContent() {
		try {
			Object localeValue = this.localeCombo.getValue();
			if (localeValue == null) {
				this.htmlEditor.setValue(null);
				this.saveButton.setEnabled(false);
				return;
			}
			EntityResult res = this.formManager
					.getReferenceLocator()
					.getEntityReference("EPreferenciasServidor")
					.query(EntityResultTools.keysvalues("NOMBRE", "Version.News." + localeValue), EntityResultTools.attributes("VALOR"),
							this.formManager.getReferenceLocator().getSessionId());
			CheckingTools.checkValidEntityResult(res, "ERROR_GETTING_NEWS", true, true, new Object[] {});
			String html = (String) res.getRecordValues(0).get("VALOR");
			this.htmlEditor.setValue(html);
			this.saveButton.setEnabled(true);
		} catch (Exception error) {
			MessageManager.getMessageManager().showExceptionMessage(error, this.managedForm, IMVersionNewsEditor.logger);
		}
	}

	class LocaleComboValueChangeListener implements ValueChangeListener {

		@Override
		public void valueChanged(ValueEvent e) {
			IMVersionNewsEditor.this.loadContent();
		}

	}
}
