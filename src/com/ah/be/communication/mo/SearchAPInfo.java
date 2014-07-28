package com.ah.be.communication.mo;

import java.util.ArrayList;
import java.util.List;

public class SearchAPInfo {
	
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
	
	/**
	 * store the count of searching result
	 */
	private int resultCount = 0;
	
	/**
	 * only used for HMOL searching result
	 */
	private List<String> hmolResult = null;
	
	/**
	 * identify which system returns the result
	 * this field will be set by program
	 */
	private String whoReturns = "hmol";
	

	public SearchAPInfo() {
		hmolResult = new ArrayList<String>();
	}

	public String getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public int getResultCount() {
		return resultCount;
	}

	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}

	public List<String> getHmolResult() {
		return hmolResult;
	}

	public void setHmolResult(List<String> hmolResult) {
		this.hmolResult = hmolResult;
	}

	public int getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}

	public String getWhoReturns() {
		return whoReturns;
	}

	public void setWhoReturns(String whoReturns) {
		this.whoReturns = whoReturns;
	}
	
}
