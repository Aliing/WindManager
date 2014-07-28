package com.ah.ws.rest.server.auth;

import java.security.Permission;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import javax.security.auth.Subject;

public class AuthUtils {

	private AuthUtils() {
	}

	static SecurityManager sm;

	static public boolean permitted(Subject subj, final Permission p)
			throws PrivilegedActionException {
		if (p == null) {
			return false;
		}
		if (subj == null) {
			subj = new Subject();
		}

		if (sm == null)
			sm = new SecurityManager();

		Subject.doAsPrivileged(subj, new PrivilegedExceptionAction<Object>() {
			public Object run() {
				sm.checkPermission(p);
				return null;
			}
		}, null);

		return true;
	}
}
