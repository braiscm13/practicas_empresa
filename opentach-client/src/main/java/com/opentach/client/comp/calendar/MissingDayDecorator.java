package com.opentach.client.comp.calendar;

import java.awt.Color;
import java.awt.Component;
import java.util.Date;
import java.util.Hashtable;

import net.sf.nachocalendar.components.DayPanel;
import net.sf.nachocalendar.components.DayRenderer;

public class MissingDayDecorator implements DayRenderer {
	private static final String	COLOR_TRUE	= "#FFE3E3";
	private static final String	COLOR_FALSE	= "#FFFFFF";
	Color						colorTrue	= null;
	Color						colorFalse	= null;
	DayRenderer					renderer	= null;

	public MissingDayDecorator(DayRenderer renderer) {
		this.renderer = renderer;
		this.colorTrue = Color.decode(MissingDayDecorator.COLOR_TRUE);
		this.colorFalse = Color.decode(MissingDayDecorator.COLOR_FALSE);
	}

	@Override
	public Component getDayRenderer(DayPanel daypanel, Date day, Object data, boolean selected, boolean working, boolean enabled) {
		Component retorno = this.renderer.getDayRenderer(daypanel, day, data, selected, working, enabled);
		if (!enabled) {
			return retorno;
		}
		if (data != null) {
			MissingDay h = (MissingDay) ((Hashtable<String, Object>) data).get("MISSINGDAY");
			if (h != null) {
				if (!selected) {
					retorno.setBackground(this.colorTrue);
				}
				daypanel.setToolTipText(h.toString());
			} else {
				// retorno.setBackground(this.colorFalse);
			}
		}
		return retorno;
	}

}
