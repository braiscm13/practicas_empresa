package com.opentach.client.util.devicecontrol.states;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.client.util.TGDFileInfo;
import com.opentach.client.util.devicecontrol.AbstractDeviceController;
import com.opentach.client.util.devicecontrol.StateFactory.StateFactoryType;
import com.opentach.client.util.usbkey.USBEvent;
import com.opentach.client.util.usbkey.USBEvent.USBEventType;
import com.opentach.client.util.usbkey.USBFileInfo;

public class USBDownloadState extends BasicState {

	private static final Logger	logger	= LoggerFactory.getLogger(USBDownloadState.class);

	public USBDownloadState(AbstractDeviceController controller) {
		super(controller);
	}

	@Override
	public IState executeUSBEvent(USBEvent event) {
		USBDownloadState.logger.info("Execute usbEvent: {}", event);
		this.getController().setAutoDownloadCard(false);
		this.getController().setAutoDownloadKey(false);
		USBEventType uet = event.getType();
		if (uet == USBEventType.USB_DOWNLOAD_END) {
			USBDownloadState.logger.info("Usb downloading end");
			this.getController().endUSBDownload();
			List<USBFileInfo> lFInfo = event.getFileInfo();
			try {
				this.getController().uploadFiles(this.toTgdFileInfo(lFInfo));
			} catch (Exception ex) {
				USBDownloadState.logger.error(null, ex);
				this.getController().processUSBError(event.getMessage());
				this.getController().setTimerToValue(AbstractDeviceController.PAUSEMILLIS);
				return this.getState(StateFactoryType.USBERROR);
			}
			return this.getState(StateFactoryType.USBUPLOADING);
		} else if (uet == USBEventType.USB_DOWNLOAD_ERROR) {
			this.getController().processUSBError(event.getMessage());
			this.getController().setTimerToValue(AbstractDeviceController.PAUSEMILLIS);
			return this.getState(StateFactoryType.USBERROR);
		} else {
			return super.executeUSBEvent(event);
		}
	}

	private List<TGDFileInfo> toTgdFileInfo(List<USBFileInfo> lFInfo) {
		List<TGDFileInfo> res = new ArrayList<>();
		if (lFInfo != null) {
			for (USBFileInfo info : lFInfo) {
				res.add(new TGDFileInfo(info.getUsbFile(), info.getHddFile()));
			}
		}
		return res;
	}

	@Override
	public void handle() {
		USBDownloadState.logger.info("handling");
		this.getController().setKeyDownloading(true);
		this.getController().pauseUploadMonitor();
		this.getController().startUSBDownload();
	}
}