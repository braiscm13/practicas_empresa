package com.opentach.common.alert.result;

import java.util.Map;

public class DefaultEmployeeAlertResult extends DefaultCompanyAlertResult implements IAlertResultEmployee {

	private static final long	serialVersionUID	= 1L;

	protected Object			drvId;

	public DefaultEmployeeAlertResult(Map<?, ?> data, Object comId, Object drvId) {
		super(data, comId);
		this.drvId = drvId;
	}

	@Override
	public Object getEmployee() {
		return this.drvId;
	}

	@Override
	public String toString() {
		return super.toString() + "{DRIVER=" + this.getEmployee() + "}";
	}

}
