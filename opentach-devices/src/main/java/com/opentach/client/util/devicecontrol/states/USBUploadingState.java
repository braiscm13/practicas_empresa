package com.opentach.client.util.devicecontrol.states;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.client.util.TGDFileInfo;
import com.opentach.client.util.devicecontrol.AbstractDeviceController;
import com.opentach.client.util.devicecontrol.StateFactory.StateFactoryType;
import com.opentach.client.util.upload.UploadEvent;
import com.opentach.client.util.upload.UploadEvent.UploadEventType;

public class USBUploadingState extends BasicState {

	private static final Logger	logger	= LoggerFactory.getLogger(USBUploadingState.class);

	public USBUploadingState(AbstractDeviceController controller) {
		super(controller);
	}

	public void deleteKeyFiles(List<TGDFileInfo> lFiles) {
		for (TGDFileInfo tfi : lFiles) {
			USBUploadingState.logger.info("Fichero {} subido con exitosamente={} para ser borrado.", tfi.getUsbFile(), tfi.isSuccess());
			if (tfi.isSuccess() && (tfi.getUsbFile() != null)) {
				if (!tfi.getUsbFile().delete()) {
					USBUploadingState.logger.warn("No se pudo borrar el fichero subido {}", tfi.getOrigFile());
				} else {
					USBUploadingState.logger.info("Fichero {} borrado.", tfi.getUsbFile());
				}
			}
		}
	}

	@Override
	public IState executeUploadEvent(UploadEvent event) {
		USBUploadingState.logger.info("Execute uploadEvent: {}", event);
		UploadEventType uet = event.getType();
		if (uet == UploadEventType.FILE_UPLOAD_END) {
			this.getController().uploadFinished(event.getFiles());
			// borro archivos
			this.deleteKeyFiles(event.getFiles());
			if (this.getController().isWithPrintBranch()) {
				this.getController().setTimerToValue(AbstractDeviceController.PAUSEMILLIS);
				return this.getState(StateFactoryType.USBUPLOADED);
			}
			if (this.getController().getNetworkStatus()) {
				return this.getState(StateFactoryType.INIT);
			}
			return this.getState(StateFactoryType.UNAVAILABLE);
		} else if ((uet == UploadEventType.CONTRACT_ERROR) || (uet == UploadEventType.DRIVER_VEH_ERROR) || (uet == UploadEventType.FILE_UPLOAD_ERROR)) {
			this.getController().setTimerToValue(AbstractDeviceController.PAUSEMILLIS);
			final boolean isDown = (uet == UploadEventType.FILE_UPLOAD_ERROR);
			// this.deleteKeyFiles(event.getFiles());
			this.getController().uploadError(isDown, event.getFiles());
			return this.getState(StateFactoryType.USBUPLOADERROR);
		} else if (uet.equals(UploadEventType.FILE_UPLOAD_PROGRESS)) {
			this.getController().downloadProgress(null, null, event.getCurrent(), event.getTotal());
			return null;
		} else {
			return super.executeUploadEvent(event);
		}
	}
}