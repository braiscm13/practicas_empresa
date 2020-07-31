package com.opentach.server.alert.alert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.StringTools;
import com.opentach.common.alert.exception.AlertException;
import com.opentach.common.alert.naming.AlertNaming;
import com.opentach.common.alert.result.DefaultAlertResult;
import com.opentach.common.alert.result.DefaultCompanyAlertResult;
import com.opentach.common.alert.result.DefaultEmployeeAlertResult;
import com.opentach.common.alert.result.IAlertResult;

public class DatabaseBasedAlert extends AbstractAlert {

	/** The CONSTANT logger */
	private static final Logger		logger		= LoggerFactory.getLogger(DatabaseBasedAlert.class);

	protected static final String	PARAM_SQL	= "SQL";

	public DatabaseBasedAlert(Number id, String name, Properties properties, Map<?, ?> settings) {
		super(id, name, properties, settings);

	}

	@Override
	public List<IAlertResult> execute() throws AlertException {
		String sql = (String) this.getProperty(DatabaseBasedAlert.PARAM_SQL);
		CheckingTools.failIf(StringTools.isEmpty(sql), AlertException.class, "E_MISSING_SQL", new Object[] { this.getAlrId(), this.getName() });
		EntityResult erData = this.executeQuery(sql);
		return this.convertResult(erData);
	}

	// TODO move this one to helper class
	protected List<IAlertResult> convertResult(EntityResult res) throws AlertException {
		boolean ok = (res != null) && !res.isWrong();
		DatabaseBasedAlert.logger.debug("result_to_convert:  ok:{}  numRecords:{}  columns:{}", ok, ok ? res.calculateRecordNumber() : 0, ok ? res.keySet().toArray() : null);
		if (!ok) {
			throw new AlertException("E_QUERY_WRONG_RESULT");
		}

		List<IAlertResult> result = new ArrayList<>();
		for (int i = 0, nrec = res.calculateRecordNumber(); i < nrec; i++) {
			Map<?, ?> recordValues = res.getRecordValues(i);
			List<IAlertResult> lAlertResult = this.convertValuesToResult(recordValues);
			result.addAll(lAlertResult);
		}
		return result;
	}

	protected List<IAlertResult> convertValuesToResult(Map<?, ?> data) {
		// Parse results
		Object comId = data.get(AlertNaming.COM_ID);
		Object drvId = data.get(AlertNaming.DRV_ID);

		// Compose result appropriately
		List<IAlertResult> lAlertResult = new ArrayList<>();
		IAlertResult alertResult = null;
		if (drvId != null) {
			alertResult = new DefaultEmployeeAlertResult(data, comId, drvId);
		} else if (comId != null) {
			alertResult = new DefaultCompanyAlertResult(data, comId);
		} else {
			alertResult = new DefaultAlertResult(data);
		}
		lAlertResult.add(alertResult);
		return lAlertResult;
	}

}
