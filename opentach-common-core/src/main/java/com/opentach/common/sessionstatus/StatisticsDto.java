package com.opentach.common.sessionstatus;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The Class StatisticsDto.
 */
public class StatisticsDto implements Serializable {

	/** The form manager. */
	private final String		formManager;

	/** The form. */
	private final String		form;

	/** The action. */
	private final String		action;

	/** The click count. */
	private final AtomicInteger	clickCount;

	/**
	 * Instantiates a new statistics dto.
	 *
	 * @param formManager
	 *            the form manager
	 * @param form
	 *            the form
	 * @param action
	 *            the action
	 */
	public StatisticsDto(String formManager, String form, String action) {
		super();
		this.formManager = formManager;
		this.form = form;
		this.action = action;
		this.clickCount = new AtomicInteger(1);
	}

	/**
	 * Adds the.
	 */
	public void add() {
		this.clickCount.incrementAndGet();
	}

	/**
	 * Gets the form manager.
	 *
	 * @return the form manager
	 */
	public String getFormManager() {
		return this.formManager;
	}

	/**
	 * Gets the form.
	 *
	 * @return the form
	 */
	public String getForm() {
		return this.form;
	}

	/**
	 * Gets the action.
	 *
	 * @return the action
	 */
	public String getAction() {
		return this.action;
	}

	/**
	 * Gets the click count.
	 *
	 * @return the click count
	 */
	public AtomicInteger getClickCount() {
		return this.clickCount;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.action == null) ? 0 : this.action.hashCode());
		result = (prime * result) + ((this.form == null) ? 0 : this.form.hashCode());
		result = (prime * result) + ((this.formManager == null) ? 0 : this.formManager.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		StatisticsDto other = (StatisticsDto) obj;
		if (this.action == null) {
			if (other.action != null) {
				return false;
			}
		} else if (!this.action.equals(other.action)) {
			return false;
		}
		if (this.form == null) {
			if (other.form != null) {
				return false;
			}
		} else if (!this.form.equals(other.form)) {
			return false;
		}
		if (this.formManager == null) {
			if (other.formManager != null) {
				return false;
			}
		} else if (!this.formManager.equals(other.formManager)) {
			return false;
		}
		return true;
	}

}