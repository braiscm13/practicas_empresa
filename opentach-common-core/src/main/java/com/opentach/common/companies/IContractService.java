package com.opentach.common.companies;

import java.rmi.Remote;

/**
 * The Interface IContractService.
 */
public interface IContractService extends Remote {
	/** The Constant ID. */
	final static String	ID	= "ContractService";

	/**
	 * Gets the contrato vigente.
	 *
	 * @param cif
	 *            the cif
	 * @param sessionID
	 *            the session id
	 * @return the contrato vigente
	 * @throws Exception
	 *             the exception
	 */
	String getContratoVigente(String cif, int sessionID) throws Exception;

	/**
	 * Limpiar contrato.
	 *
	 * @param cgContrato
	 *            the cg contrato
	 * @param sessionID
	 *            the session id
	 * @throws Exception
	 *             the exception
	 */
	void limpiarContrato(String cgContrato, int sessionID) throws Exception;

	/**
	 * Reprocesar contrato.
	 *
	 * @param cgContrato
	 *            the cg contrato
	 * @param sessionID
	 *            the session id
	 * @throws Exception
	 *             the exception
	 */
	void reprocesarContrato(String cgContrato, int sessionID) throws Exception;

	/**
	 * Eliminar contrato.
	 *
	 * @param cgContrato
	 *            the cg contrato
	 * @param sessionID
	 *            the session id
	 * @throws Exception
	 *             the exception
	 */
	void eliminarContrato(String cgContrato, int sessionID) throws Exception;

	/**
	 * Limpiar datos.
	 *
	 * @param cgContrato
	 *            the cg contrato
	 * @param sessionID
	 *            the session id
	 * @throws Exception
	 *             the exception
	 */
	void limpiarDatos(String cgContrato, int sessionID) throws Exception;

}
