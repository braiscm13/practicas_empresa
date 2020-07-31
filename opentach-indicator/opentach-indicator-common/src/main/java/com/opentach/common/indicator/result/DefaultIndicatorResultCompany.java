package com.opentach.common.indicator.result;

import java.util.Date;

public class DefaultIndicatorResultCompany extends DefaultIndicatorResult implements IIndicatorResultCompany {

	protected Object company;

	public DefaultIndicatorResultCompany(Object company, Object result, Date date) {
		super(result, date);
		this.company = company;
	}

	@Override
	public String toString() {
		return super.toString() + "  COMPANY:" + this.getCompany();
	}

	@Override
	public Object getCompany() {
		return this.company;
	}

}
