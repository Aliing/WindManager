package com.ah.be.performance;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ah.be.app.AhAppContainer;
import com.ah.bo.dashboard.AhDashboardAppAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.Tracer;

public class BeClearHighIntervalAppAp implements Runnable {

	private static final Tracer log = new Tracer(BeClearHighIntervalAppAp.class.getSimpleName());

	private ScheduledExecutorService scheduler;

	public BeClearHighIntervalAppAp() {

	}
	
	public void startTask() {
		if (scheduler == null || scheduler.isShutdown()) {
			scheduler = Executors.newSingleThreadScheduledExecutor();
			scheduler.scheduleWithFixedDelay(this, 30, 10L, TimeUnit.MINUTES);
		}
	}

	@Override
	public void run() {
		try {
			long expairTime = System.currentTimeMillis()-600000 - 1000*60*2;
			List<?> aplist = QueryUtil.executeQuery("select distinct apMac from " + AhDashboardAppAp.class.getSimpleName(), null, 
					new FilterParams("timestamp<:s1",new Object[]{expairTime}));
			if(!aplist.isEmpty()) {
				QueryUtil.bulkRemoveBos(AhDashboardAppAp.class,
						new FilterParams("timestamp<:s1",new Object[]{expairTime}));
				// TODO
				//send CLI to stop app 10 min collect
				AhAppContainer.HmBe.getPerformModule().getBeAppReportCollectionProcessor().stopAppReportCollect(aplist.toArray(new String[0]));
			}
		} catch (Exception e) {
			log.error("run", e.getMessage(), e);
		}
	}
	
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
				log.warn("shutdownScheduler", "Clear High Interval AppAp scheduler is not terminated completely.");
			} else {
				log.info("shutdownScheduler", "Clear High Interval AppAp scheduler is shutdown.");
			}
		} catch (InterruptedException e) {
			log.error("shutdownScheduler", e.getMessage(), e);
		}
		return true;
	}
	
}