package com.opentach.common.contract;

import java.rmi.Remote;
import java.util.List;

public interface ITachoFileContractService extends Remote {
	/** The Constant ID. */
	final static String ID = "TachoFileContractService";

	/**
	 * Asigna o desasigna un fichero a un contrato en funcion de los valores de lInsert (1 asigna, otro valor desasigna).
	 *
	 * @param idFichero
	 *            the id fichero
	 * @param lContratos
	 *            the l contratos
	 * @param lInsert
	 *            the l insert
	 * @param sessionId
	 *            the session id
	 * @throws Exception
	 *             the exception
	 */
	void reassignFileToContracts(Number idFichero, List<String> lContratos, List<Number> lInsert, int sessionId) throws Exception;

	/**
	 * Try to assign contract.
	 *
	 * @param lIDFiles
	 *            the l id files
	 * @param sessionID
	 *            the session id
	 * @throws Exception
	 *             the exception
	 */
	void tryToAssignContract(List<Number> lIDFiles, int sessionID) throws Exception;
}
