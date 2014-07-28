package com.ah.apiengine.response;

import java.nio.ByteBuffer;
import java.util.Collection;

import com.ah.apiengine.AbstractResponse;
import com.ah.apiengine.Element;
import com.ah.apiengine.EncodeException;
import com.ah.apiengine.agent.HmApiEngineMastAgent;
import com.ah.apiengine.agent.HmApiEngineMastAgentImpl;
import com.ah.apiengine.agent.subagent.CommonAgent;
import com.ah.apiengine.element.ExecutionResult;
import com.ah.apiengine.element.MvResponseInfo;
import com.ah.apiengine.element.VhmMovingStatusElement;
import com.ah.apiengine.request.QueryVhmMovingStatusRequest;
import com.ah.be.common.NmsUtil;
import com.ah.util.Tracer;

public class QueryVhmMovingStatusResponse extends AbstractResponse<QueryVhmMovingStatusRequest> {

	private static final long	serialVersionUID	= 1L;

	private static final Tracer	log					= new Tracer(QueryVhmMovingStatusResponse.class);

	private VhmMovingStatusElement		vhmMovingStatus;

	public VhmMovingStatusElement getVhmMovingStatus() {
		return vhmMovingStatus;
	}

	public void setVhmMovingStatus(VhmMovingStatusElement vhmMovingStatus) {
		this.vhmMovingStatus = vhmMovingStatus;
	}

	/* Execution Result */
	private ExecutionResult	executionResult;

	public ExecutionResult getExecutionResult() {
		return executionResult;
	}

	@Override
	public String getMsgName() {
		return "Query VHM moving status response";
	}

	@Override
	public int getMsgType() {
		return QUERY_VHM_MOVING_STATUS_RESPONSE;
	}

	@Override
	public void setElements(Collection<Element> elements) {
	}

	@Override
	public ByteBuffer build(QueryVhmMovingStatusRequest request) throws EncodeException {
		boolean isSucc = false;
		String failureReason;
		Collection<MvResponseInfo> result = null;

		// Check session validity.
		boolean validSess = request.checkValidity();

		if (validSess) {
			// Check HHM working status
			boolean serviced = NmsUtil.isHmInService();

			if (serviced) {
				String flag = request.getApiString().getStr();

				if (flag.equals(QueryVhmMovingStatusRequest.CLEAR_STATUS)) {
					Object[] objs = clearStatus();
					isSucc = (Boolean) objs[0];
					failureReason = (String) objs[1];
				} else {
					Object[] objs = queryStatus();
					isSucc = (Boolean) objs[0];
					failureReason = (String) objs[1];
					result = (Collection<MvResponseInfo>) objs[2];
				}
			} else {
				failureReason = "HHM is out of service currently.";
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

		if (result != null) {
			vhmMovingStatus = new VhmMovingStatusElement();
			vhmMovingStatus.setVhmMovingInfos(result);
			erElemLen += vhmMovingStatus.encode(respBB);
		}

		/* Message Elements Length */
		fillPendingElementsLength(respBB, headerLen, erElemLen);

		return respBB;
	}

	private Object[] queryStatus() {
		boolean isSucc = false;
		String failureReason = "";
		Collection<MvResponseInfo> result = null;

		try {
			HmApiEngineMastAgent mastAgent = HmApiEngineMastAgentImpl.getInstance();
			CommonAgent agent = mastAgent.getCommonAgent();
			result = agent.doQueryVhmMovingStatus();
			isSucc = true;
		} catch (Exception e) {
			log.error("executeRequest", "Failed to call doQueryVhmMovingStatus", e);
			failureReason = e.getMessage();
		}

		return new Object[] { isSucc, failureReason, result };
	}

	private Object[] clearStatus() {
		boolean isSucc = false;
		String failureReason = "";
		Collection<MvResponseInfo> result = null;

		try {
			HmApiEngineMastAgent mastAgent = HmApiEngineMastAgentImpl.getInstance();
			CommonAgent agent = mastAgent.getCommonAgent();
			agent.doClearVhmMovingStatus();
			isSucc = true;
		} catch (Exception e) {
			log.error("executeRequest", "Failed to call doClearVhmMovingStatus", e);
			failureReason = e.getMessage();
		}

		return new Object[] { isSucc, failureReason, result };
	}
}
