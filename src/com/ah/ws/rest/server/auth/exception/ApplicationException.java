package com.ah.ws.rest.server.auth.exception;

import javax.ws.rs.core.Response.Status;

import com.ah.ws.rest.models.ExceptionModel;

public final class ApplicationException extends RestException {

	private static final long serialVersionUID = 1L;

	@Override
	public Object getEntity() {
		return entity;
	}

	public ApplicationException(Object entity) {
		super(Status.INTERNAL_SERVER_ERROR, entity);
	}

	public ApplicationException(Status status, Object entity) {
		super(status, entity);
	}

	public ApplicationException(String message) {
		super(Status.INTERNAL_SERVER_ERROR, new ExceptionModel(Status.INTERNAL_SERVER_ERROR, message));
	}

	public ApplicationException(Throwable cause) {
		super(Status.INTERNAL_SERVER_ERROR, new ExceptionModel(Status.INTERNAL_SERVER_ERROR, cause.getMessage()));
	}
}
