package com.opentach.server.tdi.sync;

import java.sql.Connection;
import java.util.Hashtable;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.opentach.common.company.naming.CompanyNaming;
import com.opentach.server.OpentachServerLocator;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.tools.exception.UException;

public class TdiCompanySettingsFactory {

	private static final String					TDI			= "TDI";
	private static final String	TDI_IP		= "TDI_IP";
	private static final String	TDI_PORT	= "TDI_PORT";
	private static final String	TDI_USER	= "TDI_USER";
	private static final String	TDI_PASS	= "TDI_PASS";
	private static final String					TDI_GROUPID	= "TDI_GROUPID";

	private static TdiCompanySettingsFactory	instance;

	private TdiCompanySettingsFactory() {
		super();
	}

	public static TdiCompanySettingsFactory getInstance() {
		if (TdiCompanySettingsFactory.instance == null) {
			TdiCompanySettingsFactory.instance = new TdiCompanySettingsFactory();
		}
		return TdiCompanySettingsFactory.instance;
	}

	public TdiCompanySettings getSettings(final String companyCIF) throws Exception {
		return new OntimizeConnectionTemplate<TdiCompanySettings>() {

			@Override
			protected TdiCompanySettings doTask(Connection con) throws UException {
				try {
					return TdiCompanySettingsFactory.this.getSettings(companyCIF, con);
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}

		}.execute(OpentachServerLocator.getLocator(), true);

	}

	protected TdiCompanySettings getSettings(String companyCIF, Connection con) throws Exception {
		Entity eDfEmp = OpentachServerLocator.getLocator().getEntityReferenceFromServer(CompanyNaming.ENTITY);

		EntityResult res = eDfEmp.query(EntityResultTools.keysvalues("CIF", companyCIF), EntityResultTools.attributes(TdiCompanySettingsFactory.TDI,
				TdiCompanySettingsFactory.TDI_IP, TdiCompanySettingsFactory.TDI_GROUPID,
				TdiCompanySettingsFactory.TDI_PORT, TdiCompanySettingsFactory.TDI_USER, TdiCompanySettingsFactory.TDI_PASS),
				TableEntity.getEntityPrivilegedId(eDfEmp));
		CheckingTools.checkValidEntityResult(res, "TDI_INFO_NOT_FOUND", true, true, new Object[] {});
		Hashtable recordValues = res.getRecordValues(0);
		String tdi = (String) recordValues.get(TdiCompanySettingsFactory.TDI);
		TdiCompanySettings settings = new TdiCompanySettings();
		settings.setCif(companyCIF);
		settings.setIp((String) recordValues.get(TdiCompanySettingsFactory.TDI_IP));
		settings.setPort((String) recordValues.get(TdiCompanySettingsFactory.TDI_PORT));
		settings.setUser((String) recordValues.get(TdiCompanySettingsFactory.TDI_USER));
		settings.setPass((String) recordValues.get(TdiCompanySettingsFactory.TDI_PASS));
		settings.setGroupid((String) recordValues.get(TdiCompanySettingsFactory.TDI_GROUPID));
		if (!"S".equals(tdi) || (settings.getIp() == null) || (settings.getPort() == null)) {
			return null;
		}
		return settings;
	}

}
