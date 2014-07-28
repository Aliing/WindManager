package com.ah.be.sn.operation;

public class Status {
	
	/**
	 * true:success
	 * false:failure
	 */
	private boolean isSuccess;
	
	/**
	 * if any error occurs, no matter communication errors or others,
	 * it may store in this field
	 */
	private String errorMsg;

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}
