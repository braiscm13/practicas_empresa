package com.opentach.client.comp.activitychart;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class ChartBeanInfo extends SimpleBeanInfo {
	public ChartBeanInfo() {}

	@Override
	public PropertyDescriptor[] getPropertyDescriptors() {
		try {
			PropertyDescriptor orig = new PropertyDescriptor("origin", Chart.class);
			PropertyDescriptor bounds = new PropertyDescriptor("bounds", Chart.class);
			PropertyDescriptor[] p = { orig, bounds };
			return p;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
