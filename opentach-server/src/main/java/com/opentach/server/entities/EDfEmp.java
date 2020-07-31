package com.opentach.server.entities;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
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
import com.ontimize.gui.SearchValue;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.user.IUserData;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.companies.CompanyService;
import com.opentach.server.util.db.FileTableEntity;
import com.utilmize.server.tools.sqltemplate.QueryJdbcToEntityResultTemplate;

public class EDfEmp extends FileTableEntity {

	private static final Logger logger = LoggerFactory.getLogger(EDfEmp.class);

	public EDfEmp(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	public EDfEmp(EntityReferenceLocator locator, DatabaseConnectionManager dbConnectionManager, int port, Properties prop, Properties aliasProp) throws Exception {
		super(locator, dbConnectionManager, port, prop, aliasProp);
	}

	@Override
	public EntityResult query(Hashtable cv, Vector av, int sesionId, Connection con) throws Exception {

		// consulto las empresas del usuario y las que forman parte de las cooperativas
		EntityResult res = new EntityResult();
		IUserData du = ((IOpentachServerLocator) this.locator).getUserData(sesionId);
		if ((sesionId == TableEntity.getEntityPrivilegedId(this)) || IUserData.NIVEL_SUPERVISOR.equals(du.getLevel())) {
			return super.query(cv, av, sesionId, con);
		}

		List<String> lCompanies;
		if (cv.containsKey("ARBOL")) {
			lCompanies = du.getCompaniesList();
		} else {
			lCompanies = du.getAllCompaniesList();
		}

		if (!cv.containsKey(OpentachFieldNames.CIF_FIELD)) {
			if ((lCompanies == null) || (lCompanies.size() == 0)) {
				if (IUserData.NIVEL_OPERADOR.equals(du.getLevel())) {
					return super.query(cv, av, sesionId, con);
				}
				return new EntityResult();
			}
			BasicExpression be = new BasicExpression(new BasicField(OpentachFieldNames.CIF_FIELD), BasicOperator.IN_OP, lCompanies);
			BasicExpression be1 = new BasicExpression(new BasicField("CIF_COOPERATIVA"), BasicOperator.IN_OP, lCompanies);
			BasicExpression be2 = new BasicExpression(be, BasicOperator.OR_OP, be1);
			cv.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, be2);
		}

		return super.query(cv, av, sesionId, con);
	}

	@Override
	public EntityResult update(Hashtable atributosValoresA, Hashtable clavesValoresA, int sesionId, Connection con) throws Exception {

		EntityResult res = this.query(clavesValoresA, new Vector(), sesionId, con);
		Hashtable avRegistro = res.getRecordValues(0);

		String contratoCoop = atributosValoresA.get("CONTRATO_COOP") != null ? (String) atributosValoresA.get("CONTRATO_COOP") : (String) avRegistro.get("CONTRATO_COOP");
		String isCooperativa = atributosValoresA.get("IS_COOPERATIVA") != null ? (String) atributosValoresA.get("IS_COOPERATIVA") : (String) avRegistro.get("IS_COOPERATIVA");
		String cifCooperativa = atributosValoresA.get("CIF_COOPERATIVA") != null ? (String) atributosValoresA.get("CIF_COOPERATIVA") : (String) avRegistro.get("CIF_COOPERATIVA");

		if ("S".equals(contratoCoop) && "S".equals(isCooperativa)) {
			throw new Exception("ERROR_COOPERATIVA_CONTRATO");
		}

		if (atributosValoresA.containsKey("CONTRATO_COOP") && ("S").equals(contratoCoop)) {
			this.insertDummyContract(clavesValoresA.get(OpentachFieldNames.CIF_FIELD), cifCooperativa, sesionId, con);
		}
		// TODO eliminar contrato ficticio si pasa a N?

		return super.update(atributosValoresA, clavesValoresA, sesionId, con);
	}

	@Override
	public EntityResult insert(Hashtable av, int sesionId, Connection con) throws Exception {
		String cif = (String) av.get("CIF");

		if (!av.containsKey("IS_COOPERATIVO")) {
			av.put("IS_COOPERATIVO", "N");
		}
		if (!av.containsKey("CONTRATO_COOP")) {
			av.put("CONTRATO_COOP", "N");
		}

		//
		EntityResult res = super.insert(av, sesionId, con);

		// Si se ha insertado la empresa desde un usuario no administrador hay
		// que insertarla en EUsuDfEmpUsuarios
		IUserData du = ((IOpentachServerLocator) this.locator).getUserData(sesionId);
		if ((du != null) && !"0".equals(du.getLevel())) {
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
		if (av.containsKey("CIF_COOPERATIVA") && av.containsKey("CONTRATO_COOP") && av.get("CONTRATO_COOP").equals("S")) {
			EntityResult resdummy= this.insertDummyContract(av.get("CIF"), av.get("CIF_COOPERATIVA"), sesionId, con);

			if (!av.containsKey("NEW_SBO")){
				// insertamos el contrato de la cooperativa asociado al cliente que acabamos de dar de alta
				TableEntity entEmpreReq = (TableEntity) this.getEntityReference("EEmpreReq");
				Hashtable<String,Object> cv = new Hashtable<String,Object>();
				cv.put(OpentachFieldNames.CIF_FIELD ,av.get("CIF_COOPERATIVA"));
				EntityResult resNumReq = entEmpreReq.query(cv, new Vector(), sesionId,con);
			}


		}
		return res;
	}

	@Override
	public EntityResult delete(Hashtable av, int sessionId, Connection conn) throws Exception {
		String cif = (String) av.get(OpentachFieldNames.CIF_FIELD);
		return this.getService(CompanyService.class).deleteCompany(cif, sessionId, conn);
	}

	private EntityResult insertDummyContract(Object cif, Object cifCooperativa, int sesionId, Connection con) throws Exception {
		// GENERO UN NUMREQ FICTICIO
		TableEntity entReq = (TableEntity) this.getEntityReference("EEmpreReq");
		Hashtable<String, Object> cvCoop = new Hashtable<String, Object>();
		cvCoop.put("CIF", cifCooperativa);
		cvCoop.put("F_BAJA", new SearchValue(SearchValue.NULL, null));
		EntityResult resCoop = entReq.query(cvCoop, new Vector<Object>(), sesionId, con);
		Hashtable<String, Object> regNumReq = new Hashtable<String, Object>();
		if (resCoop.calculateRecordNumber() > 0) {
			regNumReq = resCoop.getRecordValues(0);
		}
		//		BasicExpression be = new BasicExpression(new BasicField(OpentachFieldNames.NUMREQ_FIELD), BasicOperator.LIKE_OP, new BasicField("'%FIC_%'"));
		//		cvCoop = new Hashtable<String, Object>();
		//		cvCoop.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, be);
		//		EntityResult resFict = entReq.query(cvCoop, new Vector(), sesionId, con);
		EntityResult resFict = new QueryJdbcToEntityResultTemplate().execute(con, "SELECT NUMREQ FROM CDEMPRE_REQ WHERE NUMREQ LIKE '%FIC_%'");
		int numFinal = 0;
		if ((resFict != null) && (resFict.calculateRecordNumber() > 0)) {
			Vector<Object> vnumReqFic = (Vector<Object>) resFict.get("NUMREQ");

			for (int i = 0; i < vnumReqFic.size(); i++) {
				String numReq = (String) vnumReqFic.get(i);
				String num = numReq.substring(4, numReq.length());
				int inum = (new Integer(num)).intValue();
				if (inum > numFinal) {
					numFinal = inum;
				}
			}
		}

		numFinal++;
		String newnum = numFinal + "";
		while (newnum.length() < 5) {
			newnum = "0" + newnum;
		}
		newnum = "FIC_" + newnum;
		// inserto en CDEMPRE_REQ
		regNumReq.put("NUMREQ", newnum);
		regNumReq.put("CG_CONTRATO", newnum);
		regNumReq.put("CIF", cif);
		regNumReq.put("FICTICIO", "S");
		regNumReq.put("F_REQ", new Date());
		regNumReq.put("F_ALTA", new Date());
		regNumReq.put("USUARIO_ALTA", this.getUser(sesionId));
		return entReq.insert(regNumReq, sesionId, con);
	}

	public Map<String, Object> getCompanyInfo(Object cif, int sessionID, Connection con, String... attributes) throws Exception {

		Hashtable<Object, Object> kv = EntityResultTools.keysvalues("CIF", cif);
		Vector<String> av = new Vector<String>(Arrays.asList(attributes));
		EntityResult er = null;

		if (sessionID == -1) {
			sessionID = TableEntity.getEntityPrivilegedId(this);
		}
		if (con != null) {
			er = this.query(kv, av, sessionID, con);
		} else {
			er = this.query(kv, av, sessionID);
			CheckingTools.checkValidEntityResult(er);
		}
		if (er.calculateRecordNumber() > 0) {
			return er.getRecordValues(0);
		}
		return new HashMap<String, Object>();
	}
}