package com.opentach.server.entities;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
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
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.user.IUserData;
import com.opentach.server.OpentachServerLocator;
import com.opentach.server.util.db.FileTableEntity;

public class EDfEmpCertificado extends FileTableEntity {

	private static final Logger	logger	= LoggerFactory.getLogger(EDfEmpCertificado.class);

	public EDfEmpCertificado(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	public EntityResult query(Hashtable cv, Vector av, int sesionId, Connection con) throws Exception {
		if (!cv.containsKey("IDGRUPO")) {
			if (!cv.containsKey(OpentachFieldNames.CIF_FIELD)) {
				IUserData du = ((OpentachServerLocator) this.locator).getUserData(sesionId);
				List<String> lCompanies = du.getCompaniesList();
				if (lCompanies!=null){
					BasicExpression be = new BasicExpression(new BasicField(OpentachFieldNames.CIF_FIELD), BasicOperator.IN_OP, lCompanies);
					BasicExpression be1 = new BasicExpression(new BasicField("CIF_COOPERATIVA"), BasicOperator.IN_OP, lCompanies);
					BasicExpression be2 = new BasicExpression(be, BasicOperator.OR_OP, be1);
					cv.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, be2);
				}
			}
		}
		return super.query(cv, av, sesionId, con);
	}

	@Override
	public EntityResult insert(Hashtable av, int sesionId, Connection con) throws Exception {
		String cif = (String) av.get("CIF");
		EntityResult res = super.insert(av, sesionId, con);

		// Si se ha insertado la empresa desde un usuario no administrador hay
		// que insertarla en EUsuDfEmpUsuarios
		IUserData du = ((OpentachServerLocator) this.locator).getUserData(sesionId);
		if (!"0".equals(du.getLevel())) {
			Hashtable<String, Object> av2 = new Hashtable<String, Object>();
			av2.put("USUARIO", du.getLogin());
			av2.put("CIF", cif);
			av2.put("USUARIO_ALTA", du.getLogin());
			av2.put("F_ALTA", new Date());
			TableEntity entUsuEmp = (TableEntity) this.getEntityReference("EUsuDfEmpUsuarios");
			entUsuEmp.insert(av2, TableEntity.getEntityPrivilegedId(entUsuEmp), con);
			if (du.getCompaniesList() != null) {
				du.getCompaniesList().add(cif);
			}
		}
		return res;
	}

	@Override
	public EntityResult delete(Hashtable av, int sessionId, Connection conn) throws Exception {
		Statement stmt = null;
		EntityResult res = null;
		try {
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			String cif = (String) av.get(OpentachFieldNames.CIF_FIELD);
			Vector<Object> v = new Vector<Object>(2);
			v.add(OpentachFieldNames.NUMREQ_FIELD);
			v.add(OpentachFieldNames.IDINSPECCION_FIELD);
			TableEntity eEmpreReq = (TableEntity) this.getEntityReference("EEmpreReq");
			res = eEmpreReq.query(av, v, sessionId, conn);
			if (res.getCode() == EntityResult.OPERATION_SUCCESSFUL) {
				if (res.calculateRecordNumber() > 0) {
					Vector<Object> vNumreq = (Vector<Object>) res.get(OpentachFieldNames.NUMREQ_FIELD);
					Vector<Object> vIdInsp = (Vector<Object>) res.get(OpentachFieldNames.IDINSPECCION_FIELD);
					for (int i = 0; i < vNumreq.size(); i++) {
						String numreq = (String) vNumreq.elementAt(i);
						String idInsp = (String) vIdInsp.elementAt(i);
						stmt.executeUpdate("DELETE FROM CDACTIVIDADES 			WHERE NUMREQ			 ='" + numreq + "'");
						stmt.executeUpdate("DELETE FROM CDPERIODOS_TRABAJO 	WHERE NUMREQ			 ='" + numreq + "'");
						stmt.executeUpdate("DELETE FROM CDUSO_VEHICULO 			WHERE NUMREQ 			 ='" + numreq + "'");
						stmt.executeUpdate("DELETE FROM CDCONDUCTOR_CONT 		WHERE CG_CONTRATO  ='" + numreq + "'");
						stmt.executeUpdate("DELETE FROM CDFICHEROS_CONTRATO	WHERE CG_CONTRATO  ='" + numreq + "'");
						stmt.executeUpdate("DELETE FROM CDINCIDENTES 				WHERE NUMREQ			 ='" + numreq + "'");
						stmt.executeUpdate("DELETE FROM CDCALIBRADO 				WHERE NUMREQ			 ='" + numreq + "'");
						stmt.executeUpdate("DELETE FROM CDCONTROLES 				WHERE NUMREQ			 ='" + numreq + "'");
						stmt.executeUpdate("DELETE FROM CDFALLOS 						WHERE NUMREQ			 ='" + numreq + "'");
						stmt.executeUpdate("DELETE FROM CDVEHICULO_CONT 		WHERE CG_CONTRATO  ='" + numreq + "'");
						stmt.executeUpdate("DELETE FROM CDEMPRE_REQ 				WHERE NUMREQ			 ='" + numreq + "'");
						stmt.executeUpdate("DELETE FROM CDDATOS_INSPECCION 	WHERE IDINSPECCION = '" + idInsp + "'");
					}
				}
				stmt.executeUpdate("DELETE FROM CDCONDUCTORES_EMP WHERE CIF ='" + cif + "'");
				stmt.executeUpdate("DELETE FROM CDVEHICULOS_EMP 	WHERE CIF ='" + cif + "'");
				stmt.executeUpdate("DELETE FROM DFEMP 						WHERE CIF = '" + cif + "'");
				stmt.executeUpdate("DELETE FROM CDUSU_DFEMP 			WHERE CIF = '" + cif + "'");
			}
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			EDfEmpCertificado.logger.error(null, e);
			throw e;
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				conn.setAutoCommit(true);
			} catch (Exception e) {
			}
		}
		return res;

	}
}