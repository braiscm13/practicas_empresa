package com.opentach.server.labor.util;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class IntervalHour implements Serializable {
	// in seconds
	private final int	from;
	private final int	to;

	public IntervalHour(int from, int to) {
		super();
		this.from = from;
		this.to = to;
	}

	public boolean isInto(Date toCheck) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(toCheck);
		int checkTime = calendar.get(Calendar.HOUR_OF_DAY) * 60 * 60;
		checkTime += calendar.get(Calendar.MINUTE) * 60;
		checkTime += calendar.get(Calendar.SECOND);

		return (checkTime >= this.from) && (checkTime <= this.to);
	}
}