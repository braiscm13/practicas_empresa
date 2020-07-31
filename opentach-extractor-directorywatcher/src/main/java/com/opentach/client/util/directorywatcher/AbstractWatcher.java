package com.opentach.client.util.directorywatcher;

/**
 * The Class AbstractFolderWatcher.
 */
public abstract class AbstractWatcher<T extends IWatcherSettings, Q extends IWatcherEvent<T>, P extends IWatcherListener<T, Q>> implements IWatcher<T> {

	/** The listeners. */
	private P listener;

	/**
	 * Instantiates a new abstract folder watcher.
	 */
	public AbstractWatcher() {
		super();
	}

	/**
	 * Adds the file detection listener.
	 *
	 * @param listener
	 *            the listener
	 */
	public void setFileDetectionListener(P listener) {
		this.listener = listener;
	}

	/**
	 * Notify new file.
	 *
	 * @param event
	 *            the event
	 */
	protected void notifyNewFile(Q event) {
		if (this.listener != null) {
			this.listener.onFolderWatcherEvent(event);
		}
	}

	protected void notifyError(T info, Exception error) {
		if (this.listener != null) {
			this.listener.onErrorEvent(info, error);
		}
	}

}
