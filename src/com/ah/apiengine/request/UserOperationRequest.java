package com.ah.apiengine.request;

import java.nio.ByteBuffer;
import java.util.Collection;

import com.ah.apiengine.AbstractRequest;
import com.ah.apiengine.Element;
import com.ah.apiengine.EncodeException;
import com.ah.apiengine.element.UserOperation;
import com.ah.apiengine.response.UserOperationResponse;

public class UserOperationRequest extends AbstractRequest {

	/*
	 * Elements Required:
	 *
	 * o  User Operation
	 */

	private static final long serialVersionUID = 1L;

	/* User Operation */
	private UserOperation userOperation;

	public UserOperationRequest() {
		super();
	}

	public UserOperation getUserOperation() {
		return userOperation;
	}

	public void setUserOperation(UserOperation userOperation) {
		this.userOperation = userOperation;
	}

    //*********************************************************************************
    // Methods implement Message
    //*********************************************************************************

	@Override
	public int getMsgType() {
		return USER_OPERATION_REQUEST;
	}

	@Override
	public String getMsgName() {
		return "User Operation Request";
	}

	@Override
	public void setElements(Collection<Element> elements) {
		if (elements != null) {
			for (Element e : elements) {
				if (e != null) {
					switch (e.getElemType()) {
						case USER_OPERATION:
							userOperation = (UserOperation) e;
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
		UserOperationResponse response = new UserOperationResponse();

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
//			/* User Operation */
//			int uoLen = userOperation.encode(reqBB);
//
//			/* Message Elements Length */
//			fillPendingElementsLength(reqBB, headerLen, uoLen);
//
//			return reqBB;
//		} catch (Exception e) {
//			throw new EncodeException("Build '" + getMsgName() + "' Error.", e);
//		}
//	}

}