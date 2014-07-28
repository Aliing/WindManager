package com.ah.be.ts.hiveap.impl;

import java.util.Iterator;

import com.ah.be.communication.event.BeCapwapClientResultEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.util.Tracer;

public class NotificationEventProcessor extends EventProcessor {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(NotificationEventProcessor.class.getSimpleName());

	public NotificationEventProcessor(HiveApDebugMgmtImpl hiveApDebug) {
		super.hiveApDebug = hiveApDebug;
	}

	@Override
	public String getShortName() {
		return "Notification";
	}

	public void removeEvents(int cookieId) {
		if (log.getLogger().isDebugEnabled()) {
			log.debug("removeEvents", "Removing HiveAP trouble shooting notification events with cookie " + cookieId);
		}

		int removedEventCount = 0;

		for (Iterator<BeBaseEvent> notfEventIter = eventQueue.iterator(); notfEventIter.hasNext();) {
			BeBaseEvent notfEvent = notfEventIter.next();

			if (notfEvent instanceof BeCapwapClientResultEvent) {
				if (cookieId == ((BeCapwapClientResultEvent) notfEvent).getSequenceNum()) {
					notfEventIter.remove();
					removedEventCount++;
				}
			}
		}

		if (log.getLogger().isDebugEnabled()) {
			log.debug("removeEvents", "Removed number of " + removedEventCount + " HiveAP trouble shooting notification events with cookie " + cookieId);
		}
	}

}