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
import com.ah.apiengine.element.StringList;
import com.ah.apiengine.request.SendEmailListRequest;
import com.ah.be.common.NmsUtil;
import com.ah.util.Tracer;

public class SendEmailListResponse extends AbstractResponse<SendEmailListRequest> {

	private static final long	serialVersionUID	= 1L;

	private static final Tracer	log					= new Tracer(SendEmailListResponse.class
															.getSimpleName());

	/* Execution Result */
	private ExecutionResult		executionResult;

	public ExecutionResult getExecutionResult() {
		return executionResult;
	}

	@Override
	public String getMsgName() {
		return "get send email list response";
	}

	@Override
	public int getMsgType() {
		return SEND_DENY_MAIL_LIST_RESPONSE;
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

	@Override
	public ByteBuffer build(SendEmailListRequest request) throws EncodeException {
		boolean isSucc = false;
		String failureReason;

		// Check session validity.
		boolean validSess = request.checkValidity();

		if (validSess) {
			StringList element1 = request.getStrlist();

			// Check necessary elements.
			if (element1 != null) {
				// Check HHM working status
				boolean serviced = NmsUtil.isHmInService();

				if (serviced) {
					Object[] objs = executeRequest(element1);
					isSucc = (Boolean) objs[0];
					failureReason = (String) objs[1];
				} else {
					failureReason = "HHM is out of service currently.";
				}
			} else {
				String elemName = new StringList().getElemName();
				failureReason = "'" + elemName + "' must be a required element.";
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

	private Object[] executeRequest(StringList maillist) {
		boolean isSucc = false;
		String failureReason = "";

		try {
			HmApiEngineMastAgent mastAgent = HmApiEngineMastAgentImpl.getInstance();
			HhmAgent hhmAgent = mastAgent.getHhmAgent();
			hhmAgent.updateDenyEmailInfo(maillist);
			isSucc = true;
		} catch (Exception e) {
			log.error("executeRequest", "Failed to execute " + maillist.getElemName(), e);
			failureReason = e.getMessage();
		}

		return new Object[] { isSucc, failureReason };
	}
}
