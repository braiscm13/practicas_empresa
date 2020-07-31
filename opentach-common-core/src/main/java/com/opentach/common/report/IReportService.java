package com.opentach.common.report;

import java.rmi.Remote;
import java.util.Map;

import com.ontimize.db.EntityResult;

import net.sf.jasperreports.engine.JasperPrint;

/**
 * The Interface IReportService.
 */
public interface IReportService extends Remote {

	/** The Constant ID. */
	final static String	ID	= "ReportService";

	/**
	 * Fill report.
	 *
	 * @param urlJR
	 *            the url jr
	 * @param params
	 *            the params
	 * @param after
	 *            the after
	 * @param before
	 *            the before
	 * @param res
	 *            the res
	 * @param sessionID
	 *            the session id
	 * @return the jasper print
	 * @throws Exception
	 *             the exception
	 */
	JasperPrint fillReport(String urlJR, Map<String, Object> params, String after, String before, EntityResult res, int sessionID) throws Exception;

}
