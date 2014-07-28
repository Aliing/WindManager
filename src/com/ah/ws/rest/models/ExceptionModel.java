package com.ah.ws.rest.models;

import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="exception")
public class ExceptionModel extends BaseModel {
	private static final long serialVersionUID = 1L;

	public ExceptionModel() {
	}

	private String message;

	public ExceptionModel(Status status, String message) {
		super(status);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	private int internalErrorCode;

	public int getInternalErrorCode() {
		return internalErrorCode;
	}

	public void setInternalErrorCode(int internalErrorCode) {
		this.internalErrorCode = internalErrorCode;
	}
}
