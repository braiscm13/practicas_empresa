package com.opentach.ws.common.rest.beans;

import java.util.HashMap;

public class RestLogginResponse {

	private long					sessionId;

	private HashMap<Object, Object>	map;

	public RestLogginResponse() {
		super();
	}

	public RestLogginResponse(long sessionId) {
		super();
		this.setSessionId(sessionId);
	}

	public RestLogginResponse(long sessionId, HashMap<Object, Object> map) {
		super();
		this.setSessionId(sessionId);
		this.setMap(map);
	}

	public long getSessionId() {
		return this.sessionId;
	}

	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}

	public HashMap<Object, Object> getMap() {
		return this.map;
	}

	public void setMap(HashMap<Object, Object> map) {
		this.map = map;
	}

}