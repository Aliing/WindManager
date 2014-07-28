/**
 * @filename			BeMiscModuleImpl.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.5
 *
 * Copyright (c) 2006-2010 Aerohive Co., Ltd.
 * All right reserved.
 */
package com.ah.be.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.ah.be.app.BaseModule;
import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.common.ConfigUtil;
import com.ah.be.common.DBOperationUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.event.BeTeacherViewStudentInfoEvent;
import com.ah.be.communication.event.BeTeacherViewStudentNotFoundEvent;
import com.ah.be.event.AhShutdownEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.be.misc.openDNS.OpenDNSCleaner;
import com.ah.be.misc.teacherview.ClearClassRequest;
import com.ah.be.misc.teacherview.TeacherViewCleaner;
import com.ah.be.search.SearchEngine;
import com.ah.be.search.SearchEngineImpl;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.teacherView.TvClass;
import com.ah.integration.airtight.SgeIntegrator;
import com.ah.ui.actions.admin.DeviceTagUtil;
import com.ah.ui.actions.teacherView.TvRedirectionAction;
import com.ah.util.MgrUtil;
import com.ah.util.SupportAccessUtil;
import com.ah.util.Tracer;

public class BeMiscModuleImpl extends BaseModule implements BeMiscModule {

	private static final Tracer log = new Tracer(BeMiscModuleImpl.class
											.getSimpleName());

	/**
	 * A timer to run <code>TeacherViewCleaner</code> periodically.
	 */
	private ScheduledExecutorService teacherViewTimer;

	/**
	 * A map to contain the requests to clear class on HiveAP
	 */
	private Map<String, List<ClearClassRequest>> clearClassRequests;

	/**
	 * A queue to buffer the received events from system event module
	 */
	private final BlockingQueue<BeBaseEvent> eventQueue = new LinkedBlockingQueue<BeBaseEvent>(
			20000);

	/**
	 * A counter to count the dropped events.
	 */
	private final AtomicInteger lostEventCount = new AtomicInteger(0);

	/**
	 * A thread running to deal with the events in this module.
	 */
	private Thread eventManager;

	/**
	 * AirTight SGE Integrator.
	 */
	private SgeIntegrator airTightSgeIntegrator;

	private ScheduledExecutorService syncScheduler;

	private ScheduledExecutorService accessMonitorScheduler;
	
	private ScheduledExecutorService openDNSScheduler;

	private SearchEngine searchEngine;

	public BeMiscModuleImpl() {

	}

	@Override
	public boolean init() {
		/*
		 * initialize search engine
		 */
		searchEngine = new SearchEngineImpl();

		clearClassRequests = new HashMap<String, List<ClearClassRequest>>();

		airTightSgeIntegrator = new SgeIntegrator();
		
		//Initialize Device Classifier Tag 
		DeviceTagUtil.init();

		return true;
	}

	@Override
	public boolean run() {
		/*
		 * run search engine
		 */
	    if(null != searchEngine) {
	        searchEngine.start();
	    }

		/*
		 * teacher view cleaner
		 */
	    runTeacherViewCleaner();
	    
	    /**
	     * @author huihe@aerohive.com
	     * @description Clean the OpenDNSSettings hourly, 
	     * if the Device Setting is removed from OpenDNS, 
	     * the related records should cleaned. 
	     */
	    runOpenDNSCleaner();

		/*
		 * start event manager
		 */
		eventManager = new Thread() {
			@Override
			public void run() {
				log.info("run", "MiscModule event manager started.");

				while (true) {
					try {
						// take() method blocks
						BeBaseEvent event = eventQueue.take();

						if (event.getEventType() == BeEventConst.AH_SHUTDOWN_EVENT) {
							log.info("run", "Application is shutdown, close MiscModule event manager, events lost: "
											+ lostEventCount.intValue());
							break;
						} else {
							handleEvent(event);
						}
					} catch (Exception e) {
						log.error("run", "MiscModule event manager exception", e);
					} catch (Error e) {
						log.error("run", "MiscModule event manager error", e);
					}
				}
			}
		};

		eventManager.setName("MiscModule Event Manager");
		eventManager.start();

		// Start AirTight SGE integrator.
		airTightSgeIntegrator.startService();

		if (NmsUtil.isHostedHMApplication()) {
			syncScheduler = MgrUtil.getSyncScheduler(120, 5, TimeUnit.SECONDS);
		}
		if(NmsUtil.isHostedHMApplication())
		{
			long period=Long.valueOf(ConfigUtil.getConfigInfo("support_access", "access_check_period", "60"));
			accessMonitorScheduler=SupportAccessUtil.getAccessMonitorScheduler(1, period, TimeUnit.SECONDS);
		}
		return true;
	}

	private void handleEvent(BeBaseEvent event) {
		if(event == null) {
			return ;
		}

		/*
		 * Teacher View Events
		 */
		if(event instanceof BeTeacherViewStudentNotFoundEvent) {
			BeTeacherViewStudentNotFoundEvent tvEvent
				= (BeTeacherViewStudentNotFoundEvent)event;
			handleFindStudentEvent(tvEvent);
		}
	}

	@Override
	public void eventDispatched(BeBaseEvent event) {
		addEvent(event);
	}

	@Override
	public boolean shutdown() {
		/*
		 * stop search engine
		 */
	    if(null != searchEngine) {
	        searchEngine.stop();
	    }

		/*
		 * teacher view cleaner
		 */
	    stopTeacherViewCleaner();
	    
		/*
		 * OpenDNS settings cleaner
		 */
	    stopOpenDNSCleaner();

		eventQueue.clear();
		BeBaseEvent stopEvent = new AhShutdownEvent();
		addEvent(stopEvent);

		// Stop AirTight SGE integrator.
		airTightSgeIntegrator.stop();

		if (syncScheduler != null && !syncScheduler.isShutdown()) {
			syncScheduler.shutdown();
		}

		if (accessMonitorScheduler != null && !accessMonitorScheduler.isShutdown()) {
			accessMonitorScheduler.shutdown();
		}

		return true;
	}

	private synchronized void addEvent(BeBaseEvent event) {
		if (!eventQueue.offer(event)) {
			// New event is more important, so remove the head of queue in order
			// to add new event into the FIFO queue.
			BeBaseEvent lostEvent = eventQueue.poll();

			if (lostEvent != null) {
				log.info("addEvent", "Event queue Discarding Event. Type:["
								+ lostEvent.getEventType() + "]");
			}

			lostEventCount.incrementAndGet();
			log.info("addEvent", "Event queue is full, "
					+ lostEventCount.intValue() + " events lost.");

			if (!eventQueue.offer(event)) {
				log.info("addEvent", "Event queue is full even after removing the head of queue.");
			}
		}
	}

	private void runTeacherViewCleaner() {
		if(teacherViewTimer == null || teacherViewTimer.isShutdown()) {
			teacherViewTimer = Executors.newSingleThreadScheduledExecutor();
			teacherViewTimer.scheduleWithFixedDelay(new TeacherViewCleaner(),
													10,	10,	TimeUnit.MINUTES);
			log.info("runTeacherViewCleaner", "TeacherViewCleaner is running...");
		}
	}

	private void stopTeacherViewCleaner() {
		if(teacherViewTimer != null && !teacherViewTimer.isShutdown()) {
			teacherViewTimer.shutdown();
			log.info("stopTeacherViewCleaner", "TeacherViewCleaner has been shutdown.");
		}
	}
	
	private void runOpenDNSCleaner(){
		if(openDNSScheduler == null || openDNSScheduler.isShutdown()){
			openDNSScheduler = Executors.newSingleThreadScheduledExecutor();
			openDNSScheduler.scheduleAtFixedRate(new OpenDNSCleaner(), 60, 60,TimeUnit.MINUTES);
			log.info("runOpenDNSCleaner", "OpenDNSSetting Cleaner is running...");
		}
	}
	
	private void stopOpenDNSCleaner() {
		if(openDNSScheduler != null && !openDNSScheduler.isShutdown()) {
			openDNSScheduler.shutdown();
			log.info("stopOpenDNSCleaner", "OpenDNSCleaner has been shutdown.");
		}
	}

	@Override
	public boolean addClearClassRequest(ClearClassRequest request) throws Exception {
		if(request == null) {
			throw new NullPointerException("The request object is null.");
		}

		if(request.getApAddress() == null
				|| request.getApAddress().isEmpty()) {
			throw new NullPointerException("The AP address of the request object is null.");
		}

		if(request.getClassId() == null
				|| request.getClassId().isEmpty()) {
			throw new NullPointerException("The class of the request object is null.");
		}

		if(clearClassRequests.containsKey(request.getApAddress())) {
			clearClassRequests.get(request.getApAddress()).add(request);
		} else {
			List<ClearClassRequest> listRequests = new ArrayList<ClearClassRequest>();
			listRequests.add(request);
			clearClassRequests.put(request.getApAddress(), listRequests);
		}

		return true;
	}

	@Override
	public boolean removeClearClassRequests(String apAddress, List<ClearClassRequest> requests) throws Exception {
		if(apAddress == null) {
			throw new NullPointerException("The AP address is null.");
		}

		if(requests == null) {
			throw new NullPointerException("The request object is null.");
		}

		if(clearClassRequests.containsKey(apAddress)) {
			List<ClearClassRequest> listRequests = clearClassRequests.get(apAddress);
			boolean result = listRequests.removeAll(requests);

			if(result) {
				if(listRequests.isEmpty()) {
					clearClassRequests.remove(apAddress);
				}
			}

			return result;
		}

		return true;
	}

	@Override
	public boolean hasClearClassRequest(String apAddress) {
		return clearClassRequests.containsKey(apAddress);
	}

	@Override
	public List<ClearClassRequest> getClearClassRequest(String apAddress) {
		return clearClassRequests.get(apAddress);
	}

	@Override
	public SgeIntegrator getAirTightSgeIntegrator() {
		return airTightSgeIntegrator;
	}

	@Override
	public SearchEngine getSearchEngine() {
        return searchEngine;
    }

    private void handleFindStudentEvent(BeTeacherViewStudentNotFoundEvent event) {
		/*
		 * get domain
		 */
    	HiveAp hiveAp = event.getAp();
    	if(hiveAp == null){
    		return;
    	}

		/*
		 * spell SQL by roster type
		 */
		StringBuilder macSql = new StringBuilder();
		macSql.append("SELECT clientmac,apmac FROM ah_clientsession ");
		macSql.append("WHERE lower(clientmac) IN (");
		
		StringBuilder idSql = new StringBuilder();
		idSql.append("SELECT clientusername,apmac FROM ah_clientsession ");
		idSql.append("WHERE lower(clientusername) IN (");
		
		int classType = event.getClassType();
		int msgVersion = event.getMsgVersion();
		byte classTypeByte = event.getClassTypeByte();

		/*
		 * fetch data from memory database
		 */
		List<Object> clientResults = null;
		Map<String, Integer> students = event.getStudentList();
		if(msgVersion == TvClass.TV_MSG_VERSION_BASE){
			StringBuilder sql = null;
			if(classType == TvClass.TV_ROSTER_TYPE_STUDENT) {
				sql = idSql;
			} else if(classType == TvClass.TV_ROSTER_TYPE_COMPUTERCART) {
				sql = macSql;
			} else{
				DebugUtil.commonDebugError("The Class Type is error, Class Type is " + classType);
				return;
			}			
			
			if(students.isEmpty()){
				return;
			}
			
			for(String studentIdentify : students.keySet()) {
				sql.append("'").append(studentIdentify.toLowerCase()).append("'").append(",");
			}
			sql.deleteCharAt(sql.lastIndexOf(","));
			sql.append(")");

			clientResults = (List<Object>) DBOperationUtil.executeQuery(sql.toString());
			
		}else if(msgVersion == TvClass.TV_MSG_VERSION_MIXEDTYPE){	
			boolean needQueryMac = false;
			boolean needQuerySid = false;
			for(String studentIdentify : students.keySet()) {
				int studentType = students.get(studentIdentify);
				if(studentType == TvClass.TV_STUNAME_TYPE_ID){
					needQuerySid = true;
					idSql.append("'").append(studentIdentify.toLowerCase()).append("'").append(",");
				}else if(studentType == TvClass.TV_STUNAME_TYPE_MACADDRESS){
					needQueryMac = true;
					macSql.append("'").append(studentIdentify.toLowerCase()).append("'").append(",");
				}				
			}
			
			macSql.deleteCharAt(macSql.lastIndexOf(","));
			macSql.append(")");		
			idSql.deleteCharAt(idSql.lastIndexOf(","));
			idSql.append(")");
			
			List<?> macClientResults = null;
			List<?> idClientResults  = null;
			if(needQueryMac){
				macClientResults = DBOperationUtil.executeQuery(macSql.toString());
			}
			
			if(needQuerySid){
				idClientResults = DBOperationUtil.executeQuery(idSql.toString());
			}
			
			clientResults = new ArrayList<Object>();
			if(macClientResults != null && !macClientResults.isEmpty()){
				clientResults.addAll(macClientResults);
			}
			
			if(idClientResults != null && !idClientResults.isEmpty()){
				clientResults.addAll(idClientResults);
			}
			
		}else{
			DebugUtil.commonDebugError("The Message Version is error, Message Version is " + msgVersion);
			return;
		}
		
		if(clientResults.isEmpty()) {
			return;
		}

		/*
		 * fit data fetched into map
		 */
		Map<String, List<Object[]>> apStudents = new HashMap<String, List<Object[]>>();
		addToMap(clientResults, apStudents, students);

		/*
		 * send result to HiveAP
		 */
		BeTeacherViewStudentInfoEvent studentEvent = new BeTeacherViewStudentInfoEvent();
		HmBeCommunicationUtil.getSequenceNumber();
		studentEvent.setApMac(event.getApMac());
		studentEvent.setClassID(event.getClassID());
		studentEvent.setClassTypeByte(classTypeByte);
		studentEvent.setClassType(classType);
		studentEvent.setMsgVersion(msgVersion);
		studentEvent.setApStuduentMap(apStudents);

		try {
			studentEvent.buildPacket();
			HmBeCommunicationUtil.sendRequest(studentEvent);
		} catch(Exception e) {
			log.error("handleFindStudentEvent", "Failed to send event of student information to HiveAP - " + event.getApMac() + ".");
		}
	}

	private void addToMap(List<?> queryResults, Map<String, List<Object[]>> apStudents, Map<String, Integer> students) {
		for(Object obj : queryResults) {
			if(obj == null) {
				continue;
			}

			Object[] columns = (Object[])obj;

			String key;

			if(columns[1] == null) {
				key = TvRedirectionAction.NONE_AP_IP;
			} else {
				SimpleHiveAp hiveAp = CacheMgmt.getInstance()
					.getSimpleHiveAp(columns[1].toString());

				if(hiveAp != null) {
					key = hiveAp.getIpAddress();
				} else {
					key = TvRedirectionAction.NONE_AP_IP;
				}
			}

			String stuIdentify = columns[0].toString();			
			if(apStudents.containsKey(key)) {
				List<Object[]> values = apStudents.get(key);				
				if(students.containsKey(stuIdentify)){
					Object[] stuInfo = new Object[2];
					stuInfo[0] = stuIdentify;
					stuInfo[1] = students.get(stuIdentify);
					values.add(stuInfo);
				}				
			} else {
				List<Object[]> values = new ArrayList<Object[]>();
				if(students.containsKey(stuIdentify)){
					Object[] stuInfo = new Object[2];
					stuInfo[0] = stuIdentify;
					stuInfo[1] = students.get(stuIdentify);
					values.add(stuInfo);
				}
				apStudents.put(key, values);
			}
		}
	}

}