package com.ah.ws.rest.server.auth;

import java.io.Serializable;
import java.security.Principal;

import javax.security.auth.login.LoginException;

public class RestPrincipal implements Principal, Serializable {
	private static final long serialVersionUID = 1L;

	private String name;

	public RestPrincipal(String name) throws LoginException {
		this.name = name;
	}

	public String toString() {
		return (this.getClass().getSimpleName() + ": "+ name);
	}

	@Override
	public String getName() {
		return name;
	}

	public boolean equals(Object o) {
		if (o == null)
			return false;

		if (this == o)
			return true;

		if (!(o instanceof RestPrincipal))
			return false;
		RestPrincipal that = (RestPrincipal) o;

		if (this.getName().equals(that.getName()))
			return true;
		return false;
	}

	public int hashCode() {
		return name.hashCode();
	}
}
