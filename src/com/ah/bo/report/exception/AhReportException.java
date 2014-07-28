package com.ah.bo.report.exception;

public abstract class AhReportException extends Exception {
	private static final long serialVersionUID = 1L;
	
	protected AhReportException() {
		super("AhReport error.");
	}
	
	public AhReportException(String message) {
		super(message);
	}
	
	public AhReportException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
