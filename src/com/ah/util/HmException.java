package com.ah.util;

/*
 * @author Chris Scheers
 */
public class HmException extends Exception {

	private static final long serialVersionUID = 1L;

	protected String errorCode;

	protected String[] params;

	public HmException(String message, String errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public HmException(String message, String errorCode, String[] params) {
		super(message);
		this.errorCode = errorCode;
		this.params = params;
	}

	public HmException(String message, Throwable cause, String errorCode) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public HmException(String message, Throwable cause, String errorCode,
			String[] params) {
		super(message, cause);
		this.errorCode = errorCode;
		this.params = params;
	}

    public String[] getParams() {
        return params;
    }

}