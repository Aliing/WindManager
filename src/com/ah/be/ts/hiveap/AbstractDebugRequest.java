package com.ah.be.ts.hiveap;

import com.ah.be.app.AhAppContainer;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.event.BeCapwapClientEvent;
import com.ah.be.communication.event.BeDeleteCookieEvent;
import com.ah.be.ts.hiveap.DebugState.State;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public abstract class AbstractDebugRequest extends AbstractDebug implements DebugRequest {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(AbstractDebugRequest.class.getSimpleName());

    //***************************************************************
    // Variables
    //***************************************************************

	/** Used to represent a group of debug requests */
	protected int groupId;

	/** Just HTTP session id */
	protected String sessionId;

	/** Event to initiate a HiveAP debug process */
	protected BeCapwapClientEvent debugInitEvent;

	/** Event to terminate a HiveAP debug process */
	protected BeDeleteCookieEvent debugTermEvent;

	/** Debug State */
	protected DebugState debugState = new DebugState(State.UNINITIATED);

    //***************************************************************
    // Parameter Access Methods
    //***************************************************************

	@Override
	public int getGroupId() {
		return groupId;
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}

	@Override
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Override
	public DebugState getDebugState() {
		return debugState;
	}

	/*-
	@Override
	public boolean isInProcess() {		
		boolean isInProcess;

		switch (state) {
			case INITIATION_REQUESTED:
			case INITIATION_RESPONSED:
//			case TERMINATION_FAILED:
				isInProcess = true;
				break;
			default:
				isInProcess = false;
				break;
		}

		return isInProcess;
	}

	@Override
	public boolean isAborted() {
		boolean isAborted;

		switch (state) {
			case INITIATION_FAILED:
			case ABORTED:
				isAborted = true;
				break;
			default:
				isAborted = false;
				break;
		}

		return isAborted;
	}

	@Override
	public boolean isStopped() {
		boolean isStopped;

		switch (state) {
			case STOPPED:
				isStopped = true;
				break;
			default:
				isStopped = false;
				break;
		}

		return isStopped;
	}*/

	@Override
	public synchronized int initiate() throws DebugException {
		resetState();

		// If this request has already existed in the debug request holder, we should keep it.
		if (cookieId <= 0) {
			cookieId = AhAppContainer.getBeCommunicationModule().getSequenceNumber();
		}

		debugInitEvent.setSequenceNum(cookieId);
		debugInitEvent.setApMac(hiveApMac);

		int seqNum;
		log.info("initiate", "Initiating a HiveAP debug request - " + this);

		// Send debug initiation request to CAPWAP server which is able to transform to an equivalent CAPWAP request sent to the target HiveAP.
		try {
			debugInitEvent.buildPacket();
			seqNum = AhAppContainer.getBeCommunicationModule().sendRequest(debugInitEvent);
		} catch (Exception e) {
			log.error("initiate", "Could not build a HiveAP debug initiation request - " + this, e);
			String description = MgrUtil.getUserMessage("info.debug.state.initiation.request.delivery.failed", getName());
			DebugState newState = new DebugState(State.INITIATION_FAILED, description);
			changeState(newState);
			throw new DebugException(description, e);
		}

		// It is most likely that the CAPWAP connection has gone down.
		if (seqNum == BeCommunicationConstant.SERIALNUM_SENDREQUESTFAILED) {
			log.error("initiate", "Could not send a HiveAP debug initiation request - " + this);
			String description = MgrUtil.getUserMessage("info.debug.state.initiation.request.failed", getName());
			DebugState newState = new DebugState(State.INITIATION_FAILED, description);
			changeState(newState);
			throw new DebugException(description);
		}
		
		log.info("initiate", "Successfully sent a HiveAP debug initiation request - " + this);
		String description = MgrUtil.getUserMessage("info.debug.state.initiation.request.delivered", getName());
		DebugState newState = new DebugState(State.INITIATION_REQUESTED, description);
		changeState(newState);
		return cookieId;
	}

	@Override
	/*-
	public synchronized int terminate() throws AhApDebugException {
		log.info("terminate", getKey() + " - Terminating HiveAP debug. Session: " + sessionId + "; Group: " + groupId + "; Cookie: " + cookieId);

		// Send debug termination request to CAPWAP server which is able to be transformed into an equivalent CAPWAP request sent to the target HiveAP.
		int seqNum = AhAppContainer.getBeCommunicationModule().getSequenceNumber();
		debugTermEvent = new BeDeleteCookieEvent();
		debugTermEvent.setSequenceNum(seqNum);
		debugTermEvent.setApMac(hiveApMac);
		debugTermEvent.setCookie(cookieId);
		debugTermEvent.setCookieType(getCapwapType());

		try {
			debugTermEvent.buildPacket();
			seqNum = AhAppContainer.getBeCommunicationModule().sendRequest(debugTermEvent);
		} catch (Exception e) {
			log.error("terminate", getKey() + " - Could not build debug termination request. Session: " + sessionId + "; Group: " + groupId + "; Cookie: " + cookieId, e);
			String description = MgrUtil.getUserMessage("info.debug.state.termination.request.delivery.failed", getDebugCategoryName(getCapwapType()));
			DebugState newState = new DebugState(State.TERMINATION_FAILED, description);
			changeState(newState);
			throw new AhApDebugException(description, e);
		}

		// Maybe CAPWAP link has gone down.
		if (seqNum == BeCommunicationConstant.SERIALNUM_SENDREQUESTFAILED) {
			log.error("terminate", getKey() + " - Could not send debug termination request. Session: " + sessionId + "; Group: " + groupId + "; Cookie: " + cookieId);
			String description = MgrUtil.getUserMessage("info.debug.state.termination.request.failed", getDebugCategoryName(getCapwapType()));
			DebugState newState = new DebugState(State.TERMINATION_FAILED, description);
			changeState(newState);
			throw new AhApDebugException(description);
		}

		log.info("terminate", getKey() + " - Successfully sent debug termination request. Session: " + sessionId + "; Group: " + groupId + "; Cookie: " + cookieId + "; Sequence Number: " + seqNum);
		String description = MgrUtil.getUserMessage("info.debug.state.termination.request.delivered", getDebugCategoryName(getCapwapType()));
		DebugState newState = new DebugState(State.TERMINATION_REQUESTED, description);
		changeState(newState);
		return seqNum;
	}*/

	public synchronized int terminate() throws DebugException {
		log.info("terminate", "Terminating a HiveAP debug process - " + this);

		// Send debug termination request to CAPWAP server which is able to be transformed into an equivalent CAPWAP request sent to the target HiveAP.
		int seqNum = AhAppContainer.getBeCommunicationModule().getSequenceNumber();
		debugTermEvent = new BeDeleteCookieEvent();
		debugTermEvent.setSequenceNum(seqNum);
		debugTermEvent.setApMac(hiveApMac);
		debugTermEvent.setCookie(cookieId);
		debugTermEvent.setCookieType(getCapwapType());

		try {
			debugTermEvent.buildPacket();
			seqNum = AhAppContainer.getBeCommunicationModule().sendRequest(debugTermEvent);
		} catch (Exception e) {
			log.error("terminate", "Failed to build a HiveAP debug termination request - " + this, e);
			throw new DebugException("Building a HiveAP debug termination request failed.", e);
		} finally {
			String description = MgrUtil.getUserMessage("info.debug.state.notInProcess", getName());
			DebugState newState = new DebugState(State.STOPPED, description);
			changeState(newState);
		}

		return seqNum;
	}

	@Override
	public synchronized DebugState changeState(DebugState newState) {
		switch (debugState.getState()) {
			case UNINITIATED:
				changeUninitState(newState);
				break;
			case INITIATION_REQUESTED:
				changeInitReqState(newState);
				break;
			case INITIATION_RESPONSED:
				changeInitRespState(newState);
				break;
			case INITIATION_FAILED:
				changeInitFailState(newState);
				break;
			case ABORTED:
				changeAbortState(newState);
				break;
//			case TERMINATION_REQUESTED:
//				changeTermReqState(newState);
//				break;
//			case TERMINATION_FAILED:
//				changeTermFailState(newState);
//				break;
//			case INITIATION_FAILED:
//			case TERMINATION_RESPONSED:
//			case FINISHED:
//			case STOPPED:
			default:
				log.warn("changeState", "Ignored changing debug state from " + debugState.getState() + " to " + newState.getState());
				break;
		}

		return debugState;
	}

	private void changeUninitState(DebugState newState) {
		switch (newState.getState()) {
			case INITIATION_REQUESTED:
			case INITIATION_FAILED:
				log.info("changeUninitState", "Changed debug state from " + debugState.getState() + " to " + newState.getState());
				debugState = newState;
				break;
			default:
				log.warn("changeUninitState", "Ignored changing debug state from " + debugState.getState() + " to " + newState.getState());
				break;
		}
	}

	private void changeInitReqState(DebugState newState) {
		switch (newState.getState()) {
			case INITIATION_RESPONSED:
			case INITIATION_FAILED:
//			case TERMINATION_REQUESTED:
//			case TERMINATION_FAILED:
			case ABORTED:
			case STOPPED:
				log.info("changeInitReqState", "Changed debug state from " + debugState.getState() + " to " + newState.getState());
				debugState = newState;
				break;
			case FINISHED:
				log.info("changeInitReqState", "Changed debug state from " + debugState.getState() + " to " + newState.getState());
				debugState = newState;
//				completionRate = 100;
				break;
			default:
				log.warn("changeInitReqState", "Ignored changing debug state from " + debugState.getState() + " to " + newState.getState());
				break;
		}
	}

	private void changeInitRespState(DebugState newState) {
		switch (newState.getState()) {
//			case TERMINATION_REQUESTED:
//			case TERMINATION_FAILED:
			case ABORTED:
			case STOPPED:
				log.info("changeInitRespState", "Changed debug state from " + debugState.getState() + " to " + newState.getState());
				debugState = newState;
				break;
			case FINISHED:
				log.info("changeInitRespState", "Changed debug state from " + debugState.getState() + " to " + newState.getState());
				debugState = newState;
//				completionRate = 100;
				break;
			default:
				log.warn("changeInitRespState", "Ignored changing debug state from " + debugState.getState() + " to " + newState.getState());
				break;
		}
	}

	private void changeInitFailState(DebugState newState) {
		switch (newState.getState()) {
			case INITIATION_RESPONSED:// This is aimed at double initiation responses (the first is a failed response and the other is a success one). 
			case STOPPED:
				log.info("changeInitFailState", "Changed debug state from " + debugState.getState() + " to " + newState.getState());
				debugState = newState;
				break;
			default:
				log.warn("changeInitFailState", "Ignored changing debug state from " + debugState.getState() + " to " + newState.getState());
				break;
		}
	}

	/*-
	private void changeTermReqState(DebugState newState) {
		switch (newState.getState()) {
			case TERMINATION_RESPONSED:
			case TERMINATION_FAILED:
			case ABORTED:
				log.info("changeTermReqState", "Changed debug state from " + debugState.getState() + " to " + newState.getState());
				debugState = newState;
				break;
			case FINISHED:
				log.info("changeTermReqState", "Changed debug state from " + debugState.getState() + " to " + newState.getState());
				debugState = newState;
				completionRate = 100;
				break;
			default:
				log.warn("changeTermReqState", "Ignored changing debug state from " + debugState.getState() + " to " + newState.getState());
				break;
		}
	}

	private void changeTermFailState(DebugState newState) {
		switch (newState.getState()) {
			case TERMINATION_REQUESTED:
			case TERMINATION_FAILED:
			case ABORTED:
				log.info("changeTermFailState", "Changed debug state from " + debugState.getState() + " to " + newState.getState());
				debugState = newState;
				break;
			case FINISHED:
				log.info("changeTermFailState", "Changed debug state from " + debugState.getState() + " to " + newState.getState());
				debugState = newState;
				completionRate = 100;
				break;
			default:
				log.warn("changeTermFailState", "Ignored changing debug state from " + debugState.getState() + " to " + newState.getState());
				break;
		}
	}*/

	private void changeAbortState(DebugState newState) {
		switch (newState.getState()) {
			case STOPPED:
				log.info("changeAbortState", "Changed debug state from " + debugState.getState() + " to " + newState.getState());
				debugState = newState;
				break;
			default:
				log.warn("changeAbortState", "Ignored changing debug state from " + debugState.getState() + " to " + newState.getState());
				break;
		}
	}

	private void resetState() {
		String description = MgrUtil.getUserMessage("info.debug.state.notInProcess", getName());
		debugState = new DebugState(State.UNINITIATED, description);
	}

}