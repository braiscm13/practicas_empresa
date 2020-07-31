package com.opentach.common.alert.result;

/**
 * The object returned by an indicator after its execution, when its assigned to one employee.
 */
public interface IAlertResultEmployee extends IAlertResultCompany {
	/**
	 * The employee.
	 *
	 * @return
	 */
	Object getEmployee();
}
