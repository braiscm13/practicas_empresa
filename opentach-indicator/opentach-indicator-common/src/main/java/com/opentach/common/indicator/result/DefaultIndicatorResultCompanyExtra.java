package com.opentach.common.indicator.result;

import java.util.Date;

public class DefaultIndicatorResultCompanyExtra extends DefaultIndicatorResult implements IIndicatorResultCompany, IIndicatorResultExtra {

	protected Object company;
	protected Object	extra;

	public DefaultIndicatorResultCompanyExtra(Object company, Object extra, Object result, Date date) {
		super(result, date);
		this.company = company;
		this.extra = extra;
	}

	@Override
	public String toString() {
		return super.toString() + "  COMPANY:" + this.getCompany() + "  EXTRA:" + this.getExtra();
	}

	@Override
	public Object getCompany() {
		return this.company;
	}

	@Override
	public Object getExtra() {
		return this.extra;
	}

}
