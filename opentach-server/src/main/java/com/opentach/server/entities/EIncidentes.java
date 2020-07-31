package com.opentach.server.entities;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.db.SQLStatementBuilder;
import com.ontimize.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.db.SQLStatementBuilder.BasicField;
import com.ontimize.db.SQLStatementBuilder.BasicOperator;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.server.util.db.FileTableEntity;

public class EIncidentes extends FileTableEntity {

	private static final Logger	logger	= LoggerFactory.getLogger(EIncidentes.class);

	public EIncidentes(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	public EntityResult query(Hashtable cv, Vector v, int sesionId) throws Exception {
		try {
			Date fini_insp = (Date) cv.get("FECINI_INSP");
			if (fini_insp != null) {
				Date ffin_insp = (Date) cv.get("FECFIN_INSP");
				cv.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY,
						this.buildExpression("FECINI", "FECFIN", fini_insp, ffin_insp));
			}
		} catch (Exception ex) {
			EIncidentes.logger.error(null, ex);
		}
		return super.query(cv, v, sesionId);
	}

	private BasicExpression buildExpression(String colFecIni, String colFecFin, Date initDate, Date endDate) {
		Timestamp initTime = new Timestamp(initDate.getTime());
		Timestamp endTime = new Timestamp(endDate.getTime());

		BasicField bfID = new BasicField(colFecIni);
		BasicField bfED = new BasicField(colFecFin);

		BasicOperator boLE = (BasicOperator) BasicOperator.LESS_EQUAL_OP;
		BasicOperator boL = (BasicOperator) BasicOperator.LESS_OP;
		BasicOperator boME = (BasicOperator) BasicOperator.MORE_EQUAL_OP;
		BasicOperator boM = (BasicOperator) BasicOperator.MORE_OP;
		BasicOperator boAND = (BasicOperator) BasicOperator.AND_OP;
		BasicOperator boOR = (BasicOperator) BasicOperator.OR_OP;
		// InitTime -------------------------- EndTime
		// bfID ------------------------------- bfEd
		// bfID -------------------------------------- bfEd
		// bfID --------- bfEd
		// bfID ---------------------------------------------- bfEd
		BasicExpression exprA1 = new BasicExpression(bfID, boLE, initTime);
		BasicExpression exprA2 = new BasicExpression(bfED, boM, initTime);
		BasicExpression exprA3 = new BasicExpression(exprA1, boAND, exprA2);
		BasicExpression exprB1 = new BasicExpression(bfED, boME, endTime);
		BasicExpression exprB2 = new BasicExpression(bfID, boL, endTime);
		BasicExpression exprB3 = new BasicExpression(exprB1, boAND, exprB2);
		BasicExpression exprC1 = new BasicExpression(bfID, boM, initTime);
		BasicExpression exprC2 = new BasicExpression(bfED, boL, endTime);
		BasicExpression exprC3 = new BasicExpression(exprC1, boAND, exprC2);
		BasicExpression exprVF1Final = new BasicExpression(exprA3, boOR, exprB3);
		BasicExpression exprVFFinal = new BasicExpression(exprVF1Final, boOR, exprC3);
		return exprVFFinal;
	}

}
