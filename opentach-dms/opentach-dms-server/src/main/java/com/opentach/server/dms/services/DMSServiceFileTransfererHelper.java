package com.opentach.server.dms.services;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.RandomStringGenerator;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.util.remote.BytesBlock;
import com.opentach.common.dms.DmsException;

/**
 * The Class FileTransferer.
 */
public class DMSServiceFileTransfererHelper {

	/** The Constant logger. */
	private static final Logger				logger	= LoggerFactory.getLogger(DMSServiceFileTransfererHelper.class);

	/** The file helper. */
	private final DMSServiceFileHelper		fileHelper;

	/** The transfer logs. */
	private final Map<String, TransefLog>	transferLogs;

	/**
	 * Instantiates a new file transferer.
	 *
	 * @param helper
	 *            the helper
	 */
	public DMSServiceFileTransfererHelper(DMSServiceFileHelper helper) {
		super();
		this.fileHelper = helper;
		this.transferLogs = new HashMap<String, TransefLog>();
	}

	/**
	 * Prepare to transfer.
	 *
	 * @param versionId
	 *            the version id
	 * @param con
	 *            the con
	 * @return the string
	 * @throws DmsException
	 *             the exception
	 */
	public synchronized String prepareToTransfer(final Serializable versionId, Connection con) throws DmsException {
		TransefLog fileLog = this.createTransferFileLog(versionId, con);
		return fileLog.getTransferId();
	}

	/**
	 * Gets the size.
	 *
	 * @param transferId
	 *            the transfer id
	 * @return the size
	 * @throws DmsException
	 *             the exception
	 */
	/*
	 * (non-Javadoc)
	 * @see com.ontimize.db.TableEntity#getSize(java.lang.String)
	 */
	public long getSize(String transferId) throws DmsException {
		TransefLog ifl = this.getTransferLog(transferId);
		CheckingTools.failIf(ifl.getFile() == null, "No se encuentra el fichero de la transferencia");
		try {
			return Files.size(ifl.getFile());
		} catch (IOException err) {
			throw new DmsException(err);
		}
	}

	/**
	 * Devuelve un BytesBlock con los bytes desde offset y longitud length. Devuelve null si se ha alcanzado el final de archivo. Debe llamarse antes a prepareForTransfer para
	 * obtener 'transferId'.
	 *
	 * @param transferId
	 *            the transfer id
	 * @param offset
	 *            the offset
	 * @param lenght
	 *            the lenght
	 * @param sesionId
	 *            the sesion id
	 * @return the bytes
	 * @throws DmsException
	 *             the exception
	 */
	public BytesBlock getBytes(String transferId, int offset, int lenght) throws DmsException {
		TransefLog transferLog = this.getTransferLog(transferId);
		try (FileChannel fc = FileChannel.open(transferLog.getFile(), StandardOpenOption.READ)) {
			// try (RandomAccessFile ra = new RandomAccessFile(transferLog.getFile().toFile(), "r")) {
			long longitudArchivo = fc.size();
			if (offset >= longitudArchivo) {
				this.releaseTransefLog(transferId);
				return null;
			}
			fc.position(offset);
			long bytesRestantes = longitudArchivo - offset;
			int toReadSize = (int) Math.min(bytesRestantes, lenght);
			ByteBuffer bytes = ByteBuffer.allocate(toReadSize);
			int readed = fc.read(bytes);
			BytesBlock bytesBlock = null;
			if (readed == toReadSize) {
				bytesBlock = new BytesBlock(bytes.array());
			} else {
				byte[] tmp = new byte[readed];
				bytes.put(tmp, 0, readed);
				bytesBlock = new BytesBlock(tmp);
			}
			return bytesBlock;
		} catch (Exception err) {
			throw new DmsException(err);
		}
	}

	/**
	 * Gets the transfer file log to transfer.
	 *
	 * @param versionId
	 *            the version id
	 * @param con
	 *            the con
	 * @return the transfer file log to transfer
	 * @throws DmsException
	 *             the exception
	 */
	public TransefLog createTransferFileLog(Serializable versionId, Connection con) throws DmsException {
		Path path = this.fileHelper.getPhysicalFileFor(versionId, con);
		CheckingTools.failIf(!Files.exists(path), "CAN_NOT_FIND_FILE_IN_HD");

		synchronized (this.transferLogs) {
			String transferId = RandomStringGenerator.generate(10);
			while (this.transferLogs.containsKey(transferId)) {
				transferId = RandomStringGenerator.generate(10);
			}
			TransefLog iofl = new TransefLog(transferId, versionId, path);
			this.transferLogs.put(transferId, iofl);
			return iofl;
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
				DMSServiceFileTransfererHelper.logger.error("Id no encontrado {}", transferId);
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
	 * The Class TransefLog.
	 */
	private static class TransefLog {

		/** The transfer id. */
		private final String		transferId;

		/** The file. */
		private final Path			file;

		/** The version id. */
		private final Serializable	versionId;

		/**
		 * Instantiates a new transef log.
		 *
		 * @param transferId
		 *            the transfer id
		 * @param versionId
		 *            the version id
		 * @param path
		 *            the path
		 */
		public TransefLog(String transferId, Serializable versionId, Path path) {
			super();
			this.transferId = transferId;
			this.versionId = versionId;
			this.file = path;
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
		 * Gets the version id.
		 *
		 * @return the version id
		 */
		public Serializable getVersionId() {
			return this.versionId;
		}

	}

}
