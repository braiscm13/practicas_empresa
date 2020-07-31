package com.opentach.client.comp.calendar;

import javax.swing.JComponent;

import net.sf.nachocalendar.components.MonthPanel;

public interface IPanelRenderer {
	public JComponent getRendererComponent(MonthPanel month);
}
