package com.opentach.client.alert.listeners;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

import javax.swing.JFileChooser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.field.DataField;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.common.alert.naming.AlertNaming;
import com.utilmize.client.gui.buttons.AbstractUpdateModeActionListenerButton;
import com.utilmize.client.gui.buttons.UButton;

/**
 * Open form in insert mode with all values of this alert (tables ignored).
 */
public class LoadHTMLListenerButton extends AbstractUpdateModeActionListenerButton {

	/** The CONSTANT logger */
	private static final Logger	logger	= LoggerFactory.getLogger(LoadHTMLListenerButton.class);

	@FormComponent(attr = AlertNaming.ALR_BODY)
	protected DataField			bodyDF;

	protected JFileChooser		chooser;

	public LoadHTMLListenerButton(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (this.chooser == null) {
				this.chooser = new JFileChooser();
			}
			int showOpenDialog = this.chooser.showOpenDialog(this.getForm());
			if (showOpenDialog != JFileChooser.APPROVE_OPTION) {
				return;
			}
			if ((this.chooser.getSelectedFiles().length != 1) && (this.chooser.getSelectedFile() == null)) {
				return;
			}
			File selectedFile = this.chooser.getSelectedFile();
			String content = this.readFileContent(selectedFile);
			this.bodyDF.setValue(content);
		} catch (Exception err) {
			MessageManager.getMessageManager().showExceptionMessage(err, this.getForm(), LoadHTMLListenerButton.logger, "E_GETTING_FILE_CONTENT");
		}
	}

	private String readFileContent(File file) throws IOException {
		try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "UTF8")) {
			StringBuilder content = new StringBuilder();
			char[] buffer = new char[1024];
			int readed = 0;
			while ((readed = reader.read(buffer)) > 0) {
				content.append(buffer, 0, readed);
			}
			return content.toString();
		}
	}
}
