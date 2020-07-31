package com.opentach.common.alert.result;

import java.util.Map;

public class DefaultCompanyAlertResult extends DefaultAlertResult implements IAlertResultCompany {

	private static final long	serialVersionUID	= 1L;

	protected Object			comId;

	public DefaultCompanyAlertResult(Map<?, ?> data, Object comId) {
		super(data);
		this.comId = comId;
	}

	@Override
	public Object getCompany() {
		return this.comId;
	}

	@Override
	public String toString() {
		return super.toString() + "{CIF=" + this.getCompany() + "}";
	}

}
