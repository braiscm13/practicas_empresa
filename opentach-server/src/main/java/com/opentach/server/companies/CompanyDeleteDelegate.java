package com.opentach.server.companies;

import java.sql.Connection;
import java.util.List;

import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.tools.Pair;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.util.AbstractDelegate;
import com.utilmize.server.tools.sqltemplate.QueryJdbcToListOfPairTemplate;
import com.utilmize.server.tools.sqltemplate.UpdateJdbcTemplate;

public class CompanyDeleteDelegate extends AbstractDelegate {

	public CompanyDeleteDelegate(IOpentachServerLocator locator) {
		super(locator);
	}

	public EntityResult deleteCompany(String cif, int sessionId, Connection conn) throws Exception {
		this.deleteTachoInfo(cif, sessionId, conn);
		this.deleteSmartphones(cif, sessionId, conn);
		this.deleteFiles(cif, sessionId, conn);
		this.deleteTasks(cif, sessionId, conn);
		this.deleteDrivers(cif, sessionId, conn);
		this.deleteVehicles(cif, sessionId, conn);
		this.deleteContracts(cif, sessionId, conn);

		new UpdateJdbcTemplate().execute(conn, "DELETE FROM CDFIRMANTES_EMPRESA WHERE CIF= ?", cif);
		new UpdateJdbcTemplate().execute(conn, "DELETE FROM CDNOTIF_EMP WHERE CIF= ?", cif);
		new UpdateJdbcTemplate().execute(conn, "DELETE FROM CDREPORTCONFIG_DFEMP WHERE CIF= ?", cif);
		new UpdateJdbcTemplate().execute(conn, "DELETE FROM CDDELEGACION_DFEMP WHERE CIF= ?", cif);
		new UpdateJdbcTemplate().execute(conn, "DELETE FROM CDUSU_DFEMP WHERE CIF= ?", cif);
		new UpdateJdbcTemplate().execute(conn, "DELETE FROM DFEMP_CONTACT WHERE CIF= ?", cif);
		new UpdateJdbcTemplate().execute(conn, "DELETE FROM DFEMP WHERE CIF= ?", cif);
		return new EntityResult();
	}

	private void deleteContracts(String cif, int sessionId, Connection conn) throws Exception {
		// AQUI NO USAMOS CDVEMPRE_REQ_REALES
		new UpdateJdbcTemplate().execute(conn, "DELETE FROM CDCERTIF_ACTIVIDADES WHERE NUMREQ IN (SELECT NUMREQ FROM CDEMPRE_REQ WHERE CIF= ?)", cif);
		new UpdateJdbcTemplate().execute(conn, "DELETE FROM CDINFORMEGESTOR WHERE CG_CONTRATO IN (SELECT NUMREQ FROM CDEMPRE_REQ WHERE CIF= ?)", cif);
		new UpdateJdbcTemplate().execute(conn, "DELETE FROM CDEMPRE_REQ WHERE CIF= ?", cif);
	}

	private void deleteVehicles(String cif, int sessionId, Connection conn) throws Exception {
		List<Pair<Object, Object>> vehicleIds = new QueryJdbcToListOfPairTemplate()
				.execute(
						conn,
						"SELECT MATRICULA,CG_CONTRATO FROM CDVEHICULO_CONT WHERE CG_CONTRATO IN (SELECT NUMREQ FROM CDVEMPRE_REQ_REALES WHERE CIF = ?) AND MATRICULA IN (SELECT MATRICULA FROM CDVEHICULOS_EMP WHERE CIF = ?)",
						cif, cif);
		for (Pair<Object, Object> pair : vehicleIds) {
			new UpdateJdbcTemplate().execute(conn, "DELETE FROM CDVEHICULO_CONT WHERE MATRICULA = ? AND CG_CONTRATO= ?", pair.getFirst(), pair.getSecond());
		}
		new UpdateJdbcTemplate().execute(conn, "DELETE FROM CDVEHICULOS_EMP WHERE CIF= ?", cif);

	}

	private void deleteDrivers(String cif, int sessionId, Connection conn) throws Exception {
		List<Pair<Object, Object>> driverIds = new QueryJdbcToListOfPairTemplate()
				.execute(
						conn,
						"SELECT IDCONDUCTOR,CG_CONTRATO FROM CDCONDUCTOR_CONT WHERE CG_CONTRATO IN (SELECT NUMREQ FROM CDVEMPRE_REQ_REALES WHERE CIF = ?) AND IDCONDUCTOR IN (SELECT IDCONDUCTOR FROM CDCONDUCTORES_EMP WHERE CIF = ?)",
						cif, cif);
		for (Pair<Object, Object> pair : driverIds) {
			new UpdateJdbcTemplate().execute(conn, "DELETE FROM CDCONDUCTOR_CONT WHERE IDCONDUCTOR = ? AND CG_CONTRATO= ?", pair.getFirst(), pair.getSecond());
		}
		new UpdateJdbcTemplate().execute(conn, "DELETE FROM CDCONDUCTORES_EMP WHERE CIF= ?", cif);
	}

	private void deleteTasks(String cif, int sessionId, Connection conn) throws Exception {
		new UpdateJdbcTemplate().execute(conn, "UPDATE TSK_TASK SET ECN_ID = NULL, CIF = NULL WHERE CIF = ?", cif);
	}

	private void deleteFiles(String cif, int sessionId, Connection conn) {
		// TODO tal vez sea mejor que se borren solos con el tiempo
	}

	private void deleteSmartphones(String cif, int sessionId, Connection conn) throws Exception {
		new UpdateJdbcTemplate().execute(conn, "DELETE FROM CDCLOUDFILE WHERE IDBLACKBERRY IN (SELECT IDBLACKBERRY FROM CDBLACKBERRY WHERE CIF = ?)", cif);
//		new UpdateJdbcTemplate().execute(conn, "UPDATE CDBLACKBERRY SET IDLASTPOINT  = NULL WHERE CIF = ?", cif);
		new UpdateJdbcTemplate().execute(conn, "DELETE FROM CDBLACKBERRY WHERE CIF = ?", cif);
	}

	private void deleteTachoInfo(String cif, int sessionId, Connection conn) throws Exception {
		List<Pair<Object, Object>> driverIds = new QueryJdbcToListOfPairTemplate()
				.execute(
						conn,
						"SELECT IDCONDUCTOR,CG_CONTRATO FROM CDCONDUCTOR_CONT WHERE CG_CONTRATO IN (SELECT NUMREQ FROM CDVEMPRE_REQ_REALES WHERE CIF = ?) AND IDCONDUCTOR IN (SELECT IDCONDUCTOR FROM CDCONDUCTORES_EMP WHERE CIF = ?)",
						cif, cif);
		List<Pair<Object, Object>> vehiclesIds = new QueryJdbcToListOfPairTemplate()
				.execute(
						conn,
						"SELECT MATRICULA,CG_CONTRATO FROM CDVEHICULO_CONT WHERE CG_CONTRATO IN (SELECT NUMREQ FROM CDVEMPRE_REQ_REALES WHERE CIF = ?) AND MATRICULA IN (SELECT MATRICULA FROM CDVEHICULOS_EMP WHERE CIF = ?)",
						cif, cif);

		for (Pair<Object, Object> pair : driverIds) {
			String[] driverTables = new String[] {//
					"CDACTIVIDADES",//
					"CDREGKM_CONDUCTOR",//
					"CDUSO_TARJETA",//
					"CDPERIODOS_TRABAJO"//
			};
			for (String table : driverTables) {
				new UpdateJdbcTemplate().execute(conn, "DELETE FROM " + table + " WHERE IDCONDUCTOR = ? AND NUMREQ = ?", pair.getFirst(), pair.getSecond());
			}
		}

		for (Pair<Object, Object> pair : vehiclesIds) {
			String[] vehicleTables = new String[] {//
					"CDINCIDENTES",//
					"CDREGKM_VEHICULO",//
					"CDVELOCIDAD",//
					"CDCONTROLES",//
					"CDFALLOS",//
					"CDCALIBRADO",//
					"CDUSO_VEHICULO"//
			};
			for (String table : vehicleTables) {
				new UpdateJdbcTemplate().execute(conn, "DELETE FROM " + table + " WHERE MATRICULA = ? AND NUMREQ = ?", pair.getFirst(), pair.getSecond());
			}
		}

		// TODO estos datos no estan asociados a contrato por lo que por ahora no se eliminan
		// for (Pair<Object,Object> pair: vehiclesIds) {
		// String[] vehicleTables = new String[]{//
		// "CDBLOQUEOS_EMPRESA",//
		// "CDDATOS_TECNICOS",//
		// "CDDATOS_TECNICOS_SENSOR",//
		// "CDDATOS_TECNICOS_CAL"//
		// };
		// for (String table: vehicleTables) {
		// new UpdateJdbcTemplate().execute(conn, "DELETE FROM "+table+" WHERE MATRICULA = ?", pair.getFirst());
		// }
		// }

	}
}
