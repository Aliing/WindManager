package com.ah.apiengine.response;

import java.nio.ByteBuffer;
import java.util.Collection;

import javax.servlet.http.HttpSession;

import com.ah.apiengine.AbstractResponse;
import com.ah.apiengine.Element;
import com.ah.apiengine.EncodeException;
import com.ah.apiengine.request.LogoutRequest;
import com.ah.util.Tracer;

public class LogoutResponse extends AbstractResponse<LogoutRequest> {

	/*
	 * Elements Required:
	 */

	private static final long serialVersionUID = 1L;

	private static final Tracer	log = new Tracer(LogoutResponse.class.getSimpleName());

    //*********************************************************************************
    // Methods implement Message
    //*********************************************************************************

	@Override
	public int getMsgType() {
		return LOGOUT_RESPONSE;
	}

	@Override
	public String getMsgName() {
		return "Logout Response";
	}

	@Override
	public void setElements(Collection<Element> elements) {

	}

    //*********************************************************************************
    // Methods for API-Engine to execute API-Client request and build response messages
    //*********************************************************************************

	@Override
	public ByteBuffer build(LogoutRequest request) throws EncodeException {
		HttpSession session = request.getHttpServletRequest().getSession(false);

		if (session != null) {
			// Invalidate Session.
			log.info("build", "Logging out and invalidating session '" + session.getId() + "'");
			session.invalidate();
		}

		ByteBuffer respBB = super.build(request);

		/* Header */
		int headerLen = encodeHeader(respBB);

		/* Message Elements Length */
		fillPendingElementsLength(respBB, headerLen, 0);

		return respBB;
	}

}