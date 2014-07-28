/**
 * @filename			LocationEventProcessor.java
 * @version
 * @author				Administrator
 * @since
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.location;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import com.ah.be.app.BaseModule;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.event.BeLocationTrackingEvent;
import com.ah.be.communication.event.BeLocationTrackingResultEvent;
import com.ah.be.communication.mo.RSSIReading;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.LocationRssiReport;
import com.ah.bo.monitor.MapSettings;
import com.ah.util.Tracer;

public class LocationEventProcessor extends Thread {

	private static final Tracer log = new Tracer(LocationEventProcessor.class
			.getSimpleName());

//	private BeLocationModule locationModule;

	private boolean running = true;

	private final BlockingQueue<BeBaseEvent> eventQueue;

	public LocationEventProcessor(BeLocationModule locationModule) {
//		this.locationModule = locationModule;
		this.eventQueue = locationModule.getEventQueue();
	}

	@Override
	public void run() {
		log.info("<BE Thread> Location event processor is running...");
		
		while (running) {
			try {
				BeBaseEvent event = this.eventQueue.take();

				if (event.isShutdownRequestEvent()) {
					break;
				}

				if (!(event instanceof BeLocationTrackingResultEvent)) {
					continue;
				}

				BeLocationTrackingResultEvent locationEvent = (BeLocationTrackingResultEvent) event;

				switch (locationEvent.getOpCode()) {
				case BeLocationTrackingEvent.OPCODE_ACK:

					break;
				case BeLocationTrackingEvent.OPCODE_RSSIREPORT:
					dealRssiReport(locationEvent.getClientRssiValuesMap());

					break;
				default:
					break;
				}
			} catch (Exception e) {
				log.error("run", "Error in dealing with events.", e);
			} catch (OutOfMemoryError oome) {
				System.gc();
				System.runFinalization();
				System.gc();
			}
		}
		
		log.info("<BE Thread> Location event processor is shutdown");
	}

	public void shutdown() {
		running = false;

		BeBaseEvent shutdownEvent = new BeBaseEvent();
		shutdownEvent.setModuleId(BaseModule.ModuleID_BeApp);
		shutdownEvent.setEventType(BeEventConst.Be_App_Shutdown_Request);
		this.eventQueue.offer(shutdownEvent);
	}

	private void dealRssiReport(Map<String, List<RSSIReading>> rssiReport) {
		if (rssiReport == null || rssiReport.size() == 0) {
			return;
		}

		List<LocationRssiReport> reportList = new ArrayList<LocationRssiReport>();
		Date reportTime = new Date();
		Map<Long, Set<String>> clientsByDomain = new HashMap<Long, Set<String>>();

		for (String client : rssiReport.keySet()) {
			if (client == null) {
				continue;
			}

			for (RSSIReading reading : rssiReport.get(client)) {
				if (reading == null) {
					continue;
				}

				LocationRssiReport report = new LocationRssiReport();

				report.setClientMac(client);
				report.setReportTime(reportTime);

				report.setReporterMac(reading.getApMac());
				report.setChannel(reading.getChannelFrequency());
				report.setRssi(reading.getSignalStrength());

				report.setOwner(getAPDomain(reading.getApMac()));

				Long domainId = report.getOwner().getId();
				Set<String> domainClients = clientsByDomain.get(domainId);
				if (domainClients == null) {
					domainClients = new HashSet<String>();
					clientsByDomain.put(domainId, domainClients);
				}
				domainClients.add(client);

				reportList.add(report);
			}
		}

		try {
			mergeRssiReports(rssiReport.keySet(), reportList, clientsByDomain,
					reportTime);
		} catch (Exception e) {
			log.error("dealRssiReport", "Error in merging RSSI reports into database.", e);
		}
	}

	private HmDomain getAPDomain(String apMac) {
		if (apMac == null) {
			return null;
		}

		SimpleHiveAp ap = CacheMgmt.getInstance().getSimpleHiveAp(apMac);

		if (ap != null) {
			return CacheMgmt.getInstance().getCacheDomainById(ap.getDomainId());
		} else {
			log.info("getAPDomain", "AP <" + apMac
					+ "> does not exist in cache.");

			// query from DB
			HiveAp ap2 = QueryUtil.findBoByAttribute(HiveAp.class,
					"macAddress", apMac);

			if (ap2 != null) {
				return ap2.getOwner();
			} else {
				log.info("getAPDomain", "AP <" + apMac
						+ "> does not exist in database.");
				return BoMgmt.getDomainMgmt().getHomeDomain();
			}
		}
	}

	private void mergeRssiReports(Collection<String> clientMacs,
			Collection<LocationRssiReport> reports,
			Map<Long, Set<String>> clientsByDomain, Date reportTime)
			throws Exception {
		if (reports == null || reports.size() == 0) {
			return;
		}

		for (Long domainId : clientsByDomain.keySet()) {
			MapSettings mapSettings = BeTopoModuleUtil
					.getMapGlobalSetting(domainId);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(reportTime);
			int history = mapSettings.getLocationWindow();
			// The location algorithm will use the exact location window
			// interval, but there is no need to clean up measurements so 'tightly'.
			// The interval used by CleaningTask is enough.
			history = BeLocationModuleImpl.CLEANING_TASK_INTERVAL / 60;
			calendar.add(Calendar.MINUTE, -history);
			QueryUtil.bulkRemoveBos(LocationRssiReport.class,
					new FilterParams("clientMac in (:s1) and reportTime < :s2",
							new Object[] { clientsByDomain.get(domainId),
									calendar.getTime() }));
		}

		QueryUtil.bulkCreateBos(reports);
	}

}