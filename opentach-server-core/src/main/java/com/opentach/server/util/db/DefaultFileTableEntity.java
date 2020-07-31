package com.opentach.server.util.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.util.PropertyUtils;
import com.opentach.server.util.ContractUtils;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.tools.exception.UException;

public class DefaultFileTableEntity extends FileTableEntity {

	public DefaultFileTableEntity(EntityReferenceLocator b, DatabaseConnectionManager g, int p, String props) throws Exception {
		super(b, g, p, PropertyUtils.loadProperties(props), PropertyUtils.loadProperties(props.substring(0, props.lastIndexOf(".")) + "_alias.properties"));
		this.entityName = props.substring(props.lastIndexOf("/") + 1, props.lastIndexOf("."));
	}

	@Override
	public EntityResult query(Hashtable cv, Vector av, int sesionId, Connection con) throws Exception {

		String cgContrato = (String) cv.get("CG_CONTRATO");
		if (cgContrato != null) {
			Object contratoReal = ContractUtils.checkContratoFicticio(this.getLocator(), cgContrato, sesionId, con);
			// TODO lo siguiente deberia sobrar
			if (!contratoReal.equals(cgContrato)) {
				// busco la empresa del contrato ficticio
				String last = new QueryJdbcTemplate<String>() {
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
				}.execute(con, "SELECT CIF FROM CDEMPRE_REQ WHERE NUMREQ = ?", cgContrato);

				cv.put("CIF", last);
				cv.put("CG_CONTRATO", contratoReal);
			}
		}

		EntityResult res = super.query(cv, av, sesionId, con);
		return res;
	}

}
