package com.opentach.client;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.AdvancedEntity;
import com.ontimize.db.DirectSQLQueryEntity;
import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.Application;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.preferences.ApplicationPreferences;
import com.ontimize.util.alerts.IAlertSystem;
import com.ontimize.util.notice.NewNoticeWindow;
import com.ontimize.util.notice.NoticeManagerFactory;
import com.opentach.client.devicecontrol.BasicDeviceController;
import com.opentach.client.devicecontrol.FolderWatcherFtpEventListener;
import com.opentach.client.devicecontrol.FolderWatcherLocalEventListener;
import com.opentach.client.laf.LafController;
import com.opentach.client.sessionstatus.ClientSessionStatusManager;
import com.opentach.client.util.DBUtils;
import com.opentach.client.util.HideThreadMonitor;
import com.opentach.client.util.ILocalPreferenceChangeListener;
import com.opentach.client.util.LocalPreferencesManager;
import com.opentach.client.util.SoundManager;
import com.opentach.client.util.devicecontrol.AbstractDeviceController;
import com.opentach.client.util.directorywatcher.AbstractWatcherSettings;
import com.opentach.client.util.directorywatcher.IWatcherSettings;
import com.opentach.client.util.directorywatcher.WatcherManager;
import com.opentach.client.util.download.DownloadMonitor;
import com.opentach.client.util.net.NetworkMonitor;
import com.opentach.client.util.upload.UploadMonitor;
import com.opentach.client.util.usbkey.USBInfo;
import com.opentach.client.util.usbkey.USBInfoProvider;
import com.opentach.client.util.usbkey.USBKeyMonitor;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.interfaces.IOpentachLocator;
import com.opentach.common.tacho.TachoFileStore;
import com.opentach.common.user.ICDOUserData;
import com.opentach.common.user.IUserData;
import com.opentach.model.comm.vu.TachoConfig;
import com.opentach.model.comm.vu.TachoReadMonitor;
import com.opentach.model.scard.CardListener;
import com.opentach.model.scard.SmartCardMonitor;

public class OpentachClientLocator extends AbstractOpentachClientLocator implements MonitorProvider, IAlertSystem, USBInfoProvider {

	private static final Logger			logger						= LoggerFactory.getLogger(OpentachClientLocator.class);

	protected static final String		SCHECKTIME					= "checktime";

	protected int						checkTime					= -1;

	protected USBKeyMonitor				usbkm						= null;
	protected DownloadMonitor			dwm							= null;
	protected UploadMonitor				upm							= null;
	protected TachoReadMonitor			trm							= null;
	protected HideThreadMonitor			htm							= null;
	protected AbstractDeviceController	devc						= null;
	protected NetworkMonitor			nm							= null;
	private ClientSessionStatusManager	sessionStatusManager		= null;
	protected TachoFileStore			fileStore					= null;
	protected List<USBInfo>				usbInfoCache				= null;

	public OpentachClientLocator(Hashtable params) {
		super(params);
		String sCheckTime = (String) params.get(OpentachClientLocator.SCHECKTIME);
		if (sCheckTime != null) {
			try {
				this.checkTime = Integer.parseInt(sCheckTime);
			} catch (Exception e) {
			}
		}
	}

	@Override
	protected void onSessionStarted(String userName) throws Exception {
		LafController.getInstance().installCustomLaf(this.getUser());
		IUserData ud = this.getUserData();
		if (this.htm == null) {
			this.htm = new HideThreadMonitor();
		}
		if (this.dwm == null) {
			this.dwm = new DownloadMonitor(this.htm, SoundManager.uriSonido);
		}
		if (this.trm == null) {
			this.trm = this.createTachoReadMonitor();
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
		if (this.devc == null) {
			this.devc = this.createDeviceController(ud, this.getLocalService(SmartCardMonitor.class), this.usbkm, this.upm, this.nm);
		}
		if (this.sessionStatusManager == null) {
			this.sessionStatusManager = this.createSessionStatusManager(userName);
		}

		WatcherManager.getInstance().setLocalFileDetectionListener(new FolderWatcherLocalEventListener());
		WatcherManager.getInstance().setFtpFileDetectionListener(new FolderWatcherFtpEventListener());

		// preferencias locales
		String preference = LocalPreferencesManager.getInstance().getPreference(LocalPreferencesManager.PROP_TGD_WATCH_FOLDER);
		if ((preference != null) && !"".equals(preference)) {
			try {
				WatcherManager.getInstance().register(AbstractWatcherSettings.fromPreference(preference));
			} catch (Exception ex) {
				OpentachClientLocator.logger.error(null, ex);
			}
		}
		LocalPreferencesManager.getInstance().addPropertyChangeListener(new ILocalPreferenceChangeListener() {

			@Override
			public void onPropertyChanged(String key, String oldValue, String newValue) {
				if (!key.equals(LocalPreferencesManager.PROP_TGD_WATCH_FOLDER)) {
					return;
				}
				if ((oldValue != null) && !"".equals(oldValue)) {
					IWatcherSettings info = AbstractWatcherSettings.fromPreference(oldValue);
					try {
						WatcherManager.getInstance().unregister(info);
					} catch (Exception e) {
						OpentachClientLocator.logger.error(null, e);
					}
				}
				if ((newValue != null) && !"".equals(newValue)) {
					IWatcherSettings info = AbstractWatcherSettings.fromPreference(newValue);
					try {
						WatcherManager.getInstance().register(info);
					} catch (Exception error) {
						throw new RuntimeException(error);
					}
				}
			}
		});

		// 2011/12/22 Los centros de descarga mantendrán el look and
		// feel
		if (((ICDOUserData) ud).isUSBDownloadUser()) {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		if (this.fileStore == null) {
			this.fileStore = this.createFileRepository();
			String autosave2store = ApplicationManager.getApplication().getPreferences().getPreference(this.getUser(), TachoFileStore.TACHOFILE_SAVE2STORE);
			this.changeTGDstore(this.fileStore.getTGDStore(), (autosave2store != null) && autosave2store.equals("0") ? false : true);
		}
		this.configureNotices();
	}

	private ClientSessionStatusManager createSessionStatusManager(String userName) {
		ClientSessionStatusManager sessionStatusManager = new ClientSessionStatusManager(this, userName);
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

	protected AbstractDeviceController createDeviceController(final IUserData ud, final SmartCardMonitor dcm, final USBKeyMonitor usbkm, final UploadMonitor upm,
			final NetworkMonitor nm) {
		if (!((ICDOUserData) ud).isAnonymousUser()) {
			AbstractDeviceController devc = new BasicDeviceController(this);
			if (dcm != null) {
				dcm.addCardListener(devc);
			}
			usbkm.addUSBListener(devc);
			upm.addUploadListener(devc);
			if (nm != null) {
				nm.addNetworkMonitorListener(devc);
			}
			return devc;
		}
		return null;
	}

	protected void createSmartCardMonitor() {
		try {
			SmartCardMonitor dcm = this.getLocalService(SmartCardMonitor.class);
			final Application app = ApplicationManager.getApplication();
			final ApplicationPreferences prefs = app.getPreferences();
			final String user = this.getUser();
			// dcm = SmartCardMonitor.getInstance();
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
			OpentachClientLocator.logger.error(null, e);
		}
	}

	protected UploadMonitor createUploadMonitor(final IUserData ud, final HideThreadMonitor htm) {
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
		UploadMonitor upm = new UploadMonitor(kv, htm, SoundManager.uriSonido);
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

	protected TachoFileStore createFileRepository() {
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
			OpentachClientLocator.logger.error(null, e);
		}
		TachoFileStore store = new TachoFileStore();
		store.setTGDStore(new File(repository));
		return store;
	}

	protected TachoReadMonitor createTachoReadMonitor() {
		final TachoReadMonitor trm = new TachoReadMonitor();
		final String user = this.getUser();
		final ApplicationPreferences prefs = ApplicationManager.getApplication().getPreferences();
		String autosave = prefs.getPreference(user, TachoConfig.TACHO_AUTODOWNLOAD);
		String port = prefs.getPreference(user, TachoConfig.TACHO_PORT);
		String period = prefs.getPreference(user, TachoConfig.TACHO_DOWLOAD_PERIOD);
		String from = prefs.getPreference(user, TachoConfig.TACHO_DOWNLOAD_DAYFROM);
		String to = prefs.getPreference(user, TachoConfig.TACHO_DOWNLOAD_DAYFROM);
		if (autosave == null) {
			autosave = "1";
			prefs.setPreference(user, TachoConfig.TACHO_AUTODOWNLOAD, "1");
		}
		// Activo el monitor del tacografo.
		if ((port != null) && (port.length() > 0)) {
			trm.setPort(port);
		}
		if (period != null) {
			Date f = null;
			Date t = null;
			DateFormat df = DateFormat.getDateInstance();
			try {
				if ((from != null) && (to != null)) {
					f = df.parse(from);
					t = df.parse(to);
				}
			} catch (Exception e) {
				OpentachClientLocator.logger.error(null, e);
			}
			trm.setDownloadPeriod(Integer.parseInt(period), f, t);
		}
		// Activo Descarga automatica.
		trm.setEnable(Integer.valueOf(autosave).intValue() != 0);
		String notifyOnFinish = prefs.getPreference(user, TachoConfig.TACHO_NOTIFYONFINISH);
		if (notifyOnFinish == null) {
			notifyOnFinish = "1";
			prefs.setPreference(user, TachoConfig.TACHO_NOTIFYONFINISH, "1");
		}
		trm.setNotifyOnFinish(notifyOnFinish.equals("1") ? true : false);
		return trm;
	}

	private USBKeyMonitor createUSBKeyMonitor() {
		return new USBKeyMonitor(this);
	}

	protected void configureNotices() {
		try {
			IUserData ud = this.getUserData();
			if (((ICDOUserData) ud).isAnonymousUser()) {
				NoticeManagerFactory.setNoticesWindow(null);
			} else {
				((NewNoticeWindow) NoticeManagerFactory.getNoticeWindow(true)).setFormReference("manageravisos", "formnoticesreceived.xml");
			}
		} catch (Exception e) {
			OpentachClientLocator.logger.error(null, e);
		}
	}

	public void changeTGDstore(final File fDir, final boolean autosave2store) {
		try {
			final String user = this.getUser();
			final ApplicationPreferences prefs = ApplicationManager.getApplication().getPreferences();
			this.fileStore.setTGDStore(fDir);
			prefs.setPreference(user, TachoFileStore.TACHOFILE_STORE, fDir.getPath());
			prefs.setPreference(user, TachoFileStore.TACHOFILE_SAVE2STORE, autosave2store ? "1" : "0");
			if (this.upm != null) {
				this.upm.setDir("FICHEROS_TGD", fDir.getPath());
			}
			if (this.trm != null) {
				this.trm.setRepositoryDir(autosave2store ? fDir : null);
			}

			this.getLocalService(SmartCardMonitor.class).setRepositoryDir(autosave2store ? fDir : null);
			if (this.usbkm != null) {
				this.usbkm.setRepositoryDir(autosave2store ? fDir : null);
			}
		} catch (Exception ex) {
			OpentachClientLocator.logger.error(null, ex);
		}
	}

	@Override
	public String[] getAlertGroups() throws Exception {
		IAlertSystem bServidor = (IAlertSystem) this.referenceLocatorServer;
		return bServidor.getAlertGroups();
	}

	@Override
	public EntityResult getAlertsGroupData(String group) throws Exception {
		IAlertSystem bServidor = (IAlertSystem) this.referenceLocatorServer;
		return bServidor.getAlertsGroupData(group);
	}

	@Override
	public EntityResult getAllAlertsData() throws Exception {
		IAlertSystem bServidor = (IAlertSystem) this.referenceLocatorServer;
		return bServidor.getAllAlertsData();
	}

	@Override
	public boolean pauseAlert(String name, String group) throws Exception {
		IAlertSystem bServidor = (IAlertSystem) this.referenceLocatorServer;
		return bServidor.pauseAlert(name, group);
	}

	@Override
	public boolean resumeAlert(String name, String group) throws Exception {
		IAlertSystem bServidor = (IAlertSystem) this.referenceLocatorServer;
		return bServidor.resumeAlert(name, group);
	}

	@Override
	public EntityResult getAlertData(String taskName, String taskGroup, String cronName, String cronGroup) throws Exception {
		IAlertSystem bServidor = (IAlertSystem) this.referenceLocatorServer;
		return bServidor.getAlertData(taskName, taskGroup, cronName, cronGroup);
	}

	@Override
	public void updateAlertConfiguration(Hashtable alertConfiguration) throws Exception {
		IAlertSystem bServidor = (IAlertSystem) this.referenceLocatorServer;
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
		return this.trm;
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

	@Override
	protected Class<?>[] getOntimizeEntityInterfaces() {
		return new Class<?>[] { Entity.class, AdvancedEntity.class, DirectSQLQueryEntity.class };
	}
}
