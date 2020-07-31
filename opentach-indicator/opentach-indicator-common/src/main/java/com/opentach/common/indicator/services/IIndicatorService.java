package com.opentach.common.indicator.services;


import java.util.List;
import java.util.Map;

import com.ontimize.db.EntityResult;
import com.opentach.common.indicator.exception.IndicatorException;
import com.opentach.common.indicator.result.IIndicatorResult;

/**
 * The Interface IIndicatorService contains all API to manage indicators CRUD and executions.
 */
public interface IIndicatorService {


	/////////////////////////////////////////////////////// INDICATOR METHODS ////////////////////////////////////////////////////////////////

	/**
	 * Method which allow to query defined indicators
	 *
	 * @param keysValues
	 * @param attributes
	 * @return
	 * @throws IndicatorException
	 */
	EntityResult indicatorQuery(Map<?, ?> keysValues, List<?> attributes) throws IndicatorException;

	/**
	 * Method which allow to create new indicator settings
	 *
	 * @param attributesValues
	 * @param keysValues
	 * @return
	 */
	EntityResult indicatorInsert(Map<?, ?> attributes) throws IndicatorException;

	/**
	 * Method which allow to update indicator settings
	 *
	 * @param attributesValues
	 * @param keysValues
	 * @return
	 */
	EntityResult indicatorUpdate(Map<?, ?> attributes, Map<?, ?> keysValues) throws IndicatorException;

	/**
	 * Method which allow to delete indicator settings
	 *
	 * @param attributesValues
	 * @param keysValues
	 * @return
	 */
	EntityResult indicatorDelete(Map<?, ?> keysValues) throws IndicatorException;

	/////////////////////////////////////////////////////// INDICATOR EXECTUION METHODS ////////////////////////////////////////////////////////////////
	/**
	 * Method which allow to query defined indicators
	 *
	 * @param keysValues
	 * @param attributes
	 * @return
	 * @throws IndicatorException
	 */
	EntityResult indicatorExecutionQuery(Map<?, ?> keysValues, List<?> attributes) throws IndicatorException;

	/**
	 * Method which allow to create new indicator settings
	 *
	 * @param attributesValues
	 * @param keysValues
	 * @return
	 */
	EntityResult indicatorExecutionInsert(Map<?, ?> attributes) throws IndicatorException;

	/**
	 * Method which allow to execute an indicator on demand, and returns results.
	 *
	 * @param attributesValues
	 * @param keysValues
	 * @return
	 */
	List<IIndicatorResult> executeIndicator(Object indId, boolean save) throws IndicatorException;

	/////////////////////////////////////////////////////// OTHER METHODS ////////////////////////////////////////////////////////////////

	/**
	 * Method which allow to query defined indicators
	 *
	 * @param keysValues
	 * @param attributes
	 * @return
	 * @throws IndicatorException
	 */
	EntityResult indicatorImplementationQuery(Map<?, ?> keysValues, List<?> attributes) throws IndicatorException;
}
