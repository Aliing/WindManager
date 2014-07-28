package com.ah.bo.report.exception;

public class AhReportDataTypeErrorException extends AhReportException {
	private static final long serialVersionUID = 1L;

	private static final String errorMessage = "AhReport: Data type is error.";
	
	public AhReportDataTypeErrorException() {
		super(errorMessage);
	}
	
	public AhReportDataTypeErrorException(Throwable cause) {
		super(errorMessage, cause);
	}
}
