package com.opentach.client.alert.listeners;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Hashtable;

import javax.swing.JFileChooser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.field.DataField;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.jee.common.tools.StringTools;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.common.alert.naming.AlertNaming;
import com.utilmize.client.gui.buttons.AbstractUpdateModeActionListenerButton;
import com.utilmize.client.gui.buttons.UButton;

/**
 * Open form in insert mode with all values of this alert (tables ignored).
 */
public class ExportHTMLListenerButton extends AbstractUpdateModeActionListenerButton {

	/** The CONSTANT logger */
	private static final Logger	logger	= LoggerFactory.getLogger(ExportHTMLListenerButton.class);

	@FormComponent(attr = AlertNaming.ALR_BODY)
	protected DataField			bodyDF;

	protected JFileChooser		chooser;

	public ExportHTMLListenerButton(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String content = (String) this.bodyDF.getValue();
		if (StringTools.isEmpty(content)) {
			return;
		}
		try (InputStream is = new ByteArrayInputStream(content.getBytes("UTF8"))) {
			if (this.chooser == null) {
				this.chooser = new JFileChooser();
			}
			int showOpenDialog = this.chooser.showSaveDialog(this.getForm());
			if (showOpenDialog != JFileChooser.APPROVE_OPTION) {
				return;
			}
			File selectedFile = this.chooser.getSelectedFile();
			if (selectedFile.isDirectory()) {
				selectedFile = new File(selectedFile, "file.html");
			} else if (!selectedFile.toString().endsWith(".html")) {
				selectedFile = new File(selectedFile.getParentFile() + "/" + selectedFile.getName() + ".html");
			}
			FileTools.copyFile(is, selectedFile);
		} catch (Exception err) {
			MessageManager.getMessageManager().showExceptionMessage(err, this.getForm(), ExportHTMLListenerButton.logger, "E_GETTING_FILE_CONTENT");
		}
	}
}
