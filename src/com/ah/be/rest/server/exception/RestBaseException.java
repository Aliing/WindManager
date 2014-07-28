package com.ah.be.rest.server.exception;

import com.ah.be.rest.server.models.ResultStatus;

public class RestBaseException extends Exception {
	private static final long serialVersionUID = -1776364382182632743L;
	protected ResultStatus resultStatus;
	public void setResultStatus(ResultStatus resultStatus) {
		this.resultStatus = resultStatus;
	}
	public ResultStatus getResultStatus() {
		return resultStatus;
	}
}
