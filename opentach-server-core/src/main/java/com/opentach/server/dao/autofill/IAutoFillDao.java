package com.opentach.server.dao.autofill;

import java.util.Map;

/**
 * Daos which wants to be "completed" when some data is not present.
 */
public interface IAutoFillDao {

	/**
	 * Returns the list of columns to autofill in insert operation.
	 *
	 * @return
	 */
	Map<String, String> getColumnsAutoFillInsert();

	/**
	 * Returns the list of columns to autofill in update operation
	 *
	 * @return
	 */
	Map<String, String> getColumnsAutoFillUpdate();

}
