package com.ah.be.config;

public class AhConfigDisconnectException extends AhConfigException {

	private static final long serialVersionUID = 1L;

	public AhConfigDisconnectException() {
		super();
	}

	public AhConfigDisconnectException(String message) {
		super(message);
	}

	public AhConfigDisconnectException(Throwable cause) {
		super(cause);
	}

	public AhConfigDisconnectException(String message, Throwable cause) {
		super(message, cause);
	}
}
