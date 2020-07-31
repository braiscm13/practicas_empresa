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
import com.opentach.common.company.service.ICompanyContactService;
import com.opentach.server.company.dao.CompanyContactDao;
import com.opentach.server.util.spring.AbstractSpringDelegate;

/**
 * The Class CompanyContactServiceImpl.
 */
@Service("CompanyContactService")
public class CompanyContactServiceImpl extends AbstractSpringDelegate implements ICompanyContactService {

	/** The CONSTANT logger */
	private static final Logger			logger	= LoggerFactory.getLogger(CompanyContactServiceImpl.class);

	@Autowired
	private CompanyContactDao			companyContactDao;

	@Autowired
	private DefaultOntimizeDaoHelper	daoHelper;

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public EntityResult companyContactQuery(Map<?, ?> keysValues, List<?> attributes) throws CompanyException {
		return this.daoHelper.query(this.companyContactDao, keysValues, attributes, null, "default");
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	public EntityResult companyContactInsert(Map<?, ?> attributes) throws CompanyException {
		return this.daoHelper.insert(this.companyContactDao, attributes);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	public EntityResult companyContactUpdate(Map<?, ?> attributes, Map<?, ?> keysValues) throws CompanyException {
		return this.daoHelper.update(this.companyContactDao, attributes, keysValues);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	public EntityResult companyContactDelete(Map<?, ?> keysValues) throws CompanyException {
		return this.daoHelper.delete(this.companyContactDao, keysValues);
	}
}