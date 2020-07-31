package com.opentach.common.alert.alert;

import java.util.Date;
import java.util.List;

import com.opentach.common.alert.exception.AlertException;
import com.opentach.common.alert.result.IAlertResult;

/**
 * This interface is implemented by alerts which auto-manage the fact of process result(, log, save, email, ...). For example, large results, INSERT
 * INTO SELECT, MERGES, etc.
 */
public interface IAlertResultDispatcher {

	// This method force the indicator to be executed, and return result.
	void processResults(Date executionDate, Number executionNumber, List<IAlertResult> results) throws AlertException;

	void onError(Date executionDate, Number executionNumber, Exception err);
}
