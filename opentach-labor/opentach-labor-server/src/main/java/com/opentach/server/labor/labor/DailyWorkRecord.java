package com.opentach.server.labor.labor;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.imatia.tacho.StretchType;
import com.imatia.tacho.infraction.Stretch;
import com.ontimize.jee.common.tools.DateTools;
import com.ontimize.jee.common.tools.ObjectTools;

public class DailyWorkRecord implements Serializable, Comparable<DailyWorkRecord> {
	public static enum DailyWorkRecordOrigin {
		MANUAL, AUTO
	}

	private final static long				serialVersionUID	= 1;

	private transient List<Stretch>			stretchs;
	private transient List<WorkingPeriod>	workingPeriods;
	private final int						year;
	private final int						month;
	private final int						day;
	private final int						weekOfYear;
	private DailyWorkRecordOrigin			origin;
	private int								workingMinutes;
	private Date							from;
	private Date							to;
	private final String					dayString;

	public DailyWorkRecord() {
		super();
		this.year = 0;
		this.month = 0;
		this.day = 0;
		this.weekOfYear = 0;
		this.dayString = String.format("%04d-%02d-%02d", this.year, this.month, this.day);
		this.origin = DailyWorkRecordOrigin.AUTO;
		this.stretchs = new ArrayList<>();
		this.workingPeriods = new ArrayList<>();
	}

	public DailyWorkRecord(int year, int month, int day, DailyWorkRecordOrigin origin) {
		super();
		this.year = year;
		this.month = month;
		this.day = day;
		this.dayString = String.format("%04d-%02d-%02d", this.year, this.month, this.day);
		this.weekOfYear = DateTools.createCalendar(year, month - 1, day).get(Calendar.WEEK_OF_YEAR);
		this.origin = origin;
		this.workingPeriods = new ArrayList<>();
		this.stretchs = new ArrayList<>();
	}

	public DailyWorkRecordOrigin getOrigin() {
		return this.origin;
	}

	public String getOriginStr() {
		return String.valueOf(this.getOrigin());
	}

	public void addStretch(Stretch stretch) {
		if (this.stretchs == null) {
			this.stretchs = new ArrayList<>();
		}
		this.stretchs.add(stretch);
		this.updateDates();
	}

	public void setStretchs(List<Stretch> stretchs) {
		this.stretchs = stretchs;
		this.updateDates();
	}

	private void updateDates() {
		// actualizamos fecha inicio y fin
		if ((this.stretchs != null) && (this.stretchs.size() > 0)) {
			this.from = null;
			this.to = null;
			this.from = this.stretchs.get(0).getBeginDate();

			// hay que devolver la fecha fin del ultimo stretch distinto de descanso
			for (int i = 0; i < this.stretchs.size(); i++) {
				Stretch stretch = this.stretchs.get(i);
				if (!ObjectTools.isIn(stretch.getType(), StretchType.AVAILABLE, StretchType.REST, StretchType.INDETERMINATE)) {
					this.from = stretch.getBeginDate();
					break;
				}
			}
			if (this.from == null) {
				this.stretchs.get(0).getBeginDate();
			}
			// hay que devolver la fecha fin del ultimo stretch distinto de descanso
			for (int i = this.stretchs.size() - 1; i >= 0; i--) {
				Stretch stretch = this.stretchs.get(i);
				if (!ObjectTools.isIn(stretch.getType(), StretchType.AVAILABLE, StretchType.REST, StretchType.INDETERMINATE)) {
					this.to = stretch.getEndDate();
					break;
				}
			}
			if (this.to == null) {
				this.to = this.stretchs.get(this.stretchs.size() - 1).getEndDate();
			}
		}
	}

	public void setWorkingPeriods(List<WorkingPeriod> workingPeriods) {
		this.workingPeriods = workingPeriods;
	}

	public List<WorkingPeriod> getWorkingPeriods() {
		return this.workingPeriods;
	}

	public void addWorkingPeriod(WorkingPeriod wp) {
		if (this.workingPeriods == null) {
			this.workingPeriods = new ArrayList<>();
		}
		this.workingPeriods.add(wp);
	}

	public List<Stretch> getStretchs() {
		return this.stretchs;
	}

	@Override
	public String toString() {
		if ((this.stretchs == null) || (this.stretchs.size() == 0)) {
			return String.format("%04d-%02d-%02d: from - to -", this.year, this.month, this.day);
		}
		StringBuilder sb = new StringBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		sb.append(String.format("%04d-%02d-%02d: from %s to %s\n", this.year, this.month, this.day, sdf.format(this.stretchs.get(0).getBeginDate()),
				sdf.format(this.stretchs.get(this.stretchs.size() - 1).getEndDate())));
		sb.append("Stretchs:\n");
		for (Stretch stretch : this.stretchs) {
			sb.append(stretch).append("\n");
		}
		sb.append("WorkingPeriods:\n");
		for (WorkingPeriod period : this.workingPeriods) {
			sb.append(period).append("\n");
		}
		return sb.toString();
	}

	public Date getFrom() {
		return this.from;
	}

	public Date getTo() {
		return this.to;
	}

	public int getYear() {
		return this.year;
	}

	public int getMonth() {
		return this.month;
	}

	public int getDay() {
		return this.day;
	}

	public int getWeekOfYear() {
		return this.weekOfYear;
	}

	public String getDayString() {
		return this.dayString;
	}

	public int getWorkingMinutes() {
		return this.workingMinutes;
	}

	public void setWorkingMinutes(int workingMinutes) {
		this.workingMinutes = workingMinutes;
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public void setTo(Date to) {
		this.to = to;
	}

	public void setOrigin(DailyWorkRecordOrigin origin) {
		this.origin = origin;
	}

	@Override
	public int compareTo(DailyWorkRecord o) {
		return this.getDayString().compareTo(o.getDayString());
	}

	public static String toDayString(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		return String.format("%04d-%02d-%02d", year, month, day);
	}

	public static Date fromDayString(String strFrom) {
		String[] splits = strFrom.split("-");
		Calendar cal = Calendar.getInstance();
		cal.set(Integer.valueOf(splits[0]), Integer.valueOf(splits[1]) - 1, Integer.valueOf(splits[2]));
		return cal.getTime();
	}
}
