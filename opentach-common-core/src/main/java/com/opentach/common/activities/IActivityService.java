package com.opentach.common.activities;

import java.rmi.Remote;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.ontimize.db.EntityResult;

/**
 * Service to manipulate all concepts around smartphone (Android, IPhone?, Blackberry??) localization.
 */
public interface IActivityService extends Remote {


	/** The Constant ID. */
	final static String	ID	= "ActivityService";

	/**
	 * Flat activities.
	 *
	 * @param cif
	 *            the cif
	 * @param cgContrato
	 *            the cg contrato
	 * @param cardNumber
	 *            the card number
	 * @param beginDate
	 *            the begin date
	 * @param endDate
	 *            the end date
	 * @param queryPeriods
	 *            the query periods
	 * @param vDrivers
	 *            the v drivers
	 * @param sesionId
	 *            the sesion id
	 * @param av
	 *            the av
	 * @return the entity result
	 * @throws Exception
	 *             the exception
	 */
	EntityResult flatActivities(final String cif, final Object cgContrato, String cardNumber, Date beginDate, Date endDate, boolean queryPeriods, Vector<Object> vDrivers,
			int sesionId, Vector<String> av) throws Exception;

	/**
	 * Gets the conduccion.
	 *
	 * @param av
	 *            the av
	 * @param sessionID
	 *            the session id
	 * @return the conduccion
	 * @throws Exception
	 *             the exception
	 */
	EntityResult getConduccion(Hashtable av, int sessionID) throws Exception;



}