package com.opentach.server.activities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * The Class ActivityIterator.
 */
public abstract class AbstractActivityIterator<T> implements Iterator<T> {

	/** The rs. */
	private final ResultSet	rs;

	/** The first time. */
	private boolean			firstTime;

	/**
	 * Instantiates a new activity iterator.
	 *
	 * @param rs
	 *            the rs
	 */
	public AbstractActivityIterator(ResultSet rs) {
		this.firstTime = true;
		this.rs = rs;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		try {
			boolean b = false;
			if (this.firstTime) {
				this.firstTime = false;
				if (!this.rs.isBeforeFirst()) {
					b = false;
				} else {
					b = true;
				}
			} else {
				b = !this.rs.isLast();
			}
			if (!b) {
				this.rs.close();
			}
			return b;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public T next() {
		try {
			this.rs.next();
			return this.convert(this.rs);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract T convert(ResultSet rs) throws SQLException;

	/*
	 * (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		// do nothing
	}

}