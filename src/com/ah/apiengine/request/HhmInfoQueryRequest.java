package com.ah.apiengine.request;

import java.nio.ByteBuffer;
import java.util.Collection;

import com.ah.apiengine.AbstractRequest;
import com.ah.apiengine.Element;
import com.ah.apiengine.EncodeException;
import com.ah.apiengine.response.HhmInfoQueryResponse;

public class HhmInfoQueryRequest extends AbstractRequest {

	/*
	 * Elements Required:
	 */

	private static final long serialVersionUID = 1L;

	public HhmInfoQueryRequest() {
		super();
	}

	//*********************************************************************************
    // Methods implement Message
    //*********************************************************************************

	@Override
	public int getMsgType() {
		return HHM_INFO_QUERY_REQUEST;
	}

	@Override
	public String getMsgName() {
		return "HHM Information Query Request";
	}

	@Override
	public void setElements(Collection<Element> elements) {

	}

    //*********************************************************************************
    // Methods for API-Engine to execute API-Client request and build response messages
    //*********************************************************************************

	@Override
	public ByteBuffer execute() throws EncodeException {
		HhmInfoQueryResponse response = new HhmInfoQueryResponse();

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