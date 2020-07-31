package com.opentach.server.contract;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.TableEntity;
import com.ontimize.db.TransactionalEntity;
import com.ontimize.jee.common.tools.Template;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.ITGDFileConstants;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.contract.ITachoFileContractService;
import com.opentach.common.tachofiles.ITachoFileRecordService;
import com.opentach.server.companies.ContractService;
import com.opentach.server.tachofiles.TachoFileRecordService;
import com.utilmize.server.services.UAbstractService;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.server.tools.sqltemplate.UpdateJdbcTemplate;
import com.utilmize.tools.exception.UException;

public class TachoFileContractService extends UAbstractService implements ITachoFileContractService {

	private static final Logger logger = LoggerFactory.getLogger(TachoFileContractService.class);

	/**
	 * Instantiates a new tacho file service.
	 *
	 * @param port
	 *            the port
	 * @param erl
	 *            the erl
	 * @param hconfig
	 *            the hconfig
	 * @throws Exception
	 *             the exception
	 */
	public TachoFileContractService(int port, EntityReferenceLocator erl, Hashtable hconfig) throws Exception {
		super(port, erl, hconfig);
	}

	/*
	 * (non-Javadoc)
	 * @see com.opentach.interfaces.IContractAssignment#tryToAssignContract(java.util.List, int)
	 */
	@Override
	public void tryToAssignContract(final List<Number> lFiles, final int sessionID) throws Exception {
		try {
			if ((lFiles == null) || (lFiles.size() == 0)) {
				return;
			}
			new OntimizeConnectionTemplate<Void>() {
				@Override
				protected Void doTask(Connection conn) throws UException {
					try {
						TachoFileContractService.this.tryToAssignContract(null, lFiles, sessionID, conn);
						return null;
					} catch (final Exception err) {
						throw new UException(err);
					}
				}
			}.execute(this.getConnectionManager(), false);
		} catch (final Exception error) {
			TachoFileContractService.logger.error(null, error);
			throw error;
		}
	}

	/**
	 * Try to assign contract with a connection.
	 *
	 * @param lFiles
	 *            the l files
	 * @param sessionID
	 *            the session id
	 * @param conn
	 *            the conn
	 * @throws Exception
	 *             the exception
	 */
	private void tryToAssignContract(final List<String> companyCifs, final List<Number> lFiles, final int sessionID, Connection conn) throws Exception {
		final int length = lFiles.size();
		for (int i = 0; i < length; i++) {
			final Number idFile = lFiles.get(i);
			if (idFile == null) {
				continue;
			}
			final String[] fileInfo = this.getIdFileInfo(idFile, conn);
			if (fileInfo == null) {
				continue;
			}
			final String idOrigen = fileInfo[0];
			final String tipo = fileInfo[1];
			final String nomb = fileInfo[2];
			final List<String> contracts = this.getContractToAssign(companyCifs, idOrigen, tipo, conn);
			for (final String cgcontract : contracts) {
				if ((cgcontract != null) && !this.isFileAssignedToContract(idFile, cgcontract, conn)) {
					this.assignFileToContract(idFile, cgcontract, conn, sessionID);
					this.getService(TachoFileRecordService.class).saveFileRecord(idFile, cgcontract, ITachoFileRecordService.UPLOAD, nomb, ContractService.sOBSRCONTRACT, null,
							sessionID, conn);
					TachoFileContractService.logger.debug("Asignado fichero {}  a contrato {} con éxito!", idFile, cgcontract);
				}
			}
		}
	}

	@Override
	public void reassignFileToContracts(final Number idFichero, final List<String> vContratos, final List<Number> lInsertar, final int sessionID) throws Exception {
		try {
			new OntimizeConnectionTemplate<Void>() {

				@Override
				protected Void doTask(Connection conn) throws UException {
					try {
						TachoFileContractService.this.reassignFileToContracts(idFichero, vContratos, lInsertar, sessionID, conn);
						return null;
					} catch (final Exception err) {
						throw new UException(err);
					}
				}
			}.execute(this.getConnectionManager(), false);
		} catch (final Exception error) {
			TachoFileContractService.logger.error(null, error);
			throw error;
		}
	}

	private void reassignFileToContracts(final Number idFichero, final List<String> vContratos, final List<Number> lInsertar, final int sessionID, Connection conn)
			throws Exception {
		final TableEntity eFileContract = (TableEntity) this.getEntity(ITGDFileConstants.FILECONTRACT_ENTITY);
		final TableEntity eFileRecord = (TableEntity) this.getEntity(ITGDFileConstants.FILERECORD_ENTITY);
		final int privSessionIDC = TableEntity.getEntityPrivilegedId(eFileContract);
		final int privSessionIDR = TableEntity.getEntityPrivilegedId(eFileRecord);
		Number item = null;
		String cgContrato = null;
		final Hashtable<String, Object> av = new Hashtable<String, Object>(2);
		String[] fileData = null;
		for (int i = 0; i < lInsertar.size(); i++) {
			item = lInsertar.get(i);
			cgContrato = vContratos.get(i);
			// insert
			if (item.intValue() == 1) {
				av.put(OpentachFieldNames.IDFILE_FIELD, idFichero);
				av.put(OpentachFieldNames.CG_CONTRATO_FIELD, cgContrato);
				try {
					eFileContract.insert(av, privSessionIDC, conn);
					fileData = this.getIdFileInfo(idFichero, conn);
					if (fileData != null) {
						final String nombProcesado = fileData[2];
						this.getService(TachoFileRecordService.class).saveFileRecord(idFichero, cgContrato, ITachoFileRecordService.UPLOAD, nombProcesado,
								ContractService.sOBSRCONTRACT, null, privSessionIDR, conn);
					}
				} catch (final Exception err) {
					TachoFileContractService.logger.error(null, err);
				}
			} else {
				// delete
				new UpdateJdbcTemplate().execute(conn, "DELETE FROM CDFICHEROS_CONTRATO WHERE IDFICHERO = " + idFichero.intValue() + " AND CG_CONTRATO = '" + cgContrato + "'");
			}
		}
	}

	/**
	 * Devuelve el idorigen, el tipo (VU/TC) y el nombre procesado del fichero.
	 *
	 * @param idFile
	 *            the id file
	 * @param conn
	 *            the conn
	 * @return the source type
	 * @throws Exception
	 *             the exception
	 */
	private String[] getIdFileInfo(Number idFile, Connection conn) throws Exception {
		final String sql = new Template("tachofiletransfer-sql/getIdFileInfo.sql").getTemplate();

		return new QueryJdbcTemplate<String[]>() {
			@Override
			protected String[] parseResponse(ResultSet rset) throws UException {
				try {
					if (rset.next()) {
						return new String[] { rset.getString(1), rset.getString(2), rset.getString(3) };
					}
					return null;
				} catch (final Exception err) {
					throw new UException(err);
				}
			}
		}.execute(conn, sql, idFile.intValue());
	}

	/**
	 * Assign file to contract.
	 *
	 * @param idFile
	 *            the id file
	 * @param cgContract
	 *            the cg contract
	 * @param conn
	 *            the conn
	 * @param sesionId
	 *            the sesion id
	 * @throws Exception
	 *             the exception
	 */
	public void assignFileToContract(Number idFile, Object cgContract, Connection conn, int sesionId) throws Exception {
		final Hashtable<String, Object> av = new Hashtable<String, Object>();
		av.put(OpentachFieldNames.IDFILE_FIELD, idFile);
		av.put(OpentachFieldNames.CG_CONTRATO_FIELD, cgContract);
		final TransactionalEntity ent = this.getEntity(ITGDFileConstants.FILECONTRACT_ENTITY);
		ent.insert(av, this.getSessionId(sesionId, ent), conn);
		TachoFileContractService.logger.debug("CONTRATO {} ASIGNADO a fichero {}", cgContract, idFile);
	}

	/**
	 * Gets the contract to assign the idorigin.
	 *
	 * @param idOrigen
	 *            the id origen
	 * @param tipo
	 *            the tipo
	 * @param conn
	 *            the conn
	 * @return the contract to assign
	 * @throws Exception
	 *             the exception
	 */
	public List<String> getContractToAssign(List<String> companyCifs, String idOrigen, String tipo, Connection conn) throws Exception {
		String sql = null;
		if (tipo.equals("TC")) {
			sql = new Template("tachofiletransfer-sql/getContractToAsignTC.sql").getTemplate();
		} else if (tipo.equals("VU")) {
			sql = new Template("tachofiletransfer-sql/getContractToAsignVU.sql").getTemplate();
		} else if (tipo.equals("DA")) {
			return new ArrayList<String>();
		} else {
			throw new Exception("M_TIPO_FICHERO_NO_VALIDO");
		}
		return new QueryJdbcTemplate<List<String>>() {

			@Override
			protected List<String> parseResponse(ResultSet rset) throws UException {
				try {
					final List<String> rtn = new ArrayList<String>();
					while (rset.next()) {
						final String numreq = rset.getString(1);
						final String cif = rset.getString(2);
						if ((companyCifs == null) || companyCifs.isEmpty() || companyCifs.contains(cif)) {
							rtn.add(numreq);
						}
					}
					return rtn;
				} catch (final Exception err) {
					throw new UException(err);
				}
			}
		}.execute(conn, sql, idOrigen);
	}

	/**
	 * Checks if is file assigned to contract.
	 *
	 * @param idFile
	 *            the id file
	 * @param cgContrato
	 *            the cg contrato
	 * @param conn
	 *            the conn
	 * @return true, if is file assigned to contract
	 * @throws Exception
	 *             the exception
	 */
	public boolean isFileAssignedToContract(Number idFile, Object cgContrato, Connection conn) throws Exception {
		final String sql = new Template("tachofiletransfer-sql/isFileAssignedToContract.sql").getTemplate();
		return new QueryJdbcTemplate<Boolean>() {
			@Override
			protected Boolean parseResponse(ResultSet rset) throws UException {
				try {
					if (rset.next()) {
						return rset.getInt(1) > 0;
					}
					return false;
				} catch (final Exception err) {
					throw new UException(err);
				}
			}
		}.execute(conn, sql, cgContrato, idFile.intValue());
	}

	/**
	 * Checks if source is assigned to contract.
	 *
	 * @param idSource
	 *            the id source
	 * @param cgContrato
	 *            the cg contrato
	 * @param tipo
	 *            the tipo
	 * @param conn
	 *            the conn
	 * @return true, if is source assigned to contract
	 * @throws Exception
	 *             the exception
	 */
	public boolean isSourceAssignedToContract(Object idSource, Object cgContrato, String tipo, Connection conn) throws Exception {
		String sql = null;
		if (tipo.equals("TC")) {
			sql = new Template("tachofiletransfer-sql/isSourceAssignedToContractTC.sql").getTemplate();
		} else if (tipo.equals("VU")) {
			sql = new Template("tachofiletransfer-sql/isSourceAssignedToContractVU.sql").getTemplate();
		} else if ("DA".equals(tipo)) {
			return true;
		} else {
			throw new Exception("M_TIPO_FICHERO_NO_VALIDO");
		}

		return new QueryJdbcTemplate<Boolean>() {
			@Override
			protected Boolean parseResponse(ResultSet rset) throws UException {
				try {
					if (rset.next()) {
						return rset.getInt(1) > 0;
					}
					return false;
				} catch (final Exception err) {
					throw new UException(err);
				}
			}
		}.execute(conn, sql, idSource, cgContrato);

	}
}
