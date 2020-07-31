package com.opentach.common.indicator.result;

/**
 * The object returned by an indicator after its execution, when its assigned to an extra id (distinct to company or employee).
 */
public interface IIndicatorResultExtra extends IIndicatorResult {
	/**
	 * The extra.
	 *
	 * @return
	 */
	Object getExtra();
}
