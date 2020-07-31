package com.opentach.server.company.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import com.opentach.common.company.exception.CompanyException;
import com.opentach.common.company.naming.CompanyNaming;
import com.opentach.common.company.service.ICompanyDelegationService;
import com.opentach.server.company.dao.CompanyDelegationDao;
import com.opentach.server.company.dao.CompanyUserDelegationDao;
import com.opentach.server.util.UserInfoComponent;
import com.opentach.server.util.spring.AbstractSpringDelegate;

/**
 * The Class CompanyUserDelegationServiceImpl.
 */
@Service("CompanyDelegationService")
public class CompanyDelegationServiceImpl extends AbstractSpringDelegate implements ICompanyDelegationService {

	/** The CONSTANT logger */
	private static final Logger			logger	= LoggerFactory.getLogger(CompanyDelegationServiceImpl.class);

	@Autowired
	private CompanyUserDelegationDao	companyUserDelegationDao;

	@Autowired
	private CompanyDelegationDao		companyDelegationDao;
	@Autowired
	private UserInfoComponent			userInfo;

	@Autowired
	private DefaultOntimizeDaoHelper	daoHelper;

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public EntityResult companyUserDelegationQuery(Map<?, ?> keysValues, List<?> attributes) throws CompanyException {
		return this.daoHelper.query(this.companyUserDelegationDao, keysValues, attributes, null, "default");
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	public EntityResult companyUserDelegationInsert(Map<?, ?> attributes) throws CompanyException {
		return this.daoHelper.insert(this.companyUserDelegationDao, attributes);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	public EntityResult companyUserDelegationUpdate(Map<?, ?> attributes, Map<?, ?> keysValues) throws CompanyException {
		return this.daoHelper.update(this.companyUserDelegationDao, attributes, keysValues);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	public EntityResult companyUserDelegationDelete(Map<?, ?> keysValues) throws CompanyException {
		return this.daoHelper.delete(this.companyUserDelegationDao, keysValues);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public EntityResult companyDelegationQuery(Map<?, ?> keysValues, List<?> attributes) throws CompanyException {
		this.userInfo.checkCifFilter(keysValues, CompanyNaming.CIF);
		return this.daoHelper.query(this.companyDelegationDao, keysValues, attributes, null, "default");
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public EntityResult companyDelegationDriverQuery(Map<?, ?> keysValues, List<?> attributes) throws CompanyException {
		this.userInfo.checkCifFilter(keysValues, CompanyNaming.CIF);
		return this.daoHelper.query(this.companyDelegationDao, keysValues, attributes, null, "drivers");
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public EntityResult companyDelegationVehicleQuery(Map<?, ?> keysValues, List<?> attributes) throws CompanyException {
		this.userInfo.checkCifFilter(keysValues, CompanyNaming.CIF);
		return this.daoHelper.query(this.companyDelegationDao, keysValues, attributes, null, "vehicles");
	}

}