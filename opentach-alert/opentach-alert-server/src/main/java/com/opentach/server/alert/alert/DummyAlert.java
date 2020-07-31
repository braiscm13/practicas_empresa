package com.opentach.server.alert.alert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.ontimize.jee.common.tools.EntityResultTools;
import com.opentach.common.alert.exception.AlertException;
import com.opentach.common.alert.result.DefaultAlertResult;
import com.opentach.common.alert.result.IAlertResult;

/**
 * Example of alert, for debug purpose.
 */
public class DummyAlert extends AbstractAlert {

	public DummyAlert(Number id, String name, Properties properties, Map<?, ?> settings) {
		super(id, name, properties, settings);
	}

	@Override
	public List<IAlertResult> execute() throws AlertException {
		List<IAlertResult> result = new ArrayList<>();
		result.add(new DefaultAlertResult(EntityResultTools.keysvalues("VALOR", "Pepe")));
		result.add(new DefaultAlertResult(EntityResultTools.keysvalues("VALOR", "Juan")));
		result.add(new DefaultAlertResult(new HashMap<>()));
		return result;
	}

}
