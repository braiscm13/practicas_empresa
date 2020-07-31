package com.opentach.common.indicator.result;

import java.util.Date;

public class DefaultIndicatorResultEmployeeExtra extends DefaultIndicatorResultCompanyExtra implements IIndicatorResultEmployee {

	protected Object employee;

	public DefaultIndicatorResultEmployeeExtra(Object company, Object employee, Object extra, Object result, Date date) {
		super(company, extra, result, date);
		this.employee = employee;
	}

	@Override
	public String toString() {
		return super.toString() + "  EMPLOYEE:" + this.getEmployee();
	}

	@Override
	public Object getEmployee() {
		return this.employee;
	}
}
