package com.opentach.client.comp.calendar;

import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.jee.common.tools.DateTools;
import com.utilmize.client.gui.field.table.UTable;

import net.sf.nachocalendar.components.DayPanel;
import net.sf.nachocalendar.components.DayRenderer;

public class DayRendererDecorator implements DayRenderer {
	String						sprintDataSourceName	= null;
	UTable						sprintDataSource		= null;
	Form						form					= null;

	public final String			INIT					= ApplicationManager.getTranslation("CalendarInitLabel");
	public final String			END						= ApplicationManager.getTranslation("CalendarEndLabel");

	DayRenderer					renderer;
	protected SimpleDateFormat	dateFormat				= null;

	public DayRendererDecorator(Hashtable<String, String> params) {
		this.init(params);
	}

	private void init(Hashtable<String, String> params) {
		this.sprintDataSourceName = params.get("sprintdatasource");
		this.form = null;

		DayRenderer baseRenderer = new BaseDayRenderer(params);
		DayRenderer holiDayRenderer = new HolidayDecorator(baseRenderer);
		DayRenderer sprintDayRenderer = new SprintDayDecorator(holiDayRenderer);
		DayRenderer missingDayRenderer = new MissingDayDecorator(sprintDayRenderer);
		this.renderer = missingDayRenderer;
		Locale l = this.getLanguage(params.get("language"), params.get("country"));
		this.dateFormat = new SimpleDateFormat("HH:mm", l);

	}

	@Override
	public Component getDayRenderer(DayPanel daypanel, Date day, Object data, boolean selected, boolean working, boolean enabled) {

		Hashtable<String, Object> d = new Hashtable<String, Object>();

		SprintDay s = this.getSprintDay(day);

		if (s != null) {
			d.put("SPRINTDAY", s);
		}

		Component dayRenderer = this.renderer.getDayRenderer(daypanel, day, d, selected, working, enabled);

		return dayRenderer;
	}

	private SprintDay getSprintDay(Date date) {

		Calendar dateCal = DateTools.dateToCal(date);
		int doy = dateCal.get(Calendar.DAY_OF_YEAR);

		EntityResult data = this.getDataFromTable(this.sprintDataSource, this.sprintDataSourceName);

		Vector<String> names = (Vector<String>) data.get("NOMBRE");
		Vector<String> apellidos = (Vector<String>) data.get("APELLIDOS");
		Vector<Date> begs = (Vector<Date>) data.get("FECINI");
		Vector<Date> ends = (Vector<Date>) data.get("FECFIN");
		int totalRows = names == null ? 0 : names.size();

		SprintDay s = new SprintDay();

		Date lastBeginDate = null;
		Date lastEndDate = null;
		Date dbegin = null;
		Date dend = null;
		for (int i = 0; i < totalRows; i++) {

			dbegin = begs.get(i);
			dend = ends.get(i);

			if ((dbegin != null) && (DateTools.dateToCal(dbegin).get(Calendar.DAY_OF_YEAR) == doy) && (dend != null) && (DateTools.dateToCal(dend)
					.get(Calendar.DAY_OF_YEAR) == doy)) {
				s.setDate(dbegin);
				s.setType(SprintDay.Type.INIT);
				Date dlastEndDate = null;
				Date dlastBeginDate = null;
				Date ddend = null;
				Date ddbegin = null;
				if (dbegin != null) {
					ddbegin = this.truncateDay(dbegin);
				}
				if (lastBeginDate != null) {
					dlastBeginDate = this.truncateDay(lastBeginDate);
				}
				if (dend != null) {
					ddend = this.truncateDay(dend);
				}
				if (lastEndDate != null) {
					dlastEndDate = this.truncateDay(lastEndDate);
				}

				if ((dlastBeginDate != null) && (this.truncateDay(dlastBeginDate).getTime() == this.truncateDay(ddbegin).getTime())
						&& (dlastEndDate != null) && ((this.truncateDay(dlastEndDate)).getTime() == (this.truncateDay(ddend)).getTime())) {
					s.setName(s.getName() + "  " + this.formatAnnotation(dbegin, dend, names.get(i) + " " + apellidos.get(i)));
				} else {
					s.setName(this.formatAnnotation(dbegin, dend, names.get(i) + " " + apellidos.get(i)));
				}
			} else if ((dbegin != null) && (DateTools.dateToCal(dbegin).get(Calendar.DAY_OF_YEAR) == doy)) {
				s.setDate(dbegin);
				s.setType(SprintDay.Type.INIT);
				s.setName((s.getName() == null ? "" : s.getName() + "--") + this.INIT + " " + names.get(i));
			}

			else if ((dend != null) && (DateTools.dateToCal(dend).get(Calendar.DAY_OF_YEAR) == doy)) {
				s.setDate(dend);
				s.setType(SprintDay.Type.END);
				s.setName((s.getName() == null ? "" : s.getName() + "--") + this.END + " " + names.get(i));
			}

			lastBeginDate = dbegin;
			lastEndDate = dend;

		}

		if (s.getDate() == null) {
			return null;
		} else {
			return s;
		}
	}

	private EntityResult getDataFromTable(UTable table, String tableName) {
		if (table == null) {
			if (this.form == null) {
				return new EntityResult();
			}
			table = (UTable) this.form.getDataFieldReference(tableName);
		}
		Object value = table.getValue();
		if (value instanceof Hashtable<?, ?>) {
			return new EntityResult((Hashtable) value);
		}
		return (EntityResult) value;
	}

	public void setParentForm(Form form) {
		this.form = form;
	}

	protected String formatAnnotation(Date dbegin, Date dfin, String label) {
		return this.formatDate(dbegin) + "-" + this.formatDate(dfin) + " : " + label;
	}

	protected String formatDate(Date endMeet) {
		String s = this.dateFormat.format(endMeet);
		// return s;
		char[] chars = s.toCharArray();
		chars[3] = Character.toUpperCase(chars[3]);
		return new String(chars);
	}

	private Locale getLanguage(String language, String country) {
		if ((language == null) || (country == null)) {
			return Locale.getDefault();
		} else {
			return new Locale(language, country);
		}
	}

	public Date truncateDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 00);
		calendar.set(Calendar.MINUTE, 00);
		calendar.set(Calendar.SECOND, 00);
		calendar.set(Calendar.MILLISECOND, 0);
		return new Date(calendar.getTime().getTime());
	}
}
