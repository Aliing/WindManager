package com.ah.integration.airtight.stage;

import com.ah.integration.airtight.AbstractSyncStage;

public class ClientSyncStage extends AbstractSyncStage {

	public ClientSyncStage() {

	}

	public ClientSyncStage(Status syncStatus) {
		super.status = syncStatus;
	}

	@Override
	public Stage getStage() {
		return Stage.CLIENT_SYNC;
	}

}