package com.opentach.common.company.naming;

public final class LicenseAlertNaming {

	private LicenseAlertNaming() {
		// Do nothing
	}

	// Cuando una empresa solicita a Openservices más info sobre una licencia -> Enviar a Openservices
	public static final String	ALERT_CODE__LICENSE_REQUEST_INFO	= "LICENCIA.MAS_INFO";
	public static final String	LICENCIA							= "LICENCIA";
	public static final String	FECHA								= "FECHA";
	public static final String	EMPRESA								= "EMPRESA";

	// Inicio de contrato -> avisar a cliente ¿con copia a Openservices?
	public static final String	ALERT_CODE__LICENSE_START_OPENTACH		= "LICENCIA.CONTRATAR.OPENTACH";
	public static final String	ALERT_CODE__LICENSE_START_TACHOLAB		= "LICENCIA.CONTRATAR.TACHOLAB";
	public static final String	ALERT_CODE__LICENSE_START_TACHOLABPLUS	= "LICENCIA.CONTRATAR.TACHOLABPLUS";

	// Rescindir contrato -> avisar a cliente ¿con copia a Openservices?
	public static final String	ALERT_CODE__LICENSE_END_OPENTACH		= "LICENCIA.RESCINDIR.OPENTACH";
	public static final String	ALERT_CODE__LICENSE_END_TACHOLAB		= "LICENCIA.RESCINDIR.TACHOLAB";
	public static final String	ALERT_CODE__LICENSE_END_TACHOLABPLUS	= "LICENCIA.RESCINDIR.TACHOLABPLUS";

	// Inicio de demo -> avisar a cliente ¿con copia a Openservices?
	public static final String	ALERT_CODE__DEMO_START_OPENTACH			= "LICENCIA.INICIO_DEMO.OPENTACH";
	public static final String	ALERT_CODE__DEMO_START_TACHOLAB			= "LICENCIA.INICIO_DEMO.TACHOLAB";
	public static final String	ALERT_CODE__DEMO_START_TACHOLABPLUS		= "LICENCIA.INICIO_DEMO.TACHOLABPLUS";

	// Rescindir demo -> avisar a cliente ¿con copia a Openservices?
	public static final String	ALERT_CODE__DEMO_END_OPENTACH			= "LICENCIA.FIN_DEMO.OPENTACH";
	public static final String	ALERT_CODE__DEMO_END_TACHOLAB			= "LICENCIA.FIN_DEMO.TACHOLAB";
	public static final String	ALERT_CODE__DEMO_END_TACHOLABPLUS		= "LICENCIA.FIN_DEMO.TACHOLABPLUS";

	public static final String	FROM									= "FROM";
	public static final String	TO										= "TO";
	public static final String	USER									= "USER";
	public static final String	PASSWORD								= "PASSWORD";

}
