package com.ah.bo.report.exception;

public class AhReportNotDefinedException extends AhReportException {
	private static final long serialVersionUID = 1L;

	private static final String errorMessage = "AhReport: report is not defined for id ";
	
	@SuppressWarnings("unused")
	private AhReportNotDefinedException(){
	};
	
	public AhReportNotDefinedException(Long rpId) {
		super(errorMessage + (rpId == null? "null" : rpId.toString()));
	}
	
	public AhReportNotDefinedException(Long rpId, Throwable cause) {
		super(errorMessage + (rpId == null? "null" : rpId.toString()), cause);
	}
}
