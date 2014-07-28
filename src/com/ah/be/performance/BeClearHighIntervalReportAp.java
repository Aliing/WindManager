package com.ah.be.performance;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.common.ConfigUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.event.BeCliEvent;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.hiveap.HiveAp;
import com.ah.util.Tracer;

public class BeClearHighIntervalReportAp implements Runnable {

	private static final Tracer log = new Tracer(BeClearHighIntervalReportAp.class.getSimpleName());

	private ScheduledExecutorService scheduler;

	public BeClearHighIntervalReportAp() {

	}
	
	public void startTask() {
		String keyOpen = ConfigUtil.getConfigInfo(ConfigUtil.SECTION_PERFORMANCE, ConfigUtil.KEY_OPEN_REPORT_COLLECTION_HIGH_INTERVAL);
		if (keyOpen!=null && keyOpen.equalsIgnoreCase("false")) {
			return;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.clear(Calendar.MINUTE);
		calendar.clear(Calendar.SECOND);
		calendar.clear(Calendar.MILLISECOND);
		calendar.add(Calendar.HOUR_OF_DAY, 24);
		long remainMinute = (calendar.getTimeInMillis() - System.currentTimeMillis()) / 60000;
		if (scheduler == null || scheduler.isShutdown()) {
			scheduler = Executors.newSingleThreadScheduledExecutor();
			scheduler.scheduleAtFixedRate(this, remainMinute + 60, 60 * 24L, TimeUnit.MINUTES);
		}
	}

	@Override
	public void run() {
		try {
			List<SimpleHiveAp> apList = CacheMgmt.getInstance().getManagedApList();
			if (apList.isEmpty()) {
				return;
			}
			int index = 0;
			for (SimpleHiveAp ap : apList) {
				if (sendCLI4ChangePollPeriod(ap,10)) {
					index ++;
					if (index >= 20) {
						try {
							Thread.sleep(1000);
						} catch (Exception e) {
							BeLogTools.warn(HmLogConst.M_PERFORMANCE,
											"BeClearHighIntervalReportAp.run() catch exception: ", e);
						}
						
						index = 0;
					}
				}
			}
			
		} catch (Exception e) {
			log.error("run", e.getMessage(), e);
		}
	}
	
	public static boolean sendCLI4ChangePollPeriod(SimpleHiveAp ap, int value) {
		try {
			if (ap==null || ap.isSimulated() || ap.getManageStatus()!=HiveAp.STATUS_MANAGED) {
				return false;
			}
			BeCliEvent cliRequest = new BeCliEvent();
			cliRequest.setSimpleHiveAp(ap);
			cliRequest.setClis(new String[] { "report statistic period "
					+ value + "\n" });
			cliRequest
					.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
			cliRequest.buildPacket();

			HmBeCommunicationUtil.sendRequest(cliRequest);
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE,"BeClearHighIntervalReportAp.sendCLI4ChangePollPeriod() catch exception.",
							e);
		}
		return true;
	}
	
	public static void sendCLI4ChangePollPeriod(HiveAp ap, int value) {
		try {
			if (ap!=null) {
				SimpleHiveAp sAp = CacheMgmt.getInstance().getSimpleHiveAp(ap.getMacAddress());
				sendCLI4ChangePollPeriod(sAp,value);
			}
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE,"BeClearHighIntervalReportAp.sendCLI4ChangePollPeriod1() catch exception.",
							e);
		}
	}
	
//	public static void sendCLI4ChangePollPeriod(Collection<HiveAp> aps, int value) {
//		try {
//			if (aps!=null) {
//				for(HiveAp ap: aps) {
//					SimpleHiveAp sAp = CacheMgmt.getInstance().getSimpleHiveAp(ap.getMacAddress());
//					sendCLI4ChangePollPeriod(sAp,value);
//				}
//			}
//		} catch (Exception e) {
//			BeLogTools.error(HmLogConst.M_PERFORMANCE,"BeClearHighIntervalReportAp.sendCLI4ChangePollPeriod1() catch exception.",
//							e);
//		}
//	}
	
	public boolean shutdownScheduler() {
		try {
			if (scheduler == null || scheduler.isShutdown()) {
				return true;
			}
	
			// Disable new tasks from being submitted.
			scheduler.shutdown();
	
			// Wait a while for existing tasks to terminate.
			if (scheduler.awaitTermination(1L, TimeUnit.SECONDS)) {
				return true;
			}
	
			// Cancel currently executing tasks.
			scheduler.shutdownNow();
	
			// Wait a while for tasks to respond to being cancelled.
			if (!scheduler.awaitTermination(1L, TimeUnit.SECONDS)) {
				log.warn("shutdownScheduler", "Clear High Interval report Ap scheduler is not terminated completely.");
			} else {
				log.info("shutdownScheduler", "Clear High Interval report Ap scheduler is shutdown.");
			}
		} catch (InterruptedException e) {
			log.error("shutdownScheduler", e.getMessage(), e);
		}
		return true;
	}
	
}