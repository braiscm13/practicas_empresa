package com.opentach.client.util.directorywatcher.local;

import com.opentach.client.util.directorywatcher.AbstractWatcherSettings;
import com.opentach.client.util.directorywatcher.IWatcherSettings.WatchFolderMode;

/**
 * The Class LocalFolderWatchFolderInfo.
 */
public class LocalWatcherSettings extends AbstractWatcherSettings {

	/** The local folder. */
	private String localFolder;

	/**
	 * Instantiates a new local folder watch folder info.
	 *
	 * @param localFolder
	 *            the local folder
	 */
	public LocalWatcherSettings(String localFolder) {
		this(localFolder, null);
	}

	/**
	 * Instantiates a new local folder watch folder info.
	 *
	 * @param localFolder
	 *            the local folder
	 * @param companyId
	 *            the company id
	 */
	public LocalWatcherSettings(String localFolder, String companyId) {
		super(companyId, WatchFolderMode.LOCAL);
		this.localFolder = localFolder;
	}

	/**
	 * Gets the local folder.
	 *
	 * @return the local folder
	 */
	public String getLocalFolder() {
		return this.localFolder;
	}

	/**
	 * Sets the local folder.
	 *
	 * @param localFolder
	 *            the new local folder
	 */
	public void setLocalFolder(String localFolder) {
		this.localFolder = localFolder;
	}

}