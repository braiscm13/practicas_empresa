package com.opentach.client.modules.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.Form;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.RadioButtonDataField;
import com.ontimize.gui.field.TextDataField;
import com.ontimize.gui.manager.IFormManager;
import com.opentach.client.util.LocalPreferencesManager;
import com.opentach.client.util.directorywatcher.AbstractWatcherSettings;
import com.opentach.client.util.directorywatcher.IWatcherSettings;
import com.opentach.client.util.directorywatcher.IWatcherSettings.WatchFolderMode;
import com.opentach.client.util.directorywatcher.ftp.FtpWatcherSettings;
import com.opentach.client.util.directorywatcher.local.LocalWatcherSettings;
import com.utilmize.client.fim.UBasicFIM;

public class IMWatchFolder extends UBasicFIM {
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
	private static final Logger		logger	= LoggerFactory.getLogger(IMWatchFolder.class);

	public IMWatchFolder() {
		super();
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		ValueChangeListener valueChangeListener = new RadioValueChangeListener();
		this.radioButtonLocal.addValueChangeListener(valueChangeListener);
		this.radioButtonFtp.addValueChangeListener(valueChangeListener);
	}

	@Override
	public void setInitialState() {
		this.setUpdateMode();
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		String preference = LocalPreferencesManager.getInstance().getPreference(LocalPreferencesManager.PROP_TGD_WATCH_FOLDER);
		IWatcherSettings info = AbstractWatcherSettings.fromPreference(preference);

		this.radioButtonLocal.getAbstractButton().setSelected(WatchFolderMode.LOCAL.equals(info.getMode()));
		this.radioButtonFtp.getAbstractButton().setSelected(!WatchFolderMode.LOCAL.equals(info.getMode()));
		this.setDataFieldsEnable(WatchFolderMode.LOCAL.equals(info.getMode()), "local_folder");
		this.setDataFieldsEnable(!WatchFolderMode.LOCAL.equals(info.getMode()), "ftp_server", "ftp_user", "ftp_pass", "ftp_folder");

		this.localFolder.setValue(null);
		this.ftpServer.setValue(null);
		this.ftpUser.setValue(null);
		this.ftpPass.setValue(null);
		this.ftpFolder.setValue(null);

		if (info instanceof LocalWatcherSettings) {
			this.localFolder.setValue(((LocalWatcherSettings) info).getLocalFolder());
		} else if (info instanceof FtpWatcherSettings) {
			this.ftpServer.setValue(((FtpWatcherSettings) info).getFtpServer());
			this.ftpUser.setValue(((FtpWatcherSettings) info).getFtpUser());
			this.ftpPass.setValue(((FtpWatcherSettings) info).getFtpPass());
			this.ftpFolder.setValue(((FtpWatcherSettings) info).getFtpFolder());
		}

	}

	private class RadioValueChangeListener implements ValueChangeListener {

		@Override
		public void valueChanged(ValueEvent e) {
			IMWatchFolder.this.setDataFieldsEnable(IMWatchFolder.this.radioButtonLocal.isSelected(), "local_folder");
			IMWatchFolder.this.setDataFieldsEnable(!IMWatchFolder.this.radioButtonLocal.isSelected(), "ftp_server", "ftp_user", "ftp_pass", "ftp_folder");
		}

	}
}