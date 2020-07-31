package com.opentach.server.alert.alert;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.opentach.common.alert.exception.AlertException;
import com.opentach.common.alert.result.DefaultAlertResult;
import com.opentach.common.alert.result.IAlertResult;

public class SingleAlert extends AbstractAlert {

	public SingleAlert(Number id, String name, Properties properties, Map<?, ?> settings, Map<?, ?> extraData) {
		super(id, name, properties, settings, extraData);
	}

	@Override
	public List<IAlertResult> execute() throws AlertException {
		return Arrays.asList(new DefaultAlertResult(this.getExtraData()));
	}

}
