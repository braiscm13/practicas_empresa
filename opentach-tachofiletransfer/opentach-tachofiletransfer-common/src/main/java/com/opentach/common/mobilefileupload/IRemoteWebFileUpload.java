package com.opentach.common.mobilefileupload;

import java.util.Date;
import java.util.Map;

/**
 * Interfaz remota para la subida de ficheros por web (tel�fono m�vil, web
 * services...).
 */
public interface IRemoteWebFileUpload {

	/**
	 * Sube un fichero de forma an�nima. El nombre del fichero se generar� seg�n
	 * la fecha/hora de recepci�n �y el n�mero de tel�fono?
	 *
	 * @param file
	 *            array de bytes que componen el fichero.
	 * @param mailto
	 *            the mailto
	 * @param user
	 *            the user
	 * @param password
	 *            the password
	 * @param analize
	 *            the analize
	 * @param otherParams
	 *            the other params
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	String uploadFileByPhone(byte[] file, String mailto, String user, String password, boolean analize, Map<?, ?> otherParams, Date downloadDate) throws Exception;

	/**
	 * Check credentials.
	 *
	 * @param user
	 *            the user
	 * @param password
	 *            the password
	 * @param parameters
	 *            the parameters
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	String checkCredentials(String user, String password, Map<?, ?> parameters) throws Exception;

	/**
	 * Save mail extraction.
	 *
	 * @param parameters
	 *            the parameters
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	String saveMailExtraction(Map<?, ?> parameters) throws Exception;

	/**
	 * Check vip code.
	 *
	 * @param code
	 *            the code
	 * @param deviceId
	 *            the device id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	String checkVipCode(String code, String deviceId) throws Exception;


}
