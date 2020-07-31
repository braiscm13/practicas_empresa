package com.opentach.common.indicator.result;

import java.io.Serializable;
import java.util.Date;

public class DefaultIndicatorResult implements IIndicatorResult, Serializable {

	protected Object result;

	protected Date		date;

	public DefaultIndicatorResult(Object result, Date date) {
		this.result = result;
		this.date = date;
	}

	@Override
	public Object getValue() {
		return this.result;
	}

	@Override
	public Date getDate() {
		return this.date;
	}

	@Override
	public String toString() {
		return "[" + this.getDate() + "] : " + this.getValue();
	}

}
