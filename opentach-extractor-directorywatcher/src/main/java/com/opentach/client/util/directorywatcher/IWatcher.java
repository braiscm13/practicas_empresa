package com.opentach.client.util.directorywatcher;

/**
 * The Interface IFolderWatcher.
 *
 * @param <T>
 *            the generic type
 */
public interface IWatcher<T extends IWatcherSettings> {

	/**
	 * Unregister.
	 *
	 * @param info
	 *            the info
	 * @throws Exception
	 *             the exception
	 */
	void unregister(T info) throws Exception;

	/**
	 * Register.
	 *
	 * @param info
	 *            the info
	 * @throws Exception
	 *             the exception
	 */
	void register(T info) throws Exception;

}
