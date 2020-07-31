package com.opentach.client.devicecontrol;

import java.nio.file.Path;
import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.client.MonitorProvider;
import com.opentach.client.util.SoundManager;
import com.opentach.client.util.TGDFileInfo;
import com.opentach.client.util.UserInfoProvider;
import com.opentach.client.util.directorywatcher.ftp.AbstractFtpWatcherListener;
import com.opentach.client.util.directorywatcher.ftp.FtpWatcherEvent;
import com.opentach.client.util.directorywatcher.ftp.FtpWatcherSettings;
import com.opentach.client.util.upload.TGDFileSendThread;
import com.opentach.client.util.upload.UploadMonitor;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.user.IUserData;

public class FolderWatcherFtpEventListener extends AbstractFtpWatcherListener {

	private static final Logger logger = LoggerFactory.getLogger(FolderWatcherFtpEventListener.class);

	public FolderWatcherFtpEventListener() {
		super();
	}

	@Override
	public void onFolderWatcherEvent(FtpWatcherEvent event) {
		synchronized (event.getFtpFile()) {
			FtpWatcherEvent ftpEvent = event;
			Path file = event.getFile();
			// upload the file
			FolderWatcherFtpEventListener.logger.info("upload the file {}", file);
			EntityReferenceLocator locator = ApplicationManager.getApplication().getReferenceLocator();
			String cif = null;
			String activeContract = null;
			try {
				IUserData userData = ((UserInfoProvider) locator).getUserData();
				if (userData != null) {
					List<String> cifs = userData.getCompaniesList();
					if ((cifs != null) && (cifs.size() == 1)) {
						cif = cifs.get(0);
						activeContract = userData.getActiveContract(cif);
					}
				}
			} catch (Exception ex) {
				FolderWatcherFtpEventListener.logger.error(null, ex);
			}

			Hashtable<String, Object> cv = new Hashtable<>();
			MapTools.safePut(cv, OpentachFieldNames.CIF_FIELD, cif);
			MapTools.safePut(cv, OpentachFieldNames.CG_CONTRATO_FIELD, activeContract);
			MapTools.safePut(cv, OpentachFieldNames.FILENAME_FIELD, file.toFile().getName());

			UploadMonitor upm = ((MonitorProvider) locator).getUploadMonitor();
			upm.attach(new TGDFileSendThread(null, cv, new TGDFileInfo(file.toFile()), SoundManager.uriSonido, null) {

				@Override
				protected void onSuccess() {
					super.onSuccess();
					try {
						ftpEvent.getFtpWatcher().backupFile(ftpEvent.getWatchFolderInfo(), ftpEvent.getFtpFile());
					} catch (Exception error) {
						FolderWatcherFtpEventListener.logger.error(null, error);
					}
				}

				@Override
				protected void onFinished() {
					super.onFinished();
					synchronized (ftpEvent.getFtpFile()) {
						ftpEvent.getFtpFile().notify();
					}
				}

				@Override
				protected void showErrorMessage(String message) {}
			}, null, false);
			try {
				event.getFtpFile().wait(60000);
			} catch (InterruptedException e) {
				// do nothing
			}
		}
	}

	@Override
	public void onErrorEvent(FtpWatcherSettings info, Exception error) {
		// do nothing
	}

}
