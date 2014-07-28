package com.ah.apiengine;

public class DecodeException extends Exception {

	private static final long serialVersionUID = 1L;

	public DecodeException() {
		super();
	}

	public DecodeException(String message) {
		super(message);
	}

	public DecodeException(Throwable cause) {
		super(cause);
	}

	public DecodeException(String message, Throwable cause) {
		super(message, cause);
	}

}