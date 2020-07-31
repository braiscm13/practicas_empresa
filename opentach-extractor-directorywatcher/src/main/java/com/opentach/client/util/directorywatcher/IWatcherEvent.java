package com.opentach.client.util.directorywatcher;

import java.nio.file.Path;

/**
 * The Interface IFolderWatcherEvent.
 */
public interface IWatcherEvent<T extends IWatcherSettings> {

	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	Path getFile();

	/**
	 * Gets the watcher.
	 *
	 * @return the watcher
	 */
	IWatcher getWatcher();

	/**
	 * Gets the watch folder info.
	 *
	 * @return the watch folder info
	 */
	T getWatchFolderInfo();

}
