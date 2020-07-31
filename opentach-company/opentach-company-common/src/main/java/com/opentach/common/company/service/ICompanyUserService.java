package com.opentach.common.company.service;

import java.util.List;
import java.util.Map;

import com.ontimize.db.EntityResult;
import com.opentach.common.company.exception.CompanyException;

/**
 * The Interface ICompanyUserService contains methods to manage company users.
 */
public interface ICompanyUserService {


	/**
	 * Method which allow to query users
	 *
	 * @param keysValues
	 * @param attributes
	 * @return
	 * @throws IndicatorException
	 */
	EntityResult companyUserQuery(Map<?, ?> keysValues, List<?> attributes) throws CompanyException;

	/**
	 * Method which allow to create new user
	 *
	 * @param attributesValues
	 * @param keysValues
	 * @return
	 */
	EntityResult companyUserInsert(Map<?, ?> attributes) throws CompanyException;

	/**
	 * Method which allow to update user
	 *
	 * @param attributesValues
	 * @param keysValues
	 * @return
	 */
	EntityResult companyUserUpdate(Map<?, ?> attributes, Map<?, ?> keysValues) throws CompanyException;

	/**
	 * Method which allow to delete user
	 *
	 * @param attributesValues
	 * @param keysValues
	 * @return
	 */
	EntityResult companyUserDelete(Map<?, ?> keysValues) throws CompanyException;

	/**
	 * Creates a new user for the company (random User, random Pass, level EMPRESA)
	 * 
	 * @param cif
	 * @return
	 * @throws CompanyException
	 */
	Map<String, Object> companyUserCreateDefault(String cif) throws CompanyException;

}
