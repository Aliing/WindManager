package com.ah.be.fault;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ah.be.app.DebugUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.performance.BeServerCpuMemoryUsageProcessor;
import com.ah.be.performance.db.TablePartitionProcessor;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmL3FirewallLog;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.admin.LogSettings;
import com.ah.bo.dashboard.DashboardComponent;
import com.ah.bo.dashboard.DashboardComponentMetric;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.ClientDeviceInfo;
import com.ah.bo.monitor.CpuMemoryUsage;
import com.ah.bo.performance.APConnectHistoryInfo;
import com.ah.bo.performance.AhACSPNeighbor;
import com.ah.bo.performance.AhAdminLoginSession;
import com.ah.bo.performance.AhAppDataHour;
import com.ah.bo.performance.AhAppDataSeconds;
import com.ah.bo.performance.AhAssociation;
import com.ah.bo.performance.AhBandWidthSentinelHistory;
import com.ah.bo.performance.AhClientEditValues;
import com.ah.bo.performance.AhClientSessionHistory;
import com.ah.bo.performance.AhClientStats;
import com.ah.bo.performance.AhClientStatsDay;
import com.ah.bo.performance.AhClientStatsHour;
import com.ah.bo.performance.AhClientStatsWeek;
import com.ah.bo.performance.AhClientsOsInfoCount;
import com.ah.bo.performance.AhClientsOsInfoCountDay;
import com.ah.bo.performance.AhClientsOsInfoCountHour;
import com.ah.bo.performance.AhClientsOsInfoCountWeek;
import com.ah.bo.performance.AhDeviceStats;
import com.ah.bo.performance.AhInterfaceStats;
import com.ah.bo.performance.AhInterfaceStatsDay;
import com.ah.bo.performance.AhInterfaceStatsHour;
import com.ah.bo.performance.AhInterfaceStatsWeek;
import com.ah.bo.performance.AhInterferenceStats;
import com.ah.bo.performance.AhMaxClientsCount;
import com.ah.bo.performance.AhNeighbor;
import com.ah.bo.performance.AhNewSLAStats;
import com.ah.bo.performance.AhNewSLAStatsDay;
import com.ah.bo.performance.AhNewSLAStatsHour;
import com.ah.bo.performance.AhNewSLAStatsWeek;
import com.ah.bo.performance.AhPCIData;
import com.ah.bo.performance.AhRadioAttribute;
import com.ah.bo.performance.AhRadioStats;
import com.ah.bo.performance.AhSLAStats;
import com.ah.bo.performance.AhSsidClientsCount;
import com.ah.bo.performance.AhSsidClientsCountDay;
import com.ah.bo.performance.AhSsidClientsCountHour;
import com.ah.bo.performance.AhSsidClientsCountWeek;
import com.ah.bo.performance.AhSwitchPortPeriodStats;
import com.ah.bo.performance.AhUserLoginSession;
import com.ah.bo.performance.AhVIfStats;
import com.ah.bo.performance.AhXIf;
import com.ah.util.MgrUtil;

/**
 * 
 *@filename		BeDBRecordsClearProcessor.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2008-2-22 03:59:49
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 */
public class BeDBRecordsClearProcessor implements Runnable {

	private ScheduledExecutorService			scheduler;


	// // time unit is minutes
	private final int							interval	= 24 * 60;

	// time unit is minutes
	// private int interval = 5; // for test

	/**
	 * Construct method
	 */
	public BeDBRecordsClearProcessor() {

	}

	public void startTask() {
		// start refresh scheduler
		if (scheduler == null || scheduler.isShutdown()) {
			scheduler = Executors.newSingleThreadScheduledExecutor();

			// this task will execute at 2:20AM every day
			Calendar executeCal = Calendar.getInstance();
			if (executeCal.get(Calendar.HOUR_OF_DAY) > 2) {
				executeCal.add(Calendar.DATE, 1);
			}

			executeCal.set(Calendar.HOUR_OF_DAY, 2);
			executeCal.set(Calendar.MINUTE, 20);
			executeCal.set(Calendar.SECOND, 0);
			executeCal.set(Calendar.MILLISECOND, 0);

			long delay = (executeCal.getTimeInMillis() - System.currentTimeMillis()) / (60 * 1000);

			scheduler.scheduleAtFixedRate(this, Math.max(0, delay), interval, TimeUnit.MINUTES);
		}
	}

	public void run() {
		MgrUtil.setTimerName(this.getClass().getSimpleName());
		try {
			DebugUtil
					.faultDebugInfo("BeDBRecordsClearProcessor.run(): Start remove redundant DB records.");

			// 1. get max performance records number and histroy client records
			int maxPerformanceRecords = 50000;
			int maxHistoryClientRecords = 2000000;
			// System/Audit/l3Firewall log expiration days
			int syslogExpirationDays = LogSettings.DEFAULT_SYSLOG_EXPIRATIONDAYS;
			int auditlogExpirationDays = LogSettings.DEFAULT_AUDITLOG_EXPIRATIONDAYS;
			int l3FirewallLogExpirationDays = LogSettings.DEFAULT_L3FIREWALLLOG_EXPIRATIONDAYS;
			
			int maxOriginalCount=24;
			int maxHourValue=7;
			int maxDayValue=4;
			int maxWeekValue=12;
			
			List<LogSettings> list = QueryUtil.executeQuery(LogSettings.class, null, null);
			if (list.isEmpty()) {
				DebugUtil
						.faultDebugWarn("BeDBRecordsClearProcessor.startTask(): Error find no log settings in db!");
			} else {
				maxPerformanceRecords = list.get(0).getMaxPerfRecord();
				maxHistoryClientRecords = list.get(0).getMaxHistoryClientRecord();
				// System/Audit log expiration days
				syslogExpirationDays = list.get(0).getSyslogExpirationDays();
				auditlogExpirationDays = list.get(0).getAuditlogExpirationDays();
				l3FirewallLogExpirationDays = list.get(0).getL3FirewallLogExpirationDays();
				
				maxOriginalCount = list.get(0).getMaxOriginalCount();
				maxHourValue = list.get(0).getMaxHourValue();
				maxDayValue = list.get(0).getMaxDayValue();
				maxWeekValue = list.get(0).getMaxWeekValue();
			}

//			DebugUtil.faultDebugInfo("Max performance records is " + maxPerformanceRecords);
//			DebugUtil.faultDebugInfo("Max histroy client records is " + maxHistoryClientRecords);
			// System/Audit log expiration days
			DebugUtil.faultDebugInfo("System log expiration days is " + syslogExpirationDays);
			DebugUtil.faultDebugInfo("Audit log expiration days is " + auditlogExpirationDays);
			DebugUtil.faultDebugInfo("L3Firewall log expiration days is " + l3FirewallLogExpirationDays);

			// 2. clear redundant records
			if(!TablePartitionProcessor.isEnableTablePartition()) {
				removeRedundantRecordsById(AhClientSessionHistory.class, maxHistoryClientRecords);
				// performace max is not enough for association data, so we use history clients max value here.
				removeRedundantRecordsById(AhAssociation.class, maxHistoryClientRecords);
				removeRedundantRecordsById(AhAppDataSeconds.class, maxHistoryClientRecords);
				removeRedundantRecordsById(AhAppDataHour.class, maxHistoryClientRecords);
				
				removeRedundantRecordsById(AhNeighbor.class, maxPerformanceRecords);
				removeRedundantRecordsById(AhRadioStats.class, maxPerformanceRecords);
				removeRedundantRecordsById(AhVIfStats.class, maxPerformanceRecords);
				removeRedundantRecordsById(AhXIf.class, maxPerformanceRecords);
				removeRedundantRecordsById(AhRadioAttribute.class, maxPerformanceRecords);
				removeRedundantRecordsById(AhBandWidthSentinelHistory.class, maxPerformanceRecords);
				removeRedundantRecordsById(AhPCIData.class, maxPerformanceRecords);
				removeRedundantRecordsById(AhAdminLoginSession.class, maxPerformanceRecords);
				removeRedundantRecordsById(AhUserLoginSession.class, maxPerformanceRecords);
				removeRedundantRecordsById(AhInterferenceStats.class, maxPerformanceRecords);
				removeRedundantRecordsById(AhACSPNeighbor.class, maxPerformanceRecords);
				removeRedundantRecordsById(AhInterfaceStats.class, maxPerformanceRecords);
				removeRedundantRecordsById(AhClientStats.class, maxHistoryClientRecords);
				removeRedundantRecordsById(AhMaxClientsCount.class, maxPerformanceRecords);
				removeRedundantRecordsById(AhDeviceStats.class, maxPerformanceRecords);
				removeRedundantRecordsById(APConnectHistoryInfo.class, maxPerformanceRecords);
				removeRedundantRecordsById(AhSwitchPortPeriodStats.class, maxPerformanceRecords);
			}
			
			

			QueryUtil.bulkRemoveBos(AhInterfaceStatsHour.class, new FilterParams("timeStamp < :s1",
					new Object[] { System.currentTimeMillis() - maxHourValue * 24 * 60 * 60 * 1000L + 3600000 *3 }));
			QueryUtil.bulkRemoveBos(AhInterfaceStatsDay.class, new FilterParams("timeStamp < :s1",
					new Object[] { System.currentTimeMillis() - maxDayValue * 7 * 24 * 60 * 60 * 1000L + 3600000 *3 }));
			QueryUtil.bulkRemoveBos(AhInterfaceStatsWeek.class, new FilterParams("timeStamp < :s1",
					new Object[] { System.currentTimeMillis() - maxWeekValue * 30 * 24* 60 * 60 * 1000L + 3600000 *3 }));
			
			QueryUtil.bulkRemoveBos(AhClientStatsHour.class, new FilterParams("timeStamp < :s1",
					new Object[] { System.currentTimeMillis() - maxHourValue * 24 * 60 * 60 * 1000L + 3600000 *3 }));
			QueryUtil.bulkRemoveBos(AhClientStatsDay.class, new FilterParams("timeStamp < :s1",
					new Object[] { System.currentTimeMillis() - maxDayValue * 7 * 24 * 60 * 60 * 1000L + 3600000 *3 }));
			QueryUtil.bulkRemoveBos(AhClientStatsWeek.class, new FilterParams("timeStamp < :s1",
					new Object[] { System.currentTimeMillis() - maxWeekValue * 30 * 24 * 60 * 60 * 1000L + 3600000 *3 }));
			
			QueryUtil.bulkRemoveBos(AhSsidClientsCount.class, new FilterParams("timeStamp < :s1",
					new Object[] { System.currentTimeMillis() - maxOriginalCount * 60 * 60 * 1000L + 3600000 *3 }));
			QueryUtil.bulkRemoveBos(AhSsidClientsCountHour.class, new FilterParams("timeStamp < :s1",
					new Object[] { System.currentTimeMillis() - maxHourValue * 24 * 60 * 60 * 1000L + 3600000 *3 }));
			QueryUtil.bulkRemoveBos(AhSsidClientsCountDay.class, new FilterParams("timeStamp < :s1",
					new Object[] { System.currentTimeMillis() - maxDayValue * 7 * 24 * 60 * 60 * 1000L + 3600000 *3 }));
			QueryUtil.bulkRemoveBos(AhSsidClientsCountWeek.class, new FilterParams("timeStamp < :s1",
					new Object[] { System.currentTimeMillis() - maxWeekValue * 30 * 24 * 60 * 60 * 1000L + 3600000 *3 }));
			
			QueryUtil.bulkRemoveBos(AhClientsOsInfoCount.class, new FilterParams("timeStamp < :s1",
					new Object[] { System.currentTimeMillis() - maxOriginalCount * 60 * 60 * 1000L + 3600000 *3 }));
			QueryUtil.bulkRemoveBos(AhClientsOsInfoCountHour.class, new FilterParams("timeStamp < :s1",
					new Object[] { System.currentTimeMillis() - maxHourValue * 24 * 60 * 60 * 1000L + 3600000 *3 }));
			QueryUtil.bulkRemoveBos(AhClientsOsInfoCountDay.class, new FilterParams("timeStamp < :s1",
					new Object[] { System.currentTimeMillis() - maxDayValue * 7 * 24 * 60 * 60 * 1000L + 3600000 *3 }));
			QueryUtil.bulkRemoveBos(AhClientsOsInfoCountWeek.class, new FilterParams("timeStamp < :s1",
					new Object[] { System.currentTimeMillis() - maxWeekValue * 30 * 24 * 60 * 60 * 1000L + 3600000 *3 }));
			
			QueryUtil.bulkRemoveBos(AhNewSLAStats.class, new FilterParams("timeStamp < :s1",
					new Object[] { System.currentTimeMillis() - maxOriginalCount * 60 * 60 * 1000L + 3600000 *3 }));
			QueryUtil.bulkRemoveBos(AhNewSLAStatsHour.class, new FilterParams("timeStamp < :s1",
					new Object[] { System.currentTimeMillis() - maxHourValue * 24 * 60 * 60 * 1000L + 3600000 *3 }));
			QueryUtil.bulkRemoveBos(AhNewSLAStatsDay.class, new FilterParams("timeStamp < :s1",
					new Object[] { System.currentTimeMillis() - maxDayValue * 7 * 24 * 60 * 60 * 1000L + 3600000 *3 }));
			QueryUtil.bulkRemoveBos(AhNewSLAStatsWeek.class, new FilterParams("timeStamp < :s1",
					new Object[] { System.currentTimeMillis() - maxWeekValue * 30 * 24 * 60 * 60 * 1000L + 3600000 *3 }));
			
			
			int deleteCount = QueryUtil.bulkRemoveBos(AhSLAStats.class, new FilterParams("timeStamp < :s1",
					new Object[] { System.currentTimeMillis() - 24 * 60 * 60 * 1000L }));
			DebugUtil.faultDebugInfo("BeDBRecordsClearProcessor.run(): Success remove redundant "
					+ AhSLAStats.class.getSimpleName() + " records number: " + deleteCount);
						
			// System/Audit/L3Firewall log expiration days
			QueryUtil.bulkRemoveBos(HmSystemLog.class, new FilterParams("logTimeStamp < :s1",
					new Object[] { System.currentTimeMillis() - 24L * 60 * 60 * 1000 * syslogExpirationDays }));
			QueryUtil.bulkRemoveBos(HmAuditLog.class, new FilterParams("logTimeStamp < :s1",
					new Object[] { System.currentTimeMillis() - 24L * 60 * 60 * 1000 * auditlogExpirationDays }));
			QueryUtil.bulkRemoveBos(HmL3FirewallLog.class, new FilterParams("operationTimeStamp < :s1",
					new Object[] { System.currentTimeMillis() - 24L * 60 * 60 * 1000 * l3FirewallLogExpirationDays }));
			
			// remove cpu memory data
			QueryUtil.removeBos(CpuMemoryUsage.class, new FilterParams("timestamp < :s1",
								new Object[]{BeServerCpuMemoryUsageProcessor.getCurrentDate()}));
			
			//remove client edit value by expiration time
			QueryUtil.bulkRemoveBos(AhClientEditValues.class,new FilterParams("type = :s1 and expirationtime != 0 and expirationtime < :s2",
					new Object[] {AhClientEditValues.TYPE_SELF_REGISTER,System.currentTimeMillis()}));
			CacheMgmt.getInstance().initClientEditValuesCache();
			
			// remove unused DashboardCOmponentData and DashboardComponentMetric
			String sql="select distinct id from hm_dashboard_component a where (componentname is null or componentname='') " +
					" and defaultflag=false and createtime< " + (System.currentTimeMillis() - 6L * 60 * 60 * 1000L) + 
					" and not exists (select distinct widget_config_id from HM_DASHBOARD_WIDGET where a.id =widget_config_id) ";

			List<?> removeComponentIds = QueryUtil.executeNativeQuery(sql);
			if(!removeComponentIds.isEmpty()) {
				List<Long> rmIds = new ArrayList<Long>();
				for(Object obj: removeComponentIds){
					rmIds.add(Long.parseLong(obj.toString()));
				}
				QueryUtil.bulkRemoveBos(DashboardComponent.class, new FilterParams("id", rmIds));
			}
			
			sql="select distinct id from DASHBOARD_COMPONENT_METRIC a where (metricName is null or metricName='') " +
				" and defaultflag=false and createtime< " + (System.currentTimeMillis() - 6L * 60 * 60 * 1000L) + 
				" and not exists (select distinct METRIC_ID from hm_dashboard_component where METRIC_ID is not null and a.id=METRIC_ID)";
			removeComponentIds.clear();
			removeComponentIds = QueryUtil.executeNativeQuery(sql);
			if(!removeComponentIds.isEmpty()) {
				List<Long> rmIds = new ArrayList<Long>();
				for(Object obj: removeComponentIds){
					rmIds.add(Long.parseLong(obj.toString()));
				}
				QueryUtil.bulkRemoveBos(DashboardComponentMetric.class, new FilterParams("id", rmIds));
			}

			QueryUtil.bulkRemoveBos(ClientDeviceInfo.class, new FilterParams("update_at < :s1",
					new Object[] { System.currentTimeMillis() - 366 * 24 * 60 * 60 * 1000L}));
			
			/**
			 * clean report backend row-up history data
			 */
			cleanReportRowupDataWithTimestamp(
				System.currentTimeMillis() - maxHourValue * 24 * 60 * 60 * 1000L + 3600000 *3,
				System.currentTimeMillis() - maxDayValue * 7 * 24 * 60 * 60 * 1000L + 3600000 *3,
				System.currentTimeMillis() - maxWeekValue * 30 * 24 * 60 * 60 * 1000L + 3600000 *3,
				System.currentTimeMillis() - 2 * 365 * 24 * 60 * 60 * 1000L + 3600000 *3
				);
			
		} catch (Exception e) {
			DebugUtil.faultDebugError("BeDBRecordsClearProcessor.run() catch exception", e);
		} catch (Error e) {
			DebugUtil.faultDebugError("BeDBRecordsClearProcessor.run() catch error.", e);
		}
	}

	
	private void removeRedundantRecordsById(Class<? extends HmBo> boClass, int maxNumber) {
		try {
			String query = "select max(id) from " + boClass.getSimpleName();
			List<?> list = QueryUtil.executeQuery(query, 1);
			if (list.isEmpty()) {
				DebugUtil.faultDebugInfo(boClass.getSimpleName() + " records number is normal.");

				return;
			}

			Long minId = (Long)list.get(0);
			int deleteCount = 0;
			if(null != minId) {
				minId = minId - maxNumber;
				Object[] objs = new Object[] { minId };	
				deleteCount = QueryUtil.bulkRemoveBos(boClass, new FilterParams("id"
						+ " <= :s1", objs), null, null);
			}

			DebugUtil.faultDebugWarn("BeDBRecordsClearProcessor.run(): Success remove redundant "
					+ boClass.getSimpleName() + " records number: " + deleteCount);

		} catch (Exception e) {
			DebugUtil.faultDebugError("BeDBRecordsClearProcessor.run() clear "
					+ boClass.getSimpleName() + " redundant records catch exception.", e);
		}
	}


	/**
	 * new function for remove redundant records
	 * 
	 * @param boClass -
	 * @param maxNumber -
	 * @param sortField -
	 */
//	private void removeRedundantRecordsNew(Class<? extends HmBo> boClass, int maxNumber,
//			String sortField) {
//		try {
//			String query = "select " + sortField + " from " + boClass.getSimpleName()
//					+ " order by " + sortField + " desc";
//			List<?> list = QueryUtil.executeQuery(query, 1, maxNumber);
//			if (list.isEmpty()) {
//				DebugUtil.faultDebugInfo(boClass.getSimpleName() + " records number is normal.");
//
//				return;
//			}
//
//			Object[] objs = new Object[] { list.get(0) };
//
//			int deleteCount = QueryUtil.bulkRemoveBos(boClass, new FilterParams(sortField
//					+ " <= :s1", objs), null, null);
//
//			DebugUtil.faultDebugInfo("BeDBRecordsClearProcessor.run(): Success remove redundant "
//					+ boClass.getSimpleName() + " records number: " + deleteCount);
//
//		} catch (Exception e) {
//			DebugUtil.faultDebugError("BeDBRecordsClearProcessor.run() clear "
//					+ boClass.getSimpleName() + " redundant records catch exception.", e);
//		}
//	}
	
	public boolean shutdown() {
		if (!scheduler.isShutdown()) {
			// Disable new tasks from being submitted.
			scheduler.shutdown();
		}

		return true;
	}
	
	
	/**
	 * clean report back end row-up data with timestamp
	 * 
	 * @param domainId
	 * @author zdu
	 * @maito zdu@aerohive.com
	 */
    private static void cleanReportRowupDataWithTimestamp( Long keepHour, Long keepDate, Long keepWeek, Long keepMonth ) {
	try {
	    Class< ? > clazz = Class
		    .forName( "com.ah.nms.worker.report.rowup.migration.ReportRowupDataClean" );
	    Method method = clazz.getDeclaredMethod( "cleanAllForTimestamp",
		    new Class[ ] { Long.class, Long.class, Long.class, Long.class } );

	    method.invoke( null, new Object[ ] { keepHour, keepDate, keepWeek, keepMonth } );
	} catch ( Exception e ) {
	    DebugUtil.faultDebugError("BeDBRecordsClearProcessor.cleanReportRowupDataWithTimestamp() catch exception", e);
	}
    }

}