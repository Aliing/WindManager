package com.ah.integration;

public class IntegrationException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public IntegrationException() {
		super();
	}

	public IntegrationException(String message) {
		super(message);
	}

	public IntegrationException(Throwable cause) {
		super(cause);
	}

	public IntegrationException(String message, Throwable cause) {
		super(message, cause);
	}

}