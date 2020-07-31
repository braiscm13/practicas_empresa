package com.opentach.adminclient.modules.admin;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.locator.EntityReferenceLocator;
import com.ontimize.locator.UtilReferenceLocator;
import com.opentach.common.process.ITachoFileProcessService;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.menu.AbstractActionListenerMenuItem;

public class FileProcessListenerMenuItem extends AbstractActionListenerMenuItem {

	/** The CONSTANT logger */
	private static final Logger		logger								= LoggerFactory.getLogger(FileProcessListenerMenuItem.class);

	public static final ImageIcon	FILE_PROCESS_STATUS_ICON_ENABLED	= ImageManager.getIcon("com/opentach/client/rsc/gear_run24.png");
	public static final ImageIcon	FILE_PROCESS_STATUS_ICON_DISABLED	= ImageManager.getIcon("com/opentach/client/rsc/gear_pause24.png");

	protected UButton				button;
	protected boolean				currentStatus;

	public FileProcessListenerMenuItem(UButton button, Hashtable params) throws Exception {
		super(params);
		this.button = button;

		this.setInitialState();
	}

	protected void setInitialState() {
		try {
			this.currentStatus = this.getProcessFileStatus();
			this.addaptButonStatus();
		} catch (Exception e) {
			FileProcessListenerMenuItem.logger.error("E_GETTING_PROCESS_FILE_STATUS", e);
		}
	}

	protected void addaptButonStatus() {
		String text = "file_process." + (this.currentStatus ? "enabled" : "disabled");
		this.button.setText(ApplicationManager.getTranslation(text));
		this.button.setToolTipText(ApplicationManager.getTranslation(text + ".tip"));
		this.button.setIcon(this.currentStatus ? FileProcessListenerMenuItem.FILE_PROCESS_STATUS_ICON_ENABLED : FileProcessListenerMenuItem.FILE_PROCESS_STATUS_ICON_DISABLED);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			boolean realStatus = this.getProcessFileStatus();
			if (this.currentStatus != realStatus) {
				// Only update button status
				this.currentStatus = realStatus;
				this.addaptButonStatus();
				return;
			}

			this.currentStatus = !realStatus;
			this.getService().setProcessEnabled(this.currentStatus, this.getSessionId());
			this.addaptButonStatus();
		} catch (Exception ex) {
			FileProcessListenerMenuItem.logger.error("E_SETTING_PROCESS_FILE_STATUS", ex);
		} finally {

		}
	}

	protected boolean getProcessFileStatus() throws Exception {
		return this.getService().isProcessEnabled(this.getSessionId());
	}

	protected ITachoFileProcessService getService() throws Exception {
		EntityReferenceLocator erl = this.getReferenceLocator();
		return (ITachoFileProcessService) ((UtilReferenceLocator) erl).getRemoteReference(ITachoFileProcessService.ID, this.getSessionId());
	}

}
