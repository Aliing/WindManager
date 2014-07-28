package com.ah.integration.airtight.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.airtight.spectraguard.api.datamanagers.SignalStrengthManager;
import com.airtight.spectraguard.api.dataobjects.devices.AP;
import com.airtight.spectraguard.api.dataobjects.devices.Client;
import com.airtight.spectraguard.api.dataobjects.devices.ClientWiFiInterface;
import com.airtight.spectraguard.api.dataobjects.devices.WiFiInterface;
import com.airtight.spectraguard.api.dataobjects.session.APISession;
import com.airtight.spectraguard.api.dataobjects.session.ReportingEntity;
import com.airtight.spectraguard.api.dataobjects.signalstrength.SignalStrength;
import com.airtight.spectraguard.api.exceptions.APIException;

import com.ah.be.common.NmsUtil;
import com.ah.be.log.HmLogConst;
import com.ah.bo.hiveap.Idp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
//import com.ah.bo.mgmt.impl.LocationTrackingImpl;
import com.ah.integration.airtight.SignalStrengthReportMgmt;
import com.ah.integration.airtight.util.SgeUtil;
import com.ah.util.Tracer;

public class SignalStrengthReportMgmtImpl implements SignalStrengthReportMgmt {

	private static final Tracer log = new Tracer(SignalStrengthReportMgmtImpl.class, HmLogConst.M_SGE);

	private static final short WIFI_CHANNEL_MIN = 1;

	private static final short WIFI_CHANNEL_MAX = 165;

	private enum RssiMonitorType {
		BG, A
	}

	@Override
	public synchronized void syncSignalStrengthMonitors(APISession session, Collection<AP> monitorHiveAps) throws APIException {
		log.info("syncSignalStrengthMonitors", "Synchronizing signal strength monitors with SGE.");
		long startTime = System.currentTimeMillis();

		if (monitorHiveAps == null) {
			return;
		}

		// Old signal strength monitors.
		Collection<WiFiInterface> oldMonitors = getSignalStrengthMonitors(session);

		// New signal strength monitors.
		Collection<WiFiInterface> newMonitors = new ArrayList<WiFiInterface>(monitorHiveAps.size() * 2);

		// Collect overall monitors.
		for (AP monitorHiveAp : monitorHiveAps) {
			WiFiInterface wifi0Monitor = null;
			WiFiInterface wifi1Monitor = null;

			for (WiFiInterface wifiIf : monitorHiveAp.getAllWiFiInterfaces()) {
				// The minimum B/G and A modes of WiFiInterfaces are signal strength monitors for WiFi0 and WiFi1 separately.
				if (wifiIf.getProtocol() == WiFiInterface.PROTOCOL_BG) {
					if (wifi0Monitor == null) {
						wifi0Monitor = wifiIf;
					} else {
						if (wifi0Monitor.getMacAddress().compareToIgnoreCase(wifiIf.getMacAddress()) > 0) {
							wifi0Monitor = wifiIf;
						}
					}
				} else {
					if (wifi1Monitor == null) {
						wifi1Monitor = wifiIf;
					} else {
						if (wifi1Monitor.getMacAddress().compareToIgnoreCase(wifiIf.getMacAddress()) > 0) {
							wifi1Monitor = wifiIf;
						}
					}
				}
			}

			if (wifi0Monitor != null) {
				newMonitors.add(wifi0Monitor);
			}

			if (wifi1Monitor != null) {
				newMonitors.add(wifi1Monitor);
			}
		}

		// Compare old signal strength monitors with new signal strength monitors.
		for (Iterator<WiFiInterface> oldMonitorIter = oldMonitors.iterator(); oldMonitorIter.hasNext();) {
			WiFiInterface oldMonitor = oldMonitorIter.next();
			int oldMonitorProtocol = oldMonitor.getProtocol();
			String oldMonitorMac = oldMonitor.getMacAddress();

			for (Iterator<WiFiInterface> newMonitorIter = newMonitors.iterator(); newMonitorIter.hasNext();) {
				WiFiInterface newMonitor = newMonitorIter.next();
				int newMonitorProtocol = newMonitor.getProtocol();
				String newMonitorMac = newMonitor.getMacAddress();

				// For comparing the HiveAP WiFiInterface, [macAddress, protocol] should be used.
				if (newMonitorMac.equalsIgnoreCase(oldMonitorMac) && newMonitorProtocol == oldMonitorProtocol) {
					// Remove new signal strength monitor.
					newMonitorIter.remove();

					// Remove old signal strength monitor.
					oldMonitorIter.remove();
					break;
				}
			}
		}

		// Delete old signal strength monitors.
		deleteSignalStrengthMonitors(session, oldMonitors);

		// Add new signal strength monitors.
		addSignalStrengthMonitors(session, newMonitors);

		long endTime = System.currentTimeMillis();
		log.info("syncSignalStrengthMonitors", "It took " + (endTime - startTime) + "ms to synchronize signal strength monitors with SGE.");
	}

	@Override
	public void reportClientSignalStrengths(APISession session, Collection<AP> monitorHiveAps, Collection<Client> transmitterClients, ReportingEntity reporter) throws APIException {
		if (monitorHiveAps == null || monitorHiveAps.isEmpty()) {
			return;
		}

		if (transmitterClients == null || transmitterClients.isEmpty()) {
			return;
		}

		// Existing signal strength monitors.
		Collection<WiFiInterface> rssiMonitors = getSignalStrengthMonitors(session);

		if (rssiMonitors == null || rssiMonitors.isEmpty()) {
			log.debug("reportClientSignalStrengths", "None of signal strength monitors found, ignore client RSSI reporting.");
			return;
		}

		reportClientSignalStrengths(session, monitorHiveAps, transmitterClients, rssiMonitors, reporter);
	}

	@Override
	public synchronized void reportClientSignalStrengths(APISession session, Collection<AP> monitorHiveAps, Collection<Client> transmitterClients, Collection<WiFiInterface> rssiMonitors, ReportingEntity reporter) throws APIException {
		log.info("reportClientSignalStrengths", "Reporting client signal strengths to SGE.");
		long startTime = System.currentTimeMillis();

		if (monitorHiveAps == null || monitorHiveAps.isEmpty()) {
			return;
		}

		if (transmitterClients == null || transmitterClients.isEmpty()) {
			return;
		}

		if (rssiMonitors == null || rssiMonitors.isEmpty()) {
			return;
		}

		Map<String, Map<RssiMonitorType, WiFiInterface>> nodeIdAndRssiMonitorMap = buildNodeIdAndRssiMonitorMap(monitorHiveAps, rssiMonitors);

		if (!nodeIdAndRssiMonitorMap.isEmpty()) {
			// Signal strength transmitters. A map of WiFiInterfaces, keyed by the string of transmitter's MAC in HM MAC format and value is WiFiInterface.
			Map<String, WiFiInterface> transmitters = new HashMap<String, WiFiInterface>(transmitterClients.size());

			for (Client transmitterClient : transmitterClients) {
				ClientWiFiInterface transmitterClientWifiIf = transmitterClient.getWiFiInterface();
				String clientMac = transmitterClientWifiIf.getMacAddress();

				// Because the client MAC will be used for HM database query, it needs to be converted into HM formatted MAC.
				String unformatClientMac = SgeUtil.getUnformattedMac(clientMac);
				transmitters.put(unformatClientMac, transmitterClientWifiIf);
			}

			// Create client SignalStrength objects.
			Collection<SignalStrength> clientSignalStrengths = createClientSignalStrengths(nodeIdAndRssiMonitorMap, transmitters);

			// Add client SignalStrength objects.
			addSignalStrength(session, clientSignalStrengths, reporter);
		}

		long endTime = System.currentTimeMillis();
		log.info("reportClientSignalStrengths", "It took " + (endTime - startTime) + "ms to report client signal strengths to SGE.");
	}

	@Override
	public void reportDetectedDeviceSignalStrengths(APISession session, Collection<AP> monitorHiveAps, Collection<WiFiInterface> transmitters, ReportingEntity reporter) throws APIException {
		if (monitorHiveAps == null || monitorHiveAps.isEmpty()) {
			log.debug("reportDetectedDeviceSignalStrengths", "Signal strength monitor HiveAPs must be required.");
			return;
		}

		if (transmitters == null || transmitters.isEmpty()) {
			log.debug("reportDetectedDeviceSignalStrengths", "Signal strength transmitters must be required.");
			return;
		}

		// Signal strength monitors existing in the SGE.
		Collection<WiFiInterface> rssiMonitors = getSignalStrengthMonitors(session);

		if (rssiMonitors == null || rssiMonitors.isEmpty()) {
			log.debug("reportDetectedDeviceSignalStrengths", "None of signal strength monitors existing in the SGE, ignore reporting RSSI values for detected devices.");
			return;
		}

		reportDetectedDeviceSignalStrengths(session, monitorHiveAps, transmitters, rssiMonitors, reporter);
	}

	@Override
	public synchronized void reportDetectedDeviceSignalStrengths(APISession session, Collection<AP> monitorHiveAps, Collection<WiFiInterface> transmitters, Collection<WiFiInterface> rssiMonitors, ReportingEntity reporter) throws APIException {
		log.info("reportDetectedDeviceSignalStrengths", "Reporting detected device signal strengths to SGE.");
		long startTime = System.currentTimeMillis();

		if (monitorHiveAps == null || monitorHiveAps.isEmpty()) {
			log.debug("reportDetectedDeviceSignalStrengths", "Signal strength monitor HiveAPs must be required.");
			return;
		}

		if (transmitters == null || transmitters.isEmpty()) {
			log.debug("reportDetectedDeviceSignalStrengths", "Signal strength transmitters must be required.");
			return;
		}

		if (rssiMonitors == null || rssiMonitors.isEmpty()) {
			log.debug("reportDetectedDeviceSignalStrengths", "Signal strength monitors must be required.");
			return;
		}

		List<?> idps = getIdps(monitorHiveAps, transmitters);

		if (idps.isEmpty()) {
			log.debug("reportDetectedDeviceSignalStrengths", "None of related IDPs found, ignore reporting RSSI values for detected devices.");
			return;
		}

		Map<String, Map<RssiMonitorType, WiFiInterface>> nodeIdAndRssiMonitorMap = buildNodeIdAndRssiMonitorMap(monitorHiveAps, rssiMonitors);

		if (!nodeIdAndRssiMonitorMap.isEmpty()) {
			// A map of RSSI transmitters, keyed by the transmitter MAC and value is the WiFiInterface object indicating a RSSI transmitter.
//			Map<String, WiFiInterface> rssiTransmitterMap = new HashMap<String, WiFiInterface>(transmitters.size());
//
//			for (WiFiInterface transmitter : transmitters) {
//				String transmitterMac = transmitter.getMacAddress();
//				String unformatWifiIfMac = SgeUtil.getUnformattedMac(transmitterMac);
//				rssiTransmitterMap.put(unformatWifiIfMac, transmitter);
//			}

			// Create detected device SignalStrength objects.
			Collection<SignalStrength> detectedDeviceSignalStrengths = createDetectedDeviceSignalStrengths(idps, nodeIdAndRssiMonitorMap, transmitters);

			// Add SignalStrength objects.
			addSignalStrength(session, detectedDeviceSignalStrengths, reporter);
		}

		long endTime = System.currentTimeMillis();
		log.info("reportDetectedDeviceSignalStrengths", "It took " + (endTime - startTime) + "ms to report detected device signal strengths to SGE.");
	}

	private Map<String, Map<RssiMonitorType, WiFiInterface>> buildNodeIdAndRssiMonitorMap(Collection<AP> monitorHiveAps, Collection<WiFiInterface> rssiMonitors) {
		/* Note: one HiveAP (except for HiveAP110) has two kinds of modes RSSI monitors (those WiFi0.1 and WiFi1.1 usually) */

		// A map of RSSI monitors, keyed by HiveAP Node ID and value is another map keyed by RssiMonitorType and value is WiFiInterface indicating a RSSI monitor.
		Map<String, Map<RssiMonitorType, WiFiInterface>> nodeIdAndRssiMonitorMap = new HashMap<String, Map<RssiMonitorType, WiFiInterface>>(monitorHiveAps.size());

		scope:
		for (AP monitorHiveAp : monitorHiveAps) { // AP iteration
			String monitorApWiredMac = monitorHiveAp.getWiredMACAddress().toUpperCase();
			String monitorApNodeId = SgeUtil.getUnformattedMac(monitorApWiredMac);

			for (WiFiInterface monitorApWifiIf : monitorHiveAp.getAllWiFiInterfaces()) { // AP WiFiInterface iteration
				int monitorApProtocol = monitorApWifiIf.getProtocol();
				String monitorApWifiIfMac = monitorApWifiIf.getMacAddress();

				for (WiFiInterface rssiMonitor : rssiMonitors) { // RSSI monitor iteration
					int rssiMonitorProtocol = rssiMonitor.getProtocol();
					String rssiMonitorMac = rssiMonitor.getMacAddress();

					// For comparing the HiveAP WiFiInterface, [macAddress, protocol] should be used.
					if (monitorApWifiIfMac.equalsIgnoreCase(rssiMonitorMac) && monitorApProtocol == rssiMonitorProtocol) {
						Map<RssiMonitorType, WiFiInterface> monitorTypeAndMonitorMap = nodeIdAndRssiMonitorMap.get(monitorApNodeId);

						if (monitorTypeAndMonitorMap == null) {
							monitorTypeAndMonitorMap = new EnumMap<RssiMonitorType, WiFiInterface>(RssiMonitorType.class);
							nodeIdAndRssiMonitorMap.put(monitorApNodeId, monitorTypeAndMonitorMap);
						}

						RssiMonitorType type = rssiMonitorProtocol == WiFiInterface.PROTOCOL_BG ? RssiMonitorType.BG : RssiMonitorType.A;
						monitorTypeAndMonitorMap.put(type, rssiMonitor);

						if (monitorTypeAndMonitorMap.size() == RssiMonitorType.values().length) {
							continue scope; // Both BG and A type RSSI monitors were totally collected, continue to the next iteration for AP.
						} else {
							break; // Find the other type RSSI monitor for this AP.
						}
					}
				}
			}
		}

		return nodeIdAndRssiMonitorMap;
	}

	private List<?> getIdps(Collection<AP> monitorHiveAps, Collection<WiFiInterface> transmitters) {
		// Monitor AP node ids.
		Collection<String> monitorApNodeIds = new ArrayList<String>(monitorHiveAps.size());

		for (AP monitorHiveAp : monitorHiveAps) {
			String monitorApWiredMac = monitorHiveAp.getWiredMACAddress();
			String monitorApNodeId = SgeUtil.getUnformattedMac(monitorApWiredMac);
			monitorApNodeIds.add(monitorApNodeId);
		}

		// Transmitter AP MACs.
		Collection<String> transmitterMacs = new ArrayList<String>(transmitters.size());

		for (WiFiInterface transmitter : transmitters) {
			String transmitterMac = transmitter.getMacAddress();
			String unformatWiFiIfMac = SgeUtil.getUnformattedMac(transmitterMac);
			transmitterMacs.add(unformatWiFiIfMac);
		}

		List<?> idps = QueryUtil.executeQuery("select reportNodeId, ifMacAddress, channel, rssi, reportTime from " + Idp.class.getSimpleName(), new SortParams("ifMacAddress"), new FilterParams("reportNodeId in (:s1) and ifMacAddress in (:s2) and channel >= (:s3) and channel <= (:s4)", new Object[] { monitorApNodeIds, transmitterMacs, WIFI_CHANNEL_MIN, WIFI_CHANNEL_MAX }));
		log.info("getIdps", "Searched number of " + idps.size() + " IDPs " + idps);
		return idps;
	}

	private Collection<SignalStrength> createDetectedDeviceSignalStrengths(List<?> idps, Map<String, Map<RssiMonitorType, WiFiInterface>> nodeIdAndRssiMonitorMap, Collection<WiFiInterface> transmitters) {
		Collection<SignalStrength> detectedDeviceSignalStrengths = new ArrayList<SignalStrength>(idps.size());

		for (Object obj : idps) {
			Object[] idpAttrs = (Object[]) obj;
			String monitorApNodeId = (String) idpAttrs[0];
			Map<RssiMonitorType, WiFiInterface> monitorTypeAndMonitorMap = nodeIdAndRssiMonitorMap.get(monitorApNodeId);

			if (monitorTypeAndMonitorMap != null && !monitorTypeAndMonitorMap.isEmpty()) {
				WiFiInterface rssiTransmitter = null;
				String detectedDeviceMac = (String) idpAttrs[1];
				short channel = (Short) idpAttrs[2];
				int protocol = SgeUtil.getProtocolByChannel(channel);

				for (WiFiInterface transmitter : transmitters) {
					String transmitterMac = transmitter.getMacAddress();
					String unformatWifiIfMac = SgeUtil.getUnformattedMac(transmitterMac);

					if (unformatWifiIfMac.equalsIgnoreCase(detectedDeviceMac) && protocol == transmitter.getProtocol()) {
						rssiTransmitter = transmitter;
						break;
					}
				}

				if (rssiTransmitter != null) {
					RssiMonitorType type = protocol == WiFiInterface.PROTOCOL_BG ? RssiMonitorType.BG : RssiMonitorType.A;
					WiFiInterface rssiMonitor = monitorTypeAndMonitorMap.get(type);

					if (rssiMonitor != null) {
						int rssi = (Short) idpAttrs[3] - 95;

//							if (rssi >= LocationTrackingImpl.hmRssiFrom && rssi <= LocationTrackingImpl.hmRssiUntil) {
						//	long toTime = ((HmTimeStamp) idpAttrs[4]).getTime();
						//	long fromTime = toTime;
							SignalStrength ss = createSignalStrength(rssiTransmitter, rssiMonitor, rssi);

							if (ss != null) {
								detectedDeviceSignalStrengths.add(ss);
							}
//							} else {
//								log.warn("createDetectedDeviceSignalStrengths", "Invalid RSSI value: " + rssi + ". It should be in the range of [" + LocationTrackingImpl.hmRssiFrom + ", " + LocationTrackingImpl.hmRssiUntil + "]. Monitor HiveAP Node ID: " + monitorApNodeId + "; Transmitter: " + rssiTransmitter);
//							}
					} else {
						log.warn("createDetectedDeviceSignalStrengths", type.toString() + " type RSSI monitor for AP " + monitorApNodeId + " didn't exist, so ignore reporting RSSI values for " + detectedDeviceMac + ". Transmitter " + rssiTransmitter);
					}
				} else {
					log.warn("createDetectedDeviceSignalStrengths", "Could not find a matched RSSI transmitter. Detected Device MAC: " + detectedDeviceMac + "; Channel: " + channel + "; Monitor HiveAP Node ID: " + monitorApNodeId);
				}
			} else {
				log.warn("createDetectedDeviceSignalStrengths", "Could not find any RSSI monitors for HiveAP " + monitorApNodeId + ", so ignore reporting RSSI values for the devices detected by this HiveAP.");
			}
		}

		return detectedDeviceSignalStrengths;
	}

	private Collection<SignalStrength> createClientSignalStrengths(Map<String, Map<RssiMonitorType, WiFiInterface>> nodeIdAndRssiMonitorMap, Map<String, WiFiInterface> rssiTransmitters) {
		StringBuilder inClauseBufForClients = new StringBuilder();

		for (Iterator<String> rssiTransmitterMacIter = rssiTransmitters.keySet().iterator(); rssiTransmitterMacIter.hasNext();) {
			String rssiTransmitterMac = rssiTransmitterMacIter.next();
			inClauseBufForClients.append("'").append(rssiTransmitterMac).append("'");

			if (rssiTransmitterMacIter.hasNext()) {
				inClauseBufForClients.append(",");
			}
		}

		StringBuilder inClauseBufForReporters = new StringBuilder();

		for (Iterator<String> rssiMonitorApNodeIdIter = nodeIdAndRssiMonitorMap.keySet().iterator(); rssiMonitorApNodeIdIter.hasNext();) {
			String rssiMonitorApNodeId = rssiMonitorApNodeIdIter.next();
			inClauseBufForReporters.append("'").append(rssiMonitorApNodeId).append("'");

			if (rssiMonitorApNodeIdIter.hasNext()) {
				inClauseBufForReporters.append(",");
			}
		}

		List<?> hmRssiReports = QueryUtil.executeNativeQuery("select distinct on (clientMac, reporterMac) clientMac, reporterMac, channel, rssi, reportTime from location_rssi_report where clientMac in (" + inClauseBufForClients.toString() + ") and reporterMac in (" + inClauseBufForReporters.toString() + ") order by clientMac, reporterMac, reportTime desc");
		Collection<SignalStrength> signalStrengths = new ArrayList<SignalStrength>(hmRssiReports.size());

		for (Object obj : hmRssiReports) {
			Object[] attrs = (Object[]) obj;
			String clientMac = (String) attrs[0];
			String reporterMac = (String) attrs[1];
			int frequency = (Integer) attrs[2];
			short rssi = (Short) attrs[3];
			Date reportTime = (Date) attrs[4];
			int protocol = SgeUtil.getProtocolByFrequency(frequency);
			RssiMonitorType type = protocol == WiFiInterface.PROTOCOL_BG ? RssiMonitorType.BG : RssiMonitorType.A;
			Map<RssiMonitorType, WiFiInterface> monitorTypeAndMonitorMap = nodeIdAndRssiMonitorMap.get(reporterMac);
			WiFiInterface rssiMonitor = monitorTypeAndMonitorMap.get(type);

			if (rssiMonitor != null) {
//				if (rssi < LocationTrackingImpl.hmRssiFrom || rssi > LocationTrackingImpl.hmRssiUntil) {
//					log.warn("createClientSignalStrengths", "Invalid RSSI: " + rssi + ", ignore client RSSI reporting. Client: " + clientMac + "; Reporter: " + reporterMac + "; Frequency: " + frequency + "; Report Time: " + reportTime);
//				} else {
					WiFiInterface rssiTransmitter = rssiTransmitters.get(clientMac);
				//	long toTime = reportTime.getTime();
				//	long fromTime = toTime;
					SignalStrength ss = createSignalStrength(rssiTransmitter, rssiMonitor, rssi);

					if (ss != null) {
						signalStrengths.add(ss);
					}
//				}
			} else {
				log.warn("createClientSignalStrengths", type.toString() + " mode HiveAP RSSI monitor doesn't exist, ignore client RSSI reporting. Client: " + clientMac + "; Reporter: " + reporterMac + "; Frequency: " + frequency + "; RSSI: " + rssi + "; Report Time: " + reportTime);
			}
		}

		return signalStrengths;
	}

	/*-
	private Map<String, Integer> getHmRssiMonitorIntervalMap(List<?> hmRssiMonitorApNodeIds) {
		// A map of RSSI report intervals, keyed by the string of AP wired MAC and value is an integer of RSSI report interval.
		Map<String, Integer> hmRssiMonitorIntervalMap = new HashMap<String, Integer>(hmRssiMonitorApNodeIds.size());
		String inClause = "";

		for (Iterator<?> iter = hmRssiMonitorApNodeIds.iterator(); iter.hasNext();) {
			String hmRssiMonitorApNodeId = (String) iter.next();
			inClause += "'" + hmRssiMonitorApNodeId + "'";

			if (iter.hasNext()) {
				inClause += ",";
			}

			hmRssiMonitorIntervalMap.put(hmRssiMonitorApNodeId, DEFAULT_RSSI_REPORT_INTERVAL);
		}

		List<?> hmRssiMonitorIntervals = QueryUtil.executeNativeQuery("select h.macaddress, l.locationreportinterval from hive_ap as h, location_server as l where l.id in (select t.location_server_id from config_template as t where t.id in (select a.template_id from hive_ap as a where a.macaddress in (" + inClause + "))) and h.macaddress in (" + inClause + ")");

		for (Object obj : hmRssiMonitorIntervals) {
			Object[] hmRssiMonitorInterval = (Object[]) obj;
	   		String hmRssiMonitorApNodeId = (String) hmRssiMonitorInterval[0];
			int rssiReportInterval = (Integer) hmRssiMonitorInterval[1];
			hmRssiMonitorIntervalMap.put(hmRssiMonitorApNodeId, rssiReportInterval);
		}

		return hmRssiMonitorIntervalMap;
	}*/

	public void addSignalStrengthMonitors(APISession session, Collection<WiFiInterface> signalStrengthMonitors) throws APIException {
        if (signalStrengthMonitors == null || signalStrengthMonitors.isEmpty()) {
			return;
        }

		log.info("addSignalStrengthMonitors", "Adding number of " + signalStrengthMonitors.size() + " signal strength monitors " + signalStrengthMonitors + " to SGE.");
		SignalStrengthManager signalMgr = new SignalStrengthManager(session);
		signalMgr.addSignalStrengthMonitors(signalStrengthMonitors);
		log.info("addSignalStrengthMonitors", "Signal strength monitors addition to SGE succeeded.");
	}

	@Override
	public Collection<WiFiInterface> getSignalStrengthMonitors(APISession session) throws APIException {
		log.info("getSignalStrengthMonitors", "Fetching signal strength monitors from SGE.");
		SignalStrengthManager signalMgr = new SignalStrengthManager(session);
		Collection<WiFiInterface> rssiMonitors = signalMgr.getSignalStrengthMonitors();

		if (rssiMonitors != null) {
			// Filter out non-HiveAP signal strength monitors.
			for (Iterator<WiFiInterface> rssiMonitorIter = rssiMonitors.iterator(); rssiMonitorIter.hasNext();) {
				WiFiInterface rssiMonitor = rssiMonitorIter.next();
				String rssiMonitorMac = rssiMonitor.getMacAddress();
				String unformatRssiMonitorMac = SgeUtil.getUnformattedMac(rssiMonitorMac);

				if (!NmsUtil.isAhMacOui(unformatRssiMonitorMac)) {
					rssiMonitorIter.remove();
				}
			}
		} else {
			rssiMonitors = new ArrayList<WiFiInterface>(0);
		}

		log.info("getSignalStrengthMonitors", "Fetched number of " + rssiMonitors.size() + " signal strength monitors " + rssiMonitors + " from SGE.");
		return rssiMonitors;
	}

	public void deleteSignalStrengthMonitors(APISession session, Collection<WiFiInterface> signalStrengthMonitors) throws APIException {
        if (signalStrengthMonitors == null || signalStrengthMonitors.isEmpty()) {
			return;
        }

		log.info("deleteSignalStrengthMonitors", "Deleting number of " + signalStrengthMonitors.size() + " signal strength monitors " + signalStrengthMonitors + " from SGE.");
		SignalStrengthManager signalMgr = new SignalStrengthManager(session);
		signalMgr.deleteSignalStrengthMonitors(signalStrengthMonitors);
		log.info("deleteSignalStrengthMonitors", "Signal strength monitors deletion from SGE succeeded.");
	}

	public SignalStrength createSignalStrength(WiFiInterface transmitter, WiFiInterface receiver, double signalStrength) {
		if (receiver == null || transmitter == null || signalStrength > 0) {
			return null;
		}

		log.info("createSignalStrength", "Creating signal strength. Monitor: " + receiver.getMacAddress() + "; Transmitter: " + transmitter.getMacAddress() + "; Signal Strength: " + signalStrength);
		SignalStrength ss = new SignalStrength(transmitter, receiver);
		ss.setSignalStrength(signalStrength);
		log.info("createSignalStrength", "Signal strength creation succeeded.");
		return ss;
	}

	public void addSignalStrength(APISession session, Collection<SignalStrength> signalStrengths, ReportingEntity reporter) throws APIException {
        if (signalStrengths == null || signalStrengths.isEmpty()) {
			return;
        }

		log.info("addSignalStrength", "Setting number of " + signalStrengths.size() + " signal strengths " + signalStrengths + " to SGE.");
		SignalStrengthManager signalMgr = new SignalStrengthManager(session);
		signalMgr.addOrUpdateSignalStrengths(signalStrengths, reporter);
		log.info("addSignalStrength", "Signal strengths addition to SGE succeeded.");
	}

}