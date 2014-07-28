package com.ah.be.communication.mo;

public class DeleteAPInfo {
	
	//used for searching condition
	private String serialNum;
	
	//used for returned information
	/**
	 * 0:success
	 * -1:failure
	 */
	private int returnCode;
	
	/**
	 * if any error occurs, no matter communication errors or others,
	 * it may store in this field
	 */
	private String errorMsg;

	public String getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}

	public int getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
}
