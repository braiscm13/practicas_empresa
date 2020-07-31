package com.opentach.common.indicator.result;

import java.util.Date;

public class DefaultIndicatorResultExtra extends DefaultIndicatorResult implements IIndicatorResultExtra {

	protected Object extra;

	public DefaultIndicatorResultExtra(Object extra, Object result, Date date) {
		super(result, date);
		this.extra = extra;
	}

	@Override
	public String toString() {
		return super.toString() + "  EXTRA:" + this.getExtra();
	}

	@Override
	public Object getExtra() {
		return this.extra;
	}
}
