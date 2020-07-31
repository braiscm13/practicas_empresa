package com.opentach.server.tdi.sync;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RestResponse {
	@JsonProperty("OP_NAME")
	private String	opName;
	@JsonProperty("OP_RESPONSE")
	private String	opResponse;

	public RestResponse() {
		super();
	}

	public String getOpName() {
		return this.opName;
	}

	public String getOpResponse() {
		return this.opResponse;
	}

}
