package com.ah.be.performance.procedure;

import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.Tracer;

public class BeProcedureProcessor implements Runnable {

	private static final Tracer log = new Tracer(BeProcedureProcessor.class.getSimpleName());

	private ScheduledExecutorService scheduler;

	public BeProcedureProcessor() {

	}
	
	public void startTask() {
		if (scheduler == null || scheduler.isShutdown()) {
			scheduler = Executors.newSingleThreadScheduledExecutor();
			scheduler.scheduleWithFixedDelay(this, 10, 60L, TimeUnit.MINUTES);
		}
	}

	@Override
	public void run() {
		Calendar calendar = Calendar.getInstance();
		calendar.clear(Calendar.MINUTE);
		calendar.clear(Calendar.SECOND);
		calendar.clear(Calendar.MILLISECOND);
		long cacluTime = calendar.getTimeInMillis() - 3600000;
		try {
			log.info("run", "f_interface_stats_roll_up begin run, time is " + cacluTime);
			boolean aa = QueryUtil.executeNativeStore("/*NO LOAD BALANCE*/select f_interface_stats_roll_up(" + cacluTime + ")");
			log.info("run", "f_interface_stats_roll_up end run, time is " + cacluTime + ", result is" + aa);
		} catch (Exception e) {
			log.error("run", e.getMessage(), e);
		}
		try {
			log.info("run", "f_client_stats_roll_up begin run, time is " + cacluTime);
			boolean aa = QueryUtil.executeNativeStore("/*NO LOAD BALANCE*/select f_client_stats_roll_up(" + cacluTime + ")");
			log.info("run", "f_client_stats_roll_up end run, time is " + cacluTime + ", result is" + aa);
		} catch (Exception e) {
			log.error("run", e.getMessage(), e);
		}
		try {
			log.info("run", "f_ssid_client_count_roll_up begin run, time is " + cacluTime);
			boolean aa = QueryUtil.executeNativeStore("/*NO LOAD BALANCE*/select f_ssid_client_count_roll_up(" + cacluTime + ")");
			log.info("run", "f_ssid_client_count_roll_up end run, time is " + cacluTime + ", result is" + aa);
		} catch (Exception e) {
			log.error("run", e.getMessage(), e);
		}
		try {
			log.info("run", "f_client_osinfo_count_roll_up begin run, time is " + cacluTime);
			boolean aa = QueryUtil.executeNativeStore("/*NO LOAD BALANCE*/select f_client_osinfo_count_roll_up(" + cacluTime + ")");
			log.info("run", "f_client_osinfo_count_roll_up end run, time is " + cacluTime + ", result is" + aa);
		} catch (Exception e) {
			log.error("run", e.getMessage(), e);
		}
		try {
			log.info("run", "f_sla_stats_roll_up begin run, time is " + cacluTime);
			boolean aa = QueryUtil.executeNativeStore("/*NO LOAD BALANCE*/select f_sla_stats_roll_up(" + cacluTime + ")");
			log.info("run", "f_sla_stats_roll_up end run, time is " + cacluTime + ", result is" + aa);
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
				log.warn("shutdownScheduler", "Performance procedure scheduler is not terminated completely.");
			} else {
				log.info("shutdownScheduler", "Performance procedure scheduler is shutdown.");
			}
		} catch (InterruptedException e) {
			log.error("shutdownScheduler", e.getMessage(), e);
		}
		return true;
	}
	
}