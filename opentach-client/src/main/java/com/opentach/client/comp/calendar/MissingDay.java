package com.opentach.client.comp.calendar;

import com.ontimize.gui.ApplicationManager;

public class MissingDay extends Day {

	private static final String	LABEL	= "MISSING_DAY_LABEL";

	public MissingDay() {}

	@Override
	public String toString() {
		return ApplicationManager.getTranslation(MissingDay.LABEL) + ": " + this.name;
	}

}
