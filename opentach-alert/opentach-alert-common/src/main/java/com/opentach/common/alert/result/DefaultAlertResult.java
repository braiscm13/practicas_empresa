package com.opentach.common.alert.result;

import java.io.Serializable;
import java.util.Map;

public class DefaultAlertResult implements IAlertResult, Serializable {

	private static final long	serialVersionUID	= 1L;

	protected Map<?, ?>			data;

	// protected String email;

	public DefaultAlertResult(Map<?, ?> data) {
		this.data = data;
	}

	@Override
	public Map<?, ?> getData() {
		return this.data;
	}

	@Override
	public String toString() {
		return "[" + this.getClass().getName() + "]  {DATA=" + this.getData() + "}";
	}


}
