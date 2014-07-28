package com.ah.apiengine.response;

import java.nio.ByteBuffer;
import java.util.Collection;

import javax.servlet.http.HttpSession;

import com.ah.apiengine.AbstractResponse;
import com.ah.apiengine.Element;
import com.ah.apiengine.EncodeException;
import com.ah.apiengine.agent.HmApiEngineMastAgent;
import com.ah.apiengine.agent.HmApiEngineMastAgentImpl;
import com.ah.apiengine.element.ExecutionResult;
import com.ah.apiengine.element.Login;
import com.ah.apiengine.element.Session;
import com.ah.apiengine.request.LoginRequest;
import com.ah.be.common.NmsUtil;
import com.ah.util.Tracer;

public class LoginResponse extends AbstractResponse<LoginRequest> {

	/*
	 * Elements Required:
	 *
	 * o  Execution Result
	 * o  Session
	 */

	private static final long serialVersionUID = 1L;

	private static final Tracer	log = new Tracer(LoginResponse.class.getSimpleName());

	// Session max inactive interval in seconds.
	private static final int SESSION_MAX_INACTIVE_INTERVAL = 180;

	/* Execution Result */
	private ExecutionResult executionResult;

	/* Session */
	private Session session;

	public ExecutionResult getExecutionResult() {
		return executionResult;
	}

	public Session getSession() {
		return session;
	}

    //*********************************************************************************
    // Methods implement Message
    //*********************************************************************************

	@Override
	public int getMsgType() {
		return LOGIN_RESPONSE;
	}

	@Override
	public String getMsgName() {
		return "Login Response";
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
						case SESSION:
							session = (Session) e;
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
	public ByteBuffer build(LoginRequest request) throws EncodeException {
		boolean isSucc = false;
		String failureReason;
		String sessId = "";
		Login login = request.getLogin();

		if (login != null) {
			// Check HHM working status
		   	boolean serviced = NmsUtil.isHmInService();

			if (serviced) {
				Object[] objs = executeRequest(login);
				isSucc = (Boolean) objs[0];
				failureReason = (String) objs[1];

				if (isSucc) {
					HttpSession session = request.getHttpServletRequest().getSession(false);

					if (session == null) {
						// Not authenticated yet, so needs to create a new session.
						session = request.getHttpServletRequest().getSession(true);
					} else {
						// Session has already existed.
						log.warning("build", "Session '" + session.getId() + "' must have already been created. Keep using it all the time.");
					}

					// Set session max inactive interval, 3 hours by default.
					session.setMaxInactiveInterval(SESSION_MAX_INACTIVE_INTERVAL);
					sessId = session.getId();
					log.info("build", "Session ID '" + session.getId() + "' to be returned to API-Client.");
				}
			} else {
				failureReason = "HHM is out of service currently.";
			}
		} else {
			String elemName = new Login().getElemName();
			failureReason =  "'" + elemName + "' must be a required element.";
		}

		if (!isSucc) {
			log.error("build", failureReason);
		}

		ByteBuffer respBB = super.build(request);

		/* Header */		
		int headerLen = encodeHeader(respBB);

		executionResult = new ExecutionResult(true, isSucc, failureReason);
		int erLen = executionResult.encode(respBB);
		int sessLen = 0;

		if (isSucc) {
			/* Session */
			Session session = new Session();
			session.setSessId(sessId);
			sessLen = session.encode(respBB);
		}

		/* Message Elements Length */
		fillPendingElementsLength(respBB, headerLen, erLen + sessLen);

		return respBB;
	}

	private Object[] executeRequest(Login login) {
		boolean isSucc = false;
		String failureReason = "";

		try {
			HmApiEngineMastAgent mastAgent = HmApiEngineMastAgentImpl.getInstance();
			isSucc = mastAgent.login(login);
		} catch (Exception e) {
			log.error("executeRequest", "Failed to execute " + login.getElemName(), e);
			failureReason = e.getMessage();
		}

		return new Object[] { isSucc, failureReason };
	}

}