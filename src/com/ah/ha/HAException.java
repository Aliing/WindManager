package com.ah.ha;

public class HAException extends Exception {

	private static final long serialVersionUID = 1L;

	public HAException() {
		super();
	}

	public HAException(String message) {
		super(message);
	}

	public HAException(String message, Throwable cause) {
		super(message, cause);
	}

	public HAException(Throwable cause) {
		super(cause);
	}

}