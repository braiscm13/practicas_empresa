package com.opentach.server.entities;


import java.sql.Connection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.gui.SearchValue;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.company.naming.CompanyNaming;
import com.opentach.common.user.IUserData;
import com.opentach.server.OpentachServerLocator;
import com.opentach.server.util.db.FileTableEntity;

public class EDfEmpArbol extends FileTableEntity {

	private static final Logger	logger	= LoggerFactory.getLogger(EDfEmpArbol.class);

	public EDfEmpArbol(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	public EntityResult query(Hashtable cv, Vector av, int sesionId, Connection con) throws Exception {
		IUserData du = ((OpentachServerLocator) this.locator).getUserData(sesionId);

		if (!"0".equals(du.getLevel()) && !cv.containsKey("CIF")) {
			List<String> l = du.getCompaniesList();
			cv.put("CIF",new SearchValue(SearchValue.IN, l));

		}


		// consulto EDfEmpCoop que me devuelve las cooperativas y las empresas que no son ni estan en cooperativas.
		TableEntity entUsuEmp = (TableEntity) this.getEntityReference("EDfEmpCoop");
		EntityResult res = entUsuEmp.query(cv, av, sesionId, con);

		// ahora necesito añadirle las empresas están en cooperativas pero el usuario no tiene acceso a ellas
		if (!"0".equals(du.getLevel())) {
			if (cv.containsKey("CIF")) {
				TableEntity entEmp = (TableEntity) this.getEntityReference(CompanyNaming.ENTITY);
				cv.put("ARBOL", "S");
				EntityResult resEmp = entEmp.query(cv, av, sesionId, con);
				EntityResult resTotal = EntityResultTools.doUnion(res, resEmp);
				return resTotal;
			}
			List<String> l = du.getCompaniesList();
			if (l!=null){
				Vector<String> cifCoop = (Vector<String>)res.get("CIF");
				Vector<String> resto = new Vector<String>();
				Iterator<String> it = l.iterator();

				while (it.hasNext()) {
					String cif = it.next();
					if ((cifCoop==null) || (cifCoop.size()==0) || !cifCoop.contains(cif)){
						resto.add(cif);
					}
				}
				if (resto.size()>0){
					Hashtable<String, Object> avEmp = new Hashtable<String, Object>();
					// BasicExpression bs = new SQLStatementBuilder.BasicExpression(new BasicField("CIF"), BasicOperator.EQUAL_OP, resto.get(0));
					// BasicExpression bs2 = bs;
					// for (int i=1; i<resto.size();i++){
					// bs2 = new SQLStatementBuilder.BasicExpression(bs2 , BasicOperator.OR_OP, new SQLStatementBuilder.BasicExpression(new
					// BasicField("CIF"), BasicOperator.EQUAL_OP, resto.get(i)));
					// }
					// avEmp.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, bs2);

					avEmp.put("CIF", new SearchValue(SearchValue.IN, resto));

					TableEntity entEmp = (TableEntity) this.getEntityReference(CompanyNaming.ENTITY);
					EntityResult resEmp = entEmp.query(avEmp, av, sesionId, con);
					EntityResult resTotal = EntityResultTools.doUnion(res, resEmp);
					return resTotal;
				}
			}
		}
		return res;
	}
}