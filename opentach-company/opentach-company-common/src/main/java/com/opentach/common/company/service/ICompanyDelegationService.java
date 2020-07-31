package com.opentach.common.company.service;

import java.util.List;
import java.util.Map;

import com.ontimize.db.EntityResult;
import com.opentach.common.company.exception.CompanyException;

/**
 * The Interface ICompanyDelegationService contains methods to manage company users-delegation relationship.
 */
public interface ICompanyDelegationService {

	/**
	 * Method which allow to query users
	 *
	 * @param keysValues
	 * @param attributes
	 * @return
	 * @throws IndicatorException
	 */
	EntityResult companyUserDelegationQuery(Map<?, ?> keysValues, List<?> attributes) throws CompanyException;

	/**
	 * Method which allow to create new user
	 *
	 * @param attributesValues
	 * @param keysValues
	 * @return
	 */
	EntityResult companyUserDelegationInsert(Map<?, ?> attributes) throws CompanyException;

	/**
	 * Method which allow to update user
	 *
	 * @param attributesValues
	 * @param keysValues
	 * @return
	 */
	EntityResult companyUserDelegationUpdate(Map<?, ?> attributes, Map<?, ?> keysValues) throws CompanyException;

	/**
	 * Method which allow to delete user
	 *
	 * @param attributesValues
	 * @param keysValues
	 * @return
	 */
	EntityResult companyUserDelegationDelete(Map<?, ?> keysValues) throws CompanyException;

	EntityResult companyDelegationQuery(Map<?, ?> keysValues, List<?> attributes) throws CompanyException;

	EntityResult companyDelegationDriverQuery(Map<?, ?> keysValues, List<?> attributes) throws CompanyException;

	EntityResult companyDelegationVehicleQuery(Map<?, ?> keysValues, List<?> attributes) throws CompanyException;
}
