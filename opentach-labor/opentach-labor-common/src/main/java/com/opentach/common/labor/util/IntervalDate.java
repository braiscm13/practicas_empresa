package com.opentach.common.labor.util;

import java.io.Serializable;
import java.util.Date;

import com.ontimize.jee.common.tools.DateTools;

// TODO: Auto-generated Javadoc
/**
 * The Class IntervalDate.
 */
public class IntervalDate implements Serializable {

	/** The from. */
	// in seconds
	private final Date	from;

	/** The to. */
	private final Date	to;

	/** The from ms. */
	private final long	fromMs;

	/** The to ms. */
	private final long	toMs;

	/**
	 * Instantiates a new interval date.
	 *
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 */
	public IntervalDate(Date from, Date to) {
		super();
		this.from = from;
		this.to = to;
		this.fromMs = this.from == null ? 0 : this.from.getTime();
		this.toMs = this.to == null ? Long.MAX_VALUE : this.to.getTime();
	}

	/**
	 * Checks if is into.
	 *
	 * @param check
	 *            the check
	 * @return true, if is into
	 */
	public boolean isInto(Date check) {
		return (check.getTime() >= this.fromMs) && (check.getTime() <= this.toMs);
	}

	/**
	 * Checks for into.
	 *
	 * @param check
	 *            the check
	 * @return true, if successful
	 */
	public boolean hasInto(IntervalDate check) {
		long checkFromMs = check.from == null ? 0 : check.from.getTime();
		long checkToMs = check.to == null ? Long.MAX_VALUE : check.to.getTime();
		return (checkFromMs <= this.toMs) && (checkToMs >= this.fromMs);
	}

	/**
	 * Gets the from.
	 *
	 * @return the from
	 */
	public Date getFrom() {
		return this.from;
	}

	/**
	 * Gets the to.
	 *
	 * @return the to
	 */
	public Date getTo() {
		return this.to;
	}

	/**
	 * Compute number of days.
	 *
	 * @return the float
	 */
	public float computeNumberOfDays() {
		return (this.to.getTime() - this.from.getTime()) / DateTools.MILLISECONDS_PER_DAY;
	}
}