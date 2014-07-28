package com.ah.be.config;

public class AhConfigConvertedException extends AhConfigException {

	private static final long serialVersionUID = 1L;

	public AhConfigConvertedException() {
		super();
	}

	public AhConfigConvertedException(String message) {
		super(message);
	}

	public AhConfigConvertedException(Throwable cause) {
		super(cause);
	}

	public AhConfigConvertedException(String message, Throwable cause) {
		super(message, cause);
	}

}