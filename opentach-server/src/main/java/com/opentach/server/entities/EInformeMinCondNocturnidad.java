package com.opentach.server.entities;

import java.net.URL;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.db.ServerManager;
import com.ontimize.db.TableEntity;
import com.ontimize.gui.SearchValue;
import com.ontimize.jee.common.tools.DateTools;
import com.ontimize.jee.common.tools.Template;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.server.util.db.FileTableEntity;
import com.utilmize.server.tools.sqltemplate.QueryJdbcToEntityResultTemplate;

public class EInformeMinCondNocturnidad extends FileTableEntity {

	public EInformeMinCondNocturnidad(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	public EntityResult query(Hashtable cv, Vector av, int sesionId, Connection conn) throws Exception {
		ServerManager.getServerSecurityManager().checkActionPermission(sesionId, this.getName(), TableEntity.QUERY_ACTION);

		String sql = new Template("sql/EInformeMinCondNocturnidad.sql").getTemplate();
		SearchValue svFComienzo = (SearchValue) cv.get("FEC_COMIENZO");
		Object beginDate = ((List) svFComienzo.getValue()).get(0);
		Object endDate = ((List) svFComienzo.getValue()).get(1);
		Object beginTime = this.msToHour((Number) cv.get("BEGIN_NOC_TIME"));
		Object endTime = this.msToHour((Number) cv.get("END_NOC_TIME"));
		EntityResult res = new QueryJdbcToEntityResultTemplate().execute(conn, sql, beginTime, endTime, cv.get("CG_CONTRATO"), cv.get("CIF"), beginDate, endDate);
		res.get("NOMBRE");
		return res;
	}

	private Double msToHour(Number valueMs) {
		return valueMs.longValue() / (double) DateTools.MILLLISECONDS_PER_HOUR;
	}

	@Override
	protected void readProperties() throws Exception {}

	@Override
	protected void readProperties(Properties prop, URL uRLProp) throws Exception {}

	@Override
	protected void readProperties(String path) throws Exception {}

}
