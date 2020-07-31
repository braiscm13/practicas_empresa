package com.opentach.server.entities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.SearchValue;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.common.tools.Template;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.OpentachFieldNames;
import com.opentach.server.companies.ContractService;
import com.opentach.server.util.ContractUtils;
import com.opentach.server.util.db.FileTableEntity;
import com.utilmize.server.tools.sqltemplate.ProcedureJdbcTemplate;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.tools.exception.UException;

public class EInformeTiemposCondDesc extends FileTableEntity {

	//	private static final Logger	logger	= LoggerFactory.getLogger(EInformeTiemposCondDesc.class);

	public EInformeTiemposCondDesc(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	public EntityResult query(final Hashtable cv, final Vector v, final int sesionId, final Connection con) throws Exception {
		boolean oldAutoCommit = con.getAutoCommit();
		try {
			if (oldAutoCommit) {
				con.setAutoCommit(false);
			}
			Object numReq = this.getNumReq(cv, sesionId, con);
			final Object realNumReq = ContractUtils.checkContratoFicticio(this.getLocator(), numReq, sesionId, con);
			final Timestamp fComienzo = new Timestamp(((Date) cv.get("FILTERFECINI")).getTime() - (2419200 * 1000));
			final Timestamp fFinal = new Timestamp(((Date) cv.get("FILTERFECINI")).getTime());
			Object ob = cv.get(OpentachFieldNames.IDCONDUCTOR_FIELD);
			if (ob instanceof SearchValue) {
				ob = ((SearchValue) ob).getValue();
				if (ob instanceof List) {
					ob = ((List<Object>) ob).get(0);
				}
			}
			final String driver = (String) ob;

			new ProcedureJdbcTemplate<Void>().execute(con, "PRC_INFRAC_ORGANIZA_ACTIV_AR", realNumReq, driver, fComienzo, fFinal);
			// String
			String sql = new Template("sql/EInformeTiemposCondDesc.sql").getTemplate();
			return new QueryJdbcTemplate<EntityResult>() {

				@Override
				protected EntityResult parseResponse(ResultSet rset) throws UException {
					try {
						EntityResult res = new EntityResult();
						if (rset.next()) {
							Hashtable<String, Object> av = new Hashtable<String, Object>();
							String ini_semana = rset.getString(1);
							String fin_semana = rset.getString(2);
							String ultima_fecha = rset.getString(3);
							String cond_acumulada = rset.getString(4);
							String cond_acumulada2sem = rset.getString(5);
							String ultimoDesc = rset.getString(6);
							String plazoSigDesc = rset.getString(7);
							String condDisponible = rset.getString(8);
							String DDR_DISP_DS = rset.getString(9);
							String H10_DS = rset.getString(10);

							MapTools.safePut(av, "NUMREQ", realNumReq);
							MapTools.safePut(av, "IDCONDUCTOR", driver);
							MapTools.safePut(av, "INI_SEMANA", ini_semana);
							MapTools.safePut(av, "FIN_SEMANA", fin_semana);
							MapTools.safePut(av, "ULTIMA_FECHA", ultima_fecha);
							MapTools.safePut(av, "COND_ACUMULADA", cond_acumulada);
							MapTools.safePut(av, "COND_ACUMULADA2SEM", cond_acumulada2sem);
							MapTools.safePut(av, "ULTIMODESC", ultimoDesc);
							MapTools.safePut(av, "PLAZOSIGDESC", plazoSigDesc);
							MapTools.safePut(av, "CONDDISPONIBLE", condDisponible);
							MapTools.safePut(av, "DDR_DISP_DS", DDR_DISP_DS);
							MapTools.safePut(av, "H10_DS", H10_DS);
							res.addRecord(av);
						}
						return res;
					} catch (Exception ex) {
						throw new UException(ex);
					}
				}
			}.execute(con, sql, driver, realNumReq, driver, realNumReq, driver, driver, realNumReq, driver, driver, realNumReq, realNumReq, driver, driver, realNumReq,
					driver, driver, driver, driver, realNumReq, realNumReq, driver);
		} finally {
			con.rollback();
			if (oldAutoCommit) {
				con.setAutoCommit(true);
			}
		}
	}

	private String getNumReq(Hashtable cv, int sesionId, Connection conn) throws Exception {
		String cif = (String) cv.get(OpentachFieldNames.CIF_FIELD);
		String numReq = (String) cv.get(OpentachFieldNames.CG_CONTRATO_FIELD);

		if (numReq == null) {
			Object oNumReq = cv.get(OpentachFieldNames.NUMREQ_FIELD);
			if (oNumReq instanceof String) {
				numReq = (String) oNumReq;
			} else if (oNumReq instanceof SearchValue) {
				if (((SearchValue) oNumReq).getCondition() == 8) {
					Vector<Object> vv = (Vector<Object>) ((SearchValue) oNumReq).getValue();
					numReq = (String) vv.firstElement();
				} else {
					numReq = (String) ((SearchValue) oNumReq).getValue();
				}
			}
		}
		if (numReq == null) {
			numReq = this.getService(ContractService.class).getContratoVigente(cif, sesionId, conn);
			if (numReq == null) {
				throw new Exception("No hay contrato asociado");
			}
		}
		return numReq;
	}

}
