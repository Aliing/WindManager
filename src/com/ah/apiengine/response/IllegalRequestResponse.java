package com.ah.apiengine.response;

import java.nio.ByteBuffer;
import java.util.Collection;

import com.ah.apiengine.AbstractResponse;
import com.ah.apiengine.Element;
import com.ah.apiengine.EncodeException;
import com.ah.apiengine.element.ExecutionResult;
import com.ah.apiengine.request.IllegalRequest;

public class IllegalRequestResponse extends AbstractResponse<IllegalRequest> {

	/*
	 * Illegal Request Response requires elements:
	 *
	 * o  Execution Result
	 */

	private static final long serialVersionUID = 1L;

	/* Execution Result */
	private ExecutionResult executionResult;

	public IllegalRequestResponse() {
   	    super();
	}

	public ExecutionResult getExecutionResult() {
		return executionResult;
	}

    //*********************************************************************************
    // Methods implement Message
    //*********************************************************************************

	@Override
	public int getMsgType() {
		return ILLEGAL_REQUEST_RESPONSE;
	}

	@Override
	public String getMsgName() {
		return "Illegal Response";
	}

	@Override
	public void setElements(Collection<Element> elements) {
		if (elements != null) {
			for (Element e : elements) {
				switch (e.getElemType()) {
					case EXEC_RESULT:
						executionResult = (ExecutionResult) e;
						break;
					default:
						break;
				}
			}
		}
	}

    //**********************************************************************************
    // Methods for API-Engine to execute API-Client request and build response messages.
    //**********************************************************************************

	@Override
	public ByteBuffer build(IllegalRequest request) throws EncodeException {
		ByteBuffer respBB = super.build(request);

		/* Header */
		int headerLen = encodeHeader(respBB);

		/* Execution Result */
		executionResult = new ExecutionResult(true, false, request.getCause());
		int erLen = executionResult.encode(respBB);

		/* Message Elements Length */
		fillPendingElementsLength(respBB, headerLen, erLen);

		return respBB;
	}

}