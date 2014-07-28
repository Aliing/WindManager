package com.ah.be.config;

public class AhConfigRetrievedException extends AhConfigException {

	private static final long serialVersionUID = 1L;

	public AhConfigRetrievedException() {
		super();
	}

	public AhConfigRetrievedException(String message) {
		super(message);
	}

	public AhConfigRetrievedException(Throwable cause) {
		super(cause);
	}

	public AhConfigRetrievedException(String message, Throwable cause) {
		super(message, cause);
	}

}