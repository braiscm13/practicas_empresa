package com.opentach.common.tachofiletransfer.exception;

import com.opentach.common.exception.OpentachException;

public class TachoFileTransferException extends OpentachException {

	public static TachoFileTransferException fromCode(ErrorCode code) {
		TachoFileTransferException res = new TachoFileTransferException(code.getDescription());
		res.code = code;
		return res;
	}

	public static TachoFileTransferException fromCode(ErrorCode code, Throwable cause) {
		TachoFileTransferException res = new TachoFileTransferException(code.getDescription(), cause);
		res.code = code;
		return res;
	}

	private ErrorCode code;

	public TachoFileTransferException() {
		super();
	}

	public TachoFileTransferException(String message, Object[] msgParameters, Throwable cause) {
		super(message, msgParameters, cause);
	}

	public TachoFileTransferException(String message, Object[] msgParameters) {
		super(message, msgParameters);
	}

	public TachoFileTransferException(String message, Throwable cause) {
		super(message, cause);
	}

	public TachoFileTransferException(String message) {
		super(message);

	}

	public TachoFileTransferException(Throwable cause) {
		super(cause);
	}

	public ErrorCode getCode() {
		return this.code;
	}

}
