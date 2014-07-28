package com.ah.integration.airtight.stage;

import com.ah.integration.airtight.AbstractSyncStage;

public class AuthorizedApSyncStage extends AbstractSyncStage {

	public AuthorizedApSyncStage() {

	}

	public AuthorizedApSyncStage(Status syncStatus) {
		super.status = syncStatus;
	}

	@Override
	public Stage getStage() {
		return Stage.AUTHORIZED_AP_SYNC;
	}

}