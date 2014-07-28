package com.ah.be.admin.auth;

/**
 * @description a class for authenticate and authorize exception
 * @author Jonathan Yu
 */
public class AhAuthException extends Exception {
	private static final long serialVersionUID = 1L;

	public AhAuthException() {
		super();
	}

	public AhAuthException(String message) {
		super(message);
	}

	public AhAuthException(Throwable cause) {
		super(cause);
	}

	public AhAuthException(String message, Throwable cause) {
		super(message, cause);
	}
}
