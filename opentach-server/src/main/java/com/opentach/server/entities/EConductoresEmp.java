package com.opentach.server.entities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.db.NullValue;
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
import com.opentach.server.companies.ContractService;
import com.opentach.server.util.ContractUtils;
import com.opentach.server.util.DBUtils;
import com.opentach.server.util.db.FileTableEntity;
import com.opentach.server.util.mail.AltaBajaAutoMailComposer.Type;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.tools.exception.UException;

public class EConductoresEmp extends FileTableEntity {

	private static final Logger logger = LoggerFactory.getLogger(EConductoresEmp.class);

	public EConductoresEmp(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	public EConductoresEmp(EntityReferenceLocator locator, DatabaseConnectionManager dbConnectionManager, int port, Properties prop, Properties aliasProp) throws Exception {
		super(locator, dbConnectionManager, port, prop, aliasProp);
	}

	@Override
	public EntityResult insert(Hashtable av, int sessionId) throws Exception {
		EntityResult er = super.insert(av, sessionId);

		String cif = (String) av.get(OpentachFieldNames.CIF_FIELD);
		String companyname = DBUtils.getCompanyName(this.getLocator(), cif, sessionId);
		if (er.getCode() == EntityResult.OPERATION_SUCCESSFUL) {
			// enviar mail de alta
			DBUtils.generateMailAuditDriverVeh(this.getLocator(), Type.ALTA, av, companyname, sessionId);
		}
		return er;
	}

	@Override
	public EntityResult insert(Hashtable av, int sesionId, Connection con) throws Exception {
		OpentachServerLocator bst = (OpentachServerLocator) this.locator;

		String companyCif = (String) av.get(OpentachFieldNames.CIF_FIELD);
		Object cgContrato = null;
		if (av.containsKey(OpentachFieldNames.CG_CONTRATO_FIELD)) {
			cgContrato = av.get(OpentachFieldNames.CG_CONTRATO_FIELD);
		} else {
			IUserData ds = bst.getUserData(sesionId);
			if ((ds != null) && (ds.getCIF() != null)) {
				// if the user isn't an admin update CDCONDUCTOR_EMP table (name, surname....)
				// ensure driver is not down in contract
				cgContrato = this.getService(ContractService.class).getContratoVigente(ds.getCIF(), sesionId);

			} else if ((ds == null) || (ds.getCIF() == null)) {
				// if the session is a privileged id
				cgContrato = this.getService(ContractService.class).getContratoVigente(companyCif, sesionId, con);
			}
		}

		if (av.containsKey("F_ALTA")) {
			av.put("F_ALTA", new Date());
		}
		String dni = (String) av.get(OpentachFieldNames.DNI_FIELD);
		if (dni != null) {
			dni = dni.trim();
			av.put(OpentachFieldNames.DNI_FIELD, dni);
		}

		String exists = new QueryJdbcTemplate<String>() {
			@Override
			protected String parseResponse(ResultSet rs) throws UException {
				try {
					if (rs.next()) {
						return rs.getString(1);
					}
					return null;
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(con, "SELECT IDCONDUCTOR FROM CDCONDUCTORES_EMP WHERE CIF = ? AND IDCONDUCTOR= ? ", av.get(OpentachFieldNames.CIF_FIELD),
				av.get(OpentachFieldNames.IDCONDUCTOR_FIELD));

		EntityResult resInsert = new EntityResult();

		if (exists == null) {
			resInsert = super.insert(av, sesionId, con);
		}

		if (cgContrato == null) {
			// Si no se puede asignar a ningún contrato
			resInsert.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
			resInsert.setMessage("Se ha dado de alta el conductor en la empresa.\n Pero no ha sido asociado a ningún contrato");
			return resInsert;
		}
		Object contrato = ContractUtils.checkContratoFicticio(this.getLocator(), cgContrato, sesionId, con);
		// Si se puede asignar a algún contrato o ya está asignado

		Vector<String> v = new Vector<String>();
		Hashtable<String, Object> aux = new Hashtable<String, Object>();

		aux.put(OpentachFieldNames.IDCONDUCTOR_FIELD, av.get(OpentachFieldNames.IDCONDUCTOR_FIELD));
		aux.put(OpentachFieldNames.CIF_FIELD, companyCif);
		aux.put(OpentachFieldNames.CG_CONTRATO_FIELD, contrato);
		TableEntity eCondCont = (TableEntity) this.getEntityReference("EConductorCont");
		EntityResult resCondCont = eCondCont.query(aux, v, this.getSessionId(sesionId, eCondCont), con);

		if (resCondCont.calculateRecordNumber() > 0) {
			// if this driver had already been added to this contract then insert update down date in contract

			// Confirmamos que el conductor no está dado de baja en el contrato vigente
			if (resCondCont.getRecordValues(0).get("F_BAJA") == null) {
				// no está dado de baja
				return new EntityResult(EntityResult.OPERATION_WRONG, EntityResult.OPERATION_WRONG, "CONDUCTOR_YA_EXISTE");
			}

			Hashtable<String, Object> cv = new Hashtable<String, Object>();
			cv.put(OpentachFieldNames.FECBAJA_FIELD, new NullValue(Types.DATE));
			eCondCont.update(cv, aux, this.getSessionId(sesionId, eCondCont), con);
		} else {
			// Inserto el conductor asociandolo al contrato que realiza la inserción.
			// COMPRUEBO SI EL CONTRATO QUE ME LLEGA ES FICTICIO
			if (contrato != null) {
				av.put("CG_CONTRATO", contrato);
			} else {
				return new EntityResult(EntityResult.OPERATION_WRONG, EntityResult.OPERATION_WRONG, "ERROR_BUSCANDO_CONTRATO");
			}
			eCondCont.insert(av, this.getSessionId(sesionId, eCondCont), con);
		}
		return resInsert;
	}

	@Override
	public EntityResult delete(Hashtable kv, int sessionId) throws Exception {

		String dni = (String) kv.get(OpentachFieldNames.IDCONDUCTOR_FIELD);
		String cif = (String) kv.get(OpentachFieldNames.CIF_FIELD);
		String name = DBUtils.getDriverName(this.getLocator(), dni, cif, sessionId);
		String companyname = DBUtils.getCompanyName(this.getLocator(), cif, sessionId);
		EntityResult er = super.delete(kv, sessionId);
		if (er.getCode() == EntityResult.OPERATION_SUCCESSFUL) {
			// enviar mail de baja
			if (name != null) {
				kv.put(OpentachFieldNames.NAME_FIELD, name);
			}
			DBUtils.generateMailAuditDriverVeh((OpentachServerLocator) this.locator, Type.BAJA, kv, companyname, sessionId);
		}
		return er;
	}

	@Override
	public EntityResult update(Hashtable av, Hashtable cv, int sesionId) throws Exception {
		String dni = (String) av.get(OpentachFieldNames.DNI_FIELD);
		if (dni != null) {
			av.put(OpentachFieldNames.DNI_FIELD, dni.trim());
		}
		TableEntity ent2 = (TableEntity) this.getEntityReference("EConductorCont");
		EntityResult res = super.update(av, cv, sesionId);
		if ((res != null) && (res.getCode() == EntityResult.OPERATION_SUCCESSFUL)) {
			Vector<Object> v = new Vector<Object>();
			v.add(OpentachFieldNames.CG_CONTRATO_FIELD);

			Hashtable<String, Object> at = new Hashtable<String, Object>();
			at.put(OpentachFieldNames.IDCONDUCTOR_FIELD, cv.get(OpentachFieldNames.IDCONDUCTOR_FIELD));
			at.put(OpentachFieldNames.CIF_FIELD, cv.get(OpentachFieldNames.CIF_FIELD));

			EntityResult res2 = ent2.query(at, v, sesionId);
			if ((res2 != null) && (res2.getCode() == EntityResult.OPERATION_SUCCESSFUL)) {
				Vector<Object> cg_contrato = (Vector<Object>) res2.get(OpentachFieldNames.CG_CONTRATO_FIELD);
				if ((cg_contrato != null) && (cg_contrato.size() > 0)) {
					int i = 0;
					for (i = 0; i < cg_contrato.size(); i++) {
						at.put(OpentachFieldNames.CG_CONTRATO_FIELD, cg_contrato.elementAt(i));
						res2 = ent2.update(av, at, sesionId);
						if ((res2 == null) || (res2.getCode() != EntityResult.OPERATION_SUCCESSFUL)) {
							break;
						}
					}
					if (i == cg_contrato.size()) {
						return res;
					}
				} else {
					return res;
				}
			}
			return res;
		}
		return null;
	}

	@Override
	public EntityResult delete(Hashtable cv, int sessionId, Connection con) throws Exception {
		try {
			TableEntity eCont = (TableEntity) this.getEntityReference("EConductorCont");
			int sessionIDCont = TableEntity.getEntityPrivilegedId(eCont);
			Hashtable<String, Object> av = new Hashtable<String, Object>();
			av.put(OpentachFieldNames.FECBAJA_FIELD, this.getCDateTime(sessionId));
			Vector<Object> v = new Vector<Object>();
			Hashtable<String, Object> aux = new Hashtable<String, Object>();

			aux.put(OpentachFieldNames.IDCONDUCTOR_FIELD, cv.get(OpentachFieldNames.IDCONDUCTOR_FIELD));
			aux.put(OpentachFieldNames.CIF_FIELD, cv.get(OpentachFieldNames.CIF_FIELD));
			EntityResult e = eCont.query(aux, v, sessionId, con);
			if ((e != null) && (e.getCode() == EntityResult.OPERATION_SUCCESSFUL)) {
				if (e.getRecordValues(0).get(OpentachFieldNames.CG_CONTRATO_FIELD) != null) {
					aux.put(OpentachFieldNames.CG_CONTRATO_FIELD, e.getRecordValues(0).get(OpentachFieldNames.CG_CONTRATO_FIELD));
					e = eCont.delete(aux, sessionIDCont, con);
				}
			}
			e = super.delete(cv, sessionId, con);
			return e;
		} catch (Exception e) {
			EConductoresEmp.logger.error(null, e);
			try {
				con.rollback();
			} catch (Exception ex) {
			}
			throw e;
		}
	}

	@Override
	public EntityResult query(Hashtable cv, Vector av, int sesionId, Connection con) throws Exception {

		IUserData userData = ((OpentachServerLocator) this.locator).getUserData(sesionId);
		if ((userData != null) && IUserData.NIVEL_EMPRESA.equals(userData.getLevel())) {
			BasicExpression bs = new BasicExpression(new BasicField("F_BAJA"), BasicOperator.NULL_OP, null);
			cv.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, bs);
		}
		EntityResult res = super.query(cv, av, sesionId, con);
		return res;
	}

	public Map<String, Object> getDriverInfoByIdConductor(Object idConductor, String companyCif, Connection con, String... attributes) throws Exception {
		Hashtable<Object, Object> kv = EntityResultTools.keysvalues("CIF", companyCif, "IDCONDUCTOR", idConductor);
		return this.getDriverInfo(kv, con, attributes);
	}

	private Map<String, Object> getDriverInfo(Hashtable<Object, Object> kv, Connection con, String... attributes) throws Exception {
		Vector<String> av = new Vector<String>(Arrays.asList(attributes));
		EntityResult er = null;
		if (con != null) {
			er = this.query(kv, av, TableEntity.getEntityPrivilegedId(this), con);
		} else {
			er = this.query(kv, av, TableEntity.getEntityPrivilegedId(this));
		}
		if (er.calculateRecordNumber() > 0) {
			return er.getRecordValues(0);
		}
		return new HashMap<String, Object>();
	}
}
