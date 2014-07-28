package com.ah.integration.airtight;

public abstract class AbstractSyncStage implements SyncStage {

	protected Status status = Status.PENDING;

	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public void setStatus(Status status) {
		this.status = status;
	}

}