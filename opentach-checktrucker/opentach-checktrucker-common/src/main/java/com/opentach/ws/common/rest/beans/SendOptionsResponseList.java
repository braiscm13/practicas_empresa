package com.opentach.ws.common.rest.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SendOptionsResponseList implements Serializable {

	protected List<SendOptionsResponse> sendOptionsResponse;

	public SendOptionsResponseList() {
		this.sendOptionsResponse = new ArrayList<SendOptionsResponse>();
	}

	public List<SendOptionsResponse> getSendOptionsResponse() {
		return this.sendOptionsResponse;
	}

	public void addSendOptionsResponse(SendOptionsResponse sendOptionsResponse) {
		this.sendOptionsResponse.add(sendOptionsResponse);
	}
}