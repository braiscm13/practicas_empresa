package com.opentach.server.indicator.indicator;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.common.indicator.exception.IndicatorException;
import com.opentach.common.indicator.result.DefaultIndicatorResult;
import com.opentach.common.indicator.result.IIndicatorResult;

/**
 * Example of indicator, for debug purpose.
 */
public class DummyIndicatorSavable extends AbstractIndicator implements IIndicatorSavable {

	/** The CONSTANT logger */
	private static final Logger logger = LoggerFactory.getLogger(DummyIndicatorSavable.class);

	public DummyIndicatorSavable(Number id, String name, Properties properties) {
		super(id, name, properties);
	}

	@Override
	public List<IIndicatorResult> execute(Number executionNumber) throws IndicatorException {
		// return null;
		return Arrays.asList(new IIndicatorResult[] { //
				new DefaultIndicatorResult(null, new Date()), //
				new DefaultIndicatorResult(1, new Date()), //
				new DefaultIndicatorResult("1", new Date()),//
		});
	}

	@Override
	public void save(Object indId, Number executionNumber, List<IIndicatorResult> results) throws IndicatorException {
		DummyIndicatorSavable.logger.debug("I_SAVE_MY_OWN_RESULTS_TO_CONSOLE");
		if ((results == null) | results.isEmpty()) {
			DummyIndicatorSavable.logger.trace("\tEMPTY_RESULT");
		} else {
			DummyIndicatorSavable.logger.trace("\tRESULTS:");
			results.stream().forEach((result) -> DummyIndicatorSavable.logger.trace("\t\t{}", result));
		}

	}

}
