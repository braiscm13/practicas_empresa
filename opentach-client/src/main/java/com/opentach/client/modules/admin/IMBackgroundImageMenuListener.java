package com.opentach.client.modules.admin;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.util.LocalPreferencesManager;
import com.utilmize.client.gui.menu.AbstractActionListenerMenuItem;
import com.utilmize.tools.exception.UException;

public class IMBackgroundImageMenuListener extends AbstractActionListenerMenuItem {

	private static final String		IMAGE_VALID_EXTENSIONS			= "jpg;jpeg;png;bmp";
	private static final String[]	splitValidExt					= IMBackgroundImageMenuListener.IMAGE_VALID_EXTENSIONS.split(";");
	private static final Logger		logger							= LoggerFactory.getLogger(IMBackgroundImageMenuListener.class);
	private JFileChooser			fileChooser;
	private String					action							= "select";

	public IMBackgroundImageMenuListener(Hashtable params) throws Exception {
		super(params);
		this.action = (String) params.getOrDefault("action", "select");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if ("default".equals(this.action)) {
				this.selectDefaultBackground();
			} else if ("restore".equals(this.action)) {
				this.restoreBackground();
			} else {
				this.selectBackground(e);
			}
		} catch (UException | IOException e1) {
			MessageManager.getMessageManager().showExceptionMessage(e1, IMBackgroundImageMenuListener.logger);
		}
	}

	private void selectDefaultBackground() throws UException, IOException {
		LocalPreferencesManager.getInstance().defaultImageBackground();
		this.configureBackground();
	}

	private void selectBackground(ActionEvent e) throws UException, IOException {
		if (this.fileChooser == null) {
			this.fileChooser = new JFileChooser();
			this.fileChooser.setMultiSelectionEnabled(false);// Allow only
			String images = ApplicationManager.getTranslation("Images");
			this.fileChooser.setFileFilter(new FileNameExtensionFilter(images, IMBackgroundImageMenuListener.splitValidExt));
		}
		int option = this.fileChooser.showOpenDialog((Component) e.getSource());
		if (option != 0) {
			return;
		}
		File selectedFile = this.fileChooser.getSelectedFile();
		if (selectedFile != null) {
			String extension = selectedFile.getName().substring(selectedFile.getName().lastIndexOf('.') + 1);
			boolean extValid = false;
			for (String extAux : IMBackgroundImageMenuListener.splitValidExt) {
				if (extAux.equals(extension)) {
					extValid = true;
				}
			}
			if (extValid) {
				LocalPreferencesManager.getInstance().setImageBackground(selectedFile);
				this.configureBackground();
			} else {
				throw new UException("E_EXTENSION_IMAGE");
			}
		}
	}

	private void restoreBackground() throws UException, IOException {
		LocalPreferencesManager.getInstance().restoreImageBackground();
		this.configureBackground();
	}

	private void configureBackground() throws UException, IOException {
		IFormManager gfMenu = ApplicationManager.getApplication().getFormManager("manageracercade2");
		Form f = gfMenu.getFormReference("formAcercaDe.xml");
		if (f != null) {
			((IMAcercaDe) f.getInteractionManager()).configureBackground();
		}
	}

}
