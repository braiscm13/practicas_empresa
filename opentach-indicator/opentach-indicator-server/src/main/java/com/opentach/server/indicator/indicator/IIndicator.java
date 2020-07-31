package com.opentach.server.indicator.indicator;

import java.util.List;

import com.opentach.common.indicator.exception.IndicatorException;
import com.opentach.common.indicator.result.IIndicatorResult;

/**
 * This interface defines an indicator implementation.
 */
public interface IIndicator {
	// This method executes indicator and return result.
	List<IIndicatorResult> execute(Number executionNumber) throws IndicatorException;
}
