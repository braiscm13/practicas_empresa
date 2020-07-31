package com.opentach.client.dms.transfermanager;

import java.io.Serializable;
import java.nio.file.Path;

import com.opentach.common.dms.DmsException;

/**
 * The Interface IDmsTransfererManager.
 */
public interface IDmsTransfererManager {

	/**
	 * Inits the.
	 */
	void init();

	/**
	 * Transfer.
	 *
	 * @param transferable
	 *            the transferable
	 * @throws DmsException
	 *             the dms exception
	 */
	void transfer(AbstractDmsTransferable transferable) throws DmsException;


	/**
	 * Gets the dms file version.
	 *
	 * @param idFileVersion
	 *            the id file version
	 * @return the dms file version
	 * @throws DmsException
	 * @throws Exception
	 */
	Path obtainDmsFileVersion(Serializable idFileVersion) throws Exception;

	/**
	 * Gets the dms file version.
	 *
	 * @param idFileVersion
	 *            the id file version
	 * @param fileName
	 *            the file name
	 * @param fileSize
	 *            the file size
	 * @return the dms file version
	 * @throws DmsException
	 *             the dms exception
	 * @throws Exception
	 */
	Path obtainDmsFileVersion(Serializable idFileVersion, String fileName, Long fileSize) throws Exception;
}
