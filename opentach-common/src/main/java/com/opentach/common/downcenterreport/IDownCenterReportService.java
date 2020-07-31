package com.opentach.common.downcenterreport;

import java.rmi.Remote;

/**
 * The Interface IDownCenterReport.
 */
public interface IDownCenterReportService extends Remote {

	/** The Constant ID. */
	final static String	ID	= "DownCenterReportService";
	/**
	 * The Enum HTMLReportType.
	 */
	public enum DownCenterReportType {

		/** The VU type. */
		VUType,
		/** The TC type. */
		TCType
	}

	/**
	 * Query report data.
	 *
	 * @param fileID
	 *            the file id
	 * @param type
	 *            the type
	 * @param limitedInfo
	 *            Determine if returned data must be simple, with fast response, avoiding expesive tasks
	 * @param sessionID
	 *            the session id
	 * @return the report dto
	 * @throws Exception
	 */
	AbstractReportDto queryReportData(DownCenterReportType reportType, String souce, boolean limitedInfo, int sessionID) throws Exception;
}
