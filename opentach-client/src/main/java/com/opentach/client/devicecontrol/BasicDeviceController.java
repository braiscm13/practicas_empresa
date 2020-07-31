package com.opentach.client.devicecontrol;

import java.awt.Window;
import java.util.List;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imlabs.client.laf.utils.ColorChangerImageManager;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.images.IImageManager;
import com.ontimize.gui.images.ImageManager;
import com.opentach.client.MonitorProvider;
import com.opentach.client.util.SoundManager;
import com.opentach.client.util.TGDFileInfo;
import com.opentach.client.util.devicecontrol.AbstractDeviceController;
import com.opentach.client.util.devicecontrol.DefaultStateFactory;
import com.opentach.client.util.devicecontrol.StateFactory;
import com.opentach.client.util.devicecontrol.StateFactory.StateFactoryType;
import com.opentach.client.util.devicecontrol.states.CardUploadErrorState;
import com.opentach.client.util.devicecontrol.states.CardUploadingState;
import com.opentach.client.util.devicecontrol.states.IState;
import com.opentach.client.util.ui.JMessageDialog;
import com.opentach.client.util.upload.UploadEvent;

public class BasicDeviceController extends AbstractDeviceController {

	private static final Logger	logger	= LoggerFactory.getLogger(BasicDeviceController.class);
	private CardWaitDialog		cwd;
	private final Window		parent;

	private static final String	imgPath	= "devices-i18n/images/";
	private static final String	imgname	= "DescargarDatosSmall.gif";

	public BasicDeviceController(MonitorProvider uinfo) {
		super(uinfo, SoundManager.uriSonido, false);

		this.parent = ApplicationManager.getApplication().getFrame();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Locale locale = ApplicationManager.getLocale();
				BasicDeviceController.this.cwd = new CardWaitDialog(BasicDeviceController.this.parent);
				try {
					IImageManager engine = ImageManager.getEngine();
					ImageIcon imgBackground = null;
					String imgFullPath = BasicDeviceController.imgPath + locale + "/" + BasicDeviceController.imgname;
					if (engine instanceof ColorChangerImageManager) {
						imgBackground = ((ColorChangerImageManager) engine).getIcon(imgFullPath, false);
					} else {
						imgBackground = engine.getIcon(imgFullPath);
					}
					BasicDeviceController.this.cwd.setIcon(imgBackground);
				} catch (Exception ex) {
					BasicDeviceController.logger.error(null, ex);
				}
			}
		});
	}

	@Override
	protected StateFactory createStateFactory() {
		StateFactory sf = new DefaultStateFactory(this);
		sf.registerState(StateFactoryType.CARDUPLOADING, new CardUploadingBasicState(BasicDeviceController.this));
		return sf;
	}

	@Override
	protected void showErrorMessage(final String message) {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				String msg = message == null ? "ERROR" : ApplicationManager.getTranslation(message);
				JMessageDialog.showMessageDialogWithTimeOut(BasicDeviceController.this.parent, ApplicationManager.getTranslation(msg),
						ApplicationManager.getTranslation("ERROR"), JOptionPane.ERROR_MESSAGE);
			}
		};
		this.executeInEDTH(r);
	}

	@Override
	public void cardError(final String msg) {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				BasicDeviceController.this.cwd.setVisible(false);
				BasicDeviceController.this.showErrorMessage(msg);
			}
		};
		this.executeInEDTH(r);
	}

	@Override
	public void endCardDownload() {
		this.setVisible(false);
	}

	@Override
	public void startCardDownload() {
		this.setVisible(true);
	}

	@Override
	public void downloadProgress(String message, Object[] msgParameters, int currentPart, int totalParts) {
		// do nothing
	}

	@Override
	public void uploadError(boolean isDown, List<TGDFileInfo> lFileInfo) {
		// showErrorMessage("Error");
	}

	@Override
	public void init() {
		this.setVisible(false);
	}

	@Override
	public void uploadFinished(List<TGDFileInfo> lFileInfo) {

	}

	@Override
	public void print() {

	}

	@Override
	public void generateReport(boolean ignoreInfractions) {

	}

	@Override
	public void showReport(Object htmltext, boolean limitedInfo) {

	}

	@Override
	public void thanks() {

	}

	@Override
	public void onUsbNotFound() {

	}

	@Override
	public void processUSBError(String msg) {
		this.setVisible(false);
		this.showErrorMessage(msg);
	}

	@Override
	public void endUSBDownload() {
		this.setVisible(false);
	}

	@Override
	public void startUSBDownload() {
		this.setVisible(true);
	}

	private void setVisible(final boolean visible) {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				BasicDeviceController.this.cwd.setVisible(visible);
			}
		};
		this.executeInEDTH(r);
	}

	@Override
	public void unavailableService() {

	}

	public class CardUploadingBasicState extends CardUploadingState {
		public CardUploadingBasicState(AbstractDeviceController controller) {
			super(controller);
		}

		@Override
		public IState executeUploadEvent(UploadEvent e) {
			IState as = super.executeUploadEvent(e);
			if (as instanceof CardUploadErrorState) {
				return BasicDeviceController.this.sf.getState(StateFactoryType.INIT);
			}
			return as;
		}
	}
}
