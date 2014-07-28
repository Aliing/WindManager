package com.ah.ui.actions.home.clientManagement.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("exception")
public class Exception4ACM {

	@XStreamAlias("httpStatusCode")
	private String httpStatusCode;
	
	@XStreamAlias("message")
	private String message;
	
	@XStreamAlias("exceptionCode")
	private String exceptionCode;
	
	@XStreamAlias("internalMessage")
	private String internalMessage;
	
	public String getHttpStatusCode() {
		return httpStatusCode;
	}

	public void setHttpStatusCode(String httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getExceptionCode() {
		return exceptionCode;
	}

	public void setExceptionCode(String exceptionCode) {
		this.exceptionCode = exceptionCode;
	}
	
	public String getInternalMessage() {
		return internalMessage;
	}

	public void setInternalMessage(String internalMessage) {
		this.internalMessage = internalMessage;
	}
	
}
