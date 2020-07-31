package com.opentach.server.downcenterreport;

import java.io.ByteArrayInputStream;
import java.util.Date;

public class FileInfoReport {

	/** The file content. */
	private final ByteArrayInputStream	fileContent;

	/** The d process. */
	private final Date					dProcess;

	/**
	 * Instantiates a new file info.
	 *
	 * @param file
	 *            the file
	 * @param dProcess
	 *            the d process
	 */
	public FileInfoReport(ByteArrayInputStream file, Date dProcess) {
		this.fileContent = file;
		this.dProcess = dProcess;
	}

	/**
	 * Gets the file content.
	 *
	 * @return the file content
	 */
	public ByteArrayInputStream getFileContent() {
		return this.fileContent;
	}

	/**
	 * Gets the d process.
	 *
	 * @return the d process
	 */
	public Date getDProcess() {
		return this.dProcess;
	}
}