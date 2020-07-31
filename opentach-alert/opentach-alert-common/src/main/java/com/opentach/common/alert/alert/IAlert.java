package com.opentach.common.alert.alert;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.opentach.common.alert.exception.AlertException;
import com.opentach.common.alert.result.IAlertResult;

/**
 * This interface defines an alert implementation.
 */
public interface IAlert {

	Object getAlrId();

	/** DB settings (alets table columns) */
	Map<?, ?> getSettings();

	/** Alert configuration properties (from AlertNaming.ALR_PROPERTIES) */
	Properties getProperties();

	/** Execution time parameters: on demand custom parameters */
	Map<?, ?> getExtraData();

	/**
	 * Allow to decide task if execution must me granted. Return true if it has no restrictions or implements date validation (See CronTools support)
	 */
	boolean isValidToExecute(Date dispatchDate);

	// This method executes alert and return result.
	List<IAlertResult> execute() throws AlertException;

}