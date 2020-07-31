package com.opentach.server.tachofiles;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.model.TachoFile;

/**
 * The Class TachoFileTools.
 */
public final class TachoFileTools {
	public static String		UNKNOWN_DRIVER	= "XXXXXXXXX";

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(TachoFileTools.class);

	/**
	 * Instantiates a new tacho file tools.
	 */
	private TachoFileTools() {
		super();
	}

	/**
	 * Gets the tacho file.
	 *
	 * @param file
	 *            the file
	 * @return the tacho file
	 * @throws Exception
	 *             the exception
	 */
	public static TachoFile getTachoFile(File file) throws Exception {
		try {
			return TachoFile.readTachoFile(file);
		} catch (Exception err) {
			TachoFileTools.logger.error("Invalid card driver or vehicle unit file format: {}", file.getName(), err);
			throw new Exception("Formato de fichero de tacografo o tarjeta de conductor no válido");
		}
	}

	/**
	 * Gets the tacho file.
	 *
	 * @param file
	 *            the file
	 * @return the tacho file
	 * @throws Exception
	 *             the exception
	 */
	public static TachoFile getTachoFile(byte[] file) throws Exception {
		try {
			return TachoFile.readTachoFile(file);
		} catch (Exception err) {
			TachoFileTools.logger.error("Invalid card driver or vehicle unit file format", err);
			throw new Exception("Formato de fichero de tacografo o tarjeta de conductor no válido");
		}
	}

	public static String extractDriverDni(String driverId, String nationAlpha) {
		boolean spanish = "E".equals(nationAlpha);
		if (spanish) {
			return TachoFileTools.getSpanishDriverDni(driverId);
		}
		return (nationAlpha == null ? "X" : nationAlpha) + "_" + driverId;
	}

	private static String getSpanishDriverDni(String driverCardId) {
		try {
			return driverCardId.substring(1, 10);
		} catch (StringIndexOutOfBoundsException iex) {
		} catch (Exception ex) {
			TachoFileTools.logger.error(null, ex);
		}
		return TachoFileTools.UNKNOWN_DRIVER;
	}
}
