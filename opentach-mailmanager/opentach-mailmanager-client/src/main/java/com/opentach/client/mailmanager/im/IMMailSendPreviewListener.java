package com.opentach.client.mailmanager.im;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.field.DataComponent;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.jee.desktopclient.spring.BeansFactory;
import com.opentach.common.util.TemplateTools;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.UButton;

/**
 * The listener interface for receiving save events. The class that is interested in processing a save event implements this interface, and the object created with that class is
 * registered with a component using the component's <code>addSaveListener<code> method. When the save event occurs, that object's appropriate method is invoked.
 *
 * @see SaveEvent
 */
public class IMMailSendPreviewListener extends AbstractActionListenerButton {

	/** The Constant logger. */
	protected static final Logger	logger	= LoggerFactory.getLogger(IMMailSendPreviewListener.class);

	@FormComponent(attr = "MAI_BODY")
	private DataComponent			mailBodyField;

	/**
	 * Instantiates a new save listener.
	 *
	 * @param button
	 *            the button
	 * @param params
	 *            the params
	 * @throws Exception
	 *             the exception
	 */
	public IMMailSendPreviewListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	/*
	 * (non-Javadoc)
	 * @see com.utilmize.client.gui.buttons.AbstractActionListenerButton#init(java.util.Map)
	 */
	@Override
	protected void init(Map<?, ?> params) throws Exception {
		super.init(params);
	}

	/*
	 * (non-Javadoc)
	 * @see com.utilmize.client.gui.buttons.AbstractActionListenerButton#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		try {
			File tempFile = File.createTempFile("tamplate_preview", ".html");
			String body = (String) this.mailBodyField.getValue();
			Map<String, Object> templateParameters = new HashMap<>();
			templateParameters.put("body", StringEscapeUtils.escapeHtml4(body == null ? "" : body).replaceAll("\n", "<br>"));
			String doc = BeansFactory.getBean(TemplateTools.class).fillTemplateByClasspath("templates-mailmanager/agent_mail_template.vm", templateParameters);
			FileTools.copyFile(new ByteArrayInputStream(doc.getBytes()), tempFile);
			Desktop.getDesktop().open(tempFile);
		} catch (Exception err) {
			MessageManager.getMessageManager().showExceptionMessage(err, IMMailSendPreviewListener.logger);
		}

	}

	@Override
	protected boolean getEnableValueToSet() {
		return true;
	}

}
