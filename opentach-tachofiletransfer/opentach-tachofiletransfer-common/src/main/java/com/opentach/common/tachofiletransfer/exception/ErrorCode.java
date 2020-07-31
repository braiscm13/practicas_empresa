package com.opentach.common.tachofiletransfer.exception;

import java.io.Serializable;

public class ErrorCode implements Serializable {
	public static final ErrorCode	ERR_CODE_GENERAL		= new ErrorCode(-1, "E_GENERAL_FAILURE", "Error general del sistema");
	public static final ErrorCode	NO_VALID_SERVER_FOUND	= new ErrorCode(-2, "E_NO_VALID_SERVER_FOUND", "Ningún servidor aceptó la solicitud");
	public static final ErrorCode	INVALID_FILE_LENGTH		= new ErrorCode(-3, "E_INVALID_FILE_LENGTH", "Error al leer el fichero");

	private final Integer			code;
	private final String			description;
	private final String			translation;

	private ErrorCode(Integer code, String dscr, String translation) {
		super();
		this.code = code;
		this.description = dscr;
		this.translation = translation;
	}

	public Integer getCode() {
		return this.code;
	}

	public String getDescription() {
		return this.description;
	}

	public String getTranslation() {
		return this.translation;
	}
}