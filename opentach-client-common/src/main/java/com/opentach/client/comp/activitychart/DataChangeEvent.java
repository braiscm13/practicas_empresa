package com.opentach.client.comp.activitychart;

import java.util.EventObject;

public class DataChangeEvent extends EventObject {

	private ChartData	data;

	public DataChangeEvent(Object source) {
		super(source);
	}

	public void setDataChanged(ChartData data) {
		this.data = data;
	}

	public ChartData getDataChanged() {
		return this.data;
	}

}
