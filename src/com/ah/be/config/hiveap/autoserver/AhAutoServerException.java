package com.ah.be.config.hiveap.autoserver;

public class AhAutoServerException extends Exception {

	private static final long serialVersionUID = 1L;

	public AhAutoServerException() {
		super();
	}
	
	public AhAutoServerException(String message){
		super(message);
	}
	
	public AhAutoServerException(Throwable cause){
		super(cause);
	}
	
	public AhAutoServerException(String message, Throwable cause){
		super(message, cause);
	}
}
