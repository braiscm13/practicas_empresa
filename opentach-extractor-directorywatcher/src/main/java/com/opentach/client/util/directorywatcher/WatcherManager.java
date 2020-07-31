package com.opentach.client.util.directorywatcher;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.client.util.directorywatcher.ftp.AbstractFtpWatcherListener;
import com.opentach.client.util.directorywatcher.ftp.FtpWatcher;
import com.opentach.client.util.directorywatcher.ftp.FtpWatcherSettings;
import com.opentach.client.util.directorywatcher.local.AbstractLocalWatcherListener;
import com.opentach.client.util.directorywatcher.local.LocalWatcher;
import com.opentach.client.util.directorywatcher.local.LocalWatcherSettings;
import com.utilmize.tools.exception.URuntimeException;

/**
 * The Class DirectoryWatcherManager.
 */
public class WatcherManager {

	/** The Constant logger. */
	private static final Logger		logger			= LoggerFactory.getLogger(WatcherManager.class);

	protected static final long		MAX_FILE_SIZE	= 1024 * 1024 * 2;

	/** The instance. */
	private static WatcherManager	instance;

	/**
	 * Gets the single instance of DirectoryWatcherManager.
	 *
	 * @return single instance of DirectoryWatcherManager
	 */
	public static WatcherManager getInstance() {
		if (WatcherManager.instance == null) {
			try {
				WatcherManager.instance = new WatcherManager();
			} catch (IOException error) {
				throw new URuntimeException(error);
			}
		}
		return WatcherManager.instance;
	}

	private final FtpWatcher	ftpWatcher;
	private final LocalWatcher	localWatcher;

	/**
	 * Creates a WatchService and registers the given directory.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private WatcherManager() throws IOException {
		super();
		this.ftpWatcher = new FtpWatcher();
		this.localWatcher = new LocalWatcher();
	}

	public void setFtpFileDetectionListener(AbstractFtpWatcherListener listener) {
		this.ftpWatcher.setFileDetectionListener(listener);
	}

	public void setLocalFileDetectionListener(AbstractLocalWatcherListener listener) {
		this.localWatcher.setFileDetectionListener(listener);
	}

	/**
	 * Register the given directory with the WatchService.
	 *
	 * @param dir
	 *            the dir
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void register(IWatcherSettings info) throws Exception {
		switch (info.getMode()) {
			case LOCAL:
				this.localWatcher.register((LocalWatcherSettings) info);
				break;
			case FTP:
				this.ftpWatcher.register((FtpWatcherSettings) info);
				break;
			default:
				break;
		}
	}

	/**
	 * Unregister.
	 *
	 * @param info
	 *            the info
	 * @throws Exception
	 *             the exception
	 */
	public void unregister(IWatcherSettings info) throws Exception {
		switch (info.getMode()) {
			case LOCAL:
				this.localWatcher.unregister((LocalWatcherSettings) info);
				break;
			case FTP:
				this.ftpWatcher.unregister((FtpWatcherSettings) info);
				break;
			default:
				break;
		}

	}

}