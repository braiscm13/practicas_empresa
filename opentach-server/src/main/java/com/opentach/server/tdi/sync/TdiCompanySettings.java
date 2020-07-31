package com.opentach.server.tdi.sync;

public class TdiCompanySettings {
	private String	ip;
	private String	port;
	private String	user;
	private String	pass;
	private String	groupid;
	private String	cif;

	public TdiCompanySettings() {
		super();
	}

	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return this.port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUser() {
		return this.user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return this.pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}

	public String getGroupid() {
		return this.groupid;
	}

	public void setCif(String companyCIF) {
		this.cif = companyCIF;
	}

	public String getCif() {
		return this.cif;
	}

}
