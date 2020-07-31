package com.opentach.server.distributor.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import com.opentach.common.distributor.exception.DistributorException;
import com.opentach.common.distributor.naming.DistributorNaming;
import com.opentach.common.distributor.service.IDistributorService;
import com.opentach.common.exception.OpentachException;
import com.opentach.common.services.IUserAndRoleService;
import com.opentach.common.user.IUserData;
import com.opentach.server.distributor.dao.DistributorDao;
import com.opentach.server.util.spring.AbstractSpringDelegate;

/**
 * The Class DistributorService.
 */
@Service("DistributorService")
public class DistributorServiceImpl extends AbstractSpringDelegate implements IDistributorService {

	@Autowired
	private DistributorDao				distributorDao;

	@Autowired
	private DefaultOntimizeDaoHelper	daoHelper;

	@Autowired
	private IUserAndRoleService			userAndRoleService;

	@Override
	public EntityResult distributorQuery(Map<?, ?> keysValues, List<?> attributes) throws DistributorException {
		return this.daoHelper.query(this.distributorDao, keysValues, attributes, null, "default");
	}


	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	public EntityResult distributorInsert(Map<?, ?> attributes) throws DistributorException {
		try {
			MapTools.safePut((Map) attributes, "NIVEL_CD", IUserData.NIVEL_DISTRIBUIDOR, false);
			this.userAndRoleService.userInsert(attributes);
		} catch (OpentachException err) {
			throw new DistributorException("E_INSERTING_USER", err);
		}
		return this.daoHelper.insert(this.distributorDao, attributes);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	public EntityResult distributorUpdate(Map<?, ?> attributes, Map<?, ?> keysValues) throws DistributorException {
		Map<?, ?> attributesForDistributor = this.subMap(attributes, DistributorNaming.DIS_NAME, DistributorNaming.DIS_NOTES,
				DistributorNaming.USUARIO);
		Map<?, ?> attributesForUser = this.subMapNotIn(attributes, DistributorNaming.DIS_NAME, DistributorNaming.DIS_NOTES, DistributorNaming.USUARIO);
		EntityResult toReturn = null;
		try {
			if (!attributesForUser.isEmpty()) {
				EntityResult distributorQuery = this.distributorQuery(keysValues, EntityResultTools.attributes(DistributorNaming.USUARIO));
				CheckingTools.checkValidEntityResult(distributorQuery, "E_USER_NOT_FOUND", true, true, null);
				Object usuario = ((List) distributorQuery.get(DistributorNaming.USUARIO)).get(0);
				toReturn = this.userAndRoleService.userUpdate(attributesForUser, EntityResultTools.keysvalues(DistributorNaming.USUARIO, usuario));
			}
		} catch (Exception err) {
			throw new DistributorException("E_UPDATING_USER", err);
		}

		if (!attributesForDistributor.isEmpty()) {
			toReturn = this.daoHelper.update(this.distributorDao, attributesForDistributor, keysValues);
		}
		return toReturn;
	}

	private Map<?, ?> subMapNotIn(Map<?, ?> attributes, String... notInterestingFields) {
		HashMap toReturn = new HashMap(attributes);
		for (String iField : notInterestingFields) {
			toReturn.remove(iField);
		}
		return toReturn;
	}

	private Map<?, ?> subMap(Map<?, ?> attributes, String... interestingFields) {
		HashMap toReturn = new HashMap();
		for (String iField : interestingFields) {
			MapTools.safePut(toReturn, iField, attributes.get(iField));
		}
		return toReturn;
	}


	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Exception.class)
	public EntityResult distributorDelete(Map<?, ?> keysValues) throws DistributorException {
		try {
			EntityResult distributorQuery = this.distributorQuery(keysValues, EntityResultTools.attributes(DistributorNaming.USUARIO));
			CheckingTools.checkValidEntityResult(distributorQuery, "E_USER_NOT_FOUND", true, true, null);
			Object usuario = ((List) distributorQuery.get(DistributorNaming.USUARIO)).get(0);
			this.daoHelper.delete(this.distributorDao, keysValues);
			return this.userAndRoleService.userDelete(EntityResultTools.keysvalues(DistributorNaming.USUARIO, usuario));
		} catch (Exception err) {
			throw new DistributorException("E_DELETING_USER", err);
		}


	}

}