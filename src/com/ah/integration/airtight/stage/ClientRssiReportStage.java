package com.ah.integration.airtight.stage;

import com.ah.integration.airtight.AbstractSyncStage;

public class ClientRssiReportStage extends AbstractSyncStage {

	public ClientRssiReportStage() {

	}

	public ClientRssiReportStage(Status syncStatus) {
		super.status = syncStatus;
	}

	@Override
	public Stage getStage() {
		return Stage.CLIENT_RSSI_REPORT;
	}

}