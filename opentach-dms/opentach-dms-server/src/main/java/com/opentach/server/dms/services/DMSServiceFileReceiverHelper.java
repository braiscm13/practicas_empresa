package com.opentach.server.dms.services;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.RandomStringGenerator;
import com.ontimize.util.remote.BytesBlock;
import com.opentach.common.dms.DmsException;
import com.opentach.common.dms.DocumentIdentifier;

/**
 * The Class FileReceiver.
 */
public class DMSServiceFileReceiverHelper {

	/** The Constant logger. */
	private static final Logger				logger	= LoggerFactory.getLogger(DMSServiceFileReceiverHelper.class);

	/** The transfer logs. */
	private final Map<String, TransefLog>	transferLogs;

	/** The file helper. */
	private final DMSServiceFileHelper		fileHelper;

	/**
	 * Instantiates a new file receiver.
	 *
	 * @param fileHelper
	 *            the file helper
	 */
	public DMSServiceFileReceiverHelper(DMSServiceFileHelper fileHelper) {
		super();
		this.fileHelper = fileHelper;
		this.transferLogs = new HashMap<String, TransefLog>();
	}

	/**
	 * Prepare to receive.
	 *
	 * @param documentId
	 *            the document id
	 * @param av
	 *            the av
	 * @return the string
	 * @throws DmsException
	 *             the exception
	 */
	public String prepareToReceive(Serializable documentId, Hashtable<?, ?> av) throws DmsException {
		TransefLog ifl = this.createTransferFileLog(documentId, av);
		DMSServiceFileReceiverHelper.logger.trace("Receiving file for id {}, tmp file {}", ifl.getTransferId(), ifl.getFile());
		return ifl.getTransferId();
	}

	/**
	 * Gets the transfer file log to transfer.
	 *
	 * @param documentId
	 *            the document id
	 * @param av
	 *            the con
	 * @return the transfer file log to transfer
	 * @throws DmsException
	 *             the exception
	 */
	public TransefLog createTransferFileLog(Serializable documentId, Hashtable<?, ?> av) throws DmsException {
		try {
			Path path = Files.createTempFile("DMS", null);
			synchronized (this.transferLogs) {
				String transferId = RandomStringGenerator.generate(10);
				while (this.transferLogs.containsKey(transferId)) {
					transferId = RandomStringGenerator.generate(10);
				}
				TransefLog iofl = new TransefLog(transferId, documentId, av, path);
				this.transferLogs.put(transferId, iofl);
				return iofl;
			}

		} catch (Exception err) {
			throw new DmsException(err);
		}
	}

	/**
	 * Gets the in out file log.
	 *
	 * @param transferId
	 *            the id
	 * @return the in out file log
	 * @throws DmsException
	 *             the exception
	 */
	private TransefLog getTransferLog(String transferId) throws DmsException {
		synchronized (this.transferLogs) {
			TransefLog transefLog = this.transferLogs.get(transferId);
			if (transefLog == null) {
				DMSServiceFileReceiverHelper.logger.error("Id no encontrado {}", transferId);
				throw new DmsException("ReceivingId no encontrado");
			}
			return transefLog;
		}
	}

	/**
	 * Release in out file log.
	 *
	 * @param id
	 *            the id
	 */
	private void releaseTransefLog(String id) {
		synchronized (this.transferLogs) {
			this.transferLogs.remove(id);
		}
	}

	/**
	 * Put bytes.
	 *
	 * @param receivingId
	 *            the receiving id
	 * @param bytesBlock
	 *            the bytes block
	 * @throws DmsException
	 *             the exception
	 */
	/*
	 * (non-Javadoc)
	 * @see com.ontimize.db.TableEntity#putBytes(java.lang.String, com.ontimize.util.remote.BytesBlock, int)
	 */
	public void putBytes(String receivingId, BytesBlock bytesBlock) throws DmsException {
		TransefLog ifl = this.getTransferLog(receivingId);
		DMSServiceFileReceiverHelper.logger.trace("putting {} bytes for id {} in file {}", bytesBlock.getBytes().length, receivingId, ifl.getFile());
		Path fTemporal = ifl.getFile();
		try (OutputStream fOut = Files.newOutputStream(fTemporal, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
				BufferedOutputStream bOut = new BufferedOutputStream(fOut)) {
			bOut.write(bytesBlock.getBytes());
			bOut.flush();
		} catch (Exception error) {
			DMSServiceFileReceiverHelper.logger.error("putBytes: Cancelando fichero adjunto por error: {}", error.getMessage(), error);
			this.cancelReceiving(receivingId);
			throw new DmsException(error);
		}
	}

	/**
	 * Cancel receiving.
	 *
	 * @param receivingId
	 *            the receiving id
	 * @throws DmsException
	 *             the exception
	 * @todo: No existe posibilidad de cancelar la transmision desde el cliente. Unicamente se puede cancelar no pidiendo más bytes al servidor, pero en el servidor el fichero
	 *        queda abierto.
	 */
	public void cancelReceiving(String receivingId) throws DmsException {
		try {
			DMSServiceFileReceiverHelper.logger.trace("Canceling receiving of {}", receivingId);
			TransefLog ifl = this.getTransferLog(receivingId);
			boolean borrado = Files.deleteIfExists(ifl.getFile());
			if (borrado) {
				DMSServiceFileReceiverHelper.logger.info("Borrado fichero adjunto por cancelación: {}", ifl.getFile());
			} else {
				DMSServiceFileReceiverHelper.logger.info("NO SE PUDO BORRAR fichero adjunto por cancelación: {}", ifl.getFile());
			}
		} catch (Exception error) {
			throw new DmsException(error);
		} finally {
			this.releaseTransefLog(receivingId);
		}
	}

	/**
	 * Finish receiving.
	 *
	 * @param receivingId
	 *            the receiving id
	 * @param con
	 *            the con
	 * @return the document identifier
	 * @throws DmsException
	 *             the exception
	 */
	/*
	 * (non-Javadoc)
	 * @see com.ontimize.db.TableEntity#finishReceiving(java.lang.String, int)
	 */
	public DocumentIdentifier finishReceiving(final String receivingId, String user, Connection con) throws DmsException {
		TransefLog transferLog = this.getTransferLog(receivingId);
		try (InputStream is = Files.newInputStream(transferLog.getFile(), StandardOpenOption.READ)) {
			return this.fileHelper.fileInsert(transferLog.getDocumentId(), transferLog.getAv(), user, is, con);
		} catch (Exception error) {
			throw new DmsException(error);
		} finally {
			this.releaseTransefLog(receivingId);
			try {
				boolean borrado = Files.deleteIfExists(transferLog.getFile());
				if (!borrado) {
					DMSServiceFileReceiverHelper.logger.info("No se pudo borrar el fichero temporal: {}", transferLog.getFile());
				}
			} catch (IOException err) {
				DMSServiceFileReceiverHelper.logger.error(null, err);
			}
		}
	}

	/**
	 * Gets the column helper.
	 *
	 * @return the column helper
	 */
	protected DMSColumnHelper getColumnHelper() {
		return this.fileHelper.getColumnHelper();
	}

	/**
	 * The Class TransefLog.
	 */
	private static class TransefLog {

		/** The transfer id. */
		private final String			transferId;

		/** The file. */
		private final Path				file;

		/** The av. */
		private final Hashtable<?, ?>	av;

		/** The document id. */
		private final Serializable		documentId;

		/**
		 * Instantiates a new transef log.
		 *
		 * @param transferId
		 *            the transfer id
		 * @param documentId
		 *            the document id
		 * @param av
		 *            the av
		 * @param path
		 *            the path
		 */
		public TransefLog(String transferId, Serializable documentId, Hashtable<?, ?> av, Path path) {
			super();
			this.transferId = transferId;
			this.av = av;
			this.file = path;
			this.documentId = documentId;
		}

		/**
		 * Gets the transfer id.
		 *
		 * @return the transfer id
		 */
		public String getTransferId() {
			return this.transferId;
		}

		/**
		 * Gets the file.
		 *
		 * @return the file
		 */
		public Path getFile() {
			return this.file;
		}

		/**
		 * Gets the av.
		 *
		 * @return the av
		 */
		public Hashtable<?, ?> getAv() {
			return this.av;
		}

		/**
		 * Gets the document id.
		 *
		 * @return the document id
		 */
		public Serializable getDocumentId() {
			return this.documentId;
		}
	}
}
