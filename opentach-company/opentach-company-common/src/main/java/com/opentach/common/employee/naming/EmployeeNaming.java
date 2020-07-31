package com.opentach.common.employee.naming;

public final class EmployeeNaming {

	private EmployeeNaming() {
		// Do nothing
	}

	/** The Constant ID. */
	public static final String	ID						= "ConductoresEmpService";

	/** Name of the entity. */
	public static final String	ENTITY					= "EConductoresEmp";

	// Database columns:
	public static final String	DNI						= "DNI";
	public static final String	NOMBRE					= "NOMBRE";
	public static final String	APELLIDOS				= "APELLIDOS";
	public static final String	CIF						= "CIF";
	public static final String	F_ALTA					= "F_ALTA";
	public static final String	USUARIO_ALTA			= "USUARIO_ALTA";
	public static final String	MOVIL					= "MOVIL";
	public static final String	EMAIL					= "EMAIL";
	public static final String	IDCONDUCTOR				= "IDCONDUCTOR";
	public static final String	EXTERNAL_EMPLOYEE_ID	= "EXTERNAL_EMPLOYEE_ID";
	public static final String	OBSR					= "OBSR";
	public static final String	IDDELEGACION			= "IDDELEGACION";
	public static final String	DURMIENTE				= "DURMIENTE";
	public static final String	NOMBRECOMPLETO			= "NOMBRECOMPLETO";
	public static final String	F_BAJA					= "F_BAJA";
	public static final String	EXPIRED_DATE_TRJCONDU	= "EXPIRED_DATE_TRJCONDU";
	public static final String	NOMBRE_EMPRESA			= "NOMBRE_EMPRESA";
	public static final String	F_NAC					= "F_NAC";
	public static final String	PHOTO					= "PHOTO";
	public static final String	ENVIAR_A				= "ENVIAR_A";
	public static final String	NAF						= "NAF";
	public static final String	CCC						= "CCC";
	public static final String	TYPE					= "TYPE";
	public static final String	DESCRIP					= "DESCRIP";
	public static final String	COMPANY_CALENDAR		= "COMPANY_CALENDAR";
	public static final String	PARENT_CALENDAR			= "PARENT_CALENDAR";
	public static final String	CALENDAR				= "CALENDAR";

	public static final String	WEB_DOMAIN				= "WEB_DOMAIN";
	public static final String	WEB_USER				= "WEB_USER";
	public static final String	WEB_PASSWORD			= "WEB_PASSWORD";
	public static final String	WEB_INACTIVE			= "WEB_INACTIVE";

	/**
	 * Identifies which value of AGR_ID designs the default work agreement (used for all employees except drivers with a tachograph). Must match that
	 * value.
	 */
	public static final int		DEFAULT_AGR_ID			= -1;

	/** Auxiliar field (used in new tables replacing IDCONDUCTOR) */
	public static final String	DRV_ID					= "DRV_ID";

}