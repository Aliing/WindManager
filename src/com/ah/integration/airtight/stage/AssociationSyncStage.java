package com.ah.integration.airtight.stage;

import com.ah.integration.airtight.AbstractSyncStage;

public class AssociationSyncStage extends AbstractSyncStage {

	public AssociationSyncStage() {

	}

	public AssociationSyncStage(Status status) {
		super.status = status;
	}

	@Override
	public Stage getStage() {
		return Stage.ASSOCIATION_SYNC;
	}

}