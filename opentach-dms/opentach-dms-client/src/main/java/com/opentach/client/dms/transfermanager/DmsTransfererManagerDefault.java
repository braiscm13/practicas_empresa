package com.opentach.client.dms.transfermanager;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.opentach.client.dms.transfermanager.AbstractDmsTransferable.Status;
import com.opentach.client.dms.upload.LocalDiskDmsUploadable;
import com.opentach.client.dms.upload.LocalDiskDmsUploader;
import com.opentach.client.util.UserInfoProvider;
import com.opentach.common.dms.DMSNaming;
import com.opentach.common.dms.DmsException;
import com.opentach.common.dms.IDMSService;
import com.utilmize.tools.thread.Paralelizer;

/**
 *
 * This class implements all funcionality to manage downloaders. It saves a list with all downloaders and methods to know status and list all of them.
 *
 */
public class DmsTransfererManagerDefault implements IDmsTransfererManager {

	private static final Logger logger = LoggerFactory.getLogger(DmsTransfererManagerDefault.class);

	/** Protected constructor */
	protected DmsTransfererManagerDefault() {
		super();
	}

	@Override
	public void init() {
		// do nothing
	}

	/**
	 * Execute transfer in thread
	 *
	 * @param transferable
	 * @throws DmsException
	 */
	@Override
	public void transfer(AbstractDmsTransferable transferable) throws DmsException {
		if (!transferable.getStatus().equals(Status.ON_PREPARE)) {
			throw new DmsException("Transferable already initiated");
		}
		Paralelizer.paralelizeInBackground(new TransferJob<>(transferable));
	}

	@Override
	public Path obtainDmsFileVersion(Serializable idFileVersion) throws Exception {
		EntityResult er = this.getDMSService().fileVersionQuery(idFileVersion,
				EntityResultTools.attributes(DMSNaming.DOCUMENT_FILE_VERSION_FILE_SIZE, DMSNaming.DOCUMENT_FILE_NAME), this.getSessionId());
		Long fileSize = ((List<Number>) er.get(DMSNaming.DOCUMENT_FILE_VERSION_FILE_SIZE)).get(0).longValue();
		String fileName = ((List<String>) er.get(DMSNaming.DOCUMENT_FILE_NAME)).get(0);
		return this.obtainDmsFileVersion(idFileVersion, fileName, fileSize);
	}

	@Override
	public Path obtainDmsFileVersion(Serializable idFileVersion, String fileName, Long fileSize) throws Exception {
		Path localFile = this.computeLocalFile(idFileVersion, fileName);
		if (!Files.exists(localFile)) {
			final DmsDownloadable dmsDownloadable = new DmsDownloadable(idFileVersion, localFile, fileName, fileSize);
			synchronized (dmsDownloadable) {
				dmsDownloadable.addObserver(new Observer() {
					@Override
					public void update(Observable o, Object arg) {
						if (dmsDownloadable.getStatus().isFinishState()) {
							synchronized (dmsDownloadable) {
								dmsDownloadable.notify();
							}
						}
					}
				});
				this.transfer(dmsDownloadable);
				try {
					dmsDownloadable.wait();
				} catch (InterruptedException error) {
					DmsTransfererManagerDefault.logger.error(null, error);
				}
			}
			if (!Status.COMPLETED.equals(dmsDownloadable.getStatus())) {
				throw new DmsException("E_GETTING_FILE");
			}
		}
		return localFile;
	}

	private Path computeLocalFile(Serializable idFileVersion, String fileName) {
		return Paths.get(System.getProperty("java.io.tmpdir"), idFileVersion + "_" + fileName);
	}

	protected class TransferJob<T extends AbstractDmsTransferable> implements Callable<Void> {
		protected T transferable;

		public TransferJob(T transferable) {
			super();
			this.transferable = transferable;
		}

		@Override
		public Void call() {
			try {
				if (this.transferable instanceof DmsDownloadable) {
					new DmsDownloader().transfer((DmsDownloadable) this.transferable);
				} else {
					new LocalDiskDmsUploader().transfer((LocalDiskDmsUploadable) this.transferable);
				}
			} catch (Exception ex) {
				this.transferable.setStatus(Status.ERROR);
				DmsTransfererManagerDefault.logger.error(null, ex);
			}
			return null;
		}
	}

	protected IDMSService getDMSService() throws DmsException {
		try {
			UserInfoProvider ocl = (UserInfoProvider) ApplicationManager.getApplication().getReferenceLocator();
			return ocl.getRemoteService(IDMSService.class);
		} catch (Exception error) {
			throw new DmsException(error);
		}
	}

	private int getSessionId() throws DmsException {
		try {
			return ApplicationManager.getApplication().getReferenceLocator().getSessionId();
		} catch (Exception error) {
			throw new DmsException(error);
		}
	}
}
