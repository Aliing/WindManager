package com.ah.be.topo.idp;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.ah.be.app.HmBeTopoUtil;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.util.Tracer;

public class IdpScheduledExecutor {

	public static final int IDP_SCANNING_INTERVAL = 60;// seconds

	private static final Tracer log = new Tracer(IdpScheduledExecutor.class
			.getSimpleName());

	private ScheduledExecutorService scheduler;
	private IdpCacheUpdateTask updateTask;
	private IdpRefreshTask refreshTask;
//	private IdpCalculationTask calculateTask;
	private ScheduledFuture<?> updateFuture;
	private ScheduledFuture<?> refreshFuture;
	private ScheduledFuture<?> calculateFuture;

	private boolean isStarted;

	public IdpScheduledExecutor() {
		scheduler = Executors.newScheduledThreadPool(3);
		updateTask = new IdpCacheUpdateTask();
		refreshTask = new IdpRefreshTask();
//		calculateTask = IdpCalculationTask.getInstance();
	}

	public synchronized void startTasks() {
		if (isStarted) {
			log.debug("startTask", "The tasks has been already started.");
			return;
		}
		if (scheduler==null || scheduler.isShutdown()) {
			scheduler = Executors.newScheduledThreadPool(3);
			updateTask = new IdpCacheUpdateTask();
			refreshTask = new IdpRefreshTask();
		}

		updateFuture = scheduler.scheduleWithFixedDelay(updateTask, 30, 15,
				TimeUnit.SECONDS);
		refreshFuture = scheduler.scheduleWithFixedDelay(refreshTask, 30,
				IDP_SCANNING_INTERVAL, TimeUnit.SECONDS);
//		calculateFuture = scheduler.scheduleWithFixedDelay(calculateTask, 30,
//				60, TimeUnit.SECONDS);
		// init idp settings cache
		HmBeTopoUtil.getIdpEventListener().getIdpEventProcessor().initIdpSettings();
		isStarted = true;
		log.debug("startTask", "<BE Thread> Tasks in IDP feature are started successfully");
	}

	public void stopTasks() {
		try {
			if (null != updateFuture) {
				updateFuture.cancel(false);
			}
			if (null != refreshFuture) {
				refreshFuture.cancel(false);
			}
			if (null != calculateFuture) {
				// its may time-cost, so interrupt while running.
				calculateFuture.cancel(true);
			}

			isStarted=false;

			if (scheduler == null || scheduler.isShutdown()) {
				return;
			}

			scheduler.shutdown();
			log
					.debug("stopTasks",
							"<BE Thread> IdpScheduledExecutor shutdown all IDP tasks successfully.");

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
								"<BE Thread> IdpScheduledExecutor task is not terminated completely");
			}

		} catch (Exception e) {
			log.error("stopTasks", "IdpScheduledExecutor shutdown error.", e);
		}
	}

	public IdpCacheUpdateTask getIdpCacheUpdateTask() {
		return updateTask;
	}
}