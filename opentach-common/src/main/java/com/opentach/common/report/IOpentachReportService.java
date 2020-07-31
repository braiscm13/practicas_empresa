package com.opentach.common.report;

import java.util.Date;
import java.util.Locale;

import com.opentach.common.activities.IInfractionService.EngineAnalyzer;

import net.sf.jasperreports.engine.JasperPrint;

/**
 * The Interface IDownCenterReport.
 */
public interface IOpentachReportService extends IReportService {
	/** The Constant ID. */
	final static String ID = "ReportService";

	/**
	 * Genera informe express cond.
	 *
	 * @param cif
	 *            the cif
	 * @param empresa
	 *            the empresa
	 * @param cgContrato
	 *            the cg contrato
	 * @param idconductor
	 *            the idconductor
	 * @param nombreConductor
	 *            the nombre conductor
	 * @param dni
	 *            the dni
	 * @param fdesde
	 *            the fdesde
	 * @param fhasta
	 *            the fhasta
	 * @param sessionID
	 *            the session id
	 * @return the jasper print
	 * @throws Exception
	 *             the exception
	 */
	JasperPrint generaInformeExpressCond(String cif, String empresa, String cgContrato, String idconductor, String nombreConductor, String dni,
			Date fdesde, Date fhasta, int sessionID) throws Exception;

	/**
	 * Genera informe express veh.
	 *
	 * @param cif
	 *            the cif
	 * @param empresa
	 *            the empresa
	 * @param cgContrato
	 *            the cg contrato
	 * @param matricula
	 *            the matricula
	 * @param fdesde
	 *            the fdesde
	 * @param fhasta
	 *            the fhasta
	 * @param sessionID
	 *            the session id
	 * @return the jasper print
	 * @throws Exception
	 *             the exception
	 */
	JasperPrint generaInformeExpressVeh(String cif, String empresa, String cgContrato, String matricula, Date fdesde, Date fhasta, int sessionID)
			throws Exception;

	/**
	 * Genera informe consulta ficheros.
	 *
	 * @param usuario_alta
	 *            the usuario_alta
	 * @param fdesde
	 *            the fdesde
	 * @param fhasta
	 *            the fhasta
	 * @param sessionID
	 *            the session id
	 * @return the jasper print
	 * @throws Exception
	 *             the exception
	 */
	JasperPrint generaInformeConsultaFicheros(String usuario_alta, Date fdesde, Date fhasta, int sessionID) throws Exception;

	/**
	 * Genera informe gestor.
	 *
	 * @param cif
	 *            the cif
	 * @param empresa
	 *            the empresa
	 * @param cgContrato
	 *            the cg contrato
	 * @param sCorreo
	 *            the s correo
	 * @param dInforme
	 *            the d informe
	 * @param analyzer
	 *            the analyzer
	 * @param locale
	 *            the locale
	 * @param send
	 *            the send
	 * @param sessionID
	 *            the session id
	 * @throws Exception
	 *             the exception
	 */
	void generateManagementReport(String cif, String empresa, String cgContrato, String sCorreo, Date dInforme, EngineAnalyzer analyzer,
			Locale locale, boolean send, int sessionID)
					throws Exception;
}
