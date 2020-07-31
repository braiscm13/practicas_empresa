package com.opentach.client.comp.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.jee.common.tools.DateTools;

public class SprintCalendarRenderer extends CalendarDatesDescriptionRenderer {

	public static final String	BEGIN	= ApplicationManager.getTranslation("CalendarInitLabel");
	public static final String	END		= ApplicationManager.getTranslation("CalendarEndLabel");

	public SprintCalendarRenderer(Hashtable<String, String> params) {
		super(params);
		this.leavingEmptyRows = false;
	}

	@Override
	protected Hashtable<Integer, ArrayList<String>> parseData(Calendar c, int monthNumber, EntityResult data) {
		Hashtable<Integer, ArrayList<String>> annotations = new Hashtable<Integer, ArrayList<String>>();
		if (data != null) {
			Vector<String> names = (Vector<String>) data.get("NOMBRE");
			Vector<String> apellidos = (Vector<String>) data.get("APELLIDOS");
			Vector<Date> begs = (Vector<Date>) data.get("FECINI");
			Vector<Date> ends = (Vector<Date>) data.get("FECFIN");

			int totalRows = names == null ? 0 : names.size();

			for (int i = 0; i < totalRows; i++) {
				String name = null;
				name = names.get(i) + " " + apellidos.get(i);

				// BEGINDATE
				Date beg = begs.get(i);
				Date end = ends.get(i);
				if ((beg != null) && (DateTools.dateToCal(beg).get(Calendar.MONTH) == monthNumber) && (end != null)
						&& (DateTools.dateToCal(end).get(Calendar.MONTH) == monthNumber)) {
					String s = this.formatAnnotation(beg, end, name);
					this.addAnnotation(annotations, s, beg);
				}
			}
		}
		return annotations;
	}

	@Override
	protected String formatAnnotation(Date date, String label, String... params) {
		return this.formatDate(date) + ": " + label + " " + params[0];
	}

	protected String formatAnnotation(Date dbegin, Date dfin, String label) {
		return this.formatDate(dbegin) + " - " + this.formatDate(dfin) + " : " + label;
	}

	@Override
	protected String getDataSourceNameKey() {
		return "sprintdatasource";
	}

}
