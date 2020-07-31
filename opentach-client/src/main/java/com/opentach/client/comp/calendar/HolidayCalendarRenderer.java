package com.opentach.client.comp.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.tools.DateTools;

public class HolidayCalendarRenderer extends CalendarDatesDescriptionRenderer implements IPanelRenderer {

	public HolidayCalendarRenderer(Hashtable<String, String> params) {
		super(params);
		this.leavingEmptyRows = true;
	}

	@Override
	protected String formatAnnotation(Date begMeet, String label, String... params) {
		return this.formatDate(begMeet) + ": " + label;
	}

	@Override
	protected Hashtable<Integer, ArrayList<String>> parseData(Calendar c, int monthNumber, EntityResult data) {
		Hashtable<Integer, ArrayList<String>> annotations = new Hashtable<Integer, ArrayList<String>>();

		Vector<String> names = (Vector<String>) data.get("NAME");
		Vector<Date> dates = (Vector<Date>) data.get("HOLIDAY");

		int totalRows = names == null ? 0 : names.size();

		for (int i = 0; i < totalRows; i++) {
			String name = names.get(i);
			Date date = dates.get(i);

			if ((date != null) && (DateTools.dateToCal(date).get(Calendar.MONTH) == monthNumber)) {
				String s = this.formatAnnotation(date, name, null);
				this.addAnnotation(annotations, s, date);
			}

		}

		return annotations;
	}

	@Override
	protected String getDataSourceNameKey() {
		return "holidaydatasource";
	}

}
