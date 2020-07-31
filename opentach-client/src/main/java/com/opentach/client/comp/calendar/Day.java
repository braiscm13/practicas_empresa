package com.opentach.client.comp.calendar;

import java.util.Date;

public class Day implements IDay {

	protected String	name;
	protected Date		date;
	protected String	description;

	public Day() {
		super();
	}

	public Day(String name, Date date, String description) {
		super();
		this.name = name;
		this.date = date;
		this.description = description;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Date getDate() {
		if (this.date != null) {
			return (Date) this.date.clone();
		} else {
			return null;
		}
	}

	@Override
	public void setDate(Date date) {

		if (date == null) {
			throw new IllegalArgumentException("Illegal Date");
		} else {
			this.date = (Date) date.clone();
			return;
		}
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return this.name + " " + this.description + " " + this.date;
	}

}