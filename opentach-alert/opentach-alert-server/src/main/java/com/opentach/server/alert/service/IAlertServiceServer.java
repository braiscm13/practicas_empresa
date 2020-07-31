package com.opentach.server.alert.service;

import java.util.Date;
import java.util.Map;

import com.opentach.common.alert.exception.AlertException;

/**
 * The Interface IAlertServiceServer contains all API of exclusive use in server (scheduler)
 */
public interface IAlertServiceServer {

	/**
	 * Send to execute the alert, and returns if it is valid to execute or its ignored (raise IgnoreAlertException)
	 *
	 * @param alrId
	 * @param dispatchDate
	 * @return
	 * @throws AlertException
	 */
	void executeAlertAsynchOnDate(Object alrId, boolean forceExecution, Date dispatchDate, Map<?, ?> extraData);

	public static class IgnoreAlertException extends AlertException {
		public IgnoreAlertException() {
			super("IGNORE");
		}
	}
}
