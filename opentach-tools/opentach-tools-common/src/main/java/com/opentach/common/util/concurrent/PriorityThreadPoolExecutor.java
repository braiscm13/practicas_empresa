package com.opentach.common.util.concurrent;

import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PriorityThreadPoolExecutor extends ThreadPoolExecutor {

	public PriorityThreadPoolExecutor(String name, int poolSize, long keepAliveTime, TimeUnit unit) {
		super(poolSize, poolSize, keepAliveTime, unit, new PriorityBlockingQueue<Runnable>(11, new PriorityTaskComparator()), new NamedThreadFactory(name));
		this.allowCoreThreadTimeOut(true);
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(final Callable<T> callable) {
		if (callable instanceof Priorizable) {
			return new PriorityTask<T>(((Priorizable) callable).getPriority(), callable);
		}
		return new PriorityTask<T>(0, callable);
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(final Runnable runnable, final T value) {
		if (runnable instanceof Priorizable) {
			return new PriorityTask<T>(((Priorizable) runnable).getPriority(), runnable, value);
		}
		return new PriorityTask<T>(0, runnable, value);
	}

	public interface Priorizable {
		int getPriority();
	}

	public static final class PriorityTask<T> extends FutureTask<T> implements Comparable<PriorityTask<T>> {
		private final int		priority;
		private final Object	callable;

		public PriorityTask(final int priority, final Callable<T> tCallable) {
			super(tCallable);
			this.callable = tCallable;
			this.priority = priority;
		}

		public PriorityTask(final int priority, final Runnable runnable, final T result) {
			super(runnable, result);
			this.callable = runnable;
			this.priority = priority;
		}

		/**
		 * Gets the callable.
		 *
		 * @return the callable
		 */
		public Object getWrappedObject() {
			return this.callable;
		}

		@Override
		public int compareTo(final PriorityTask<T> o) {
			return o.priority - this.priority;
		}
	}

	private static class PriorityTaskComparator implements Comparator<Runnable> {
		@Override
		public int compare(final Runnable left, final Runnable right) {
			return ((PriorityTask) left).compareTo((PriorityTask) right);
		}
	}
}