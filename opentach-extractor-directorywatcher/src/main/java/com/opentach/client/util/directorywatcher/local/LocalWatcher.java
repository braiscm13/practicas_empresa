package com.opentach.client.util.directorywatcher.local;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.client.util.directorywatcher.AbstractWatcher;

/**
 * The Class DirectoryWatcherManager.
 */
public class LocalWatcher extends AbstractWatcher<LocalWatcherSettings, LocalWatcherEvent, AbstractLocalWatcherListener> {

	/** The Constant logger. */
	private static final Logger					logger				= LoggerFactory.getLogger(LocalWatcher.class);

	/* tiempo desde la última modificación para el que se considera que un fichero no se va a modificar más */
	/** The Constant FINISHED_CHECK_TIME. */
	private static final long					FINISHED_CHECK_TIME	= 5000;

	protected static final long					MAX_FILE_SIZE		= 1024 * 1024 * 2;

	/** The watcher. */
	private final WatchService					watcher;

	/** The keys. */
	private final Map<WatchKey, LocalWatcherSettings>	keys;

	/** The created files to check. */
	private final Map<Path, Long>				createdFilesToCheck;

	/** The executor service. */
	private final ScheduledThreadPoolExecutor	executorService;

	/**
	 * Cast.
	 *
	 * @param <T>
	 *            the generic type
	 * @param event
	 *            the event
	 * @return the watch event
	 */
	@SuppressWarnings("unchecked")
	static <T> WatchEvent<T> cast(WatchEvent<?> event) {
		return (WatchEvent<T>) event;
	}

	/**
	 * Creates a WatchService and registers the given directory.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public LocalWatcher() throws IOException {
		super();
		this.watcher = FileSystems.getDefault().newWatchService();
		this.keys = new HashMap<>();
		this.createdFilesToCheck = new HashMap<>();
		this.executorService = new ScheduledThreadPoolExecutor(5);
		this.executorService.execute(new TaskProcessEvents());

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
	public void register(LocalWatcherSettings info) throws IOException {
		if ((info.getLocalFolder() == null) || "".equals(info.getLocalFolder())) {
			return;
		}

		final Path dir = Paths.get(info.getLocalFolder());
		if (Files.exists(dir)) {
			// intentamos subir los ficheros existentes
			Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(Path folder, BasicFileAttributes attrs) throws IOException {
					return dir.equals(folder) ? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if (Files.size(file) < LocalWatcher.MAX_FILE_SIZE) {
						LocalWatcher.this.executorService.schedule(new TaskCheckFileFinished(file, -1, info),
								LocalWatcher.FINISHED_CHECK_TIME, TimeUnit.MILLISECONDS);
					}
					return FileVisitResult.CONTINUE;
				}
			});
		}
		WatchKey key = dir.register(this.watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
				StandardWatchEventKinds.ENTRY_MODIFY);
		this.keys.put(key, info);
	}

	@Override
	public void unregister(LocalWatcherSettings info) throws IOException {
		if ((info.getLocalFolder() == null) || "".equals(info.getLocalFolder())) {
			return;
		}
		final Path dir = Paths.get(info.getLocalFolder());
		for (WatchKey key : this.keys.keySet()) {
			if (dir.equals(this.keys.get(key))) {
				key.cancel();
				this.keys.remove(key);
			}
		}
	}

	/**
	 * Process all events for keys queued to the watcher.
	 */
	void processEvents() {
		// wait for key to be signalled
		WatchKey key;
		try {
			key = this.watcher.take();
		} catch (InterruptedException x) {
			return;
		}
		LocalWatcherSettings watchFolderInfo = this.keys.get(key);
		Path dir = watchFolderInfo == null ? null : Paths.get(watchFolderInfo.getLocalFolder());
		if (dir == null) {
			LocalWatcher.logger.error("WatchKey not recognized!!");
			return;
		}
		// Prevent receiving two separate ENTRY_MODIFY events: file modified
		// and timestamp updated. Instead, receive one ENTRY_MODIFY event
		// with two counts.
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			// do nothing
		}

		for (WatchEvent<?> event : key.pollEvents()) {
			WatchEvent.Kind<?> kind = event.kind();

			// TBD - provide example of how OVERFLOW event is handled
			if (kind == StandardWatchEventKinds.OVERFLOW) {
				continue;
			}

			// Context for directory entry event is the file name of entry
			WatchEvent<Path> ev = LocalWatcher.cast(event);
			Path name = ev.context();
			Path child = dir.resolve(name);

			// print out event
			LocalWatcher.logger.info("%s: %s\n", event.kind().name(), child);

			if (!Files.isDirectory(child, NOFOLLOW_LINKS)) {
				long time = System.currentTimeMillis();
				if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
					this.createdFilesToCheck.put(child, time);
					this.executorService.schedule(new TaskCheckFileFinished(child, time, watchFolderInfo), LocalWatcher.FINISHED_CHECK_TIME,
							TimeUnit.MILLISECONDS);
				} else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
					if (this.createdFilesToCheck.containsKey(child)) {
						this.createdFilesToCheck.put(child, time);
						this.executorService.schedule(new TaskCheckFileFinished(child, time, watchFolderInfo), LocalWatcher.FINISHED_CHECK_TIME,
								TimeUnit.MILLISECONDS);
					}
				} else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
					this.createdFilesToCheck.remove(child);
				}
			}
		}

		// reset key and remove from set if directory no longer accessible
		boolean valid = key.reset();
		if (!valid) {
			this.keys.remove(key);
		}
	}

	protected class TaskCheckFileFinished implements Callable<Void> {

		/** The child. */
		private final Path	child;

		/** The time. */
		private final long	time;

		private final LocalWatcherSettings	folderInfo;

		/**
		 * Instantiates a new task check file finished.
		 *
		 * @param child
		 *            the child
		 * @param time
		 *            the time
		 */
		public TaskCheckFileFinished(Path child, long time, LocalWatcherSettings folderInfo) {
			this.child = child;
			this.time = time;
			this.folderInfo = folderInfo;
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.concurrent.Callable#call()
		 */
		@Override
		public Void call() throws Exception {
			Long cacheTime = LocalWatcher.this.createdFilesToCheck.get(this.child);
			if (((cacheTime != null) && (cacheTime == this.time)) || (this.time < 0)) {
				if (Files.size(this.child) > LocalWatcher.MAX_FILE_SIZE) {
					return null;
				}
				// notify file
				LocalWatcher.this.notifyNewFile(new LocalWatcherEvent(this.child, LocalWatcher.this, this.folderInfo));
			}
			return null;
		}
	}

	/**
	 * The Class TaskProcessEvents.
	 */
	protected class TaskProcessEvents implements Runnable {

		/*
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			for (;;) {
				try {
					LocalWatcher.this.processEvents();
				} catch (Exception error) {
					LocalWatcher.logger.error(null, error);
				}
			}
		}
	}

}