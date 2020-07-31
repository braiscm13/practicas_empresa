package com.opentach.client.util.devicecontrol;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.client.MonitorProvider;
import com.opentach.client.util.TGDFileInfo;
import com.opentach.client.util.devicecontrol.StateFactory.StateFactoryType;
import com.opentach.client.util.devicecontrol.states.IState;
import com.opentach.client.util.net.NetworkEvent;
import com.opentach.client.util.net.NetworkEvent.NetworkEventType;
import com.opentach.client.util.net.NetworkMonitorListener;
import com.opentach.client.util.upload.UploadEvent;
import com.opentach.client.util.upload.UploadListener;
import com.opentach.client.util.upload.UploadMonitor;
import com.opentach.client.util.usbkey.USBEvent;
import com.opentach.client.util.usbkey.USBKeyMonitor;
import com.opentach.client.util.usbkey.USBListener;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.companies.IContractService;
import com.opentach.common.user.IUserData;
import com.opentach.model.scard.CardEvent;
import com.opentach.model.scard.CardListener;
import com.opentach.model.scard.SmartCardMonitor;

public abstract class AbstractDeviceController
implements OpentachFieldNames, CardListener, USBListener, UploadListener, ActionListener, NetworkMonitorListener {

	private static final Logger	logger			= LoggerFactory.getLogger(AbstractDeviceController.class);
	public static final int		PAUSEMILLIS		= 10000;
	public static final int		PRINTMILLIS		= 120000;
	protected boolean			uploadStatus;
	protected final boolean		withPrintBranch;
	protected transient boolean	cardDownloading	= false;
	protected transient boolean	keyDownloading	= false;
	protected transient boolean	networkStatus;
	protected MonitorProvider	monitorProvider;
	protected IState			state;
	protected Timer				timer;
	protected StateFactory		sf;
	protected String			uriSonido;

	public AbstractDeviceController(final MonitorProvider uinfo, final String uriSonido, final boolean withPrintBranch) {
		super();
		this.monitorProvider = uinfo;
		this.sf = this.createStateFactory();
		this.timer = new Timer(Integer.MAX_VALUE, this);
		this.timer.setInitialDelay(0);
		this.timer.setRepeats(false);
		this.state = this.sf.getState(StateFactoryType.INIT);
		this.uriSonido = uriSonido;
		this.networkStatus = true;
		this.withPrintBranch = withPrintBranch;
	}

	protected StateFactory createStateFactory() {
		return new DefaultStateFactory(this);
	}

	@Override
	public void cardStatusChange(final CardEvent ce) {
		IState s = this.state.execute(ce);
		this.handle(s);
	}

	@Override
	public void usbStatusChange(USBEvent ue) {
		IState s = this.state.execute(ue);
		this.handle(s);
	}

	@Override
	public void uploadStatusChange(UploadEvent upe) {
		IState s = this.state.execute(upe);
		this.handle(s);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		AbstractDeviceController.logger.info("Timeout in state {}", this.state);
		IState s = this.state.execute(e);
		this.handle(s);
	}


	@Override
	public void networkStatusChange(NetworkEvent e) {
		NetworkEventType net = e.getType();
		if (NetworkEventType.NETWORK_UP == net) {
			this.networkStatus = true;
		} else {
			this.networkStatus = false;
		}
		IState s = this.state.execute(e);
		this.handle(s);
	}

	protected void handle(IState s) {
		AbstractDeviceController.logger.info("Current state {}, go to handle {}", this.state, s);
		IState sBack = this.state;
		if (s != null) {
			this.state = s;
		}
		if ((s != sBack) && (this.state != null)) {
			this.state.handle();
		}
	}

	public void setTimerToValue(final int delay) {
		AbstractDeviceController.logger.info("Timer to value {}", delay);
		if (delay == 0) {
			this.timer.stop();
		} else {
			this.timer.setInitialDelay(delay);
			this.timer.setDelay(delay);
			this.timer.restart();
		}
	}

	protected void executeInEDTH(Runnable r) {
		if (SwingUtilities.isEventDispatchThread()) {
			r.run();
		} else {
			SwingUtilities.invokeLater(r);
		}
	}

	public boolean getNetworkStatus() {
		return this.networkStatus;
	}

	public StateFactory getStateFactory() {
		return this.sf;
	}

	public void uploadFile(File file) throws Exception {
		TGDFileInfo tgdfi = new TGDFileInfo(file);
		this.uploadFiles(Arrays.asList(tgdfi));
	}

	public void uploadFiles(List<TGDFileInfo> list) throws Exception {
		UploadMonitor up = this.monitorProvider.getUploadMonitor();
		IUserData ud = this.monitorProvider.getUserData();
		Hashtable<String, Object> kv = new Hashtable<String, Object>();
		if (ud.getCIF() != null) {
			kv.put(OpentachFieldNames.CIF_FIELD, ud.getCIF());
		}
		String cVigente = this.monitorProvider.getRemoteService(IContractService.class).getContratoVigente(ud.getCIF(), this.monitorProvider.getSessionId());
		if (cVigente != null) {
			kv.put(OpentachFieldNames.CG_CONTRATO_FIELD, cVigente);
		}
		up.sendTGDFiles(list, "FICHERO", kv, null, !ud.isAnonymousUser(), this.uriSonido);
	}

	public void pauseUploadMonitor() {
		UploadMonitor up = this.monitorProvider.getUploadMonitor();
		this.uploadStatus = up.isRunning();
		if (this.uploadStatus) {
			up.pause();
		}
	}

	public void restartUploadMonitor() {
		UploadMonitor up = this.monitorProvider.getUploadMonitor();
		if (this.uploadStatus) {
			up.restart();
		}
	}
	public void setAutoDownloadCard(boolean download) {
		SmartCardMonitor dcm = this.monitorProvider.getLocalService(SmartCardMonitor.class);
		if (dcm != null) {
			dcm.setAutoDownload(download);
		}
	}

	public void setAutoDownloadKey(boolean download) {
		USBKeyMonitor ukm = this.monitorProvider.getUSBKeyMonitor();
		if (ukm != null) {
			ukm.setAutoDownload(download);
		}
	}

	public void setCardDownloading(boolean cardDownloading) {
		this.cardDownloading = cardDownloading;
	}

	public boolean isCardDownloading() {
		return this.cardDownloading;
	}

	public boolean isKeyDownloading() {
		return this.keyDownloading;
	}

	public void setKeyDownloading(boolean keyDownloading) {
		this.keyDownloading = keyDownloading;
	}

	public boolean isWithPrintBranch() {
		return this.withPrintBranch;
	}

	public IUserData getUserData() throws Exception {
		return this.monitorProvider.getUserData();
	}

	public USBKeyMonitor getUSBKeyMonitor() {
		return this.monitorProvider.getUSBKeyMonitor();
	}

	// CARD

	public abstract void init();

	protected abstract void showErrorMessage(String message);

	public abstract void startCardDownload();

	public abstract void endCardDownload();

	public abstract void cardError(String msg);

	// COMMON

	public abstract void downloadProgress(String message, Object[] msgParameters, int currentPart, int totalParts);

	public abstract void uploadError(boolean isDown, List<TGDFileInfo> lFileInfo);

	public abstract void uploadFinished(List<TGDFileInfo> lFileInfo);

	public abstract void print();

	public abstract void thanks();

	// USB

	public abstract void startUSBDownload();

	public abstract void endUSBDownload();

	public abstract void processUSBError(String msg);

	public abstract void onUsbNotFound();

	// REPORT

	public abstract void generateReport(boolean ignoreInfractions);

	public abstract void showReport(Object text, boolean limitedInfo);

	// UNAVAILABLE
	public abstract void unavailableService();




}
