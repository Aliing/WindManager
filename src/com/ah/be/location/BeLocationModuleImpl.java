/**
 * @filename			BeLocationModuleImpl.java
 * @version
 * @author				Administrator
 * @since
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.location;

import java.util.ArrayList;
//import java.util.HashMap;
import java.util.List;
//import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ah.be.app.BaseModule;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeCapwapClientResultEvent;
import com.ah.be.communication.event.BeLocationTrackingEvent;
//import com.ah.be.communication.mo.RSSIReading;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;

//import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * implementation of location module
 */
public class BeLocationModuleImpl extends BaseModule implements
		BeLocationModule {
	
	private static final Tracer			log				= new Tracer(BeLocationModuleImpl.class
														.getSimpleName());
	
	private BlockingQueue<BeBaseEvent>	locationEventQueue;

	private LocationEventProcessor		eventProcessor;
	
	private ScheduledExecutorService	timer;
	
	public static final int	CLEANING_TASK_INTERVAL	=	3600; // one hour
	
	private static final int	EVENT_QUEUE_SIZE	=	10000;
	
	public BeLocationModuleImpl() {
		super();
		setModuleId(BaseModule.ModuleID_Location);
		setModuleName("BeLocationModule");
	}
	
	@Override
	public boolean init() {
		locationEventQueue = new LinkedBlockingQueue<BeBaseEvent>(EVENT_QUEUE_SIZE);
		eventProcessor = new LocationEventProcessor(this);
		return super.init();
	}

	@Override
	public boolean run() {
		runCleaningTask();
		eventProcessor.setName("location event processor");
		eventProcessor.start();
		
//		new Thread(new LocationEventTester()).start();
		
		return super.run();
	}
	
	@Override
	public boolean shutdown() {
		shutdownCleaningTask();
		
		if(eventProcessor != null) {
			try {
				eventProcessor.shutdown();
			} catch(Exception e) {
				log.error("shutdown", "Error in stopping location event processor.", e);
			}
		}
		
		return super.shutdown();
	}
	
	@Override
	public void eventDispatched(BeBaseEvent arg_Event) {
		super.eventDispatched(arg_Event);		
		
		if(!isLocationResultEvent(arg_Event)) {
			// this event is not what location module want
			return;
		}

		try {
			this.locationEventQueue.offer(arg_Event);
		} catch(Exception e) {
			log.error("eventDispatched", "Error in putting event into the queue", e);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.ah.be.location.BeLocationModule#modifyTracking(com.ah.be.location.TrackingParameter)
	 */
	@Override
	public void modifyTracking(TrackingParameter parameter) throws Exception {
		if(parameter == null) {
			throw new NullPointerException("The tracking parameter object is null.");
		}
		
		if(parameter.getTrackers() == null ||
				parameter.getTrackers().size() == 0) {
			throw new Exception("No checker existed in the object.");
		}
		
		TrackingList trackList = new TrackingList();
		trackList.setParameters(parameter);
		trackList.setTrackers(parameter.getTrackers());

		sendEvent(BeLocationTrackingEvent.OPCODE_MODIFY, trackList);
	}

	/* (non-Javadoc)
	 * @see com.ah.be.location.BeLocationModule#queryLocation(com.ah.be.location.TrackingList)
	 */
	@Override
	public void queryLocation(TrackingList clients) throws Exception {
		sendEvent(BeLocationTrackingEvent.OPCODE_QUERY, clients);
	}

	/* (non-Javadoc)
	 * @see com.ah.be.location.BeLocationModule#reportLocation(com.ah.be.location.LocationReport)
	 */
	@Override
	public void reportLocation(LocationReport reports) throws Exception {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see com.ah.be.location.BeLocationModule#startTracking(com.ah.be.location.TrackingList)
	 */
	@Override
	public void startTracking(TrackingList clients) throws Exception {
		sendEvent(BeLocationTrackingEvent.OPCODE_START, clients);
	}

	/* (non-Javadoc)
	 * @see com.ah.be.location.BeLocationModule#stopTracking(com.ah.be.location.TrackingList)
	 */
	@Override
	public void stopTracking(TrackingList clients) throws Exception {
		sendEvent(BeLocationTrackingEvent.OPCODE_STOP, clients);
	}
	
	public BlockingQueue<BeBaseEvent> getEventQueue() {
		return this.locationEventQueue;
	}
	
	private void runCleaningTask() {
		if (timer == null || timer.isShutdown()) {
			timer = Executors.newSingleThreadScheduledExecutor();
		}
		
		try {
			timer.scheduleWithFixedDelay(new CleaningTask(), CLEANING_TASK_INTERVAL,
					CLEANING_TASK_INTERVAL, TimeUnit.SECONDS);
			log.info("runCleaningTask", "<BE Thread> Cleaning task is succeessfully scheduled.");
		} catch (Exception exception) {
			log.error("runCleaningTask", "Failed to schedule cleaning task.", exception);
		}
	}

	private void shutdownCleaningTask() {
		if (!timer.isShutdown()) {
			// Disable new tasks from being submitted.
			timer.shutdown();

			// Wait a while for existing tasks to terminate.
			try {
				if (!timer.awaitTermination(10, TimeUnit.SECONDS)) {
					// Cancel currently executing tasks.
					timer.shutdownNow();
					// Wait a while for tasks to respond to being canceled.
					if (!timer.awaitTermination(10, TimeUnit.SECONDS)) {
						log.info("shutdownCleaningTask", "cleaning task is not terminated.");
					}
				}
				
				log.info("shutdownCleaningTask", "<BE Thread> Cleaning task is shutdown");
			} catch (InterruptedException ie) {
				log.error("shutdownCleaningTask", "Failed to shutdown cleaning task", ie);
			}
		}
	}
	
	private void validateTrackingList(TrackingList tracking) throws Exception {
		if(tracking == null) {
			throw new NullPointerException("The tracking list object is null.");
		}
		
		if(tracking.getTrackers() == null ||
				tracking.getTrackers().size() == 0) {
			throw new Exception("No tracker existed in the tracking list.");
		}		
	}
	
	private void sendEvent(short type, TrackingList tracking) throws Exception {
		validateTrackingList(tracking);
		
		// construct a location tracking event
		BeLocationTrackingEvent event = new BeLocationTrackingEvent();
		event.setOpCode(type);
		event.setAllClients(tracking.isAllClients());

		if(tracking.getParameters() != null) {
			event.setReportInterval((short)tracking.getParameters().getReportInterval());
			event.setRssiChangeUpdateThreshold((byte)tracking.getParameters().getUpdateThreshold());
			event.setRssiValidPeriod(tracking.getParameters().getValidTime());
		}
		
		if(!tracking.isAllClients()
				&& tracking.getClients() != null
				&& tracking.getClients().size() > 0) {
			List<String> macClients = new ArrayList<String>();
			List<String> ouiClients = new ArrayList<String>();
			
			for(TrackingClient client : tracking.getClients()) {
				if(client instanceof MACClient) {
					macClients.add(client.getIdentification());
				} else if(client instanceof OUIClient){
					ouiClients.add(client.getIdentification());
				}
			}
			
			if(macClients.size() > 0)
				event.setClientMacList(macClients);

			if(ouiClients.size() > 0)
				event.setClientOuiList(ouiClients);
		}
		
		for(Tracker tracker : tracking.getTrackers()) {
			if(tracker == null ||
					tracker.getIdentification() == null) {
				continue ;
			}
			
			// set ap mac and sequence
			event.setApMac(tracker.getIdentification());
			event.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
			
			// build packet
			event.buildPacket();
			
			// send
			HmBeCommunicationUtil.sendRequest(event);
		}
	}
	
	private boolean isLocationResultEvent(BeBaseEvent event) {
		if (event.getEventType() == BeEventConst.COMMUNICATIONEVENTTYPE) {
			BeCommunicationEvent communicationEvent = (BeCommunicationEvent) event;
			int msgType = communicationEvent.getMsgType();
			
			if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT) {
				BeCapwapClientResultEvent clientEventResult = (BeCapwapClientResultEvent) communicationEvent;

				return clientEventResult.getResultType() == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_LOCATIONTRACK;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/*-
	private class LocationEventTester implements Runnable {
		@Override
		public void run() {
			MgrUtil.setTimerName(this.getClass().getSimpleName());
			byte rssi = 0;
			
			while(true) {
				BeLocationTrackingResultEvent event = new BeLocationTrackingResultEvent();
				event.setOpCode(BeLocationTrackingEvent.OPCODE_RSSIREPORT);
				
				Map<String, List<RSSIReading>>	rssiMap = new HashMap<String, List<RSSIReading>>();
				
				RSSIReading rssiReading = new RSSIReading();
				rssiReading.setApMac("001790AD25DE");
				rssiReading.setChannelFrequency(1234);
				rssiReading.setSignalStrength(rssi++);
				
				List<RSSIReading> rssiList = new ArrayList<RSSIReading>();
				rssiList.add(rssiReading);
				
				rssiMap.put("001790AD2500", rssiList);
				event.setClientRssiValuesMap(rssiMap);
				
				eventDispatched(event);
				
				try {
					Thread.sleep(30000);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}*/

}