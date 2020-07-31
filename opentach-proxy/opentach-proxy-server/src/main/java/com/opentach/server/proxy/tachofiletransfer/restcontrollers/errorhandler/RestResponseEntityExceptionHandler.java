package com.opentach.server.proxy.tachofiletransfer.restcontrollers.errorhandler;

import java.io.Serializable;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.opentach.common.tachofiletransfer.exception.ErrorCode;
import com.opentach.common.tachofiletransfer.exception.TachoFileTransferException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = { Exception.class })
	protected ResponseEntity<Object> handleConflict(Exception ex, WebRequest request) {
		this.logger.error(null, ex);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		int code = 0;
		String message = null;
		String description = null;
		ErrorCode error = null;
		if (ex instanceof TachoFileTransferException) {
			error = ((TachoFileTransferException) ex).getCode();
		} else {
			error = ErrorCode.ERR_CODE_GENERAL;
		}
		if (error == null) {
			message = null;
			description = ex.getMessage();
		} else {
			code = error.getCode();
			message = error.getDescription();
			description = error.getTranslation();
		}
		return this.handleExceptionInternal(null, new ErrorResponse(code, message, description), headers, HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	public static class ErrorResponse implements Serializable {

		protected int		code;
		protected String	message;
		protected String	description;

		public ErrorResponse(int code, String message, String description) {
			super();
			this.code = code;
			this.message = message;
			this.description = description;
		}

		/**
		 * @return the message
		 */
		public String getMessage() {
			return this.message;
		}

		/**
		 * @param message
		 *            the message to set
		 */
		public void setMessage(String message) {
			this.message = message;
		}

		/**
		 * @return the code
		 */
		public int getCode() {
			return this.code;
		}

		public String getDescription() {
			return this.description;
		}

	}
}