package com.ah.ws.rest.server.auth.exception;

import javax.ws.rs.core.Response.Status;

public abstract class RestException extends Exception {

	public static enum RestEx {
		NOT_ACTIVE_NODE			("error.rest.notactive"),
		AUTH_CREDENTIALS_NULL	("error.rest.auth.credentials.null"),
		AUTH_BAD_SECRETKEY		("error.rest.auth.bad.secretkey"),
		AUTH_BASIC_ONLY			("error.rest.auth.basic.only"),
		AUTH_BASIC_HTTPS		("error.rest.auth.basic.https"),
		AUTH_BASIC_FORMAT		("error.rest.auth.basic.format"),
		AUTH_BASIC_SECRETKEY	("error.rest.auth.basic.secretkey"),
		AUTH_PRINCIPAL_NULL		("error.rest.auth.principal.null"),
		PERM_DENIED				("error.rest.perm.denied"),
		JAAS_CONFIG				("error.rest.jaas.config"),
		JAAS_CONFIG_DB			("error.rest.jaas.config.db");

		private final String key;

		RestEx(String key) {
			this.key = key;
		}

		@Override
		public String toString() {
			return key;
		}
	}

	public RestException() {
		super();
	}

	public RestException(String message, Throwable cause) {
		super(message, cause);
	}

	public RestException(String message) {
		super(message);
	}

	public RestException(Throwable cause) {
		super(cause);
	}

	public RestException(Status status, Object entity) {
		super();
		this.status = status;
		this.entity = entity;
	}

	private static final long serialVersionUID = 1L;

	protected Status status;

	protected Object entity;

	public Status getStatus() {
		return status;
	}

	public abstract Object getEntity();
}
