package com.opentach.client.util.directorywatcher.ftp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.tools.ObjectTools;
import com.opentach.client.util.directorywatcher.AbstractWatcher;
import com.opentach.client.util.directorywatcher.DirectoryWatcherException;

/**
 * The Class DirectoryWatcherManager.
 */
public class FtpWatcher extends AbstractWatcher<FtpWatcherSettings, FtpWatcherEvent, AbstractFtpWatcherListener> {

	/** The Constant logger. */
	private static final Logger					logger				= LoggerFactory.getLogger(FtpWatcher.class);


	protected static final long					MAX_FILE_SIZE		= 1024 * 1024 * 2;

	/** The created files to check. */
	private final Map<Path, Long>				createdFilesToCheck;

	/** The executor service. */
	private final ScheduledThreadPoolExecutor	executorService;
	private final List<FtpWatcherSettings>		ftpSites;
	private final FtpEngine						ftpEngine;

	/**
	 * Creates a WatchService and registers the given directory.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public FtpWatcher() throws IOException {
		super();
		this.ftpEngine = new FtpEngine(new IFtpEngineListener() {
			@Override
			public void onError(FtpWatcherSettings info, IFtpFile ftpFile, Exception ex) {
				// do nothing
			}

			@Override
			public void onFileToUpload(Path file, IFtpFile ftpFile, FtpWatcherSettings info) {
				FtpWatcher.this.notifyNewFile(new FtpWatcherEvent(file, ftpFile, FtpWatcher.this, info));
			}
		});
		this.ftpSites = Collections.synchronizedList(new ArrayList<FtpWatcherSettings>());
		this.createdFilesToCheck = new HashMap<Path, Long>();
		this.executorService = new ScheduledThreadPoolExecutor(1);
		this.executorService.scheduleWithFixedDelay(new TaskCheckFtp(), 5, 10, TimeUnit.MINUTES);

	}

	/**
	 * Register the given directory with the WatchService.
	 *
	 * @param dir
	 *            the dir
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Override
	public void register(final FtpWatcherSettings info) throws IOException {
		ListIterator<FtpWatcherSettings> listIterator = this.ftpSites.listIterator();
		while (listIterator.hasNext()) {
			FtpWatcherSettings registeredInfo = listIterator.next();
			if (this.isSame(registeredInfo, info)) {
				return;
			}
		}
		this.ftpSites.add(info);
	}

	@Override
	public void unregister(final FtpWatcherSettings info) {
		ListIterator<FtpWatcherSettings> listIterator = this.ftpSites.listIterator();
		while (listIterator.hasNext()) {
			FtpWatcherSettings registeredInfo = listIterator.next();
			if (this.isSame(registeredInfo, info)) {
				listIterator.remove();
				return;
			}
		}
	}

	private boolean isSame(FtpWatcherSettings ainf, FtpWatcherSettings binf) {
		return ObjectTools.safeIsEquals(ainf.getFtpServer(), binf.getFtpServer()) && //
				ObjectTools.safeIsEquals(ainf.getFtpUser(), binf.getFtpUser()) && //
				ObjectTools.safeIsEquals(ainf.getFtpPass(), binf.getFtpPass()) && //
				ObjectTools.safeIsEquals(ainf.getFtpFolder(), binf.getFtpFolder());
	}

	public void checkFtp() {
		for (FtpWatcherSettings info : this.ftpSites) {
			try {
				this.ftpEngine.checkFtp(info);
			} catch (Exception ex) {
				this.notifyError(info, ex);
				FtpWatcher.logger.error(null, ex);
			}
		}
	}

	public void backupFile(FtpWatcherSettings ftpSettings, IFtpFile ftpFile) throws DirectoryWatcherException {
		this.ftpEngine.backupFile(ftpSettings, ftpFile);
	}

	/**
	 * The Class TaskCheckFileFinished.
	 */
	protected class TaskCheckFileFinished implements Callable<Void> {

		/** The child. */
		private final Path	child;

		/** The time. */
		private final long	time;

		/**
		 * Instantiates a new task check file finished.
		 *
		 * @param child
		 *            the child
		 * @param time
		 *            the time
		 */
		public TaskCheckFileFinished(Path child, long time) {
			this.child = child;
			this.time = time;
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.concurrent.Callable#call()
		 */
		@Override
		public Void call() throws Exception {
			Long cacheTime = FtpWatcher.this.createdFilesToCheck.get(this.child);
			if (((cacheTime != null) && (cacheTime == this.time)) || (this.time < 0)) {
				if (Files.size(this.child) > FtpWatcher.MAX_FILE_SIZE) {
					return null;
				}
			}
			return null;
		}
	}

	/**
	 * The Class TaskProcessEvents.
	 */
	protected class TaskCheckFtp implements Runnable {

		/*
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			try {
				FtpWatcher.this.checkFtp();
			} catch (Exception error) {
				FtpWatcher.logger.error(null, error);
			}
		}
	}
}