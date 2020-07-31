package com.opentach.client.devicecontrol;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
import com.opentach.client.util.directorywatcher.local.AbstractLocalWatcherListener;
import com.opentach.client.util.directorywatcher.local.LocalWatcherEvent;
import com.opentach.client.util.directorywatcher.local.LocalWatcherSettings;
import com.opentach.client.util.upload.TGDFileSendThread;
import com.opentach.client.util.upload.UploadMonitor;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.user.IUserData;

public class FolderWatcherLocalEventListener extends AbstractLocalWatcherListener {

	private static final Logger	logger	= LoggerFactory.getLogger(FolderWatcherLocalEventListener.class);

	public FolderWatcherLocalEventListener() {
		super();
	}

	@Override
	public void onFolderWatcherEvent(LocalWatcherEvent event) {
		Path child = event.getFile();
		// upload the file
		FolderWatcherLocalEventListener.logger.info("upload the file" + child);
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
			FolderWatcherLocalEventListener.logger.error(null, ex);
		}

		Hashtable<String, Object> cv = new Hashtable<>();
		MapTools.safePut(cv, OpentachFieldNames.CIF_FIELD, cif);
		MapTools.safePut(cv, OpentachFieldNames.CG_CONTRATO_FIELD, activeContract);
		MapTools.safePut(cv, OpentachFieldNames.FILENAME_FIELD, child.toFile().getName());

		UploadMonitor upm = ((MonitorProvider) locator).getUploadMonitor();
		upm.attach(new TGDFileSendThread(null, cv, new TGDFileInfo(child.toFile()), SoundManager.uriSonido, null) {

			@Override
			protected void onSuccess() {
				super.onSuccess();
				try {
					Files.move(child, this.getTargetPath(child), StandardCopyOption.REPLACE_EXISTING);
				} catch (Exception error) {
					FolderWatcherLocalEventListener.logger.error(null, error);
					try {
						Files.deleteIfExists(child);
					} catch (IOException ex) {
						FolderWatcherLocalEventListener.logger.error(null, ex);
					}
				}
			}

			private Path getTargetPath(Path child) throws IOException {
				Path parent = child.getParent();
				Path target = parent.resolve("uploaded");
				if (!Files.exists(target)) {
					Files.createDirectories(target);
				}
				return target.resolve(child.getFileName());
			}

			@Override
			protected void showErrorMessage(String message) {}
		}, null, true);
	}

	@Override
	public void onErrorEvent(LocalWatcherSettings info, Exception error) {
		// do nothing
	}

}
