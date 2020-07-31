package com.opentach.common.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public final class DateUtil {

	private DateUtil() {}

	public static Date addDays(Date dIni, int days) {
		if (dIni == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(dIni);
		cal.add(Calendar.DATE, days);
		return cal.getTime();
	}

	public static Date trunc(Date d) {
		if (d == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date truncToEnd(Date d) {
		if (d == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		return cal.getTime();
	}

	// metodos para cambiar una fecha de TimeZone
	public static Date toUTCFromTimeZone(Date date, TimeZone tz) {
		final long time = date.getTime();
		return new Date(time - tz.getOffset(time));
	}

	public static Date fromUTCFromTimeZone(Date date, TimeZone tz) {
		final long time = date.getTime();
		return new Date(time + tz.getOffset(time));
	}

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
	}

	public static String parsearTiempoDisponible(int value, boolean secresolution) {
		String resultado = "";
		boolean neg = (value < 0);
		value = Math.abs(value);
		int h, m, sec;
		if (secresolution) {
			// Value is the number of seconds.
			h = value / (60 * 60);
			value = value % (60 * 60);
			m = value / 60;
			sec = value % 60;
		} else {
			// Value is number of minutes
			h = value / 60;
			m = value % 60;
			sec = 0;
		}

		String tH;
		if (h <= 9) {
			tH = "0" + h;
		} else {
			tH = String.valueOf(h);
		}
		String tM;
		if (m <= 9) {
			tM = "0" + m;
		} else {
			tM = String.valueOf(m);
		}
		String tS = "";
		if (secresolution) {
			if (sec <= 9) {
				tS = "0" + sec;
			} else {
				tS = String.valueOf(sec);
			}
		}

		resultado = " " + tH + ":" + tM + (secresolution ? (":" + tS) : "");
		if (neg) {
			resultado = "-" + resultado;
		}
		return resultado;
	}
}
