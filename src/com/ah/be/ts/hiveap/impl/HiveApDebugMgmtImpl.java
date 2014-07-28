package com.ah.be.ts.hiveap.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import com.ah.be.app.AhAppContainer;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeAPConnectEvent;
import com.ah.be.communication.event.BeCapwapClientEvent;
import com.ah.be.communication.event.BeCapwapClientInfoEvent;
import com.ah.be.communication.event.BeCapwapClientResultEvent;
import com.ah.be.communication.event.BeClientPerformanceMonitorResultEvent;
import com.ah.be.communication.event.BeDeleteCookieEvent;
import com.ah.be.communication.event.BeTroubleShootingResultEvent;
import com.ah.be.communication.event.BeVLANProbeResultEvent;
import com.ah.be.event.AhEventMgmt;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.be.ts.hiveap.AbstractDebug;
import com.ah.be.ts.hiveap.DebugException;
import com.ah.be.ts.hiveap.DebugMgmt;
import com.ah.be.ts.hiveap.DebugNotification;
import com.ah.be.ts.hiveap.DebugRequest;
import com.ah.be.ts.hiveap.DebugState;
import com.ah.be.ts.hiveap.DebugState.State;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitor;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitorFilterParams.LogLevel;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitorMgmt;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitorNotification;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitorNotification.Stage;
import com.ah.be.ts.hiveap.monitor.client.impl.ClientMonitorMgmtImpl;
import com.ah.be.ts.hiveap.monitor.clientperformance.result.AbstractClientPerfResult;
import com.ah.be.ts.hiveap.probe.vlan.VlanProbe;
import com.ah.be.ts.hiveap.probe.vlan.VlanProbeMgmt;
import com.ah.be.ts.hiveap.probe.vlan.VlanProbeNotification;
import com.ah.be.ts.hiveap.probe.vlan.impl.VlanProbeMgmtImpl;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public final class HiveApDebugMgmtImpl implements DebugMgmt<DebugRequest, DebugNotification>, AhEventMgmt<BeBaseEvent> {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(HiveApDebugMgmtImpl.class.getSimpleName());

	/* Response event processor */
	private final ResponseEventProcessor respProc;

	/* Notification event processor */
	private final NotificationEventProcessor notfProc;

	/* Client monitor management */
	private final ClientMonitorMgmt<ClientMonitor, ClientMonitorNotification> clientMonitorMgmt;

	/* VLAN probe management */
	private final VlanProbeMgmt<VlanProbe, VlanProbeNotification> vlanProbeMgmt;

	public HiveApDebugMgmtImpl() {
		respProc = new ResponseEventProcessor(this);
		notfProc = new NotificationEventProcessor(this);
		clientMonitorMgmt = new ClientMonitorMgmtImpl(this);
		vlanProbeMgmt = new VlanProbeMgmtImpl(this);
	}

	@Override
	public synchronized void start() {
		log.info("start", "<BE Thread>Starting HiveAP trouble shooting process...");

		// Start response processor.
		boolean started = respProc.isStarted();

		if (!started) {
			respProc.start();
		}

		// Start notification processor.
		started = notfProc.isStarted();

		if (!started) {
			notfProc.start();
		}
	}

	@Override
	public boolean isStarted() {
		return respProc.isStarted() && notfProc.isStarted();
	}

	@Override
	public synchronized void stop() {
		log.info("stop", "<BE Thread>Stopping HiveAP trouble shooting process...");

		// Stop notification processor.
		notfProc.stop();

		// Stop response processor.
		respProc.stop();
	}

	/*
	 * Add HiveAP debug event. BlockingQueue by itself is thread safe, but in case offer() fails, we want to
	 * be able to remove the head of the queue and re-try inserting the new event in the queue. Therefore the
	 * synchronization.
	 */
	@Override
	public synchronized void add(BeBaseEvent event) {
		switch (event.getEventType()) {
			case BeEventConst.COMMUNICATIONEVENTTYPE:
				// Communication Event
				switch (((BeCommunicationEvent) event).getMsgType()) {
					// Connect Event
					case BeCommunicationConstant.MESSAGETYPE_APCONNECT:
					// Disconnect Event
					case BeCommunicationConstant.MESSAGETYPE_APDISCONNECT:
						respProc.add(event);
						break;
					// Debug Initiation Response
					case BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTEVENTRSP:
						switch (((BeCapwapClientEvent) event).getQueryType()) {
							// Client Monitor Response
							case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_CLIENTMONITORING:
							// VLAN Probe Response
							case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_VLANPROBE:
							// Client Performance Response
							case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_CLIENTPERFORMANCE:
								respProc.add(event);
								break;
							default:
								break;
						}
						break;
					// Debug Termination Response
					case BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTINFORSP:
						switch (((BeCapwapClientInfoEvent) event).getQueryType()) {
							case BeCapwapClientInfoEvent.TYPE_DELETECOOKIE:
								respProc.add(event);
								break;
							default:
								break;
						}
						break;
					// Debug Notification
					case BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT:
						switch (((BeCapwapClientResultEvent) event).getResultType()) {
							// Client Monitor Notification
							case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_CLIENTMONITORING:
							// VLAN Probe Notification
							case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_VLANPROBE:
							 // Client Performance Notification
                            case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_CLIENTPERFORMANCE:
								notfProc.add(event);
								break;
							default:
								break;
						}
						break;
					default:
						break;
				}
				break;
			default:
				break;
		}
	}

	@Override
	public void notify(BeBaseEvent event) {
		AhAppContainer.getBeEventListener().eventGenerated(event);
	}

	@Override
	public int getEventQueueSize() {
		return respProc.getEventQueueSize() + notfProc.getEventQueueSize();
	}

	@Override
	public Thread[] getEventProcessThreads() {
		List<Thread> totalThreads = new ArrayList<Thread>(2);

		Thread[] respThreads = respProc.getEventProcessThreads();

		if (respThreads != null && respThreads.length > 0) {
			totalThreads.addAll(Arrays.asList(respThreads));
		}

		Thread[] notfThreads = notfProc.getEventProcessThreads();

		if (notfThreads != null && notfThreads.length > 0) {
			totalThreads.addAll(Arrays.asList(notfThreads));
		}

		return totalThreads.toArray(new Thread[totalThreads.size()]);
	}

	@Override
	public int terminatePseudoRequest(DebugNotification notification) throws DebugException {
		short debugEventType = notification.getCapwapType();
		int cookieId = notification.getCookieId();
		String hiveApMac = notification.getHiveApMac();
		log.info("terminatePseudoRequest", "Terminating a pseudo HiveAP debug process - " + notification);
		int seqNum = AhAppContainer.getBeCommunicationModule().getSequenceNumber();
		BeDeleteCookieEvent debugTermEvent = new BeDeleteCookieEvent();
		debugTermEvent.setSequenceNum(seqNum);
		debugTermEvent.setApMac(hiveApMac);
		debugTermEvent.setCookie(cookieId);
		debugTermEvent.setCookieType(debugEventType);

		try {
			debugTermEvent.buildPacket();
			seqNum = AhAppContainer.getBeCommunicationModule().sendRequest(debugTermEvent);
		} catch (Exception e) {
			log.error("terminatePseudoRequest", "Failed to build a HiveAP debug termination request - " + notification, e);
			String errorMsg = MgrUtil.getUserMessage("info.debug.state.termination.request.delivery.failed", AbstractDebug.getDebugName(debugEventType));
			throw new DebugException(errorMsg, e);
		}

		// It is most likely that the CAPWAP connection has gone down.
		if (seqNum == BeCommunicationConstant.SERIALNUM_SENDREQUESTFAILED) {
			log.error("terminatePseudoRequest", "Failed to send a HiveAP debug termination request - " + notification);
			String errorMsg = MgrUtil.getUserMessage("info.debug.state.termination.request.failed", AbstractDebug.getDebugName(debugEventType));
			throw new DebugException(errorMsg);
		}

		log.info("terminatePseudoRequest", "Sent a HiveAP debug termination request - " + notification + "; Sequence Number: " + seqNum);
		return seqNum;
	}

	@Override
	public void terminate(DebugRequest request) {
		switch (request.getDebugState().getState()) {
			case INITIATION_REQUESTED:
			case INITIATION_RESPONSED:
				try {
					request.terminate();

					// Remove unused notification events.
					notfProc.removeEvents(request.getCookieId());
				} catch (Exception e) {
					log.error("terminate", "Failed to terminate a HiveAP debug process - " + request, e);
				}
				break;
			case INITIATION_FAILED:
			case ABORTED:
				String description = MgrUtil.getUserMessage("info.debug.state.notInProcess", request.getName());
				DebugState newState = new DebugState(State.STOPPED, description);
				request.changeState(newState);
				break;
			case UNINITIATED:
			case FINISHED:
			case STOPPED:
			default:
				break;
		}
	}

	public ResponseEventProcessor getResponseProcessor() {
		return respProc;
	}

	public NotificationEventProcessor getNotificationProcessor() {
		return notfProc;
	}

	public ClientMonitorMgmt<ClientMonitor, ClientMonitorNotification> getClientMonitorMgmt() {
		return clientMonitorMgmt;
	}

	public VlanProbeMgmt<VlanProbe, VlanProbeNotification> getVlanProbeMgmt() {
		return vlanProbeMgmt;
	}

	protected void handleEvent(BeCommunicationEvent event) {
		// Communication Event
		int commMsgType = event.getMsgType();

		switch (commMsgType) {
			// Connect Event
			case BeCommunicationConstant.MESSAGETYPE_APCONNECT:
			// Disconnect Event
			case BeCommunicationConstant.MESSAGETYPE_APDISCONNECT:
				handleCapwapConnect((BeAPConnectEvent) event);
				break;
			// Debug Initiation Response
			case BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTEVENTRSP:
				int debugInitRespType = ((BeCapwapClientEvent) event).getQueryType();

				switch (debugInitRespType) {
					// Client Monitor Response
					case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_CLIENTMONITORING:
					// VLAN Probe Response
					case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_VLANPROBE:
					 // Client Performance Response
                    case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_CLIENTPERFORMANCE:
						handleDebugInitResp((BeCapwapClientEvent) event);
						break;
					// Unknown Initiation Response
					default:
						log.warn("handleEvent", "Received an unexpected HiveAP debug initiation response event. Event Type: " + debugInitRespType + "; HiveAP: " + event.getApMac());
						break;
				}
				break;
			// Debug Termination Response
			case BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTINFORSP:
				int debugTermRespType = ((BeCapwapClientInfoEvent) event).getQueryType();

				switch (debugTermRespType) {
					// Debug termination success
					case BeCapwapClientInfoEvent.TYPE_DELETECOOKIE:
						handleDebugTermResp((BeDeleteCookieEvent) event);
						break;
					// Unknown Termination Response
					default:
						log.warn("handleEvent", "Received an unexpected HiveAP debug termination response event. Event Type: " + debugTermRespType + "; HiveAP: " + event.getApMac());
						break;
				}
				break;
			// Debug Notification
			case BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT:
				handleDebugNotification((BeCapwapClientResultEvent) event);
				break;
			// Unknown Event
			default:
				log.warn("handleEvent", "Received an unexpected communication event. Event Type: " + commMsgType + "; HiveAP: " + event.getApMac());
				break;
		}
	}

	/**
	 * So far, any kind of HiveAP debug process will be automatically aborted whenever the CAPWAP link goes down.
	 * But users don't realize it immediately. So it is necessary for HM to inform users of this situation actively when it happening.
	 *
	 * @param connectEvent CAPWAP connect event offered by CAPWAP server.
	 */
	private void handleCapwapConnect(BeAPConnectEvent connectEvent) {
		boolean connected = connectEvent.isConnectState();
		String hiveApMac = connectEvent.getApMac();
		log.info("handleCapwapConnect", "Received a CAPWAP " + (connected ? "connnect" : "disconnect") + " event for HiveAP " + hiveApMac);

		if (connected) {
			// Recover overall aborted client monitor processes for this HiveAP already reconnected.
			clientMonitorMgmt.recoverRequests(hiveApMac);
		} else {
			// Change stage for client monitor processes.
			String debugName = AbstractDebug.getDebugName(BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_CLIENTMONITORING);
			String description = MgrUtil.getUserMessage("info.debug.state.capwap.aborted", debugName);
			DebugState newState = new DebugState(State.ABORTED, description);
			clientMonitorMgmt.changeState(newState, hiveApMac);

			// Change stage for VLAN probe processes.
			debugName = AbstractDebug.getDebugName(BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_VLANPROBE);
			description = MgrUtil.getUserMessage("info.debug.state.capwap.aborted", debugName);
			newState = new DebugState(State.ABORTED, description);
			vlanProbeMgmt.changeState(newState, hiveApMac);
		}
	}

	/**
	 * Handle debug initiation response.
	 *
	 * @param event debug initiation response event.
	 */
	private void handleDebugInitResp(BeCapwapClientEvent event) {
		short debugInitRespType = event.getQueryType();
		String debugName = AbstractDebug.getDebugName(debugInitRespType);
		State state = State.INITIATION_FAILED;
		String description;

		switch (event.getResult()) {
			case BeCommunicationConstant.RESULTTYPE_SUCCESS:
				switch (event.getDebugResult()) {
					// Debug initiation success
					case BeCapwapClientEvent.DEBUGRESULT_SUCCESSFUL:
						description = MgrUtil.getUserMessage("info.debug.state.initiation.request.succeed", debugName);
						state = State.INITIATION_RESPONSED;
						break;
					// Over maximum number of clients to monitor in one HiveAP
					case BeCapwapClientEvent.DEBUGRESULT_FAILED_MACTOOMANY:
 						description = MgrUtil.getUserMessage("error.hiveap.debug.client.monitor.reachMaxClients", String.valueOf(8));
						break;
					// Over maximum number of monitor process to a certain client in one HiveAP
					case BeCapwapClientEvent.DEBUGRESULT_FAILED_COOKIETOOMANY:
						description = MgrUtil.getUserMessage("error.hiveap.debug.client.monitor.reachMaxProcess", String.valueOf(8));
						break;
					// Over maximum number of probe process in one HiveAP
					case BeCapwapClientEvent.DEBUGRESULT_FAILED_VLANPROBESTARTED:
						description = MgrUtil.getUserMessage("error.hiveap.debug.vlan.probe.reachMaxProcess");
						break;
					// Debug initiation failure
					default:
						description = "Initiation request was unsuccessful. Error Code: " + event.getDebugResult();
						break;
				}
				break;
			case BeCommunicationConstant.RESULTTYPE_NOFSM:
				description = MgrUtil.getUserMessage("info.debug.state.initiation.request.failed.nofsm", debugName);
				break;
			case BeCommunicationConstant.RESULTTYPE_FSMNOTRUN:
				description = MgrUtil.getUserMessage("info.debug.state.initiation.request.failed.fsmnoturn", debugName);
				break;
			case BeCommunicationConstant.RESULTTYPE_TIMEOUT:
				description = MgrUtil.getUserMessage("info.debug.state.initiation.request.failed.timeout", debugName);
				break;
			case BeCommunicationConstant.RESULTTYPE_CONNECTCLOSE:
				description = MgrUtil.getUserMessage("info.debug.state.initiation.request.failed.disconnect", debugName);
				break;
			default:
				// Debug initiation failure
				log.error("handleDebugInitResp", "A HiveAP debug initiation request wasn't sent to HiveAP " + event.getApMac() + "; Error Code: " + event.getResult());
				description = MgrUtil.getUserMessage("info.debug.state.initiation.request.failed", debugName);
				break;
		}

		DebugState newState = new DebugState(state, description);
		log.info("handleDebugInitResp", "Received a HiveAP debug initiation response event. HiveAP: " + event.getApMac() + "; Cookie: " + event.getSerialNum() + "; Debug State: " + newState);

		switch (debugInitRespType) {
			// Client Monitor Initiation Response
			case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_CLIENTMONITORING:
			 // Client Performance Initiation Response
            case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_CLIENTPERFORMANCE:
				clientMonitorMgmt.changeState(newState, event.getSerialNum());
				break;
			// VLAN Probe Initiation Response
			case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_VLANPROBE:
				vlanProbeMgmt.changeState(newState, event.getSerialNum());
				break;
			// Unknown Debug Initiation Response
			default:
				log.warn("handleDebugInitResp", "Received an unknown HiveAP debug initiation response event. Event Type: " + debugInitRespType + "; HiveAP: " + event.getApMac());
				break;
		}
	}

	/**
	 * Handle debug termination response.
	 *
	 * @param event debug termination response event.
	 */
	private void handleDebugTermResp(BeDeleteCookieEvent event) {
		log.info("handleDebugTermResp", "Received a HiveAP debug termination response. HiveAP: " + event.getApMac() + "; Cookie: " + event.getCookie() + "; Result: " + event.getResult());

		/*-
		String debugName = AhApAbstractDebugging.getDebugName(event.getCookieType());
		State state;

		switch (event.getResult()) {
			case BeCommunicationConstant.RESULTTYPE_SUCCESS:
				// Terminated a HiveAP debug process.
				state = State.TERMINATION_RESPONSED;
				state.setDescription(MgrUtil.getUserMessage("info.debug.state.termination.request.succeed", debugName));
				break;
			default:
				// Failed to send a HiveAP debug termination request.
				log.error("handleDebugTermResp", "Failed to send a HiveAP debug termination request. Error Code: " + event.getResult());
				state = State.TERMINATION_FAILED;
				state.setDescription(MgrUtil.getUserMessage("info.debug.state.termination.request.failed", debugName));
				break;
		}

		changeDebugState(state, event.getCookie());*/
	}

	private void handleDebugNotification(BeCapwapClientResultEvent event) {
		// Convert into corresponding debug notification object based on the type of event received.
		switch (event.getResultType()) {
			// Client Monitor Notification
			case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_CLIENTMONITORING:
				if (log.getLogger().isDebugEnabled()) {
					log.debug("handleDebugNotification", "Received a client monitor notification event. HiveAP: " + event.getApMac() + "; Cookie: " + event.getSequenceNum());
				}

				ClientMonitorNotification cmn = convert((BeTroubleShootingResultEvent) event);
				clientMonitorMgmt.addNotification(cmn);
				break;
			// VLAN Probe Notification
			case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_VLANPROBE:
				if (log.getLogger().isDebugEnabled()) {
					log.debug("handleDebugNotification", "Received a VLAN probe notification event. HiveAP: " + event.getApMac() + "; Cookie: " + event.getSequenceNum());
				}

				VlanProbeNotification vpn = convert((BeVLANProbeResultEvent) event);

				if (vpn != null) {
					vlanProbeMgmt.addNotification(vpn);
				}
				break;
			// Client Performance Monitor Notification
			case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_CLIENTPERFORMANCE:
                if (log.getLogger().isDebugEnabled()) {
                    log.debug("handleDebugNotification",
                            "Received a client monitor notification event. HiveAP: " + event.getApMac()
                                    + "; Cookie: " + event.getSequenceNum());
                }
                List<ClientMonitorNotification> notifications = convert((BeClientPerformanceMonitorResultEvent) event);
                for (ClientMonitorNotification cnotification : notifications) {
                    clientMonitorMgmt.addNotification(cnotification, true);
                }
                break;
			// Unknown Notification
			default:
				log.warn("handleDebugNotification", "Received an unknown HiveAP debug notification event. Event Type: " + event.getResultType() + "; HiveAP: " + event.getApMac() + "; Cookie: " + event.getSequenceNum());
				break;
		}
	}

    /*
     * Convert <tt>BeClientPerformanceMonitorResultEvent</tt> object into client monitor notification object.
     */
    private List<ClientMonitorNotification> convert(BeClientPerformanceMonitorResultEvent event) {
        if (log.getLogger().isDebugEnabled()) {
            StringBuilder logBuf = new StringBuilder().append("\nCookie: ")
                    .append(event.getSequenceNum()).append("\nHiveAP: ").append(event.getApMac())
                    .append("\nClients: ").append(event.getClientResults());

            log.debug("convert",
                    "Converting " + BeClientPerformanceMonitorResultEvent.class.getSimpleName() + " into "
                            + ClientMonitorNotification.class.getSimpleName() + logBuf.toString());
        }
        List<ClientMonitorNotification> clients = new ArrayList<ClientMonitorNotification>();
        for (AbstractClientPerfResult client : event.getClientResults()) {
            final String clientMac = event.getClientMac();
            if(StringUtils.isBlank(clientMac)) {
                log.warn("convert",
                        "The client MacAddress should not be empty! Skip the convert operation. Type: "
                                + client.getClass().getSimpleName());
                continue;
            }
            if(StringUtils.isBlank(client.getDescription())) {
                log.warn("convert", "The description is Empty/Null. Type: " + client.getClass().getSimpleName());
                continue;
            }
            
            ClientMonitorNotification clientMonitor = new ClientMonitorNotification(
                    event.getApMac(), clientMac, event.getSequenceNum());
            
            clientMonitor.setMsgSeqNum(event.getSequenceNumber4TroubleShoot());
            clientMonitor.setDescription(client.getDescription());
            clientMonitor.setTimstamp(event.getMessageTimeStamp());
            TimeZone timeZone = TimeZone.getTimeZone(event.getMessageTimeZone());
            clientMonitor.setTimeZone(timeZone);
            clientMonitor.setLogLevel(LogLevel.DETAIL.ordinal());
            
            clientMonitor.setPerformance(true);
            
            clients.add(clientMonitor);
        }
        return clients;
    }

    /*
	 * Convert <tt>BeTroubleShootingResultEvent</tt> object into client monitor notification object.
	 */
	private ClientMonitorNotification convert(BeTroubleShootingResultEvent event) {
		int stageType = event.getStage();
		Stage stage;

		switch (stageType) {
			// IEEE802.11
			case BeTroubleShootingResultEvent.STAGE_80211:
				stage = Stage.IEEE80211;
				break;
			// RADIUS
			case BeTroubleShootingResultEvent.STAGE_RADIUS:
				stage = Stage.RADIUS;
				break;
			// AUTH
			case BeTroubleShootingResultEvent.STAGE_AUTH:
				stage = Stage.AUTH;
				break;
			// DHCP
			case BeTroubleShootingResultEvent.STAGE_DHCP:
				stage = Stage.DHCP;
				break;
			// Unknown Stage
			default:
				log.warn("convert", "Unknown Client Monitor Stage Type: " + stageType);
				stage = null;
				break;
		}

		String stageName = stage != null ? stage.toString() : String.valueOf(stageType);

		if (log.getLogger().isDebugEnabled()) {
			StringBuilder logBuf = new StringBuilder()
					.append("\nCookie: ").append(event.getSequenceNum())
					.append("\nHiveAP: ").append(event.getApMac())
					.append("\nClient MAC: ").append(event.getMacAddress())
					.append("\nMessage Sequence Number: ").append(event.getSequenceNumber4TroubleShoot())
					.append("\nSuccess: ").append(event.isStageResult())
					.append("\nBSSID: ").append(event.getBssid())
					.append("\nStage: ").append(stageName)
					.append("\nTotal Step: ").append(event.getTotalStep())
					.append("\nCurrent Step: ").append(event.getCompletingStep())
					.append("\nLog Message Time: ").append(event.getTimeStamp())
					.append("\nLog Level: ").append(event.getLevel())
					.append("\nDescription: ").append(event.getDescription());

			log.debug("convert", "Converting " + BeTroubleShootingResultEvent.class.getSimpleName() + " into " + ClientMonitorNotification.class.getSimpleName() + logBuf.toString());
		}

		ClientMonitorNotification clientMonitor = new ClientMonitorNotification(event.getApMac(), event.getMacAddress(), event.getSequenceNum());
		clientMonitor.setMsgSeqNum(event.getSequenceNumber4TroubleShoot());
		clientMonitor.setSuccess(event.isStageResult());
		clientMonitor.setBssid(event.getBssid());
		clientMonitor.setStage(stage);
		clientMonitor.setTotalStep(event.getTotalStep());
		clientMonitor.setCurrentStep(event.getCompletingStep());
		clientMonitor.setLogLevel(event.getLevel());
		clientMonitor.setDescription(event.getDescription());
		clientMonitor.setTimstamp(event.getTimeStamp());
		TimeZone timeZone = TimeZone.getTimeZone(event.getMessageTimeZone());
		clientMonitor.setTimeZone(timeZone);

		return clientMonitor;
	}

	private VlanProbeNotification convert(BeVLANProbeResultEvent event) {
		if (log.getLogger().isDebugEnabled()) {
			StringBuilder logBuf = new StringBuilder()
					.append("\nCookie: ").append(event.getSequenceNum())
					.append("\nHiveAP: ").append(event.getApMac())
					.append("\nComplete: ").append(event.isComplete())
					.append("\nVLAN: ").append(event.getVlanID())
					.append("\nIP Address: ").append(event.getIpAddress())
					.append("\nGateway: ").append(event.getDefaultGateway())
					.append("\nNetmask: ").append(event.getMaskLen())
					.append("\nDNS: ").append(event.getDns())
					.append("\nDescription: ").append(event.getDescription());

			log.debug("convert", "Converting " + BeVLANProbeResultEvent.class.getSimpleName() + " into " + VlanProbeNotification.class.getSimpleName() + logBuf.toString());
		}

		VlanProbeNotification vlanProbe = null;

		if (event.isComplete()) {
			// VLAN probe process is finish.
			String description = MgrUtil.getUserMessage("info.debug.state.finished", "VLAN probe");
			DebugState newState = new DebugState(State.FINISHED, description);
			vlanProbeMgmt.changeState(newState, event.getSequenceNum());
		} else {
			vlanProbe = new VlanProbeNotification(event.getApMac(), event.getSequenceNum());
			vlanProbe.setVlan(event.getVlanID());
			vlanProbe.setIpAddress(event.getIpAddress());
			vlanProbe.setGateway(event.getDefaultGateway());
			vlanProbe.setNetmask(event.getMaskLen());
			vlanProbe.setDns(event.getDns());
			vlanProbe.setDescription(event.getDescription());
		}

		return vlanProbe;
	}

}
