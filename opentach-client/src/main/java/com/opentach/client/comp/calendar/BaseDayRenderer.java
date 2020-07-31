package com.opentach.client.comp.calendar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JList;

import com.ontimize.util.ParseUtils;

import net.sf.nachocalendar.components.DayPanel;
import net.sf.nachocalendar.components.DayRenderer;

public class BaseDayRenderer extends JLabel implements DayRenderer {
	private final Calendar	cal;
	private final Color		selectedbg;
	private final Color		unselectedbg;
	private final Color		selectedfg;
	private final Color		unselectedfg;
	private final Color		notworkingbg;
	private final Color		notworkingfg;

	Font					baseFont;

	public BaseDayRenderer(Hashtable<String, String> params) {
		this.cal = Calendar.getInstance();

		JList<Object> jl = new JList<Object>();

		this.notworkingbg = ParseUtils.getColor(params.get("calendarnotworkingbg"), Color.decode("#C4E8FF"));
		this.unselectedbg = ParseUtils.getColor(params.get("calendarunselectedbg"), jl.getBackground());
		this.unselectedfg = ParseUtils.getColor(params.get("calendarsunelectedfg"), jl.getForeground());
		this.selectedbg = ParseUtils.getColor(params.get("calendarselectedbg"), jl.getSelectionBackground());
		this.selectedfg = ParseUtils.getColor(params.get("calendarselectedfg"), jl.getSelectionForeground());
		this.baseFont = ParseUtils.getFont(params.get("calendarfont"), new Font("Tahoma", Font.PLAIN, 12));
		this.notworkingfg = ParseUtils.getColor(params.get("calendarnotworkingfg"), this.unselectedfg);

		this.setVerticalAlignment(0);
		this.setHorizontalAlignment(0);
		this.setOpaque(true);
	}

	@Override
	public Component getDayRenderer(DayPanel daypanel, Date day, Object data, boolean selected, boolean working, boolean enabled) {

		// Base Background
		if (selected) {
			this.setBackground(this.selectedbg);
		} else if (working) {
			this.setBackground(this.unselectedbg);
		} else {
			this.setBackground(this.notworkingbg);
		}

		// Base foreground
		if (working) {
			if (selected) {
				this.setForeground(this.selectedfg);
			} else {
				this.setForeground(this.unselectedfg);
			}
		} else if (selected) {
			this.setForeground(this.selectedfg);
		} else {
			this.setForeground(this.notworkingfg);
		}

		// base font
		this.setFont(this.baseFont);
		// base border
		this.setBorder(null);

		this.cal.setTime(day);
		this.setText(Integer.toString(this.cal.get(Calendar.DAY_OF_MONTH)));

		if (!enabled) {
			this.setForeground(Color.lightGray);
			return this;
		}

		if (data == null) {
			daypanel.setToolTipText(null);
		}

		return this;
	}

}
