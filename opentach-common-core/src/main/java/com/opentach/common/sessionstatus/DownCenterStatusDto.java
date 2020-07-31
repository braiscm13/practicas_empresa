package com.opentach.common.sessionstatus;

/**
 * The Class CDOStatusDto.
 */
public class DownCenterStatusDto extends AbstractStatusDto {

	/** The download actions. */
	private int	downloadActions;

	/** The uploaded files. */
	private int	uploadedFiles;

	/** The upload fails. */
	private int	uploadFails;

	/** The show report timeouts. */
	private int	showReportTimeouts;

	/**
	 * Instantiates a new CDO status dto.
	 */
	public DownCenterStatusDto() {
		super();
	}

	/**
	 * Gets the download actions.
	 *
	 * @return the download actions
	 */
	public int getDownloadActions() {
		return this.downloadActions;
	}

	/**
	 * Gets the show report timeouts.
	 *
	 * @return the show report timeouts
	 */
	public int getShowReportTimeouts() {
		return this.showReportTimeouts;
	}

	/**
	 * Gets the uploaded files.
	 *
	 * @return the uploaded files
	 */
	public int getUploadedFiles() {
		return this.uploadedFiles;
	}

	/**
	 * Gets the upload fails.
	 *
	 * @return the upload fails
	 */
	public int getUploadFails() {
		return this.uploadFails;
	}

	/**
	 * Sets the download actions.
	 *
	 * @param downloadActions
	 *            the new download actions
	 */
	public void setDownloadActions(int downloadActions) {
		this.downloadActions = downloadActions;
	}

	/**
	 * Sets the show report timeouts.
	 *
	 * @param showReportTimeouts
	 *            the new show report timeouts
	 */
	public void setShowReportTimeouts(int showReportTimeouts) {
		this.showReportTimeouts = showReportTimeouts;
	}

	/**
	 * Sets the uploaded files.
	 *
	 * @param uploadedFiles
	 *            the new uploaded files
	 */
	public void setUploadedFiles(int uploadedFiles) {
		this.uploadedFiles = uploadedFiles;
	}

	/**
	 * Sets the upload fails.
	 *
	 * @param uploadFails
	 *            the new upload fails
	 */
	public void setUploadFails(int uploadFails) {
		this.uploadFails = uploadFails;
	}

	@Override
	public String getApp() {
		return "DOWNCENTER_CLIENT";
	}
}
