package com.ah.be.performance;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBePerformUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeCapwapClientResultEvent;
import com.ah.be.communication.event.BeLLDPCDPInfoResultEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.topo.BeTopoModuleListener;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class BeLLDPCDPEventProcessor {
	private static final Tracer log = new Tracer(BeTopoModuleListener.class
			.getSimpleName());
	
	private final BlockingQueue<BeBaseEvent> eventQueue;

	private static final int eventQueueSize = 10000;
	
	private boolean isContinue;
	
	private final AtomicInteger lostEventCount;
	
	private LLDPCDPEventProcessThread lldpcdpEventThread;
	
	public BeLLDPCDPEventProcessor(){
		eventQueue = new LinkedBlockingQueue<BeBaseEvent>(eventQueueSize);
		lostEventCount = new AtomicInteger(0);
	}
	
	public void addEvent(BeBaseEvent event) {
		if (eventQueue.offer(event)) {

			if(lostEventCount.intValue() > 0) {
				log.error("addEvent", "LLDPCDPEventProcessThread Event queue is full, "
						+ lostEventCount.intValue() + " events lost.");
				lostEventCount.set(0);
			}
		}else {
			lostEventCount.incrementAndGet();
			// New event is more important, so remove the head of queue in order
			// to add new event into the FIFO queue.
			eventQueue.poll();

			if (!eventQueue.offer(event)) {
				log
						.error("addEvent",
								"LLDPCDPEventProcessThread Event queue is full even after removing the head of queue.");
			}
		}
	}
	
	public void startTask() {
		// nothing else to do at current time
		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
				"<BE Thread> LLDPCDPEventProcessThread Event Processor is running...");
		
		if(isContinue)
			return;
		isContinue = true;
		lldpcdpEventThread = new LLDPCDPEventProcessThread();
		lldpcdpEventThread.setName("lldpcdpEventThread");
		lldpcdpEventThread.start();
		
	}
	
	public boolean shutdown() {
		if(!isContinue)
			return true;
		isContinue = false;
		eventQueue.clear();
		BeBaseEvent stopThreadEvent = new BeBaseEvent();
		eventQueue.offer(stopThreadEvent);
		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
				"<BE Thread> LLDPCDPEventProcessThread Event Processer is shutdown");
		
		return true;
	}
	
	public class LLDPCDPEventProcessThread extends Thread {
		@Override
		public void run() {
			MgrUtil.setThreadName(lldpcdpEventThread, this.getClass().getSimpleName());
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"<BE Thread>LLDPCDPEventProcessThread Event Process Thread is running...");
			
			while (isContinue) {
				try {
					// take() method blocks
					BeBaseEvent event = eventQueue.take();
					if (null == event){
						continue;
					}
						
					if (event.getEventType() == BeEventConst.COMMUNICATIONEVENTTYPE) {
						BeCommunicationEvent communicationEvent = (BeCommunicationEvent) event;
						if (communicationEvent.getMsgType() == BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT) {
							
						try {
							BeCapwapClientResultEvent result = (BeCapwapClientResultEvent) event;
							String hiveApMac = result.getApMac();
							short resultType = result.getResultType();
							if (BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_LLDPCDPINFO == resultType) {
								BeLLDPCDPInfoResultEvent lldpResult = (BeLLDPCDPInfoResultEvent) result;
								String eth0DeviceId = lldpResult.getEth0DeviceId();
								String eth0PortId = lldpResult.getEth0PortId();
								String eth0SystemId = lldpResult.getEth0SystemName();
								int eth0PoePower = lldpResult.getEth0PoePower();
								String eth1DeviceId = lldpResult.getEth1DeviceId();
								String eth1PortId = lldpResult.getEth1PortId();
								String eth1SystemId = lldpResult.getEth1SystemName();
								int eth1PoePower = lldpResult.getEth1PoePower();
								log.debug("LLDPCDPEventProcessThread", "HiveAP: "
										+ hiveApMac + ", Eth0 device id:" + eth0DeviceId
										+ ", Eth0 port id:" + eth0PortId
										+ ", Eth0 system id:" + eth0SystemId
										+ ", Eth0 PoE power value:" + eth0PoePower
										+ ", Eth1 device id:" + eth1DeviceId
										+ ", Eth1 port id:" + eth1PortId
										+ ", Eth1 system id:" + eth1SystemId
										+ ", Eth1 PoE power value:" + eth1PoePower);
								BoMgmt.getHiveApMgmt().updateLldpCdpInfo(hiveApMac,
										eth0DeviceId, eth0PortId, eth0SystemId,
										eth1DeviceId, eth1PortId, eth1SystemId);
								HmBePerformUtil.updateLLDPInfo(hiveApMac, lldpResult.getLldbCdpInfoList());
							}
						} catch (Exception e) {
							log.error("BeLLDPCDPEventProcessor", "handle LLDP/CDP info process error", e);
						}
							
						}
					}
				} catch (Exception e) {
					DebugUtil.performanceDebugWarn(
							"BeLLDPCDPEventProcessor.lldpcdpEventThread.run() Exception in processor thread", e);
				} catch (Error e) {
					DebugUtil.performanceDebugWarn(
							"BeLLDPCDPEventProcessor.lldpcdpEventThread.run() Error in processor thread", e);
				}
			}
		}
	}
	
}
