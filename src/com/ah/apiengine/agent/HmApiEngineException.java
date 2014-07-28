package com.ah.apiengine.agent;

public class HmApiEngineException extends Exception {

	private static final long serialVersionUID = 1L;

	public HmApiEngineException() {
		super();
	}

	public HmApiEngineException(String message) {
		super(message);
	}

	public HmApiEngineException(Throwable cause) {
		super(cause);
	}

	public HmApiEngineException(String message, Throwable cause) {
		super(message, cause);
	}

}