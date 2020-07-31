package com.opentach.common.labor.laboral;

import java.rmi.Remote;
import java.util.Date;
import java.util.List;

import com.ontimize.db.EntityResult;
import com.ontimize.util.remote.BytesBlock;

/**
 * The Interface ILaborService.
 */
public interface ILaborService extends Remote {

	/** The Constant ID. */
	final static String ID = "LaborService";

	/**
	 * Creates the PDF laboral report.
	 *
	 * @param driverIds
	 *            the driver ids
	 * @param companyCif
	 *            the company cif
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @param sessionId
	 *            the session id
	 * @return the byte[]
	 * @throws Exception
	 *             the exception
	 */
	BytesBlock createLaborReport(List<Object> driverIds, String companyCif, Date from, Date to, int sessionId) throws Exception;

	/**
	 * Creates the daily record report.
	 *
	 * @param driverIds
	 *            the driver ids
	 * @param companyCif
	 *            the company cif
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @param sessionId
	 *            the session id
	 * @return the bytes block
	 * @throws Exception
	 *             the exception
	 */
	BytesBlock createDailyRecordReport(List<Object> driverIds, String companyCif, Date from, Date to, int sessionId) throws Exception;

	/**
	 * Creates the driver journal report.
	 *
	 * @param driverIds
	 *            the driver ids
	 * @param companyCif
	 *            the company cif
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @param reportType
	 *            the report type
	 * @param avoidExtraTime
	 *            the avoid extra time
	 * @param sessionId
	 *            the session id
	 * @return the bytes block
	 * @throws Exception
	 *             the exception
	 */
	BytesBlock createDriverJournalReport(List<Object> driverIds, String companyCif, Date from, Date to, String reportType, boolean avoidExtraTime, int sessionId) throws Exception;

	/**
	 * Creates the driver status time report.
	 *
	 * @param driverIds
	 *            the driver ids
	 * @param companyCif
	 *            the company cif
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @param reportType
	 *            the report type
	 * @param activityTypes
	 *            the activity types
	 * @param sessionId
	 *            the session id
	 * @return the bytes block
	 * @throws Exception
	 *             the exception
	 */
	BytesBlock createDriverStatusTimeReport(List<Object> driverIds, String companyCif, Date from, Date to, String reportType, List<String> activityTypes, int sessionId)
			throws Exception;

	/**
	 * Query labor report.
	 *
	 * @param driverIds
	 *            the driver ids
	 * @param companyCif
	 *            the company cif
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @param sessionId
	 *            the session id
	 * @return the entity result
	 * @throws Exception
	 *             the exception
	 */
	EntityResult queryLaborReport(List<Object> driverIds, String companyCif, Date from, Date to, int sessionId) throws Exception;

}
