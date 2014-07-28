package com.ah.ws.rest.server.auth.exception;

import javax.ws.rs.core.Response.Status;

import com.ah.util.MgrUtil;
import com.ah.ws.rest.models.ExceptionModel;

public final class AuthenticationException extends RestException {

	private static final long serialVersionUID = 1L;

	public AuthenticationException(Status status, Throwable cause) {
		super(cause);
		this.status = status;
		this.entity = new ExceptionModel(status, cause.getMessage());
	}

	public AuthenticationException(Status status, String message) {
		super(message);
		this.status = status;
		this.entity = new ExceptionModel(status, message);
	}

	public AuthenticationException(Status status, RestEx code, String... params) {
		super(MgrUtil.getUserMessage(code.toString(), params));
		this.status = status;
		this.entity = new ExceptionModel(status, MgrUtil.getUserMessage(code.toString(), params));
	}

	@Override
	public Object getEntity() {
		return entity;
	}
}
