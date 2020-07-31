package com.opentach.client.modules.admin;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import javax.swing.AbstractButton;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.field.RadioButtonDataField;
import com.ontimize.gui.field.TextComboDataField;
import com.ontimize.gui.field.TextDataField;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.util.LocalPreferencesManager;
import com.opentach.client.util.directorywatcher.AbstractWatcherSettings;
import com.opentach.client.util.directorywatcher.IWatcherSettings;
import com.opentach.client.util.directorywatcher.ftp.FtpWatcherSettings;
import com.opentach.client.util.directorywatcher.local.LocalWatcherSettings;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.tasks.USwingWorker;

public class IMWatchFolderOkActionListener extends AbstractActionListenerButton {

	private static final Logger		logger	= LoggerFactory.getLogger(IMWatchFolderOkActionListener.class);

	@FormComponent(attr = "rdb_local")
	private RadioButtonDataField	radioButtonLocal;
	@FormComponent(attr = "rdb_ftp")
	private RadioButtonDataField	radioButtonFtp;

	@FormComponent(attr = "local_folder")
	private TextDataField			localFolder;

	@FormComponent(attr = "ftp_server")
	private TextDataField			ftpServer;
	@FormComponent(attr = "ftp_user")
	private TextDataField			ftpUser;
	@FormComponent(attr = "ftp_pass")
	private TextDataField			ftpPass;
	@FormComponent(attr = "ftp_folder")
	private TextDataField			ftpFolder;
	@FormComponent(attr = "ftp_connection_type")
	private TextComboDataField		ftpConnectionType;

	public IMWatchFolderOkActionListener() throws Exception {
		super();
	}

	public IMWatchFolderOkActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public IMWatchFolderOkActionListener(Hashtable params) throws Exception {
		super(params);
	}

	public IMWatchFolderOkActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		new USwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				IWatcherSettings info = null;
				if (IMWatchFolderOkActionListener.this.radioButtonFtp.isSelected()) {
					info = new FtpWatcherSettings(//
							(String) IMWatchFolderOkActionListener.this.ftpServer.getValue(),//
							(String) IMWatchFolderOkActionListener.this.ftpPass.getValue(),//
							(String) IMWatchFolderOkActionListener.this.ftpUser.getValue(), //
							(String) IMWatchFolderOkActionListener.this.ftpFolder.getValue(),//
							FtpWatcherSettings.parseFtpConnectionType((String) IMWatchFolderOkActionListener.this.ftpConnectionType.getValue())//
							);
				} else {
					info = new LocalWatcherSettings(IMWatchFolderOkActionListener.this.localFolder.getText());
				}
				LocalPreferencesManager.getInstance().setPreference(LocalPreferencesManager.PROP_TGD_WATCH_FOLDER, AbstractWatcherSettings.toPreference(info));
				return null;
			}

			@Override
			protected void done() {
				super.done();
				try {
					this.uget();
					SwingUtilities.getWindowAncestor(IMWatchFolderOkActionListener.this.getForm()).setVisible(false);
				} catch (Throwable error) {
					MessageManager.getMessageManager().showExceptionMessage(error, IMWatchFolderOkActionListener.logger);
				}
			}
		}.executeOperation(this.getForm());
	}
}
