package com.opentach.server.entities;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.db.SQLStatementBuilder;
import com.ontimize.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.db.SQLStatementBuilder.BasicField;
import com.ontimize.db.SQLStatementBuilder.BasicOperator;
import com.ontimize.db.TableEntity;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.user.IUserData;
import com.opentach.server.OpentachServerLocator;
import com.opentach.server.util.db.FileTableEntity;

public class EGrupoEmpFiltered extends FileTableEntity {

	private static final Logger	logger	= LoggerFactory.getLogger(EGrupoEmpFiltered.class);

	public EGrupoEmpFiltered(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	public EGrupoEmpFiltered(EntityReferenceLocator locator, DatabaseConnectionManager dbConnectionManager, int port, Properties prop,
			Properties aliasProp)
					throws Exception {
		super(locator, dbConnectionManager, port, prop, aliasProp);
	}

	@Override
	public EntityResult query(Hashtable cv, Vector av, int sesionId, Connection con) throws Exception {

		// consulto las empresas del usuario y las que forman parte de las cooperativas

		IUserData du = ((OpentachServerLocator) this.locator).getUserData(sesionId);
		if ((sesionId == TableEntity.getEntityPrivilegedId(this)) || IUserData.NIVEL_SUPERVISOR.equals(du.getLevel())) {
			return super.query(cv, av, sesionId, con);
		}
		if (!cv.containsKey(OpentachFieldNames.CIF_FIELD)) {
			List<String> lCompanies = du.getCompaniesList();
			if ((lCompanies == null) || (lCompanies.size() == 0)) {
				if (IUserData.NIVEL_OPERADOR.equals(du.getLevel())) {
					return super.query(cv, av, sesionId, con);
				}
				return new EntityResult();
			}
			BasicExpression be = new BasicExpression(new BasicField(OpentachFieldNames.CIF_FIELD), BasicOperator.IN_OP, lCompanies);
			cv.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, be);
		}

		EntityResult res = super.query(cv, av, sesionId, con);
		res = EntityResultTools.doRemoveDuplicates(res);
		return res;
	}
}