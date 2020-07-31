package com.opentach.common.alert.services;

import java.util.List;
import java.util.Map;

import com.ontimize.db.EntityResult;
import com.opentach.common.alert.exception.AlertException;
import com.opentach.common.alert.result.IAlertResult;

/**
 * The Interface IAlertService contains all API to manage alerts CRUD and executions.
 */
public interface IAlertService {

	/////////////////////////////////////////////////////// ALERT METHODS ////////////////////////////////////////////////////////////////

	/**
	 * Method which allow to query defined alerts
	 *
	 * @param keysValues
	 * @param attributes
	 * @return
	 * @throws AlertException
	 */
	EntityResult alertQuery(Map<?, ?> keysValues, List<?> attributes) throws AlertException;

	/** Method to query alerts that can be autoexecuted by alert scheduler */
	EntityResult alertSchedulerQuery(Map<?, ?> keysValues, List<?> attributes) throws AlertException;

	/**
	 * Method which allow to create new alert settings
	 *
	 * @param attributesValues
	 * @param keysValues
	 * @return
	 * @throws AlertException
	 */
	EntityResult alertInsert(Map<?, ?> attributes) throws AlertException;

	/**
	 * Method which allow to update alert settings
	 *
	 * @param attributesValues
	 * @param keysValues
	 * @return
	 * @throws AlertException
	 */
	EntityResult alertUpdate(Map<?, ?> attributes, Map<?, ?> keysValues) throws AlertException;

	/**
	 * Method which allow to delete alert settings
	 *
	 * @param attributesValues
	 * @param keysValues
	 * @return
	 * @throws AlertException
	 */
	EntityResult alertDelete(Map<?, ?> keysValues) throws AlertException;

	EntityResult alertImplementationQuery(Map<?, ?> keysValues, List<?> attributes) throws AlertException;

	/////////////////////////////////////////////////////// ALERT EXECUTION METHODS ///////////////////////////////////////////////////////

	EntityResult alertExecutionQuery(Map<?, ?> keysValues, List<?> attributes) throws AlertException;

	EntityResult alertExecutionInsert(Map<?, ?> attributes) throws AlertException;

	EntityResult alertExecutionUpdate(Map<?, ?> attributes, Map<?, ?> keysValues) throws AlertException;

	EntityResult alertExecutionDelete(Map<?, ?> keysValues) throws AlertException;

	/////////////////////////////////////////////////////// ALERT EXECUTION METHODS ///////////////////////////////////////////////////////

	List<IAlertResult> executeAlert(Object alrId) throws AlertException;

	List<IAlertResult> executeAlertWithData(Object alrId, Map<?, ?> extraData) throws AlertException;

	void executeAlertAsynch(Object alrId) throws AlertException;

	void executeAlertAsynchWithData(Object alrId, Map<?, ?> extraData) throws AlertException;

	List<IAlertResult> executeAlertByCode(String alrCode) throws AlertException;

	List<IAlertResult> executeAlertByCodeWithData(String alrCode, Map<?, ?> extraData) throws AlertException;

	void executeAlertByCodeAsynch(String alrCode) throws AlertException;

	void executeAlertByCodeAsynchWithData(String alrCode, Map<?, ?> extraData) throws AlertException;

	/////////////////////////////////////////////////////// MAIL METHODS ////////////////////////////////////////////////////////////////
	void sendEmail(List<String> to, List<String> cc, List<String> bcc, String templateSubject, String templateBody) throws AlertException;

	void sendEmailCompose(List<String> to, List<String> cc, List<String> bcc, String templateSubject, String templateBody, Map<?, ?> data) throws AlertException;

}
