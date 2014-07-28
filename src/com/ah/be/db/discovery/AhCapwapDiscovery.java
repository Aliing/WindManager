package com.ah.be.db.discovery;

import com.ah.be.communication.event.BeAPConnectEvent;

public class AhCapwapDiscovery {

	/*
	 * A ThreadLocal holder object: every thread has its own copy of an update
	 * request map
	 */
	private static final ThreadLocal<AhDiscoveryProcessor> updateReqsHolder = new ThreadLocal<AhDiscoveryProcessor>() {
		protected synchronized AhDiscoveryProcessor initialValue() {
			AhDiscoveryProcessor processor = new AhDiscoveryProcessor(
					AhDiscoveryProcessor.DEFAULT_BULK_SIZE,
					AhDiscoveryProcessor.DEFAULT_FLUSH_INTERVAL);
			processor.reinit();

			return processor;
		}
	};

	public static void discover(BeAPConnectEvent event) {
		updateReqsHolder.get().execute(event);
	}

	public static void decreaseFlushTimer() {
		// Decrease timer to flush request holder actively.
		updateReqsHolder.get().decreaseFlushTimer();
	}

}