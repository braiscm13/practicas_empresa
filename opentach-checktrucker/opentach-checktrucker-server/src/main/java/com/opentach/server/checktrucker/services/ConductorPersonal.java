package com.opentach.server.checktrucker.services;

public class ConductorPersonal {
	private Object	id;
	private Object	type;

	public enum Type {
		CONDUCTOR, PERSONAL
	}

	public ConductorPersonal(Type type, Object id) {
		this.id = id;
		this.type = type;
	}

	public Object getId() {
		return this.id;
	}

	public void setId(Object id) {
		this.id = id;
	}

	public Object getType() {
		return this.type;
	}

	public void setType(Object type) {
		this.type = type;
	}
}
