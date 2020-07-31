package com.opentach.client.util.upload;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.locator.PermissionReferenceLocator;
import com.ontimize.util.remote.BytesBlock;
import com.opentach.client.util.upload.TGDFileSendThread.ProgressNotifier;
import com.opentach.common.ITGDFileConstants;
import com.opentach.common.interfaces.AdvancedFileManagementEntity;

public class UploadFileStrategyEFicherosTGD {

	private static final Logger logger = LoggerFactory.getLogger(UploadFileStrategyEFicherosTGD.class);

	public void upload(int sesId, String filename, Hashtable<String, Object> cv, byte[] fileData, ProgressNotifier progressNotifier) throws Exception {
		final PermissionReferenceLocator erl = (PermissionReferenceLocator) ApplicationManager.getApplication().getReferenceLocator();
		AdvancedFileManagementEntity ent = (AdvancedFileManagementEntity) erl.getEntityReference(ITGDFileConstants.FILE_ENTITY);
		String rId = null;
		// Preparo le entidad para la recepcion del fichero
		rId = ent.prepareToReceive(cv, filename, "", sesId);
		try {
			if (progressNotifier.isCancelled()) {
				ent.cancelReceiving(rId, sesId);
				return;
			}
			progressNotifier.updateStatus("EnviandoFicheroEntrada");

			ByteArrayInputStream bIn = new ByteArrayInputStream(fileData);
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
				progressNotifier.setCurrentPosition(leidosTotales);
				if (leidos >= TGDFileSendThread.BLOCK_SIZE) {
					Thread.yield();
					leidos = 0;
					ent.putBytes(rId, new BytesBlock(bOut.toByteArray()), sesId);
					bOut.reset();
					tiempoTranscurrido = System.currentTimeMillis() - tIni;
					progressNotifier.setEstimatedTimeLeft((int) (((fileData.length - leidosTotales) * tiempoTranscurrido) / (float) leidosTotales));
					if (progressNotifier.isCancelled()) {
						ent.cancelReceiving(rId, sesId);
						return;
					}
				}
			}
			// Ahora lo que quede.
			if (bOut.size() > 0) {
				ent.putBytes(rId, new BytesBlock(bOut.toByteArray()), sesId);
			}
			ent.finishReceivingReturnKey(rId, sesId);
		} catch (Exception ex) {
			if ((rId != null) && (ent != null)) {
				try {
					ent.cancelReceiving(rId, sesId);
				} catch (Exception error) {
					UploadFileStrategyEFicherosTGD.logger.error(null, error);
				}
			}
			throw ex;
		}

	}

}
