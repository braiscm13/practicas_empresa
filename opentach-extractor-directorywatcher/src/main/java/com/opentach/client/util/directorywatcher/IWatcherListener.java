package com.opentach.client.util.directorywatcher;

/**
 * The listener interface for receiving IFolderWatcher events. The class that is interested in processing a IFolderWatcher event implements this interface, and the object created
 * with that class is registered with a component using the component's <code>addIFolderWatcherListener<code> method. When the IFolderWatcher event occurs, that object's
 * appropriate method is invoked.
 *
 * @see IWatcherEvent
 */
public interface IWatcherListener<Q extends IWatcherSettings, T extends IWatcherEvent<Q>> {

	/**
	 * On folder watcher event.
	 *
	 * @param event
	 *            the event
	 */
	void onFolderWatcherEvent(T event);

	/**
	 * On error event.
	 *
	 * @param info
	 *            the info
	 * @param error
	 *            the error
	 */
	void onErrorEvent(Q info, Exception error);

}
