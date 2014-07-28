package com.ah.integration.airtight;

import java.util.Collection;

public class SyncProgressEvent {

	/* Synchronization stage holder used to store overall synchronization stages */
	protected final Collection<SyncStage> syncStages;

	/* Current synchronization progress description */
	protected final String description;

	/* Indicates if the description above is a success or failure information */
	protected final boolean succDesc;

	public SyncProgressEvent(Collection<SyncStage> syncStages, String description, boolean succDesc) {
		this.syncStages = syncStages;
		this.description = description;
		this.succDesc = succDesc;
	}

	public Collection<SyncStage> getSyncStages() {
		return syncStages;
	}

	public String getDescription() {
		return description;
	}

	public boolean isSuccDesc() {
		return succDesc;
	}

}