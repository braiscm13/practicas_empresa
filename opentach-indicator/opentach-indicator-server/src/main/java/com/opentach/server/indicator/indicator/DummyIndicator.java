package com.opentach.server.indicator.indicator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.opentach.common.indicator.exception.IndicatorException;
import com.opentach.common.indicator.result.DefaultIndicatorResult;
import com.opentach.common.indicator.result.IIndicatorResult;

/**
 * Example of indicator, for debug purpose.
 */
public class DummyIndicator extends AbstractIndicator {

	protected static final String	PARAM_NUM_ROWS	= "Num";
	protected static final String	PARAM_VALUE		= "Value";

	public DummyIndicator(Number id, String name, Properties properties) {
		super(id, name, properties);
	}

	@Override
	public List<IIndicatorResult> execute(Number executionNumber) throws IndicatorException {
		int num = ParseUtilsExtended.getInteger((String) this.getProperties().get(DummyIndicator.PARAM_NUM_ROWS), 3);
		double value = ParseUtilsExtended.getDouble((String) this.getProperties().get(DummyIndicator.PARAM_VALUE), 1.23);
		Date date = new Date();
		List<IIndicatorResult> result = new ArrayList<IIndicatorResult>();
		for (int i = 0; i < num; i++) {
			result.add(new DefaultIndicatorResult(value, date));
		}
		return result;
	}

}
