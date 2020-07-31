package com.opentach.client.comp.calendar;

import java.util.Date;

public interface IDay {

	public abstract String getName();

	public abstract void setName(String s);

	public abstract Date getDate();

	public abstract void setDate(Date date);

	public abstract String getDescription();

	public abstract void setDescription(String description);
}
