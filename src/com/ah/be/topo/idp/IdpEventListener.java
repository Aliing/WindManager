package com.ah.be.topo.idp;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.ah.be.app.DebugUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.event.BeAutoMitigationResultEvent;
import com.ah.be.communication.event.BeCapwapIDPStatisticsEvent;
import com.ah.be.communication.event.BeIDPMitigationResultEvent;
import com.ah.be.communication.event.BeMitigationArbitratorResultEvent;
import com.ah.be.event.AhShutdownEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;

public class IdpEventListener {

	private final IdpEventProcessor processor;
	private final IdpMitigationProcessor mitigation;
	private final BlockingQueue<BeBaseEvent> eventQueue;
	private final AtomicInteger lostEventCount;
	private Thread eventMgr;

	public IdpEventListener() {
		eventQueue = new LinkedBlockingQueue<BeBaseEvent>(
				20000);
		lostEventCount = new AtomicInteger(0);
		processor = new IdpEventProcessor();
		mitigation = new IdpMitigationProcessor();

	}

	public synchronized void addIdpEvent(BeBaseEvent event) {
		if (!eventQueue.offer(event)) {
			lostEventCount.incrementAndGet();

			DebugUtil.topoDebugWarn("IDP Event queue is full, "
					+ lostEventCount.intValue() + " events lost.");

			// New event is more important, so remove the head of queue in order
			// to add new event into the FIFO queue.
			BeBaseEvent lostEvent = eventQueue.poll();

			if (lostEvent != null) {
				DebugUtil
						.topoDebugInfo("IDP Event queue Discarding Event. Type:["
								+ lostEvent.getEventType() + "]");
			}

			if (!eventQueue.offer(event)) {
				DebugUtil
						.topoDebugInfo("IDP Event queue is full even after removing the head of queue.");
			}
		}
	}

	public void start() {
		if (isStart()) {
			return;
		}
		eventMgr = new Thread() {
			@Override
			public void run() {
				DebugUtil
						.topoDebugInfo("IDP Event Notification thread started.");

				while (true) {
					try {
						// take() method blocks
						BeBaseEvent event = eventQueue.take();

						if (event.getEventType() == BeEventConst.AH_SHUTDOWN_EVENT) {
							DebugUtil
									.topoDebugInfo("Application is shutdown, close update event thread, events lost: "
											+ lostEventCount.intValue());
							break;
						} else {
							processEvent(event);
						}
					} catch (Exception e) {
						DebugUtil.topoDebugError(
								"IdpEventListener run exception", e);
					} catch (Error e) {
						DebugUtil.topoDebugError("IdpEventListener run error",
								e);
					}
				}
			}
		};
		eventMgr.setName("IdpEventListener");
		eventMgr.start();
	}

	public boolean isStart() {
		return eventMgr != null && eventMgr.isAlive();
	}

	public final void stop() {
		eventQueue.clear();
		BeBaseEvent stopEvent = new AhShutdownEvent();
		addIdpEvent(stopEvent);
	}

	private void processEvent(BeBaseEvent event) {
		if (event instanceof BeCapwapIDPStatisticsEvent) {
			BeCapwapIDPStatisticsEvent i_event = (BeCapwapIDPStatisticsEvent) event;
			try {
				i_event.parsePacket();
				if (i_event.getIdpMsgType() == BeCommunicationConstant.IDP_MSG_QUERY) {
					DebugUtil
							.topoDebugInfo("HiveAP:"
									+ i_event.getApMac()
									+ " receive IDP query result successfully. Sequence Number:"
									+ i_event.getSequenceNum());
				}
				processor.dealIdpEvent(i_event);
			} catch (Exception e) {
				DebugUtil.topoDebugError(
						"Parse IDP Statistics event error for HiveAP:"
								+ i_event.getApMac(), e);
			}
		} else if (event instanceof IdpMitigationEvent) {
			IdpMitigationEvent m_event = (IdpMitigationEvent) event;
			try {
				processor.dealMitigationEvent(m_event);
			} catch (Exception e) {
				DebugUtil.topoDebugError(
						"Deal IDP mitigation event error for IDP:"
								+ m_event.getIdpBssid(), e);
			}
		} else if (event instanceof BeIDPMitigationResultEvent) {
			BeIDPMitigationResultEvent m_event = (BeIDPMitigationResultEvent) event;
			try {
				mitigation.dealIdpMitigationEvent(m_event);
			} catch (Exception e) {
				DebugUtil.topoDebugError(
						"Deal IDP mitigation result error for HiveAP:"
								+ m_event.getApMac(), e);
			}
		} else if (event instanceof BeMitigationArbitratorResultEvent) {
			// query from DA
			BeMitigationArbitratorResultEvent m_event = (BeMitigationArbitratorResultEvent)event;
			try {
				processor.dealDAQueryEvent(m_event);
			} catch (Exception e) {
				DebugUtil.topoDebugError(
						"Deal mitigation arbitrator event error for DA:"
								+ m_event.getApMac(), e);
			}
		} else if (event instanceof BeAutoMitigationResultEvent) {
			//response from DA of report APs after sending friendly/rogue APs to them
			BeAutoMitigationResultEvent m_event = (BeAutoMitigationResultEvent)event;
			try {
				processor.dealAPClassificationResponseEvent(m_event);
			} catch (Exception e) {
				DebugUtil.topoDebugError(
						"Deal mitigation arbitrator event error for DA:"
								+ m_event.getApMac(), e);
			}
		}
	}

	public IdpEventProcessor getIdpEventProcessor() {
		return processor;
	}
}