package com.ah.ha.event;

import com.ah.ha.HAStatus;

public class HAStatusChangedEvent {

	private final HAStatus newStatus;

	private final HAStatus oldStatus;

	public HAStatusChangedEvent(HAStatus newStatus, HAStatus oldStatus) {
		this.newStatus = newStatus;
		this.oldStatus = oldStatus;
	}

	public HAStatus getNewStatus() {
		return newStatus;
	}

	public HAStatus getOldStatus() {
		return oldStatus;
	}

	@Override
	public String toString() {
		return "New Status: " + newStatus + "; Old Status: " + oldStatus;
	}

}