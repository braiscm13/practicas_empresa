package com.opentach.server.indicator.indicator;

import java.util.Properties;

import com.opentach.server.IOpentachServerLocator;

/**
 * The Class Activity.
 */
public abstract class AbstractIndicator implements IIndicator, IIndicatorLocator {
	/** The indicator id. */
	private final Number	id;

	/** The indicator name. */
	private final String	name;

	/** The indicator name. */
	private final Properties	properties;

	/** The locator */
	private IOpentachServerLocator	locator;

	/**
	 * Instantiates a new activity.
	 *
	 * @param origin
	 *            the origin
	 * @param regimen
	 *            the regimen
	 * @param beginDate
	 *            the begin date
	 * @param endDate
	 *            the end date
	 * @param type
	 *            the type
	 */
	public AbstractIndicator(Number id, String name, Properties properties) {
		super();
		this.id = id;
		this.name = name;
		this.properties = properties;
	}

	/**
	 * Gets the indicator id.
	 *
	 * @return the indicator id
	 */
	public Number getId() {
		return this.id;
	}

	/**
	 * Gets the indicator name.
	 *
	 * @return the indicator name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the indicator properties.
	 *
	 * @return the indicator properties
	 */
	public Properties getProperties() {
		return this.properties;
	}

	/*
	 * @Override public String toString() { return String.format("[%.1s| %tH:%tM %td/%tm - %tH:%tM %td/%tm]", String.valueOf(this.type),
	 * this.beginDate, this.beginDate, this.beginDate, this.beginDate, this.endDate, this.endDate, this.endDate, this.endDate); }
	 */

	///////// UTILITIES ///////////////////////
	@Override
	public void setLocator(IOpentachServerLocator locator) {
		this.locator = locator;
	}

	@Override
	public IOpentachServerLocator getLocator() {
		return this.locator;
	}
}