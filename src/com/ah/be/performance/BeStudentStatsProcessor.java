package com.ah.be.performance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.common.ConfigUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.event.BeStudentReportEvent;
import com.ah.bo.admin.LogSettings;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.MgrUtil;

/**
 * 
 *@filename		BeStudentStatsProcessor.java
 *@version		V1.0.0.0
 *@author		
 *@createtime	2011-1-11 10:02:54
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modify history*
 * 
 */
public class BeStudentStatsProcessor implements Runnable {

	private ScheduledExecutorService							scheduler;

	// time unit is minutes
	private int													interval			= 30;

	private int													index				= 0;

	private  byte												CYCLE_APNUM			= 10;

	private final short											RELAXTIME			= 1000;

	/**
	 * Construct method
	 */
	public BeStudentStatsProcessor() {
		try {
			CYCLE_APNUM = Byte.parseByte(ConfigUtil.getConfigInfo(
					ConfigUtil.SECTION_PERFORMANCE,
					ConfigUtil.KEY_POLLING_DEVICE_NUMBER_PER_SEC, "10"));
		}catch (Exception e) {
		}
	}

	public void startTask() {
		List<LogSettings> list = QueryUtil.executeQuery(LogSettings.class, null, null);
		if (!list.isEmpty()) {
			interval = list.get(0).getInterfaceStatsInterval();
			if(interval <= 0)
				interval = 30;
		}

		// start scheduler
		if (scheduler == null || scheduler.isShutdown()) {
			scheduler = Executors.newSingleThreadScheduledExecutor();
			scheduler.scheduleWithFixedDelay(this, interval, interval, TimeUnit.MINUTES);
		}
	}

	@Override
	public void run() {
		try {
			MgrUtil.setTimerName(getClass().getSimpleName());
			DebugUtil.performanceDebugWarn("BeStudentStatsProcessor.run(): Start collect student stats thread.");

			if(!CacheMgmt.getInstance().getSMInfo().isConnected()) {
				DebugUtil.performanceDebugWarn("BeStudentStatsProcessor.run(): Stop collect student stats thread because student manager is disconnected.");
				return;
			}
			
			List<SimpleHiveAp> apList = CacheMgmt.getInstance().getManagedApList();
			if (apList.isEmpty()) {
				return;
			}

			index = 0;
			for (SimpleHiveAp ap : apList) {
				if (NmsUtil.compareSoftwareVersion("3.6.0.0", ap.getSoftVer()) > 0) {
					continue;
				}
				
				BeStudentReportEvent request = new BeStudentReportEvent();
				request.setSimpleHiveAp(ap);
				List<Byte> queryList = new ArrayList<Byte>();
				queryList.add(BeStudentReportEvent.TABLETYPE_STUDENT);
				request.setTableTypeList(queryList);
				request.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
				request.buildPacket();
				int serialNum = HmBeCommunicationUtil.sendRequest(request);

				if (serialNum == BeCommunicationConstant.SERIALNUM_SENDREQUESTFAILED) {
					// connect to capwap closed
					DebugUtil
							.performanceDebugError("BeStudentStatsProcessor.run(): Send request failed, capwap connect closed.");
//					HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR,
//							HmSystemLog.FEATURE_MONITORING, "Connect to CAPWAP closed.");
					return;
				}

				if (++index == CYCLE_APNUM) {
					try {
						Thread.sleep(RELAXTIME);
					} catch (Exception e) {
						DebugUtil.performanceDebugWarn(
								"BeInterfaceClientStatsProcessor.run() catch exception: ", e);
					}

					index = 0;
				}
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugError(
					"BeStudentStatsProcessor.run() catch exception", e);
		} catch (Error e) {
			DebugUtil
					.performanceDebugError("BeStudentStatsProcessor.run() catch error.", e);
		}
	}

	public boolean shutdown() {
		if (!scheduler.isShutdown()) {
			// Disable new tasks from being submitted.
			scheduler.shutdown();
		}
		return true;
	}

}