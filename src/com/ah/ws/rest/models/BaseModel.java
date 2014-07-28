package com.ah.ws.rest.models;

import java.io.Serializable;

import javax.ws.rs.core.Response.Status;

public abstract class BaseModel implements Serializable {
	private static final long serialVersionUID = 1L;

	public BaseModel() {
	}

	public BaseModel(Status status) {
		this.status = status;
	}

	private Status status;

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
}
