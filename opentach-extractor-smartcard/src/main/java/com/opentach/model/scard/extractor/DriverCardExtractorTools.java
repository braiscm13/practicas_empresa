package com.opentach.model.scard.extractor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardTerminal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.extractor.ExtractorProgressEvent;
import com.imatia.tacho.extractor.IExtractorProgressListener;
import com.imatia.tacho.extractor.scard.CardFileInfoDriver;
import com.imatia.tacho.extractor.scard.SmartCardExtractor;
import com.imatia.tacho.extractor.scard.SmartCardExtractorSettings;
import com.imatia.tacho.extractor.scard.TCFile;
import com.imatia.tacho.extractor.tacho.ConnectorHangException;
import com.imatia.tacho.extractor.tacho.TachoException;
import com.imatia.tacho.model.GenerationEnum;
import com.imatia.tacho.model.TachoFile;
import com.opentach.model.scard.CardEvent;
import com.opentach.model.scard.CardEvent.CardEventType;
import com.opentach.model.scard.SmartCardMonitor;
import com.opentach.model.scard.SmartCardMonitor.SmartCardType;

public class DriverCardExtractorTools {

	private static final Logger				logger	= LoggerFactory.getLogger(DriverCardExtractorTools.class);


	public DriverCardExtractorTools() {
		super();
	}

	public void extractDriverCardFiles(File targetFolder, SmartCardMonitor smon) {
		ATR atr = null;
		SmartCardType typeCardInserted = smon.getTypeCardInserted();
		try {
			targetFolder = this.ensureDriverCardExtraction(targetFolder, smon);
		} catch (Exception error) {
			DriverCardExtractorTools.logger.error(null, error);
			String msg = error.getMessage();
			smon.notifyCardStatusChange(new CardEvent(this, CardEventType.CARD_ERROR, typeCardInserted, atr, msg));
			return;
		}

		// RAFA: 1º lo guardo en temp, luego lo paso al dir con el nombre ok
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String tmpDir = System.getProperty("java.io.tmpdir");
		if (!tmpDir.endsWith("\\") && !tmpDir.endsWith("/")) {
			tmpDir += "/";
		}
		String filename = tmpDir + sdf.format(new Date());

		File file = new File(filename);
		try {

			Card card = smon.getCurrentCard();
			atr = card.getATR();
			smon.notifyCardStatusChange(new CardEvent(this, CardEventType.CARD_DOWNLOAD_START, typeCardInserted, atr, "Descargando fichero: " + filename));
			try {

				FileOutputStream fos = new FileOutputStream(file);
				SmartCardExtractorSettings settings = new SmartCardExtractorSettings(new SmartCardEtractorProgressListener(smon), fos, new SmartcardioAPDUConnector(card),
						atr.getBytes());
				card.beginExclusive();
				try {
					SmartCardExtractor extractor = new SmartCardExtractor(settings) {
						@Override
						public void extractTCFile(OutputStream os, TCFile info, GenerationEnum generation) throws TachoException, IOException, ConnectorHangException {
							if (info == CardFileInfoDriver.TC_EF_IC) {
								byte[] ic = smon.getIc();
								os.write(ic);
							} else if (info == CardFileInfoDriver.TC_EF_ICC) {
								byte[] icc = smon.getIcc();
								os.write(icc);
							} else {
								super.extractTCFile(os, info, generation);
							}
						}
					};
					extractor.extract();
				} finally {
					card.endExclusive();
				}

				fos.close();
				try {
					file = this.arrangeCardFile(file, smon);
				} catch (Exception error) {
					smon.notifyCardStatusChange(new CardEvent(this, CardEventType.CARD_ERROR, typeCardInserted, atr, error.getMessage(), file));
					return;
				}
				smon.notifyCardStatusChange(new CardEvent(this, CardEventType.CARD_DOWNLOAD_END, typeCardInserted, atr, null, file));
			} finally {
				try {
					card.endExclusive();
					// card.disconnect(true);
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
			DriverCardExtractorTools.logger.error(null, e);
			smon.notifyCardStatusChange(new CardEvent(this, CardEventType.CARD_ERROR, typeCardInserted, atr, "ERROR descarga fichero " + filename, file));
		}
	}

	protected File arrangeCardFile(File file, SmartCardMonitor smon) throws Exception {
		if (file != null) {
			String validname = null;
			try {
				com.imatia.tacho.model.TachoFile driverfile2 = com.imatia.tacho.model.TachoFile.readTachoFile(file);
				validname = driverfile2.computeFileName(null, TachoFile.FILENAME_FORMAT_SPAIN, Calendar.getInstance());
				if (validname == null) {
					throw new Exception("Nombre de fichero no valido");
				}
			} catch (Exception e) {
				DriverCardExtractorTools.logger.error(null, e);
				throw new Exception("M_FICHERO_TC_NO_VALIDO");
			}
			// Fuerzo la subida al servidor del fichero.
			String rightfilename = smon.getRepositoryDir().getAbsolutePath() + File.separator + validname;
			// RAFA dice: si el fichero existe, lo machaco
			File fr = new File(rightfilename);
			if (fr.exists()) {
				fr.delete();
			}
			boolean moving = file.renameTo(fr);
			if (!moving) {
				throw new Exception("M_ERROR_MOVING_FILE");
			}
			file.delete();
			return fr;
		}
		return file;
	}

	protected File ensureDriverCardExtraction(File targetFolder, SmartCardMonitor smon) throws Exception {
		CardTerminal cardTerminal = smon.getCardTerminal();
		// if (cardTerminal == null) {
		// throw new Exception("M_ERROR_DISPOSITIVO");
		// }
		if (!smon.isDriverCardInserted()) {
			throw new Exception("M_UNKNOW_SMARTCARD");
		}
		// Card card = cardTerminal.connect("T=1");
		// try {
		// card.beginExclusive();
		// } catch (CardException e) {
		// throw new Exception("M_ERROR_DISPOSITIVO");
		// } finally {
		// card.endExclusive();
		// // card.disconnect(true);
		// }

		// Si no se especifica directorio descargo al almacen.
		if (targetFolder == null) {
			targetFolder = smon.getRepositoryDir();
			if ((targetFolder != null) && !targetFolder.exists()) {
				if (!targetFolder.mkdirs()) {
					throw new Exception("M_ERROR_CREATING_FOLDER");
				}
			}
		}
		if (targetFolder == null) {
			throw new Exception("M_NO_REPOSITORY_DEFINED");
		}
		return targetFolder;
	}

	class SmartCardEtractorProgressListener implements IExtractorProgressListener {
		private final SmartCardMonitor smon;

		public SmartCardEtractorProgressListener(SmartCardMonitor smon) {
			super();
			this.smon = smon;
		}

		@Override
		public void progressUpdated(ExtractorProgressEvent progressEvent) {
			this.smon.notifyCardStatusChange(
					new CardEvent(this, CardEventType.CARD_DOWNLOAD_PROGRESS, this.smon.getTypeCardInserted(), this.smon.getCurrentCard()
							.getATR(), progressEvent.getMessage(), progressEvent.getMessageParameters(), progressEvent.getCurrentPart(), progressEvent.getTotalParts()));
		}

	}
}
