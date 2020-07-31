package com.opentach.server.company.service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ontimize.db.EntityResult;
import com.ontimize.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.db.SQLStatementBuilder.BasicField;
import com.ontimize.db.SQLStatementBuilder.BasicOperator;
import com.ontimize.db.SQLStatementBuilder.ExtendedSQLConditionValuesProcessor;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import com.opentach.common.company.exception.CompanyException;
import com.opentach.common.company.naming.CompanyNaming;
import com.opentach.common.company.service.ICompanyUserService;
import com.opentach.common.exception.OpentachException;
import com.opentach.common.naming.UserNaming;
import com.opentach.common.services.IUserAndRoleService;
import com.opentach.common.user.IUserData;
import com.opentach.server.company.dao.CompanyUserDao;
import com.opentach.server.util.spring.AbstractSpringDelegate;

/**
 * The Class CompanyUserServiceImpl.
 */
@Service("CompanyUserService")
public class CompanyUserServiceImpl extends AbstractSpringDelegate implements ICompanyUserService {

	/** The CONSTANT logger */
	private static final Logger			logger	= LoggerFactory.getLogger(CompanyUserServiceImpl.class);

	@Autowired
	private CompanyUserDao				companyUserDao;

	@Autowired
	private DefaultOntimizeDaoHelper	daoHelper;

	@Autowired
	private IUserAndRoleService			userAndRoleService;

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public EntityResult companyUserQuery(Map<?, ?> keysValues, List<?> attributes) throws CompanyException {
		return this.daoHelper.query(this.companyUserDao, keysValues, attributes, null, "default");
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	public EntityResult companyUserInsert(Map<?, ?> attributes) throws CompanyException {
		try {
			this.userAndRoleService.userInsert(attributes);
		} catch (OpentachException err) {
			throw new CompanyException("E_INSERTING_USER", err);
		}
		return this.daoHelper.insert(this.companyUserDao, attributes);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> companyUserCreateDefault(String cif) throws CompanyException {
		Map<String, Object> values = new HashMap();
		values.put(CompanyNaming.CIF, cif);
		String generatedUser = this.generateUser(cif);
		values.put(UserNaming.USUARIO, generatedUser);
		values.put(UserNaming.PASSWORD, this.generatePassword());
		values.put(UserNaming.NIVEL_CD, IUserData.NIVEL_EMPRESA);
		this.companyUserInsert(values);
		return values;
	}

	private String generateUser(String cif) throws CompanyException {
		try {
			BasicExpression be = new BasicExpression(new BasicField(UserNaming.USUARIO), BasicOperator.LIKE_OP, cif + "*");
			EntityResult res = this.userAndRoleService.userQuery(//
					EntityResultTools.keysvalues(ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, be), EntityResultTools.attributes(UserNaming.USUARIO));
			List<String> usersThatExists = (List<String>) res.get(UserNaming.USUARIO);
			if (!usersThatExists.contains(cif)) {
				return cif;
			}
			int i = 0;
			while (usersThatExists.contains(cif + "_" + i)) {
				i++;
			}
			return cif + "_" + i;
		} catch (Exception err) {
			throw new CompanyException("E_CREATING_COMPANY_USER", err);
		}
	}

	private String generatePassword() {
		String PASSVALIDSYMBOLS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		final SecureRandom rnd = new SecureRandom();
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 8; i++) {
			sb.append(PASSVALIDSYMBOLS.charAt(rnd.nextInt(PASSVALIDSYMBOLS.length())));
		}
		return sb.toString();
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	public EntityResult companyUserUpdate(Map<?, ?> attributes, Map<?, ?> keysValues) throws CompanyException {
		return this.daoHelper.update(this.companyUserDao, attributes, keysValues);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	public EntityResult companyUserDelete(Map<?, ?> keysValues) throws CompanyException {
		return this.daoHelper.delete(this.companyUserDao, keysValues);
	}

}