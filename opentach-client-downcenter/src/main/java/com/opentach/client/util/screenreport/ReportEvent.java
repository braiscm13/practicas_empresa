package com.opentach.client.util.screenreport;

import java.util.EventObject;

public class ReportEvent extends EventObject {

	private final Object	reportInfo;

	public ReportEvent(Object source, Object text) {
		super(source);
		this.reportInfo = text;
	}

	public Object getReportInfo() {
		return this.reportInfo;
	}

	@Override
	public String toString() {
		return String.format("%s [reportInfo=%s]", this.getClass().getName(), String.valueOf(this.reportInfo));
	}
}
