package com.opentach.client.comp.calendar;

import java.awt.Color;
import java.awt.Component;
import java.util.Date;
import java.util.Hashtable;

import net.sf.nachocalendar.components.DayPanel;
import net.sf.nachocalendar.components.DayRenderer;
import net.sf.nachocalendar.holidays.HoliDay;

public class HolidayDecorator implements DayRenderer {
	private static final String	COLOR_TRUE	= "#FF0000";
	private static final String	COLOR_FALSE	= "#000000";
	Color						colorTrue	= null;
	Color						colorFalse	= null;

	DayRenderer					renderer	= null;

	public HolidayDecorator(DayRenderer renderer) {
		this.colorTrue = Color.decode(HolidayDecorator.COLOR_TRUE);
		this.colorFalse = Color.decode(HolidayDecorator.COLOR_FALSE);
		this.renderer = renderer;
	}

	@Override
	public Component getDayRenderer(DayPanel daypanel, Date day, Object data, boolean selected, boolean working, boolean enabled) {

		Component retorno = this.renderer.getDayRenderer(daypanel, day, data, selected, working, enabled);
		if (!enabled) {
			return retorno;
		}
		if (data != null) {
			HoliDay h = (HoliDay) ((Hashtable<String, Object>) data).remove("HOLIDAY");
			if (h != null) {
				retorno.setForeground(this.colorTrue);
				daypanel.setToolTipText(h.getName());
			} else {
				// retorno.setForeground(this.colorFalse);
			}
		}
		return retorno;
	}

}
