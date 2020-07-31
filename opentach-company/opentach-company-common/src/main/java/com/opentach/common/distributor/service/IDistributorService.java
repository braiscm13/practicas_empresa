package com.opentach.common.distributor.service;

import java.util.List;
import java.util.Map;

import com.ontimize.db.EntityResult;
import com.opentach.common.distributor.exception.DistributorException;

/**
 * The Interface ILaborReportService contains all reports about labor concepts.
 */
public interface IDistributorService {

	/**
	 * Method which allow to query distributors
	 *
	 * @param attributesValues
	 * @param keysValues
	 * @return
	 * @throws DistributorException
	 */
	EntityResult distributorQuery(Map<?, ?> keysValues, List<?> attributes) throws DistributorException;

	/**
	 * Method which allow to create a new distributor
	 *
	 * @param attributesValues
	 * @param keysValues
	 * @return
	 * @throws DistributorException
	 */
	EntityResult distributorInsert(Map<?, ?> attributes) throws DistributorException;

	/**
	 * Method which allow to update a distributor
	 *
	 * @param attributesValues
	 * @param keysValues
	 * @return
	 */
	EntityResult distributorUpdate(Map<?, ?> keysValues, Map<?, ?> attributes) throws DistributorException;

	/**
	 * Method which allow to delete a distributor
	 *
	 * @param attributesValues
	 * @param keysValues
	 * @return
	 */
	EntityResult distributorDelete(Map<?, ?> keysValues) throws DistributorException;

}
