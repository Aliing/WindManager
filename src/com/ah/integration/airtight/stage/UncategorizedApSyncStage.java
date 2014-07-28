package com.ah.integration.airtight.stage;

import com.ah.integration.airtight.AbstractSyncStage;

public class UncategorizedApSyncStage extends AbstractSyncStage {

	public UncategorizedApSyncStage() {

	}

	public UncategorizedApSyncStage(Status syncStatus) {
		super.status = syncStatus;
	}

	@Override
	public Stage getStage() {
		return Stage.UNCATEGORIZED_AP_SYNC;
	}

}