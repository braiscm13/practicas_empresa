package com.opentach.ws.common.rest.beans;

public class RestResponseOptions implements IRestResponse {

	/** Code: -1: ERROR / 0: OK */
	protected int						code;

	/** Code description: In case of error, message to return. */
	protected String					codeDesc;

	protected SendOptionsResponseList	sendOptionsResponseList;

	public RestResponseOptions() {
		super();
		this.code = 0;
	}

	public RestResponseOptions(int code, String codeDesc) {
		this();
		this.setCode(code);
		this.setCodeDesc(codeDesc);
	}

	public RestResponseOptions(SendOptionsResponseList sendOptionsResponseList) {
		this();
		this.setSendOptionsResponseList(sendOptionsResponseList);
	}

	@Override
	public int getCode() {
		return this.code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	@Override
	public String getCodeDesc() {
		return this.codeDesc;
	}

	public void setCodeDesc(String codeDesc) {
		this.codeDesc = codeDesc;
	}

	public SendOptionsResponseList getSendOptionsResponseList() {
		return this.sendOptionsResponseList;
	}

	public void setSendOptionsResponseList(SendOptionsResponseList sendOptionsResponseList) {
		this.sendOptionsResponseList = sendOptionsResponseList;
	}

}