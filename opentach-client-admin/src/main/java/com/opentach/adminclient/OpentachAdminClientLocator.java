package com.opentach.adminclient;

import java.util.Hashtable;

import com.ontimize.gui.ApplicationManager;
import com.opentach.adminclient.sessionstatus.AdminClientSessionStatusManager;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.util.HideThreadMonitor;
import com.opentach.client.util.SoundManager;
import com.opentach.client.util.download.DownloadMonitor;
import com.opentach.common.tacho.TachoFileStore;
import com.opentach.common.user.IUserData;

public class OpentachAdminClientLocator extends OpentachClientLocator {

	private AdminClientSessionStatusManager	sessionStatusManager	= null;

	public OpentachAdminClientLocator(Hashtable params) {
		super(params);
	}

	@Override
	protected void onSessionStarted(String userName) throws Exception {
		IUserData ud = this.getUserData();
		if (this.htm == null) {
			this.htm = new HideThreadMonitor();
		}
		if (this.dwm == null) {
			this.dwm = new DownloadMonitor(this.htm, SoundManager.uriSonido);
		}
		// if (this.trm == null) {
		// this.trm = this.createTachoReadMonitor();
		// }
		if (this.upm == null) {
			this.upm = this.createUploadMonitor(ud, this.htm);
		}
		if (this.fileStore == null) {
			this.fileStore = this.createFileRepository();
			String autosave2store = ApplicationManager.getApplication().getPreferences().getPreference(this.getUser(), TachoFileStore.TACHOFILE_SAVE2STORE);
			this.changeTGDstore(this.fileStore.getTGDStore(), (autosave2store != null) && autosave2store.equals("0") ? false : true);
		}

		if (this.sessionStatusManager == null) {
			this.sessionStatusManager = this.createSessionStatusManager(userName);
		}

	}

	private AdminClientSessionStatusManager createSessionStatusManager(String userName) {
		AdminClientSessionStatusManager sessionStatusManager = new AdminClientSessionStatusManager(this, userName);
		sessionStatusManager.start();
		return sessionStatusManager;
	}
}
