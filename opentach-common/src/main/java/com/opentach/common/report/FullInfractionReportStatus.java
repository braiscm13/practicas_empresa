package com.opentach.common.report;

import java.io.Serializable;

/**
 * The Class FullInfractionReportStatus.
 */
public class FullInfractionReportStatus implements Serializable {

	/** The running. */
	private final boolean	running;

	/** The processed. */
	private final int		processed;

	/** The total count. */
	private final int		totalCount;

	/** The duration. */
	private final long		duration;

	/**
	 * Instantiates a new full infraction report status.
	 *
	 * @param running
	 *            the running
	 * @param totalCount
	 *            the total count
	 * @param processed
	 *            the processed
	 * @param duration
	 *            the duration
	 */
	public FullInfractionReportStatus(boolean running, int totalCount, int processed, long duration) {
		super();
		this.running = running;
		this.totalCount = totalCount;
		this.processed = processed;
		this.duration = duration;
	}

	/**
	 * Gets the duration.
	 *
	 * @return the duration
	 */
	public long getDuration() {
		return this.duration;
	}

	/**
	 * Gets the processed.
	 *
	 * @return the processed
	 */
	public int getProcessed() {
		return this.processed;
	}

	/**
	 * Gets the total count.
	 *
	 * @return the total count
	 */
	public int getTotalCount() {
		return this.totalCount;
	}

	/**
	 * Checks if is running.
	 *
	 * @return true, if is running
	 */
	public boolean isRunning() {
		return this.running;
	}
}
