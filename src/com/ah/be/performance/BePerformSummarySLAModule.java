package com.ah.be.performance;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ah.be.common.DBOperationUtil;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.performance.AhBandWidthSentinelHistory;
import com.ah.bo.performance.AhClientSessionHistory;
import com.ah.bo.performance.AhClientStats;
import com.ah.bo.performance.AhInterfaceStats;
import com.ah.bo.performance.SlaDataField;
import com.ah.util.LongItem;
import com.ah.util.MgrUtil;

public class BePerformSummarySLAModule implements Runnable {
	// this class is no used now
	
//	public static Map<Long, Boolean> domainData = new HashMap<Long, Boolean>();
	public static Map<Long, List<LongItem>> mapSlaApBad;
	public static Map<Long, List<LongItem>> mapSlaClientBad;
	private static final Boolean synchronizedFlg1 = Boolean.TRUE;


	private ScheduledExecutorService scheduler;

	private BePerformSummarySLAModule() {

	}



	public void start() {
		mapSlaApBad = new HashMap<Long, List<LongItem>>();
		mapSlaClientBad = new HashMap<Long, List<LongItem>>();

		if (scheduler == null || scheduler.isShutdown()) {
			scheduler = Executors.newSingleThreadScheduledExecutor();
			scheduler.scheduleWithFixedDelay(this, 3, 3L, TimeUnit.MINUTES);
		}
		new Thread() {
			@Override
			public void run() {
//				this.setName("bePerformSummarySLAModule");
//				bePerformSummarySLAModule.initValueWhenStart();
			}
		}.start();

		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
				"<BE Thread> Performance summary SLA module - scheduler is running...");
	}

	@Override
	public void run() {
		MgrUtil.setTimerName(this.getClass().getSimpleName());
		synchronized (synchronizedFlg1) {
			try {
				BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
						"Performance summary SLA module - scheduler start running..." 
						+ Runtime.getRuntime().freeMemory() + "----" +
						Runtime.getRuntime().totalMemory());
				List<HmDomain> listData = QueryUtil.executeQuery(HmDomain.class, null, null);
				Calendar calendar = Calendar.getInstance();
				calendar.clear(Calendar.SECOND);
				calendar.clear(Calendar.MILLISECOND);
				calendar.add(Calendar.MINUTE, -3);
				
				FilterParams myFilterParamsforAp =new FilterParams("manageStatus=:s1",
						new Object[]{HiveAp.STATUS_MANAGED});
				List<?> managedAps = QueryUtil.executeQuery("select macAddress,owner.id from " 
						+ HiveAp.class.getSimpleName(), null, myFilterParamsforAp);
				
				
//				StringBuffer cleintSql = new StringBuffer();
//				cleintSql.append("select distinct clientmac,owner from ah_clientsession_history where endtimestamp>")
//					.append(calendar.getTimeInMillis())
//					.append(" union ")
//					.append("select distinct clientmac,owner from ah_clientsession");
//				List<?> clientHistory = QueryUtil.executeNativeQuery(cleintSql.toString());
				
				List<Object> clientHistory = new ArrayList<Object>();
				
				Map<String,Object> mapClient = new HashMap<String,Object>();

				StringBuffer clientSql = new StringBuffer();
				clientSql.append("select distinct clientmac,owner from ah_clientsession_history where endtimestamp>")
					.append(calendar.getTimeInMillis());
				List<?> rsList = QueryUtil.executeNativeQuery(clientSql.toString());
				for(Object obj: rsList) {
					Object[] objs = (Object[])obj;
					String key = objs[0].toString();
					mapClient.put(key, obj);
				}
				clientSql = new StringBuffer();
				clientSql.append("select distinct clientmac,owner from ah_clientsession");
				rsList = DBOperationUtil.executeQuery(clientSql.toString());
				for(Object obj: rsList) {
					Object[] objs = (Object[])obj;
					String key = objs[0].toString();
					mapClient.put(key, obj);
				}
				clientHistory.addAll(mapClient.values());
				
				FilterParams filterSLAHistory = new FilterParams(
						"timeStamp.time>:s1 and (bandWidthSentinelStatus=:s2 or bandWidthSentinelStatus=:s3)",
						new Object[] { calendar.getTimeInMillis(), AhBandWidthSentinelHistory.STATUS_BAD
								,AhBandWidthSentinelHistory.STATUS_ALERT});
				List<?> slaHistory = QueryUtil.executeQuery(
						"select distinct apMac,clientMac,owner.id from " + AhBandWidthSentinelHistory.class.getSimpleName(),
						null, filterSLAHistory);
				
				FilterParams filterInterfaceHistory = new FilterParams("timeStamp>:s1 and alarmFlag>:s2",
						new Object[] { calendar.getTimeInMillis(),0 });
				List<?> interfaceStatsHistory =QueryUtil.executeQuery(
						"select distinct apMac,owner.id from "
						+ AhInterfaceStats.class.getSimpleName(), null,
						filterInterfaceHistory);
				
				List<?> clientStatsHistory =QueryUtil.executeQuery(
						"select distinct clientMac,owner.id from "
						+ AhClientStats.class.getSimpleName(), null,
						filterInterfaceHistory);
				
				calendar.add(Calendar.MINUTE, 3);
				
				for (HmDomain hmDomain : listData) {
					Calendar domainCalendar = Calendar.getInstance(hmDomain.getTimeZone());
					domainCalendar.setTimeInMillis(calendar.getTimeInMillis());
//					domainCalendar.setTimeZone(hmDomain.getTimeZone());
					// DebugUtil.performanceDebugWarn("Start getting 3 minutes
					// statistics at "
					// + domainCalendar.getTime());
					Long domainId;

					if ("global".equalsIgnoreCase(hmDomain.getDomainName())) {
						domainId = (long) -1;
					} else {
						domainId = hmDomain.getId();
					}

					List<LongItem> slaApBad = mapSlaApBad.get(domainId);
					List<LongItem> slaClientBad = mapSlaClientBad.get(domainId);
					if (slaApBad == null) {
						slaApBad = new ArrayList<LongItem>();
					}
					if (slaClientBad == null) {
						slaClientBad = new ArrayList<LongItem>();
					}

					long totalApCount = getTotalApCount(managedAps,domainId);
					long totalClientCount = getTotalClientCount(clientHistory,domainId);
					long badApCount = getBadApCountValue(slaHistory,interfaceStatsHistory,domainId);
					long badClientCount = getBadClientCountValue(slaHistory,clientStatsHistory,domainId);

					long longTime = domainCalendar.getTimeInMillis();
					if (slaApBad.size() > 479) {
						slaApBad.remove(0);
						slaClientBad.remove(0);
					}
					if (totalApCount != 0) {
						slaApBad.add(new LongItem(badApCount*100/totalApCount, longTime));
					} else {
						slaApBad.add(new LongItem(0, longTime));
					}

					if (totalClientCount != 0) {
						slaClientBad.add(new LongItem(badClientCount*100/totalClientCount, longTime));
					} else {
						slaClientBad.add(new LongItem(0, longTime));
					}

					mapSlaApBad.put(domainId, slaApBad);
					mapSlaClientBad.put(domainId, slaClientBad);
				}
				BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
						"Performance summary SLA module - scheduler end running..."
						+ Runtime.getRuntime().freeMemory() + "----" +
						Runtime.getRuntime().totalMemory());
			} catch (OutOfMemoryError outError) {
				BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
						"Performance summary SLA module scheduler running out of memory~~~!!!");
			} catch (Exception e) {
				BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
				"Performance summary SLA module scheduler running exception~~~!!!" + e.getMessage());
			}
		}
	}
	
	public long getTotalApCount(List<?> managedAps,Long domainId){
		if (domainId ==-1) {
			return managedAps.size();
		} else {
			long totalCount=0;
			for(Object oneObj:managedAps){
				Object[] oneItem = (Object[])oneObj;
				if (Long.parseLong(oneItem[1].toString())==domainId){
					totalCount++;
				}
			}
			return totalCount;
		}
	}
	
	public long getTotalClientCount(List<?> clientHistory,Long domainId){
		if (domainId ==-1) {
			return clientHistory.size();
		} else {
			long totalCount=0;
			for(Object oneObj:clientHistory){
				Object[] oneItem = (Object[])oneObj;
				if (Long.parseLong(oneItem[1].toString())==domainId){
					totalCount++;
				}
			}
			return totalCount;
		}
	}
	
	public long getBadApCountValue(List<?>slaHistory,List<?>interfaceStatsHistory,Long domainId){
		Set<String> setBadAP = new HashSet<String>();
		if (domainId ==-1) {
			for(Object oneObj:slaHistory){
				Object[] oneItem = (Object[])oneObj;
				setBadAP.add(oneItem[0].toString());
			}
			for(Object oneObj: interfaceStatsHistory){
				Object[] oneItem = (Object[]) oneObj;
				setBadAP.add(oneItem[0].toString());
			}
			return setBadAP.size();
		} else {
			for(Object oneObj: slaHistory){
				Object[] oneItem = (Object[]) oneObj;
				if (Long.parseLong(oneItem[2].toString())==domainId){
					setBadAP.add(oneItem[0].toString());
				}
			}
			
			for(Object oneObj: interfaceStatsHistory) {
				Object[] oneItem = (Object[]) oneObj;
				if (Long.parseLong(oneItem[1].toString())==domainId){
					setBadAP.add(oneItem[0].toString());
				}
			}
			return setBadAP.size();
		}
	}
	
	public long getBadClientCountValue(List<?> slaHistory, List<?> clientStatsHistory, Long domainId){
		Set<String> setBadClient = new HashSet<String>();
		if (domainId ==-1) {
			for(Object oneObj: slaHistory){
				Object[] oneItem = (Object[]) oneObj;
				setBadClient.add(oneItem[1].toString());
			}
			for(Object oneObj: clientStatsHistory){
				Object[] oneItem = (Object[]) oneObj;
				setBadClient.add(oneItem[0].toString());
			}
			return setBadClient.size();
		} else {
			for(Object oneObj: slaHistory) {
				Object[] oneItem = (Object[]) oneObj;
				if (Long.parseLong(oneItem[2].toString())==domainId) {
					setBadClient.add(oneItem[1].toString());
				}
			}
			for(Object oneObj: clientStatsHistory){
				Object[] oneItem = (Object[]) oneObj;
				if (Long.parseLong(oneItem[1].toString())==domainId){
					setBadClient.add(oneItem[0].toString());
				}
			}
			return setBadClient.size();
		}
	}

	public void initValueWhenStart() {
		synchronized (synchronizedFlg1) {
			try {
				BeLogTools
						.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
								"Performance summary SLA module initValueWhenStart - scheduler start running..."
								+ Runtime.getRuntime().freeMemory() + "----" +
								Runtime.getRuntime().totalMemory());
				List<HmDomain> listData = QueryUtil.executeQuery(HmDomain.class, null, null);
				Calendar calendar = Calendar.getInstance();
				calendar.clear(Calendar.SECOND);
				calendar.clear(Calendar.MILLISECOND);
				for (HmDomain hmDomain : listData) {
					Calendar domainCalendar = Calendar.getInstance(hmDomain.getTimeZone());
					domainCalendar.setTimeInMillis(calendar.getTimeInMillis());
					//domainCalendar.setTimeZone(hmDomain.getTimeZone());
					Long domainId;

					if ("global".equalsIgnoreCase(hmDomain.getDomainName())) {
						domainId = (long) -1;
					} else {
						domainId = hmDomain.getId();
					}

					List<LongItem> slaApBad = mapSlaApBad.get(domainId);
					List<LongItem> slaClientBad = mapSlaClientBad.get(domainId);

					if (slaApBad == null) {
						slaApBad = new ArrayList<LongItem>();
					}

					if (slaClientBad == null) {
						slaClientBad = new ArrayList<LongItem>();
					}
					try {
						// find management ap count
						FilterParams myFilterParamsforAp;
						if (domainId != null && domainId != -1) {
							myFilterParamsforAp = new FilterParams(
									"manageStatus = :s1 and owner.id = :s2", new Object[] {
											HiveAp.STATUS_MANAGED, domainId});
						} else {
							myFilterParamsforAp = new FilterParams("manageStatus = :s1",
									new Object[] { HiveAp.STATUS_MANAGED});
						}
						int managedAps = (int) QueryUtil.findRowCount(HiveAp.class,
								myFilterParamsforAp);

						domainCalendar.add(Calendar.HOUR_OF_DAY, -24);
						// search client session history
						FilterParams filterClientHistory = new FilterParams("startTimeStamp>:s1",
								new Object[] { domainCalendar.getTimeInMillis() });
						List<?> clientHistory = QueryUtil.executeQuery(
								"select clientMac,startTimeStamp, endTimeStamp from "
										+ AhClientSessionHistory.class.getSimpleName(), null,
								filterClientHistory, domainId == -1 ? null : domainId);

						FilterParams filterSLAHistory = new FilterParams("timeStamp.time>:s1",
								new Object[] { domainCalendar.getTimeInMillis() });
						List<AhBandWidthSentinelHistory> slaHistory = QueryUtil.executeQuery(
								AhBandWidthSentinelHistory.class, new SortParams("timeStamp.time"),
								filterSLAHistory, domainId == -1 ? null : domainId);

						FilterParams filterInterfaceHistory = new FilterParams("timeStamp>:s1 and alarmFlag>:s2",
								new Object[] { domainCalendar.getTimeInMillis(),0 });
						List<?> interfaceStatsHistory =QueryUtil.executeQuery(
								"select apMac,timeStamp from "
								+ AhInterfaceStats.class.getSimpleName(), null,
								filterInterfaceHistory, domainId == -1 ? null : domainId);
						
						List<?> clientStatsHistory =QueryUtil.executeQuery(
								"select clientMac,timeStamp from "
								+ AhClientStats.class.getSimpleName(), null,
								filterInterfaceHistory, domainId == -1 ? null : domainId);
						
						domainCalendar.add(Calendar.MINUTE, 3);

						Map<String, SlaDataField> setSlaHistory = new HashMap<String, SlaDataField>();
						for (AhBandWidthSentinelHistory tmpSlaHistory : slaHistory) {
							while (tmpSlaHistory.getTimeStamp().getTime() > domainCalendar
									.getTimeInMillis()) {
								
								setCurrentTimeValue(clientHistory,
										domainCalendar.getTimeInMillis(), setSlaHistory,
										slaApBad, slaClientBad, managedAps,interfaceStatsHistory,clientStatsHistory);
								
								domainCalendar.add(Calendar.MINUTE, 3);
							}
							if (setSlaHistory.get(tmpSlaHistory.getClientMac()) != null) {
								setSlaHistory.get(tmpSlaHistory.getClientMac()).setApMac(
										tmpSlaHistory.getApMac());
								setSlaHistory.get(tmpSlaHistory.getClientMac()).setClientMac(
										tmpSlaHistory.getClientMac());
								setSlaHistory.get(tmpSlaHistory.getClientMac()).setStatus(
										tmpSlaHistory.getBandWidthSentinelStatus());
								setSlaHistory.get(tmpSlaHistory.getClientMac()).setAction(
										tmpSlaHistory.getAction());
							} else {
								SlaDataField dataField = new SlaDataField();
								dataField.setApMac(tmpSlaHistory.getApMac());
								dataField.setClientMac(tmpSlaHistory.getClientMac());
								dataField.setStatus(tmpSlaHistory.getBandWidthSentinelStatus());
								dataField.setAction(tmpSlaHistory.getAction());
								setSlaHistory.put(tmpSlaHistory.getClientMac(), dataField);
							}
						}
						while (domainCalendar.getTimeInMillis() <= calendar.getTimeInMillis()) {
							setCurrentTimeValue(clientHistory,
									domainCalendar.getTimeInMillis(), setSlaHistory,
									slaApBad, slaClientBad, managedAps,interfaceStatsHistory,clientStatsHistory);
							domainCalendar.add(Calendar.MINUTE, 3);
						}
					} catch (Exception e) {
						BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
								"Performance summary SLA module initValueWhenStart Exception-"
										+ e.getMessage());
						long longTime = domainCalendar.getTimeInMillis();
						if (slaApBad.size() > 479) {
							slaApBad.remove(0);
							slaClientBad.remove(0);
						}
						slaApBad.add(new LongItem(0, longTime));
						slaClientBad.add(new LongItem(0, longTime));
					} catch (OutOfMemoryError ex) {
						BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
								"Performance summary SLA module initValueWhenStart OutOfMemoryError-"
										+ ex.getMessage());
						long longTime = domainCalendar.getTimeInMillis();
						if (slaApBad.size() > 479) {
							slaApBad.remove(0);
							slaClientBad.remove(0);
						}

						slaApBad.add(new LongItem(0, longTime));
						slaClientBad.add(new LongItem(0, longTime));
					}
					mapSlaApBad.put(domainId, slaApBad);
					mapSlaClientBad.put(domainId, slaClientBad);
				}
				BeLogTools
						.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
								"Performance summary SLA module initValueWhenStart - scheduler end running..."
								+ Runtime.getRuntime().freeMemory() + "----" +
								Runtime.getRuntime().totalMemory());
			} catch (OutOfMemoryError outError) {
				BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
						"Performance summary SLA module init data out of memory~~~!!!");
			} catch (Exception e) {
				BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
				"Performance summary SLA module scheduler running exception~~~!!!" + e.getMessage());
			}
		}
	}

	public void setCurrentTimeValue(List<?> clientHistory, long time,
			Map<String, SlaDataField> tmpSlaHistory,
			List<LongItem> slaApBad,
			List<LongItem> slaClientBad,
			int managedAps,
			List<?> interfaceStatsHistory,
			List<?> clientStatsHistory) {
		Set<String> currentClient = new HashSet<String>();
		for (Object clientObj : clientHistory) {
			Object[] boClientSession = (Object[]) clientObj;
			if (Long.parseLong(boClientSession[1].toString()) <= time && Long.parseLong(boClientSession[2].toString()) > time - 180000) {
				currentClient.add(boClientSession[0].toString());
			}
		}

		Set<String> currentSlaClient = new HashSet<String>();
		if (tmpSlaHistory.keySet() != null) {
			for (String currMac : tmpSlaHistory.keySet()) {
				if (!currentClient.contains(currMac)) {
					currentSlaClient.add(currMac);
				}
			}
		}
		for (String currMac : currentSlaClient) {
			tmpSlaHistory.remove(currMac);
		}
		Set<String> badAp = new HashSet<String>();
		Set<String> badClient = new HashSet<String>();
		if (tmpSlaHistory.size() > 0) {
			for (String strMac : tmpSlaHistory.keySet()) {
				if (tmpSlaHistory.get(strMac).getStatus() == AhBandWidthSentinelHistory.STATUS_BAD
						|| tmpSlaHistory.get(strMac).getStatus() == AhBandWidthSentinelHistory.STATUS_ALERT) {
					badClient.add(tmpSlaHistory.get(strMac).getClientMac());
					badAp.add(tmpSlaHistory.get(strMac).getApMac());
				}
			}
		}
		
		if (interfaceStatsHistory.size()>0) {
			for (Object interObj : interfaceStatsHistory) {
				Object[] oneInterface = (Object[]) interObj;
				if (Long.parseLong(oneInterface[1].toString())<=time && Long.parseLong(oneInterface[1].toString())> (time - 180000)){
					badAp.add(oneInterface[0].toString());
				}
			}
		}
		
		if (clientStatsHistory.size()>0) {
			for (Object interObj : clientStatsHistory) {
				Object[] oneClient = (Object[]) interObj;
				if (Long.parseLong(oneClient[1].toString())<=time && Long.parseLong(oneClient[1].toString())> (time - 180000)){
					badClient.add(oneClient[0].toString());
				}
			}
		}

		if (managedAps != 0) {
			slaApBad.add(new LongItem(badAp.size()*100/managedAps, time));
		} else {
			slaApBad.add(new LongItem(0, time));
		}

		int totalClientCount = currentClient.size();
		if (totalClientCount != 0) {
			slaClientBad.add(new LongItem(badClient.size()*100/totalClientCount,time));
		} else {
			slaClientBad.add(new LongItem(0, time));
		}
	}

	public void shutdownScheduler() throws InterruptedException {
		if (scheduler == null || scheduler.isShutdown()) {
			return;
		}

		// Disable new tasks from being submitted.
		scheduler.shutdown();

		// Wait a while for existing tasks to terminate.
		if (scheduler.awaitTermination(1L, TimeUnit.SECONDS)) {
			return;
		}

		// Cancel currently executing tasks.
		scheduler.shutdownNow();

		// Wait a while for tasks to respond to being cancelled.
		if (!scheduler.awaitTermination(1L, TimeUnit.SECONDS)) {
			BeLogTools
					.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
							"<BE Thread> Performance summary SLA module - task is not terminated completely");
		}
//		bePerformSummarySLAModule= null;
		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
				"<BE Thread> Performance summary SLA module - scheduler is shutdown");
	}

	public static List<LongItem> getMapSlaApBad(Long domainId) {
		return mapSlaApBad.get(domainId);
	}

	public static List<LongItem> getMapSlaClientBad(Long domainId) {
		return mapSlaClientBad.get(domainId);
	}

}