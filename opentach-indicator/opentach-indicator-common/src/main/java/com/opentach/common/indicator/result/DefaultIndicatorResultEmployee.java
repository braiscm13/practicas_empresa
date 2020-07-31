package com.opentach.common.indicator.result;

import java.util.Date;

public class DefaultIndicatorResultEmployee extends DefaultIndicatorResultCompany implements IIndicatorResultEmployee {

	protected Object employee;

	public DefaultIndicatorResultEmployee(Object company, Object employee, Object result, Date date) {
		super(company, result, date);
		this.employee = employee;
	}

	@Override
	public String toString() {
		return super.toString() + "  EMPLOYEE:" + this.getEmployee();
	}

	public Object getEmployee() {
		return this.employee;
	}
}
