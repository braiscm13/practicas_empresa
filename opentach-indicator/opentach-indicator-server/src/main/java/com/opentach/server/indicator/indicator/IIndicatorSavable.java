package com.opentach.server.indicator.indicator;

import java.util.List;

import com.opentach.common.indicator.exception.IndicatorException;
import com.opentach.common.indicator.result.IIndicatorResult;

/**
 * This interface is implemented by indicators which auto-manage the fact of save result.
 * For example, large results, INSERT INTO SELECT, MERGES, etc.
 */
public interface IIndicatorSavable {
	// This method force the indicator to be executed, and return result.
	void save(Object indId, Number executionNumber, List<IIndicatorResult> results) throws IndicatorException;
}
