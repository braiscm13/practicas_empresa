package com.opentach.client.labor.comp;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.InputStream;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.utilmize.client.gui.menu.AbstractActionListenerMenuItem;
import com.utilmize.client.gui.tasks.USwingWorker;

public class OpenFileMenuListener extends AbstractActionListenerMenuItem {

	private static final Logger	logger	= LoggerFactory.getLogger(OpenFileMenuListener.class);
	private String filePath;

	public OpenFileMenuListener(Hashtable params) throws Exception {
		super(params);
	}

	@Override
	protected void init(Hashtable params) throws Exception {
		super.init(params);
		this.filePath = (String) params.get("file");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		new USwingWorker<File, Void>() {

			@Override
			protected File doInBackground() throws Exception {
				File file = File.createTempFile("tacolab", ".pdf");
				file.deleteOnExit();
				try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(OpenFileMenuListener.this.filePath)) {
					FileTools.copyFile(is, file);
				}
				return file;
			}

			@Override
			protected void done() {
				try {
					Desktop.getDesktop().open(this.uget());
				} catch (Throwable error) {
					MessageManager.getMessageManager().showExceptionMessage(error, OpenFileMenuListener.logger);
				}
			}

		}.executeOperation(ApplicationManager.getApplication().getFrame());
	}

}

