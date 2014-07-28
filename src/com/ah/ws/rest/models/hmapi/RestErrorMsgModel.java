package com.ah.ws.rest.models.hmapi;

import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "errorMsg")
public class RestErrorMsgModel {
	private Status status;
	private int code;
	private String message = "";
	private String errorParams = "";
	private String moreInfo = "";

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
