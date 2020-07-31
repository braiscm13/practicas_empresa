package com.opentach.server.entities;

import java.sql.Connection;
import java.sql.Types;
import java.util.Date;
import java.util.Hashtable;
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
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.user.IUserData;
import com.opentach.server.OpentachServerLocator;
import com.opentach.server.companies.ContractService;
import com.opentach.server.companies.VehicleService;
import com.opentach.server.util.ContractUtils;
import com.opentach.server.util.DBUtils;
import com.opentach.server.util.db.FileTableEntity;
import com.opentach.server.util.mail.AltaBajaAutoMailComposer.Type;

public class EVehiculosEmp extends FileTableEntity {

	private static final Logger	logger		= LoggerFactory.getLogger(EVehiculosEmp.class);
	public static final String	FICHERO_VU	= "FICHERO_VU";
	public static final String	FICHERO_TD	= "FICHERO_TD";
	public static final String	FICHERO_DA	= "FICHERO_DA";

	public EVehiculosEmp(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	public EVehiculosEmp(EntityReferenceLocator locator, DatabaseConnectionManager dbConnectionManager, int port, Properties prop, Properties aliasProp) throws Exception {
		super(locator, dbConnectionManager, port, prop, aliasProp);
	}

	@Override
	public EntityResult query(Hashtable cv, Vector av, int sesionId, Connection con) throws Exception {
		// BasicExpression bs = new BasicExpression(new BasicField("F_BAJA"), BasicOperator.NOT_NULL_OP, null);
		// cv.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, bs);

		IUserData userData = ((OpentachServerLocator) this.locator).getUserData(sesionId);
		if ((userData != null) && IUserData.NIVEL_EMPRESA.equals(userData.getLevel())) {
			BasicExpression bs = new BasicExpression(new BasicField("F_BAJA"), BasicOperator.NULL_OP, null);
			cv.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, bs);
		}
		EntityResult res = super.query(cv, av, sesionId, con);
		return res;
	}

	@Override
	public EntityResult insert(Hashtable av, int sessionId) throws Exception {

		EntityResult er = super.insert(av, sessionId);
		if (er.getCode() == EntityResult.OPERATION_SUCCESSFUL) {
			// enviar mail de alta
			String cif = (String) av.get(OpentachFieldNames.CIF_FIELD);
			String companyname = DBUtils.getCompanyName(this.getLocator(), cif, sessionId);
			DBUtils.generateMailAuditDriverVeh((OpentachServerLocator) this.locator, Type.ALTA, av, companyname, sessionId);
		}
		return er;
	}

	@Override
	public EntityResult delete(Hashtable kv, int sessionId) throws Exception {

		String cif = (String) kv.get(OpentachFieldNames.CIF_FIELD);
		String companyname = DBUtils.getCompanyName(this.getLocator(), cif, sessionId);

		EntityResult er = super.delete(kv, sessionId);
		if (er.getCode() == EntityResult.OPERATION_SUCCESSFUL) {
			// enviar mail de baja
			DBUtils.generateMailAuditDriverVeh((OpentachServerLocator) this.locator, Type.BAJA, kv, companyname, sessionId);
		}
		return er;
	}

	@Override
	public EntityResult update(Hashtable av, Hashtable cv, int sesionId) throws Exception {
		String matricula = (String) av.get(OpentachFieldNames.MATRICULA_FIELD);
		if (matricula != null) {
			av.put(OpentachFieldNames.MATRICULA_FIELD, matricula.trim());
		}
		TableEntity ent2 = (TableEntity) this.getEntityReference("EVehiculoCont");
		EntityResult res = super.update(av, cv, sesionId);
		if ((res != null) && (res.getCode() == EntityResult.OPERATION_SUCCESSFUL)) {
			Vector<Object> v = new Vector<Object>();
			v.add(OpentachFieldNames.CG_CONTRATO_FIELD);
			Hashtable<String, Object> at = new Hashtable<String, Object>();
			at.put(OpentachFieldNames.MATRICULA_FIELD, cv.get(OpentachFieldNames.MATRICULA_FIELD));
			at.put(OpentachFieldNames.CIF_FIELD, cv.get(OpentachFieldNames.CIF_FIELD));
			EntityResult res2 = ent2.query(at, v, sesionId);
			if ((res2 != null) && (res2.getCode() == EntityResult.OPERATION_SUCCESSFUL)) {
				Vector<Object> cg_contrato = (Vector<Object>) res2.get(OpentachFieldNames.CG_CONTRATO_FIELD);
				if ((cg_contrato != null) && (cg_contrato.size() > 0)) {
					int i = 0;
					for (i = 0; i < cg_contrato.size(); i++) {
						at.put(OpentachFieldNames.CG_CONTRATO_FIELD, cg_contrato.elementAt(i));
						res2 = ent2.update(av, at, sesionId);
						if ((res2 == null) || (res2.getCode() != EntityResult.OPERATION_SUCCESSFUL && res2.getCode() != EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE)) {
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
		}

		return null;
	}

	@Override
	public EntityResult insert(Hashtable av, int sesionId, Connection con) throws Exception {

		if (!this.getService(VehicleService.class).hasNewVehicleSpace((String) av.get("CIF"))) {
			throw new Exception("NUM_MAX_VEHICLES_SUPERATED");
		}

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

		String matricula = (String) av.get(OpentachFieldNames.MATRICULA_FIELD);
		if (matricula != null) {
			matricula = matricula.trim();
			av.put(OpentachFieldNames.MATRICULA_FIELD, matricula);
		}
		if (av.containsKey("F_ALTA")) {
			av.put("F_ALTA", new Date());
		}
		EntityResult resInsert = super.insert(av, sesionId, con);
		if (cgContrato == null) {
			// Si no se puede asignar a ningún contrato
			resInsert.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
			resInsert.setMessage("Se ha dado de alta el vehículo en la empresa.\n Pero no ha sido asociado a ningún contrato");
			return resInsert;
		}
		// Si se puede asignar a algún contrato o ya está asignado

		Vector<String> v = new Vector<String>();
		Hashtable<String, Object> aux = new Hashtable<String, Object>();
		aux.put(OpentachFieldNames.MATRICULA_FIELD, matricula);
		aux.put(OpentachFieldNames.CIF_FIELD, companyCif);
		if (av.get("IDVEHICLETYPE") != null) {
			aux.put("TIPOMATRICULA", av.get("IDVEHICLETYPE"));
		}
		aux.put(OpentachFieldNames.CG_CONTRATO_FIELD, ContractUtils.checkContratoFicticio(this.getLocator(), cgContrato, sesionId, con));
		TableEntity eVehCont = (TableEntity) this.getEntityReference("EVehiculoCont");
		EntityResult resVehCont = eVehCont.query(aux, v, this.getSessionId(sesionId, eVehCont), con);

		if (resVehCont.calculateRecordNumber() > 0) {
			// if this vehicle had already been added to this contract then insert update down date in contract
			if (resVehCont.getRecordValues(0).get("F_BAJA") == null) {
				// no está dado de baja
				return new EntityResult(EntityResult.OPERATION_WRONG, EntityResult.OPERATION_WRONG, "VEHICULO_YA_EXISTE");
			}

			// Confirmamos que el vehículo no está dado de baja en el contrato vigente
			Hashtable<String, Object> cv = new Hashtable<String, Object>();
			cv.put(OpentachFieldNames.FECBAJA_FIELD, new NullValue(Types.DATE));
			eVehCont.update(cv, aux, this.getSessionId(sesionId, eVehCont), con);
		} else {
			// Inserto el vehículo asociandolo al contrato que realiza la inserción.
			eVehCont.insert(aux, this.getSessionId(sesionId, eVehCont), con);
		}
		return resInsert;
	}

	@Override
	public EntityResult delete(Hashtable cv, int sessionId, Connection con) throws Exception {
		try {
			TableEntity eCont = (TableEntity) this.getEntityReference("EVehiculoCont");
			int sessionIDCont = TableEntity.getEntityPrivilegedId(eCont);
			Hashtable<String, Object> av = new Hashtable<String, Object>();
			av.put(OpentachFieldNames.FECBAJA_FIELD, this.getCDateTime(sessionId));
			Vector<Object> v = new Vector<Object>();
			Hashtable<String, Object> aux = new Hashtable<String, Object>();
			aux.put(OpentachFieldNames.MATRICULA_FIELD, cv.get(OpentachFieldNames.MATRICULA_FIELD));
			aux.put(OpentachFieldNames.CIF_FIELD, cv.get(OpentachFieldNames.CIF_FIELD));
			EntityResult e = eCont.query(aux, v, sessionId, con);
			if ((e != null) && (e.getCode() == EntityResult.OPERATION_SUCCESSFUL)) {
				if (e.getRecordValues(0).get(OpentachFieldNames.CG_CONTRATO_FIELD) != null) {
					aux.put(OpentachFieldNames.CG_CONTRATO_FIELD, e.getRecordValues(0).get(OpentachFieldNames.CG_CONTRATO_FIELD));
					e = eCont.delete(aux, sessionIDCont, con);
				}
			}
			e = super.delete(aux, sessionId, con);
			return e;
		} catch (Exception e) {
			EVehiculosEmp.logger.error(null, e);
			try {
				con.rollback();
			} catch (Exception ex) {
			}
			throw e;
		}

	}
}
