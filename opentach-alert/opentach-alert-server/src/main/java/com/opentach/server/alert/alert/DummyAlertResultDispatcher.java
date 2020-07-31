package com.opentach.server.alert.alert;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.tools.EntityResultTools;
import com.opentach.common.alert.alert.IAlertResultDispatcher;
import com.opentach.common.alert.exception.AlertException;
import com.opentach.common.alert.result.DefaultAlertResult;
import com.opentach.common.alert.result.IAlertResult;

/**
 * Example of alert, for debug purpose.
 */
public class DummyAlertResultDispatcher extends DummyAlert implements IAlertResultDispatcher {

	private static final Logger	logger	= LoggerFactory.getLogger(DummyAlertResultDispatcher.class);

	public DummyAlertResultDispatcher(Number id, String name, Properties properties, Map<?, ?> settings) {
		super(id, name, properties, settings);
	}

	@Override
	public void processResults(Date executionDate, Number executionNumber, List<IAlertResult> results) throws AlertException {
		// Log to console
		DummyAlertResultDispatcher.logger.debug("M_SAVING_RESULTS__alrId:{}  executionNumber:{}", this.getAlrId(), executionNumber);
		if ((results == null) | results.isEmpty()) {
			DummyAlertResultDispatcher.logger.trace("\tEMPTY_RESULT");
		} else {
			DummyAlertResultDispatcher.logger.trace("\tRESULTS:");
			results.stream().forEach((result) -> DummyAlertResultDispatcher.logger.trace("\t\t{}", result));
		}

		// Single result: single email and single result entry to save
		List<String> values = results == null ? null : results.stream().map((result) -> {
			return String.valueOf(result.getData().get("VALOR"));
		}).collect(Collectors.toList());
		this.saveAlertResult(executionDate, executionNumber, this, new DefaultAlertResult(EntityResultTools.keysvalues("VALUES", values)));
	}

	@Override
	public void onError(Date executionDate, Number executionNumber, Exception err) {
		DummyAlertResultDispatcher.logger.error("E_EXECUTING_ALERT", err);
	}

}