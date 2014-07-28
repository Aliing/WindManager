package com.ah.be.topo;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeTopoUtil;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class MapLinkPolling implements Runnable {

	private static final Tracer log = new Tracer(MapLinkPolling.class
			.getSimpleName());

	private ScheduledExecutorService scheduler;

	public MapLinkPolling() {
	}

	public void start() {
		if(scheduler == null || scheduler.isShutdown()) {
			scheduler = Executors.newSingleThreadScheduledExecutor();
			DebugUtil.topoDebugInfo("Schedule links refresh after: "
					+ BeTopoModuleParameters.MAP_REFRESH_TASK_DELAY + " seconds.");
			scheduler.scheduleWithFixedDelay(this,
					BeTopoModuleParameters.MAP_REFRESH_TASK_DELAY,
					BeTopoModuleParameters.MAP_REFRESH_TASK_INTERVAL,
					TimeUnit.SECONDS);			
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_TOPO, 
				"<BE Thread> Map link polling service is running...");
		}
	}

	public void stopScheduler() {
		shutdownScheduler();
		DebugUtil.topoDebugInfo("MapLink RefreshTask shutdown gracefully.");
	}
	
	private void shutdownScheduler() {
		if (scheduler == null || scheduler.isShutdown()) {
			return;
		}

		// Disable new tasks from being submitted.
		scheduler.shutdown();

		try {
			// Wait a while for existing tasks to terminate.
			if (scheduler.awaitTermination(5L, TimeUnit.SECONDS)) {
				return;
			}

			// Cancel currently executing tasks.
			scheduler.shutdownNow();

			// Wait a while for tasks to respond to being canceled.
			if (!scheduler.awaitTermination(5L, TimeUnit.SECONDS)) {
				log.warning("shutdownScheduler",
						"MapLink Refresh scheduler did not terminate.");
			}
			
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_TOPO, 
				"<BE Thread> Map link polling service is shutdown");
		} catch (Exception ie) {
			// (Re-)Cancel if current thread also interrupted.
			DebugUtil.topoDebugError("Shutdown ScheduledExecutorService error");
			// Preserve interrupt status.
			// Thread.currentThread().interrupt();
		}
	}

	@Override
	public void run() {
		MgrUtil.setTimerName(this.getClass().getSimpleName());
		Set<Long> pollingMapId_set = new HashSet<Long>();
		if(null != HmBeTopoUtil.getPollingController()) {
            pollingMapId_set = HmBeTopoUtil.getPollingController().getPollingContainer();
		}
		if (!pollingMapId_set.isEmpty()) {
		    if(null != HmBeTopoUtil.getMapLinkProcessor()) {
		        HmBeTopoUtil.getMapLinkProcessor().addPollingContainers(
		                pollingMapId_set);
		    }
		}
	}

}