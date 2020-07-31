package com.opentach.server.task.interfaces;

import java.util.Date;

import com.opentach.server.alert.utils.CronTools;

/**
 * Default implementation for task based in CRON pattern to decide valid executionTime
 *
 * @author senen.dieguez
 *
 */
public abstract class AbstractCronBasedTask implements ITask {

	public AbstractCronBasedTask() {
		// Nothing
	}

	@Override
	public boolean isValidToExecute(Date dispatchDate) {
		return CronTools.checkCronMinute(this.getCronExpression(), dispatchDate);
	}

	public abstract String getCronExpression();

	@Override
	public String toString() {
		return "AbstractFixedTimeTask";
	}

}