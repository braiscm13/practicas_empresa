package com.opentach.server.util;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.db.SQLStatementBuilder;
import com.ontimize.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.db.SQLStatementBuilder.ExtendedSQLConditionValuesProcessor;
import com.ontimize.db.handler.SQLStatementHandler;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.StringTools;
import com.ontimize.jee.common.tools.Template;
import com.ontimize.jee.common.tools.Trio;
import com.opentach.common.sessionstatus.ISessionStatusReportService.GroupingTime;
import com.utilmize.server.UReferenceSeeker;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.server.tools.sqltemplate.QueryJdbcToEntityResultTemplate;
import com.utilmize.tools.exception.UException;

public final class StatementBuilderHelper {

	private static final Logger	logger						= LoggerFactory.getLogger(StatementBuilderHelper.class);

	private static final String	PLACEHOLDER_WHERE			= "#WHERE#";
	/** The Constant PLACEHOLDER_WHERE_CONCAT. */
	private static final String	PLACEHOLDER_WHERE_CONCAT	= "#WHERE_CONCAT#";

	private StatementBuilderHelper() {
		super();
	}

	public static EntityResult doQuery(UReferenceSeeker locator, String templatePrefix, BasicExpression queryFilter, GroupingTime groupingTime) throws Exception {
		return StatementBuilderHelper.doQuery(locator, templatePrefix, queryFilter, groupingTime, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
	}

	public static EntityResult doQuery(UReferenceSeeker locator, String templatePrefix, BasicExpression queryFilter, GroupingTime grouping,
			List<Trio<String, String, BasicExpression>> previousReplacements, List<Trio<String, String, BasicExpression>> postReplacement) throws Exception {

		Template sqlTemplate = new Template(templatePrefix + (grouping == null ? "" : grouping.toString()) + ".sql");
		SQLStatementHandler sqlStatementHandler = SQLStatementBuilder.getSQLStatementHandler(locator.getConnectionManager().getDatabase());
		final List<Trio<String, String, BasicExpression>> conditionsPlaceHolders = new ArrayList<>();
		conditionsPlaceHolders.addAll(previousReplacements);
		conditionsPlaceHolders.add(new Trio<>(StatementBuilderHelper.PLACEHOLDER_WHERE_CONCAT, "", queryFilter));
		conditionsPlaceHolders.addAll(postReplacement);

		final Vector vValues = new Vector();
		Map<String, String> replacement = new HashMap<>();
		for (Trio<String, String, BasicExpression> entry : conditionsPlaceHolders) {
			String cond = sqlStatementHandler.createQueryConditionsWithoutWhere(EntityResultTools.keysvalues(ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, entry.getThird()),
					new Vector<>(), vValues);
			if (cond == null) {
				cond = "";
			}
			cond = cond.trim();
			if (entry.getFirst().equals(StatementBuilderHelper.PLACEHOLDER_WHERE_CONCAT)) {
				replacement.put(StatementBuilderHelper.PLACEHOLDER_WHERE_CONCAT, cond.length() == 0 ? "" : (SQLStatementBuilder.AND + " " + cond));
				replacement.put(StatementBuilderHelper.PLACEHOLDER_WHERE, cond.length() == 0 ? "" : (SQLStatementBuilder.WHERE + " " + cond));
			} else {
				if (!StringTools.isEmpty(cond)) {
					cond = entry.getSecond() + cond;
				}
				replacement.put(entry.getFirst(), cond);
			}
		}

		final String sql = sqlTemplate.fillTemplate(replacement);

		return new OntimizeConnectionTemplate<EntityResult>() {

			@Override
			protected EntityResult doTask(Connection con) throws UException {
				try {
					return new QueryJdbcToEntityResultTemplate().execute(con, sql, vValues.toArray());
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(locator.getConnectionManager(), true);
	}
}
