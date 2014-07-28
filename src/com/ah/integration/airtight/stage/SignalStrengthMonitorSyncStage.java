package com.ah.integration.airtight.stage;

import com.ah.integration.airtight.AbstractSyncStage;

public class SignalStrengthMonitorSyncStage extends AbstractSyncStage {

	public SignalStrengthMonitorSyncStage() {

	}

	public SignalStrengthMonitorSyncStage(Status syncStatus) {
		super.status = syncStatus;
	}

	@Override
	public Stage getStage() {
		return Stage.SIGNAL_STRENGTH_MONITOR_SYNC;
	}

}