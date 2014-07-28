package com.ah.be.performance;

import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class LoadStatisticsProcessor implements Runnable {
	private static final Tracer log = new Tracer(LoadStatisticsProcessor.class);

	private CurrentLoadCache cache = CurrentLoadCache.getInstance();

	private long currentTime = 0;
	private long CYCLE_TIME = 5 * 60 * 1000;

	public void run() {
		try {
			MgrUtil.setTimerName(this.getClass().getSimpleName());
			this.currentTime = System.currentTimeMillis();

			processCapwapLike();
			processConfigLike();

//			cache.printValues();
		} catch (Exception e) {
			log.error("LoadStatisticsProcessor exception", e);
		}

	}

	private void processConfigLike() {
		/* Configuration */
		if ((currentTime - cache.getRecordTimeOfConfigRunning()) > CYCLE_TIME
				|| (cache.getNumberOfConfigRunning() > cache.getMaxNumberOfConfigRunning())) {
			cache.setMaxNumberOfConfigRunning(cache.getNumberOfConfigRunning());
			cache.setRecordTimeOfConfigRunning(currentTime);
		}
		if ((currentTime - cache.getRecordTimeOfConfigRequest()) > CYCLE_TIME
				|| (cache.getNumberOfConfigRequest() > cache.getMaxNumberOfConfigRequest())) {
			cache.setMaxNumberOfConfigRequest(cache.getNumberOfConfigRequest());
			cache.setRecordTimeOfConfigRequest(currentTime);
		}

		/* Backup */
		if ((currentTime - cache.getRecordTimeOfBackupRunning()) > CYCLE_TIME
				|| (cache.getNumberOfBackupRunning() > cache.getMaxNumberOfBackupRunning())) {
			cache.setMaxNumberOfBackupRunning(cache.getNumberOfBackupRunning());
			cache.setRecordTimeOfBackupRunning(currentTime);
		}
		if ((currentTime - cache.getRecordTimeOfBackupRequest()) > CYCLE_TIME
				|| (cache.getNumberOfBackupRequest() > cache.getMaxNumberOfBackupRequest())) {
			cache.setMaxNumberOfBackupRequest(cache.getNumberOfBackupRequest());
			cache.setRecordTimeOfBackupRequest(currentTime);
		}

		/* Restore */
		if ((currentTime - cache.getRecordTimeOfRestoreRunning()) > CYCLE_TIME
				|| (cache.getNumberOfRestoreRunning() > cache.getMaxNumberOfRestoreRunning())) {
			cache.setMaxNumberOfRestoreRunning(cache.getNumberOfRestoreRunning());
			cache.setRecordTimeOfRestoreRunning(currentTime);
		}
		if ((currentTime - cache.getRecordTimeOfRestoreRequest()) > CYCLE_TIME
				|| (cache.getNumberOfRestoreRequest() > cache.getMaxNumberOfRestoreRequest())) {
			cache.setMaxNumberOfRestoreRequest(cache.getNumberOfRestoreRequest());
			cache.setRecordTimeOfRestoreRequest(currentTime);
		}

		/* Online User */
		if ((currentTime - cache.getRecordTimeOfOnlineUser()) > CYCLE_TIME
				|| (cache.getNumberOfOnlineUser() > cache.getMaxNumberOfOnlineUser())) {
			cache.setMaxNumberOfOnlineUser(cache.getNumberOfOnlineUser());
			cache.setRecordTimeOfOnlineUser(currentTime);
		}

		/* Upgrade */
		if ((currentTime - cache.getRecordTimeOfUpgradeRunning()) > CYCLE_TIME
				|| (cache.getNumberOfUpgradeRunning() > cache.getMaxNumberOfUpgradeRunning())) {
			cache.setMaxNumberOfUpgradeRunning(cache.getNumberOfUpgradeRunning());
			cache.setRecordTimeOfUpgradeRunning(currentTime);
		}
		if ((currentTime - cache.getRecordTimeOfUpgradeRequest()) > CYCLE_TIME
				|| (cache.getNumberOfUpgradeRequest() > cache.getMaxNumberOfUpgradeRequest())) {
			cache.setMaxNumberOfUpgradeRequest(cache.getNumberOfUpgradeRequest());
			cache.setRecordTimeOfUpgradeRequest(currentTime);
		}
	}

	private void processCapwapLike() {
		/* CAPWAP */
		cache.setResultOfCAPWAP((cache.getNumberOfCAPWAP() - cache.getNumberOfLastCAPWAP()) / 5);
		if (cache.getResultOfCAPWAP() < 0) {
			log.warn("result of capwap is reset for number of capwap error, current value:"
					+ cache.getNumberOfCAPWAP() + ", last value:" + cache.getNumberOfLastCAPWAP()
					+ ".");
			cache.setResultOfCAPWAP(cache.getNumberOfCAPWAP() / 5);
			if (cache.getResultOfCAPWAP() < 0) {
				log.warn("result of capwap is set to zero for number of capwap is negative");
				cache.setResultOfCAPWAP(0);
			}
		}
		cache.setNumberOfLastCAPWAP(cache.getNumberOfCAPWAP());

		/* Alarm */
		cache.setResultOfAlarm((cache.getNumberOfAlarm() - cache.getNumberOfLastAlarm()) / 5);
		cache.setNumberOfLastAlarm(cache.getNumberOfAlarm());

		/* Event */
		cache.setResultOfEvent((cache.getNumberOfEvent() - cache.getNumberOfLastEvent()) / 5);
		cache.setNumberOfLastEvent(cache.getNumberOfEvent());
	}
}
