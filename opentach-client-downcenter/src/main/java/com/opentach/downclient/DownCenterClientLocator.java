package com.opentach.downclient;

import java.io.File;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.Application;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.preferences.ApplicationPreferences;
import com.ontimize.util.alerts.IAlertSystem;
import com.ontimize.util.notice.NoticeManagerFactory;
import com.opentach.client.AbstractOpentachClientLocator;
import com.opentach.client.util.DBUtils;
import com.opentach.client.util.HideThreadMonitor;
import com.opentach.client.util.UserInfoProvider;
import com.opentach.client.util.devicecontrol.IDownCenterMonitorProvider;
import com.opentach.client.util.download.DownloadMonitor;
import com.opentach.client.util.net.NetworkMonitor;
import com.opentach.client.util.printer.TicketPrinter;
import com.opentach.client.util.upload.UploadMonitor;
import com.opentach.client.util.usbkey.USBInfo;
import com.opentach.client.util.usbkey.USBInfoProvider;
import com.opentach.client.util.usbkey.USBKeyMonitor;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.interfaces.IOpentachLocator;
import com.opentach.common.tacho.TachoFileStore;
import com.opentach.common.user.ICDOUserData;
import com.opentach.common.user.IUserData;
import com.opentach.downclient.sessionstatus.DownCenterSessionStatusManager;
import com.opentach.model.comm.vu.TachoReadMonitor;
import com.opentach.model.scard.CardListener;
import com.opentach.model.scard.SmartCardMonitor;

public class DownCenterClientLocator extends AbstractOpentachClientLocator implements IDownCenterMonitorProvider, IAlertSystem, USBInfoProvider {

	private static final Logger				logger					= LoggerFactory.getLogger(DownCenterClientLocator.class);

	public static final String				P_MINIMIZED_START		= "P_MINIMIZED_START";
	private static final String				SCHECKTIME				= "checktime";
	private static final String				uriSonido				= "com/opentach/client/rsc/notify.wav";

	private IUserData						userData				= null;
	private int								checkTime				= -1;

	private USBKeyMonitor					usbkm					= null;
	private DownloadMonitor					dwm						= null;
	private UploadMonitor					upm						= null;
	private HideThreadMonitor				htm						= null;
	private TicketPrinter					tickPrinter				= null;
	private NetworkMonitor					nm						= null;
	private DownCenterSessionStatusManager	sessionStatusManager	= null;
	private TachoFileStore					fileStore				= null;
	private String							cdoDscrCache			= null;
	private List<USBInfo>					usbInfoCache			= null;
	private final long						tIni					= -1;

	public DownCenterClientLocator(Hashtable params) {
		super(params);
		String sCheckTime = (String) params.get(DownCenterClientLocator.SCHECKTIME);
		if (sCheckTime != null) {
			try {
				this.checkTime = Integer.parseInt(sCheckTime);
			} catch (Exception e) {
			}
		}
	}

	public long gettIni() {
		return this.tIni;
	}

	@Override
	protected void onSessionStarted(String userName) throws Exception {
		IUserData ud = this.getUserData();
		if (this.htm == null) {
			this.htm = new HideThreadMonitor();
		}
		if (this.dwm == null) {
			this.dwm = new DownloadMonitor(this.htm, DownCenterClientLocator.uriSonido);
		}
		this.createSmartCardMonitor();
		if (this.upm == null) {
			this.upm = this.createUploadMonitor(ud, this.htm);
		}
		if (this.usbkm == null) {
			this.usbkm = this.createUSBKeyMonitor();
		}
		if (this.nm == null) {
			this.nm = this.createNetworkMonitor(ud, this.checkTime);
		}
		if (this.sessionStatusManager == null) {
			this.sessionStatusManager = this.createSessionStatusManager(userName);
		}

		if (this.fileStore == null) {
			this.fileStore = this.createFileRepository();
			String autosave2store = ApplicationManager.getApplication().getPreferences().getPreference(this.getUser(), TachoFileStore.TACHOFILE_SAVE2STORE);
			this.changeTGDstore(this.fileStore.getTGDStore(), (autosave2store != null) && autosave2store.equals("0") ? false : true);
		}
		this.configureNotices();
	}

	private DownCenterSessionStatusManager createSessionStatusManager(String userName) {
		DownCenterSessionStatusManager sessionStatusManager = new DownCenterSessionStatusManager(this, userName);
		sessionStatusManager.start();
		return sessionStatusManager;
	}

	protected NetworkMonitor createNetworkMonitor(final IUserData ud, final int checkTime) {
		NetworkMonitor nm = null;
		if ((checkTime > 0) && ((ICDOUserData) ud).isAnonymousUser() && ((ICDOUserData) ud).isMonitor()) {
			nm = new NetworkMonitor(this, checkTime);
			nm.start();
		}
		return nm;
	}

	@Override
	public IUserData getUserData() throws Exception {
		this.ensureRemoteReferenceLocator();
		if ((this.userData == null) && (this.referenceLocatorServer != null)) {
			this.userData = ((IOpentachLocator) this.referenceLocatorServer).getUserData(this.getSessionId());
			if (this.userData != null) {
				// ((OpentachClient) ApplicationManager.getApplication()).setUserInfo(this.userData);
			}
		}
		return this.userData;
	}

	@Override
	public void reloadUserData() throws Exception {
		this.userData = null;
		this.getUserData();
	}

	protected void createSmartCardMonitor() {
		try {
			SmartCardMonitor dcm = this.getLocalService(SmartCardMonitor.class);
			final Application app = ApplicationManager.getApplication();
			final ApplicationPreferences prefs = app.getPreferences();
			final String user = this.getUser();
			String autosave = prefs.getPreference(user, SmartCardMonitor.SMARTCARD_AUTODOWNLOAD);
			if (autosave == null) {
				autosave = "1";
				prefs.setPreference(user, SmartCardMonitor.SMARTCARD_AUTODOWNLOAD, "1");
			}
			// Descarga automatica.
			dcm.setAutoDownload(Integer.valueOf(autosave).intValue() != 0);
			String showpreviewtc = prefs.getPreference(user, SmartCardMonitor.SMARTCARD_AUTODOWNLOAD);
			if (showpreviewtc == null) {
				// Por defecto el fichero no se previsualiza.
				showpreviewtc = "0";
				prefs.setPreference(user, SmartCardMonitor.SMARTCARD_NOTIFYONFINISH, "1");
			}
			if (app instanceof CardListener) {
				// en caso de ser un centro de descarga para quitar el salvapantallas
				dcm.addCardListener((CardListener) app);
			}
		} catch (Exception e) {
			DownCenterClientLocator.logger.error(null, e);
		}
	}

	private UploadMonitor createUploadMonitor(final IUserData ud, final HideThreadMonitor htm) {
		if (ud == null) {
			return null;
		}
		final Hashtable kv = new Hashtable();
		final String cif = ud.getCIF();
		if (cif != null) {
			final String cgContract = ud.getActiveContract(cif);
			kv.put(OpentachFieldNames.CIF_FIELD, cif);
			kv.put(OpentachFieldNames.CG_CONTRATO_FIELD, cgContract);
		}
		UploadMonitor upm = new UploadMonitor(kv, htm, DownCenterClientLocator.uriSonido);
		final Application app = ApplicationManager.getApplication();
		final ApplicationPreferences prefs = app.getPreferences();
		final String user = this.getUser();
		String val = prefs.getPreference(user, UploadMonitor.RUNNING_TAG);
		if (val == null) {
			prefs.setPreference(user, UploadMonitor.RUNNING_TAG, "0");
			val = "0";
		}
		if ((val != null) && !val.equals("0")) {
			upm.restart();
		} else {
			upm.pause();
		}
		return upm;
	}

	private TachoFileStore createFileRepository() {
		final ApplicationPreferences prefs = ApplicationManager.getApplication().getPreferences();
		String repository = prefs.getPreference(this.getUser(), TachoFileStore.TACHOFILE_STORE);
		if ((repository == null) || (repository.length() == 0)) {
			repository = System.getProperty("user.home") + File.separator + "OPENTACH_ALMACEN_FICHEROS" + File.separator;
		}
		try {
			File f = new File(repository);
			if (!f.exists()) {
				f.mkdirs();
			}
		} catch (SecurityException e) {
			DownCenterClientLocator.logger.error(null, e);
		}
		TachoFileStore store = new TachoFileStore();
		store.setTGDStore(new File(repository));
		return store;
	}

	private USBKeyMonitor createUSBKeyMonitor() {
		USBKeyMonitor usbkm = new USBKeyMonitor(this);
		return usbkm;
	}

	protected void configureNotices() {
		try {
			NoticeManagerFactory.setNoticesWindow(null);
		} catch (Exception e) {
			DownCenterClientLocator.logger.error(null, e);
		}
	}

	public void changeTGDstore(final File fDir, final boolean autosave2store) {
		try {
			final String user = this.getUser();
			final ApplicationPreferences prefs = ApplicationManager.getApplication().getPreferences();
			this.fileStore.setTGDStore(fDir);
			prefs.setPreference(user, TachoFileStore.TACHOFILE_STORE, fDir.getPath());
			prefs.setPreference(user, TachoFileStore.TACHOFILE_SAVE2STORE, autosave2store ? "1" : "0");
			this.upm.setDir("FICHEROS_TGD", fDir.getPath());
			this.getLocalService(SmartCardMonitor.class).setRepositoryDir(autosave2store ? fDir : null);
			this.usbkm.setRepositoryDir(autosave2store ? fDir : null);
		} catch (Exception ex) {
			DownCenterClientLocator.logger.error(null, ex);
		}
	}

	@Override
	public String getUserDescription() {
		if (this.cdoDscrCache == null) {
			this.cdoDscrCache = UserInfoProvider.getUserDescription(this);
		}
		return this.cdoDscrCache;
	}

	private IAlertSystem getAlertService() throws Exception {
		this.ensureRemoteReferenceLocator();
		IAlertSystem bServidor = (IAlertSystem) this.referenceLocatorServer;
		return bServidor;
	}

	@Override
	public String[] getAlertGroups() throws Exception {
		IAlertSystem bServidor = this.getAlertService();
		return bServidor.getAlertGroups();
	}

	@Override
	public EntityResult getAlertsGroupData(String group) throws Exception {
		IAlertSystem bServidor = this.getAlertService();
		return bServidor.getAlertsGroupData(group);
	}

	@Override
	public EntityResult getAllAlertsData() throws Exception {
		IAlertSystem bServidor = this.getAlertService();
		return bServidor.getAllAlertsData();
	}

	@Override
	public boolean pauseAlert(String name, String group) throws Exception {
		IAlertSystem bServidor = this.getAlertService();
		return bServidor.pauseAlert(name, group);
	}

	@Override
	public boolean resumeAlert(String name, String group) throws Exception {
		IAlertSystem bServidor = this.getAlertService();
		return bServidor.resumeAlert(name, group);
	}

	@Override
	public EntityResult getAlertData(String taskName, String taskGroup, String cronName, String cronGroup) throws Exception {
		IAlertSystem bServidor = this.getAlertService();
		return bServidor.getAlertData(taskName, taskGroup, cronName, cronGroup);
	}

	@Override
	public void updateAlertConfiguration(Hashtable alertConfiguration) throws Exception {
		IAlertSystem bServidor = this.getAlertService();
		bServidor.updateAlertConfiguration(alertConfiguration);
	}

	public void setLocale(Locale locale) throws Exception {
		IOpentachLocator bServidor = (IOpentachLocator) this.referenceLocatorServer;
		if (bServidor != null) {
			bServidor.setLocale(locale, this.getSessionId());
		}
	}

	@Override
	public DownloadMonitor getDownloadMonitor() {
		return this.dwm;
	}

	@Override
	public USBKeyMonitor getUSBKeyMonitor() {
		return this.usbkm;
	}

	@Override
	public UploadMonitor getUploadMonitor() {
		return this.upm;
	}

	@Override
	public TachoReadMonitor getTachoReadMonitor() {
		return null;
	}

	@Override
	public TicketPrinter getTicketPrinter() {
		if (this.tickPrinter == null) {
			this.tickPrinter = new TicketPrinter();
		}
		return this.tickPrinter;
	}

	@Override
	public TachoFileStore getFileStore() {
		return this.fileStore;
	}

	@Override
	public List<USBInfo> getUSBInfo() {
		if (this.usbInfoCache == null) {
			this.usbInfoCache = DBUtils.getUSBInfo(this);
		}
		return this.usbInfoCache;
	}

	@Override
	public NetworkMonitor getNetworkMonitor() {
		return this.nm;
	}
}
