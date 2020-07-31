package com.opentach.client.comp.calendar;

import java.awt.Color;
import java.awt.Component;
import java.util.Date;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.border.LineBorder;

import net.sf.nachocalendar.components.DayPanel;
import net.sf.nachocalendar.components.DayRenderer;

public class SprintDayDecorator implements DayRenderer {
	private static final String	COLOR_TRUE		= "#9ECCFF";
	private static final String	COLOR_FALSE		= "#FFFFFF";
	private static final String	COLOR_BORDER	= "#166CFF";

	Color						colorTrue		= null;
	Color						colorFalse		= null;
	Color						colorBorder		= null;

	DayRenderer					renderer		= null;

	public SprintDayDecorator(DayRenderer renderer) {
		this.renderer = renderer;
		this.colorTrue = Color.decode(SprintDayDecorator.COLOR_TRUE);
		this.colorFalse = Color.decode(SprintDayDecorator.COLOR_FALSE);
		this.colorBorder = Color.decode(SprintDayDecorator.COLOR_BORDER);
	}

	@Override
	public Component getDayRenderer(DayPanel daypanel, Date day, Object data, boolean selected, boolean working, boolean enabled) {
		Component retorno = this.renderer.getDayRenderer(daypanel, day, data, selected, working, enabled);
		if (!enabled) {
			return retorno;
		}
		if (data != null) {
			SprintDay h = (SprintDay) ((Hashtable<String, Object>) data).get("SPRINTDAY");
			if (h != null) {
				if (!selected) {
					retorno.setBackground(this.colorTrue);
				}
				if (h.getType() == SprintDay.Type.END) {
					((JLabel) retorno).setBorder(new LineBorder(this.colorBorder, 1, true));
				}

				if (h.getName().contains("  ")) {
					h.setName(h.getName().replace("  ", " \n"));
				}
				daypanel.setToolTipText(h.getName());
			} else {
				// retorno.setBackground(this.colorFalse);
			}
		}
		return retorno;
	}

}
