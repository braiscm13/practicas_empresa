package com.opentach.client.comp.calendar;

import java.util.Date;

import com.ontimize.gui.ApplicationManager;

public class SprintDay extends Day {

	public enum Type {
		INIT, END, MEETING, PLANIF
	};

	public Type	type;

	public SprintDay() {}

	public SprintDay(String name, Date date, String description, Type type) {
		super(name, date, description);
		this.type = type;
	}

	@Override
	public String toString() {
		return this.description + ": " + ApplicationManager.getTranslation(this.type.toString()) + ": " + this.name;
	}

	public Type getType() {
		return this.type;
	}

	public void setType(Type type) {
		this.type = type;
	}

}
