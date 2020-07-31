package com.opentach.server.companies;

import java.sql.Connection;
import java.util.Hashtable;

import com.ontimize.db.EntityResult;
import com.ontimize.db.TransactionalEntity;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.companies.ICompanyService;
import com.opentach.common.company.naming.CompanyNaming;
import com.opentach.server.IOpentachServerLocator;
import com.utilmize.server.services.UAbstractService;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.tools.exception.UException;

public class CompanyService extends UAbstractService implements ICompanyService {

	private final CompanyDeleteDelegate deleteDelegate;

	public CompanyService(int port, EntityReferenceLocator erl, Hashtable hconfig) throws Exception {
		super(port, erl, hconfig);
		this.deleteDelegate = new CompanyDeleteDelegate((IOpentachServerLocator) erl);
	}

	@Override
	public void makeDemoCompanyFix(final Object cif, final int sessionId) throws Exception {
		new OntimizeConnectionTemplate<Void>() {

			@Override
			protected Void doTask(Connection con) throws UException {
				try {
					CompanyService.this.makeDemoComopanyFix(cif, sessionId, con);
					return null;
				} catch (Exception ex) {
					throw new UException(ex);
				}

			}
		}.execute(this.getConnectionManager(), false);
	}

	private void makeDemoComopanyFix(Object cif, int sessionId, Connection con) throws Exception {
		TransactionalEntity eDfEmp = this.getEntity(CompanyNaming.ENTITY);
		eDfEmp.update(EntityResultTools.keysvalues("IS_DEMO", "N"), EntityResultTools.keysvalues("CIF", cif), sessionId, con);
	}

	public EntityResult deleteCompany(String cif, int sessionId, Connection conn) throws Exception {
		return this.deleteDelegate.deleteCompany(cif, sessionId, conn);
	}
}
