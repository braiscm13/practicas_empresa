package com.opentach.common.util.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * The Class ThreadPoolExecutors.
 */
public final class PoolExecutors {

	/**
	 * Instantiates a new thread pool executors.
	 */
	private PoolExecutors() {}

	/**
	 * New pool executor.
	 *
	 * @param name
	 *            the name
	 * @param poolSize
	 *            the pool size
	 * @param keepAliveTime
	 *            the keep alive time
	 * @param unit
	 *            the unit
	 * @return the thread pool executor
	 */
	public static ThreadPoolExecutor newPoolExecutor(String name, int poolSize, long keepAliveTime, TimeUnit unit) {
		final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(poolSize, poolSize, keepAliveTime, unit, new LinkedBlockingQueue<Runnable>(),
				new NamedThreadFactory(name));
		threadPoolExecutor.allowCoreThreadTimeOut(true);
		return threadPoolExecutor;
	}

	/**
	 * New priority pool executor.
	 *
	 * @param name
	 *            the name
	 * @param poolSize
	 *            the pool size
	 * @param keepAliveTime
	 *            the keep alive time
	 * @param unit
	 *            the unit
	 * @return the priority thread pool executor
	 */
	public static PriorityThreadPoolExecutor newPriorityPoolExecutor(String name, int poolSize, long keepAliveTime, TimeUnit unit) {
		return new PriorityThreadPoolExecutor(name, poolSize, keepAliveTime, unit);
	}

	/**
	 * New fixed thread pool.
	 *
	 * @param name
	 *            the name
	 * @param poolSize
	 *            the pool size
	 * @return the executor service
	 */
	public static ExecutorService newFixedThreadPool(String name, int poolSize) {
		return new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory(name));
	}

	public static ScheduledThreadPoolExecutor newScheduledFixedThreadPool(String name, int poolSize) {
		return new ScheduledThreadPoolExecutor(poolSize, new NamedThreadFactory(name));
	}

	/**
	 * Creates a thread pool that creates new threads as needed, but will reuse previously constructed threads when they are available. These pools will typically improve the
	 * performance of programs that execute many short-lived asynchronous tasks. Calls to {@code execute} will reuse previously constructed threads if available. If no existing
	 * thread is available, a new thread will be created and added to the pool. Threads that have not been used for sixty seconds are terminated and removed from the cache. Thus, a
	 * pool that remains idle for long enough will not consume any resources. Note that pools with similar properties but different details (for example, timeout parameters) may be
	 * created using {@link ThreadPoolExecutor} constructors.
	 *
	 * @param name
	 *
	 * @return the newly created thread pool
	 */
	public static ExecutorService newCachedThreadPool(String name) {
		return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new NamedThreadFactory(name));
	}

}
