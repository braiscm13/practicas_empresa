package com.opentach.server.indicator.indicator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.ObjectTools;
import com.ontimize.jee.common.tools.StringTools;
import com.ontimize.locator.SecureReferenceLocator;
import com.opentach.common.indicator.exception.IndicatorException;
import com.opentach.common.indicator.naming.IndicatorNaming;
import com.opentach.common.indicator.result.DefaultIndicatorResult;
import com.opentach.common.indicator.result.DefaultIndicatorResultCompany;
import com.opentach.common.indicator.result.DefaultIndicatorResultCompanyExtra;
import com.opentach.common.indicator.result.DefaultIndicatorResultEmployee;
import com.opentach.common.indicator.result.DefaultIndicatorResultEmployeeExtra;
import com.opentach.common.indicator.result.DefaultIndicatorResultExtra;
import com.opentach.common.indicator.result.IIndicatorResult;
import com.opentach.server.util.db.FileTableEntity;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.tools.exception.UException;

public class DatabaseBasedIndicator extends AbstractIndicator {

	/** The CONSTANT logger */
	private static final Logger		logger		= LoggerFactory.getLogger(DatabaseBasedIndicator.class);

	protected static final String PARAM_SQL = "SQL";

	public DatabaseBasedIndicator(Number id, String name, Properties properties) {
		super(id, name, properties);
	}

	@Override
	public List<IIndicatorResult> execute(Number executionNumber) throws IndicatorException {
		String sql = this.getProperties().getProperty(DatabaseBasedIndicator.PARAM_SQL);
		CheckingTools.failIf(StringTools.isEmpty(sql), IndicatorException.class, "E_MISSING_SQL", new Object[] { this.getId(), this.getName() });

		EntityResult res = this.executeQuery(sql);
		return this.convertResult(res, executionNumber);
	}

	protected EntityResult executeQuery(String sql) throws IndicatorException {
		DatabaseBasedIndicator.logger.debug("sentence_to_execute:{}", sql);
		try {
			EntityResult res = new OntimizeConnectionTemplate<EntityResult>() {
				@Override
				protected EntityResult doTask(Connection con) throws UException, SQLException {
					return new QueryJdbcTemplate<EntityResult>() {

						@Override
						protected EntityResult parseResponse(ResultSet rset) throws UException, SQLException {
							EntityResult res = new EntityResult();
							FileTableEntity.resultSetToEntityResult(rset, res);
							return res;
						}
					}.execute(con, sql);
				}
			}.execute((SecureReferenceLocator) this.getLocator(), true);
			return res;
		} catch (Exception err) {
			throw new IndicatorException("E_EXECUTING_SQL", err);
		}
	}

	protected List<IIndicatorResult> convertResult(EntityResult res, Number executionNumber) throws IndicatorException {
		boolean ok = (res != null) && !res.isWrong();
		DatabaseBasedIndicator.logger.debug("result_to_convert:  ok:{}  numRecords:{}  columns:{}", ok, ok ? res.calculateRecordNumber() : 0, ok ? res.keySet().toArray() : null);
		if (!ok) {
			throw new IndicatorException("E_QUERY_WRONG_RESULT");
		}
		int numRes = res.calculateRecordNumber();
		List<IIndicatorResult> result = new ArrayList<IIndicatorResult>();
		for (int i = 0; i < numRes; i++) {
			Hashtable recordValues = res.getRecordValues(i);
			IIndicatorResult recordResult = this.convertValuesToResult(recordValues, executionNumber);
			result.add(recordResult);
		}
		return result;
	}

	protected IIndicatorResult convertValuesToResult(Hashtable recordValues, Object executionNumber) {
		// Parse results
		Object comId = recordValues.get(IndicatorNaming.COM_ID);
		Object drvId = recordValues.get(IndicatorNaming.DRV_ID);
		Object extraId = recordValues.get(IndicatorNaming.EXE_IDEXTRA);
		boolean isCompany = comId != null;
		boolean isEmployee = drvId != null;
		boolean isExtra = extraId != null;
		Object value = recordValues.get(IndicatorNaming.EXE_VALUE);
		Date date = ObjectTools.coalesce((Date) recordValues.get(IndicatorNaming.EXE_DATE), new Date());

		// Compose result appropriately
		IIndicatorResult recordResult = null;
		if (isEmployee && isExtra) {
			recordResult = new DefaultIndicatorResultEmployeeExtra(comId, drvId, extraId, value, date);
		} else if (isEmployee) {
			recordResult = new DefaultIndicatorResultEmployee(comId, drvId, value, date);
		} else if (isCompany && isExtra) {
			recordResult = new DefaultIndicatorResultCompanyExtra(comId, extraId, value, date);
		} else if (isCompany) {
			recordResult = new DefaultIndicatorResultCompany(comId, value, date);
		} else if (isExtra) {
			recordResult = new DefaultIndicatorResultExtra(extraId, value, date);
		} else {
			recordResult = new DefaultIndicatorResult(value, date);
		}

		return recordResult;
	}

}
