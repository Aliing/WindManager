package com.ah.integration.airtight.stage;

import com.ah.integration.airtight.AbstractSyncStage;

public class DetectedDeviceRssiReportStage extends AbstractSyncStage {

	public DetectedDeviceRssiReportStage() {

	}

	public DetectedDeviceRssiReportStage(Status syncStatus) {
		super.status = syncStatus;
	}

	@Override
	public Stage getStage() {
		return Stage.DETECTED_DEVICE_RSSI_REPORT;
	}

}