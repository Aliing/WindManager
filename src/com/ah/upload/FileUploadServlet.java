package com.ah.upload;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.cert.X509Certificate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadException;

import com.ah.upload.handler.UploadHandlers;
import com.ah.util.Tracer;
import com.ah.util.coder.encoder.MD5Encoder;

public class FileUploadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(FileUploadServlet.class.getSimpleName());


	// ----------------------------------------------------- Instance Variables


    /**
     * Private key.
     */
	protected static final String key = "HiveManager";

	/**
	 * Realm name that is just the value wrapped in <login-config><realm-name></realm-name></login-config> in the web.xml
	 */
	protected static final String REALM_NAME = "Hive Manager Application";

	/**
	 * The MD5 message digest provider.
	 */
	protected static final MessageDigest md5Helper;


	// --------------------------------------------------------- Static Initialization


	static {
        try {
			md5Helper = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new ExceptionInInitializerError(e);
        }
	}


	// --------------------------------------------------------- Public Methods


	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String remoteAddr = getRemoteAddr(request);
		log.info("service", "Received a file upload request from " + remoteAddr);
		boolean authenticated = authenticate(request, response);

		if (authenticated) {
			String strFileType = request.getParameter(UploadHandler.REQ_PARAM_FILE_TYPE);
			String errorMsg;

			if (strFileType != null && !strFileType.trim().isEmpty()) {
				try {
					int intFileType = Integer.parseInt(strFileType.trim());
					UploadHandler uploadHandler = UploadHandlers.getHandler(intFileType);

					if (uploadHandler != null) {
						try {
							uploadHandler.execute(request);
						} catch (FileUploadException e) {
							sendErrorResponse(response, e.getMessage(), remoteAddr);
						}
					} else {
						errorMsg = "The file type '" + strFileType + "' provided was unknown to HM";
						sendErrorResponse(response, errorMsg, remoteAddr);
					}
				} catch (NumberFormatException nfe) {
					errorMsg = "The file type '" + strFileType + "' provided was not a valid numeric";
					sendErrorResponse(response, errorMsg, remoteAddr);
				}
			} else {
				errorMsg = "The parameter of " + UploadHandler.REQ_PARAM_FILE_TYPE + " must be required";
				sendErrorResponse(response, errorMsg, remoteAddr);
			}
		} else {
			log.warning("service", "Client authentication failed. Remote Address: " + remoteAddr);
		}
	}


	// ------------------------------------------------------ Protected Methods


	/**
	 * Returns the remote address through parsing the "x-forwarded-for" header from the request given.
	 *
	 * @param request Request we are processing
	 * @return the remote address gotten from the request given.
	 */
	protected String getRemoteAddr(HttpServletRequest request) {
		String remoteAddr = request.getHeader("x-forwarded-for");

//		if (remoteAddr == null || remoteAddr.isEmpty() || "unknown".equalsIgnoreCase(remoteAddr)) {
//			remoteAddr = request.getHeader("Proxy-Client-IP");
//		}
//
//		if (remoteAddr == null || remoteAddr.isEmpty() || "unknown".equalsIgnoreCase(remoteAddr)) {
//			remoteAddr = request.getHeader("WL-Proxy-Client-IP");
//		}

		if (remoteAddr == null || remoteAddr.isEmpty() || "unknown".equalsIgnoreCase(remoteAddr)) {
			remoteAddr = request.getRemoteAddr();
		}

		return remoteAddr;
	}

   /**
     * Authenticate the user making this request, based on the specified
     * login configuration.  Return <code>true</code> if any specified
     * constraint has been satisfied, or <code>false</code> if we have
     * created a response challenge already.
     *
     * @param request Request we are processing
     * @param response Response we are creating
     * @throws IOException if an input/output error occurs
	 * @return -
     */
	protected boolean authenticate(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// Validate any credentials already included with this request.
		String authorization = request.getHeader("authorization");

		if (authorization != null) {// Basic/Digest Authentication
			if (log.getLogger().isDebugEnabled()) {
				log.debug("authenticate", "Authorization Header: " + authorization);
			}

			return true;
		} else {// Certificate Authentication
			X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");

			if (certs != null && certs.length > 0) {
				if (log.getLogger().isDebugEnabled()) {
					Principal principal = certs[0].getSubjectDN();
					log.debug("authenticate", "User principal subject DN: " + principal.getName());
				}

				return true;
			}
		}

		// Send an "unauthorized" response and an appropriate challenge

		// Next, generate a nOnce token (that is a token which is supposed to be unique).
		String nOnce = generateNOnce(request);
		setAuthenticateHeader(request, response, REALM_NAME, nOnce);
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED);

		return false;
	}

    /**
     * Generate a unique token. The token is generated according to the
     * following pattern. NOnceToken = Base64 ( MD5 ( client-IP ":"
     * time-stamp ":" private-key ) ).
     *
     * @param request HTTP Servlet request
	 * @return generated nonce value
     */
	protected String generateNOnce(HttpServletRequest request) {
		long currentTime = System.currentTimeMillis();
		String nOnceValue = request.getRemoteAddr() + ":" + currentTime + ":" + key;
		byte[] buffer;

		synchronized (md5Helper) {
			buffer = md5Helper.digest(nOnceValue.getBytes());
		}

		return MD5Encoder.digestToString(buffer);
	}

    /**
     * Generates the WWW-Authenticate header.
     * <p>
     * The header MUST follow this template :
     * <pre>
     *      WWW-Authenticate    = "WWW-Authenticate" ":" "Digest"
     *                            digest-challenge
     *
     *      digest-challenge    = 1#( realm | [ domain ] | nOnce |
     *                  [ digest-opaque ] |[ stale ] | [ algorithm ] )
     *
     *      realm               = "realm" "=" realm-value
     *      realm-value         = quoted-string
     *      domain              = "domain" "=" <"> 1#URI <">
     *      nonce               = "nonce" "=" nonce-value
     *      nonce-value         = quoted-string
     *      opaque              = "opaque" "=" quoted-string
     *      stale               = "stale" "=" ( "true" | "false" )
     *      algorithm           = "algorithm" "=" ( "MD5" | token )
     * </pre>
     *
     * @param request HTTP Servlet request
     * @param response HTTP Servlet response
     * @param realmName realm name.
     * @param nOnce nonce token
     */
    protected void setAuthenticateHeader(HttpServletRequest request, HttpServletResponse response, String realmName, String nOnce) {
		if (realmName == null) {
			realmName = request.getServerName() + ":" + request.getServerPort();
		}

		byte[] buffer;

		synchronized (md5Helper) {
			buffer = md5Helper.digest(nOnce.getBytes());
		}

		String authenticateHeader = "Digest realm=\"" + realmName + "\", "
			+  "qop=\"auth\", nonce=\"" + nOnce + "\", " + "opaque=\""
			+ MD5Encoder.digestToString(buffer) + "\"";

		if (log.getLogger().isDebugEnabled()) {
			log.debug("setAuthenticateHeader", "Authenticate Header: \n" + authenticateHeader);
		}

		response.setHeader("WWW-Authenticate", authenticateHeader);
    }

	protected void sendErrorResponse(HttpServletResponse response, String errorMsg, String remoteAddr) throws IOException {
		log.error("sendErrorResponse", "Remote Address: " + remoteAddr + "; " + errorMsg);
		response.sendError(HttpServletResponse.SC_BAD_REQUEST, errorMsg);
	}

}