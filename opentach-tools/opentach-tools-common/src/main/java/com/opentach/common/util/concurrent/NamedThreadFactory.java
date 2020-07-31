package com.opentach.common.util.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The default thread factory
 */
public class NamedThreadFactory implements ThreadFactory {
	private static final AtomicInteger	poolNumber		= new AtomicInteger(1);
	private final ThreadGroup			group;
	private final AtomicInteger			threadNumber	= new AtomicInteger(1);
	private final String				namePrefix;

	public NamedThreadFactory(String prefix) {
		SecurityManager s = System.getSecurityManager();
		this.group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
		this.namePrefix = prefix + "-" + NamedThreadFactory.poolNumber.getAndIncrement() + "-thread-";
	}

	@Override
	public Thread newThread(Runnable runnable) {
		Thread thread = new Thread(this.group, runnable, this.namePrefix + this.threadNumber.getAndIncrement(), 0);
		if (thread.isDaemon()) {
			thread.setDaemon(false);
		}
		if (thread.getPriority() != Thread.NORM_PRIORITY) {
			thread.setPriority(Thread.NORM_PRIORITY);
		}
		return thread;
	}
}