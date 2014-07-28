package com.ah.apiengine.request;

import java.nio.ByteBuffer;
import java.util.Collection;

import com.ah.apiengine.AbstractRequest;
import com.ah.apiengine.Element;
import com.ah.apiengine.EncodeException;
import com.ah.apiengine.element.VhmOperation;
import com.ah.apiengine.response.VhmOperationResponse;

public class VhmOperationRequest extends AbstractRequest {

	/*
	 * Elements Required:
	 *
	 * o  VHM Operation
	 */

	private static final long serialVersionUID = 1L;

	/* VHM Operation */
	private VhmOperation vhmOperation;

	public VhmOperationRequest() {
		super();
	}

	public VhmOperation getVhmOperation() {
		return vhmOperation;
	}

	public void setVhmOperation(VhmOperation vhmOperation) {
		this.vhmOperation = vhmOperation;
	}

    //*********************************************************************************
    // Methods implement Message
    //*********************************************************************************

	@Override
	public int getMsgType() {
		return VHM_OPERATION_REQUEST;
	}

	@Override
	public String getMsgName() {
		return "VHM Operation Request";
	}

	@Override
	public void setElements(Collection<Element> elements) {
		if (elements != null) {
			for (Element e : elements) {
				if (e != null) {
					switch (e.getElemType()) {
						case VHM_OPERATION:
							vhmOperation = (VhmOperation) e;
							break;
						default:
							break;
					}
				}
			}
		}
	}

    //*********************************************************************************
    // Methods for API-Engine to execute API-Client request and build response messages
    //*********************************************************************************

	@Override
	public ByteBuffer execute() throws EncodeException {
		VhmOperationResponse response = new VhmOperationResponse();

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
//			/* VHM Operation */
//			int voLen = vhmOperation.encode(reqBB);
//
//			/* Message Elements Length */
//			fillPendingElementsLength(reqBB, headerLen, voLen);
//
//			return reqBB;
//		} catch (Exception e) {
//			throw new EncodeException("Build '" + getMsgName() + "' Error.", e);
//		}
//	}

}