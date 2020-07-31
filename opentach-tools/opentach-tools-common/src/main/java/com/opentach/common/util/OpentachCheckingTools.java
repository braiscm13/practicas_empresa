package com.opentach.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.exceptions.OntimizeJEEException;
import com.ontimize.jee.common.tools.ReflectionTools;

public final class OpentachCheckingTools {
	private OpentachCheckingTools() {
		super();
	}

	/**
	 * Metodo para chequear si un {@link EntityResult} es incorrecto. Permite encapsular la excepcion con un mensaje concreto.
	 *
	 * @param res
	 * @throws Exception
	 *             Cuando o es reaultado NULL o no es correcto.
	 */
	public static <T extends Throwable> void checkValidEntityResult(EntityResult rs, Class<T> exceptionClass, String messageFormat, Object... messageParameters) throws T {
		try {
			if (rs == null) {
				throw new OntimizeJEEException("EMPTY_RESULT");
			} else if (rs.getCode() == EntityResult.OPERATION_WRONG) {
				throw new OntimizeJEEException(rs.getMessage(), rs.getMessageParameter());
			}
		} catch (OntimizeJEEException ex) {
			String message = ex.getMessage();
			if (messageFormat != null) {
				message = (messageParameters == null) || (messageParameters.length == 0) ? messageFormat : String.format(messageFormat, messageParameters);
			}
			throw ReflectionTools.newInstance(exceptionClass, message);
		}
	}

	/**
	 * Metodo para chequear si un {@link EntityResult} es incorrecto.
	 *
	 * @param res
	 * @throws Exception
	 *             Cuando o es reaultado NULL o no es correcto.
	 */
	public static <T extends Throwable> void checkValidEntityResult(EntityResult res, Class<T> exceptionClass) throws T {
		OpentachCheckingTools.checkValidEntityResult(res, exceptionClass, null);
	}

	/**
	 * Metodo para chequear si un {@link EntityResult} es incorrecto. Permite encapsular la excepcion con un mensaje concreto.
	 *
	 * @param res
	 * @throws Exception
	 *             Cuando o es reaultado NULL o no es correcto.
	 */
	public static <T extends Throwable> void checkValidEntityResult(EntityResult res, Class<T> exceptionClass, String messageFormat, boolean checkSomeRecordRequired,
			boolean checkOnlyOneRecord, Object... messageParameters) throws T {
		OpentachCheckingTools.checkValidEntityResult(res, exceptionClass, messageFormat, messageParameters);
		try {
			int num = res.calculateRecordNumber();
			if (checkSomeRecordRequired && (num <= 0)) {
				throw new OntimizeJEEException("NOT_AVAILABLE_DATA");
			}
			if (checkOnlyOneRecord && (num != 1)) {
				throw new OntimizeJEEException("NOT_ONLY_ONE_RECORD");
			}

		} catch (Exception ex) {
			String message = ex.getMessage();
			if (messageFormat != null) {
				message = (messageParameters == null) || (messageParameters.length == 0) ? messageFormat : String.format(messageFormat, messageParameters);
			}
			throw ReflectionTools.newInstance(exceptionClass, message);
		}
	}

	public static String getStackTrace(Throwable ex) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		return sw.toString();
	}
}
