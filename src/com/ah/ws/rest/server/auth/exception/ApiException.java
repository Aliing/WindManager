package com.ah.ws.rest.server.auth.exception;

import javax.ws.rs.core.Response.Status;

public class ApiException extends Exception {
	private static final long serialVersionUID = 1L;
	private Status status;
	private int code;
	private String message = "";
	private String errorParams = "";
	private String moreInfo = "";

	public ApiException() {

	}

	public ApiException(Exception e) {
		this.status = Status.INTERNAL_SERVER_ERROR;
		initCodeByStatus(status);
		String msg = e.getMessage();
		if (msg.indexOf("ERROR:") >= 0) {
			this.message = msg.substring(msg.indexOf("ERROR:"));
		} else {
			this.message = e.getMessage();
		}
	}

	public ApiException(Status status, String message, String errorParams) {
		this.status = status;
		initCodeByStatus(status);
		this.message = message;
		this.errorParams = errorParams;
	}

	public void initCodeByStatus(Status status) {
		if (status == Status.INTERNAL_SERVER_ERROR) {
			this.code = 500;
		} else if (status == Status.BAD_REQUEST) {
			this.code = 400;
		} else if (status == Status.UNAUTHORIZED) {
			this.code = 401;
		} else {
			this.code = 404;
		}
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getErrorParams() {
		return errorParams;
	}

	public void setErrorParams(String errorParams) {
		this.errorParams = errorParams;
	}

	public String getMoreInfo() {
		return moreInfo;
	}

	public void setMoreInfo(String moreInfo) {
		this.moreInfo = moreInfo;
	}
}
