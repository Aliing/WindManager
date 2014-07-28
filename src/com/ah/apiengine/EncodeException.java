package com.ah.apiengine;

public class EncodeException extends Exception {

	private static final long serialVersionUID = 1L;

	public EncodeException() {
		super();
	}

	public EncodeException(String message) {
		super(message);
	}

	public EncodeException(Throwable cause) {
		super(cause);
	}

	public EncodeException(String message, Throwable cause) {
		super(message, cause);
	}

}