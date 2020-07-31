package com.opentach.server.entities;

import java.sql.Connection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.db.SQLStatementBuilder;
import com.ontimize.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.db.SQLStatementBuilder.BasicField;
import com.ontimize.db.SQLStatementBuilder.BasicOperator;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.server.util.db.OracleTableEntity;

public class UsuarioSinExpirar extends OracleTableEntity {

	public UsuarioSinExpirar(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	public EntityResult query(Hashtable cv, Vector v, int sesionId, Connection con) throws Exception {

		BasicExpression bs = new SQLStatementBuilder.BasicExpression(new BasicField("DMAXLOGIN"), BasicOperator.MORE_OP, new Date());
		BasicExpression bs2 = new SQLStatementBuilder.BasicExpression(new BasicField("DMAXLOGIN"), BasicOperator.NOT_NULL_OP, null);
		BasicExpression bs3 = new SQLStatementBuilder.BasicExpression(bs, BasicOperator.AND_OP, bs2);
		cv.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, bs3);

		return super.query(cv, v, sesionId, con);
	}
}
