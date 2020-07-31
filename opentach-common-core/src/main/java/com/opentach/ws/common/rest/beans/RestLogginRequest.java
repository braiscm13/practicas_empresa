package com.opentach.ws.common.rest.beans;

import java.util.HashMap;

public class RestLogginRequest {

	private String					usrLogin;
	private String					usrPsswd;
	private HashMap<Object, Object>	map;

	public RestLogginRequest() {
		super();
	}

	public String getUsrPsswd() {
		return this.usrPsswd;
	}

	public void setUsrPsswd(String usrPsswd) {
		this.usrPsswd = usrPsswd;
	}

	public String getUsrLogin() {
		return this.usrLogin;
	}

	public void setUsrLogin(String usrLogin) {
		this.usrLogin = usrLogin;
	}

	public HashMap<Object, Object> getMap() {
		return this.map;
	}

	public void setMap(HashMap<Object, Object> map) {
		this.map = map;
	}

}