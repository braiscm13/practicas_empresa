package com.opentach.common.indicator.result;

/**
 * The object returned by an indicator after its execution, when its assigned to one employee.
 */
public interface IIndicatorResultEmployee extends IIndicatorResultCompany {
	/**
	 * The employee.
	 *
	 * @return
	 */
	Object getEmployee();
}
