package com.opentach.common.company.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.opentach.common.exception.OpentachRuntimeException;

/**
 * Interface for staff with no manager assigned to them.
 */

public enum EmployeeType {
	/**
	 * These numbers identify the meaning for the column TIPO into the table EMPLOYEE_TYPES. Must match the values in that table.
	 */
	@JsonProperty("1")
	DRIVER_WITH_TACHOGRAPH(1), //
	@JsonProperty("2")
	DRIVER_WITHOUT_TACHOGRAPH(2), //
	@JsonProperty("3")
	OTHER_MOBILE_STAFF(3), //
	@JsonProperty("4")
	STAFF_IN_HEADQUARTERS(4), //
	@JsonProperty("5")
	TELEWORKING(5);

	private int value;

	EmployeeType(int value) {
		this.value = value;
	}

	public int toId() {
		return this.value;
	}

	public static EmployeeType fromId(Number val) {
		if (val == null) {
			throw new OpentachRuntimeException("Invalid employee type");
		}
		for (final EmployeeType attr : EmployeeType.values()) {
			if (attr.toId() == val.intValue()) {
				return attr;
			}
		}
		return null;
	}
}

