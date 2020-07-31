package com.opentach.common;

/**
 * Some constants
 *
 * @author rafael.lopez
 *
 */
public interface ITGDFileConstants extends OpentachFieldNames {

	// Columns
	public static final String	F_PROCESADO_FIELD	= "F_PROCESADO";
	public static final String	OBSR_FIELD			= "OBSR";
	public static final String	FILEKIND_FIELD		= "TIPO";
	public static final String	FILE_FIELD			= "FICHERO";
	public static final String	EMAIL_FIELD			= "MAIL";
	public static final String	NOTIF_FIELD			= "NOTIF_URL";
	public static final String	ANALIZAR_FIELD		= "ANALIZAR";
	public static final String	EXTRACT_DATE_FIELD	= "F_DESCARGA_DATOS";	// ANTES:
	public static final String	NOMB_GUARDADO		= "NOMB_GUARDADO";

	// Entities
	public static final String	FILE_ENTITY			= "EFicherosTGD";
	public static final String	FILECONTRACT_ENTITY	= "EFicherosContrato";
	public static final String	FILERECORD_ENTITY	= "EFicherosRegistro";

	// FILE TYPES
	public static final String	FILETYPE_TC			= "2";
	public static final String	FILETYPE_VU			= "1";

}
