package com.ah.be.performance;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.common.ConfigUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeGetStatisticEvent;
import com.ah.be.communication.event.BeInterferenceMapEvent;
import com.ah.be.communication.event.BeStatisticResultEvent;
import com.ah.be.communication.event.BeTrapEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.performance.db.BulkOperationProcessor;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.admin.LogSettings;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.performance.AhAssociation;
import com.ah.bo.performance.AhLatestNeighbor;
import com.ah.bo.performance.AhLatestRadioAttribute;
import com.ah.bo.performance.AhLatestXif;
import com.ah.bo.performance.AhNeighbor;
import com.ah.bo.performance.AhRadioAttribute;
import com.ah.bo.performance.AhRadioStats;
import com.ah.bo.performance.AhVIfStats;
import com.ah.bo.performance.AhXIf;
import com.ah.util.MgrUtil;

/**
 * process statistics data request
 *@filename		BePerformStatisticsProcessor.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-11-15 10:45:37 AM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modify history*
 */
public class BePerformStatisticsProcessor implements Runnable {

	private byte CYCLE_APNUM = 10;

	private final short RELAXTIME = 1000;

	// // statsTime
	// private Date statsTime;

	/**
	 * keep track of statsSerialNum for refresh statistics
	 */
	private final Set<Integer> statsSerialNumSet = new HashSet<>();

	private ScheduledExecutorService scheduler;

	/**
	 * reference to module instance
	 */
	private BePerformModuleImpl module;

	/**
	 * empty list(String)
	 */
	private final List<String> EMPTY_STRING_LIST = new ArrayList<>();

	/**
	 * key: ap; value: counter
	 */
	private final ConcurrentMap<SimpleHiveAp, Integer> apMap4Interference = new ConcurrentHashMap<>();

	private final int COUNTER4INTERFERENCE = 10;

	private ScheduledExecutorService scheduler4Interference;

	// unit is second
	private final long PERIOD4INTERFERENCE = 600;
	
	// time unit is minutes
	private int statsStartMinute = 0;

	/**
	 * Construct method
	 */
	public BePerformStatisticsProcessor() {
		eventQueue = new LinkedBlockingQueue<>(eventQueueSize);
		try {
			CYCLE_APNUM = Byte.parseByte(ConfigUtil.getConfigInfo(
					ConfigUtil.SECTION_PERFORMANCE,
					ConfigUtil.KEY_POLLING_DEVICE_NUMBER_PER_SEC, "10"));
		}catch (Exception e) {
		}
	}

	public void start() {
		List<LogSettings> list = QueryUtil.executeQuery(LogSettings.class,
				null, null);
		if (!list.isEmpty()) {
			statsStartMinute = list.get(0).getStatsStartMinute();
		}
		
		// schedule at whole hour
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY) + 1);
		c.set(Calendar.MINUTE, statsStartMinute);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		if (scheduler == null || scheduler.isShutdown()) {
			scheduler = Executors.newSingleThreadScheduledExecutor();
			 scheduler.scheduleAtFixedRate(this, ((Math.abs(c.getTimeInMillis()
					- System.currentTimeMillis())) / 1000) + 1, 3600,
					TimeUnit.SECONDS);
		}

		// scheduler4Interference every 5 minutes
		if (scheduler4Interference == null
				|| scheduler4Interference.isShutdown()) {
			scheduler4Interference = Executors
					.newSingleThreadScheduledExecutor();
			scheduler4Interference.scheduleWithFixedDelay(runBody4Interference,
					PERIOD4INTERFERENCE, PERIOD4INTERFERENCE, TimeUnit.SECONDS);
		}

		isContinue = true;
		processEventThread = new StatsEventProcessorThread();
		processEventThread.setName("processInterfaceClientStatsThread");
		processEventThread.start();

		BeLogTools
				.info(
						HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
						"<BE Thread> Performance statistics processor(task scheduler and scheduler for interference) is running...");
	}
	
	public void updateStatsStartMinute(int startMinute) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY) + 1);
		c.set(Calendar.MINUTE, startMinute);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		if (!scheduler.isShutdown()) {
			// Disable new tasks from being submitted.
			scheduler.shutdown();
		}
		
		if (scheduler.isShutdown()) {
			scheduler = Executors.newSingleThreadScheduledExecutor();
			scheduler.scheduleAtFixedRate(this, ((Math.abs(c.getTimeInMillis()
					- System.currentTimeMillis())) / 1000) + 1, 3600,
					TimeUnit.SECONDS);
		}
	}

	/**
	 * run code for collect interference statistics data
	 */
	final Runnable runBody4Interference = new Runnable() {
		@Override
		public void run() {
			MgrUtil.setTimerName(getClass().getSimpleName());

			try {
				DebugUtil
						.performanceDebugInfo("Start getting Interference statistics(Short Interval) at "
								+ (new Date()));

				if (apMap4Interference.isEmpty()) {
					DebugUtil
							.performanceDebugInfo("No AP need collect interference statistics more frequently.");
					return;
				}

				int index_forInterference = 0;
				for (Iterator<SimpleHiveAp> iter = apMap4Interference.keySet()
						.iterator(); iter.hasNext();) {
					SimpleHiveAp ap = iter.next();
					Integer counter = apMap4Interference.get(ap);
					int newValue = counter - 1;
					if (newValue == 0) {
						iter.remove();
					} else {
						apMap4Interference.put(ap, newValue);
					}

					sendInterferenceRequest(ap);

					if (++index_forInterference == CYCLE_APNUM) {
						try {
							Thread.sleep(RELAXTIME);
						} catch (Exception e) {
							DebugUtil.performanceDebugError("catch error", e);
						}

						index_forInterference = 0;
					}
				}
			} catch (Exception e) {
				DebugUtil
						.performanceDebugError(
								"Send request for collect interference statistics catch exception.",
								e);
			} catch (Error e) {
				DebugUtil
						.performanceDebugError(
								"Send request for collect interference statistics catch error.",
								e);
			}
		}
	};

	@Override
	public void run() {
		MgrUtil.setTimerName(getClass().getSimpleName());

		try {
			pollingBeginTime = System.currentTimeMillis();
			pollingPreviousAPtime = pollingBeginTime;
			pollingTotalAps = 0;

			DebugUtil.performanceDebugInfo("Start getting statistics at "
					+ (new Date()));

			List<SimpleHiveAp> apList = CacheMgmt.getInstance()
					.getManagedApList();
			if (apList.isEmpty()) {
				return;
			}

			index = 0;

			Map<Byte, List<String>> statsTableInfo = new HashMap<>(
					6);
			statsTableInfo.put(BeCommunicationConstant.STATTABLE_AHASSOCIATION,
					EMPTY_STRING_LIST);
			statsTableInfo.put(BeCommunicationConstant.STATTABLE_AHXIF,
					EMPTY_STRING_LIST);
			statsTableInfo.put(BeCommunicationConstant.STATTABLE_AHNEIGHBOR,
					EMPTY_STRING_LIST);
			statsTableInfo.put(BeCommunicationConstant.STATTABLE_AHRADIOSTATES,
					EMPTY_STRING_LIST);
			statsTableInfo.put(BeCommunicationConstant.STATTABLE_AHVIFSTATS,
					EMPTY_STRING_LIST);
			statsTableInfo.put(
					BeCommunicationConstant.STATTABLE_AHRADIOATTRIBUTE,
					EMPTY_STRING_LIST);

			for (SimpleHiveAp ap : apList) {
				sendStatsRequest(ap, statsTableInfo);

				if (HiveAp.is11nHiveAP(ap.getHiveApModel())
						&& NmsUtil.compareSoftwareVersion("3.4.0.0", ap
								.getSoftVer()) <= 0) {
					sendInterferenceRequest(ap);
				}
			}
		} catch (Exception e) {
			DebugUtil
					.performanceDebugWarn(
							"BePerformStatisticsProcessor.run() catch exception, Exception: ",
							e);
			systemLog(
					HmSystemLog.LEVEL_MAJOR,
					"Failed to send request for getting statistics data. Maybe build request packet error.");
		} catch (Error e) {
			DebugUtil.performanceDebugError(
					"BePerformStatisticsProcessor.run() catch error ", e);
			systemLog(HmSystemLog.LEVEL_CRITICAL, "VM internal error, error: "
					+ e.getMessage());
		}
	}

	/**
	 * keep track of continuous request number
	 */
	private int index = 0;

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
	private void sendStatsRequest(SimpleHiveAp ap,
			Map<Byte, List<String>> statsTableMap)
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
			systemLog(
					HmSystemLog.LEVEL_MINOR,
					"Connection to capwap process closed! Failed to send request for getting statistics data.");
			return;
		}

		// debug log
		// DebugUtil.performanceDebugError(
		// "Send request for stats fo AP["+ap.getMacAddress()+"]");

		statsSerialNumSet.add(statsSerialNum);
		module.addRequestObj(serialNum, req);
		if (++index == CYCLE_APNUM) {
			try {
				Thread.sleep(RELAXTIME);
			} catch (Exception e) {
				DebugUtil
						.performanceDebugWarn(
								"BePerformStatisticsProcessor.sendStatsRequest() thread sleep error. Exception: ",
								e);
				systemLog(HmSystemLog.LEVEL_MINOR,
						"Thread for getting statistics data meet some error.");
			}

			index = 0;
		}
	}

	/**
	 * send request for interference statistics data
	 * 
	 * @param ap
	 *            -
	 * @throws BeCommunicationEncodeException
	 *             -
	 */
	private void sendInterferenceRequest(SimpleHiveAp ap)
			throws BeCommunicationEncodeException {
		BeInterferenceMapEvent interferenceEvent = new BeInterferenceMapEvent();
		interferenceEvent.setSimpleHiveAp(ap);
		interferenceEvent.setSequenceNum(HmBeCommunicationUtil
				.getSequenceNumber());
		interferenceEvent.setRequestAll(true);

		interferenceEvent.buildPacket();

		int serialNum = HmBeCommunicationUtil.sendRequest(interferenceEvent);
		if (serialNum == BeCommunicationConstant.SERIALNUM_SENDREQUESTFAILED) {
			// connect closed
			systemLog(
					HmSystemLog.LEVEL_MINOR,
					"Connection to capwap process closed! Failed to send request for getting statistics data.");
		}
	}

	/**
	 * poll ap more frequently (5 minutes interval)
	 * 
	 * @param apMac
	 *            -
	 */
	private void pollAP4Interference(String apMac) {
		SimpleHiveAp ap = CacheMgmt.getInstance().getSimpleHiveAp(apMac);
		if ((HiveAp.is11nHiveAP(ap.getHiveApModel()) || HiveAp.is20HiveAP(ap
				.getHiveApModel()))
				&& (NmsUtil.compareSoftwareVersion("3.4.0.0", ap.getSoftVer()) <= 0)) {
			try {
				// request new data at first
				sendInterferenceRequest(ap);
			} catch (Exception e) {
				DebugUtil
						.performanceDebugError(
								"pollAP4Interference(): Send request for interference statistics catch exception.",
								e);
			}

			apMap4Interference.put(ap, COUNTER4INTERFERENCE);
		}
	}

	/**
	 * not poll ap 5 minutes interval
	 * 
	 * @param apMac
	 *            -
	 */
	private void removeAP4Interference(String apMac) {
		SimpleHiveAp ap = CacheMgmt.getInstance().getSimpleHiveAp(apMac);
		apMap4Interference.remove(ap);
	}

	/**
	 * check statsSerial number validate
	 * 
	 * @param statsSerialNum
	 *            -
	 * @return -
	 */
	public boolean is4StatisticsRefresh(int statsSerialNum) {
		return statsSerialNumSet.contains(statsSerialNum);
	}

	/**
	 * remove given number from list
	 * 
	 * @param statsSerialNum
	 *            -
	 */
	public void removeStatsSerialNum(int statsSerialNum) {
		statsSerialNumSet.remove(statsSerialNum);
	}

	/**
	 * add given number to list
	 * 
	 * @param statsSerialNum
	 *            -
	 */
	public void addStatsSerialNum(int statsSerialNum) {
		statsSerialNumSet.add(statsSerialNum);
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

	/**
	 * cancel time task
	 * 
	 * @return -
	 */
	public boolean shutdown() {
		if (!scheduler.isShutdown()) {
			// Disable new tasks from being submitted.
			scheduler.shutdown();
		}

		if (!scheduler4Interference.isShutdown()) {
			// Disable new tasks from being submitted.
			scheduler4Interference.shutdown();
		}

		isContinue = false;
		eventQueue.clear();
		BeBaseEvent stopThreadEvent = new BeBaseEvent();
		eventQueue.offer(stopThreadEvent);
		BeLogTools
				.info(
						HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
						"<BE Thread> Performance statistics processor(task scheduler and scheduler for interference) is shutdown.");

		return true;
	}

	/**
	 * the following code is used for the process old stats data
	 */
	/**
	 * queue for event about client
	 */
	private final BlockingQueue<BeBaseEvent> eventQueue;

	private static final int eventQueueSize = 10000;

	private boolean isContinue = true;

	private int lostEventCount = 0;

	private StatsEventProcessorThread processEventThread;

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
					DebugUtil
							.performanceDebugError("BePerformStatisticsProcessor.addEvent(): Lost "
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
			DebugUtil
					.performanceDebugError(
							"BePerformStatisticsProcessor.getEvent(): Exception while get event from queue",
							e);
			return null;
		}
	}

	/**
	 * handle communication event of statistic result
	 * 
	 * @param communicationEvent
	 *            -
	 */
	private void handleCommunicationEventStatResult(
			BeCommunicationEvent communicationEvent) {
		BeStatisticResultEvent statEvent = (BeStatisticResultEvent) communicationEvent;

		int statsSerialNumber = statEvent.parseStatsSerialNum();

		// remove statistics number from cache
		removeStatsSerialNum(statsSerialNumber);

		// parse packet
		try {
			// set the get statistic time
			// statEvent.setStatTime(statsTime);
			statEvent.parsePacket();
		} catch (BeCommunicationDecodeException e) {
			DebugUtil
					.performanceDebugWarn(
							"BePerformStatisticsProcessor.handleCommunicationEventStatResult(): Catch decode exception when receive statistics result",
							e);
			systemLog(HmSystemLog.LEVEL_MAJOR,
					"Failed to parse received statistics result packet! AP Mac="
							+ statEvent.getApMac());

			return;
		}
		// update data into db
		Map<Byte, List<HmBo>> rowData = statEvent.getStatsRowData();
		if (rowData.isEmpty()) {
			// it maybe happen
			DebugUtil
					.performanceDebugInfo("BePerformStatisticsProcessor.handleCommunicationEventStatResult(): parse receive statistics result, but there are no data in.");
			return;
		}

		try {
			List<HmBo> dataList = new ArrayList<>();
			List<HmBo> assoList = new ArrayList<>();
			List<HmBo> neighborList = new ArrayList<>();
			List<HmBo> radioattrList = new ArrayList<>();
			List<HmBo> radiostatsList = new ArrayList<>();
			List<HmBo> vifstatsList = new ArrayList<>();
			List<HmBo> xifList = new ArrayList<>();
			for (Byte id : rowData.keySet()) {
				List<HmBo> rowList = rowData.get(id);
				switch (id) {
				case BeCommunicationConstant.STATTABLE_AHASSOCIATION:
					assoList.addAll(rowList);
					break;

				case BeCommunicationConstant.STATTABLE_AHNEIGHBOR:
					neighborList.addAll(rowList);
					break;
					
				case BeCommunicationConstant.STATTABLE_AHETHCLIENT:
					assoList.addAll(rowList);
					break;

				case BeCommunicationConstant.STATTABLE_AHRADIOATTRIBUTE:
					radioattrList.addAll(rowList);
					break;

				case BeCommunicationConstant.STATTABLE_AHRADIOSTATES:
					radiostatsList.addAll(rowList);
					break;

				case BeCommunicationConstant.STATTABLE_AHVIFSTATS:
					vifstatsList.addAll(rowList);
					break;

				case BeCommunicationConstant.STATTABLE_AHXIF:
					xifList.addAll(rowList);
					break;

				default:
//					dataList.addAll(rowList);
					break;
				}
			}
			
			//update latest data
			dataList = module.getLatestStatsData(rowData);
			Set<Byte> tableIDSet =rowData.keySet();
			if(tableIDSet.contains(BeCommunicationConstant.STATTABLE_AHRADIOATTRIBUTE))
				BulkOperationProcessor.addDeleteInsertBoList(AhLatestRadioAttribute.class, dataList, statEvent.getApMac());
			if(tableIDSet.contains(BeCommunicationConstant.STATTABLE_AHNEIGHBOR))
				BulkOperationProcessor.addDeleteInsertBoList(AhLatestNeighbor.class, dataList, statEvent.getApMac());
			if(tableIDSet.contains(BeCommunicationConstant.STATTABLE_AHXIF))
				BulkOperationProcessor.addDeleteInsertBoList(AhLatestXif.class, dataList, statEvent.getApMac());
			
			//insert history data
			BulkOperationProcessor.addBoList(AhAssociation.class, assoList);
			BulkOperationProcessor.addBoList(AhNeighbor.class, neighborList);
			BulkOperationProcessor.addBoList(AhRadioAttribute.class, radioattrList);
			BulkOperationProcessor.addBoList(AhRadioStats.class, radiostatsList);
			BulkOperationProcessor.addBoList(AhXIf.class, xifList);
			BulkOperationProcessor.addBoList(AhVIfStats.class, vifstatsList);
		} catch (Exception e) {
			DebugUtil
					.performanceDebugWarn(
							"BePerformStatisticsProcessor.handleCommunicationEventStatResult(): update statistic result to DB failed! ",
							e);
			systemLog(HmSystemLog.LEVEL_MAJOR,
					"Failed to save statistics data into database! AP Mac="
							+ statEvent.getApMac());
		}
	}

	class StatsEventProcessorThread extends Thread {
		@Override
		public void run() {
			MgrUtil.setTimerName(getClass().getSimpleName());
			BeLogTools
					.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
							"<BE Thread> Statistics data processor - event processor is running...");

			while (isContinue) {
				try {
					// take() method blocks
					BeBaseEvent event = getEvent();
					if (null == event)
						continue;

					if (event.getEventType() == BeEventConst.COMMUNICATIONEVENTTYPE) {
						BeCommunicationEvent communicationEvent = (BeCommunicationEvent) event;

						int msgType = communicationEvent.getMsgType();

						if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_STATISTICRESULT) {
							handleCommunicationEventStatResult(communicationEvent);
							pollingTotalAps++;
						}
						else if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_TRAPEVENT) {
							BeTrapEvent trapEvent = (BeTrapEvent) communicationEvent;
							if (trapEvent.getTrapType() == BeTrapEvent.TYPE_INTERFERENCEALERT) {
								if (trapEvent.isFailureFlag()) {
									// interference alert
									pollAP4Interference(trapEvent.getApMac());
								} else {
									// clear interference alert
									removeAP4Interference(trapEvent.getApMac());
								}
							}
						}
					}

					// log
					if(pollingBeginTime != 0) {
						long currentTime = System.currentTimeMillis();
						if ((currentTime - pollingPreviousAPtime) > 30000 && pollingTotalAps > 0) {
							DebugUtil
									.performanceDebugWarn("BePerformStatisticsProcessor: Time to process stats for "
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
					DebugUtil
							.performanceDebugWarn(
									"BePerformStatisticsProcessor.EventProcessorThread.run() Exception in processor thread",
									e);
				} catch (Error e) {
					DebugUtil
							.performanceDebugWarn(
									"BePerformStatisticsProcessor.EventProcessorThread.run() Error in processor thread",
									e);
				}
			}

			BeLogTools
					.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
							"<BE Thread> Statistics data stats processor - event processor is shutdown.");
		}
	}

	public int getStatsStartMinute() {
		return statsStartMinute;
	}

	public void setStatsStartMinute(int statsStartMinute) {
		this.statsStartMinute = statsStartMinute;
	}

}