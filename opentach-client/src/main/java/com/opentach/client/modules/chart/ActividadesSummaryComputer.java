package com.opentach.client.modules.chart;

import java.util.Date;
import java.util.Hashtable;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.opentach.client.comp.activitychart.ChartDataField.ISummaryComputer;
import com.opentach.client.comp.render.HourCellRender;

public class ActividadesSummaryComputer implements ISummaryComputer {

	private String[]	values;

	public ActividadesSummaryComputer() {

	}

	@Override
	public String computeSummary(EntityResult data, Date from, Date to) {
		if (data == null) {
			return "";
		}
		final int count = data.calculateRecordNumber();
		final long lFromTime = from.getTime();
		final long lToTime = to.getTime();
		int tcond = 0;
		int tdis = 0;
		int ttrab = 0;
		int tpaus = 0;
		int tindef = 0;
		for (int i = 0; i < count; i++) {
			Hashtable htRow = data.getRecordValues(i);
			Date dCom = (Date) htRow.get("FEC_COMIENZO");
			long time = dCom.getTime();
			if ((time >= lFromTime) && (time < lToTime)) {
				Number ncond = (Number) htRow.get("TCONDUCCION");
				Number npaus = (Number) htRow.get("TDESCANSO");
				Number ntrab = (Number) htRow.get("TTRABAJO");
				Number ndis = (Number) htRow.get("TDISPONIBILIDAD");
				Number nindef = (Number) htRow.get("TINDEFINIDA");
				if (ncond != null) {
					tcond += ncond.intValue();
				}
				if (ndis != null) {
					tdis += ndis.intValue();
				}
				if (npaus != null) {
					tpaus += npaus.intValue();
				}
				if (ntrab != null) {
					ttrab += ntrab.intValue();
				}
				if (nindef != null) {
					tindef += nindef.intValue();
				}
			}
		}
		String timeCond = HourCellRender.parsearTiempoDisponible(tcond, HourCellRender.MINUTES);
		String timeDis = HourCellRender.parsearTiempoDisponible(tdis, HourCellRender.MINUTES);
		String timePaus = HourCellRender.parsearTiempoDisponible(tpaus, HourCellRender.MINUTES);
		String timeTrab = HourCellRender.parsearTiempoDisponible(ttrab, HourCellRender.MINUTES);
		String timeIndef = HourCellRender.parsearTiempoDisponible(tindef, HourCellRender.MINUTES);
		this.values = new String[] { timeCond, timePaus, timeTrab, timeDis, timeIndef };
		return this.refreshSummary();
	}

	@Override
	public String refreshSummary() {
		if (this.values != null) {
			return ApplicationManager.getTranslation("ACT_SUMMARY_TEXT", ApplicationManager.getApplicationBundle(), this.values);
		}
		return null;
	}

}
