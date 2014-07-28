package com.ah.be.config.hiveap;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeConfigUtil;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeAbortEvent;
import com.ah.be.communication.event.BeCapwapCliResultEvent;
import com.ah.be.communication.event.BeCapwapFileDownProgressEvent;
import com.ah.be.communication.event.BeCliEvent;
import com.ah.be.communication.event.BeHiveCommEvent;
import com.ah.be.communication.event.BeHiveCommResultEvent;
import com.ah.be.config.event.AhBootstrapGeneratedEvent;
import com.ah.be.config.event.AhCloudAuthCAGenerateEvent;
import com.ah.be.config.event.AhConfigGeneratedEvent;
import com.ah.be.config.event.AhConfigGeneratedEvent.UseMode;
import com.ah.be.config.event.AhConfigGenerationProgressEvent;
import com.ah.be.config.event.AhDeviceRebootResultEvent;
import com.ah.be.db.configuration.ConfigAuditProcessor;
import com.ah.be.db.discovery.event.AhDiscoveryEvent;
import com.ah.be.event.AhShutdownEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.bo.mgmt.impl.HiveApMgmtImpl;

public class UpdateResponseListener {

	private final BlockingQueue<BeBaseEvent> eventQueue;
	private final AtomicInteger lostEventCount;
	private Thread eventMgr;

	public UpdateResponseListener() {
		eventQueue = new LinkedBlockingQueue<BeBaseEvent>(
				20000);
		lostEventCount = new AtomicInteger(0);
	}

	public synchronized void addResponseEvent(BeBaseEvent event) {
		if (!eventQueue.offer(event)) {
			lostEventCount.incrementAndGet();

			DebugUtil.configDebugInfo("HiveAp update response queue is full, "
					+ lostEventCount.intValue() + " events lost.");

			// New event is more important, so remove the head of queue in order
			// to add new event into the FIFO queue.
			BeBaseEvent lostEvent = eventQueue.poll();

			if (lostEvent != null) {
				DebugUtil.configDebugInfo("Discarding Event. Type:["
						+ lostEvent.getEventType() + "]");
			}

			if (!eventQueue.offer(event)) {
				DebugUtil
						.configDebugInfo("HiveAp update response queue is full even after removing the head of queue.");
			}
		}
	}

	public void start() {
	//	HmBeConfigUtil.getUpdateManager().start();

		if (isStart()) {
			return;
		}
		eventMgr = new Thread() {
			@Override
			public void run() {
				DebugUtil
						.configDebugInfo("HiveAp update Event Notification thread started.");

				while (true) {
					try {
						// take() method blocks
						BeBaseEvent event = eventQueue.take();

						if (event.getEventType() == BeEventConst.AH_SHUTDOWN_EVENT) {
							DebugUtil
									.configDebugInfo("Application is shutdown, close update event thread, events lost: "
											+ lostEventCount.intValue());
							break;
						} else {
							processEvent(event);
						}
					} catch (Exception e) {
						DebugUtil.configDebugError(
								"UpdateResponseListener run exception", e);
					} catch (Error e) {
						DebugUtil.configDebugError(
								"UpdateResponseListener run error", e);
					}
				}
			}
		};
		eventMgr.setName("UpdateResponseListener eventMgr");
		eventMgr.start();
	}

	public boolean isStart() {
		return eventMgr != null && eventMgr.isAlive();
	}

	public final void stop() {
	//	HmBeConfigUtil.getUpdateManager().stop();
		eventQueue.clear();
		BeBaseEvent stopEvent = new AhShutdownEvent();
		addResponseEvent(stopEvent);
	}

	private void processEvent(BeBaseEvent event) {
		try {
			if (event instanceof AhBootstrapGeneratedEvent) {
				AhBootstrapGeneratedEvent response = (AhBootstrapGeneratedEvent) event;
				HmBeConfigUtil.getUpdateManager().dealBootstrapResponseEvent(response);
			} else if (event instanceof AhConfigGenerationProgressEvent) {
				AhConfigGenerationProgressEvent progress_event = (AhConfigGenerationProgressEvent) event;
				if (progress_event.getUseMode() == UseMode.FULL_AUDIT
						|| progress_event.getUseMode() == UseMode.RADIUS_USER_AUDIT) {
					// configuration audit process doesn't deal with the
					// progress event;
					return;
				}
				HmBeConfigUtil.getUpdateManager().dealScriptProgressEvent(progress_event);
			} else if (event instanceof AhConfigGeneratedEvent) {
				setStagedStatusUpdateHiveAp(event);
				AhConfigGeneratedEvent response = (AhConfigGeneratedEvent) event;
				if (response.getUseMode() == UseMode.FULL_AUDIT
						|| response.getUseMode() == UseMode.RADIUS_USER_AUDIT) {
					ConfigAuditProcessor.dealConfigAuditResult(response);
				} else {
					HmBeConfigUtil.getUpdateManager().dealScriptResponseEvent(response);
				}
			} else if (event instanceof BeCliEvent) {
				setStagedStatusUpdateHiveAp(event);
				BeCliEvent response = (BeCliEvent) event;
				try {
					response.parsePacket();
					HmBeConfigUtil.getUpdateManager().dealCliResponse(response);
					HmBeConfigUtil.getImageDistributor().dealUploadImageResponse(response);
				} catch (Exception e) {
					DebugUtil.configDebugWarn(
							"Parse update CLI response error.", e);
				}
			} else if (event instanceof BeCapwapFileDownProgressEvent) {
				BeCapwapFileDownProgressEvent progress_event = (BeCapwapFileDownProgressEvent) event;
				try {
					progress_event.parsePacket();
					HmBeConfigUtil.getUpdateManager().dealFileDownloadProgressEvent(progress_event);
					HmBeConfigUtil.getImageDistributor().dealImageProgress(progress_event);
				} catch (Exception e) {
					DebugUtil.configDebugWarn(
							"Parse update file download progress event error.",
							e);
				}
			} else if (event instanceof BeCapwapCliResultEvent) {
				BeCapwapCliResultEvent result_event = (BeCapwapCliResultEvent) event;
				try {
					result_event.parsePacket();
					HmBeConfigUtil.getUpdateManager().dealCliFinishEvent(result_event);
					HmBeConfigUtil.getImageDistributor().dealUploadImageResult(result_event);
				} catch (Exception e) {
					DebugUtil.configDebugWarn(
							"Parse update CLI result event error.", e);
				}
			} else if (event instanceof AhDiscoveryEvent) {
				AhDiscoveryEvent connectionEvent = (AhDiscoveryEvent) event;
				if (connectionEvent.getHiveAp().isConnected() && connectionEvent.getType() == AhDiscoveryEvent.HiveApType.UPDATED) {
					HmBeConfigUtil.getUpdateManager().dealReConnectionEvent(connectionEvent);
					udpateDeviceRebootResult(connectionEvent);
				}
				if(connectionEvent.getHiveAp().isConnected()){
					HiveApMgmtImpl.synchronizeCVGInterfaceState(connectionEvent.getHiveAp());
				}
			} else if (event instanceof BeAbortEvent) {
				try {
					BeAbortEvent abort_event = (BeAbortEvent) event;
					abort_event.parsePacket();
					HmBeConfigUtil.getUpdateManager().dealCancelOperationEvent(abort_event);
				} catch (Exception e) {
					DebugUtil.configDebugWarn("Parse abort event error.", e);
				}
			} else if (event instanceof BeHiveCommEvent) {
				try {
					BeHiveCommEvent hive_event = (BeHiveCommEvent) event;
					hive_event.parsePacket();
					HmBeConfigUtil.getImageDistributor().dealHiveCommEventResponse(hive_event);
				} catch (Exception e) {
					DebugUtil.configDebugWarn(
							"Parse hive common response event error.", e);
				}
			} else if (event instanceof BeHiveCommResultEvent) {
				try {
					BeHiveCommResultEvent hive_event = (BeHiveCommResultEvent) event;
					hive_event.parsePacket();
					HmBeConfigUtil.getImageDistributor().dealHiveCommEventResult(hive_event);
				} catch (Exception e) {
					DebugUtil.configDebugWarn(
							"Parse hive common result event error.", e);
				}
			} else if (event instanceof AhCloudAuthCAGenerateEvent){
				AhCloudAuthCAGenerateEvent response = (AhCloudAuthCAGenerateEvent)event;
				HmBeConfigUtil.getUpdateManager().dealCloudAuthCAResponseEvent(response);
			} else if(event instanceof AhDeviceRebootResultEvent){
				AhDeviceRebootResultEvent rebootEvent = (AhDeviceRebootResultEvent)event;
				HmBeConfigUtil.getUpdateManager().dealDeviceRebootResultEvent(rebootEvent);
			}
		} catch (Exception e) {
			DebugUtil.configDebugWarn(
					"UpdateResponseListener.processEvent() catch exception", e);
		}
	}
	
	private void setStagedStatusUpdateHiveAp(BeBaseEvent event){
		//set staged status to UpdateHiveAp
		if(event instanceof BeCommunicationEvent || 
				event instanceof AhConfigGeneratedEvent){
			HmBeConfigUtil.getUpdateManager().setStagedUpdateHiveAp(event);
		}
	}
	
	private void udpateDeviceRebootResult(AhDiscoveryEvent connectionEvent) throws Exception{
		AhDeviceRebootResultEvent rebootEvent = new AhDeviceRebootResultEvent();
		rebootEvent.setDeviceMac(connectionEvent.getHiveAp().getMacAddress());
		rebootEvent.setOperation(AhDeviceRebootResultEvent.OPERATION_UPDATE_RESULT);
		rebootEvent.setResultType(connectionEvent.getHiveAp().getRebootResult());
		
		HmBeConfigUtil.getUpdateManager().dealDeviceRebootResultEvent(rebootEvent);
	}

}