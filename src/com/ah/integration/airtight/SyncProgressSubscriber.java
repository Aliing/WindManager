package com.ah.integration.airtight;

/**
 * This interface is provided to WiFiScanner to display progress
 * information during synchronization.
 */
public interface SyncProgressSubscriber {

	/**
	 * When a progress is updated, the event given containing the latest progress information given will be dispatched to a specified subscriber that implements this interface.
	 *
	 * @param event which contains the synchronization progress information.
	 */
	void progressUpdated(SyncProgressEvent event);

}