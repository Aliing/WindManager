package com.ah.apiengine.request;

import java.nio.ByteBuffer;
import java.util.Collection;

import com.ah.apiengine.AbstractRequest;
import com.ah.apiengine.Element;
import com.ah.apiengine.EncodeException;
import com.ah.apiengine.response.LogoutResponse;

public class LogoutRequest extends AbstractRequest {

	/*
	 * Elements Required:
	 */

	private static final long serialVersionUID = 1L;

	public LogoutRequest() {
		super();
	}

    //*********************************************************************************
    // Methods implement Message
    //*********************************************************************************

	@Override
	public int getMsgType() {
		return LOGOUT_REQUEST;
	}

	@Override
	public String getMsgName() {
		return "Logout Request";
	}

	@Override
	public void setElements(Collection<Element> elements) {

	}

    //*********************************************************************************
    // Methods for API-Engine to execute API-Client request and build response messages
    //*********************************************************************************

	@Override
	public ByteBuffer execute() throws EncodeException {
		LogoutResponse response = new LogoutResponse();

		return response.build(this);
	}

	@Override
	public void callback() {

	}

//    //*********************************************************************************
//    // Methods for API-Client to build request messages to API-Engine.
//    //*********************************************************************************
//
//	@Override
//	public ByteBuffer build() throws EncodeException {
//		ByteBuffer reqBB = super.build();
//
//		try {
//			/* Header */
//			int headerLen = encodeHeader(reqBB);
//
//			/* Message Elements Length */
//			fillPendingElementsLength(reqBB, headerLen, 0);
//
//			return reqBB;
//		} catch (Exception e) {
//			throw new EncodeException("Build '" + getMsgName() + "' Error.", e);
//		}
//	}

}