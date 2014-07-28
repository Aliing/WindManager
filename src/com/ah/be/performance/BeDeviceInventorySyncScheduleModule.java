package com.ah.be.performance;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeLogUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.hiveap.DeviceInventory;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.MgrUtil;
import com.ah.ws.rest.client.utils.DeviceImpUtils;

/**
 * 
 * This module is used to sync device inventory(e.g. serial number and connect status) with Redirector.
 * Rules:
 * 		1, if HMOL has not sync with Redirector(e.g. new one or upgraded), sync it right now
 * 		2, if HMOL has synchronized, sync it with a random value in 3 hours
 * 		3, then, sync every 24 hours
 * 		4, no other actions when sync is failed, just let it fail
 * 		5, this thread is only available for HMOL instance, and only sync for whole instance
 * 
 * Notes:
 * 		1, currently, check whether there are records in table "device_inventory" to define whether this HMOL has sync with Redirector
 *
 */
public class BeDeviceInventorySyncScheduleModule implements Runnable {

	private ScheduledExecutorService scheduler;
	private ScheduledFuture<?> scheduledFuture;
	
	private boolean blnHasSync = false;
	
	public BeDeviceInventorySyncScheduleModule() {

	}
	
	public void start() {
		if (!NmsUtil.isHostedHMApplication()) {
			// this thread only run for HMOL
			return;
		}
		this.blnHasSync = this.checkWhetherHasSync();
		long remainMinute = getFirstTimeSyncInMinutes();
		if (scheduler == null || scheduler.isShutdown()){
			scheduler = Executors.newSingleThreadScheduledExecutor();
			scheduledFuture = scheduler.scheduleAtFixedRate(this, remainMinute,
					24*60L, TimeUnit.MINUTES);
			
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE, 
				"<BE Thread> BeDeviceInventorySync schedule module - scheduler is running...");
		}
		this.debugIt("start thread BeDeviceInventorySyncScheduleModule");
	}

	@Override
	public void run() {
		MgrUtil.setTimerName(this.getClass().getSimpleName());
		if (!this.syncDeviceInventoriesWithRedirector()) {
			HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MINOR,
					HmSystemLog.FEATURE_MONITORING, "Failed to sync device inventories with Redirector.");
			DebugUtil.performanceDebugError(
		       "<BE Thread> BeDeviceInventorySync schedule module: Failed to sync device inventories with Redirector.");
		}
	}
	
	private void debugIt(String msg) {
		/*BeLogTools.debug(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE, 
				"<BE Thread> BeDeviceInventorySync schedule module: " + msg);
		System.out.println("<BE Thread> BeDeviceInventorySync schedule module: " + msg);*/
	}
	
	private boolean checkWhetherHasSync() {
		long snCount = QueryUtil.findRowCount(DeviceInventory.class, null);
		this.debugIt("has sync with Redirector? " + (snCount > 0));
		return snCount > 0;
	}
	
	private long getFirstTimeSyncInMinutes() {
		long result = 0;
		if (this.blnHasSync) {
			Random random = new Random();
			result = random.nextInt(180);
		}
		if (result == 0) {
			result = 1;
		}
		this.debugIt("will start sync in " + result + " minutes, has sync? " + this.blnHasSync);
		return result;
	}
	
	private boolean syncDeviceInventoriesWithRedirector() {
		return DeviceImpUtils.getInstance().syncDeviceInventoriesWithRedirector(BoMgmt.getDomainMgmt().getHomeDomain());
	}
	
	public void shutdownScheduler() throws InterruptedException {
		this.debugIt("shutdown thread BeDeviceInventorySyncScheduleModule");
		if (scheduler == null || scheduler.isShutdown()) {
			return;
		}

		try {
			if (!scheduledFuture.isDone()) {
				scheduledFuture.cancel(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
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
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE, 
				"<BE Thread> BeDeviceInventorySync schedule module - scheduler is not terminated completely");
		}
		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE, 
			"<BE Thread> BeDeviceInventorySync schedule module - scheduler is shutdown");
	}

}