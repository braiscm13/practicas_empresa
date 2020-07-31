package com.opentach.server.task.interfaces;

import java.util.Date;

/**
 * Interface to be implemented by all tasks (with support to audtodiscover) that can be executed
 */
public interface ITask {
	/**
	 * Allow to decide task if execution must me granted. Return true if it has no restrictions or implements date validation (See CronTools support)
	 */
	boolean isValidToExecute(Date dispatchDate);

	void execute();

}
