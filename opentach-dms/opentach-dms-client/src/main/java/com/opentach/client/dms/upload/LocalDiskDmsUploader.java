package com.opentach.client.dms.upload;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.Hashtable;
import java.util.concurrent.CancellationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ExtendedOperationThread;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.locator.PermissionReferenceLocator;
import com.ontimize.util.remote.BytesBlock;
import com.opentach.client.dms.tools.DMSConstants;
import com.opentach.client.util.operationmonitor.OperationMonitor;
import com.opentach.common.dms.DMSNaming;
import com.opentach.common.dms.DmsException;

/**
 *
 * This class implements the Uploader for localdisk requests
 *
 */
public class LocalDiskDmsUploader extends AbstractDmsUploader<LocalDiskDmsUploadable> {

	private static final Logger logger = LoggerFactory.getLogger(LocalDiskDmsUploader.class);

	@Override
	protected InputStream doDownloadFromSource(LocalDiskDmsUploadable transferable) throws DmsException {
		try {
			return Files.newInputStream(transferable.getFile());
		} catch (Exception error) {
			throw new DmsException("E_DOWNLOADING", error);
		}
	}

	@Override
	protected void doUploadToDms(InputStream is, LocalDiskDmsUploadable transferable) throws DmsException {

		LocalDiskDmsUploader.logger.info("Downloading file {}", transferable.getName());

		ExtendedOperationThread eop = new ExtendedOperationThread(">>  " + transferable.getName()) {
			@Override
			public void run() {
				this.hasStarted = true;
				this.status = this.updateStatus("InicializandoTransferencia");
				this.setPriority(Thread.MIN_PRIORITY);
				final long totalSize = transferable.getSize();
				final PermissionReferenceLocator erl = (PermissionReferenceLocator) ApplicationManager.getApplication().getReferenceLocator();
				try {
					Hashtable<String, Object> cv = LocalDiskDmsUploader.this.getAVFromTransferable(transferable);
					this.status = this.updateStatus("LeyendoFicheroEntrada");
					this.progressDivisions = (int) totalSize;
					// Escribo
					this.uploadFile(transferable.getDocumentIdentifier().getDocumentId(), erl.getSessionId(), transferable.getName(), cv, is, transferable.getSize(),
							transferable.getDescription());
					if (this.isCancelled()) {
						this.status = this.updateStatus("Cancelado");
						return;
					}
					this.status = this.updateStatus("Finalizado");
					this.currentPosition = this.progressDivisions;
				} catch (CancellationException ex) {
					return;
				} catch (Throwable error) {
					MessageManager.getMessageManager().showExceptionMessage(error, LocalDiskDmsUploader.logger);
				} finally {
					this.hasFinished = true;
				}
			}

			private Object uploadFile(Serializable documentId, int sesId, String filename, Hashtable<String, Object> cv, InputStream bIn, long fileSize, String description)
					throws Exception {
				String rId = null;
				// Preparo le entidad para la recepcion del fichero
				rId = LocalDiskDmsUploader.this.getDMSService().prepareToReceive(documentId, cv, sesId);
				try {
					if (this.isCancelled()) {
						LocalDiskDmsUploader.this.getDMSService().cancelReceiving(rId, sesId);
						return null;
					}
					this.status = this.updateStatus("EnviandoFicheroEntrada");

					ByteArrayOutputStream bOut = new ByteArrayOutputStream();
					int by = -1;
					int leidos = 0;
					int leidosTotales = 0;
					long tIni = System.currentTimeMillis();
					long tiempoTranscurrido = 0;
					while ((by = bIn.read()) != -1) {
						bOut.write(by);
						leidos++;
						leidosTotales++;
						this.currentPosition = leidosTotales;
						if (leidos >= DMSConstants.BLOCK_SIZE) {
							Thread.yield();
							leidos = 0;
							LocalDiskDmsUploader.this.getDMSService().putBytes(rId, new BytesBlock(bOut.toByteArray()), sesId);
							bOut.reset();
							tiempoTranscurrido = System.currentTimeMillis() - tIni;
							this.estimatedTimeLeft = (int) (((fileSize - leidosTotales) * tiempoTranscurrido) / (float) leidosTotales);
							if (this.isCancelled()) {
								LocalDiskDmsUploader.this.getDMSService().cancelReceiving(rId, sesId);
								return null;
							}
						}
					}
					// Ahora lo que quede.
					if (bOut.size() > 0) {
						LocalDiskDmsUploader.this.getDMSService().putBytes(rId, new BytesBlock(bOut.toByteArray()), sesId);
					}
					return LocalDiskDmsUploader.this.getDMSService().finishReceiving(rId, sesId);
				} catch (Exception ex) {
					if ((rId != null)) {
						try {
							LocalDiskDmsUploader.this.getDMSService().cancelReceiving(rId, sesId);
						} catch (Exception error) {
							LocalDiskDmsUploader.logger.error(null, error);
						}
					}
					throw ex;
				}
			}

			private String updateStatus(String string) {
				if (string == null) {
					return "";
				}
				return ApplicationManager.getTranslation(string, ApplicationManager.getApplicationBundle());
			}
		};
		OperationMonitor.ExtOpThreadsMonitor threadsMonitor = OperationMonitor.getExtOpThreadsMonitor();
		threadsMonitor.addExtOpThread(eop);
		threadsMonitor.setVisible(true);

	}

	@Override
	protected Hashtable<String, Object> getAVFromTransferable(LocalDiskDmsUploadable transferable) {
		Hashtable<String, Object> attrs = new Hashtable<String, Object>();
		MapTools.safePut(attrs, DMSNaming.DOCUMENT_FILE_NAME, transferable.getName());
		MapTools.safePut(attrs, DMSNaming.CATEGORY_ID_CATEGORY, transferable.getCategoryId());
		MapTools.safePut(attrs, DMSNaming.DOCUMENT_FILE_VERSION_FILE_DESCRIPTION, transferable.getDescription());
		return attrs;
	}


}
