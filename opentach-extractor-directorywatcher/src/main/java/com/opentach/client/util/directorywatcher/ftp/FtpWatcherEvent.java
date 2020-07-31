package com.opentach.client.util.directorywatcher.ftp;

import java.nio.file.Path;

import com.opentach.client.util.directorywatcher.IWatcherEvent;

/**
 * The Class LocalFolderWatcherEvent.
 */
public class FtpWatcherEvent implements IWatcherEvent<FtpWatcherSettings> {

	/** The file. */
	private final Path					file;

	/** The watcher. */
	private final FtpWatcher		watcher;

	/** The ftp file. */
	private final IFtpFile				ftpFile;

	/** The folder info. */
	private final FtpWatcherSettings	watchFolderInfo;

	/**
	 * Instantiates a new local folder watcher event.
	 *
	 * @param file
	 *            the file
	 * @param ftpFile
	 *            the ftp file
	 * @param ftp
	 *            the ftp
	 * @param watcher
	 *            the watcher
	 * @param folderInfo
	 *            the folder info
	 */
	public FtpWatcherEvent(Path file, IFtpFile ftpFile, FtpWatcher watcher, FtpWatcherSettings folderInfo) {
		super();
		this.file = file;
		this.watcher = watcher;
		this.ftpFile = ftpFile;
		this.watchFolderInfo = folderInfo;
	}

	/*
	 * (non-Javadoc)
	 * @see com.opentach.client.util.directorywatcher.IFolderWatcherEvent#getFile()
	 */
	@Override
	public Path getFile() {
		return this.file;
	}

	/*
	 * (non-Javadoc)
	 * @see com.opentach.client.util.directorywatcher.IFolderWatcherEvent#getWatcher()
	 */
	@Override
	public FtpWatcher getWatcher() {
		return this.watcher;
	}

	/**
	 * Gets the ftp file.
	 *
	 * @return the ftp file
	 */
	public IFtpFile getFtpFile() {
		return this.ftpFile;
	}

	/**
	 * Gets the ftp watcher.
	 *
	 * @return the ftp watcher
	 */
	public FtpWatcher getFtpWatcher() {
		return this.getWatcher();
	}

	/*
	 * (non-Javadoc)
	 * @see com.opentach.client.util.directorywatcher.IFolderWatcherEvent#getWatchFolderInfo()
	 */
	@Override
	public FtpWatcherSettings getWatchFolderInfo() {
		return this.watchFolderInfo;
	}
}
