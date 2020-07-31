package com.opentach.server.tachofiletransfer.restcontrollers.errorhandler;

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
		ErrorCode error = null;
		if (ex instanceof TachoFileTransferException) {
			error = ((TachoFileTransferException) ex).getCode();
		} else {
			error = ErrorCode.ERR_CODE_GENERAL;
		}
		return this.handleExceptionInternal(null, new ErrorResponse(error.getCode(), error.getTranslation()), headers, HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	public static class ErrorResponse implements Serializable {

		protected int		code;
		protected String	message;

		public ErrorResponse(int code, String message) {
			super();
			this.code = code;
			this.message = message;
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

	}
}