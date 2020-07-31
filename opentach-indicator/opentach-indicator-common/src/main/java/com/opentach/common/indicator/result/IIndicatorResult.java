package com.opentach.common.indicator.result;

import java.util.Date;

/**
 * The object returned by an indicator after its execution.
 */
public interface IIndicatorResult {
	/**
	 * The final result of this execution
	 *
	 * @return
	 */
	Object getValue();

	/**
	 * The execution time.
	 * 
	 * @return
	 */
	Date getDate();

}
