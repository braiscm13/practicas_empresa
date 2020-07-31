package com.opentach.common.company.service;

import java.util.List;
import java.util.Map;

import com.ontimize.db.EntityResult;
import com.opentach.common.company.exception.CompanyException;

/**
 * The Interface ICompanyContactService contains methods to manage company contacts.
 */
public interface ICompanyContactService {


	/**
	 * Method which allow to query contacts
	 *
	 * @param keysValues
	 * @param attributes
	 * @return
	 * @throws IndicatorException
	 */
	EntityResult companyContactQuery(Map<?, ?> keysValues, List<?> attributes) throws CompanyException;

	/**
	 * Method which allow to create new contact
	 *
	 * @param attributesValues
	 * @param keysValues
	 * @return
	 */
	EntityResult companyContactInsert(Map<?, ?> attributes) throws CompanyException;

	/**
	 * Method which allow to update contact
	 *
	 * @param attributesValues
	 * @param keysValues
	 * @return
	 */
	EntityResult companyContactUpdate(Map<?, ?> attributes, Map<?, ?> keysValues) throws CompanyException;

	/**
	 * Method which allow to delete contact
	 *
	 * @param attributesValues
	 * @param keysValues
	 * @return
	 */
	EntityResult companyContactDelete(Map<?, ?> keysValues) throws CompanyException;

}
