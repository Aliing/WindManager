package com.ah.apiengine.response;

import java.nio.ByteBuffer;
import java.util.Collection;

import com.ah.apiengine.AbstractResponse;
import com.ah.apiengine.Element;
import com.ah.apiengine.EncodeException;
import com.ah.apiengine.agent.HmApiEngineMastAgent;
import com.ah.apiengine.agent.HmApiEngineMastAgentImpl;
import com.ah.apiengine.agent.subagent.HhmAgent;
import com.ah.apiengine.element.ExecutionResult;
import com.ah.apiengine.element.HhmInfo;
import com.ah.apiengine.element.HhmList;
import com.ah.apiengine.request.UpdateHhmListRequest;
import com.ah.be.common.NmsUtil;
import com.ah.util.Tracer;

public class UpdateHhmListResponse extends AbstractResponse<UpdateHhmListRequest> {

	/*
	 * Elements Required:
	 *
	 * o  Execution Result
	 * o  HHM Info
	 */

	private static final long serialVersionUID = 1L;

	private static final Tracer	log = new Tracer(UpdateHhmListResponse.class.getSimpleName());

	/* Execution Result */
	private ExecutionResult executionResult;

	/* HHM Info */
	private HhmInfo hhmInfo;

	public UpdateHhmListResponse() {
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
		return UPDATE_HHM_LIST_RESPONSE;
	}

	@Override
	public String getMsgName() {
		return "Update HHM List Response";
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
	public ByteBuffer build(UpdateHhmListRequest request) throws EncodeException {
		boolean isSync = true;
		boolean isSucc = false;
		String failureReason;
		boolean validSess = request.checkValidity();

		if (validSess) {
			HhmList hhmList = request.getHhmList();

			// Check necessary elements.
			if (hhmList != null) {
				// Check HHM working status
				boolean isHmInService = NmsUtil.isHmInService();

				if (isHmInService) {
					Object[] objs = executeRequest(hhmList);
					isSucc = (Boolean) objs[0];
					failureReason = (String) objs[1];
				} else {
					failureReason = "HHM is out of service currently.";
				}
			} else {
				String elemName = new HhmList().getElemName();
				failureReason =  "'" + elemName + "' must be a required element.";
			}
		} else {
			failureReason = "Session must have expired.";
		}

		ByteBuffer respBB = super.build(request);

		/* Header */
		int headerLen = encodeHeader(respBB);

		/* Execution Result */
		executionResult = new ExecutionResult(isSync, isSucc, failureReason);
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

	private Object[] executeRequest(HhmList hhmList) {
		boolean isSucc = false;
		String failureReason = "";

		try {
			HmApiEngineMastAgent mastAgent = HmApiEngineMastAgentImpl.getInstance();
			HhmAgent hhmAgent = mastAgent.getHhmAgent();
			hhmAgent.updateHhmList(hhmList);
			hhmInfo = hhmAgent.queryHhmInfo();
			isSucc = true;
		} catch (Exception e) {
			log.error("executeRequest", "Failed to execute " + hhmList.getElemName(), e);
			failureReason = e.getMessage();
		}

		return new Object[] { isSucc, failureReason };
	}

}