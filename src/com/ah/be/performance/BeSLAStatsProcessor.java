package com.ah.be.performance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jfree.util.Log;

import com.ah.be.app.DebugUtil;
import com.ah.be.common.DBOperationUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.common.db.BulkUpdateUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeAPConnectEvent;
import com.ah.be.communication.event.BeTrapEvent;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.LogSettings;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.performance.AhBandWidthSentinelHistory;
import com.ah.bo.performance.AhClientSession;
import com.ah.bo.performance.AhMaxClientsCount;
import com.ah.bo.performance.AhNewSLAStats;
import com.ah.bo.performance.AhSLAStats;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * 
 *@filename		BeSLAStatsProcessor.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2010-6-3 02:23:17
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 * 
 */
public class BeSLAStatsProcessor implements Runnable {
	
	private static final Tracer log = new Tracer(BeSLAStatsProcessor.class.getSimpleName());

	private ScheduledExecutorService							scheduler;

	private final BlockingQueue<BeCommunicationEvent>					eventQueue;

	private boolean												isContinue					= true;

	/**
	 * SLA for AP is 6 field SLA violation, CRC Error, Tx Drop, Rx Drop, Tx Retry, Airtime<br>
	 * SLA for Client is 4 field SLA violation, Tx Drop, Rx Drop, Airtime
	 */
	// cache group
	private final ConcurrentMap<SimpleHiveAp, List<BeTrapEvent>>	ap_sla_red_map				= new ConcurrentHashMap<SimpleHiveAp, List<BeTrapEvent>>();

	private final ConcurrentMap<SimpleHiveAp, List<BeTrapEvent>>	ap_sla_yellow_map			= new ConcurrentHashMap<SimpleHiveAp, List<BeTrapEvent>>();

	private final ConcurrentMap<SimpleHiveAp, List<BeTrapEvent>>	ap_crc_error_map			= new ConcurrentHashMap<SimpleHiveAp, List<BeTrapEvent>>();

	private final ConcurrentMap<SimpleHiveAp, List<BeTrapEvent>>	ap_tx_drop_map				= new ConcurrentHashMap<SimpleHiveAp, List<BeTrapEvent>>();

	private final ConcurrentMap<SimpleHiveAp, List<BeTrapEvent>>	ap_rx_drop_map				= new ConcurrentHashMap<SimpleHiveAp, List<BeTrapEvent>>();

	private final ConcurrentMap<SimpleHiveAp, List<BeTrapEvent>>	ap_tx_retry_map				= new ConcurrentHashMap<SimpleHiveAp, List<BeTrapEvent>>();

	private final ConcurrentMap<SimpleHiveAp, List<BeTrapEvent>>	ap_airtime_map				= new ConcurrentHashMap<SimpleHiveAp, List<BeTrapEvent>>();

	private final ConcurrentSkipListSet<BeTrapEvent>				client_sla_red_Set			= new ConcurrentSkipListSet<BeTrapEvent>();

	private final ConcurrentSkipListSet<BeTrapEvent>				client_sla_yellow_Set		= new ConcurrentSkipListSet<BeTrapEvent>();

	private final ConcurrentSkipListSet<BeTrapEvent>				client_airtime_Set			= new ConcurrentSkipListSet<BeTrapEvent>();
	
	
	private final ConcurrentMap<SimpleHiveAp, List<BeTrapEvent>>	ap_sla_red_map_clear			= new ConcurrentHashMap<SimpleHiveAp, List<BeTrapEvent>>();

	private final ConcurrentMap<SimpleHiveAp, List<BeTrapEvent>>	ap_sla_yellow_map_clear		= new ConcurrentHashMap<SimpleHiveAp, List<BeTrapEvent>>();

	private final ConcurrentMap<SimpleHiveAp, List<BeTrapEvent>>	ap_crc_error_map_clear		= new ConcurrentHashMap<SimpleHiveAp, List<BeTrapEvent>>();

	private final ConcurrentMap<SimpleHiveAp, List<BeTrapEvent>>	ap_tx_drop_map_clear		= new ConcurrentHashMap<SimpleHiveAp, List<BeTrapEvent>>();

	private final ConcurrentMap<SimpleHiveAp, List<BeTrapEvent>>	ap_rx_drop_map_clear		= new ConcurrentHashMap<SimpleHiveAp, List<BeTrapEvent>>();

	private final ConcurrentMap<SimpleHiveAp, List<BeTrapEvent>>	ap_tx_retry_map_clear		= new ConcurrentHashMap<SimpleHiveAp, List<BeTrapEvent>>();

	private final ConcurrentMap<SimpleHiveAp, List<BeTrapEvent>>	ap_airtime_map_clear		= new ConcurrentHashMap<SimpleHiveAp, List<BeTrapEvent>>();

	private final ConcurrentSkipListSet<BeTrapEvent>				client_sla_red_Set_clear	= new ConcurrentSkipListSet<BeTrapEvent>();

	private final ConcurrentSkipListSet<BeTrapEvent>				client_sla_yellow_Set_clear = new ConcurrentSkipListSet<BeTrapEvent>();

	private final ConcurrentSkipListSet<BeTrapEvent>				client_airtime_Set_clear	= new ConcurrentSkipListSet<BeTrapEvent>();

	private final ConcurrentSkipListSet<SimpleHiveAp>				disconnectDeviceSet			= new ConcurrentSkipListSet<SimpleHiveAp>();
	
	// count cache
	// domain ID - ap/client mac address
	private final Map<Long, Set<String>>								apMac_sla_red_Map			= new HashMap<Long, Set<String>>();

	private final Map<Long, Set<String>>								apMac_sla_yellow_Map		= new HashMap<Long, Set<String>>();

	private final Map<Long, Set<String>>								apMac_crc_error_Map			= new HashMap<Long, Set<String>>();

	private final Map<Long, Set<String>>								apMac_tx_drop_Map			= new HashMap<Long, Set<String>>();

	private final Map<Long, Set<String>>								apMac_rx_drop_Map			= new HashMap<Long, Set<String>>();

	private final Map<Long, Set<String>>								apMac_tx_retry_Map			= new HashMap<Long, Set<String>>();

	private final Map<Long, Set<String>>								apMac_airtime_Map			= new HashMap<Long, Set<String>>();

	private final Map<Long, Set<String>>								clientMac_sla_red_Map		= new HashMap<Long, Set<String>>();

	private final Map<Long, Set<String>>								clientMac_sla_yellow_Map	= new HashMap<Long, Set<String>>();

	private final Map<Long, Set<String>>								clientMac_airtime_Map		= new HashMap<Long, Set<String>>();

	private ConcurrentMap<Long, AhMaxClientsCount>				clientCountMap;

	private final Map<Long, Set<String>>								clientScore_Red_Map			= new HashMap<Long, Set<String>>();

	private final Map<Long, Set<String>>								clientScore_Yellow_Map		= new HashMap<Long, Set<String>>();

	private Set<String>											activeClientMacSet4Check;

	private int							interval	= 3;
	
	/**
	 * Construct method
	 */
	public BeSLAStatsProcessor() {
		eventQueue = new LinkedBlockingQueue<BeCommunicationEvent>(10000);
	}

	public void startTask() {
		isContinue = true;

		List<LogSettings> list = QueryUtil.executeQuery(LogSettings.class,
				null, null);
		if (!list.isEmpty()) {
			interval = list.get(0).getSlaPeriod();
		}
		
		if (interval<1 || interval>10) {
			interval=3;
		}
		
		// start scheduler
		if (scheduler == null || scheduler.isShutdown()) {
			scheduler = Executors.newSingleThreadScheduledExecutor();

			scheduler.scheduleWithFixedDelay(this, interval, interval, TimeUnit.MINUTES);
		}

		EventProcessorThread eventProcessorThread = new EventProcessorThread();
		eventProcessorThread.setName("Event Processor Thread");
		eventProcessorThread.start();
	}

	@Override
	public void run() {
		try {
			MgrUtil.setTimerName(this.getClass().getSimpleName());
			DebugUtil.faultDebugInfo("BeSLAStatsProcessor.run(): Start handle SLA stats thread.");
			long currentTime = System.currentTimeMillis();
			prepareSLACount(currentTime);

			List<HmDomain> domainList = CacheMgmt.getInstance().getCacheDomains();
			List<AhSLAStats> slaList = new ArrayList<AhSLAStats>(domainList.size() + 1);
			for (HmDomain hmDomain : domainList) {
				AhSLAStats slaStats = new AhSLAStats();
				slaStats.setTimeStamp(currentTime);
				slaStats.setOwner(hmDomain);
				
				if(hmDomain.getRunStatus() != HmDomain.DOMAIN_DEFAULT_STATUS) {
					DebugUtil.performanceDebugWarn("BeSLAStatsProcessor.run(): Domain:" + 
							hmDomain.getDomainName() + " Status is not default, ignored");
					continue;
				}
				
				int clientCount = clientCountMap.get(hmDomain.getId()) == null ? 0 : clientCountMap
						.get(hmDomain.getId()).getCurrentClientCount();
				if (clientCount > 0) {
					// client airtime
					Set<String> clientMacSet_airtime = clientMac_airtime_Map.get(hmDomain.getId());
					int value = checkNumValid4Client(slaStats, "client airtime",
							clientMacSet_airtime, clientCount);
					slaStats.setClientAirTime_Red(value);

					// client sla red
					Set<String> clientMacSet_slaRed = clientMac_sla_red_Map.get(hmDomain.getId());
					value = checkNumValid4Client(slaStats, "client sla red", clientMacSet_slaRed,
							clientCount);
					slaStats.setClientSla_Red(value);

					// client sla yellow
					Set<String> clientMacSet_slaYellow = clientMac_sla_yellow_Map.get(hmDomain
							.getId());
					if (clientMacSet_slaRed!=null && clientMacSet_slaYellow!=null) {
						clientMacSet_slaYellow.removeAll(clientMacSet_slaRed);
					}
					value = checkNumValid4Client(slaStats, "client sla yellow",
							clientMacSet_slaYellow, clientCount);
					slaStats.setClientSla_Yellow(value);

					// client score red
					Set<String> clientMacSet_scoreRed = clientScore_Red_Map.get(hmDomain.getId());
					value = checkNumValid4Client(slaStats, "client score red",
							clientMacSet_scoreRed, clientCount);
					slaStats.setClientScore_Red(value);

					// client score yellow
					Set<String> clientMacSet_scoreYellow = clientScore_Yellow_Map.get(hmDomain
							.getId());
					if (clientMacSet_scoreRed!=null && clientMacSet_scoreYellow!=null) {
						clientMacSet_scoreYellow.removeAll(clientMacSet_scoreRed);
					}
					value = checkNumValid4Client(slaStats, "client score yellow",
							clientMacSet_scoreYellow, clientCount);
					slaStats.setClientScore_Yellow(value);

					// client total red
					Set<String> clientMacSet_TotalRed = new HashSet<String>();
					if (clientMacSet_airtime != null) {
						clientMacSet_TotalRed.addAll(clientMacSet_airtime);
					}
					if (clientMacSet_slaRed != null) {
						clientMacSet_TotalRed.addAll(clientMacSet_slaRed);
					}
					if (clientMacSet_scoreRed != null) {
						clientMacSet_TotalRed.addAll(clientMacSet_scoreRed);
					}
					value = checkNumValid4Client(slaStats, "client total red",
							clientMacSet_TotalRed, clientCount);
					slaStats.setClientTotal_Red(value);

					// client total yellow
					Set<String> clientMacSet_TotalYellow = new HashSet<String>();
					if (clientMacSet_slaYellow != null) {
						clientMacSet_TotalYellow.addAll(clientMacSet_slaYellow);
					}
					if (clientMacSet_scoreYellow != null) {
						clientMacSet_TotalYellow.addAll(clientMacSet_scoreYellow);
					}
					clientMacSet_TotalYellow.removeAll(clientMacSet_TotalRed);
					value = checkNumValid4Client(slaStats, "client total yellow",
							clientMacSet_TotalYellow, clientCount);
					slaStats.setClientTotal_Yellow(value);
				} else {
					slaStats.setClientAirTime_Red(-1);
					slaStats.setClientSla_Red(-1);
					slaStats.setClientSla_Yellow(-1);
					slaStats.setClientScore_Red(-1);
					slaStats.setClientScore_Yellow(-1);
					slaStats.setClientTotal_Red(-1);
					slaStats.setClientTotal_Yellow(-1);
				}

				int apCount = CacheMgmt.getInstance().getManagedApList(hmDomain.getId()).size();
				if (apCount > 0) {
					// ap airtime
					Set<String> apMacSet_airtime = apMac_airtime_Map.get(hmDomain.getId());
					slaStats.setApAirTime_Red(apMacSet_airtime == null ? 0 : (apMacSet_airtime
							.size() * 100 / apCount));
					errorCheck(slaStats, "ap airtime", slaStats.getApAirTime_Red(),
							apMacSet_airtime, apCount);

					// ap crc error
					Set<String> apMacSet_crcError = apMac_crc_error_Map.get(hmDomain.getId());
					slaStats.setApCrcError_Red(apMacSet_crcError == null ? 0 : (apMacSet_crcError
							.size() * 100 / apCount));
					errorCheck(slaStats, "ap crc error", slaStats.getApCrcError_Red(),
							apMacSet_crcError, apCount);

					// ap tx retry
					Set<String> apMacSet_txRetry = apMac_tx_retry_Map.get(hmDomain.getId());
					slaStats.setApRetry_Red(apMacSet_txRetry == null ? 0
							: (apMacSet_txRetry.size() * 100 / apCount));
					errorCheck(slaStats, "ap tx retry", slaStats.getApRetry_Red(),
							apMacSet_txRetry, apCount);

					// ap rx drop
					Set<String> apMacSet_rxDrop = apMac_rx_drop_Map.get(hmDomain.getId());
					slaStats.setApRxDrop_Red(apMacSet_rxDrop == null ? 0
							: (apMacSet_rxDrop.size() * 100 / apCount));
					errorCheck(slaStats, "ap rx drop", slaStats.getApRxDrop_Red(), apMacSet_rxDrop,
							apCount);

					// ap tx drop
					Set<String> apMacSet_txDrop = apMac_tx_drop_Map.get(hmDomain.getId());
					slaStats.setApTxDrop_Red(apMacSet_txDrop == null ? 0
							: (apMacSet_txDrop.size() * 100 / apCount));
					errorCheck(slaStats, "ap tx drop", slaStats.getApTxDrop_Red(), apMacSet_txDrop,
							apCount);

					// ap sla red
					Set<String> apMacSet_slaRed = apMac_sla_red_Map.get(hmDomain.getId());
					slaStats.setApSla_Red(apMacSet_slaRed == null ? 0
							: (apMacSet_slaRed.size() * 100 / apCount));
					errorCheck(slaStats, "ap sla red", slaStats.getApSla_Red(), apMacSet_slaRed,
							apCount);

					// ap sla yellow
					Set<String> apMacSet_slaYellow = apMac_sla_yellow_Map.get(hmDomain.getId());
					if (apMacSet_slaRed!=null && apMacSet_slaYellow!=null) {
						apMacSet_slaYellow.removeAll(apMacSet_slaRed);
					}
					slaStats.setApSla_Yellow(apMacSet_slaYellow == null ? 0 : (apMacSet_slaYellow
							.size() * 100 / apCount));
					errorCheck(slaStats, "ap sla yellow", slaStats.getApSla_Yellow(),
							apMacSet_slaYellow, apCount);

					// ap total red
					Set<String> apMacSet_TotalRed = new HashSet<String>();
					if (apMacSet_airtime != null) {
						apMacSet_TotalRed.addAll(apMacSet_airtime);
					}
					if (apMacSet_crcError != null) {
						apMacSet_TotalRed.addAll(apMacSet_crcError);
					}
					if (apMacSet_txRetry != null) {
						apMacSet_TotalRed.addAll(apMacSet_txRetry);
					}
					if (apMacSet_rxDrop != null) {
						apMacSet_TotalRed.addAll(apMacSet_rxDrop);
					}
					if (apMacSet_txDrop != null) {
						apMacSet_TotalRed.addAll(apMacSet_txDrop);
					}
					if (apMacSet_slaRed != null) {
						apMacSet_TotalRed.addAll(apMacSet_slaRed);
					}
					slaStats.setApTotal_Red((apMacSet_TotalRed.size() * 100 / apCount));
					errorCheck(slaStats, "ap total red", slaStats.getApTotal_Red(),
							apMacSet_TotalRed, apCount);

					// ap total yellow
					Set<String> apMacSet_TotalYellow = new HashSet<String>();
					if (apMacSet_slaYellow != null) {
						apMacSet_TotalYellow.addAll(apMacSet_slaYellow);
					}
					apMacSet_TotalYellow.removeAll(apMacSet_TotalRed);
					slaStats.setApTotal_Yellow((apMacSet_TotalYellow.size() * 100 / apCount));
					errorCheck(slaStats, "ap total yellow", slaStats.getApTotal_Yellow(),
							apMacSet_TotalYellow, apCount);
				} else {
					slaStats.setApAirTime_Red(-1);
					slaStats.setApCrcError_Red(-1);
					slaStats.setApRetry_Red(-1);
					slaStats.setApRxDrop_Red(-1);
					slaStats.setApSla_Red(-1);
					slaStats.setApSla_Yellow(-1);
					slaStats.setApTotal_Red(-1);
					slaStats.setApTotal_Yellow(-1);
					slaStats.setApTxDrop_Red(-1);
				}

				slaList.add(slaStats);
			}

			AhSLAStats globalSlaStats = new AhSLAStats();
			globalSlaStats.setTimeStamp(currentTime);
			globalSlaStats.setOwner(BoMgmt.getDomainMgmt().getHomeDomain());
			globalSlaStats.setGlobalFlag(true);

			int clientCount = clientCountMap.get(CacheMgmt.TOTAL_KEY).getCurrentClientCount();
			if (clientCount > 0) {
				// client airtime
				Set<String> clientMacSet_airtime = mergeValuesOfMap(clientMac_airtime_Map);
				int value = checkNumValid4Client(globalSlaStats, "client airtime",
						clientMacSet_airtime, clientCount);
				globalSlaStats.setClientAirTime_Red(value);

				// client sla red
				Set<String> clientMacSet_slaRed = mergeValuesOfMap(clientMac_sla_red_Map);
				value = checkNumValid4Client(globalSlaStats, "client sla red", clientMacSet_slaRed,
						clientCount);
				globalSlaStats.setClientSla_Red(value);

				// client sla yellow
				Set<String> clientMacSet_slaYellow = mergeValuesOfMap(clientMac_sla_yellow_Map);
				clientMacSet_slaYellow.removeAll(clientMacSet_slaRed);
				value = checkNumValid4Client(globalSlaStats, "client sla yellow",
						clientMacSet_slaYellow, clientCount);
				globalSlaStats.setClientSla_Yellow(value);

				// client score red
				Set<String> clientMacSet_scoreRed = mergeValuesOfMap(clientScore_Red_Map);
				value = checkNumValid4Client(globalSlaStats, "client score red",
						clientMacSet_scoreRed, clientCount);
				globalSlaStats.setClientScore_Red(value);

				// client score yellow
				Set<String> clientMacSet_scoreYellow = mergeValuesOfMap(clientScore_Yellow_Map);
				clientMacSet_scoreYellow.removeAll(clientMacSet_scoreRed);
				value = checkNumValid4Client(globalSlaStats, "client score yellow",
						clientMacSet_scoreYellow, clientCount);
				globalSlaStats.setClientScore_Yellow(value);

				// client total red
				Set<String> clientMacSet_TotalRed = new HashSet<String>();
				if (clientMacSet_airtime != null) {
					clientMacSet_TotalRed.addAll(clientMacSet_airtime);
				}
				if (clientMacSet_slaRed != null) {
					clientMacSet_TotalRed.addAll(clientMacSet_slaRed);
				}
				if (clientMacSet_scoreRed != null) {
					clientMacSet_TotalRed.addAll(clientMacSet_scoreRed);
				}
				value = checkNumValid4Client(globalSlaStats, "client total red",
						clientMacSet_TotalRed, clientCount);
				globalSlaStats.setClientTotal_Red(value);

				// client total yellow
				Set<String> clientMacSet_TotalYellow = new HashSet<String>();
				if (clientMacSet_slaYellow != null) {
					clientMacSet_TotalYellow.addAll(clientMacSet_slaYellow);
				}
				if (clientMacSet_scoreYellow != null) {
					clientMacSet_TotalYellow.addAll(clientMacSet_scoreYellow);
				}
				clientMacSet_TotalYellow.removeAll(clientMacSet_TotalRed);
				value = checkNumValid4Client(globalSlaStats, "client total yellow",
						clientMacSet_TotalYellow, clientCount);
				globalSlaStats.setClientTotal_Yellow(value);
			} else {
				globalSlaStats.setClientAirTime_Red(-1);
				globalSlaStats.setClientSla_Red(-1);
				globalSlaStats.setClientSla_Yellow(-1);
				globalSlaStats.setClientScore_Red(-1);
				globalSlaStats.setClientScore_Yellow(-1);
				globalSlaStats.setClientTotal_Red(-1);
				globalSlaStats.setClientTotal_Yellow(-1);
			}

			int apCount = CacheMgmt.getInstance().getManagedApList().size();
			if (apCount > 0) {
				// ap airtime
				Set<String> apMacSet_airtime = mergeValuesOfMap(apMac_airtime_Map);
				globalSlaStats.setApAirTime_Red(apMacSet_airtime == null ? 0 : (apMacSet_airtime
						.size() * 100 / apCount));
				errorCheck(globalSlaStats, "ap airtime", globalSlaStats.getApAirTime_Red(),
						apMacSet_airtime, apCount);

				// ap crc error
				Set<String> apMacSet_crcError = mergeValuesOfMap(apMac_crc_error_Map);
				globalSlaStats.setApCrcError_Red(apMacSet_crcError == null ? 0 : (apMacSet_crcError
						.size() * 100 / apCount));
				errorCheck(globalSlaStats, "ap crc error", globalSlaStats.getApCrcError_Red(),
						apMacSet_crcError, apCount);

				// ap tx retry
				Set<String> apMacSet_txRetry = mergeValuesOfMap(apMac_tx_retry_Map);
				globalSlaStats.setApRetry_Red(apMacSet_txRetry == null ? 0 : (apMacSet_txRetry
						.size() * 100 / apCount));
				errorCheck(globalSlaStats, "ap tx retry", globalSlaStats.getApRetry_Red(),
						apMacSet_txRetry, apCount);

				// ap rx drop
				Set<String> apMacSet_rxDrop = mergeValuesOfMap(apMac_rx_drop_Map);
				globalSlaStats.setApRxDrop_Red(apMacSet_rxDrop == null ? 0 : (apMacSet_rxDrop
						.size() * 100 / apCount));
				errorCheck(globalSlaStats, "ap rx drop", globalSlaStats.getApRxDrop_Red(),
						apMacSet_rxDrop, apCount);

				// ap tx drop
				Set<String> apMacSet_txDrop = mergeValuesOfMap(apMac_tx_drop_Map);
				globalSlaStats.setApTxDrop_Red(apMacSet_txDrop == null ? 0 : (apMacSet_txDrop
						.size() * 100 / apCount));
				errorCheck(globalSlaStats, "ap tx drop", globalSlaStats.getApTxDrop_Red(),
						apMacSet_txDrop, apCount);

				// ap sla red
				Set<String> apMacSet_slaRed = mergeValuesOfMap(apMac_sla_red_Map);
				globalSlaStats.setApSla_Red(apMacSet_slaRed == null ? 0
						: (apMacSet_slaRed.size() * 100 / apCount));
				errorCheck(globalSlaStats, "ap sla red", globalSlaStats.getApSla_Red(),
						apMacSet_slaRed, apCount);

				// ap sla yellow
				Set<String> apMacSet_slaYellow = mergeValuesOfMap(apMac_sla_yellow_Map);
				apMacSet_slaYellow.removeAll(apMacSet_slaRed);
				globalSlaStats.setApSla_Yellow(apMacSet_slaYellow == null ? 0 : (apMacSet_slaYellow
						.size() * 100 / apCount));
				errorCheck(globalSlaStats, "ap sla yellow", globalSlaStats.getApSla_Yellow(),
						apMacSet_slaYellow, apCount);

				// ap total red
				Set<String> apMacSet_TotalRed = new HashSet<String>();
				if (apMacSet_airtime != null) {
					apMacSet_TotalRed.addAll(apMacSet_airtime);
				}
				if (apMacSet_crcError != null) {
					apMacSet_TotalRed.addAll(apMacSet_crcError);
				}
				if (apMacSet_txRetry != null) {
					apMacSet_TotalRed.addAll(apMacSet_txRetry);
				}
				if (apMacSet_rxDrop != null) {
					apMacSet_TotalRed.addAll(apMacSet_rxDrop);
				}
				if (apMacSet_txDrop != null) {
					apMacSet_TotalRed.addAll(apMacSet_txDrop);
				}
				if (apMacSet_slaRed != null) {
					apMacSet_TotalRed.addAll(apMacSet_slaRed);
				}
				globalSlaStats.setApTotal_Red((apMacSet_TotalRed.size() * 100 / apCount));
				errorCheck(globalSlaStats, "ap total red", globalSlaStats.getApTotal_Red(),
						apMacSet_TotalRed, apCount);

				// ap total yellow
				Set<String> apMacSet_TotalYellow = new HashSet<String>();
				if (apMacSet_slaYellow != null) {
					apMacSet_TotalYellow.addAll(apMacSet_slaYellow);
				}
				apMacSet_TotalYellow.removeAll(apMacSet_TotalRed);
				globalSlaStats.setApTotal_Yellow((apMacSet_TotalYellow.size() * 100 / apCount));
				errorCheck(globalSlaStats, "ap total yellow", globalSlaStats.getApTotal_Yellow(),
						apMacSet_TotalYellow, apCount);
			} else {
				globalSlaStats.setApAirTime_Red(-1);
				globalSlaStats.setApCrcError_Red(-1);
				globalSlaStats.setApRetry_Red(-1);
				globalSlaStats.setApRxDrop_Red(-1);
				globalSlaStats.setApSla_Red(-1);
				globalSlaStats.setApSla_Yellow(-1);
				globalSlaStats.setApTotal_Red(-1);
				globalSlaStats.setApTotal_Yellow(-1);
				globalSlaStats.setApTxDrop_Red(-1);
			}
			slaList.add(globalSlaStats);

			BulkUpdateUtil.bulkInsert(AhSLAStats.class, slaList);
			//QueryUtil.bulkCreateBos(slaList);

			activeClientMacSet4Check = null;
			
		} catch (Exception e) {
			DebugUtil.performanceDebugError("BeSLAStatsProcessor.run() catch exception", e);
		} catch (Error e) {
			DebugUtil.performanceDebugError("BeSLAStatsProcessor.run() catch error.", e);
		}
	}

	/**
	 * check validity
	 *
	 * @param slaStats -
	 * @param fieldName -
	 * @param fieldValue -
	 * @param macSet -
	 * @param baseCount -
	 */
	private void errorCheck(AhSLAStats slaStats, String fieldName, int fieldValue,
			Set<String> macSet, int baseCount) {
		if (fieldValue > 100) {
			DebugUtil.performanceDebugWarn("BeSLAStatsProcessor.errorCheck() " + fieldName
					+ " error.");
			DebugUtil.performanceDebugWarn("Owner: " + slaStats.getOwner().getDomainName()
					+ ", Global Flag: " + slaStats.isGlobalFlag());
			DebugUtil.performanceDebugWarn("Cached mac set: " + macSet);
			DebugUtil.performanceDebugWarn("Base count: " + baseCount);
		} else {
			// add some info log for debug
			DebugUtil.performanceDebugInfo("BeSLAStatsProcessor. calculate percentage value for field " + fieldName
					+ ", info log for debug.");
			DebugUtil.performanceDebugInfo("Owner: " + slaStats.getOwner().getDomainName()
					+ ", Global Flag: " + slaStats.isGlobalFlag());
			DebugUtil.performanceDebugInfo("Cached mac set: " + macSet);
			DebugUtil.performanceDebugInfo("Base count: " + baseCount);
		}
	}

	private int checkNumValid4Client(AhSLAStats slaStats, String fieldName, Set<String> macSet,
			int baseCount) {
		if (macSet == null) {
			return 0;
		}

		int value = macSet.size() * 100 / baseCount;

		errorCheck(slaStats, fieldName, value, macSet, baseCount);

		if (value > 100) {
			//
			if (activeClientMacSet4Check == null) {
				prepareActiveClientMacSet();
			}

			for (Iterator<String> iterator = macSet.iterator(); iterator.hasNext();) {
				String clientMac = iterator.next();
				if (!activeClientMacSet4Check.contains(clientMac)) {
					iterator.remove();
				}
			}

			int newValue = macSet.size() * 100 / baseCount;
			if (newValue > 100) {
				newValue = 100;
				DebugUtil
						.performanceDebugWarn("BeSLAStatsProcessor.checkNumValid4Client() After filter for active client, percentage value still not right, the new value = "
								+ newValue);
				DebugUtil.performanceDebugWarn("The new cached mac set: " + macSet);
			}
			return newValue;
		}

		return value;
	}

	private void prepareActiveClientMacSet() {
//		List<?> list = QueryUtil
//				.executeNativeQuery("select clientmac from ah_clientsession where connectstate="
//						+ AhClientSession.CONNECT_STATE_UP);
		List<?> list = DBOperationUtil
			.executeQuery("select clientmac from ah_clientsession where connectstate="
				+ AhClientSession.CONNECT_STATE_UP);
		activeClientMacSet4Check = new HashSet<String>(list.size());
		for (Object object : list) {
			activeClientMacSet4Check.add(String.valueOf(object));
		}
	}

	private Set<String> mergeValuesOfMap(Map<Long, Set<String>> map) {
		Set<String> totalSet = new HashSet<String>();
		for (Set<String> set : map.values()) {
			totalSet.addAll(set);
		}

		return totalSet;
	}

	private void prepareSLACount(long currentTime) {
		apMac_sla_red_Map.clear();
		apMac_sla_yellow_Map.clear();
		apMac_crc_error_Map.clear();
		apMac_tx_drop_Map.clear();
		apMac_rx_drop_Map.clear();
		apMac_tx_retry_Map.clear();
		apMac_airtime_Map.clear();
		clientMac_sla_red_Map.clear();
		clientMac_sla_yellow_Map.clear();
		clientMac_airtime_Map.clear();

		Map<String, AhNewSLAStats> sla = new HashMap<String, AhNewSLAStats>();
		
		calculateCommonAPI4AP(ap_sla_red_map.keySet(), apMac_sla_red_Map);
		calculateCommonAPI4AP(ap_sla_yellow_map.keySet(), apMac_sla_yellow_Map);
		calculateCommonAPI4AP(ap_crc_error_map.keySet(), apMac_crc_error_Map);
		calculateCommonAPI4AP(ap_tx_drop_map.keySet(), apMac_tx_drop_Map);
		calculateCommonAPI4AP(ap_rx_drop_map.keySet(), apMac_rx_drop_Map);
		calculateCommonAPI4AP(ap_tx_retry_map.keySet(), apMac_tx_retry_Map);
		calculateCommonAPI4AP(ap_airtime_map.keySet(), apMac_airtime_Map);
		calculateCommonAPI4Client(client_sla_red_Set, clientMac_sla_red_Map);
		calculateCommonAPI4Client(client_sla_yellow_Set, clientMac_sla_yellow_Map);
		calculateCommonAPI4Client(client_airtime_Set, clientMac_airtime_Map);

		// for client score
		clientScore_Red_Map.clear();
		clientScore_Yellow_Map.clear();
//		List<?> list = QueryUtil
//				.executeNativeQuery("select owner,clientmac,overallClientHealthScore from ah_clientsession where connectstate="
//						+ AhClientSession.CONNECT_STATE_UP
//						+ " and overallClientHealthScore<"
//						+ AhClientSession.CLIENT_SCORE_YELLOW);
		List<?> list = DBOperationUtil
			.executeQuery("select owner,clientmac,overallClientHealthScore, apMac, apName from ah_clientsession where connectstate="
				+ AhClientSession.CONNECT_STATE_UP
				+ " and overallClientHealthScore<"
				+ AhClientSession.CLIENT_SCORE_YELLOW);
		for (Object obj : list) {
			Object[] attrs = (Object[]) obj;
			Long domainID = Long.valueOf(attrs[0].toString());
			String clientMac = attrs[1].toString();
			byte clientScore = Byte.valueOf(attrs[2].toString());
			String apMac = attrs[3].toString();
			String apName = attrs[4].toString();

			if (clientScore < AhClientSession.CLIENT_SCORE_RED) {
				Set<String> clientMacSet = clientScore_Red_Map.get(domainID);
				if (clientMacSet == null) {
					clientMacSet = new HashSet<String>();
					clientScore_Red_Map.put(domainID, clientMacSet);
				}
				clientMacSet.add(clientMac);
				if (apMac!=null && !apMac.equals("")){
					if (sla.get(apMac)==null) {
						SimpleHiveAp ap = CacheMgmt.getInstance().getSimpleHiveAp(apMac);
						if (ap != null) {
							AhNewSLAStats stats = new AhNewSLAStats();
							HmDomain myDomain = CacheMgmt.getInstance().getCacheDomainById(ap.getDomainId());
							if (myDomain==null) continue;
							stats.setOwner(myDomain);
							stats.setClientCount(ap.getActiveClientCount());
							stats.setApMac(apMac);
							stats.setApName(apName);
							stats.getcScoreRedSet().add(clientMac);
							sla.put(apMac, stats);
						}
					}  else {
						sla.get(apMac).getcScoreRedSet().add(clientMac);
					}
				}
			} else {
				Set<String> clientMacSet = clientScore_Yellow_Map.get(domainID);
				if (clientMacSet == null) {
					clientMacSet = new HashSet<String>();
					clientScore_Yellow_Map.put(domainID, clientMacSet);
				}
				clientMacSet.add(clientMac);
				if (apMac!=null && !apMac.equals("")){
					if (sla.get(apMac)==null) {
						SimpleHiveAp ap = CacheMgmt.getInstance().getSimpleHiveAp(apMac);
						if (ap != null) {
							AhNewSLAStats stats = new AhNewSLAStats();
							HmDomain myDomain = CacheMgmt.getInstance().getCacheDomainById(ap.getDomainId());
							if (myDomain==null) continue;
							stats.setOwner(myDomain);
							stats.setClientCount(ap.getActiveClientCount());
							stats.setApMac(apMac);
							stats.setApName(apName);
							stats.getcScoreYellowSet().add(clientMac);
							sla.put(apMac, stats);
						}
					}  else {
						sla.get(apMac).getcScoreYellowSet().add(clientMac);
					}
				}
			}
		}

		clientCountMap = CacheMgmt.getInstance().getMaxClientsCountMap();
		
		for(SimpleHiveAp ap: ap_sla_red_map.keySet()){
			if (sla.get(ap.getMacAddress())==null) {
				AhNewSLAStats stats = new AhNewSLAStats();
				HmDomain myDomain = CacheMgmt.getInstance().getCacheDomainById(ap.getDomainId());
				if (myDomain==null) continue;
				stats.setOwner(myDomain);
				stats.setApMac(ap.getMacAddress());
				stats.setApName(ap.getHostname());
				stats.setClientCount(ap.getActiveClientCount());
				sla.put(ap.getMacAddress(), stats);
			} 
			AhNewSLAStats stats = sla.get(ap.getMacAddress());
			stats.setApTotal_Red(1);
			stats.setApSla_Red(1);
		}
		
		for(SimpleHiveAp ap: ap_sla_yellow_map.keySet()){
			if (sla.get(ap.getMacAddress())==null) {
				AhNewSLAStats stats = new AhNewSLAStats();
				stats.setApMac(ap.getMacAddress());
				HmDomain myDomain = CacheMgmt.getInstance().getCacheDomainById(ap.getDomainId());
				if (myDomain==null) continue;
				stats.setOwner(myDomain);
				stats.setApName(ap.getHostname());
				stats.setClientCount(ap.getActiveClientCount());
				sla.put(ap.getMacAddress(), stats);
			} 
			AhNewSLAStats stats = sla.get(ap.getMacAddress());
			stats.setApTotal_Yellow(1);
			stats.setApSla_Yellow(1);
		}
		
		for(SimpleHiveAp ap: ap_crc_error_map.keySet()){
			if (sla.get(ap.getMacAddress())==null) {
				AhNewSLAStats stats = new AhNewSLAStats();
				HmDomain myDomain = CacheMgmt.getInstance().getCacheDomainById(ap.getDomainId());
				if (myDomain==null) continue;
				stats.setOwner(myDomain);
				stats.setApMac(ap.getMacAddress());
				stats.setApName(ap.getHostname());
				stats.setClientCount(ap.getActiveClientCount());
				sla.put(ap.getMacAddress(), stats);
			} 
			AhNewSLAStats stats = sla.get(ap.getMacAddress());
			stats.setApTotal_Red(1);
			stats.setApCrcError_Red(1);
		}
		
		for(SimpleHiveAp ap: ap_tx_drop_map.keySet()){
			if (sla.get(ap.getMacAddress())==null) {
				AhNewSLAStats stats = new AhNewSLAStats();
				HmDomain myDomain = CacheMgmt.getInstance().getCacheDomainById(ap.getDomainId());
				if (myDomain==null) continue;
				stats.setOwner(myDomain);
				stats.setApMac(ap.getMacAddress());
				stats.setApName(ap.getHostname());
				stats.setClientCount(ap.getActiveClientCount());
				sla.put(ap.getMacAddress(), stats);
			} 
			AhNewSLAStats stats = sla.get(ap.getMacAddress());
			stats.setApTotal_Red(1);
			stats.setApTxDrop_Red(1);
		}
		
		for(SimpleHiveAp ap: ap_rx_drop_map.keySet()){
			if (sla.get(ap.getMacAddress())==null) {
				AhNewSLAStats stats = new AhNewSLAStats();
				HmDomain myDomain = CacheMgmt.getInstance().getCacheDomainById(ap.getDomainId());
				if (myDomain==null) continue;
				stats.setOwner(myDomain);
				stats.setApMac(ap.getMacAddress());
				stats.setApName(ap.getHostname());
				stats.setClientCount(ap.getActiveClientCount());
				sla.put(ap.getMacAddress(), stats);
			} 
			AhNewSLAStats stats = sla.get(ap.getMacAddress());
			stats.setApTotal_Red(1);
			stats.setApRxDrop_Red(1);
		}
		
		for(SimpleHiveAp ap: ap_tx_retry_map.keySet()){
			if (sla.get(ap.getMacAddress())==null) {
				AhNewSLAStats stats = new AhNewSLAStats();
				HmDomain myDomain = CacheMgmt.getInstance().getCacheDomainById(ap.getDomainId());
				if (myDomain==null) continue;
				stats.setOwner(myDomain);
				stats.setApMac(ap.getMacAddress());
				stats.setApName(ap.getHostname());
				stats.setClientCount(ap.getActiveClientCount());
				sla.put(ap.getMacAddress(), stats);
			} 
			AhNewSLAStats stats = sla.get(ap.getMacAddress());
			stats.setApTotal_Red(1);
			stats.setApRetry_Red(1);
		}
		
		for(SimpleHiveAp ap: ap_airtime_map.keySet()){
			if (sla.get(ap.getMacAddress())==null) {
				AhNewSLAStats stats = new AhNewSLAStats();
				HmDomain myDomain = CacheMgmt.getInstance().getCacheDomainById(ap.getDomainId());
				if (myDomain==null) continue;
				stats.setOwner(myDomain);
				stats.setApMac(ap.getMacAddress());
				stats.setApName(ap.getHostname());
				stats.setClientCount(ap.getActiveClientCount());
				sla.put(ap.getMacAddress(), stats);
			} 
			AhNewSLAStats stats = sla.get(ap.getMacAddress());
			stats.setApTotal_Red(1);
			stats.setApAirTime_Red(1);
		}
		
		
		for (BeTrapEvent event : client_sla_red_Set) {
			String apMac = event.getApMac();
			String apName = event.getApName();
			if (apMac == null) {
				continue;
			}
			SimpleHiveAp ap = CacheMgmt.getInstance().getSimpleHiveAp(apMac);
			if (ap == null) {
				continue;
			}
			
			if (sla.get(apMac)==null && ap.getActiveClientCount()>0) {
				AhNewSLAStats stats = new AhNewSLAStats();
				HmDomain myDomain = CacheMgmt.getInstance().getCacheDomainById(ap.getDomainId());
				if (myDomain==null) continue;
				stats.setOwner(myDomain);
				stats.setClientCount(ap.getActiveClientCount());
				stats.setApMac(apMac);
				stats.setApName(apName);
				sla.put(apMac, stats);
			} 
			if (sla.get(apMac)!=null && ap.getActiveClientCount()>0) {
				AhNewSLAStats stats = sla.get(apMac);
			
				stats.getcSlaRedSet().add(event.getRemoteID());
			}
		}
		
		for (BeTrapEvent event : client_sla_yellow_Set) {
			String apMac = event.getApMac();
			String apName = event.getApName();
			if (apMac == null) {
				continue;
			}
			SimpleHiveAp ap = CacheMgmt.getInstance().getSimpleHiveAp(apMac);
			if (ap == null) {
				continue;
			}
			
			if (sla.get(apMac)==null && ap.getActiveClientCount()>0) {
				AhNewSLAStats stats = new AhNewSLAStats();
				HmDomain myDomain = CacheMgmt.getInstance().getCacheDomainById(ap.getDomainId());
				if (myDomain==null) continue;
				stats.setOwner(myDomain);
				stats.setClientCount(ap.getActiveClientCount());
				stats.setApMac(apMac);
				stats.setApName(apName);
				sla.put(apMac, stats);
			} 
			if (sla.get(apMac)!=null && ap.getActiveClientCount()>0) {
				AhNewSLAStats stats = sla.get(apMac);
			
				stats.getcSlaYellowSet().add(event.getRemoteID());
			}
		}
		
		for (BeTrapEvent event : client_airtime_Set) {
			String apMac = event.getApMac();
			String apName = event.getApName();
			if (apMac == null) {
				continue;
			}
			SimpleHiveAp ap = CacheMgmt.getInstance().getSimpleHiveAp(apMac);
			if (ap == null) {
				continue;
			}
			
			if (sla.get(apMac)==null && ap.getActiveClientCount()>0) {
				AhNewSLAStats stats = new AhNewSLAStats();
				HmDomain myDomain = CacheMgmt.getInstance().getCacheDomainById(ap.getDomainId());
				if (myDomain==null) continue;
				stats.setOwner(myDomain);
				stats.setClientCount(ap.getActiveClientCount());
				stats.setApMac(apMac);
				stats.setApName(apName);
				sla.put(apMac, stats);
			} 
			if (sla.get(apMac)!=null && ap.getActiveClientCount()>0) {
				AhNewSLAStats stats = sla.get(apMac);
			
				stats.getcAirTimeSet().add(event.getRemoteID());
			}
		}
		List<?> apManList = QueryUtil.executeQuery("select connected,macAddress from " + HiveAp.class.getSimpleName(), 
				null, new FilterParams("manageStatus",HiveAp.STATUS_MANAGED));
		Map<String, Integer> apManMap = new HashMap<String,Integer>();
		for(Object obj: apManList){
			Object[] oneObj = (Object[]) obj;
			Integer con = ((boolean)oneObj[0])?0:1;
			apManMap.put(oneObj[1].toString(), con);
		}
		for(AhNewSLAStats stats: sla.values()){
			stats.setTimeStamp(currentTime);
			if (stats.getApSla_Red()==1) {
				stats.setApSla_Yellow(0);
			}
			if (stats.getApTotal_Red()==1) {
				stats.setApTotal_Yellow(0);
			}
			Integer con = apManMap.get(stats.getApMac());
			if(con!=null) {
				stats.setApStatus(apManMap.get(stats.getApMac()));
			}
			stats.getcSlaYellowSet().removeAll(stats.getcSlaRedSet());
			stats.getcScoreYellowSet().removeAll(stats.getcScoreRedSet());
			if (stats.getClientCount()!=0) {
				stats.setClientAirTime_Red(stats.getcAirTimeSet().size()* 100 /stats.getClientCount());
				if (stats.getClientAirTime_Red()>100) {
					stats.setClientAirTime_Red(100);
				}
				stats.setClientScore_Red(stats.getcScoreRedSet().size()* 100 /stats.getClientCount());
				if (stats.getClientScore_Red()>100) {
					stats.setClientScore_Red(100);
				}
				stats.setClientScore_Yellow(stats.getcScoreYellowSet().size() * 100 /stats.getClientCount());
				if (stats.getClientScore_Yellow() + stats.getClientScore_Red()>100 ) {
					stats.setClientScore_Yellow(100 - stats.getClientScore_Red());
				}
				stats.setClientSla_Red(stats.getcSlaRedSet().size() * 100 /stats.getClientCount() );
				if (stats.getClientSla_Red()>100) {
					stats.setClientSla_Red(100);
				}
				stats.setClientSla_Yellow(stats.getcSlaYellowSet().size() * 100 /stats.getClientCount() );
				if (stats.getClientSla_Yellow() + stats.getClientSla_Red()>100 ) {
					stats.setClientSla_Yellow(100 - stats.getClientSla_Red());
				}
				stats.setClientTotal_Red(stats.getCTotalRedCount() * 100/stats.getClientCount() );
				if (stats.getClientTotal_Red()>100) {
					stats.setClientTotal_Red(100);
				}
				stats.setClientTotal_Yellow(stats.getCTotalYellowCount() * 100 /stats.getClientCount() );
				if (stats.getClientTotal_Yellow() + stats.getClientTotal_Red()>100 ) {
					stats.setClientTotal_Yellow(100 - stats.getClientTotal_Red());
				}
			}
		}
		
		List<SimpleHiveAp> listAps = CacheMgmt.getInstance().getManagedApList();
		List<AhNewSLAStats> normalApList = new ArrayList<AhNewSLAStats>();
		for(SimpleHiveAp oneAp : listAps){
			HmDomain myDomain = CacheMgmt.getInstance().getCacheDomainById(oneAp.getDomainId());
			if (myDomain==null) continue;
			if(sla.get(oneAp.getMacAddress())==null) {
				AhNewSLAStats sta = new AhNewSLAStats();
				sta.setApMac(oneAp.getMacAddress());
				sta.setApName(oneAp.getHostname());
				sta.setClientCount(oneAp.getActiveClientCount());
				sta.setOwner(myDomain);
				sta.setTimeStamp(currentTime);
				Integer con = apManMap.get(oneAp.getMacAddress());
				if(con!=null) {
					sta.setApStatus(apManMap.get(oneAp.getMacAddress()));
				}
				normalApList.add(sta);
			}
		}
		
		if (!sla.values().isEmpty()) {
			try {
				Log.info("begin insert sla data to DB. size is: "+ sla.values().size() + " time is" + System.currentTimeMillis());
				BulkUpdateUtil.bulkInsert(AhNewSLAStats.class, sla.values());
				//QueryUtil.bulkCreateBos(sla.values());
				Log.info("end insert sla data to DB. time is" + System.currentTimeMillis());
			} catch (Exception e) {
				DebugUtil.performanceDebugError("Exception in client processor thread insert sla data error", e);
			}
		}
		
		if (!normalApList.isEmpty()) {
			try {
				Log.info("begin insert normal sla data to DB. size is: "+ normalApList.size() + " time is" + System.currentTimeMillis());
				BulkUpdateUtil.bulkInsert(AhNewSLAStats.class, normalApList);
				//QueryUtil.bulkCreateBos(normalApList);
				Log.info("end insert normal sla data to DB. time is" + System.currentTimeMillis());
			} catch (Exception e) {
				DebugUtil.performanceDebugError("Exception in client processor thread insert normal sla data error", e);
			}
		}
		clearTrapProcessor();
		
		clearDisconnectDeviceTrap();
		
		clearDisconnectClientTrap();
	}
	
	private void holdClearApTrap(ConcurrentMap<SimpleHiveAp, List<BeTrapEvent>> traps, boolean red) {
		try {
			if (traps==null) return;
			for(List<BeTrapEvent> lst : traps.values()){
				for(BeTrapEvent trap: lst){
					if (trap!=null) {
						if (trap.getTrapType() == BeTrapEvent.TYPE_BANDWIDTHSENTINELEVENT) {
							handleBandWidthTrap(trap, true, red);
						} else if (trap.getTrapType() == BeTrapEvent.TYPE_INTERFACECLIENTTRAP) {
							handleInterfaceClientTrap(trap, true);
						}
					}
				}
			}
		} catch (Exception ex) {
			DebugUtil.performanceDebugError("Exception in client processor thread holdClearApTrap", ex);
		} finally {
			traps.clear();
		}

	}
	
	private void holdClearClientTrap(ConcurrentSkipListSet<BeTrapEvent> traps, boolean red) {
		try {
			if (traps==null) return;
			for(BeTrapEvent trap: traps){
				if (trap!=null) {
					if (trap.getTrapType() == BeTrapEvent.TYPE_BANDWIDTHSENTINELEVENT) {
						handleBandWidthTrap(trap, true, red);
					} else if (trap.getTrapType() == BeTrapEvent.TYPE_INTERFACECLIENTTRAP) {
						handleInterfaceClientTrap(trap, true);
					}
				}
			}
		} catch (Exception ex) {
			DebugUtil.performanceDebugError("Exception in client processor thread holdClearClientTrap", ex);
		} finally {
			traps.clear();
		}
	}
	
	private void clearTrapProcessor(){
		holdClearApTrap(ap_sla_red_map_clear, true);
		holdClearApTrap(ap_sla_yellow_map_clear, false);
		holdClearApTrap(ap_crc_error_map_clear, false);
		holdClearApTrap(ap_tx_drop_map_clear, false);
		holdClearApTrap(ap_rx_drop_map_clear, false);
		holdClearApTrap(ap_tx_retry_map_clear, false);
		holdClearApTrap(ap_airtime_map_clear, false);
		holdClearClientTrap(client_sla_red_Set_clear, true);
		holdClearClientTrap(client_sla_yellow_Set_clear, false);
		holdClearClientTrap(client_airtime_Set_clear, false);
	}
	
	private void addRemovedDevice(Set<SimpleHiveAp> aplist){
		if (aplist==null || aplist.isEmpty()) {
			return ;
		}
		for(SimpleHiveAp ap: aplist) {
			if (ap!=null && ap.getMacAddress()!=null) {
				if (CacheMgmt.getInstance().getSimpleHiveAp(ap.getMacAddress())==null) {
					disconnectDeviceSet.add(ap);
				}
			}
		}
	}
	
	private void clearDisconnectDeviceTrap(){
		try {
			addRemovedDevice(ap_sla_red_map.keySet());
			addRemovedDevice(ap_sla_yellow_map.keySet());
			addRemovedDevice(ap_crc_error_map.keySet());
			addRemovedDevice(ap_tx_drop_map.keySet());
			addRemovedDevice(ap_rx_drop_map.keySet());
			addRemovedDevice(ap_tx_retry_map.keySet());
			addRemovedDevice(ap_airtime_map.keySet());
	
			for(SimpleHiveAp ap: disconnectDeviceSet) {
				ap_sla_red_map.remove(ap);
				ap_sla_yellow_map.remove(ap);
				ap_crc_error_map.remove(ap);
				ap_tx_drop_map.remove(ap);
				ap_rx_drop_map.remove(ap);
				ap_tx_retry_map.remove(ap);
				ap_airtime_map.remove(ap);
	
				removeInvalidClientTrapByMac(client_sla_red_Set,ap.getMacAddress(), true);
				removeInvalidClientTrapByMac(client_sla_yellow_Set,ap.getMacAddress(), true);
				removeInvalidClientTrapByMac(client_airtime_Set,ap.getMacAddress(), true);
			}
			
			disconnectDeviceSet.clear();
			
			// this is for remove ap when ap is removed.
			Set<String> apMacSet = new HashSet<String>();
			for(BeTrapEvent trap: client_airtime_Set) {
				if (trap.getApMac()!=null) {
					if (CacheMgmt.getInstance().getSimpleHiveAp(trap.getApMac())==null) {
						apMacSet.add(trap.getApMac());
					}
				}
			}
			for(String mac: apMacSet) {
				removeInvalidClientTrapByMac(client_airtime_Set,mac, true);
			}
			
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	private void removeInvalidClientTrapByMac(ConcurrentSkipListSet<BeTrapEvent> tpList, String mac, boolean isAp) {
		if (tpList!=null) {
			for (Iterator<BeTrapEvent> iterator = tpList.iterator(); iterator.hasNext();) {
				BeTrapEvent cacheEvent = iterator.next();
				if (isAp) {
					if (cacheEvent.getApMac().equals(mac)) {
						iterator.remove();
					}
				} else {
					if (cacheEvent.getRemoteID()!=null && !cacheEvent.getRemoteID().isEmpty()){
						if (cacheEvent.getRemoteID().equals(mac)) {
							iterator.remove();
						}
					}
				}
			}
		}
	}
	
	private void clearDisconnectClientTrap() {
		try {
			HashSet<String> clientMacSet = new HashSet<String>();
			if (client_sla_red_Set!=null) {
				for(BeTrapEvent trap: client_sla_red_Set) {
					if (trap!=null && trap.getRemoteID()!=null && !trap.getRemoteID().isEmpty()) {
						clientMacSet.add(trap.getRemoteID());
					}
				}
			}
			if (client_sla_yellow_Set!=null) {
				for(BeTrapEvent trap: client_sla_yellow_Set) {
					if (trap!=null && trap.getRemoteID()!=null && !trap.getRemoteID().isEmpty()) {
						clientMacSet.add(trap.getRemoteID());
					}
				}
			}
			if (client_airtime_Set!=null) {
				for(BeTrapEvent trap: client_airtime_Set) {
					if (trap!=null && trap.getRemoteID()!=null && !trap.getRemoteID().isEmpty()) {
						clientMacSet.add(trap.getRemoteID());
					}
				}
			}
			
			if (clientMacSet!=null && !clientMacSet.isEmpty()) {
				StringBuffer where = new StringBuffer();
				int i = 0;
				where.append("clientMac in (");
				for (i = 0; i < clientMacSet.size(); i++) {
					where.append("?");
					if (i != clientMacSet.size() - 1)
						where.append(",");
				}
				where.append(") and connectstate = ?");
				List<Object> paraList = new ArrayList<Object>();
				paraList.addAll(clientMacSet);
				paraList.add(AhClientSession.CONNECT_STATE_UP);
				List<?> list =  DBOperationUtil
						.executeQuery( "select distinct clientMac from ah_clientsession",
								null, new FilterParams(where.toString(), paraList.toArray()));
				for(Object obj: list){
					String mac= (String)obj;
					clientMacSet.remove(mac);
				}
				for(String disMac: clientMacSet) {
					removeInvalidClientTrapByMac(client_sla_red_Set, disMac, false);
					removeInvalidClientTrapByMac(client_sla_yellow_Set, disMac, false);
					removeInvalidClientTrapByMac(client_airtime_Set, disMac, false);
					
					for(SimpleHiveAp ap: ap_sla_red_map.keySet()){
						if (ap_sla_red_map.get(ap)!=null) {
							for (Iterator<BeTrapEvent> iterator = ap_sla_red_map.get(ap).iterator(); iterator.hasNext();) {
								BeTrapEvent cacheEvent = iterator.next();
								if (cacheEvent.getRemoteID()!=null && !cacheEvent.getRemoteID().isEmpty()){
									if (cacheEvent.getRemoteID().equals(disMac)) {
										iterator.remove();
									}
								}
							}
						}
						if (ap_sla_red_map.get(ap) == null || ap_sla_red_map.get(ap).isEmpty()) {
							ap_sla_red_map.remove(ap);
						}
					}
					
					for(SimpleHiveAp ap: ap_sla_yellow_map.keySet()){
						if (ap_sla_yellow_map.get(ap)!=null) {
							for (Iterator<BeTrapEvent> iterator = ap_sla_yellow_map.get(ap).iterator(); iterator.hasNext();) {
								BeTrapEvent cacheEvent = iterator.next();
								if (cacheEvent.getRemoteID()!=null && !cacheEvent.getRemoteID().isEmpty()){
									if (cacheEvent.getRemoteID().equals(disMac)) {
										iterator.remove();
									}
								}
							}
						}
						if (ap_sla_yellow_map.get(ap) == null || ap_sla_yellow_map.get(ap).isEmpty()) {
							ap_sla_yellow_map.remove(ap);
						}
					}
				}
			}
		
		} catch (Exception e) {
			log.error(e);
		}
		
	}

	private void calculateCommonAPI4Client(ConcurrentSkipListSet<BeTrapEvent> clientEventSet,
			Map<Long, Set<String>> map) {
		for (BeTrapEvent event : clientEventSet) {
			String apMac = event.getApMac();
			if (apMac == null) {
				continue;
			}

			SimpleHiveAp ap = CacheMgmt.getInstance().getSimpleHiveAp(apMac);
			if (ap == null) {
				continue;
			}

			Set<String> set = map.get(ap.getDomainId());
			if (set == null) {
				set = new HashSet<String>();
				map.put(ap.getDomainId(), set);
			}
			set.add(event.getRemoteID());
		}
	}

	private void calculateCommonAPI4AP(Collection<SimpleHiveAp> apCollection,
			Map<Long, Set<String>> map) {
		for (SimpleHiveAp ap : apCollection) {
			Set<String> set = map.get(ap.getDomainId());
			if (set == null) {
				set = new HashSet<String>();
				map.put(ap.getDomainId(), set);
			}
			set.add(ap.getMacAddress());
		}
	}

	class EventProcessorThread extends Thread {
		@Override
		public void run() {
			MgrUtil.setTimerName(this.getClass().getSimpleName());
			BeLogTools
					.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
							"<BE Thread> SLA stats processor - handle snmp trap event processor is running...");

			while (isContinue) {
				try {
					BeCommunicationEvent snmpEventbase = getEvent();
					BeTrapEvent snmpEvent = null;
					BeAPConnectEvent snmpEventDisconn = null;
					if (snmpEventbase instanceof BeTrapEvent) {
						snmpEvent = (BeTrapEvent)snmpEventbase;
						//FIXME if TrapType has 0
						if (null == snmpEvent || snmpEvent.getTrapType() == 0) {
							continue;
						}

						if (snmpEvent.getTrapType() == BeTrapEvent.TYPE_BANDWIDTHSENTINELEVENT) {
							handleBandWidthTrap(snmpEvent, false, false);
						} else if (snmpEvent.getTrapType() == BeTrapEvent.TYPE_INTERFACECLIENTTRAP) {
							handleInterfaceClientTrap(snmpEvent, false);
						}
					} else if (snmpEventbase instanceof BeAPConnectEvent) {
						snmpEventDisconn = (BeAPConnectEvent)snmpEventbase;
						//FIXME if TrapType has 0
						if (null == snmpEventDisconn || snmpEventDisconn.getMsgType()!=BeCommunicationConstant.MESSAGETYPE_APDISCONNECT) {
							continue;
						}
						handleDeviceDisConnectTrap(snmpEventDisconn);
					}
					

				} catch (Exception e) {
					DebugUtil.performanceDebugWarn("Exception in client processor thread", e);
				} catch (Error e) {
					DebugUtil.performanceDebugWarn("Error in client processor thread", e);
				}
			}
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"<BE Thread> SLA stats processor - processor is shutdown.");
		}
	}

	private void handleBandWidthTrap(BeTrapEvent trapEvent, boolean clearFlg, boolean red) {
		SimpleHiveAp ap = CacheMgmt.getInstance().getSimpleHiveAp(trapEvent.getApMac());
		if (ap == null || ap.getManageStatus() != HiveAp.STATUS_MANAGED) {
			ap_sla_red_map.remove(ap);
			ap_sla_yellow_map.remove(ap);
			return;
		}
		if (clearFlg) {
			List<BeTrapEvent> list = null;
			if (red) {
				list = ap_sla_red_map.get(ap);
				if (list != null) {
					list.remove(trapEvent);
					if (list.isEmpty()) {
						ap_sla_red_map.remove(ap);
					}
				}
				// for client
				if (client_sla_red_Set.contains(trapEvent)) {
					client_sla_red_Set.remove(trapEvent);
				}
			} else {
				// remove from yellow cache if exists
				list = ap_sla_yellow_map.get(ap);
				if (list != null) {
					list.remove(trapEvent);
					if (list.isEmpty()) {
						ap_sla_yellow_map.remove(ap);
					}
				}
				client_sla_yellow_Set.remove(trapEvent);
			}
		} else {
			if (trapEvent.getBandwidthSentinelStatus() == AhBandWidthSentinelHistory.STATUS_BAD
					|| trapEvent.getBandwidthSentinelStatus() == AhBandWidthSentinelHistory.STATUS_ALERT) {
				List<BeTrapEvent> list = null;
//				if (clearFlg) {
//					// remove from yellow cache if exists
//					list = ap_sla_yellow_map.get(ap);
//					if (list != null) {
//						list.remove(trapEvent);
//						if (list.isEmpty()) {
//							ap_sla_yellow_map.remove(ap);
//						}
//					}
//					client_sla_yellow_Set.remove(trapEvent);
//				} else {
					// red
					list = getListValueFromMap(ap_sla_red_map, ap);
					// for ap
					putIfAbsent4Client(list, trapEvent);
	
					// for client
					client_sla_red_Set.add(trapEvent);
					
					// add this code for clear trap
					list = getListValueFromMap(ap_sla_red_map_clear, ap);
					list.remove(trapEvent);
					
					client_sla_red_Set_clear.remove(trapEvent);
					
					list = getListValueFromMap(ap_sla_yellow_map_clear, ap);
					putIfAbsent4Client(list, trapEvent);
					client_sla_yellow_Set_clear.add(trapEvent);
//				}
	
			} else if (trapEvent.getBandwidthSentinelStatus() == AhBandWidthSentinelHistory.STATUS_CLEAR
					&& (trapEvent.getBandWidthAction() == AhBandWidthSentinelHistory.ACTION_STATUS_BOOST_YES)){
				List<BeTrapEvent> list=null;
				// yellow
//				if (clearFlg) {
//					// for ap
//					list = ap_sla_red_map.get(ap);
//					if (list != null) {
//						list.remove(trapEvent);
//						if (list.isEmpty()) {
//							ap_sla_red_map.remove(ap);
//						}
//					}
//					// for client
//					if (client_sla_red_Set.contains(trapEvent)) {
//						client_sla_red_Set.remove(trapEvent);
//					}
//				} else {
					// for ap
					list = ap_sla_yellow_map.get(ap);
					if (list == null || list.isEmpty()) {
						list = getListValueFromMap(ap_sla_yellow_map, ap);
						putIfAbsent4Client(list, trapEvent);
					}
					
					// for client
					if (!client_sla_yellow_Set.contains(trapEvent)) {
						client_sla_yellow_Set.add(trapEvent);
					}
					
					// add this code for clear trap
					list = getListValueFromMap(ap_sla_yellow_map_clear, ap);
					list.remove(trapEvent);
					
					client_sla_yellow_Set_clear.remove(trapEvent);
					
					list = getListValueFromMap(ap_sla_red_map_clear, ap);
					putIfAbsent4Client(list, trapEvent);
					client_sla_red_Set_clear.add(trapEvent);
//				}
	
			} else if (trapEvent.getBandwidthSentinelStatus() == AhBandWidthSentinelHistory.STATUS_CLEAR) {
				List<BeTrapEvent> list = null;
//				if (clearFlg) {
//					// for ap
//					list = ap_sla_red_map.get(ap);
//					if (list != null) {
//						list.remove(trapEvent);
//						if (list.isEmpty()) {
//							ap_sla_red_map.remove(ap);
//						}
//					}
//		
//					list = ap_sla_yellow_map.get(ap);
//					if (list != null) {
//						list.remove(trapEvent);
//						if (list.isEmpty()) {
//							ap_sla_yellow_map.remove(ap);
//						}
//					}
//		
//					// for client
//					client_sla_red_Set.remove(trapEvent);
//					client_sla_yellow_Set.remove(trapEvent);
//				} else {
					// add this code for clear trap
					list = getListValueFromMap(ap_sla_red_map_clear, ap);
					putIfAbsent4Client(list, trapEvent);
					list = getListValueFromMap(ap_sla_yellow_map_clear, ap);
					putIfAbsent4Client(list, trapEvent);
					
					client_sla_red_Set_clear.add(trapEvent);
					client_sla_yellow_Set_clear.add(trapEvent);
//				}
			}
		}
	}

	private void handleInterfaceClientTrap(BeTrapEvent trapEvent, boolean clearFlg) {
		SimpleHiveAp ap = CacheMgmt.getInstance().getSimpleHiveAp(trapEvent.getApMac());
		if (ap == null) {
			return;
		}

		if (trapEvent.isFailureFlag()) {
			// alarm
			if (!clearFlg) {
				if (trapEvent.getSourceType() == BeTrapEvent.SOURCE_CLIENT) {
					// client
					switch (trapEvent.getAlertType()) {
					// case BeTrapEvent.ALERTTYPE_TXDROP:
					// client_tx_drop_Set.add(trapEvent);
					// break;
					//
					// case BeTrapEvent.ALERTTYPE_RXDROP:
					// client_rx_drop_Set.add(trapEvent);
					// break;
	
					case BeTrapEvent.ALERTTYPE_AIRTIMECONSUME:
						client_airtime_Set.add(trapEvent);
						client_airtime_Set_clear.remove(trapEvent);
						break;
	
					default:
						break;
					}
	
				} else {
					// interface
					switch (trapEvent.getAlertType()) {
					case BeTrapEvent.ALERTTYPE_CRCERROR:
						putIfAbsent4APInterface(getListValueFromMap(ap_crc_error_map, ap), trapEvent);
						removeIfExist4APInterface(ap_crc_error_map_clear.get(ap), trapEvent);
						break;
	
					case BeTrapEvent.ALERTTYPE_TXDROP:
						putIfAbsent4APInterface(getListValueFromMap(ap_tx_drop_map, ap), trapEvent);
						removeIfExist4APInterface(ap_tx_drop_map_clear.get(ap), trapEvent);
						break;
	
					case BeTrapEvent.ALERTTYPE_TXRETRY:
						putIfAbsent4APInterface(getListValueFromMap(ap_tx_retry_map, ap), trapEvent);
						removeIfExist4APInterface(ap_tx_retry_map_clear.get(ap), trapEvent);
						break;
	
					case BeTrapEvent.ALERTTYPE_RXDROP:
						putIfAbsent4APInterface(getListValueFromMap(ap_rx_drop_map, ap), trapEvent);
						removeIfExist4APInterface(ap_rx_drop_map_clear.get(ap), trapEvent);
						break;
	
					case BeTrapEvent.ALERTTYPE_AIRTIMECONSUME:
						putIfAbsent4APInterface(getListValueFromMap(ap_airtime_map, ap), trapEvent);
						removeIfExist4APInterface(ap_airtime_map_clear.get(ap), trapEvent);
						break;
	
					default:
						break;
					}
				}
			}

		} else {
			// clear alarm
			if (clearFlg){
				if (trapEvent.getSourceType() == BeTrapEvent.SOURCE_CLIENT) {
					// client
					switch (trapEvent.getAlertType()) {
					// case BeTrapEvent.ALERTTYPE_TXDROP:
					// client_tx_drop_Set.remove(trapEvent);
					// break;
					//
					// case BeTrapEvent.ALERTTYPE_RXDROP:
					// client_rx_drop_Set.remove(trapEvent);
					// break;
	
					case BeTrapEvent.ALERTTYPE_AIRTIMECONSUME:
						client_airtime_Set.remove(trapEvent);
						break;
	
					default:
						break;
					}
	
				} else {
					// interface
					switch (trapEvent.getAlertType()) {
					case BeTrapEvent.ALERTTYPE_CRCERROR:
						removeIfExist4APInterface(ap_crc_error_map.get(ap), trapEvent);
						removeMapKeyIfEmpty(ap_crc_error_map, ap);
						break;
	
					case BeTrapEvent.ALERTTYPE_TXDROP:
						removeIfExist4APInterface(ap_tx_drop_map.get(ap), trapEvent);
						removeMapKeyIfEmpty(ap_tx_drop_map, ap);
						break;
	
					case BeTrapEvent.ALERTTYPE_TXRETRY:
						removeIfExist4APInterface(ap_tx_retry_map.get(ap), trapEvent);
						removeMapKeyIfEmpty(ap_tx_retry_map, ap);
						break;
	
					case BeTrapEvent.ALERTTYPE_RXDROP:
						removeIfExist4APInterface(ap_rx_drop_map.get(ap), trapEvent);
						removeMapKeyIfEmpty(ap_rx_drop_map, ap);
						break;
	
					case BeTrapEvent.ALERTTYPE_AIRTIMECONSUME:
						removeIfExist4APInterface(ap_airtime_map.get(ap), trapEvent);
						removeMapKeyIfEmpty(ap_airtime_map, ap);
						break;
	
					default:
						break;
					}
				}
			} else {
				if (trapEvent.getSourceType() == BeTrapEvent.SOURCE_CLIENT) {
					// client
					switch (trapEvent.getAlertType()) {
					// case BeTrapEvent.ALERTTYPE_TXDROP:
					// client_tx_drop_Set.remove(trapEvent);
					// break;
					//
					// case BeTrapEvent.ALERTTYPE_RXDROP:
					// client_rx_drop_Set.remove(trapEvent);
					// break;
	
					case BeTrapEvent.ALERTTYPE_AIRTIMECONSUME:
						client_airtime_Set_clear.add(trapEvent);
						break;
	
					default:
						break;
					}
	
				} else {
					// interface
					switch (trapEvent.getAlertType()) {
					case BeTrapEvent.ALERTTYPE_CRCERROR:
						putIfAbsent4APInterface(getListValueFromMap(ap_crc_error_map_clear, ap), trapEvent);
						break;
	
					case BeTrapEvent.ALERTTYPE_TXDROP:
						putIfAbsent4APInterface(getListValueFromMap(ap_tx_drop_map_clear, ap), trapEvent);
						break;
	
					case BeTrapEvent.ALERTTYPE_TXRETRY:
						putIfAbsent4APInterface(getListValueFromMap(ap_tx_retry_map_clear, ap), trapEvent);
						break;
	
					case BeTrapEvent.ALERTTYPE_RXDROP:
						putIfAbsent4APInterface(getListValueFromMap(ap_rx_drop_map_clear, ap), trapEvent);
						break;
	
					case BeTrapEvent.ALERTTYPE_AIRTIMECONSUME:
						putIfAbsent4APInterface(getListValueFromMap(ap_airtime_map_clear, ap), trapEvent);
						break;
	
					default:
						break;
					}
				}
				
				
			}
		}
	}
	
	private void handleDeviceDisConnectTrap(BeAPConnectEvent trapEvent) {
		SimpleHiveAp ap = CacheMgmt.getInstance().getSimpleHiveAp(trapEvent.getApMac());
		if (ap!=null && ap.getManageStatus()==HiveAp.STATUS_MANAGED) {
			disconnectDeviceSet.add(ap);
		}
	}

	private void removeMapKeyIfEmpty(ConcurrentMap<SimpleHiveAp, List<BeTrapEvent>> map,
			SimpleHiveAp key) {
		List<BeTrapEvent> value = map.get(key);
		if (value == null) {
			return;
		}

		if (value.isEmpty()) {
			map.remove(key);
		}
	}

	private void putIfAbsent4Client(List<BeTrapEvent> list, BeTrapEvent trapEvent) {
		boolean isContain = false;

		for (BeTrapEvent cacheEvent : list) {
			if (cacheEvent.getRemoteID().equals(trapEvent.getRemoteID())) {
				isContain = true;
				break;
			}
		}

		if (!isContain) {
			list.add(trapEvent);
		}
	}

	private void putIfAbsent4APInterface(List<BeTrapEvent> list, BeTrapEvent trapEvent) {
		boolean isContain = false;

		for (BeTrapEvent cacheEvent : list) {
			if (cacheEvent.getIfIndex() == trapEvent.getIfIndex()) {
				isContain = true;
				break;
			}
		}

		if (!isContain) {
			list.add(trapEvent);
		}
	}

	private void removeIfExist4APInterface(List<BeTrapEvent> list, BeTrapEvent trapEvent) {
		if (list == null) {
			return;
		}

		for (Iterator<BeTrapEvent> iterator = list.iterator(); iterator.hasNext();) {
			BeTrapEvent cacheEvent = iterator.next();
			if (cacheEvent.getIfIndex() == trapEvent.getIfIndex()) {
				iterator.remove();
			}
		}
	}

	private List<BeTrapEvent> getListValueFromMap(
			ConcurrentMap<SimpleHiveAp, List<BeTrapEvent>> map, SimpleHiveAp ap) {
		List<BeTrapEvent> list = map.get(ap);
		if (list == null) {
			list = new ArrayList<BeTrapEvent>();
			map.put(ap, list);
		}

		return list;
	}

	/**
	 * add event to client session queue
	 * 
	 * @param event
	 *            -
	 */
	public void addEvent(BeCommunicationEvent event) {
		try {
			eventQueue.offer(event);
		} catch (Exception e) {
			DebugUtil.performanceDebugError("BeSLAStatsProcessor.addEvent() catch exception", e);
		}
	}

	/**
	 * get event from client session queue
	 * 
	 * @return BeBaseEvent or null
	 */
	private BeCommunicationEvent getEvent() {
		try {
			return eventQueue.take();
		} catch (Exception e) {
			DebugUtil.performanceDebugError("BeSLAStatsProcessor.getEvent() catch exception", e);
			return null;
		}
	}

	public boolean shutdown() {
		if (!scheduler.isShutdown()) {
			// Disable new tasks from being submitted.
			scheduler.shutdown();
		}

		isContinue = false;
		eventQueue.clear();
		BeTrapEvent stopThreadEvent = new BeTrapEvent();
		eventQueue.offer(stopThreadEvent);
		return true;
	}

}