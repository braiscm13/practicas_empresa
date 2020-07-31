package com.opentach.server.alert.utils;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.support.CronSequenceGenerator;

import com.ibm.icu.util.Calendar;
import com.ontimize.jee.common.tools.DateTools;

public class CronTools {

	/** The CONSTANT logger */
	private static final Logger	logger			= LoggerFactory.getLogger(CronTools.class);

	public static final long	DELTA_MINUTE	= 29 * 1000;	// 29 seconds (to use when scheduler runs for each minute, and with value under half
	// we ensure not overlaps)
	public static final long	DELTA_HOUR		= 59 * 1000;	// 2 minutes (to use when scheduler runs for each hour, and with value under half we
	// ensure not overlaps)

	private CronTools() {
		// Nothing
	}

	public static boolean checkCronMinute(String pattern, Date dispatchDate) {
		return CronTools.checkCron(pattern, dispatchDate, CronTools.DELTA_MINUTE);
	}

	public static boolean checkCronHourly(String pattern, Date dispatchDate) {
		return CronTools.checkCron(pattern, dispatchDate, CronTools.DELTA_HOUR);
	}

	/**
	 * Checks if current date (new Date()) matches with "dispatchDate" and pattern next execution, with some "delta" margin
	 *
	 * @param pattern
	 *            A cron pattern.
	 * @param dispatchDate
	 *            Dispacth date or initial date to consider with pattern
	 * @param delta
	 *            Delta time. See {@link DELTA_MINUTE DELTA_MINUTE} or {@link DELTA_HOUR DELTA_HOUR}
	 * @return
	 */
	public static boolean checkCron(String pattern, Date dispatchDate, long delta) {
		CronSequenceGenerator crond = new CronSequenceGenerator(pattern);
		Date theoricalHour = crond.next(DateTools.add(dispatchDate, Calendar.SECOND, -2));// Protect from just some milliseconds (@Scheduled)

		// Fix timezone
		// int timezoneOffset = Integer.parseInt(System.getProperty("OPENTACH_SYSTEM_DEFAULT_TIMEZONE"));
		// theoricalHour = DateTools.add(theoricalHour, Calendar.MILLISECOND, timezoneOffset);

		Date currentDate = new Date();
		long abs = Math.abs(theoricalHour.getTime() - currentDate.getTime());
		boolean result = abs < delta;
		CronTools.logger.trace("checkCron \t\tPATTERN:{}\tDISPATCH_DATE:{}\tCURRENT_DATE:{}\tTHEORICAL_DATE:{}\tVARIANCE:{}\tDELTA:{}\tRESULT:{}", pattern, dispatchDate,
				currentDate, theoricalHour, abs, delta, result);
		return result;
	}

}
