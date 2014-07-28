package com.ah.be.monitor;

import com.ah.be.monitor.thread.AhThreadMonitoring;

public interface AhMonitorModule {

	AhThreadMonitoring getThreadMonitor();

}