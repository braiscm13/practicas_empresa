package com.opentach.server.companies;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.db.TransactionalEntity;
import com.ontimize.gui.SearchValue;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.common.tools.Pair;
import com.ontimize.jee.common.tools.Template;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.companies.IContractService;
import com.opentach.common.user.Company;
import com.opentach.common.user.IUserData;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.entities.EPreferenciasServidor;
import com.opentach.server.util.ContractUtils;
import com.utilmize.server.services.UAbstractService;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.server.tools.sqltemplate.UpdateJdbcTemplate;
import com.utilmize.tools.exception.UException;

public class ContractService extends UAbstractService implements IContractService {

	private static final Logger	logger										= LoggerFactory.getLogger(ContractService.class);

	private static final String	ERROR_PASSWORD_IS_MANDATORY					= "PASSWORD_IS_MANDATORY";
	private static final String	ERROR_USER_IS_MANDATORY						= "USER_IS_MANDATORY";
	private static final String	ERROR_INVALID_USER_PASSWORD_OR_NOT_COMPANY	= "INVALID_USER_PASSWORD_OR_NOT_COMPANY";
	private static final String	ERROR_COMPANY_NOT_FOUND_FOR_USER			= "COMPANY_NOT_FOUND_FOR_USER";
	public static final String	sOBSRCONTRACT								= "Fichero asociado a contrato";

	public ContractService(int port, EntityReferenceLocator erl, Hashtable hconfig) throws Exception {
		super(port, erl, hconfig);
	}

	@Override
	public void limpiarDatos(final String cgContrato, final int sessionID) throws Exception {
		try {
			new OntimizeConnectionTemplate<Void>() {
				@Override
				protected Void doTask(Connection con) throws UException {
					try {
						ContractService.this.limpiarDatos(cgContrato, sessionID, con);
						return null;
					} catch (final Exception err) {
						throw new UException(err);
					}
				}
			}.execute(this.getConnectionManager(), true);
		} catch (final Exception error) {
			ContractService.logger.error(null, error);
			throw error;
		}
	}

	private void limpiarDatos(final String cgContrato, int sessionID, Connection con) throws Exception {
		final String[] tablas = { "CDCALIBRADO", "CDFALLOS", "CDUSO_VEHICULO", "CDCONTROLES", "CDACTIVIDADES", "CDPERIODOS_TRABAJO", "CDINCIDENTES", "CDUSO_TARJETA", "CDREGKM_CONDUCTOR", "CDREGKM_VEHICULO", "CDVELOCIDAD", "CDCERTIF_ACTIVIDADES" };
		for (final String tabla : tablas) {
			final String sql = "DELETE FROM " + tabla + " WHERE NUMREQ ='" + cgContrato + "'";
			new UpdateJdbcTemplate().execute(con, sql);
		}
	}

	@Override
	public void reprocesarContrato(final String cgContrato, final int sessionID) throws Exception {
		try {
			new OntimizeConnectionTemplate<Void>() {
				@Override
				protected Void doTask(Connection con) throws UException {
					try {
						ContractService.this.reprocesarContrato(cgContrato, sessionID, con);
						return null;
					} catch (final Exception err) {
						throw new UException(err);
					}
				}
			}.execute(this.getConnectionManager(), false);
		} catch (final Exception error) {
			ContractService.logger.error(null, error);
			throw error;
		}
	}

	private void reprocesarContrato(final String cgContrato, int sessionID, Connection con) throws Exception {
		final String[] tablas = { "CDCALIBRADO", "CDFALLOS", "CDUSO_VEHICULO", "CDCONTROLES", "CDACTIVIDADES", "CDPERIODOS_TRABAJO", "CDINCIDENTES", "CDUSO_TARJETA", "CDREGKM_CONDUCTOR", "CDREGKM_VEHICULO", "CDVELOCIDAD" };
		for (final String tabla : tablas) {
			final String sqlDelete = "DELETE FROM " + tabla + " WHERE NUMREQ ='" + cgContrato + "'";
			new UpdateJdbcTemplate().execute(con, sqlDelete);
		}
		final String sqlUpdateFProcesado = "UPDATE CDFICHEROS_CONTRATO SET F_PROCESADO = NULL WHERE CG_CONTRATO ='" + cgContrato + "'";
		new UpdateJdbcTemplate().execute(con, sqlUpdateFProcesado);
		final String sqlUpdateObsr = "UPDATE CDFICHEROS SET CDFICHEROS.OBSR = 'Fichero sin procesar' WHERE 1 <= (SELECT COUNT(*) FROM CDFICHEROS_CONTRATO WHERE CDFICHEROS_CONTRATO.CG_CONTRATO='" + cgContrato + "' AND CDFICHEROS_CONTRATO.IDFICHERO = CDFICHEROS.IDFICHERO)";
		new UpdateJdbcTemplate().execute(con, sqlUpdateObsr);
	}

	// FIXME: Probar esto
	@Override
	public void limpiarContrato(final String cgContrato, final int sessionID) throws Exception {
		try {
			new OntimizeConnectionTemplate<Void>() {

				@Override
				protected Void doTask(Connection con) throws UException {
					try {
						ContractService.this.limpiarContrato(cgContrato, sessionID, con);
						return null;
					} catch (final Exception err) {
						throw new UException(err);
					}
				}

			}.execute(this.getConnectionManager(), false);
		} catch (final Exception error) {
			ContractService.logger.error(null, error);
			throw error;
		}
	}

	private void limpiarContrato(final String cgContrato, int sessionID, Connection con) throws Exception {
		ContractService.this.limpiarDatos(cgContrato, sessionID, con);
		final String sqlDelete = "DELETE FROM CDFICHEROS_CONTRATO WHERE CG_CONTRATO ='" + cgContrato + "'";
		new UpdateJdbcTemplate().execute(con, sqlDelete);
	}

	@Override
	public void eliminarContrato(final String cgContrato, final int sessionID) throws Exception {
		try {
			new OntimizeConnectionTemplate<Void>() {

				@Override
				protected Void doTask(Connection con) throws UException {
					try {
						ContractService.this.eliminarContrato(cgContrato, sessionID, con);
					} catch (final Exception err) {
						throw new UException(err);
					}
					return null;

				}

			}.execute(this.getConnectionManager(), false);
		} catch (final Exception error) {
			ContractService.logger.error(null, error);
			throw error;
		}
	}

	private void eliminarContrato(String cgContrato, int sessionID, Connection con) throws Exception {

		String idInspeccion = null;
		// busco el IDINSPECCION de este contrato
		final Vector<Object> vq = new Vector<Object>(1);
		vq.add(OpentachFieldNames.IDINSPECCION_FIELD);
		final Hashtable<String, Object> htq = new Hashtable<String, Object>();
		htq.put(OpentachFieldNames.NUMREQ_FIELD, cgContrato);
		final TransactionalEntity eEmpreReq = this.getEntity("EEmpreReq");
		final EntityResult er = eEmpreReq.query(htq, vq, this.getSessionId(sessionID, eEmpreReq), con);
		if (er.calculateRecordNumber() == 1) {
			final Vector<Object> vres = (Vector<Object>) er.get(OpentachFieldNames.IDINSPECCION_FIELD);
			idInspeccion = (String) vres.firstElement();
		} else {
			throw new Exception();
		}

		final String[] tables = { "CDCALIBRADO", "CDFALLOS", "CDUSO_VEHICULO", "CDCONTROLES", "CDACTIVIDADES", "CDPERIODOS_TRABAJO", "CDUSO_TARJETA", "CDREGKM_CONDUCTOR", "CDREGKM_VEHICULO", "CDVELOCIDAD", "CDCERTIF_ACTIVIDADES" };
		for (final String table : tables) {
			final String sql = "DELETE FROM " + table + " WHERE NUMREQ ='" + cgContrato + "'";
			new UpdateJdbcTemplate().execute(con, sql);
		}
		new UpdateJdbcTemplate().execute(con, "DELETE FROM CDFICHEROS_CONTRATO WHERE CG_CONTRATO ='" + cgContrato + "'");
		new UpdateJdbcTemplate().execute(con, "DELETE FROM CDINCIDENTES 				WHERE NUMREQ = '" + cgContrato + "'");
		new UpdateJdbcTemplate().execute(con, "DELETE FROM CDVEHICULO_CONT 		WHERE CG_CONTRATO ='" + cgContrato + "'");
		new UpdateJdbcTemplate().execute(con, "DELETE FROM CDCONDUCTOR_CONT  	WHERE CG_CONTRATO ='" + cgContrato + "'");
		new UpdateJdbcTemplate().execute(con, "DELETE FROM CDEMPRE_REQ  				WHERE CG_CONTRATO ='" + cgContrato + "'");

	}

	@Override
	public String getContratoVigente(final String cif, final int sessionID) throws Exception {
		try {
			if (cif == null) {
				return null;
			}
			return new OntimizeConnectionTemplate<String>() {
				@Override
				protected String doTask(Connection con) throws UException {
					try {
						return ContractService.this.getContratoVigente(cif, sessionID, con);
					} catch (final Exception err) {
						throw new UException(err);
					}
				}
			}.execute(this.getConnectionManager(), true);
		} catch (final Exception error) {
			ContractService.logger.error(null, error);
			throw error;
		}
	}

	public String getContratoVigente(String cif, int sessionID, Connection conn) throws Exception {
		if (cif == null) {
			return null;
		}
		final String sql = "SELECT NUMREQ FROM CDVEMPRE_REQ_REALES WHERE F_BAJA IS NULL AND CIF = ?";
		return new QueryJdbcTemplate<String>() {
			@Override
			protected String parseResponse(ResultSet rset) throws UException {
				try {
					if (rset.next()) {
						return rset.getString(1);
					}
					return null;
				} catch (final Exception err) {
					throw new UException(err);
				}
			}
		}.execute(conn, sql, cif);
	}

	public List<String[]> getEmpresasUsuario(String usuario, int sessionID, Connection conn) throws Exception {
		final String sql = new Template("sql/companies/ContractServiceUserCompanies.sql").getTemplate();

		return new QueryJdbcTemplate<List<String[]>>() {
			@Override
			protected List<String[]> parseResponse(ResultSet rset) throws UException {
				try {
					final List<String[]> lEmpresas = new Vector<String[]>();
					while (rset.next()) {
						lEmpresas.add(new String[] { rset.getString(1), rset.getString(2) });
					}
					return lEmpresas;
				} catch (final Exception err) {
					throw new UException(err);
				}
			}
		}.execute(conn, sql, usuario);
	}

	public Vector<String[]> getEmpresasTodasUsuario(String usuario, int sessionID, Connection conn) throws Exception {
		final String sql = new Template("sql/companies/ContractServiceUserCompaniesAll.sql").getTemplate();

		return new QueryJdbcTemplate<Vector<String[]>>() {
			@Override
			protected Vector<String[]> parseResponse(ResultSet rset) throws UException {
				try {
					final Vector<String[]> lEmpresas = new Vector<String[]>();
					while (rset.next()) {
						lEmpresas.add(new String[] { rset.getString(1), rset.getString(2) });
					}
					return lEmpresas;
				} catch (final Exception err) {
					throw new UException(err);
				}
			}
		}.execute(conn, sql, usuario, usuario);
	}

	public List<Company> getUserCompanies(String user, String password) throws Exception {

		CheckingTools.failIfNull(user, ContractService.ERROR_USER_IS_MANDATORY);
		CheckingTools.failIfNull(password, ContractService.ERROR_PASSWORD_IS_MANDATORY);
		List<Company> res = new ArrayList<Company>();
		int sessionID = -1;
		try {
			sessionID = this.getLocator().startSession(user, password, null);
			if (sessionID > 0) {
				final IUserData userData = ((IOpentachServerLocator) this.getLocator()).getUserData(sessionID);
				res = userData.getCompanies();
			}
			if (res.isEmpty()) {
				throw new Exception();
			}
			return res;
		} catch (final Exception ex) {
			throw new Exception(ContractService.ERROR_INVALID_USER_PASSWORD_OR_NOT_COMPANY, ex);
		} finally {
			if (sessionID > 0) {
				this.getLocator().endSession(sessionID);
			}
		}
	}

	public Company getUserCompany(String user, String password, String companyId) throws Exception {
		final List<Company> userCompanies = this.getUserCompanies(user, password);

		for (final Company company : userCompanies) {
			if (company.getCif().equals(companyId)) {
				return company;
			}
		}
		throw new Exception(ContractService.ERROR_COMPANY_NOT_FOUND_FOR_USER);
	}

	public Map<String, Number> getDelegacionesUsuario(String usuario, int sessionID, Connection conn) throws Exception {
		final String sql = new Template("sql/companies/ContractServiceDelegacionesUsuario.sql").getTemplate();
		return new QueryJdbcTemplate<Map<String, Number>>() {
			@Override
			protected Map<String, Number> parseResponse(ResultSet rset) throws UException {
				try {
					final Map<String, Number> map = new Hashtable<String, Number>();
					while (rset.next()) {
						map.put(rset.getString(1), rset.getBigDecimal(2));
					}
					return map;
				} catch (final Exception err) {
					throw new UException(err);
				}
			}
		}.execute(conn, sql, usuario);

	}

	public Map<String, Number> getGruposUsuario(String usuario, int sessionID, Connection conn) throws Exception {
		final String sql1 = new Template("sql/companies/ContractServiceGruposUsuario.sql").getTemplate();
		return new QueryJdbcTemplate<Map<String, Number>>() {
			@Override
			protected Map<String, Number> parseResponse(ResultSet rset) throws UException {
				try {
					final Map<String, Number> map = new Hashtable<String, Number>();
					while (rset.next()) {
						map.put(rset.getString(1), rset.getBigDecimal(2));
					}
					return map;
				} catch (final Exception err) {
					throw new UException(err);
				}
			}
		}.execute(conn, sql1, usuario);
	}

	public Map<String, String> getContratosEmpresas(Vector<String> cifs, boolean isAdmin, boolean allowAllCompanies, int sessionID, Connection conn) throws Exception {
		final Hashtable<String, Object> filter = new Hashtable<>();
		filter.put("F_BAJA", new SearchValue(SearchValue.NULL, null));
		if (!isAdmin) {
			if (((cifs == null) || cifs.isEmpty()) && !allowAllCompanies) {
				throw new Exception("Invalid user settings");
			} else if ((cifs != null) && (cifs.size() > 0)) {
				filter.put("CIF", new SearchValue(SearchValue.IN, cifs));
			}
		}
		final EntityResult er = this.getEntity("ECifEmpreReq").query(filter, EntityResultTools.attributes("CIF", "CG_CONTRATO"), sessionID, conn);
		CheckingTools.checkValidEntityResult(er);
		final Map<String, String> res = new Hashtable<String, String>();
		final int nrec = er.calculateRecordNumber();
		for (int i = 0; i < nrec; i++) {
			final Hashtable record = er.getRecordValues(i);
			MapTools.safePut(res, (String) record.get("CIF"), (String) record.get("CG_CONTRATO"));
		}
		return res;
	}


	/**
	 * Gets the dummy contract.
	 *
	 * @param conn
	 *            the conn
	 * @return the dummy contract
	 * @throws Exception
	 *             the exception
	 */
	public Object getDummyContract(Connection conn) throws Exception {
		final EPreferenciasServidor ePreferences = (EPreferenciasServidor) this.getEntity(EPreferenciasServidor.SERVER_PREFERENCES_ENTITY_NAME);
		return ePreferences.getValue(EPreferenciasServidor.DUMMY_CONTRACT, TableEntity.getEntityPrivilegedId(ePreferences), conn);
	}


	public Pair<Object, Vector<Object>> queryContractAndDrivers(final String cif, final Object cgContrato, final Vector<Object> vDrivers, final int sesionId) throws Exception {
		final Pair<Object, Vector<Object>> pair = new OntimizeConnectionTemplate<Pair<Object, Vector<Object>>>() {

			@Override
			protected Pair<Object, Vector<Object>> doTask(Connection conn) throws UException {
				try {
					final Object resCgContrato = ContractService.this.getContract(cgContrato, cif, sesionId, conn);
					final Vector<Object> resDrivers = ContractService.this.getDriversToQuery(vDrivers, cif, cgContrato, sesionId, conn);
					return new Pair<Object, Vector<Object>>(resCgContrato, resDrivers);
				} catch (final Exception err) {
					throw new UException(err);
				}
			}

		}.execute(this.getConnectionManager(), true);
		return pair;
	}

	private Vector<Object> getDriversToQuery(Vector<Object> vDrivers, String cif, Object cgContrato, int sesionId, Connection conn) throws Exception {
		if ((vDrivers == null) || vDrivers.isEmpty()) {
			// hay que consultar todos los conductores de la empresa
			final TransactionalEntity entValidos = this.getEntity("EConductorContValido");
			final Hashtable<String, Object> av2 = new Hashtable<String, Object>();
			if (cif != null) {
				av2.put("CIF", cif);
			}
			av2.put("CG_CONTRATO", cgContrato);
			final EntityResult resConductores = entValidos.query(av2, EntityResultTools.attributes("IDCONDUCTOR"), sesionId, conn);
			if (resConductores.calculateRecordNumber() > 0) {
				vDrivers = (Vector<Object>) resConductores.get("IDCONDUCTOR");
			}
		}
		return vDrivers;
	}

	private Object getContract(Object cgContrato, String cif, int sesionId, Connection conn) throws Exception {
		if (cgContrato == null) {
			cgContrato = this.getContratoVigente(cif, sesionId, conn);
		}
		if (cgContrato == null) {
			throw new Exception("M_NO_SE_PUEDE_REALIZAR_ANALISIS");
		}

		return ContractUtils.checkContratoFicticio((IOpentachServerLocator) this.getLocator(), cgContrato, sesionId, conn);
	}

	public Object queryCompanyCif(Object cgContrato, Object driverid) throws UException, SQLException {
		final String sql = "SELECT CIF FROM CDVCONDUCTOR_CONT WHERE F_BAJA IS NULL AND idconductor=? and cg_contrato = ?";
		return new OntimizeConnectionTemplate<Object>() {
			@Override
			protected Object doTask(Connection conn) throws UException, SQLException {
				return new QueryJdbcTemplate<String>() {
					@Override
					protected String parseResponse(ResultSet rset) throws UException {
						try {
							if (rset.next()) {
								return rset.getString(1);
							}
							return null;
						} catch (final Exception err) {
							throw new UException(err);
						}
					}
				}.execute(conn, sql, driverid, cgContrato);
			}
		}.execute(this.getConnectionManager(), true);

	}
}
