package com.ah.be.performance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;

import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.common.AhConvertBOToSQL;
import com.ah.be.common.ConfigUtil;
import com.ah.be.common.DBOperationUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.ClientInfoBean;
import com.ah.be.common.cache.ReportCacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.common.db.BulkUpdateUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeGetStatisticEvent;
import com.ah.be.communication.event.BeStatisticResultEvent;
import com.ah.be.communication.event.BeTrapEvent;
import com.ah.be.event.AhTimeoutEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.performance.dataretention.ClientHistoryTracking;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.performance.ActiveClientFilter;
import com.ah.bo.performance.AhAssociation;
import com.ah.bo.performance.AhClientEditValues;
import com.ah.bo.performance.AhClientSession;
import com.ah.bo.performance.AhClientSessionHistory;
import com.ah.bo.performance.AhInterfaceStats;
import com.ah.events.BoEventFilter;
import com.ah.events.BoEventListener;
import com.ah.events.impl.BoObserver;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.coder.AhEncoder;

/**
 * poll every ap for client session data every interval
 *@filename		BeClientSessionProcessor.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-12-26 10:15:02
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modify history*
 */
public class BeClientSessionProcessor implements Runnable, BoEventListener<HmDomain> {
	private static final Tracer log = new Tracer(BeClientSessionProcessor.class
			.getSimpleName());
	private ScheduledExecutorService				scheduler;

	/**
	 * reference to module instance
	 */
	private BePerformModuleImpl						module;

	/**
	 * queue for event about client
	 */
	private final BlockingQueue<BeBaseEvent>		eventQueue;

	private static final int						eventQueueSize					= 10000;

	private boolean									isContinue						= true;

	/**
	 * vector for insert and update
	 */
	public static final int							MAX_CACHE_SIZE					= 100;

	private final List<HmBo>						insertList						= new ArrayList<>(
																							MAX_CACHE_SIZE);

	public static final int TRAP_EVENT_UP = 0;
	
	public static final int TRAP_EVENT_DOWN = 1;
	
	public static final int TRAP_EVENT_INFO = 2;
	

	/**
	 * keep track of statSerialNum of send request for AhAssociation when receive client connect
	 * trap.
	 */
	// private Map<Integer, Date> association_StatSerialNumMap = new HashMap<Integer, Date>();
	private final Set<Integer>						statsSerialNumSet4ClientTrap	= new HashSet<>();

	EventProcessorThread							processClientThread;

	private ScheduledExecutorService				scheduler_ForClient;

	public static final int							REFRESH_TIMER_INTERVAL			= 5;

	public static final int							TRANSFER_TIMER_COUNT			= 12;

	private int										timerCount						= 0;
	
	private int										lostEventCount					= 0;

	/**
	 * keep track of continuous request number
	 */
	private int										index							= 0;

	private byte									CYCLE_APNUM						= 10;

	private final short								RELAXTIME						= 1000;

	/**
	 * keep track of statsSerialNum for refresh active clients
	 */
	private final Set<Integer>						statsSerialNumSet4RefreshClient	= new HashSet<>();

	private final CacheMgmt							cacheMgmt						= CacheMgmt
																							.getInstance();

	private final ConcurrentMap<HmDomain, Integer>	domainCounterMap				= new ConcurrentHashMap<>();
	
	final public static String[] CLIENTSESSIONHISTORY_FIELDS = new String[]{"id","apmac","apname","apserialnumber",
		"clientauthmethod","clientbssid","clientcwpused","clientchannel","clientencryptionmethod"
		,"clienthostname","clientip","clientmacprotocol","clientmac","clientssid",
		"clientuserprofid","clientusername","clientvlan","comment1","comment2",
		"ifindex","mapid","memo","bandwidthsentinelstatus","starttimestamp","endtimestamp",
		"starttimezone","endtimezone","simulated","ifName","clientOsInfo","userprofilename","os_option55","email","companyname","owner"};
	
	private static final String						CLIENTSESSIONFIELD;
	
	private static final String						ALLCLIENTSESSIONFIELD;
	
	private static final String						HISTORYCLIENTSESSION_SQL;
	
	public static boolean logDoubtfulClientInfo = false;

//	private static final String						TRANSFERSQL;

	static {
		int i;
		StringBuilder buffer = new StringBuilder();
		for (i = 1; i < CLIENTSESSIONHISTORY_FIELDS.length; i++) {
			buffer.append(CLIENTSESSIONHISTORY_FIELDS[i]);
			if (i != CLIENTSESSIONHISTORY_FIELDS.length - 1) {
				buffer.append(",");
			}
		}
		CLIENTSESSIONFIELD = buffer.toString();
		ALLCLIENTSESSIONFIELD = CLIENTSESSIONHISTORY_FIELDS[0] + ","
				+ CLIENTSESSIONFIELD;
		HISTORYCLIENTSESSION_SQL = (new StringBuffer()).append(
				"insert into ah_clientsession_history(").append(
				CLIENTSESSIONFIELD).append(") ").toString();
//		TRANSFERSQL = (new StringBuffer())
//				.append(
//						"lock TABLE ah_clientsession_history,ah_clientsession in EXCLUSIVE mode;")
//				.append("insert into ah_clientsession_history(").append(
//						CLIENTSESSIONFIELD).append(") (select ").append(
//						CLIENTSESSIONFIELD).append(
//						" from ah_clientsession where connectstate = 0);")
//				.append("delete from ah_clientsession where connectstate=0;")
//				.toString();
	}

	/**
	 * Construct method
	 */
	public BeClientSessionProcessor() {
		eventQueue = new LinkedBlockingQueue<>(eventQueueSize);
		try {
			CYCLE_APNUM = Byte.parseByte(ConfigUtil.getConfigInfo(
					ConfigUtil.SECTION_PERFORMANCE,
					ConfigUtil.KEY_POLLING_DEVICE_NUMBER_PER_SEC, "10"));
		}catch (Exception e) {
		}
		initDomainCounterMap();

		BoObserver.addBoEventListener(this, new BoEventFilter<>(HmDomain.class));
	}

	private void initDomainCounterMap() {
		try {
			List<HMServicesSettings> list = QueryUtil.executeQuery(HMServicesSettings.class, null, null);
			if (list.isEmpty()) {
				DebugUtil
						.performanceDebugError("BeClientSessionProcessor.initDomainCounterMap(): Error find no active client refresh interval settings in db!");
				return;
			}

			for (HMServicesSettings bo : list) {
				if (bo.isEnableClientRefresh()) {
					domainCounterMap.put(bo.getOwner(), bo.getRefreshInterval());
					bo.getOwner().setClientRefreshInterval(bo.getRefreshInterval());
				}
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugError("initDomainCounterMap catch exception", e);
		}
	}

	public void startTask() {
		isContinue = true;

		scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleWithFixedDelay(this, 1, 1, TimeUnit.MINUTES);

		// start timeout factory scheduler
		if (scheduler_ForClient == null || scheduler_ForClient.isShutdown()) {
			scheduler_ForClient = Executors.newSingleThreadScheduledExecutor();
			scheduler_ForClient.scheduleWithFixedDelay(new RefreshTimerGenerator(),
					REFRESH_TIMER_INTERVAL, REFRESH_TIMER_INTERVAL, TimeUnit.SECONDS);
		}
		// start event process thread
		processClientThread = new EventProcessorThread();
		processClientThread.setName("processClientThread");
		processClientThread.start();

		BeLogTools
				.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
						"<BE Thread> Client session processor - scheduler and scheduler for client is running...");
	}

	@Override
	public void run() {
		MgrUtil.setTimerName(getClass().getSimpleName());
		try {
			for (HmDomain domain : domainCounterMap.keySet()) {
				int newCounter = domainCounterMap.get(domain) - 1;
				if (newCounter == 0) {
					// reinitial counter for domain
					newCounter = domain.getClientRefreshInterval();

					// refresh active clients for this domain
					refreshActiveClients(domain);
				}

				domainCounterMap.put(domain, newCounter);
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugError("BeClientSessionProcessor.run() catch exception", e);
		} catch (Error e) {
			DebugUtil.performanceDebugError("BeClientSessionProcessor.run() catch error", e);
		}
	}

	private void refreshActiveClients(HmDomain domain) {
		DebugUtil.performanceDebugInfo("Start refresh active clients for domain:"
				+ domain.getDomainName());

		List<SimpleHiveAp> apList = getRefreshApList(domain);

		if (apList == null || apList.isEmpty()) {
			return;
		}

		try {
			// send request for AhAssociation
			Map<Byte, List<String>> statsTableInfo = new HashMap<>(1);
			statsTableInfo.put(BeCommunicationConstant.STATTABLE_AHASSOCIATION,
					new ArrayList<String>());
			
			// Date statsTime = handleStatsTime4Show(Calendar.getInstance()).getTime();
			for (SimpleHiveAp ap : apList) {
				sendStatsRequest(ap, statsTableInfo);
			}
		} catch (Exception e) {
			DebugUtil
					.performanceDebugError(
							"BeClientSessionProcessor.refreshActiveClients(): Refresh active clients catch exception ",
							e);
			systemLog(HmSystemLog.LEVEL_MAJOR,
					"Failed to send request for getting statistics data. Maybe build request packet error.");
		}
	}

	/**
	 * get refresh ap list by filter criterion
	 *
	 * @param domain -
	 * @return -
	 */
	private List<SimpleHiveAp> getRefreshApList(HmDomain domain) {
		HMServicesSettings bo = QueryUtil.findBoByAttribute(
				HMServicesSettings.class, "owner", domain);
		if (bo == null) {
			return null;
		}

		String filterName = bo.getRefreshFilterName();
		if (filterName == null || filterName.trim().isEmpty()) {
			return cacheMgmt.getManagedApList(domain.getId());
		}

		ActiveClientFilter filter = QueryUtil.findBoByAttribute(
				ActiveClientFilter.class, "filterName", filterName, domain.getId());
		if (filter == null) {
			DebugUtil
					.performanceDebugWarn("BeClientSessionProcessor.getRefreshApList(): Can't get filter bo from db which filter name is "
							+ filterName);
			return null;
		}

		List<SimpleHiveAp> apList;
		Long filterMap = filter.getFilterTopologyMap();
		String filterApName = filter.getFilterApName();
		String filterClientMac = filter.getFilterClientMac();
		String filterClientIP = filter.getFilterClientIP();
		String filterClientHostName = filter.getFilterClientHostName();
		String filterClientUserName = filter.getFilterClientUserName();

		if (filterMap == null || filterMap == -2) {
			apList = cacheMgmt.getManagedApList(domain.getId());
		} else if (filterMap == -1) {
			apList = cacheMgmt.getApListByMapContainer(null, domain.getId());
		} else {
			apList = cacheMgmt.getApListByMapContainer(filterMap, domain.getId());
		}

		if (apList == null) {
			return null;
		}

		// filter aplist by ap name
		if (filterApName != null && !filterApName.trim().isEmpty()) {
			for (Iterator<SimpleHiveAp> iter = apList.iterator(); iter.hasNext();) {
				SimpleHiveAp ap = iter.next();
				if (!ap.getHostname().toLowerCase().contains(filterApName.toLowerCase())) {
					iter.remove();
				}
			}
		}

		// filter by clientmac,clientip,clienthostname,clientusername
		if (!((filterClientMac == null || filterClientMac.trim().isEmpty())
				&& (filterClientIP == null || filterClientIP.trim().isEmpty())
				&& (filterClientHostName == null || filterClientHostName.trim().isEmpty()) && (filterClientUserName == null || filterClientUserName
				.trim().isEmpty()))) {
			String searchSQL = "connectstate=:s1";
			List<Object> lstCondition = new ArrayList<>();
			lstCondition.add(AhClientSession.CONNECT_STATE_UP);

			if (filterClientMac != null && !filterClientMac.trim().isEmpty()) {
				searchSQL = searchSQL + " AND lower(clientmac) like :s" + (lstCondition.size() + 1);
				lstCondition.add("%" + filterClientMac.toLowerCase() + "%");
			}

			if (filterClientIP != null && !filterClientIP.trim().isEmpty()) {
				searchSQL = searchSQL + " AND lower(clientIP) like :s" + (lstCondition.size() + 1);
				lstCondition.add("%" + filterClientIP.toLowerCase() + "%");
			}

			if (filterClientHostName != null && !filterClientHostName.trim().isEmpty()) {
				searchSQL = searchSQL + " AND lower(clientHostname) like :s"
						+ (lstCondition.size() + 1);
				lstCondition.add("%" + filterClientHostName.toLowerCase() + "%");
			}

			if (filterClientUserName != null && !filterClientUserName.trim().isEmpty()) {
				searchSQL = searchSQL + " AND lower(clientUsername) like :s"
						+ (lstCondition.size() + 1);
				lstCondition.add("%" + filterClientUserName.toLowerCase() + "%");
			}

//			List<?> apMacList = QueryUtil.executeQuery("select apMac from "
//					+ AhClientSession.class.getSimpleName(), null, new FilterParams(searchSQL,
//					lstCondition), domain.getId());
			List<?> apMacList = DBOperationUtil.executeQuery("select apMac from ah_clientsession where " + searchSQL,
						new Object[]{AhClientSession.CONNECT_STATE_UP});
			
			if (apMacList == null || apMacList.isEmpty()) {
				return null;
			}

			for (Iterator<SimpleHiveAp> iter = apList.iterator(); iter.hasNext();) {
				SimpleHiveAp ap = iter.next();
				if (!apMacList.contains(ap.getMacAddress())) {
					iter.remove();
				}
			}
		}

		return apList;
	}

	/**
	 * send request
	 * 
	 * @param ap
	 *            -
	 * @param statsTableMap
	 *            -
	 * @throws BeCommunicationEncodeException
	 *             -
	 */
	private void sendStatsRequest(SimpleHiveAp ap, Map<Byte, List<String>> statsTableMap)
			throws BeCommunicationEncodeException {
		BeGetStatisticEvent req = new BeGetStatisticEvent();
		req.setSimpleHiveAp(ap);
		int statsSerialNum = HmBeCommunicationUtil.getSequenceNumber();
		req.setSequenceNum(statsSerialNum);
		req.setStatsTableIndexMap(statsTableMap);

		req.buildPacket();

		int serialNum = HmBeCommunicationUtil.sendRequest(req);
		if (serialNum == BeCommunicationConstant.SERIALNUM_SENDREQUESTFAILED) {
			// connect closed
			systemLog(HmSystemLog.LEVEL_MINOR,
					"Connection to capwap process closed! Failed to send request for getting"
							+ ap.getHostname() + " statistics data.");
			return;
		}

		statsSerialNumSet4RefreshClient.add(statsSerialNum);
		module.addRequestObj(serialNum, req);
		if (++index == CYCLE_APNUM) {
			try {
				Thread.sleep(RELAXTIME);
			} catch (Exception e) {
				DebugUtil
						.performanceDebugWarn("BeClientSessionProcessor.sendStatsRequest(): Thread sleep error");
				systemLog(HmSystemLog.LEVEL_MINOR,
						"Thread for getting statistics data meet some error.");
			}

			index = 0;
		}
	}

	/**
	 * sync active clients when ap status changed managed status
	 * 
	 * @param ap
	 *            -
	 */
	void retrieveActiveClients(HiveAp ap) {
		try {
			// send request for AhAssociation
			Map<Byte, List<String>> statsTableInfo = new HashMap<>(1);
			statsTableInfo.put(BeCommunicationConstant.STATTABLE_AHASSOCIATION,
					new ArrayList<String>());
			
			// Date statsTime = handleStatsTime4Show(Calendar.getInstance()).getTime();
			SimpleHiveAp simpleHiveAp = cacheMgmt.getSimpleHiveAp(ap.getMacAddress());
			if (simpleHiveAp == null) {
				DebugUtil
						.performanceDebugWarn("BePerformModuleImpl.retrieveActiveClients(): failed to retrieve active clients, relative ap obj is null. ");
				return;
			}
			sendStatsRequest(simpleHiveAp, statsTableInfo);
		} catch (Exception e) {
			DebugUtil
					.performanceDebugWarn(
							"BePerformModuleImpl.retrieveActiveClients(): Refresh active clients catch exception ",
							e);
			this
					.systemLog(HmSystemLog.LEVEL_MAJOR,
							"Failed to send request for getting statistics data. Maybe build request packet error.");
		}
	}

	/**
	 * sync request all active clients
	 * 
	 * @return 0: success, 1: full fail, 2: partial fail
	 */
	int syncRequestAllActiveClients() {
		// 1. query all managed ap
		// String where = "manageStatus = :s1";
		// Object[] values = new Object[1];
		// values[0] = HiveAp.STATUS_MANAGED;
		//
//		 List apList = QueryUtil.executeQuery(HiveAp.class, null,
//		 new FilterParams(where, values));

		List<SimpleHiveAp> apList = cacheMgmt.getManagedApList();

		if (apList == null || apList.isEmpty()) {
			return SYNCREQUEST_RESULT_SUCCESS;
		}

		// 2. sync request active clients
		return syncRequestActiveClients(apList);
	}

	/**
	 * calculate timeout value for request statistics data
	 * 
	 * @param apNum
	 *            -
	 * @return -
	 */
	private int getTimeOutValue(int apNum) {
		return Math.max(BeCommunicationConstant.DEFAULTTIMEOUT, (35 + apNum / 2));
	}

	public static final int	SYNCREQUEST_RESULT_SUCCESS		= 0;

	public static final int	SYNCREQUEST_RESULT_FULLFAIL		= 1;

	public static final int	SYNCREQUEST_RESULT_PARTIALFAIL	= 2;

	/**
	 * sync request active clients
	 * 
	 * @param apList
	 *            -
	 * @return 0: success, 1: full fail, 2: partial fail
	 */
	int syncRequestActiveClients(Collection<SimpleHiveAp> apList) {
		if (apList == null || apList.isEmpty()) {
			return 0;
		}

		Map<Byte, List<String>> statsTableInfo = new HashMap<>();
		statsTableInfo
				.put(BeCommunicationConstant.STATTABLE_AHASSOCIATION, new ArrayList<String>());
		
		
		List<BeCommunicationEvent> requestList = new ArrayList<>(apList.size());

		try {
			for (SimpleHiveAp ap : apList) {
				BeGetStatisticEvent req = new BeGetStatisticEvent();
				req.setSimpleHiveAp(ap);
				int statsSerialNum = HmBeCommunicationUtil.getSequenceNumber();
				req.setSequenceNum(statsSerialNum);
				req.setStatsTableIndexMap(statsTableInfo);

				req.buildPacket();

				requestList.add(req);
			}
		} catch (BeCommunicationEncodeException e) {
			DebugUtil.performanceDebugWarn(
					"BeClientSessionProcessor.syncRequestActiveClients(): build packet error", e);
			return SYNCREQUEST_RESULT_FULLFAIL;
		}

		List<BeCommunicationEvent> responseList = HmBeCommunicationUtil.sendSyncGroupRequest(
				requestList, getTimeOutValue(apList.size()));
		if (responseList == null) {
			DebugUtil
					.performanceDebugWarn("BeClientSessionProcessor.syncRequestActiveClients(): request failed, return null.");
			return SYNCREQUEST_RESULT_FULLFAIL;
		}

		// cache all ap that request time out
		List<String> apList_timeOut = new ArrayList<>();

		for (BeCommunicationEvent communicationEvent : responseList) {
			switch (communicationEvent.getMsgType()) {
			// statistics data, save into db
			case BeCommunicationConstant.MESSAGEELEMENTTYPE_STATISTICRESULT: {
				// handleStatsPacket((BeStatisticResultEvent) communicationEvent);
				addEvent(communicationEvent);
				break;
			}
			case BeCommunicationConstant.MESSAGETYPE_GETSTATISTICRSP: {
				// if (communicationEvent.getResult() == BeCommunicationConstant.RESULTTYPE_TIMEOUT)
				// {
				// apList_timeOut.add(communicationEvent.getAp());
				// }

				// maybe connect close
				if (communicationEvent.getResult() != BeCommunicationConstant.RESULTTYPE_SUCCESS) {
					apList_timeOut.add(communicationEvent.getApMac());
				}

				break;
			}
			}
		}

		if (apList_timeOut.size() == requestList.size()) {
			return SYNCREQUEST_RESULT_FULLFAIL;
		} else if (!apList_timeOut.isEmpty()) {
			return SYNCREQUEST_RESULT_PARTIALFAIL;
		}

		return SYNCREQUEST_RESULT_SUCCESS;
	}

	/**
	 * query client AhAssociation list synchronized.
	 * 
	 * @param apList -
	 * @return List<AhAssociation>, if return null, maybe disconnect with capwap
	 */
	public List<AhAssociation> syncQueryHiveAPsClients(List<SimpleHiveAp> apList) {
		Map<Byte, List<String>> statsTableInfo = new HashMap<>();
		statsTableInfo
				.put(BeCommunicationConstant.STATTABLE_AHASSOCIATION, new ArrayList<String>());
		
		List<BeCommunicationEvent> requestList = new ArrayList<>(apList.size());

		try {
			for (SimpleHiveAp ap : apList) {
				BeGetStatisticEvent req = new BeGetStatisticEvent();
				req.setSimpleHiveAp(ap);
				int statsSerialNum = HmBeCommunicationUtil.getSequenceNumber();
				req.setSequenceNum(statsSerialNum);
				req.setStatsTableIndexMap(statsTableInfo);

				req.buildPacket();

				requestList.add(req);
			}
		} catch (BeCommunicationEncodeException e) {
			DebugUtil.performanceDebugWarn(
					"BeClientSessionProcessor.syncQueryHiveAPsClients(): build packet error", e);
			return null;
		}

		List<BeCommunicationEvent> responseList = HmBeCommunicationUtil.sendSyncGroupRequest(
				requestList, getTimeOutValue(apList.size()));
		if (responseList == null) {
			DebugUtil
					.performanceDebugWarn("BeClientSessionProcessor.syncQueryHiveAPsClients(): request failed, return null.");
			return null;
		}

		List<HmBo> boList = new ArrayList<>();

		for (BeCommunicationEvent communicationEvent : responseList) {
			switch (communicationEvent.getMsgType()) {
			// statistics data, save into db
			case BeCommunicationConstant.MESSAGEELEMENTTYPE_STATISTICRESULT: {
				// put into process
				addEvent(communicationEvent);

				// get association
				BeStatisticResultEvent statEvent = (BeStatisticResultEvent) communicationEvent;
				try {
					statEvent.parsePacket();
				} catch (BeCommunicationDecodeException e) {
					DebugUtil
							.performanceDebugError(
									"BeClientSessionProcessor.syncQueryHiveAPsClients(): Catch decode exception when receive statistics result",
									e);

					continue;
				}

				Map<Byte, List<HmBo>> rowData = statEvent.getStatsRowData();
				if (rowData.isEmpty()) {
					continue;
				}

				for (Byte id : rowData.keySet()) {
					switch (id) {
					case BeCommunicationConstant.STATTABLE_AHASSOCIATION:
						boList.addAll(rowData.get(id));
						break;
					}
				}

				break;
			}
			case BeCommunicationConstant.MESSAGETYPE_GETSTATISTICRSP: {
				DebugUtil
						.performanceDebugWarn("BeClientSessionProcessor.syncQueryHiveAPsClients(): request to AP("
								+ communicationEvent.getApMac()
								+ ") failed, result is "
								+ communicationEvent.getResult());
				break;
			}
			}
		}

		List<AhAssociation> assocList = new ArrayList<>(boList.size());
		for (HmBo bo : boList) {
			AhAssociation associ = (AhAssociation) bo;
			assocList.add(associ);
		}

		return assocList;
	}

	/**
	 * judge statistics result packet's target
	 * 
	 * @param statsSerialNum
	 *            -
	 * @return -
	 */
	public boolean is4RefreshActiveCleints(Integer statsSerialNum) {
		return statsSerialNumSet4RefreshClient.contains(statsSerialNum);
	}

	/**
	 * handle statistics event
	 * 
	 * @param statEvent
	 *            -
	 */
	private void handleStatsPacket(BeStatisticResultEvent statEvent) {
		// Date statTime = statsSerialNumTimeMap.get(statEvent.getStatsSerialNum());
		// if (null == statTime) {
		// statTime = handleStatsTime4Show(Calendar.getInstance()).getTime();
		// }
		// remove statistics number from cache
		statsSerialNumSet4RefreshClient.remove(statEvent.getSequenceNum());

		// 1. parse packet
		try {
			// statEvent.setStatTime(statTime);
			statEvent.parsePacket();
		} catch (BeCommunicationDecodeException e) {
			DebugUtil
					.performanceDebugWarn("BeClientSessionProcessor.handleStatsPacket(): parse statistics packet error");
			systemLog(HmSystemLog.LEVEL_MAJOR,
					"Failed to parse received statistics result packet! AP="
							+ (statEvent.getSimpleHiveAp() == null ? "UNKNOWN" : statEvent
									.getSimpleHiveAp().getHostname()));

			return;
		}

		// 3. update data into db
		Map<Byte, List<HmBo>> rowData = statEvent.getStatsRowData();
		List<HmBo> rowList = rowData.get(BeCommunicationConstant.STATTABLE_AHASSOCIATION);
		if (rowList.isEmpty()) {
			DebugUtil
					.performanceDebugInfo("BeClientSessionProcessor.handleStatsPacket(): parse receive statistics result, but there are no data in.");

			// remove all clients record belong to this ap
			removeAllClients(statEvent.getSimpleHiveAp());

			// module.updateLatestStatsTime(
			// BeCommunicationConstant.STATTABLE_AHASSOCIATION,
			// statsSerialNumTimeMap.get(statEvent.getStatsSerialNum()));

			return;
		}
//		List<HmBo> dataList = new ArrayList<>(rowList.size() * 2);
		try {
			// save AhAssociation data
			// QueryUtil.bulkCreateBos(rowList);
			
			// update AhCurrentclientSession
			updateActiveClients(rowData.get(BeCommunicationConstant.STATTABLE_AHASSOCIATION),
					statEvent.getSimpleHiveAp(), insertList);

			insertList.addAll(rowList);

//			QueryUtil.bulkCreateBos(dataList);

			// update active client in cache.
//			module.activeClientAdd(statEvent.getSimpleHiveAp(), rowList.size());

			DebugUtil
					.performanceDebugInfo("BeClientSessionProcessor.handleStatsPacket: add AhAssociation and current client statistic result to DB :"
							+ rowList.size()*2
							+ " AP:"
							+ statEvent.getSimpleHiveAp().getMacAddress());
		} catch (Exception e) {
			DebugUtil
					.performanceDebugWarn("BeClientSessionProcessor.handleStatsPacket: update AhAssociation statistic result to DB failed!");
			systemLog(HmSystemLog.LEVEL_MAJOR, "Failed to save statistics data into database! AP="
					+ (statEvent.getSimpleHiveAp() == null ? "UNKNOWN" : statEvent
							.getSimpleHiveAp().getHostname()));
		}
	}

	/**
	 * remove all clients record belong to this ap
	 * 
	 * @param ap
	 *            -
	 */
	void removeAllClientsByAp(HiveAp ap) {
		// maybe ap has been removed from cache,let's create a obj
		SimpleHiveAp simpleHiveAp = cacheMgmt.getSimpleHiveAp(ap);
		if (simpleHiveAp != null) {
			BeClientSessionEvent event = new BeClientSessionEvent();
			event.setEventType(BeEventConst.AH_CLIENT_SESSION_REMOVE_EVENT);
			event.setAp(simpleHiveAp);
			addEvent(event);
		}
	}

	/**
	 * remove all clients record belong to this ap
	 * 
	 * @param ap
	 *            -
	 */
	private void removeAllClients(SimpleHiveAp ap) {
		if (ap == null) {
			DebugUtil
					.performanceDebugWarn("BeClientSessionProcessor.removeAllClients(): AP argument is null.");
			return;
		}

		try {
			// refresh cache
			refreshDb();

//			String where = "apMac = :s1 and connectstate = :s2";
//			Object[] values = new Object[2];
//			values[0] = ap.getMacAddress();
//			values[1] = AhClientSession.CONNECT_STATE_UP;

//			int count = QueryUtil.bulkRemoveBos(AhClientSession.class, new FilterParams(where,
//					values), null, null);
			
//			int count = DBOperationUtil.executeUpdate("delete from ah_clientsession where apMac = ? and connectstate = ?",
//					values);
			
			int countWire = DBOperationUtil.executeUpdate("delete from ah_clientsession where apMac = ? and connectstate = ? and wirelessClient = ?",
					new Object[]{ap.getMacAddress(),AhClientSession.CONNECT_STATE_UP, false});
			 
			int count24 = DBOperationUtil.executeUpdate("delete from ah_clientsession where apMac = ? and connectstate = ? " +
					" and wirelessClient=? and (clientMACProtocol=? or clientMACProtocol=? or clientMACProtocol=?)",
					new Object[]{ap.getMacAddress(),AhClientSession.CONNECT_STATE_UP, true, 
					AhAssociation.CLIENTMACPROTOCOL_BMODE,AhAssociation.CLIENTMACPROTOCOL_GMODE,
					AhAssociation.CLIENTMACPROTOCOL_NGMODE});
	
			int count5 = DBOperationUtil.executeUpdate("delete from ah_clientsession where apMac = ? and connectstate = ? " +
					" and wirelessClient=? and (clientMACProtocol=? or clientMACProtocol=? or clientMACProtocol=?)",
					new Object[]{ap.getMacAddress(),AhClientSession.CONNECT_STATE_UP, true, 
					AhAssociation.CLIENTMACPROTOCOL_AMODE,AhAssociation.CLIENTMACPROTOCOL_NAMODE,
					AhAssociation.CLIENTMACPROTOCOL_ACMODE});
	
			// update active client value in cache.
			// try to remove the cache number of ap to correct the count on HiveAP
	
			SimpleHiveAp simpleAp = CacheMgmt.getInstance().getSimpleHiveAp(ap.getMacAddress());
			if(simpleAp != null) {
				int delCount = simpleAp.getActiveClientCount() -countWire - count24 - count5;
				if (countWire>0) {
					module.activeClientRemove(ap, countWire, AhInterfaceStats.RADIOTYPE_OTHER);
				}
				if (count24>0) {
					module.activeClientRemove(ap, count24,AhInterfaceStats.RADIOTYPE_24G);
				}
				if (count5>0) {
					module.activeClientRemove(ap, count5,AhInterfaceStats.RADIOTYPE_5G);
				}
				if (delCount>0) {
					log.error("Client is mismatch between radio type and total count.........for max client count by nfang.");
					module.activeClientRemove(ap, delCount,-1);
				}
//				module.activeClientRemove(simpleAp, simpleAp.getActiveClientCount() > 0 ? simpleAp.getActiveClientCount()
//						: count);
			} else {
				if (countWire>0) {
					module.activeClientRemove(ap, countWire, AhInterfaceStats.RADIOTYPE_OTHER);
				}
				if (count24>0) {
					module.activeClientRemove(ap, count24,AhInterfaceStats.RADIOTYPE_24G);
				}
				if (count5>0) {
					module.activeClientRemove(ap, count5,AhInterfaceStats.RADIOTYPE_5G);
				}
			}
			// DebugUtil
			// .performanceDebugInfo("BeClientSessionProcessor.removeAllClients():
			// Success remove all active clients(Num:"
			// + count + ") of ap(mac:" + ap.getMacAddress() + ")");
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn(
					"BeClientSessionProcessor.removeAllClients(): Failed to remove all clients belong to ap(mac="
							+ ap.getMacAddress() + ")", e);
		}
	}

	/**
	 * remove statsSerialNum from cache
	 * 
	 * @param statsSerialNum
	 *            -
	 */
	void removeStatsSerialNumFromCache(Integer statsSerialNum) {
		statsSerialNumSet4RefreshClient.remove(statsSerialNum);
	}

	/**
	 * add sequence number into cache, for receive corresponding result event.
	 * 
	 * @param statsSerialNum -
	 */
	public void addStatsSerialNum4ClientRefresh(Integer statsSerialNum) {
		statsSerialNumSet4RefreshClient.add(statsSerialNum);
	}

	/**
	 * update data in activeclients
	 * 
	 * @param rowList
	 *            -
	 * @param ap
	 *            -
	 * @param dataList
	 *            -
	 */
	private void updateActiveClients(List<HmBo> rowList, SimpleHiveAp ap, List<HmBo> dataList) {
		if (rowList == null) {
			DebugUtil
					.performanceDebugWarn("BeClientSessionProcessor.updateActiveClients(): Association list is null, possible caused by error dispatch statsEvent in BePerformModuleImpl.handleCommunicationEvent()");
			return;
		}

		if (ap == null) {
			DebugUtil
					.performanceDebugWarn("BeClientSessionProcessor.updateActiveClients(): ap obj is null!");
			return;
		}

		// 1. clear pre data at first, and then input new data into db
		removeAllClients(ap);

		// 2. update new data
		try {
			List<String> clientMacs = new ArrayList<>();
			List<AhClientSession> clientList = new ArrayList<>(rowList.size());
			for (HmBo hmBo : rowList) {
				// maybe duplicate client if receive same client connect msg
				// during statistics query.
				AhAssociation association = (AhAssociation) hmBo;
				AhClientSession cuClientSession = createClientSessionFromAssociation(association);

				clientList.add(cuClientSession);
				clientMacs.add(cuClientSession.getClientMac());
			}
			
			//check the exist of client mac and set history client
			List<?> clientInfoList = queryAPMacAncClientInfoByClientMac(clientMacs,AhClientSession.CONNECT_STATE_UP);
			if (!clientInfoList.isEmpty()) {
				try {
					List<Object[]> argList = new ArrayList<>();
					List<Object[]> delArgList = new ArrayList<>();
					
					for(Object info : clientInfoList) {
						Object[] clientInfo = (Object[])info;
						
						//find new client info
						AhClientSession currentClient = null;
						for(AhClientSession clientSession: clientList) {
							if(clientSession.getClientMac().equalsIgnoreCase(clientInfo[1].toString())) {
								currentClient = clientSession;
								break;
							}
						}
						if(null == currentClient)
							continue;
						boolean newWirelessClient = currentClient.isWirelessClient();
						boolean wirelessClient = Boolean.parseBoolean(clientInfo[2].toString());
						int	handleType;
						if(newWirelessClient && wirelessClient) {
							handleType = 1;
						} else if (newWirelessClient && !wirelessClient) {
							handleType = 2;
						} else if (!newWirelessClient && wirelessClient) {
							handleType = 3;
						} else {
							//??, all are wired client
							handleType = 2;
						}
						if(handleType == 1) {
							//change the exist active client to history client
							Object[] arg = new Object[5];
							arg[0] = currentClient.getStartTimeStamp();
							arg[1] = currentClient.getStartTimeZone();
							arg[2] = AhClientSession.CONNECT_STATE_DOWN;
							arg[3] = clientInfo[1];
							arg[4] = AhClientSession.CONNECT_STATE_UP;
							argList.add(arg);
							SimpleHiveAp hiveAp = cacheMgmt.getSimpleHiveAp(clientInfo[0].toString());
							if (null != hiveAp) {
								// update active client in cache.
								
								int radioMode = AhInterfaceStats.RADIOTYPE_24G;
								if (Boolean.parseBoolean(clientInfo[2].toString())){
									radioMode = calcClientRadioType(1,Integer.parseInt(clientInfo[3].toString()));
								} else {
									radioMode = AhInterfaceStats.RADIOTYPE_OTHER;
								}
								module.activeClientRemove(hiveAp, 1, radioMode);
							}
						} else if (handleType == 2) {
							//remove exist active client
							Object[] arg = new Object[2];
							arg[0] = clientInfo[1];
							arg[1] = AhClientSession.CONNECT_STATE_UP;
							delArgList.add(arg);
							SimpleHiveAp hiveAp = cacheMgmt.getSimpleHiveAp(clientInfo[0].toString());
							if (null != hiveAp) {
								// update active client in cache.
								int radioMode = AhInterfaceStats.RADIOTYPE_24G;
								if (Boolean.parseBoolean(clientInfo[2].toString())){
									radioMode = calcClientRadioType(1,Integer.parseInt(clientInfo[3].toString()));
								} else {
									radioMode = AhInterfaceStats.RADIOTYPE_OTHER;
								}
								module.activeClientRemove(hiveAp, 1, radioMode);
							}
						} else if (handleType == 3) {
							//discard
							// TODO
							clientList.remove(currentClient);
						}
					}
					// update database
					if(!argList.isEmpty()) {
						DBOperationUtil.executeBatchUpdate("update ah_clientsession set endtimestamp = ? ,endtimezone = ? , connectstate = ? where clientMac = ? and connectstate = ?",
							argList);
					}
					if(!delArgList.isEmpty()) {
						DBOperationUtil.executeBatchUpdate("delete from ah_clientsession where clientMac = ? and connectstate = ?",
							delArgList);
					}
					
				} catch (Exception e) {
					DebugUtil
							.performanceDebugWarn(
									"BeClientSessionProcessor.handleClientUp(): Exception when update client information into db",
									e);
				}
			}
			//add to insert list
			dataList.addAll(clientList);
			ReportCacheMgmt.getInstance().refreshClientInfos(clientList);
		} catch (Exception e) {
			DebugUtil
					.performanceDebugWarn(
							"BeClientSessionProcessor.updateActiveClients(): update active clients data failed! Exception message: ",
							e);
			systemLog(HmSystemLog.LEVEL_MAJOR, "Failed to update new active clients data! AP="
					+ ap.getHostname());
		}
	}

	/**
	 * create AhCurrentClientSession from AhAssociation obj
	 * 
	 * @param association
	 *            -
	 * @return -
	 */
	public AhClientSession createClientSessionFromAssociation(AhAssociation association) {
		SimpleHiveAp ap = cacheMgmt.getSimpleHiveAp(association.getApMac());
		HmDomain domain = cacheMgmt.getCacheDomainById(ap.getDomainId());

		AhClientSession currentClient = new AhClientSession();
		currentClient.setApMac(association.getApMac());
		currentClient.setApName(association.getApName());
		currentClient.setApSerialNumber(association.getApSerialNumber());
		currentClient.setClientIP(association.getClientIP());
		currentClient.setClientMac(association.getClientMac());
		currentClient.setClientUsername(association.getClientUsername());
		currentClient.setIfIndex(association.getIfIndex());
		currentClient.setMemo("");
		currentClient.setStartTimeStamp(association.getClientAssociateTime() * 1000);
		currentClient.setClientAuthMethod(association.getClientAuthMethod());
		currentClient.setClientChannel(association.getClientChannel());
		currentClient.setClientEncryptionMethod(association.getClientEncryptionMethod());
		currentClient.setClientHostname(association.getClientHostname());
		currentClient.setClientMACProtocol(association.getClientMACProtocol());
		currentClient.setClientSSID(association.getClientSSID());
		currentClient.setClientUserProfId(association.getClientUserProfId());
		currentClient.setClientVLAN(association.getClientVLAN());
		currentClient.setClientCWPUsed(association.getClientCWPUsed());
		currentClient.setClientBSSID(association.getClientBSSID());
		currentClient.setMapId(ap.getMapContainerId());
		currentClient.setOwner(domain);
		currentClient.setStartTimeZone(association.getTimeStamp().getTimeZone());
		currentClient.setSimulated(ap.isSimulated());
		currentClient.setIfName(association.getIfName());
		currentClient.setClientOsInfo(association.getClientOsInfo());
		currentClient.setClientRssi(association.getClientRSSI());
		currentClient.setUserProfileName(association.getUserProfileName());
		currentClient.setSNR(association.getSNR());
		currentClient.setClientMacBasedAuthUsed(association.getClientMacBasedAuthUsed());
		currentClient.setManagedStatus(association.getManagedStatus());
		
		//os detection
		if(association.getOs_option55() != null && association.getClientOsInfo().equalsIgnoreCase("unknown")){
			String osversion = cacheMgmt.getClientOsInfoFromCacheByOption55(association.getOs_option55(), currentClient.getOwner());
			if(osversion != null){
				currentClient.setClientOsInfo(osversion);
				currentClient.setOs_option55(association.getOs_option55());
			}else{
				currentClient.setClientOsInfo(association.getClientOsInfo());
				currentClient.setOs_option55(association.getOs_option55());
			}
		}else{
			currentClient.setClientOsInfo(association.getClientOsInfo());
			currentClient.setOs_option55(association.getOs_option55());
		}
		
		//set client score
		currentClient.setIpNetworkConnectivityScore(association.getIpNetworkConnectivityScore());
		currentClient.setApplicationHealthScore(association.getApplicationHealthScore());
		currentClient.setSlaConnectScore(association.getSlaConnectScore());
		currentClient.setOverallClientHealthScore(association.getOverallClientHealthScore());

		// apply modifications for clients
		AhClientEditValues editValues = cacheMgmt.getClientEditValues(association.getClientMac(),
				domain);
		if (editValues != null) {
			if (currentClient.getClientHostname() == null
					|| currentClient.getClientHostname().trim().isEmpty()) {
				currentClient.setClientHostname(editValues.getClientHostname());
			}

			if (currentClient.getClientUsername() == null
					|| currentClient.getClientUsername().trim().isEmpty()) {
				currentClient.setClientUsername(editValues.getClientUsername());
			}

			if ((currentClient.getClientIP() == null
					|| currentClient.getClientIP().trim().isEmpty() || currentClient
					.getClientIP().equals("0.0.0.0"))
					&& !editValues.getClientIP().isEmpty()) {
				currentClient.setClientIP(editValues.getClientIP());
			}

			if (!editValues.getComment1().isEmpty()) {
				currentClient.setComment1(editValues.getComment1());
			}

			if (!editValues.getComment2().isEmpty()) {
				currentClient.setComment2(editValues.getComment2());
			}
		}
		if(currentClient.getClientAuthMethod() == AhAssociation.CLIENTAUTHMETHOD_WPAPSK ||
				currentClient.getClientAuthMethod() == AhAssociation.CLIENTAUTHMETHOD_WPA2PSK  ||
				currentClient.getClientCWPUsed() == AhAssociation.CLIENT_CWP_USED)
		{
			editValues = cacheMgmt.getClientEditValues(association.getClientMac(),
					domain,AhClientEditValues.TYPE_SELF_REGISTER,currentClient.getClientSSID());
			if(editValues != null) {
				if(currentClient.getClientUsername() == null || 
						currentClient.getClientUsername().trim().isEmpty())
					currentClient.setClientUsername(editValues.getClientUsername());
				currentClient.setEmail(editValues.getEmail());
				currentClient.setCompanyName(editValues.getCompanyName());
			}
		}

		return currentClient;
	}

	public void setModule(BePerformModuleImpl module) {
		this.module = module;
	}

	/**
	 * write system log
	 * 
	 * @param sLevel
	 *            -
	 * @param strComment
	 *            -
	 */
	private void systemLog(short sLevel, String strComment) {
		module.setSystemLog(sLevel, HmSystemLog.FEATURE_MONITORING, strComment);
	}

	private void shutdownScheduler() {
		if (scheduler != null && !scheduler.isShutdown()) {
			// Disable new tasks from being submitted.
			scheduler.shutdown();
		}

		if (scheduler_ForClient != null && !scheduler_ForClient.isShutdown()) {
			scheduler_ForClient.shutdown();
		}

		BeLogTools
				.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
						"<BE Thread> Client session processor - scheduler and scheduler for client is shutdown");
	}

	public boolean shutdown() {
		BoObserver.removeBoEventListener(this);
		shutdownScheduler();
		isContinue = false;
		eventQueue.clear();
		BeBaseEvent stopThreadEvent = new BeBaseEvent();
		eventQueue.offer(stopThreadEvent);
		return true;
	}

	/**
	 * add event to client session queue
	 * 
	 * @param event
	 *            -
	 */
	public void addEvent(BeBaseEvent event) {
		try {
			if(eventQueue.offer(event))
			{
				if(eventQueue.size() % 1000 == 999)
					DebugUtil.performanceDebugWarn("Queue size of client is "+eventQueue.size());
				
				if(lostEventCount > 0) {
					DebugUtil.performanceDebugError("BeClientSessionProcessor:addEvent, Lost "+ lostEventCount +" events");
					lostEventCount = 0;
				}
			}
			else {
				lostEventCount ++;
			}
		} catch (Exception e) {
			lostEventCount ++;
//			DebugUtil.performanceDebugError("Exception while add event to client event queue", e);
		}
	}

	/**
	 * get event from client session queue
	 * 
	 * @return BeBaseEvent or null
	 */
	private BeBaseEvent getEvent() {
		try {
			// if(eventQueue.size() >= 100 && (eventQueue.size()%100) == 0)
			// DebugUtil.performanceDebugWarn("Get event: "+eventQueue.size());
			return eventQueue.take();
		} catch (Exception e) {
			DebugUtil.performanceDebugError("Exception while get event from client event queue", e);
			return null;
		}
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
					"<BE Thread> Client session processor - event processor is running...");

			while (isContinue) {
				try {
					// take() method blocks
					BeBaseEvent event = getEvent();
					if (null == event)
						continue;
					if (event.getEventType() == BeEventConst.COMMUNICATIONEVENTTYPE) {
						BeCommunicationEvent communicationEvent = (BeCommunicationEvent) event;

						// put interested communication event in queue
						int msgType = communicationEvent.getMsgType();
						if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_STATISTICRESULT) {
							BeStatisticResultEvent statEvent = (BeStatisticResultEvent) communicationEvent;
							// validate stats serial number
							int statsSerialNumber = statEvent.parseStatsSerialNum();
							if (statsSerialNumber == 0) {
								// parse statistics event to bo list and then insert into insertList
								//handleStatsPacket_ReportByAP(statEvent);
							} else if (is4ClientTrapStatResult(statsSerialNumber)) {
								handleStatResult4ClientTrap(statEvent);
							} else {
								// refresh client or sync refresh client of ap list.
								handleStatsPacket(statEvent);
							}
						}
						if(msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_TRAPEVENT) {
							BeTrapEvent trapEvent = (BeTrapEvent)event;
							if(trapEvent.getTrapType() == BeTrapEvent.TYPE_CLIENTOSINFOMATION) {
								handleClientOSInfo(trapEvent);
							}
							else {
								ClientTrap clientTrap = convertFromBeTrapEvent(trapEvent);
								if (null == clientTrap) {
									continue;
								}
								handleClientTrap(clientTrap);
							}
						}
						if (insertList.size() >= MAX_CACHE_SIZE) {
							refreshDb();
						}
					} else if (event.getEventType() == BeEventConst.AH_TIMEOUT_EVENT) {
						refreshDb();
					} else if (event.getEventType() == BeEventConst.AH_CLIENT_SESSION_REMOVE_EVENT) {
						BeClientSessionEvent clientSessionEvent = (BeClientSessionEvent) event;
						removeAllClients(clientSessionEvent.getAp());
					}
				} catch (Exception e) {
					DebugUtil.performanceDebugWarn("Exception in client processor thread", e);
				} catch (Error e) {
					DebugUtil.performanceDebugWarn("Error in client processor thread", e);
				}
			}
			refreshDb();
			transferDb();
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"<BE Thread> Client session processor - event processor is shutdown.");
		}
	}

	/**
	 * convert SnmpTrapEvent to ClientTrap
	 * 
	 * @param event
	 *            -
	 * @return -
	 */
	private ClientTrap convertFromBeTrapEvent(BeTrapEvent event) {
		ClientTrap clientTrap = null;
		if (event.getTrapType() == BeTrapEvent.TYPE_CONNECTCHANGE) {
			int objectType = event.getObjectType();
			if (objectType == BeTrapEvent.TRAP_OBJECT_TYPE_CLIENT_INDEX) {
				clientTrap = new ClientTrap();
				int connectState = event.getCurrentState();
				if (connectState == BeTrapEvent.TRAP_STATE_UP_INDEX) {
					clientTrap.setType(ClientTrap.TYPE_STATE_UP);
					clientTrap.setApMac(event.getApMac());
					clientTrap.setApName(event.getApName());
					clientTrap.setClientIp(event.getClientIP());
					clientTrap.setClientMac(event.getRemoteID());
					clientTrap.setIfIndex(event.getIfIndex());
					clientTrap.setDesc(event.getDescribe());
					clientTrap.setClientIp(event.getClientIP());
					clientTrap.setClientHostName(event.getClientHostName());
					clientTrap.setClientUserName(event.getClientUserName());
					clientTrap.setSSID(event.getClientSSID());
					clientTrap.setClientCWPUsed(event.getClientCWPUsed());
					clientTrap.setClientAuthMethod(event.getClientAuthMethod());
					clientTrap.setClientEncryptionMethod(event.getClientEncryptionMethod());
					clientTrap.setClientMacProtocol(event.getClientMacProtocol());
					clientTrap.setClientVLAN(event.getClientVLAN());
					clientTrap.setClientUserProfId(event.getClientUserProfId());
					clientTrap.setClientChannel(event.getClientChannel());
					clientTrap.setClientBSSID(event.getClientBSSID());
					clientTrap.setAssociateTime(event.getAssociationTime());
					clientTrap.setAssociateTimeZone(event.getTimeZone());
					clientTrap.setIfName(event.getIfName());
					clientTrap.setClientRssi(event.getClientRssi());
					clientTrap.setClientOsInfo(event.getClientOsInfo());
					clientTrap.setUserProfileName(event.getUserProfileName());
					clientTrap.setSNR(event.getSNR());
					clientTrap.setClientMacBasedAuthUsed(event.getClientMacBasedAuthUsed());
					clientTrap.setManagedStatus(event.getManagedStatus());
					
					clientTrap.setOs_option55(event.getOs_option55());
				} else if (connectState == BeTrapEvent.TRAP_STATE_DOWN_INDEX) {
					clientTrap.setType(ClientTrap.TYPE_STATE_DOWN);
					clientTrap.setApMac(event.getApMac());
					clientTrap.setApName(event.getApName());
					clientTrap.setClientMac(event.getRemoteID());
					clientTrap.setIfIndex(event.getIfIndex());
					clientTrap.setDesc(event.getDescribe());
					clientTrap.setAssociateTime(event.getAssociationTime());
					clientTrap.setAssociateTimeZone(event.getTimeZone());
				} else {
					DebugUtil
							.performanceDebugWarn("BeClientSessionProcessor.convertFromSnmpTrapEvent:unknown connect state "
									+ connectState);

					return null;
				}
			}
		} else if (event.getTrapType() == BeTrapEvent.TYPE_CLIENTINFOMATION) {
			clientTrap = new ClientTrap();
			clientTrap.setType(ClientTrap.TYPE_CLIENT_INFO);
			clientTrap.setApMac(event.getApMac());
			clientTrap.setClientMac(event.getRemoteID());
			clientTrap.setClientIp(event.getClientIP());
			clientTrap.setClientHostName(event.getClientHostName());
			clientTrap.setClientUserName(event.getClientUserName());
			clientTrap.setSSID(event.getClientSSID());
			clientTrap.setManagedStatus(event.getManagedStatus());
		}
		return clientTrap;
	}

	/**
	 * handle client trap
	 * 
	 * @param clientTrap
	 *            -
	 */
	private void handleClientTrap(ClientTrap clientTrap) {
		SimpleHiveAp ap = cacheMgmt.getSimpleHiveAp(clientTrap.getApMac());
		if (ap == null || ap.getManageStatus() != HiveAp.STATUS_MANAGED) {
			return;
		}

		if (clientTrap.getType() == ClientTrap.TYPE_STATE_UP)
		{
			handleClientUp(clientTrap, ap);
		}
		else if (clientTrap.getType() == ClientTrap.TYPE_STATE_DOWN)
			handleClientDown(clientTrap, ap);
		else if (clientTrap.getType() == ClientTrap.TYPE_CLIENT_INFO)
			handleClientInfo(clientTrap, ap);
	}
	
	private ClientInfoBean createClientInfoBean(ClientTrap client, SimpleHiveAp ap, int trapEventKey) {
		if (client == null) {
			return null;
		} 
		ClientInfoBean bean = new ClientInfoBean();
		if (trapEventKey == TRAP_EVENT_UP) {
			HmDomain owner = cacheMgmt.getCacheDomainById(ap.getDomainId());
			String convertedOsInfo = CacheMgmt.getInstance().getClientOsInfoFromCacheByOption55(client.getOs_option55(), owner);
			bean.setAllClientOsInfo(client.getClientOsInfo(), client.getOs_option55(), convertedOsInfo);
			bean.setHostName(client.getClientHostName());
			bean.setProfileName(client.getUserProfileName());
			bean.setSsid(client.getSSID());
			bean.setUserName(client.getClientUserName());
			bean.setVlan(client.getClientVLAN());
			bean.setOnline(true);
			if (client.getClientChannel() <= 0) {
				bean.setRadioType(-1);
			} else {
				bean.setRadioType(client.getClientMacProtocol());
			}
			bean.setClientMac(client.getClientMac());
			bean.setDomainId(ap.getDomainId());
		} 
		else if (trapEventKey == TRAP_EVENT_DOWN) {
			bean.setOnline(false);
		}
		else if (trapEventKey == TRAP_EVENT_INFO){
			bean.setHostName(client.getClientHostName());
			bean.setSsid(client.getSSID());
			bean.setUserName(client.getClientUserName());
			bean.setOnline(true);
    		bean.setClientMac(client.getClientMac());
    		bean.setDomainId(ap.getDomainId());
		}
		return bean;
	}

	private void applyEditValues(AhClientSession currentClient, ClientInfoBean clientInfoBean, HmDomain domain) {
		AhClientEditValues editValues = cacheMgmt.getClientEditValues(
					currentClient.getClientMac(), domain);
		if (editValues != null) {
			if (currentClient.getClientHostname() == null
					|| currentClient.getClientHostname().trim().isEmpty()) {
				currentClient.setClientHostname(editValues.getClientHostname());
			}
			if (clientInfoBean != null && StringUtils.isBlank(clientInfoBean.getHostName())) {
				clientInfoBean.setHostName(editValues.getClientHostname());
			}
	
			if (currentClient.getClientIP()== null || currentClient.getClientIP().trim().isEmpty()) {
				currentClient.setClientIP(editValues.getClientIP());
			}

			if (currentClient.getClientUsername() == null
					|| currentClient.getClientUsername().trim().isEmpty()) {
				currentClient.setClientUsername(editValues.getClientUsername());
			}
			
			if (clientInfoBean != null && StringUtils.isBlank(clientInfoBean.getUserName())) {
				clientInfoBean.setUserName(editValues.getClientUsername());
			}

			if (!editValues.getComment1().isEmpty()) {
				currentClient.setComment1(editValues.getComment1());
			}

			if (!editValues.getComment2().isEmpty()) {
				currentClient.setComment2(editValues.getComment2());
			}
		}
		if(currentClient.getClientAuthMethod() == AhAssociation.CLIENTAUTHMETHOD_WPAPSK ||
				currentClient.getClientAuthMethod() == AhAssociation.CLIENTAUTHMETHOD_WPA2PSK  ||
				currentClient.getClientCWPUsed() == AhAssociation.CLIENT_CWP_USED)
		{
			editValues = cacheMgmt.getClientEditValues(currentClient.getClientMac(),
					domain,AhClientEditValues.TYPE_SELF_REGISTER,currentClient.getClientSSID());
			if(editValues != null) {
				if(currentClient.getClientUsername() == null || 
						currentClient.getClientUsername().trim().isEmpty()) {
					currentClient.setClientUsername(editValues.getClientUsername());
				}

				if (clientInfoBean != null && StringUtils.isBlank(clientInfoBean.getUserName())) {
					clientInfoBean.setUserName(editValues.getClientUsername());
				}
				currentClient.setEmail(editValues.getEmail());
				currentClient.setCompanyName(editValues.getCompanyName());
			}
		}
	}

	/**
	 * handle client up trap
	 * 
	 * @param clientTrap
	 *            -
	 * @param ap
	 *            -
	 */
	@SuppressWarnings("unchecked")
	private void handleClientUp(ClientTrap clientTrap, SimpleHiveAp ap) {
		// check existing record, if same client exists, change the connect state check database
		List<Object[]> clientInfoList = (List<Object[]>)queryClientInfoByClientMac(clientTrap.getClientMac(),
				AhClientSession.CONNECT_STATE_UP);
		if (!clientInfoList.isEmpty()) {
			// update database
			try {
				int	handleType;
				if(clientInfoList.size() != 1) {
					//this should never happened, if happened change the exist active client to history client
					handleType = 1;
				} else {
					Object[] clientInfo = clientInfoList.get(0);
					boolean wirelessClient = Boolean.parseBoolean(clientInfo[1].toString());
					boolean newWirelessClient = clientTrap.getClientChannel() > 0;
					if(newWirelessClient && wirelessClient) {
						handleType = 1;
					} else if (newWirelessClient && !wirelessClient) {
						handleType = 2;
					} else if (!newWirelessClient && wirelessClient) {
						handleType = 3;
					} else {
						//??, all are wired client
						handleType = 2;
					}
				}
				if(handleType == 1) {
					//change the exist active client to history client
					DBOperationUtil.executeUpdate("update ah_clientsession set endtimestamp = ? ,endtimezone = ? , connectstate = ? where clientMac = ? and connectstate = ?",
							new Object[] {
							clientTrap.getAssociateTime() * 1000,
							clientTrap.getAssociateTimeZone(),
							AhClientSession.CONNECT_STATE_DOWN, clientTrap.getClientMac(),
							AhClientSession.CONNECT_STATE_UP });
					for (Object[] clientInfo : clientInfoList) {
						SimpleHiveAp hiveAp = cacheMgmt.getSimpleHiveAp(clientInfo[0].toString());
						if (null != hiveAp) {
							// update active client in cache.
							int radioMode = AhInterfaceStats.RADIOTYPE_24G;
							if (Boolean.parseBoolean(clientInfo[1].toString())){
								radioMode = calcClientRadioType(1,Integer.parseInt(clientInfo[2].toString()));
							} else {
								radioMode = AhInterfaceStats.RADIOTYPE_OTHER;
							}
							module.activeClientRemove(hiveAp, 1, radioMode);
						}
					}
				} else if (handleType == 2) {
					//remove exist active client
					DBOperationUtil.executeUpdate("delete from ah_clientsession where clientMac = ? and connectstate = ?",
							new Object[] {
							clientTrap.getClientMac(),
							AhClientSession.CONNECT_STATE_UP });
					for (Object[] clientInfo : clientInfoList) {
						SimpleHiveAp hiveAp = cacheMgmt.getSimpleHiveAp(clientInfo[0].toString());
						if (null != hiveAp) {
							// update active client in cache.
							int radioMode = AhInterfaceStats.RADIOTYPE_24G;
							if (Boolean.parseBoolean(clientInfo[1].toString())){
								radioMode = calcClientRadioType(1,Integer.parseInt(clientInfo[2].toString()));
							} else {
								radioMode = AhInterfaceStats.RADIOTYPE_OTHER;
							}
							module.activeClientRemove(hiveAp, 1, radioMode);
						}
					}
				} else if (handleType == 3) {
					//discard the client event
					
					//TODO
					return;
				}
			} catch (Exception e) {
				DebugUtil
						.performanceDebugWarn(
								"BeClientSessionProcessor.handleClientUp(): Exception when update client information into db",
								e);
			}
		}
		// check cache list
		for (HmBo bo : insertList) {
			if (bo instanceof AhClientSession) {
				AhClientSession currentClient = (AhClientSession) bo;
				if (currentClient.getClientMac().equals(clientTrap.getClientMac())
						&& currentClient.getConnectstate() == AhClientSession.CONNECT_STATE_UP) {
					boolean wirelessClient = currentClient.isWirelessClient();
					boolean newWirelessClient = clientTrap.getClientChannel() > 0;
					int	handleType;
					if(newWirelessClient && wirelessClient) {
						handleType = 1;
					} else if (newWirelessClient && !wirelessClient) {
						handleType = 2;
					} else if (!newWirelessClient && wirelessClient) {
						handleType = 3;
					} else {
						//??, all are wired client
						handleType = 2;
					}
					if(handleType == 1) {
						//change the exist active client to history client
						currentClient.setConnectstate(AhClientSession.CONNECT_STATE_DOWN);
						currentClient.setEndTimeStamp(System.currentTimeMillis());
						currentClient.setEndTimeZone(TimeZone.getDefault().getID());
					} else if (handleType == 2) {
						//remove exist active client
						insertList.remove(bo);
					} else if (handleType == 3) {
						//discard the client event
						return;
					}
					
					break;
				}
			}
		}
		
		// add client information
		AhClientSession currentBo = new AhClientSession();
		currentBo.setApMac(clientTrap.getApMac());
		currentBo.setApName(clientTrap.getApName());
		currentBo.setClientMac(clientTrap.getClientMac());
		currentBo.setIfIndex(clientTrap.getIfIndex());
		currentBo.setIfName(clientTrap.getIfName());
		currentBo.setMemo(clientTrap.getDesc());
		currentBo.setStartTimeStamp(clientTrap.getAssociateTime() * 1000);
		currentBo.setStartTimeZone(clientTrap.getAssociateTimeZone());
		currentBo.setClientIP(clientTrap.getClientIp());
		if(currentBo.getClientIP() == null || currentBo.getClientIP().equalsIgnoreCase("0.0.0.0")
				|| currentBo.getClientIP().startsWith("169.254.")) {
			currentBo.setIpNetworkConnectivityScore((byte)0);
			currentBo.setOverallClientHealthScore((byte)0);
		}
 			
		currentBo.setClientUsername(clientTrap.getClientUserName());
		currentBo.setApSerialNumber(ap.getSerialNumber());
		currentBo.setMapId(ap.getMapContainerId());
		currentBo.setOwner(cacheMgmt.getCacheDomainById(ap.getDomainId()));
		currentBo.setComment1(clientTrap.getComment1());
		currentBo.setComment2(clientTrap.getComment2());

		currentBo.setClientHostname(clientTrap.getClientHostName());
		currentBo.setClientUsername(clientTrap.getClientUserName());
		currentBo.setClientSSID(clientTrap.getSSID());
		currentBo.setClientCWPUsed(clientTrap.getClientCWPUsed());
		currentBo.setClientAuthMethod(clientTrap.getClientAuthMethod());
		currentBo.setClientEncryptionMethod(clientTrap.getClientEncryptionMethod());
		currentBo.setClientMACProtocol(clientTrap.getClientMacProtocol());
		currentBo.setClientVLAN(clientTrap.getClientVLAN());
		currentBo.setClientUserProfId(clientTrap.getClientUserProfId());
		currentBo.setClientChannel(clientTrap.getClientChannel());
		currentBo.setClientBSSID(clientTrap.getClientBSSID());
		currentBo.setSimulated(ap.isSimulated());
		currentBo.setClientRssi(clientTrap.getClientRssi());
		currentBo.setUserProfileName(clientTrap.getUserProfileName());
		currentBo.setSNR(clientTrap.getSNR());
		currentBo.setClientMacBasedAuthUsed(clientTrap.getClientMacBasedAuthUsed());
		currentBo.setManagedStatus(clientTrap.getManagedStatus());

		checkDoubtfulValue(clientTrap, ap);
		ClientInfoBean clientInfoBean = null;
		if (clientTrap != null && clientTrap.getClientMac() != null) {
			clientInfoBean = createClientInfoBean(clientTrap, ap, TRAP_EVENT_UP);
		}
		
		applyEditValues(currentBo, clientInfoBean, cacheMgmt.getCacheDomainById(ap.getDomainId()));
		
		if (clientInfoBean != null) {
			ReportCacheMgmt.getInstance().saveClientInfo(clientTrap.getClientMac(), clientInfoBean);
		}
		
		clientInfoBean = ReportCacheMgmt.getInstance().getClientInfoBean(clientTrap.getClientMac(), ap.getDomainId());

		if(clientTrap.getOs_option55() != null && !clientTrap.getOs_option55().isEmpty()){
			String os_name = cacheMgmt.getClientOsInfoFromCacheByOption55(clientTrap.getOs_option55(), cacheMgmt.getCacheDomainById(ap.getDomainId()));
			if(os_name == null){
				currentBo.setClientOsInfo("unknown");
			}else{
				currentBo.setClientOsInfo(os_name);
			}
			currentBo.setOs_option55(clientTrap.getOs_option55());
		}else{
			if(clientInfoBean != null){
				if(clientTrap.getOs_option55() != null 
						&& clientTrap.getClientOsInfo() != null 
						&& clientTrap.getOs_option55().isEmpty() 
						&& !clientTrap.getClientOsInfo().isEmpty()){
					currentBo.setClientOsInfo(clientTrap.getClientOsInfo());
				}else{
					currentBo.setOs_option55(clientInfoBean.getOption55());
					currentBo.setClientOsInfo(clientInfoBean.getOsInfo());
				}
			}else{
				currentBo.setClientOsInfo(clientTrap.getClientOsInfo());
			}
		}
		
		if (!insertList.add(currentBo)) {
			DebugUtil
					.performanceDebugError("BeClientSessionProcessor.handleClientUp():Fail to add client information to insert list");
		} else {
			// update active client in cache.
			// module.activeClientAdd(ap, 1);
		}
		
		//Client history track when a client associate with device  
		ClientHistoryTracking.associated
		(
				currentBo.getStartTimeStamp(),
				currentBo.getClientMac(),
				currentBo.getClientHostname(),
				currentBo.getClientOsInfo(),
				currentBo.getOs_option55(),
				currentBo.getApMac(),
//				Long.parseLong(currentBo.getApMac(), 16),
				currentBo.getOwner().getId(),
				currentBo.getClientUsername(),
				currentBo.getUserProfileName(),
				currentBo.getEmail(),
				currentBo.getClientSSID(),
				currentBo.getClientAuthMethod(),
				AhEncoder.int2bytes(AhEncoder.ip2Int(currentBo.getClientIP())),
				null
		);
		
		// check HiveAP software version, request association data only for 3.2 before version.
		if (NmsUtil.compareSoftwareVersion("3.2.0.0", ap.getSoftVer()) <= 0) {
			return;
		}

		// send request to get ahAssociation
		BeGetStatisticEvent req = new BeGetStatisticEvent();
		req.setSimpleHiveAp(ap);
		int statsSerialNum = HmBeCommunicationUtil.getSequenceNumber();
		req.setSequenceNum(statsSerialNum);
		Map<Byte, List<String>> statsTableInfo = new HashMap<>();
		List<String> tableInfo = new ArrayList<>(2);
		tableInfo.add(String.valueOf(clientTrap.getIfIndex()));
		tableInfo.add(clientTrap.getClientMac());
		statsTableInfo.put(BeCommunicationConstant.STATTABLE_AHASSOCIATION, tableInfo);
		req.setStatsTableIndexMap(statsTableInfo);
		try {
			req.buildPacket();
		} catch (Exception e) {
			DebugUtil
					.performanceDebugWarn(
							"BeClientSessionProcessor.handleClientUp(): build BeGetStatisticEvent catch exception",
							e);
			return;
		}

		int serialNum = HmBeCommunicationUtil.sendRequest(req);
		if (serialNum == BeCommunicationConstant.SERIALNUM_SENDREQUESTFAILED) {
			// connect closed
			DebugUtil
					.performanceDebugWarn("BeClientSessionProcessor.handleClientUp():Connection to capwap process closed! Failed to send request for getting statistics data.");
			return;
		}

		module.addRequestObj(serialNum, req);
		statsSerialNumSet4ClientTrap.add(statsSerialNum);
	}

	/**
	 * handle client down trap
	 * 
	 * @param client
	 *            -
	 * @param ap
	 *            -
	 */
	private void handleClientDown(ClientTrap client, SimpleHiveAp ap) {
		if (ap == null || client == null)return;
		if (client.getClientMac() != null) {
			ReportCacheMgmt.getInstance().saveClientInfo(client.getClientMac(), createClientInfoBean(client, ap, TRAP_EVENT_DOWN));
		}
		// check existing record, if same client exists, change check cache list
		boolean inCache = false;
		for (HmBo bo : insertList) {
			if (bo instanceof AhClientSession) {
				AhClientSession currentClient = (AhClientSession) bo;
				if (currentClient.getClientMac().equals(client.getClientMac())
						&& currentClient.getApMac().equals(client.getApMac())
						&& currentClient.getConnectstate() == AhClientSession.CONNECT_STATE_UP) {
					currentClient.setConnectstate(AhClientSession.CONNECT_STATE_DOWN);
					currentClient.setEndTimeStamp(client.getAssociateTime() * 1000);
					currentClient.setEndTimeZone(client.getAssociateTimeZone());
					inCache = true;
					break;
				}
			}
		}
		if (!inCache) {
			// update database
			try {
//				int updates = QueryUtil.updateBo(AhClientSession.class,
//						"endtimestamp = :s1, endtimezone = :s2, connectstate = :s3",
//						new FilterParams("clientMac = :s4 and connectstate = :s5 and apMac = :s6",
//								new Object[] { client.getAssociateTime() * 1000,
//										client.getAssociateTimeZone(),
//										AhClientSession.CONNECT_STATE_DOWN, client.getClientMac(),
//										AhClientSession.CONNECT_STATE_UP, ap.getMacAddress() }));
				
				int updatesWire = DBOperationUtil.executeUpdate("update ah_clientsession set endtimestamp=?,endtimezone=?,connectstate=? where clientMac = ? and connectstate = ? and apMac = ? and wirelessClient= ? ",
						new Object[] { client.getAssociateTime() * 1000,
						client.getAssociateTimeZone(),
						AhClientSession.CONNECT_STATE_DOWN, client.getClientMac(),
						AhClientSession.CONNECT_STATE_UP, ap.getMacAddress(), false });
				if (updatesWire>0) {
					module.activeClientRemove(ap, updatesWire, AhInterfaceStats.RADIOTYPE_OTHER);
				}
				
				int updates24 = DBOperationUtil.executeUpdate("update ah_clientsession set endtimestamp=?,endtimezone=?,connectstate=? where clientMac = ? and connectstate = ? and apMac = ? " +
						" and wirelessClient=? and (clientMACProtocol=? or clientMACProtocol=? or clientMACProtocol=?)",
						new Object[] { client.getAssociateTime() * 1000,
						client.getAssociateTimeZone(),
						AhClientSession.CONNECT_STATE_DOWN, client.getClientMac(),
						AhClientSession.CONNECT_STATE_UP, ap.getMacAddress(), true,
						AhAssociation.CLIENTMACPROTOCOL_BMODE,AhAssociation.CLIENTMACPROTOCOL_GMODE,
						AhAssociation.CLIENTMACPROTOCOL_NGMODE});
				
				if (updates24>0) {
					module.activeClientRemove(ap, updates24, AhInterfaceStats.RADIOTYPE_24G);
				}
				
				int updates5 = DBOperationUtil.executeUpdate("update ah_clientsession set endtimestamp=?,endtimezone=?,connectstate=? where clientMac = ? and connectstate = ? and apMac = ? " +
						" and wirelessClient=? and (clientMACProtocol=? or clientMACProtocol=? or clientMACProtocol=?)",
						new Object[] { client.getAssociateTime() * 1000,
						client.getAssociateTimeZone(),
						AhClientSession.CONNECT_STATE_DOWN, client.getClientMac(),
						AhClientSession.CONNECT_STATE_UP, ap.getMacAddress(), true, 
						AhAssociation.CLIENTMACPROTOCOL_AMODE,AhAssociation.CLIENTMACPROTOCOL_NAMODE,
						AhAssociation.CLIENTMACPROTOCOL_ACMODE});
				
				// update active client in cache.
				if (updates5>0) {
					module.activeClientRemove(ap, updates5, AhInterfaceStats.RADIOTYPE_5G);
				}
				//module.activeClientRemove(ap, updates);
			} catch (Exception e) {
				DebugUtil
						.performanceDebugWarn(
								"BeClientSessionProcessor.handleClientDown(): Error when update client information into db",
								e);
			}
		}
		//Client history track when a client deassociate with device 
		if(null != client && null != ap){
			ClientHistoryTracking.deassociated
			(
				client.getAssociateTime() * 1000,
				client.getClientMac(),
				client.getSSID(),
				ap.getMacAddress(),
				ap.getDomainId(),
				client.getClientUserName()
			);
		}
	}

	/**
	 * handle client info trap
	 * 
	 * @param clientTrap
	 *            -
	 * @param ap
	 *            -
	 */
	private void handleClientInfo(ClientTrap clientTrap, SimpleHiveAp ap) {
		// check existing record, if same client exists, change
		// check cache list
		checkDoubtfulValue(clientTrap, ap);
		String clientMac = clientTrap.getClientMac();
		if (clientTrap != null && clientMac != null) {
			ReportCacheMgmt.getInstance().saveClientInfo(clientMac, this.createClientInfoBean(clientTrap, ap, TRAP_EVENT_INFO));
		}
		boolean inCache = false;
		for (HmBo bo : insertList) {
			if (bo instanceof AhClientSession) {
				AhClientSession currentClient = (AhClientSession) bo;
				if (currentClient.getClientMac().equals(clientTrap.getClientMac())
						&& currentClient.getApMac().equals(clientTrap.getApMac())
						&& currentClient.getConnectstate() == AhClientSession.CONNECT_STATE_UP) {
					currentClient.setClientIP(clientTrap.getClientIp());
					if(currentClient.getClientIP() != null && !currentClient.getClientIP().equalsIgnoreCase("0.0.0.0")
							&& !currentClient.getClientIP().startsWith("169.254.")) {
						currentClient.setIpNetworkConnectivityScore((byte)100);
						currentClient.setOverallClientHealthScore((byte)100);
					}
					else {
						currentClient.setIpNetworkConnectivityScore((byte)0);
						currentClient.setOverallClientHealthScore((byte)0);
					}
					currentClient.setClientHostname(clientTrap.getClientHostName());
					if(clientTrap.getClientUserName() != null &&
							!clientTrap.getClientUserName().equalsIgnoreCase(""))
						currentClient.setClientUsername(clientTrap.getClientUserName());
					currentClient.setClientSSID(clientTrap.getSSID());
					currentClient.setManagedStatus(clientTrap.getManagedStatus());
					inCache = true;
					break;
				}
			}
		}
		if (!inCache) {
			// update database
			try {
//				QueryUtil
//						.updateBo(
//								AhClientSession.class,
//								"clientIp = :s1, clientHostname = :s2, clientUsername = :s3, clientSSID = :s4",
//								new FilterParams("clientMac = :s5 and connectstate = :s6",
//										new Object[] { clientTrap.getClientIp(),
//												clientTrap.getClientHostName(),
//												clientTrap.getClientUserName(),
//												clientTrap.getSSID(), clientTrap.getClientMac(),
//												AhClientSession.CONNECT_STATE_UP }));
				
				List<AhClientSession> lsClient = DBOperationUtil.executeQuery(AhClientSession.class, null, 
						new FilterParams("clientMac = ? and apmac = ? and connectstate = ?",
						new Object[] {clientTrap.getClientMac(),
						clientTrap.getApMac(),
						AhClientSession.CONNECT_STATE_UP }));
				
				if(lsClient.size() == 1) {
					AhClientSession currentClient = lsClient.get(0);
					currentClient.setClientIP(clientTrap.getClientIp());
					if(currentClient.getClientIP() != null && !currentClient.getClientIP().equalsIgnoreCase("0.0.0.0")
							&& !currentClient.getClientIP().startsWith("169.254.")) {
						currentClient.setIpNetworkConnectivityScore((byte)100);
						currentClient.setOverallClientHealthScore((byte)100);
					}
					else {
						currentClient.setIpNetworkConnectivityScore((byte)0);
						currentClient.setOverallClientHealthScore((byte)0);
					}
					currentClient.setClientHostname(clientTrap.getClientHostName());
					if(clientTrap.getClientUserName() != null &&
							!clientTrap.getClientUserName().equalsIgnoreCase(""))
						currentClient.setClientUsername(clientTrap.getClientUserName());
					currentClient.setClientSSID(clientTrap.getSSID());
					currentClient.setManagedStatus(clientTrap.getManagedStatus());
					DBOperationUtil.updateBO(currentClient);
				}
				
//				DBOperationUtil.executeUpdate("update ah_clientsession set clientIp = ?, clientHostname=?, clientUsername=?, clientSSID=? where clientMac = ? and connectstate = ?",
//						new Object[] { clientTrap.getClientIp(),
//						clientTrap.getClientHostName(),
//						clientTrap.getClientUserName(),
//						clientTrap.getSSID(), clientTrap.getClientMac(),
//						AhClientSession.CONNECT_STATE_UP });
//				
//				//change the client health score if ip exist
//				if(clientTrap.getClientIp() != null && !clientTrap.getClientIp().equalsIgnoreCase("0.0.0.0")
//						&& !clientTrap.getClientIp().startsWith("169.254.")) {
//					DBOperationUtil.executeUpdate("update ah_clientsession set ipNetworkConnectivityScore = ?, overallClientHealthScore=? where (clientip = '' or clientip like '169.254.%' or clientip = ?) and clientMac = ? and connectstate = ?",
//							new Object[] { 100,
//							100,"0.0.0.0",
//							clientTrap.getClientMac(),
//							AhClientSession.CONNECT_STATE_UP });
//				}
//				else {
//					DBOperationUtil.executeUpdate("update ah_clientsession set ipNetworkConnectivityScore = ?, overallClientHealthScore=? where clientMac = ? and connectstate = ?",
//							new Object[] { 0,0,
//							clientTrap.getClientMac(),
//							AhClientSession.CONNECT_STATE_UP });
//				}
			} catch (Exception e) {
				DebugUtil
						.performanceDebugWarn(
								"BeClientSessionProcessor.handleClientInfo: Error when update client information into db",
								e);
			}
		}
		//Client history track when a client is changed
		if(null != clientTrap && null != ap){
			ClientHistoryTracking.clientInfo
			(
				clientTrap.getClientMac(),
				ap.getDomainId(),
				clientTrap.getClientIp(),
				clientTrap.getClientHostName(),
				clientTrap.getClientUserName(),
				clientTrap.getSSID(),
				null
			);
		}
	}
	
	/**
	 * handle client os info trap
	 * 
	 * @param trapEvent
	 *            -
	 */
	private void handleClientOSInfo(BeTrapEvent trapEvent) {
		// check existing record, if same client exists, change
		// check cache list
		HmDomain owner = cacheMgmt.getCacheDomainByName(HmDomain.GLOBAL_DOMAIN);
		SimpleHiveAp ap = cacheMgmt.getSimpleHiveAp(trapEvent.getApMac());
		if (ap != null && ap.getDomainId() != null) {
			HmDomain currentOwner = cacheMgmt.getCacheDomainById(ap.getDomainId());
			if (currentOwner != null) {
				owner = currentOwner;
			}
		}
		ReportCacheMgmt.getInstance().setClientOsInfo(trapEvent.getRemoteID(), trapEvent.getClientOsInfo(), trapEvent.getOs_option55(), owner);
		boolean inCache = false;
		for (HmBo bo : insertList) {
			if (bo instanceof AhClientSession) {
				AhClientSession currentClient = (AhClientSession) bo;
				if (currentClient.getClientMac().equals(trapEvent.getRemoteID())
						&& currentClient.getApMac().equals(trapEvent.getApMac())
						&& currentClient.getConnectstate() == AhClientSession.CONNECT_STATE_UP) {
					// os detection
					ClientInfoBean clientBean = ReportCacheMgmt.getInstance().getClientInfoBean(trapEvent.getRemoteID(), owner.getId());
					if (trapEvent.getOs_option55() != null
							&& trapEvent.getClientOsInfo().equalsIgnoreCase(
									"unknown")) {
						String osversion = cacheMgmt.getClientOsInfoFromCacheByOption55(
								trapEvent.getOs_option55(),
								currentClient.getOwner());
						if (osversion != null) {
							currentClient.setClientOsInfo(osversion);
							currentClient.setOs_option55(trapEvent
									.getOs_option55());
						} else {
							currentClient.setClientOsInfo(trapEvent
									.getClientOsInfo());
							currentClient.setOs_option55(trapEvent
									.getOs_option55());
						}
					} else {
						if(trapEvent.getClientOsInfo() != null && !trapEvent.getClientOsInfo().isEmpty()){
							currentClient.setClientOsInfo(trapEvent
									.getClientOsInfo());
						}else{
							if(clientBean != null){
								currentClient.setClientOsInfo(clientBean.getOsInfo());
							}
						}
						
						if(trapEvent.getOs_option55() != null && !trapEvent.getOs_option55().isEmpty()){
							currentClient.setOs_option55(trapEvent.getOs_option55());
						}else{
							currentClient.setOs_option55(clientBean.getOption55());
						}
						
					}
					
					inCache = true;
					break;
				}
			}
		}
		if (!inCache) {
			// update database
			try {
//				QueryUtil
//						.updateBo(
//								AhClientSession.class,
//								"clientOsInfo = :s1",
//								new FilterParams("clientMac = :s2 and connectstate = :s3",
//										new Object[] { trapEvent.getClientOsInfo(),
//												trapEvent.getRemoteID(),
//												AhClientSession.CONNECT_STATE_UP }));
				String osversion = null;
				if (trapEvent.getClientOsInfo() != null && trapEvent.getOs_option55() != null 
						&& trapEvent.getClientOsInfo().trim().equalsIgnoreCase(
								"unknown")) {
					osversion = cacheMgmt.getClientOsInfoFromCacheByOption55(trapEvent.getOs_option55(),owner);
				} 

				if(osversion == null){
					osversion = trapEvent.getClientOsInfo();
				}
				
				DBOperationUtil.executeUpdate("update ah_clientsession set clientOsInfo = ? , os_option55 = ? where clientmac=? and apmac=? and connectstate=?",
						new Object[] { osversion,trapEvent.getOs_option55(),
						trapEvent.getRemoteID(),
						trapEvent.getApMac(),
						AhClientSession.CONNECT_STATE_UP });
			} catch (Exception e) {
				DebugUtil
						.performanceDebugWarn(
								"BeClientSessionProcessor.handleClientOsInfo: Error when update client os information into db",
								e);
			}
		}
		//Client history track when client os info is changed
		if(null != ap){
			ClientHistoryTracking.clientOsInfo
			(
				trapEvent.getRemoteID(),
				ap.getDomainId(),
				trapEvent.getClientOsInfo()
			);
		}
	}

	private List<?> queryClientInfoByClientMac(String clientMac, byte connectState) {
		Object[] values = new Object[2];
		values[0] = clientMac;
		values[1] = connectState;

		return DBOperationUtil.executeQuery("select apmac, wirelessclient,clientMACProtocol from ah_clientsession where clientMac = ? and connectstate = ?",
					values);
	}

	private List<?> queryAPMacAncClientInfoByClientMac(List<String> clientMacs, byte connectState) {
		StringBuilder sql = new StringBuilder();
		sql.append("select apmac, clientmac, wirelessclient, clientMACProtocol from ah_clientsession where connectstate = ? and clientMac in ('0'");
		for (String clientMac : clientMacs) {
			sql.append(",?");
		}
		sql.append(")");
		List<Object> values = new ArrayList<>(clientMacs.size()+1);
		values.add(connectState);
		values.addAll(clientMacs);

		return DBOperationUtil.executeQuery(sql.toString(),values.toArray());
	}

	/**
	 * handle statistic result for retrieve association by client up trap
	 * 
	 * @param statEvent
	 *            -
	 */
	private void handleStatResult4ClientTrap(BeStatisticResultEvent statEvent) {
		// get stat time
		// Date statTime = association_StatSerialNumMap.get(statEvent.getStatsSerialNum());
		// if (null == statTime) {
		// DebugUtil
		// .performanceDebugWarn("BeClientSessionProcessor.handleStatResult4ClientTrap():no item in
		// stat serial number map");
		// return;
		// }
		removeClientTrapStatSerialMap(statEvent.getSequenceNum());
		// parse packet
		try {
			// statEvent.setStatTime(statTime);
			statEvent.parsePacket();
		} catch (BeCommunicationDecodeException e) {
			DebugUtil
					.performanceDebugWarn(
							"BeClientSessionProcessor.handleStatResult4ClientTrap(): Catch decode exception when parse statistics result ",
							e);
			return;
		}

		// update data into db
		Map<Byte, List<HmBo>> rowData = statEvent.getStatsRowData();
		List<HmBo> rowList = rowData.get(BeCommunicationConstant.STATTABLE_AHASSOCIATION);
		if (rowList.size() != 1) {
			// it maybe happen
			DebugUtil
					.performanceDebugInfo("BeClientSessionProcessor.handleStatResult4ClientTrap(): Parse receive statistics result, but there should be one data in.");
			return;
		}

		try {
			// save AhAssociation data
			AhAssociation association = (AhAssociation) rowList.get(0);
			if (!insertList.add(association)) {
				DebugUtil
						.performanceDebugError("BeClientSessionProcessor.handleStatResult4ClientTrap():Fail to add association information to insert list");
			}
			// update AhCurrentclientSession
			updateCurrentClientSession(association);
		} catch (Exception e) {
			DebugUtil
					.performanceDebugWarn(
							"BeClientSessionProcessor.handleStatResult4ClientTrap(): Failed update AhAssociation statistic result to DB! ",
							e);
		}
	}

	/**
	 * update client information by association
	 * 
	 * @param association
	 *            -
	 */
	private void updateCurrentClientSession(AhAssociation association) {
		// check cache list
		boolean inCache = false;
		for (HmBo bo : insertList) {
			if (bo instanceof AhClientSession) {
				AhClientSession currentClient = (AhClientSession) bo;
				if (currentClient.getClientMac().equals(association.getClientMac())
						&& currentClient.getApMac().equals(association.getApMac())
						&& currentClient.getConnectstate() == AhClientSession.CONNECT_STATE_UP) {

					if (association.getClientHostname() != null
							&& !association.getClientHostname().trim().isEmpty()) {
						currentClient.setClientHostname(association.getClientHostname());
					}

					if (association.getClientUsername() != null
							&& !association.getClientUsername().trim().isEmpty()) {
						currentClient.setClientUsername(association.getClientUsername());
					}

					if (association.getClientIP() != null
							&& !association.getClientIP().trim().isEmpty()) {
						currentClient.setClientIP(association.getClientIP());
					}

					currentClient.setApSerialNumber(association.getApSerialNumber());
					// currentClient.setClientMac(association.getClientMac());
					currentClient.setIfIndex(association.getIfIndex());
					currentClient.setStartTimeStamp(association.getClientAssociateTime() * 1000);
					currentClient.setStartTimeZone(association.getTimeStamp().getTimeZone());
					currentClient.setClientAuthMethod(association.getClientAuthMethod());
					currentClient.setClientChannel(association.getClientChannel());
					currentClient
							.setClientEncryptionMethod(association.getClientEncryptionMethod());
					currentClient.setClientMACProtocol(association.getClientMACProtocol());
					currentClient.setClientSSID(association.getClientSSID());
					currentClient.setClientUserProfId(association.getClientUserProfId());
					currentClient.setClientVLAN(association.getClientVLAN());
					currentClient.setClientCWPUsed(association.getClientCWPUsed());
					currentClient.setClientBSSID(association.getClientBSSID());
					currentClient.setIfName(association.getIfName());
					currentClient.setManagedStatus(association.getManagedStatus());
					
					// os detection
					if (association.getOs_option55() != null
							&& association.getClientOsInfo().equalsIgnoreCase(
									"unknown")) {
						String osversion = cacheMgmt.getClientOsInfoFromCacheByOption55(
								association.getOs_option55(),
								currentClient.getOwner());
						if (osversion != null) {
							currentClient.setClientOsInfo(osversion);
							currentClient.setOs_option55(association
									.getOs_option55());
						} else {
							currentClient.setClientOsInfo(association
									.getClientOsInfo());
							currentClient.setOs_option55(association
									.getOs_option55());
						}
					} else {
						currentClient.setClientOsInfo(association
								.getClientOsInfo());
						currentClient.setOs_option55(association
								.getOs_option55());
					}
					
					inCache = true;
					break;
				}
			}
		}
		if (!inCache) {
			// remove object at first and then create client
			SimpleHiveAp ap = cacheMgmt.getSimpleHiveAp(association.getApMac());
			try {
//				int count = QueryUtil.bulkRemoveBos(AhClientSession.class, new FilterParams(
//						"apMac = :s1 AND clientMac = :s2 and connectstate = :s3", new Object[] {
//								association.getApMac(), association.getClientMac(),
//								AhClientSession.CONNECT_STATE_UP }), null, null);
				int countWire = DBOperationUtil.executeUpdate("delete from ah_clientsession where ap = ? and clientmac = ? and connectstat = ? and wirelessClient = ?",
						new Object[]{association.getApMac(),association.getClientMac(),
						AhClientSession.CONNECT_STATE_UP, false});
				if (countWire > 0) {// Also remove from cache!
					module.activeClientRemove(ap, countWire, AhInterfaceStats.RADIOTYPE_OTHER);
				}
				int count24 = DBOperationUtil.executeUpdate("delete from ah_clientsession where ap = ? and clientmac = ? and connectstat = ? " +
						" and wirelessClient=? and (clientMACProtocol=? or clientMACProtocol=? or clientMACProtocol=?)",
						new Object[]{association.getApMac(),association.getClientMac(),
						AhClientSession.CONNECT_STATE_UP, true,
						AhAssociation.CLIENTMACPROTOCOL_BMODE,AhAssociation.CLIENTMACPROTOCOL_GMODE,
						AhAssociation.CLIENTMACPROTOCOL_NGMODE});
				if (count24 > 0) {// Also remove from cache!
					module.activeClientRemove(ap, count24, AhInterfaceStats.RADIOTYPE_24G);
				}
				int count5 = DBOperationUtil.executeUpdate("delete from ah_clientsession where ap = ? and clientmac = ? and connectstat = ? " +
						" and wirelessClient=? and (clientMACProtocol=? or clientMACProtocol=? or clientMACProtocol=?)",
						new Object[]{association.getApMac(),association.getClientMac(),
						AhClientSession.CONNECT_STATE_UP, true, 
						AhAssociation.CLIENTMACPROTOCOL_AMODE,AhAssociation.CLIENTMACPROTOCOL_NAMODE,
						AhAssociation.CLIENTMACPROTOCOL_ACMODE});
				if (count5 > 0) {// Also remove from cache!
					module.activeClientRemove(ap, count5, AhInterfaceStats.RADIOTYPE_5G);
				}
			} catch (Exception e) {
				DebugUtil
						.performanceDebugWarn(
								"BeClientSessionProcessor.updateCurrentClientSession(): Failed to remove client information.",
								e);
			}
			AhClientSession clientSession = createClientSessionFromAssociation(association);
			insertList.add(clientSession);
			// module.activeClientAdd(ap, 1);
		}
	}

	/**
	 * refresh db from cache list
	 */
	private void refreshDb() {
		if (insertList.isEmpty()) {
			return;
		}

		try {
			StringBuilder buffer = new StringBuilder();
			buffer.append(AhConvertBOToSQL.convertClientSessionToSQL(insertList));
			DBOperationUtil.executeUpdate(buffer.toString());
			BulkUpdateUtil.bulkInsertForAssociation(insertList);

			// check cache list
			for (HmBo bo : insertList) {
				if (bo instanceof AhClientSession) {
					AhClientSession currentClient = (AhClientSession) bo;
					if (currentClient.getConnectstate() == AhClientSession.CONNECT_STATE_UP) {
						SimpleHiveAp hiveAp = cacheMgmt.getSimpleHiveAp(currentClient.getApMac());
						if (null != hiveAp) {
							// update active client in cache.
							int radioMode = calcClientRadioType(currentClient.getClientChannel(),currentClient.getClientMACProtocol());
							module.activeClientAdd(hiveAp, 1, radioMode);
						}
					}
				}
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn(
					"BeClientSessionProcessor.freshDb(): Failed to insert db information.", e);
		}
		insertList.clear();
	}
	
	private int calcClientRadioType(int channel, int macPol){
		if (channel==0) {
			return  AhInterfaceStats.RADIOTYPE_OTHER;
		}else {
			switch(macPol)
			{
			case AhAssociation.CLIENTMACPROTOCOL_BMODE:
			case AhAssociation.CLIENTMACPROTOCOL_GMODE:
			case AhAssociation.CLIENTMACPROTOCOL_NGMODE:
					return AhInterfaceStats.RADIOTYPE_24G; 
			case AhAssociation.CLIENTMACPROTOCOL_AMODE:
			case AhAssociation.CLIENTMACPROTOCOL_NAMODE:
			case AhAssociation.CLIENTMACPROTOCOL_ACMODE:
					return AhInterfaceStats.RADIOTYPE_5G;
			default:
				log.error("Don't know the client radio type...........in max client count.");
				return -1;		
			}
		}

	}

	/**
	 * transfer db from client session to client history
	 * 
	 */
	private void transferDb() {
		try {
			int transfers = 0;
			int limit = 500;
			long begin = System.currentTimeMillis();

			//use prepared SQL to transfer client history
			while(true) {
				//query from clientsession
				List<AhClientSession> lsClient = DBOperationUtil.executeQuery(AhClientSession.class, null, 
						new FilterParams("connectstate = ?",
						new Object[] {AhClientSession.CONNECT_STATE_DOWN }),null,limit);
				
				if(lsClient == null || lsClient.isEmpty())
					break;
				
				//change from AhClientSession to AhClientSessionHistory
				List<AhClientSessionHistory> lsClientHistory = getClientSessionHistory(lsClient);
				
				//insert into clientsession_history
				try {
					BulkUpdateUtil.bulkInsertForClientSessionHistory(lsClientHistory);
				} catch (Exception e) {
					DebugUtil
					.performanceDebugWarn(
							"BeClientSessionProcessor.transferDb(): Failed to insert client history.",
							e);
				}

				//delete from clientsession
				StringBuilder deleteSqlBuffer = new StringBuilder();
				deleteSqlBuffer.append("delete from ah_clientsession where id in(");
				for(int i = 0; i < lsClient.size(); i++) {
					AhClientSession clientSession = lsClient.get(i);
					deleteSqlBuffer.append(clientSession.getId());
					if(i != lsClient.size() - 1)
						deleteSqlBuffer.append(",");
				}
				deleteSqlBuffer.append(")");
				DBOperationUtil.executeUpdate(deleteSqlBuffer.toString());
				
				transfers += lsClient.size();
				if(lsClient.size() < limit)
					break;
			}
			if(transfers > limit)
				DebugUtil.performanceDebugWarn("Transfer records: " + transfers+", elapse time: "+(System.currentTimeMillis()-begin)+"ms");
		} catch (Exception e) {
			DebugUtil
					.performanceDebugWarn(
							"BeClientSessionProcessor.transferDb(): Failed to transfer client db information.",
							e);
		}
	}
	
	/**
	 * change AhClientSession to AhClientSessionHistory
	 * 
	 * @param listClientSession
	 *            -
	 * @return -
	 */
	private List<AhClientSessionHistory> getClientSessionHistory(List<AhClientSession> listClientSession) {
		if(listClientSession == null)
			return null;
		List<AhClientSessionHistory> resultClientHistoryList = new ArrayList<>(listClientSession.size());
		for(AhClientSession clientSession: listClientSession) {
			AhClientSessionHistory clientSessionHistory = new AhClientSessionHistory();
			clientSessionHistory.setApMac(clientSession.getApMac());
			clientSessionHistory.setApName(clientSession.getApName());
			clientSessionHistory.setApSerialNumber(clientSession.getApSerialNumber());
			clientSessionHistory.setBandWidthSentinelStatus(clientSession.getBandWidthSentinelStatus());
			clientSessionHistory.setClientIP(clientSession.getClientIP());
			clientSessionHistory.setClientMac(clientSession.getClientMac());
			clientSessionHistory.setClientUsername(clientSession.getClientUsername());
			clientSessionHistory.setIfIndex(clientSession.getIfIndex());
			clientSessionHistory.setMemo(clientSession.getMemo());
			clientSessionHistory.setStartTimeStamp(clientSession.getStartTimeStamp());
			clientSessionHistory.setClientAuthMethod(clientSession.getClientAuthMethod());
			clientSessionHistory.setClientChannel(clientSession.getClientChannel());
			clientSessionHistory.setClientEncryptionMethod(clientSession.getClientEncryptionMethod());
			clientSessionHistory.setClientHostname(clientSession.getClientHostname());
			clientSessionHistory.setClientMACProtocol(clientSession.getClientMACProtocol());
			clientSessionHistory.setClientSSID(clientSession.getClientSSID());
			clientSessionHistory.setClientUserProfId(clientSession.getClientUserProfId());
			clientSessionHistory.setClientVLAN(clientSession.getClientVLAN());
			clientSessionHistory.setClientCWPUsed(clientSession.getClientCWPUsed());
			clientSessionHistory.setClientBSSID(clientSession.getClientBSSID());
			clientSessionHistory.setMapId(clientSession.getMapId());
			clientSessionHistory.setOwner(clientSession.getOwner());
			clientSessionHistory.setStartTimeZone(clientSession.getStartTimeZone());
			clientSessionHistory.setSimulated(clientSession.isSimulated());
			clientSessionHistory.setIfName(clientSession.getIfName());
			clientSessionHistory.setClientOsInfo(clientSession.getClientOsInfo());
			clientSessionHistory.setComment1(clientSession.getComment1());
			clientSessionHistory.setComment2(clientSession.getComment2());
			clientSessionHistory.setEmail(clientSession.getEmail());
			clientSessionHistory.setCompanyName(clientSession.getCompanyName());
			clientSessionHistory.setEndTimeStamp(clientSession.getEndTimeStamp());
			clientSessionHistory.setEndTimeZone(clientSession.getEndTimeZone());
			clientSessionHistory.setUserProfileName(clientSession.getUserProfileName());
			
			resultClientHistoryList.add(clientSessionHistory);
		}
		return resultClientHistoryList;
	}

	/**
	 * check statsSerial number validate
	 * 
	 * @param statsSerialNum
	 *            -
	 * @return -
	 */
	public boolean is4ClientTrapStatResult(int statsSerialNum) {
		return statsSerialNumSet4ClientTrap.contains(statsSerialNum);
	}

	/**
	 * remove stat serial number map
	 * 
	 * @param statsSerialNum
	 *            -
	 */
	void removeClientTrapStatSerialMap(int statsSerialNum) {
		statsSerialNumSet4ClientTrap.remove(statsSerialNum);
	}

	class RefreshTimerGenerator implements Runnable {
		@Override
		public void run() {
			MgrUtil.setTimerName(getClass().getSimpleName());
			try {
				AhTimeoutEvent timer = new AhTimeoutEvent();
				addEvent(timer);
				timerCount = (++timerCount) % 100000000;
				if (0 == (timerCount % TRANSFER_TIMER_COUNT)) {
					transferDb();
				}
			} catch (Exception e) {
				DebugUtil.performanceDebugError("RefreshTimerGenerator.run() catch exception", e);
			} catch (Error e) {
				DebugUtil.performanceDebugError("RefreshTimerGenerator.run() catch error", e);
			}
		}
	}

	@Override
	public void boCreated(HmDomain hmDomain) {
		DebugUtil.performanceDebugInfo("BeClientSessionProcessor.boCreated(): Create HmDomain: "
				+ hmDomain.getLabel());
	}

	@Override
	public void boRemoved(HmDomain hmDomain) {
		DebugUtil.performanceDebugInfo("BeClientSessionProcessor.boRemoved(): Remove HmDomain: "
				+ hmDomain.getLabel());
		domainCounterMap.remove(hmDomain);
	}

	@Override
	public void boUpdated(HmDomain hmDomain) {
		DebugUtil.performanceDebugInfo("BeClientSessionProcessor.boUpdated(): Update HmDomain: "
				+ hmDomain.getLabel() + "; Status: " + hmDomain.getRunStatus());

		if (hmDomain.getRunStatus() == HmDomain.DOMAIN_DISABLE_STATUS) {
			domainCounterMap.remove(hmDomain);
			return;
		}

		HMServicesSettings bo = QueryUtil.findBoByAttribute(
				HMServicesSettings.class, "owner", hmDomain);
		if (bo == null) {
			domainCounterMap.remove(hmDomain);
			return;
		}

		if (!bo.isEnableClientRefresh()) {
			domainCounterMap.remove(hmDomain);
			return;
		}

		domainCounterMap.put(hmDomain, bo.getRefreshInterval());
		hmDomain.setClientRefreshInterval(bo.getRefreshInterval());
	}
	
	private void checkDoubtfulValue(ClientTrap client, SimpleHiveAp ap) {
		if (!logDoubtfulClientInfo) {
			return;
		}
		if (client == null || ap == null) {
			return;
		}
		if ("default security-obj".equalsIgnoreCase(client.getSSID())) {
			BeLogTools.debug(HmLogConst.M_PERFORMANCE, "ClientCache catch the doubtful ssid default security-obj, report device is " + ap.getMacAddress() + ", clientmac is " + client.getClientMac());  
		}
		if ("default-profile".equalsIgnoreCase(client.getUserProfileName())) {
			BeLogTools.debug(HmLogConst.M_PERFORMANCE, "ClientCache catch the doubtful userprofilename default-profile, report device is " + ap.getMacAddress() + ", clientmac is " + client.getClientMac());  
		}
		
	}
	
}

class ClientTrap {
	public final static int	TYPE_STATE_UP		= 0;
	public final static int	TYPE_STATE_DOWN		= 1;
	public final static int	TYPE_CLIENT_INFO	= 2;

	private int				type;
	private String			apMac;
	private String			apName;
	private String			clientMac;
	private int				ifIndex;
	private String			desc;
	private String			SSID;
	private String			clientIp;
	private String			clientHostName;
	private String			clientUserName;

	private byte			clientCWPUsed;

	private byte			clientAuthMethod;

	private byte			clientEncryptionMethod;

	private byte			clientMacProtocol;

	private int				clientVLAN;

	private int				clientUserProfId;

	private int				clientChannel;

	private String			clientBSSID;

	private long			associateTime;

	private String			associateTimeZone;

	private String			comment1;
	private String			comment2;

	private String			ifName;
	
	private int				clientRssi;
	
	private String			clientOsInfo;
	
	private String			os_option55;

	private String 			userProfileName;
	
	private short			SNR;
	
	private byte			clientMacBasedAuthUsed;
	
	private short			managedStatus;
	
	public String getOs_option55() {
		return os_option55;
	}

	public void setOs_option55(String os_option55) {
		this.os_option55 = os_option55;
	}

	public String getIfName() {
		return ifName;
	}

	public void setIfName(String ifName) {
		this.ifName = ifName;
	}

	
	public int getClientRssi() {
		return clientRssi;
	}

	public void setClientRssi(int clientRssi) {
		this.clientRssi = clientRssi;
	}

	public String getApMac() {
		return apMac;
	}

	public void setApMac(String apMac) {
		this.apMac = apMac;
	}

	public String getApName() {
		return apName;
	}

	public void setApName(String apName) {
		this.apName = apName;
	}

	public String getClientHostName() {
		return clientHostName;
	}

	public void setClientHostName(String clientHostName) {
		this.clientHostName = clientHostName;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getClientMac() {
		return clientMac;
	}

	public void setClientMac(String clientMac) {
		this.clientMac = clientMac;
	}

	public String getClientUserName() {
		return clientUserName;
	}

	public void setClientUserName(String clientUserName) {
		this.clientUserName = clientUserName;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getIfIndex() {
		return ifIndex;
	}

	public void setIfIndex(int ifIndex) {
		this.ifIndex = ifIndex;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getSSID() {
		return SSID;
	}

	public void setSSID(String ssid) {
		SSID = ssid;
	}

	public String getComment1() {
		return comment1;
	}

	public void setComment1(String comment1) {
		this.comment1 = comment1;
	}

	public String getComment2() {
		return comment2;
	}

	public void setComment2(String comment2) {
		this.comment2 = comment2;
	}

	public byte getClientCWPUsed() {
		return clientCWPUsed;
	}

	public void setClientCWPUsed(byte clientCWPUsed) {
		this.clientCWPUsed = clientCWPUsed;
	}

	public byte getClientAuthMethod() {
		return clientAuthMethod;
	}

	public void setClientAuthMethod(byte clientAuthMethod) {
		this.clientAuthMethod = clientAuthMethod;
	}

	public byte getClientEncryptionMethod() {
		return clientEncryptionMethod;
	}

	public void setClientEncryptionMethod(byte clientEncryptionMethod) {
		this.clientEncryptionMethod = clientEncryptionMethod;
	}

	public byte getClientMacProtocol() {
		return clientMacProtocol;
	}

	public void setClientMacProtocol(byte clientMacProtocol) {
		this.clientMacProtocol = clientMacProtocol;
	}

	public int getClientVLAN() {
		return clientVLAN;
	}

	public void setClientVLAN(int clientVLAN) {
		this.clientVLAN = clientVLAN;
	}

	public int getClientUserProfId() {
		return clientUserProfId;
	}

	public void setClientUserProfId(int clientUserProfId) {
		this.clientUserProfId = clientUserProfId;
	}

	public int getClientChannel() {
		return clientChannel;
	}

	public void setClientChannel(int clientChannel) {
		this.clientChannel = clientChannel;
	}

	public String getClientBSSID() {
		return clientBSSID;
	}

	public void setClientBSSID(String clientBSSID) {
		this.clientBSSID = clientBSSID;
	}

	public long getAssociateTime() {
		return associateTime;
	}

	public void setAssociateTime(long associateTime) {
		this.associateTime = associateTime;
	}

	public String getAssociateTimeZone() {
		return associateTimeZone;
	}

	public void setAssociateTimeZone(String associateTimeZone) {
		this.associateTimeZone = associateTimeZone;
	}

	public String getClientOsInfo() {
		return clientOsInfo;
	}

	public void setClientOsInfo(String clientOsInfo) {
		this.clientOsInfo = clientOsInfo;
	}
	
	public String getUserProfileName() {
		return userProfileName;
	}

	public void setUserProfileName(String userProfileName) {
		this.userProfileName = userProfileName;
	}
	
	public short getSNR() {
		return SNR;
	}

	public void setSNR(short sNR) {
		SNR = sNR;
	}
	public byte getClientMacBasedAuthUsed() {
		return clientMacBasedAuthUsed;
	}

	public void setClientMacBasedAuthUsed(byte clientMacBasedAuthUsed) {
		this.clientMacBasedAuthUsed = clientMacBasedAuthUsed;
	}

	public short getManagedStatus() {
		return managedStatus;
	}

	public void setManagedStatus(short managedStatus) {
		this.managedStatus = managedStatus;
	}
}