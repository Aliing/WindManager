package com.ah.apiengine.response;

import java.nio.ByteBuffer;
import java.util.Collection;

import com.ah.apiengine.AbstractResponse;
import com.ah.apiengine.Element;
import com.ah.apiengine.EncodeException;
import com.ah.apiengine.agent.HmApiEngineMastAgent;
import com.ah.apiengine.agent.HmApiEngineMastAgentImpl;
import com.ah.apiengine.element.ExecutionResult;
import com.ah.apiengine.element.HhmInfo;
import com.ah.apiengine.request.HhmInfoQueryRequest;
import com.ah.be.common.NmsUtil;
import com.ah.util.Tracer;

public class HhmInfoQueryResponse extends AbstractResponse<HhmInfoQueryRequest> {

	/*
	 * Elements Required:
	 *
	 * o  Execution Result
	 * o  HHM Info
	 */

	private static final long serialVersionUID = 1L;

	private static final Tracer	log = new Tracer(HhmInfoQueryResponse.class.getSimpleName());

	/* Execution Result */
	private ExecutionResult executionResult;

	/* HHM Info */
	private HhmInfo hhmInfo;

	public HhmInfoQueryResponse() {
		super();
	}

	public ExecutionResult getExecutionResult() {
		return executionResult;
	}

	public HhmInfo getHhmInfo() {
		return hhmInfo;
	}

	//*********************************************************************************
    // Methods implement Message
    //*********************************************************************************

	@Override
	public int getMsgType() {
		return HHM_INFO_QUERY_RESPONSE;
	}

	@Override
	public String getMsgName() {
		return "HHM Information Query Response";
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
						case HHM_INFO:
							hhmInfo = (HhmInfo) e;
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
	public ByteBuffer build(HhmInfoQueryRequest request) throws EncodeException {
		boolean isSucc = false;
		String failureReason;

		// Check session validity.
		boolean validSess = request.checkValidity();

		if (validSess) {
			// Check HHM working status
			boolean serviced = NmsUtil.isHmInService();

			if (serviced) {
				Object[] objs = executeRequest();
				isSucc = (Boolean) objs[0];
				failureReason = (String) objs[1];
			} else {
				failureReason = "HHM is out of service currently.";
			}
		} else {
			failureReason = "Session must have expired.";
		}

		ByteBuffer respBB = super.build(request);

		/* Header */
		int headerLen = encodeHeader(respBB);

		/* Execution Result */
		executionResult = new ExecutionResult(true, isSucc, failureReason);
		int erElemLen = executionResult.encode(respBB);

		/* HHM Info */
		int hiElemLen = 0;

		if (isSucc && hhmInfo != null) {
			hiElemLen = hhmInfo.encode(respBB);
		} else {
			log.error("build", failureReason);
		}

		/* Message Elements Length */
		fillPendingElementsLength(respBB, headerLen, erElemLen + hiElemLen);

		return respBB;
	}

	private Object[] executeRequest() {
		boolean isSucc = false;
		String failureReason = "";

		try {
			HmApiEngineMastAgent mastAgent = HmApiEngineMastAgentImpl.getInstance();
			hhmInfo = mastAgent.getHhmAgent().queryHhmInfo();
			isSucc = true;
		} catch (Exception e) {
			log.error("executeRequest", "Failed to query HHM Information.", e);
			failureReason = e.getMessage();
		}

		return new Object[] { isSucc, failureReason };
	}

}