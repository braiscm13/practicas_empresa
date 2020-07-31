package com.opentach.server.remotevehicle.provider.jaltest.webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "code", "message" })
public class JaltestTachoFileEventResponse {
	private int		code;
	private String	message;

	public JaltestTachoFileEventResponse() {
		super();
	}


	public JaltestTachoFileEventResponse(int code, String message) {
		super();
		this.code = code;
		this.message = message;
	}


	public int getCode() {
		return this.code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}


}
