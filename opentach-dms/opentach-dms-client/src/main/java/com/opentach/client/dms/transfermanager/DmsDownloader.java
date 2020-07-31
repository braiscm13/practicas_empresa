package com.opentach.client.dms.transfermanager;

import java.io.BufferedOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ExtendedOperationThread;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.util.remote.BytesBlock;
import com.opentach.client.dms.tools.DMSConstants;
import com.opentach.client.util.operationmonitor.OperationMonitor;
import com.opentach.common.dms.DmsException;
import com.opentach.common.dms.IDMSService;

/**
 *
 * This class implements the Downloader to download files from server
 *
 */
public class DmsDownloader extends AbstractDmsTransferer<DmsDownloadable> {

	private static final Logger logger = LoggerFactory.getLogger(DmsDownloader.class);


	@Override
	protected void doTransfer(DmsDownloadable transferable) throws DmsException {
		Path targetFile = transferable.getTargetFile();
		if (Files.exists(targetFile)) {
			FileTools.deleteQuitely(targetFile);
		}

		final IDMSService entF = this.getDMSService();
		DmsDownloader.logger.info("Downloading file {}", transferable.getTargetFile());

		ExtendedOperationThread eop = new ExtendedOperationThread("<<  " + transferable.getTargetFile()) {
			@Override
			public void run() {
				this.hasStarted = true;
				final int blockSize = DMSConstants.BLOCK_SIZE;
				this.setPriority(Thread.MIN_PRIORITY);
				try (BufferedOutputStream bOut = new BufferedOutputStream(Files.newOutputStream(transferable.getTargetFile(), StandardOpenOption.CREATE_NEW))) {
					this.status = ApplicationManager.getTranslation("INICIANDO_TRANSFERENCIA");
					String rId = entF.prepareToTransfer(transferable.getIdVersionFile(), DmsDownloader.this.getSessionId());
					this.status = ApplicationManager.getTranslation("ARCHIVO_DESCARGADO");
					this.progressDivisions = transferable.getSize().intValue();
					BytesBlock by = null;
					int leidosTotales = 0;
					long tIni = System.currentTimeMillis();
					long tiempoTranscurrido = 0;
					while ((by = entF.getBytes(rId, leidosTotales, blockSize, DmsDownloader.this.getSessionId())) != null) {
						Thread.yield(); // Permite que la barra de progreso
						// se refresque más dinamicamente.
						bOut.write(by.getBytes());
						leidosTotales = leidosTotales + by.getBytes().length;
						DmsDownloader.logger.info("Descargados {} bytes", leidosTotales);
						this.currentPosition = leidosTotales;
						if (this.isCancelled()) {
							this.hasFinished = true;
							this.status = ApplicationManager.getTranslation("OPERACION_CANCELADA");
							bOut.close();
							return;
						}
						tiempoTranscurrido = System.currentTimeMillis() - tIni;
						this.estimatedTimeLeft = (int) (((transferable.getSize() - leidosTotales) * tiempoTranscurrido) / (float) leidosTotales);
					}
					// Es necesario cerrar el buffered o hacer un flush para
					// que los datos leidos esten disponibles.
					bOut.close();

					this.currentPosition = this.progressDivisions;
					this.status = ApplicationManager.getTranslation("Finalizado");
				} catch (Throwable error) {
					DmsDownloader.logger.error(null, error);
					this.res = error.getMessage();
					this.status = ApplicationManager.getTranslation("Error") + " : " + error.getMessage();
				}
			}
		};
		OperationMonitor.ExtOpThreadsMonitor threadsMonitor = OperationMonitor.getExtOpThreadsMonitor();
		threadsMonitor.addExtOpThread(eop);
		threadsMonitor.setVisible(true);
	}

}
