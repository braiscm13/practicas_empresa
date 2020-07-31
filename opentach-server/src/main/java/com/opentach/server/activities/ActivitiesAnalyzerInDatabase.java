package com.opentach.server.activities;

import java.sql.Connection;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.db.SQLStatementBuilder;
import com.ontimize.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.db.TransactionalEntity;
import com.ontimize.gui.SearchValue;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.user.IUserData;
import com.opentach.common.util.StringUtils;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.entities.EInformeInfrac;
import com.opentach.server.util.AbstractDelegate;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.server.tools.sqltemplate.ProcedureJdbcTemplate;
import com.utilmize.tools.exception.UException;

public class ActivitiesAnalyzerInDatabase extends AbstractDelegate {

	private static final Logger logger = LoggerFactory.getLogger(ActivitiesAnalyzerInDatabase.class);

	public ActivitiesAnalyzerInDatabase(IOpentachServerLocator locator) {
		super(locator);
	}

	public EntityResult analyze(final String cgContrato, final String cardNumber, final Vector<Object> vDrivers, final Date fIni, final Date fFin, final boolean queryPeriods,
			final Vector<String> av, final int sessionId) throws Exception {
		if (cgContrato == null) {
			throw new Exception("M_NO_SE_PUEDE_REALIZAR_ANALISIS");
		}

		return new OntimizeConnectionTemplate<EntityResult>() {
			@Override
			protected EntityResult doTask(Connection conn) throws UException {
				try {
					return ActivitiesAnalyzerInDatabase.this.analyze(cgContrato, cardNumber, vDrivers, fIni, fFin, queryPeriods, av, sessionId, conn);
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(this.getConnectionManager(), false);
	}

	private EntityResult analyze(String numReq, String cardNumber, Vector<Object> vDrivers, Date fIni, Date fFin, boolean queryPeriods, Vector<String> av, int sesionId,
			Connection con) throws Exception {
		EntityResult res = null;
		try {

			String sDrivers = StringUtils.vectorToCommaSeparated(vDrivers);

			new ProcedureJdbcTemplate<Void>().execute(con, "PRC_INFRAC_ORGANIZA_ACTIV_AR", numReq, sDrivers, fIni, fFin);

			Hashtable<String, Object> cv = new Hashtable<String, Object>();
			cv.put("NUMREQ", numReq);
			cv.put("CG_CONTRATO", numReq);
			cv.put(EInformeInfrac.DO_QUERY, new Object());
			cv.put("IDCONDUCTOR", new SearchValue(SearchValue.IN, vDrivers));
			// TODO fecini y fecfin
			res = this.getEntity("EInformeActivCond").query(cv, av, sesionId, con);

			IUserData ud = this.getLocator().getUserData(sesionId);

			// sólo si es administrador o empresa gestion tarjetas se muestran
			// datos de una única tarjeta
			if ((ud.getLevel().equals("0") || ud.getLevel().equals("18"))) {
				Set<Object> set = new HashSet<Object>();
				Vector<Object> vNum = new Vector<Object>();
				if (res.containsKey("NUM_TARJ")) {
					vNum = (Vector<Object>) res.get("NUM_TARJ");

					for (int i = 0; i < vNum.size(); i++) {
						if (!set.contains(vNum.get(i))) {
							set.add(vNum.get(i));
						}
					}
				}

				if (set.size() > 1) {
					SQLStatementBuilder.BasicField fieldNumTrj = new SQLStatementBuilder.BasicField("NUM_TARJ");
					if (set.contains(null)) {
						BasicExpression bs = new BasicExpression(fieldNumTrj, SQLStatementBuilder.BasicOperator.NULL_OP, null);
						BasicExpression bs1 = new BasicExpression(fieldNumTrj, SQLStatementBuilder.BasicOperator.EQUAL_OP, "0000");
						BasicExpression bs7 = new BasicExpression(bs, SQLStatementBuilder.BasicOperator.OR_OP, bs1);
						cv.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, bs7);
					} else {
						BasicExpression bs = new BasicExpression(fieldNumTrj, SQLStatementBuilder.BasicOperator.EQUAL_OP, vNum.get(0));
						BasicExpression bs1 = new BasicExpression(fieldNumTrj, SQLStatementBuilder.BasicOperator.EQUAL_OP, "0000");
						BasicExpression bs7 = new BasicExpression(bs, SQLStatementBuilder.BasicOperator.OR_OP, bs1);
						cv.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, bs7);
					}
					res = this.getEntity("EInformeActivCond").query(cv, av, sesionId, con);
				}
			}
		} finally {
			try {
				con.rollback();
				con.setAutoCommit(true);
			} catch (Exception e) {
			}
		}

		if (queryPeriods) {
			EntityResult resPeriodos = this.queryPeriods(numReq, cardNumber, vDrivers, fIni, fFin, sesionId, con);
			EntityResult resmayor = new EntityResult();
			Hashtable<String, Object> avmayor = new Hashtable<String, Object>();
			avmayor.put("ACTIVIDADES", res);
			avmayor.put("PERIODOS", resPeriodos);
			resmayor.addRecord(avmayor);
			return resmayor;
		}
		return res;

	}

	private EntityResult queryPeriods(Object numReq, String cardNumber, Vector<Object> vDrivers, Date fIni, Date fFin, int sesionId, Connection con) throws Exception {
		TransactionalEntity ent2 = this.getEntity("EPeriodosTrabajo");
		Hashtable<String, Object> cvPeriodos = new Hashtable<String, Object>();
		cvPeriodos.put(OpentachFieldNames.NUMREQ_FIELD, numReq);
		cvPeriodos.put(OpentachFieldNames.IDCONDUCTOR_FIELD, new SearchValue(SearchValue.IN, vDrivers));
		cvPeriodos.put(OpentachFieldNames.FECINI_FIELD, fIni);
		cvPeriodos.put(OpentachFieldNames.FECFIN_FIELD, fFin);
		if (cardNumber != null) {
			cvPeriodos.put("NUM_TARJETA", cardNumber);
		}

		EntityResult resPeriodos = ent2.query(cvPeriodos, new Vector(0), sesionId, con);
		return resPeriodos;
	}
}
