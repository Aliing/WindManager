package com.ah.be.ts.hiveap;

public class DebugException extends Exception {

	private static final long serialVersionUID = 1L;

	public DebugException() {
		super();
	}

	public DebugException(String message) {
		super(message);
	}

	public DebugException(Throwable cause) {
		super(cause);
	}

	public DebugException(String message, Throwable cause) {
		super(message, cause);
	}

}