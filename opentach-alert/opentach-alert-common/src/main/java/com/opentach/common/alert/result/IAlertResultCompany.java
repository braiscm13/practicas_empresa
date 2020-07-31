package com.opentach.common.alert.result;

/**
 * The object returned by an indicator after its execution, when its assigned to one company.
 */
public interface IAlertResultCompany extends IAlertResult {
	/**
	 * The company.
	 *
	 * @return
	 */
	Object getCompany();

}
