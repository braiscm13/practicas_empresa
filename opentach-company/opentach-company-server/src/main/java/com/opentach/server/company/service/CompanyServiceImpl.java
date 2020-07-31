package com.opentach.server.company.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.exceptions.OntimizeJEEException;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import com.ontimize.jee.server.dao.IDownDateHelper;
import com.opentach.common.company.exception.CompanyException;
import com.opentach.common.company.naming.CompanyContractNaming;
import com.opentach.common.company.naming.CompanyNaming;
import com.opentach.common.company.service.ICompanyService;
import com.opentach.server.company.dao.CompanyDao;
import com.opentach.server.util.spring.AbstractSpringDelegate;

/**
 * The Class CompanyService.
 */
@Service("CompanyService")
public class CompanyServiceImpl extends AbstractSpringDelegate implements ICompanyService {

	@Autowired
	private CompanyDao					companyDao;

	@Autowired
	private DefaultOntimizeDaoHelper	daoHelper;

	@Autowired
	private IDownDateHelper				downdateHelper;

	@Override
	@Deprecated
	public String getCompanyCif(String companyName) throws CompanyException {
		CheckingTools.failIf(companyName == null, CompanyException.class, "E_MANDATORY_COMPANYNAME");
		EntityResult res = this.daoHelper.query(this.companyDao, EntityResultTools.keysvalues(CompanyNaming.NOMB, companyName), EntityResultTools.attributes(CompanyNaming.CIF));
		try {
			CheckingTools.checkValidEntityResult(res, "E_GETTING_COMPANY_CIF_BY_NAME", true, true, new Object[] {});
		} catch (OntimizeJEEException err) {
			throw new CompanyException("E_GETTING_COMPANY_CIF_BY_NAME", err);
		}
		return ((List<String>) res.get(CompanyNaming.CIF)).get(0);
	}

	@Override
	@Deprecated
	public boolean isCompanyDemo(String cif) throws CompanyException {
		CheckingTools.failIf(cif == null, CompanyException.class, "E_MANDATORY_CIF");
		try {
			EntityResult res = this.daoHelper.query(this.companyDao, EntityResultTools.keysvalues(CompanyNaming.CIF, cif), EntityResultTools.attributes(CompanyNaming.IS_DEMO));
			CheckingTools.checkValidEntityResult(res, "E_CHECKING_COMPANY_DEMO", true, true, new Object[] {});
			return ParseUtilsExtended.getBoolean(((List<Object>) res.get(CompanyNaming.IS_DEMO)).get(0), false);
		} catch (OntimizeJEEException err) {
			throw new CompanyException("E_CHECKING_COMPANY_DEMO", err);
		}
	}

	@Override
	public String getCompanyContract(String cif) throws CompanyException {
		CheckingTools.failIf(cif == null, CompanyException.class, "E_MANDATORY_CIF");
		try {
			EntityResult res = this.daoHelper.query(this.companyDao, EntityResultTools.keysvalues(CompanyNaming.CIF, cif),
					EntityResultTools.attributes(CompanyContractNaming.CG_CONTRATO),
					"currentContract");
			CheckingTools.checkValidEntityResult(res, "E_CHECKING_COMPANY_CONTRACT", true, true, new Object[] {});
			return (String) ((List<Object>) res.get(CompanyContractNaming.CG_CONTRATO)).get(0);
		} catch (OntimizeJEEException err) {
			throw new CompanyException("E_CHECKING_COMPANY_DEMO", err);
		}
	}

}