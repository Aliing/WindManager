package com.ah.be.performance;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.ah.be.app.DebugUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeCapwapClientResultEvent;
import com.ah.be.communication.event.BeInterferenceMapResultEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.performance.db.BulkOperationProcessor;
import com.ah.bo.HmBo;
import com.ah.bo.performance.AhACSPNeighbor;
import com.ah.bo.performance.AhInterferenceStats;
import com.ah.bo.performance.AhLatestACSPNeighbor;
import com.ah.bo.performance.AhLatestInterferenceStats;
import com.ah.util.MgrUtil;

/**
 * 
 *@filename		BePerformInterferenceMapProcessor.java
 *@version		V1.0.0.0
 *All right reserved.
 */
/**
 *modify history*
 *
 */
public class BePerformInterferenceMapProcessor {

	/**
	 * queue for event about client
	 */
	private final BlockingQueue<BeBaseEvent>	eventQueue;

	private static final int					eventQueueSize		= 10000;

	private boolean								isContinue			= true;

	private int									lostEventCount			= 0;

	private EventProcessorThread				processEventThread;

	/**
	 * reference to module instance
	 */
	private BePerformModuleImpl module;
	
	/**
	 * Construct method
	 */
	public BePerformInterferenceMapProcessor() {
		eventQueue = new LinkedBlockingQueue<>(eventQueueSize);
	}

	public void setModule(BePerformModuleImpl module) {
		this.module = module;
	}
	
	public void startTask() {
		isContinue = true;
		
		// start event process thread
		processEventThread = new EventProcessorThread();
		processEventThread.setName("processInterferenceMapThread");
		processEventThread.start();

		BeLogTools
				.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
						"<BE Thread> InterferenceMap data processor - scheduler for Perform data collector is running...");
	}

	/**
	 * add event to queue
	 * 
	 * @param event -
	 */
	public void addEvent(BeBaseEvent event) {
		try {
			if(eventQueue.offer(event)) {
				if(lostEventCount > 0) {
					DebugUtil.performanceDebugError(
							"BeInterferenceMapDataCollector.addEvent(): Lost " + lostEventCount +" events");
					lostEventCount = 0;
				}
			}
			else {
				lostEventCount ++;
			}
		} catch (Exception e) {
			lostEventCount ++;
		}
	}

	/**
	 * get event from queue
	 * 
	 * @return BeBaseEvent or null
	 */
	private BeBaseEvent getEvent() {
		try {
			return eventQueue.take();
		} catch (Exception e) {
			DebugUtil.performanceDebugError("BePerformInterferenceMapProcessor.getEvent(): Exception while get event from queue", e);
			return null;
		}
	}
	
	public boolean shutdown() {
		BeLogTools
				.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
						"<BE Thread> Perform Interference Map data processor - scheduler for pci data collector is shutdown");
		
		isContinue = false;
		eventQueue.clear();
		BeBaseEvent stopThreadEvent = new BeBaseEvent();
		eventQueue.offer(stopThreadEvent);
		return true;
	}
	
	/**
	 * handle Interference result
	 * 
	 * @param interferenceResultEvent -
	 */
	private void handleInterferenceResultEvent(BeInterferenceMapResultEvent interferenceResultEvent) {
		// update latest data
		List<HmBo> dataList = module.getLatestInterferenceData(interferenceResultEvent);
		BulkOperationProcessor.addDeleteInsertBoList(AhLatestInterferenceStats.class, dataList, interferenceResultEvent.getApMac());
		BulkOperationProcessor.addDeleteInsertBoList(AhLatestACSPNeighbor.class, dataList, interferenceResultEvent.getApMac());
		
		// insert history data
		BulkOperationProcessor.addBoList(AhInterferenceStats.class, interferenceResultEvent.getInterferenceStatsList());
		BulkOperationProcessor.addBoList(AhACSPNeighbor.class, interferenceResultEvent.getNeighborList());
	}

	/**
	 * process for client trap and client event
	 * 
	 */
	class EventProcessorThread extends Thread {
		@Override
		public void run() {
			MgrUtil.setTimerName(getClass().getSimpleName());
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"<BE Thread> Perform Interference Map processor - event processor is running...");

			while (isContinue) {
				try {
					// take() method blocks
					BeBaseEvent event = getEvent();
					if (null == event)
						continue;
					
					if (event.getEventType() == BeEventConst.COMMUNICATIONEVENTTYPE) {
						BeCommunicationEvent communicationEvent = (BeCommunicationEvent) event;

						int msgType = communicationEvent.getMsgType();
						if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT) {
							BeCapwapClientResultEvent resultEvent = (BeCapwapClientResultEvent) communicationEvent;
							if (resultEvent.getResultType() == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_INTERFERENCEMAP) {
								handleInterferenceResultEvent((BeInterferenceMapResultEvent) resultEvent);
							}
						}
					}
				} catch (Exception e) {
					DebugUtil.performanceDebugWarn("BePerformInterferenceMapProcessor.EventProcessorThread.run() Exception in processor thread", e);
				} catch (Error e) {
					DebugUtil.performanceDebugWarn("BePerformInterferenceMapProcessor.EventProcessorThread.run() Error in processor thread", e);
				}
			}
			
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"<BE Thread> Perform Interference Map processor - event processor is shutdown.");
		}
	}

}