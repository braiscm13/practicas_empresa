package com.opentach.client.util.directorywatcher.local;

import java.nio.file.Path;

import com.opentach.client.util.directorywatcher.IWatcher;
import com.opentach.client.util.directorywatcher.IWatcherEvent;

/**
 * The Class LocalFolderWatcherEvent.
 */
public class LocalWatcherEvent implements IWatcherEvent<LocalWatcherSettings> {

	/** The file. */
	private final Path				file;
	private final IWatcher	watcher;
	private final LocalWatcherSettings	watchFolderInfo;

	/**
	 * Instantiates a new local folder watcher event.
	 *
	 * @param file
	 *            the file
	 */
	public LocalWatcherEvent(Path file, LocalWatcher watcher, LocalWatcherSettings folderInfo) {
		super();
		this.file = file;
		this.watcher = watcher;
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

	@Override
	public IWatcher getWatcher() {
		return this.watcher;
	}

	@Override
	public LocalWatcherSettings getWatchFolderInfo() {
		return this.watchFolderInfo;
	}

}
