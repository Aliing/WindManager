package com.ah.be.performance;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.ah.be.app.AhAppContainer;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.app.HmBeLogUtil;
import com.ah.be.common.ConfigUtil;
import com.ah.be.common.DBOperationUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeCapwapClientResultEvent;
import com.ah.be.communication.event.BeCliEvent;
import com.ah.be.communication.event.BeInterfaceClientEvent;
import com.ah.be.communication.event.BeInterfaceClientResultEvent;
import com.ah.be.communication.event.BeTrapEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.performance.db.BulkOperationProcessor;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.admin.LogSettings;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.performance.AhClientSession;
import com.ah.bo.performance.AhClientStats;
import com.ah.bo.performance.AhDeviceStats;
import com.ah.bo.performance.AhInterfaceStats;
import com.ah.util.MgrUtil;

/**
 * 
 *@filename		BeInterfaceClientStatsProcessor.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2010-3-18 10:02:54
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modify history*
 * 
 */
public class BeInterfaceClientStatsProcessor implements Runnable {

	// time unit is minutes
	private int interval = 30;

	private final int intervalExt = 10;

	private int index = 0;

	private byte CYCLE_APNUM = 10;

	private final short RELAXTIME = 1000;

	private final ConcurrentMap<SimpleHiveAp, List<BeTrapEvent>> apMap4PollMoreQuick = new ConcurrentHashMap<>();

	/**
	 * Construct method
	 */
	public BeInterfaceClientStatsProcessor() {
		eventQueue = new LinkedBlockingQueue<>(eventQueueSize);
		try {
			CYCLE_APNUM = Byte.parseByte(ConfigUtil.getConfigInfo(
					ConfigUtil.SECTION_PERFORMANCE,
					ConfigUtil.KEY_POLLING_DEVICE_NUMBER_PER_SEC, "10"));
		}catch (Exception e) {
		}
	}

	public void startTask() {
		List<LogSettings> list = QueryUtil.executeQuery(LogSettings.class,
				null, null);
		if (!list.isEmpty()) {
			interval = list.get(0).getInterfaceStatsInterval();
			if(interval <= 0)
				interval = 30;
		}

		// start scheduler
		AhAppContainer.HmBe.getPerformModule().getTimerProcessor().registerTimer(this, interval*60);

		// execute this task every 10 minutes
		AhAppContainer.HmBe.getPerformModule().getTimerProcessor().registerTimer(runBodyExt, intervalExt*60,intervalExt*60);
		
		isContinue = true;
		processEventThread = new InterfaceClientStatsEventProcessorThread();
		processEventThread.setName("processInterfaceClientStatsThread");
		processEventThread.start();
	}

	@Override
	public void run() {
		try {
			MgrUtil.setTimerName(getClass().getSimpleName());
			BeLogTools.info(HmLogConst.M_PERFORMANCE, "BeInterfaceClientStatsProcessor.run(): Start collect interface/clients/device stats thread.");

			List<SimpleHiveAp> apList = CacheMgmt.getInstance()
					.getManagedApList();
			if (apList.isEmpty()) {
				return;
			}

			pollingBeginTime = System.currentTimeMillis();
			pollingPreviousAPtime = pollingBeginTime;
			pollingTotalAps = 0;
			
			index = 0;
			for (SimpleHiveAp ap : apList) {
				if (NmsUtil.compareSoftwareVersion("3.5.1.0", ap.getSoftVer()) > 0) {
					continue;
				}

				BeInterfaceClientEvent request = new BeInterfaceClientEvent();
				request.setSimpleHiveAp(ap);
				request.setQueryAllTable(true);
				request.setSequenceNum(HmBeCommunicationUtil
						.getSequenceNumber());
				request.buildPacket();
				int serialNum = HmBeCommunicationUtil.sendRequest(request);

				if (serialNum == BeCommunicationConstant.SERIALNUM_SENDREQUESTFAILED) {
					// connect to capwap closed
					BeLogTools.error(HmLogConst.M_PERFORMANCE,"BeInterfaceClientStatsProcessor.run(): Send request failed, capwap connect closed.");
					HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR,
							HmSystemLog.FEATURE_MONITORING,
							MgrUtil.getUserMessage("hm.system.log.be.interface.client.capwap.closed"));
					return;
				}
				if (++index == CYCLE_APNUM) {
					try {
						Thread.sleep(RELAXTIME);
					} catch (Exception e) {
						BeLogTools.warn(HmLogConst.M_PERFORMANCE,
										"BeInterfaceClientStatsProcessor.run() catch exception: ",
										e);
					}

					index = 0;
				}
			}
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE,"BeInterfaceClientStatsProcessor.run() catch exception", e);
		} catch (Error e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE,"BeInterfaceClientStatsProcessor.run() catch error.", e);
		}
	}

	private void handleInterfaceClientTrap(BeTrapEvent trapEvent) {
		SimpleHiveAp ap = CacheMgmt.getInstance().getSimpleHiveAp(
				trapEvent.getApMac());
		if (ap == null) {
			return;
		}

		boolean isExisted = isContainTrapInMap(ap, trapEvent);

		if (trapEvent.isFailureFlag()) {
			// alert
			if (!isExisted) {
				List<BeTrapEvent> eventList = apMap4PollMoreQuick.get(ap);
				if (eventList == null) {
					eventList = new ArrayList<>();
				}
				eventList.add(trapEvent);
				apMap4PollMoreQuick.put(ap, eventList);

				// send cli for change poll period
				String keyOpen = ConfigUtil.getConfigInfo(ConfigUtil.SECTION_PERFORMANCE, ConfigUtil.KEY_OPEN_REPORT_COLLECTION_HIGH_INTERVAL);
				if (keyOpen!=null && keyOpen.equalsIgnoreCase("false")) {
					return;
				}
				sendCLI4ChangePollPeriod(ap, 1);
			}
		} else {
			// clear alert
			if (isExisted) {
				removeSnmpTrapEventFromCacheMap(ap, trapEvent);

				// send cli for change poll period
				if (!apMap4PollMoreQuick.containsKey(ap)) {
					sendCLI4ResetPollPeriod(ap);
				}
			}
		}
	}

	private void sendCLI4ChangePollPeriod(SimpleHiveAp ap, int value) {
		try {
			BeCliEvent cliRequest = new BeCliEvent();
			cliRequest.setSimpleHiveAp(ap);
			cliRequest.setClis(new String[] { "report statistic period "
					+ value + "\n" });
			cliRequest
					.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
			cliRequest.buildPacket();

			HmBeCommunicationUtil.sendRequest(cliRequest);
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE,"BeInterfaceClientStatsProcessor.sendCLI4ChangePollPeriod() catch exception.",
							e);
		}
	}

	private void sendCLI4ResetPollPeriod(SimpleHiveAp ap) {
		int interval = 10;
		List<?> list = QueryUtil
				.executeNativeQuery("select a.collectionInterval from config_template a,hive_ap b where b.template_id=a.id and b.macaddress='"
						+ ap.getMacAddress() + "'");
		if (!list.isEmpty()) {
			interval = (Integer) list.get(0);
		}

		sendCLI4ChangePollPeriod(ap, interval);
	}

	private boolean isContainTrapInMap(SimpleHiveAp ap, BeTrapEvent trapEvent) {
		List<BeTrapEvent> eventList = apMap4PollMoreQuick.get(ap);
		if (eventList == null || eventList.isEmpty()) {
			return false;
		}

		if (trapEvent.getSourceType() == BeTrapEvent.SOURCE_CLIENT) {
			// client
			for (BeTrapEvent cacheEvent : eventList) {
				if (cacheEvent.getRemoteID().equals(trapEvent.getRemoteID())
						&& cacheEvent.getAlertType() == trapEvent
								.getAlertType()) {
					return true;
				}
			}
		} else {
			// interface
			for (BeTrapEvent cacheEvent : eventList) {
				if (cacheEvent.getIfIndex() == trapEvent.getIfIndex()
						&& cacheEvent.getAlertType() == trapEvent
								.getAlertType()) {
					return true;
				}
			}
		}

		return false;
	}

	private void removeSnmpTrapEventFromCacheMap(SimpleHiveAp ap,
			BeTrapEvent trapEvent) {
		List<BeTrapEvent> eventList = apMap4PollMoreQuick.get(ap);
		if (eventList == null || eventList.isEmpty()) {
			return;
		}

		if (trapEvent.getSourceType() == BeTrapEvent.SOURCE_CLIENT) {
			// client
			for (Iterator<BeTrapEvent> iter = eventList.iterator(); iter
					.hasNext();) {
				BeTrapEvent cacheEvent = iter.next();
				if (cacheEvent.getRemoteID().equals(trapEvent.getRemoteID())
						&& cacheEvent.getAlertType() == trapEvent
								.getAlertType()) {
					iter.remove();
				}
			}
		} else {
			// interface
			for (Iterator<BeTrapEvent> iter = eventList.iterator(); iter
					.hasNext();) {
				BeTrapEvent cacheEvent = iter.next();
				if (cacheEvent.getIfIndex() == trapEvent.getIfIndex()
						&& cacheEvent.getAlertType() == trapEvent
								.getAlertType()) {
					iter.remove();
				}
			}
		}

		if (eventList.isEmpty()) {
			apMap4PollMoreQuick.remove(ap);
		}
	}

	/**
	 * collect interface or clients stats more frequently
	 */
	final Runnable runBodyExt = new Runnable() {
		@Override
		public void run() {
			MgrUtil.setTimerName(getClass().getSimpleName());

			try {
				BeLogTools.info(HmLogConst.M_PERFORMANCE,"Start collect interface/clients/device stats(Short Interval) at "
								+ (new Date()));

				for (SimpleHiveAp ap : apMap4PollMoreQuick.keySet()) {
					BeInterfaceClientEvent request = new BeInterfaceClientEvent();
					request.setSimpleHiveAp(ap);
					request.setQueryAllTable(true);
					request.setSequenceNum(HmBeCommunicationUtil
							.getSequenceNumber());
					request.buildPacket();
					HmBeCommunicationUtil.sendRequest(request);
				}
			} catch (Exception e) {
				BeLogTools.error(HmLogConst.M_PERFORMANCE,"BeInterfaceClientStatsProcessor.runBodyExt catch exception.",
								e);
			} catch (Error e) {
				BeLogTools.error(HmLogConst.M_PERFORMANCE,"BeInterfaceClientStatsProcessor.runBodyExt catch error.",
								e);
			}
		}
	};

	public void updateInterfaceStatsPollInterval(int interval) {
		AhAppContainer.HmBe.getPerformModule().getTimerProcessor().registerTimer(this, interval*60);

	}

	public boolean shutdown() {
		isContinue = false;
		eventQueue.clear();
		BeBaseEvent stopThreadEvent = new BeBaseEvent();
		eventQueue.offer(stopThreadEvent);
		return true;
	}

	/**
	 * the following code is used for the process interface client stats data
	 */
	/**
	 * queue for event about client
	 */
	private final BlockingQueue<BeBaseEvent> eventQueue;

	private static final int eventQueueSize = 10000;

	private boolean isContinue = true;

	private int lostEventCount = 0;

	private InterfaceClientStatsEventProcessorThread processEventThread;
	
	// used for log
	private long pollingBeginTime = 0;
	private long pollingPreviousAPtime = 0;
	private long pollingTotalAps = 0;

	/**
	 * add event to queue
	 * 
	 * @param event
	 *            -
	 */
	public void addEvent(BeBaseEvent event) {
		try {
			if(eventQueue.offer(event)) {
				if (lostEventCount > 0) {
					BeLogTools.error(HmLogConst.M_PERFORMANCE,"BeInterfaceClientStatsProcessor.addEvent(): Lost "
									+ lostEventCount + " events");
					lostEventCount = 0;
				}
			}
			else {
				lostEventCount++;
			}
		} catch (Exception e) {
			lostEventCount++;
			// DebugUtil.performanceDebugError(
			// "BePCIDataCollector.addEvent(): Exception while add event to queue"
			// , e);
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
			BeLogTools.error(HmLogConst.M_PERFORMANCE,"BeInterfaceClientStatsProcessor.getEvent(): Exception while get event from queue",
							e);
			return null;
		}
	}

	/**
	 * handle interface client result
	 * 
	 * @param resultEvent
	 *            -
	 */
	private void handleInterfaceClientResultEvent(
			BeInterfaceClientResultEvent resultEvent) {
		try {
			StringBuilder clientBuffer = new StringBuilder();
			if (resultEvent.getInterfaceStatsList() != null
					&& !resultEvent.getInterfaceStatsList().isEmpty()) {
				BulkOperationProcessor.addBoList(AhInterfaceStats.class, resultEvent.getInterfaceStatsList());
			}
			if (resultEvent.getClientStatsList() != null
					&& !resultEvent.getClientStatsList().isEmpty()) {
				BulkOperationProcessor.addBoList(AhClientStats.class, resultEvent.getClientStatsList());
				clientBuffer.append(createUpdateClientSQL(resultEvent
						.getClientStatsList()));
			}
			if (resultEvent.getDeviceStatsList() != null	
					&& !resultEvent.getDeviceStatsList().isEmpty()) {
				BulkOperationProcessor.addBoList(AhDeviceStats.class, resultEvent.getDeviceStatsList());
			}

			DBOperationUtil.executeUpdate(clientBuffer.toString());
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE,"BeInterfaceClientStatsProcessor.handleInterfaceClientResultEvent(): insert native sql error.",
							e);
		}
	}

	private String createUpdateClientSQL(List<AhClientStats> clientStats) {
		// filter list, left the latest record.
		Map<String, AhClientStats> resultMap = new HashMap<>();

		for (AhClientStats statsItem : clientStats) {
			AhClientStats cacheItem = resultMap.get(statsItem.getClientMac());
			if (cacheItem == null) {
				resultMap.put(statsItem.getClientMac(), statsItem);
			} else {
				if (cacheItem.getTimeStamp() < statsItem.getTimeStamp()) {
					// replace with the newer item
					resultMap.put(statsItem.getClientMac(), statsItem);
				}
			}
		}

		StringBuilder strBuf = new StringBuilder();
		for (AhClientStats stats : resultMap.values()) {
			strBuf.append("update ah_clientsession set slaConnectScore=");
			strBuf.append(stats.getSlaConnectScore());
			strBuf.append(",ipNetworkConnectivityScore=").append(
					stats.getIpNetworkConnectivityScore());
			strBuf.append(",applicationHealthScore=").append(
					stats.getApplicationHealthScore());
			strBuf.append(",overallClientHealthScore=").append(
					stats.getOverallClientHealthScore());
			strBuf.append(" where clientMac='");
			strBuf.append(stats.getClientMac());
			strBuf.append("' and connectstate=").append(
					AhClientSession.CONNECT_STATE_UP).append(";");
		}
		return strBuf.toString();
	}

	class InterfaceClientStatsEventProcessorThread extends Thread {
		@Override
		public void run() {
			MgrUtil.setTimerName(getClass().getSimpleName());
			BeLogTools
					.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
							"<BE Thread> Interface client stats processor - event processor is running...");

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
							if (resultEvent.getResultType() == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_INTERFACECLIENT) {
								handleInterfaceClientResultEvent((BeInterfaceClientResultEvent) resultEvent);
								pollingTotalAps ++;
							}
						} else if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_TRAPEVENT) {
							BeTrapEvent trapEvent = (BeTrapEvent) communicationEvent;
							if (trapEvent.getTrapType() == BeTrapEvent.TYPE_INTERFACECLIENTTRAP) {
								handleInterfaceClientTrap(trapEvent);
							}
						}
					}
					
					// log
					if(pollingBeginTime != 0) {
						long currentTime = System.currentTimeMillis();
						if ((currentTime - pollingPreviousAPtime) > 30000  && pollingTotalAps > 0) {
							BeLogTools.warn(HmLogConst.M_PERFORMANCE,"BeInterfaceClientStatsProcessor: Time to process interface client stats for "
											+ pollingTotalAps
											+ " APs is "
											+ (pollingPreviousAPtime - pollingBeginTime)
											/ 1000
											+ " seconds since "
											+ new Date(pollingBeginTime)
											+ ", average processing time per AP is "
											+ (pollingPreviousAPtime - pollingBeginTime)
											/ pollingTotalAps + "ms");
							pollingBeginTime = 0;
						} else {
							pollingPreviousAPtime = currentTime;
						}
					}
				} catch (Exception e) {
					BeLogTools.error(HmLogConst.M_PERFORMANCE,"BeInterfaceClientStatsProcessor.EventProcessorThread.run() Exception in processor thread",
									e);
				} catch (Error e) {
					BeLogTools.error(HmLogConst.M_PERFORMANCE,"BeInterfaceClientStatsProcessor.EventProcessorThread.run() Error in processor thread",
									e);
				}
			}

			BeLogTools
					.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
							"<BE Thread> Interface client stats processor - event processor is shutdown.");
		}
	}

}