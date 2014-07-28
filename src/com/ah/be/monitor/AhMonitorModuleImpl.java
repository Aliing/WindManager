package com.ah.be.monitor;

import java.util.concurrent.TimeUnit;

import com.ah.be.app.BaseModule;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.monitor.thread.AhThreadMonitorImpl;
import com.ah.be.monitor.thread.AhThreadMonitoring;

public class AhMonitorModuleImpl extends BaseModule implements AhMonitorModule {

	private static final long AH_THREAD_MONITOR_INITIAL_DELAY = 1;

	private static final long AH_THREAD_MONITOR_PERIOD = 1;

	private static final TimeUnit AH_THREAD_MONITOR_TIMEUNIT = TimeUnit.MINUTES;

	private AhThreadMonitoring threadMonitor;

	public AhMonitorModuleImpl() {
		setModuleId(BaseModule.ModuleID_Monitor);
		setModuleName("BeMonitorModule");
	}

	@Override
	public boolean init() {
		threadMonitor = new AhThreadMonitorImpl(AH_THREAD_MONITOR_INITIAL_DELAY,
				AH_THREAD_MONITOR_PERIOD, AH_THREAD_MONITOR_TIMEUNIT);

		return true;
	}

	@Override
	public boolean run() {
		if (threadMonitor != null) {
			threadMonitor.start();
		}

		return true;
	}

	@Override
	public void eventDispatched(BeBaseEvent event) {
		if (event.isShutdownRequestEvent()) {
			shutdown();
		}
	}

	@Override
	public boolean shutdown() {
		if (threadMonitor != null) {
			threadMonitor.stop();
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ah.be.monitor.AhMonitorModule#getThreadMonitor()
	 */
	@Override
	public AhThreadMonitoring getThreadMonitor() {
		return threadMonitor;
	}

}