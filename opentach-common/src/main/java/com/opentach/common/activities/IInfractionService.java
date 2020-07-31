package com.opentach.common.activities;

import java.rmi.Remote;
import java.util.Date;
import java.util.Vector;

import com.ontimize.db.EntityResult;
import com.opentach.common.report.FullInfractionReportStatus;

public interface IInfractionService extends Remote {

	/**
	 * The Enum EngineAnalyzer.
	 */
	public enum EngineAnalyzer {

		/** The default. */
		DEFAULT,
		/** The islands. */
		ISLANDS

	}

	/** The Constant ID. */
	final static String	ID				= "InfractionService";

	/** The Constant ENGINE_ANALYZER. */
	final static String	ENGINE_ANALYZER	= "ENGINEANALYZER";

	/**
	 * Analyze.
	 *
	 * @param cif
	 *            the cif
	 * @param cgContrato
	 *            the cg contrato
	 * @param beginDate
	 *            the begin date
	 * @param endDate
	 *            the end date
	 * @param vDrivers
	 *            the v drivers
	 * @param engine
	 *            the engine
	 * @param sesionId
	 *            the sesion id
	 * @param av
	 *            the av
	 * @return the entity result
	 * @throws Exception
	 *             the exception
	 */
	EntityResult analyzeInfractions(String cif, Object cgContrato, Date beginDate, Date endDate, Vector<Object> vDrivers, EngineAnalyzer engine, int sesionId, Vector<String> av)
			throws Exception;

	/**
	 * Gets the full infraction report status.
	 *
	 * @param sessionId
	 *            the session id
	 * @return the status
	 * @throws Exception
	 *             the exception
	 */
	FullInfractionReportStatus getFullInfractionReportStatus(int sessionId) throws Exception;

	/**
	 * Do full infraction report.
	 *
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @param sessionId
	 *            the session id
	 * @throws Exception
	 *             the exception
	 */
	void doFullInfractionReport(Date from, Date to, int sessionId) throws Exception;
}
