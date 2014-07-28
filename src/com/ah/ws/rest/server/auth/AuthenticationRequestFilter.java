package com.ah.ws.rest.server.auth;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringUtils;

import com.ah.ha.HAUtil;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.ws.rest.server.auth.exception.ApplicationException;
import com.ah.ws.rest.server.auth.exception.AuthenticationException;
import com.ah.ws.rest.server.auth.exception.RestException;
import com.ah.ws.rest.server.auth.exception.RestException.RestEx;
import com.ah.ws.rest.server.auth.perms.MethodPermission;
import com.sun.jersey.api.container.MappableContainerException;
import com.sun.jersey.core.util.Base64;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

public class AuthenticationRequestFilter implements ContainerRequestFilter {

	private static final Tracer log	= new Tracer(AuthenticationRequestFilter.class.getSimpleName());

    private final static String LOGIN_MODULE_NAME = "AuthLoginModule";

    public ContainerRequest filter(ContainerRequest request) {
    	if (HAUtil.isSlave()) {
			throw new MappableContainerException(new ApplicationException(
					MgrUtil.getUserMessage(RestEx.NOT_ACTIVE_NODE.toString())));
    	}
    	LoginContext lc = null;

    	// authorize user access for specific URI
    	try {
			authorize(authenticate(request, lc), request);
		} catch (RestException e) {
			throw new MappableContainerException(e);
		} catch (Exception e) {
			throw new MappableContainerException(e);
		} finally {
			if (lc != null)
				try {
					lc.logout();
				} catch (LoginException le) {
					throw new MappableContainerException(
							new AuthenticationException(Status.UNAUTHORIZED, le));
				}
		}
        return request;
    }

    private void authorize(LoginContext lc, ContainerRequest request) throws RestException {
    	try {
			log.info("AuthenticationRequestFilter.authorize()",
					"API calling with method '" + request.getMethod()
							+ "' on path '" + request.getPath() + "'");
			AuthUtils.permitted(lc.getSubject(), new MethodPermission("/" + request.getPath(), request.getMethod()));
		} catch (Exception e) {
			throw new AuthenticationException(Status.FORBIDDEN,
					RestEx.PERM_DENIED, new String[] {
							"/" + request.getPath(), request.getMethod() });
		}
	}

	private LoginContext authenticate(ContainerRequest request, LoginContext lc) throws RestException {
        // Extract authentication credentials
        String authentication = request.getHeaderValue(ContainerRequest.AUTHORIZATION);
        if (authentication == null) {
            throw new AuthenticationException(Status.UNAUTHORIZED, RestEx.AUTH_CREDENTIALS_NULL);
        }

        if (!authentication.startsWith("Basic ")) {
        	throw new AuthenticationException(Status.UNAUTHORIZED, RestEx.AUTH_BASIC_ONLY);
        }

        if (!"https".equalsIgnoreCase(request.getBaseUri().getScheme())) {
        	throw new AuthenticationException(Status.UNAUTHORIZED, RestEx.AUTH_BASIC_HTTPS);
        }

        authentication = authentication.substring("Basic ".length());
        String[] values = new String(Base64.base64Decode(authentication)).split(":");
        if (values.length < 2) {
            throw new AuthenticationException(Status.UNAUTHORIZED, RestEx.AUTH_BASIC_FORMAT);
        }

        String username = values[0];
        String secretKey = values[1];
        if (secretKey == null || StringUtils.isBlank(secretKey)) {
        	throw new AuthenticationException(Status.UNAUTHORIZED, RestEx.AUTH_BASIC_SECRETKEY);
        }

        // Validate the extracted credentials
		try {
			AuthCallBackHandler callback = new AuthCallBackHandler(username, secretKey);

			if (lc == null) {
				lc = new LoginContext(LOGIN_MODULE_NAME, callback);
				lc.login();
			}
		} catch (LoginException le) {
			if (lc != null) {
				try {
					lc.logout();
				} catch (Exception e) {
				}
			}
			throw new AuthenticationException(Status.UNAUTHORIZED, le);
		} catch (SecurityException se) {
			if (lc != null) {
				try {
					lc.logout();
				} catch (Exception e) {
				}
			}
			throw new AuthenticationException(Status.UNAUTHORIZED, se);
		}

        return lc;
    }
}
