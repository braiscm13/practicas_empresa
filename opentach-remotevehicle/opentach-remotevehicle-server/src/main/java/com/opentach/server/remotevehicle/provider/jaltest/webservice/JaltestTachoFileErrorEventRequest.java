package com.opentach.server.remotevehicle.provider.jaltest.webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "companyCif", "requestDate", "requestObjectId", "code", "message" })
public class JaltestTachoFileErrorEventRequest {
	private String	companyCif;
	private String	requestDate;
	private String	requestObjectId;
	private int		code;
	private String	message;

	public JaltestTachoFileErrorEventRequest() {
		super();
	}

	public String getCompanyCif() {
		return this.companyCif;
	}

	public void setCompanyCif(String companyCif) {
		this.companyCif = companyCif;
	}

	public String getRequestDate() {
		return this.requestDate;
	}

	public void setRequestDate(String requestDate) {
		this.requestDate = requestDate;
	}

	public String getRequestObjectId() {
		return this.requestObjectId;
	}

	public void setRequestObjectId(String requestObjectId) {
		this.requestObjectId = requestObjectId;
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
