package com.ah.be.performance;

import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeCapwapStatisticsEvent;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class CapwapLoadCollector implements Runnable {
	private static final Tracer	log		= new Tracer(CapwapLoadCollector.class);

	private int					TIMEOUT	= 15;

	public void run() {
		try {
			MgrUtil.setTimerName(this.getClass().getSimpleName());

			BeCommunicationEvent request = new BeCapwapStatisticsEvent();
			request.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
			request.buildPacket();

			BeCommunicationEvent resp = HmBeCommunicationUtil.sendSyncRequest(request, TIMEOUT);

			if (resp == null) {
				log.warn("query capwap stat failed! no response.");
				return;
			}

			if (!(resp instanceof BeCapwapStatisticsEvent)) {
				log.warn("query capwap stat failed! response not match.");
				return;
			}

			long x = ((BeCapwapStatisticsEvent) resp).getTotalRX();

			CurrentLoadCache.getInstance().setNumberOfCAPWAP(x);

		} catch (Exception e) {
			log.error("CapwapLoadCollector exception", e);
		}

	}

}
