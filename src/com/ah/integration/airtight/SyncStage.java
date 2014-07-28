package com.ah.integration.airtight;

public interface SyncStage {

	enum Stage {
		AUTHORIZED_AP_SYNC,
		UNCATEGORIZED_AP_SYNC,
		CLIENT_SYNC,
		ASSOCIATION_SYNC,
		SIGNAL_STRENGTH_MONITOR_SYNC,
		CLIENT_RSSI_REPORT,
		DETECTED_DEVICE_RSSI_REPORT
	}

	enum Status {
		PENDING, RUNNING, FINISHED
	}

	Status getStatus();

	void setStatus(Status syncStatus);

	Stage getStage();

}