package com.ah.apiengine.response;

import java.nio.ByteBuffer;
import java.util.Collection;

import com.ah.apiengine.AbstractResponse;
import com.ah.apiengine.Element;
import com.ah.apiengine.EncodeException;
import com.ah.apiengine.agent.HmApiEngineMastAgent;
import com.ah.apiengine.agent.HmApiEngineMastAgentImpl;
import com.ah.apiengine.element.ExecutionResult;
import com.ah.apiengine.element.VhmOperation;
import com.ah.apiengine.request.VhmOperationRequest;
import com.ah.be.common.NmsUtil;
import com.ah.util.Tracer;

public class VhmOperationResponse extends AbstractResponse<VhmOperationRequest> {

	/*
	 * Elements Required:
	 *
	 * o  Execution Result
	 */

	private static final long serialVersionUID = 1L;

	private static final Tracer	log = new Tracer(VhmOperationResponse.class.getSimpleName());

	/* Execution Result */
	private ExecutionResult executionResult;

	public VhmOperationResponse() {
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
		return VHM_OPERATION_RESPONSE;
	}

	@Override
	public String getMsgName() {
		return "VHM Operation Response";
	}

	@Override
	public void setElements(Collection<Element> elements) {
		if (elements != null) {
			for (Element e : elements) {
				if (e != null) {
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
	}

    //*********************************************************************************
    // Methods for API-Engine to execute API-Client request and build response messages
    //*********************************************************************************

	@Override
	public ByteBuffer build(VhmOperationRequest request) throws EncodeException {
		boolean isSucc = false;
		String failureReason;

		// Check session validity.
		boolean validSess = request.checkValidity();

		if (validSess) {
			VhmOperation operation = request.getVhmOperation();

			// Check necessary elements.
			if (operation != null) {
				// Check HHM working status
				boolean serviced = NmsUtil.isHmInService();

				if (serviced) {
					Object[] objs = executeRequest(operation);
					isSucc = (Boolean) objs[0];
					failureReason = (String) objs[1];
				} else {
					failureReason = "HHM is out of service currently.";
				}
			} else {
				String elemName = new VhmOperation().getElemName();
				failureReason =  "'" + elemName + "' must be a required element.";
			}
		} else {
			failureReason = "Session must have expired.";
		}

		if (!isSucc) {
			log.error("build", failureReason);
		}

		ByteBuffer respBB = super.build(request);

		/* Header */
		int headerLen = encodeHeader(respBB);

		/* Execution Result */
		executionResult = new ExecutionResult(true, isSucc, failureReason);
		int erElemLen = executionResult.encode(respBB);

		/* Message Elements Length */
		fillPendingElementsLength(respBB, headerLen, erElemLen);

		return respBB;
	}

	private Object[] executeRequest(VhmOperation operation) {
		boolean isSucc = false;
		String failureReason = "";

		try {
			HmApiEngineMastAgent mastAgent = HmApiEngineMastAgentImpl.getInstance();
			mastAgent.getVhmAgent().execute(operation);
			isSucc = true;
		} catch (Exception e) {
			log.error("executeRequest", "Failed to execute " + operation.getElemName(), e);
			failureReason = e.getMessage();
		}

		return new Object[] { isSucc, failureReason };
	}

}