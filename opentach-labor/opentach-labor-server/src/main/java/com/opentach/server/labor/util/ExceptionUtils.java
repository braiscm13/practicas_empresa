package com.opentach.server.labor.util;

import java.lang.reflect.InvocationTargetException;

import org.springframework.core.NestedRuntimeException;
import org.springframework.remoting.RemoteAccessException;

import com.ontimize.jee.common.exceptions.IParametrizedException;
import com.ontimize.jee.common.exceptions.NoTraceOntimizeJEEException;
import com.ontimize.jee.common.exceptions.OntimizeJEERuntimeException;
import com.ontimize.jee.common.tools.ReflectionTools;
import com.ontimize.jee.common.tools.proxy.InvalidDelegateException;
import com.opentach.common.exception.OpentachWrapException;

import net.sf.jasperreports.engine.JRException;

public class ExceptionUtils {

	public static Exception translateException(Throwable original) {

		Throwable error = ExceptionUtils.rescueCorrectExceptionToClient(original);
		if (error instanceof IParametrizedException) {
			IParametrizedException oee = (IParametrizedException) error;
			try {
				return (Exception) ReflectionTools.newInstance(error.getClass(), oee.getMessage(), oee.getMessageParameters(), null, oee.getMessageType(), false, false);
			} catch (Exception ex) {
				return new NoTraceOntimizeJEEException(oee.getMessage(), null, oee.getMessageParameters(), null, false, false);
			}
		}
		return new NoTraceOntimizeJEEException(error.getMessage());
	}

	protected static Throwable rescueCorrectExceptionToClient(Throwable error) {
		if ((error instanceof InvalidDelegateException) && (error.getCause() != null)) {
			return ExceptionUtils.rescueCorrectExceptionToClient(error.getCause());
		} else if ((error instanceof RemoteAccessException) && (error.getCause() != null)) {
			return ExceptionUtils.rescueCorrectExceptionToClient(error.getCause());
		} else if ((error instanceof OntimizeJEERuntimeException) && (error.getCause() != null) && (error.getMessage() == null)) {
			return ExceptionUtils.rescueCorrectExceptionToClient(error.getCause());
		} else if ((error instanceof InvocationTargetException) && (((InvocationTargetException) error).getTargetException() != null) && (error.getMessage() == null)) {
			return ExceptionUtils.rescueCorrectExceptionToClient(((InvocationTargetException) error).getTargetException());
		} else if ((error instanceof NestedRuntimeException) && (((NestedRuntimeException) error).getMostSpecificCause() != null) && (error.getMessage() == null)) {
			return ExceptionUtils.rescueCorrectExceptionToClient(((NestedRuntimeException) error).getMostSpecificCause());
		} else if ((error instanceof JRException) && (error.getCause() != null)) {
			return ExceptionUtils.rescueCorrectExceptionToClient(error.getCause());
		} else if ((error instanceof OpentachWrapException)) {
			return ExceptionUtils.rescueCorrectExceptionToClient(error.getCause());
		}
		return error;
	}

}
