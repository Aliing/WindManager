package com.ah.be.config.hiveap;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ah.be.app.HmBeConfigUtil;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.util.MgrUtil;

public class UpdatePeriodicTask implements Runnable {

	private ScheduledExecutorService timer;

	public UpdatePeriodicTask() {
	}

	public void start() {
		if (timer == null || timer.isShutdown()) {
			timer = Executors.newSingleThreadScheduledExecutor();
			timer.scheduleWithFixedDelay(this, UpdateParameters.TIMER_DELAY,
					UpdateParameters.TIMER_INTERVAL, TimeUnit.MILLISECONDS);

			BeLogTools
					.info(HmLogConst.M_TRACER | HmLogConst.M_CONFIG,
							"<BE Thread> Update Periodic - cleaning task is running...");
		}
	}

	public void stop() {
		if (timer != null && !timer.isShutdown()) {
			timer.shutdown();
		}
		BeLogTools
				.info(HmLogConst.M_TRACER | HmLogConst.M_CONFIG,
						"<BE Thread> HiveAP Update Periodic cleaning task is shutdown gracefully");
	}

	public boolean isStart() {
		return timer != null && !timer.isShutdown();
	}

	@Override
	public void run() {
		MgrUtil.setTimerName(this.getClass().getSimpleName());
		HmBeConfigUtil.getUpdateManager().scanning();
		HmBeConfigUtil.getImageDistributor().scanning();
	}

}