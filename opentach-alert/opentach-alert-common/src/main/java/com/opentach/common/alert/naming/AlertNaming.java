package com.opentach.common.alert.naming;

public final class AlertNaming {

	private AlertNaming() {
		super();
	}

	// Database Alert columns: ------------------------------------------------------
	public static final String	ALR_ID				= "ALR_ID";
	public static final String	ALR_CODE			= "ALR_CODE";
	public static final String	ALR_NAME			= "ALR_NAME";
	public static final String	ALR_ACTION			= "ALR_ACTION";
	public static final String	ALR_DESC			= "ALR_DESC";
	public static final String	ALR_PROPERTIES		= "ALR_PROPERTIES";
	public static final String	ALR_SUBJECT			= "ALR_SUBJECT";
	public static final String	ALR_BODY			= "ALR_BODY";
	public static final String	ALR_SENDMAIL		= "ALR_SENDMAIL";
	public static final String	ALR_SAVEDB			= "ALR_SAVEDB";

	// Database IndicatorExecution columns:---------------------------------------------
	public static final String	ALE_ID				= "ALE_ID";
	public static final String	ALE_NUMBER			= "ALE_NUMBER";
	public static final String	ALE_EXEDATE			= "ALE_EXEDATE";		// Cuando se hizo
	public static final String	ALE_READDATE		= "ALE_READDATE";		// Marcado como leido o no

	public static final String	ALE_ERROR			= "ALE_ERROR";			// Hubo algún problema al procesar

	public static final String	ALE_SENDMAIL		= "ALE_SENDMAIL";		// Se tenía que enviar
	public static final String	ALE_SENTMAIL		= "ALE_SENTMAIL";		// Se envió email
	public static final String	ALE_SENTMAIL_TO		= "ALE_SENTMAIL_TO";	// Destinatarios del email
	public static final String	ALE_SENTMAIL_CC		= "ALE_SENTMAIL_CC";	// Destinatarios del email (CC)
	public static final String	ALE_SENTMAIL_BCC	= "ALE_SENTMAIL_BCC";	// Destinatarios del email (BCC)

	public static final String	ALE_SAVEDB			= "ALE_SAVEDB";			// Se tenía que guardar en BBDD
	public static final String	ALE_SAVEDDB			= "ALE_SAVEDDB";		// Se guardó en BBDD
	public static final String	DRV_ID				= "DRV_ID";				// Si relacionado con un conductor
	public static final String	COM_ID				= "COM_ID";				// Si está relacionado con una empresa

	public static final String	ALE_SUBJECT			= "ALE_SUBJECT";		// Asunto enviado
	public static final String	ALE_BODY			= "ALE_BODY";			// Body enviado

	// Constants

}
