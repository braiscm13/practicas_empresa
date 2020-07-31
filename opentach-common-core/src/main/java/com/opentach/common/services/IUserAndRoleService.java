package com.opentach.common.services;

import java.util.List;
import java.util.Map;

import com.ontimize.db.EntityResult;
import com.opentach.common.exception.OpentachException;

/**
 * The Interface UserRoleService.
 */
public interface IUserAndRoleService {

	/**
	 * Users query.
	 *
	 * @param keysValues
	 *            the keys values
	 * @param attributes
	 *            the attributes
	 * @return the entity result
	 * @throws OpentachException
	 *             the ontimize jee exception
	 */
	EntityResult userQuery(Map<?, ?> keysValues, List<?> attributes) throws OpentachException;

	/**
	 * Users update.
	 *
	 * @param attributesValues
	 *            the attributes values
	 * @param keysValues
	 *            the keys values
	 * @return the entity result
	 * @throws OpentachException
	 *             the ontimize jee exception
	 */
	EntityResult userUpdate(Map<?, ?> attributesValues, Map<?, ?> keysValues) throws OpentachException;

	/**
	 * Users delete.
	 *
	 * @param keysValues
	 *            the keys values
	 * @return the entity result
	 * @throws OpentachException
	 *             the ontimize jee exception
	 */
	EntityResult userDelete(Map<?, ?> keysValues) throws OpentachException;

	/**
	 * Users insert.
	 *
	 * @param keysValues
	 *            the keys values
	 * @return the entity result
	 * @throws OpentachException
	 *             the ontimize jee exception
	 */

	EntityResult userInsert(Map<?, ?> keysValues) throws OpentachException;

	/**
	 * Users query.
	 *
	 * @param keysValues
	 *            the keys values
	 * @param attributes
	 *            the attributes
	 * @return the entity result
	 * @throws Exception
	 *             the exception
	 */
	EntityResult roleQuery(Map<?, ?> keysValues, List<?> attributes) throws OpentachException;

	/**
	 * Users update.
	 *
	 * @param attributesValues
	 *            the attributes values
	 * @param keysValues
	 *            the keys values
	 * @return the entity result
	 * @throws Exception
	 *             the exception
	 */
	EntityResult roleUpdate(Map<?, ?> attributesValues, Map<?, ?> keysValues) throws OpentachException;

	/**
	 * Users delete.
	 *
	 * @param keysValues
	 *            the keys values
	 * @return the entity result
	 * @throws Exception
	 *             the exception
	 */
	EntityResult roleDelete(Map<?, ?> keysValues) throws OpentachException;

	/**
	 * Users insert.
	 *
	 * @param keysValues
	 *            the keys values
	 * @return the entity result
	 * @throws Exception
	 *             the exception
	 */
	EntityResult roleInsert(Map<?, ?> keysValues) throws OpentachException;

	/**
	 * Server role query.
	 *
	 * @param keysValues
	 *            the keys values
	 * @param attributes
	 *            the attributes
	 * @return the entity result
	 * @throws Exception
	 *             the exception
	 */
	EntityResult serverRoleQuery(Map<?, ?> keysValues, List<?> attributes) throws OpentachException;

	/**
	 * Server role update.
	 *
	 * @param attributesValues
	 *            the attributes values
	 * @param keysValues
	 *            the keys values
	 * @return the entity result
	 * @throws Exception
	 *             the exception
	 */
	EntityResult serverRoleUpdate(Map<?, ?> attributesValues, Map<?, ?> keysValues) throws OpentachException;


}
