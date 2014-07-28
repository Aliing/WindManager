package com.ah.integration.airtight.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.airtight.spectraguard.api.datamanagers.DeviceManager;
import com.airtight.spectraguard.api.dataobjects.devices.AP;
import com.airtight.spectraguard.api.dataobjects.devices.APWiFiInterface;
import com.airtight.spectraguard.api.dataobjects.devices.Device;
import com.airtight.spectraguard.api.dataobjects.devices.WiFiInterface;
import com.airtight.spectraguard.api.dataobjects.session.APISession;
import com.airtight.spectraguard.api.dataobjects.session.ReportingEntity;
import com.airtight.spectraguard.api.exceptions.APIException;
import com.airtight.spectraguard.api.exceptions.ProcessingException;
import com.airtight.spectraguard.api.exceptions.ValidationException;

import com.ah.be.common.NmsUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.log.HmLogConst;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.Idp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.performance.AhLatestXif;
import com.ah.bo.performance.AhXIf;
import com.ah.bo.wlan.RadioProfile;
import com.ah.bo.wlan.SsidProfile;
import com.ah.integration.airtight.ApSyncMgmt;
import com.ah.integration.airtight.util.SgeUtil;
import com.ah.util.Tracer;

public class ApSyncMgmtImpl implements ApSyncMgmt, QueryBo {

	private static final String HIVEAP_RADIO_WIFI0_NAME = "wifi0";

	private static final String HIVEAP_RADIO_WIFI1_NAME = "wifi1";

	public static final String ERROR_MSG_ERROR_FETCHING_DEVICES = "error fetching devices";

	private static final Collection<String> illegalSsids;

	static {
		illegalSsids = new ArrayList<String>(2);
		illegalSsids.add("");
		illegalSsids.add("N/A");
	}

	/**
	 * Note:
	 *
	 * Relocation
	 *
	 * 1.	APs in uncategorized list can be relocated into authorized list using AirTight API.
	 * 2.	APs in authorized list cannot be relocated into the other lists using AirTight API.
	 *
	 * Status update restriction with SGE
	 *
	 * 1.	Active -> Active	(Permit)
	 * 2.	Active -> Inactive	(Permit)
	 * 3.	Inactive -> Active	(Permit)
	 * 4.	Inactive -> Inactive(Deny)
	 */

    private static final Tracer log = new Tracer(ApSyncMgmtImpl.class, HmLogConst.M_SGE);

	@Override
	public synchronized void syncAuthorizedHiveAps(APISession session, Collection<HmDomain> hmDomains, ReportingEntity reporter) throws APIException {
		log.info("syncAuthorizedHiveAps", "Synchronizing authorized HiveAPs with SGE.");
		long startTime = System.currentTimeMillis();

		// Get HM actively managed HiveAp objects to create corresponding SGE AP objects.
		Map<HiveAp, Collection<AhLatestXif>> hmManagedHiveAps = getActiveManagedHiveAps(hmDomains);

		// Fetch the authorized HiveAPs previously imported with the same session.
		Collection<AP> sgeAuthorizedHiveAps = fetchAuthorizedHiveAps(session, reporter);

		// Remove inactive APWiFiInterface and AP objects.
		removeInactiveApWifiIfs(sgeAuthorizedHiveAps);

		if (!hmManagedHiveAps.isEmpty()) {
			// Exclude previously imported authorized HiveAPs from the new HM managed HiveAP list.
			for (Iterator<AP> sgeHiveApIter = sgeAuthorizedHiveAps.iterator(); sgeHiveApIter.hasNext();) {
				AP sgeHiveAp = sgeHiveApIter.next();
				String sgeHiveApWiredMac = sgeHiveAp.getWiredMACAddress();
				String unformatSgeHiveApWiredMac = SgeUtil.getUnformattedMac(sgeHiveApWiredMac);

				for (HiveAp hmHiveAp : hmManagedHiveAps.keySet()) {
					if (unformatSgeHiveApWiredMac.equalsIgnoreCase(hmHiveAp.getMacAddress())) {
						// HM HiveAP WiFi interface list.
						Collection<AhLatestXif> hmHiveApWifiIfs = hmManagedHiveAps.get(hmHiveAp);

						// SGE HiveAP WiFi interface list.
						Collection<APWiFiInterface> sgeHiveApWifiIfs = sgeHiveAp.getAllWiFiInterfaces();

						for (Iterator<APWiFiInterface> sgeHiveApWifiIfIter = sgeHiveApWifiIfs.iterator(); sgeHiveApWifiIfIter.hasNext();) {
							APWiFiInterface sgeHiveApWifiIf = sgeHiveApWifiIfIter.next();
							int sgeHiveApIfProtocol = sgeHiveApWifiIf.getProtocol();
							String sgeHiveApWifiIfMac = sgeHiveApWifiIf.getMacAddress();
							String unformatSgeHiveApWifiIfMac = SgeUtil.getUnformattedMac(sgeHiveApWifiIfMac);

							for (AhLatestXif hmHiveApWifiIf : hmHiveApWifiIfs) {
								int channel = hmHiveApWifiIf.getChannel();
								int hmHiveApIfProtocol = SgeUtil.getProtocolByChannel(channel);
								String hmHiveApWifiIfMac = hmHiveApWifiIf.getBssid();

								// For the APWiFiInterface comparison, [macAddress, protocol] must be required.
								if (unformatSgeHiveApWifiIfMac.equalsIgnoreCase(hmHiveApWifiIfMac) && sgeHiveApIfProtocol == hmHiveApIfProtocol) {
									sgeHiveApWifiIfIter.remove();
									break;
								}
							}
						}

						if (sgeHiveApWifiIfs.isEmpty()) {
							sgeHiveApIter.remove();
						}

						break;
					}
				}
			}
		}

		// Create SGE authorized AP objects based on HM HiveAp objects.
		Collection<AP> importedAuthHiveAps = createAuthorizedHiveAps(hmManagedHiveAps);

		if (!sgeAuthorizedHiveAps.isEmpty()) {
			for (AP inactiveAuthHiveAp : sgeAuthorizedHiveAps) {
				for (APWiFiInterface inactiveAuthHiveApWifiIf : inactiveAuthHiveAp.getAllWiFiInterfaces()) {
					inactiveAuthHiveApWifiIf.setActiveStatus(false);
				}
			}

			// Merge active and inactive WiFi interfaces into the same AP object to import.
			for (AP importedAuthHiveAp : importedAuthHiveAps) {
				for (Iterator<AP> inactiveAuthHiveApIter = sgeAuthorizedHiveAps.iterator(); inactiveAuthHiveApIter.hasNext();) {
					AP inactiveAuthHiveAp = inactiveAuthHiveApIter.next();

					if (importedAuthHiveAp.getWiredMACAddress().equalsIgnoreCase(inactiveAuthHiveAp.getWiredMACAddress())) {
						importedAuthHiveAp.getAllWiFiInterfaces().addAll(inactiveAuthHiveAp.getAllWiFiInterfaces());
						inactiveAuthHiveApIter.remove();
						break;
					}
				}
			}

			if (!sgeAuthorizedHiveAps.isEmpty()) {
				// Put active and inactive APs into the same list to import.
				importedAuthHiveAps.addAll(sgeAuthorizedHiveAps);
			}
		}

		if (!importedAuthHiveAps.isEmpty()) {
			log.info("syncAuthorizedHiveAps", "Importing number of " + importedAuthHiveAps.size() + " authorized HiveAPs " + importedAuthHiveAps + " to SGE.");
			importAps(session, importedAuthHiveAps, reporter);
		}

		long endTime = System.currentTimeMillis();
		log.info("syncAuthorizedHiveAps", "It took " + (endTime - startTime) + "ms to synchronize authorized HiveAPs with SGE.");
	}

	@Override
	public synchronized void syncUncategorizedAps(APISession session, Collection<HmDomain> hmDomains, ReportingEntity reporter) throws APIException {
		log.info("syncUncategorizedAps", "Synchronizing uncategorized APs with SGE.");
		long startTime = System.currentTimeMillis();

		// Fetch overall authorized APs from SGE.
		Collection<AP> authAps = fetchAllAuthorizedAps(session);

		// Get HM detected APs.
		List<Idp> idps = getIdps(hmDomains, authAps);

		// Fetch the uncategorized APs previously imported with the same session.
		Collection<AP> sgeUncategorizedAps = fetchUncategorizedAps(session, reporter);

		// Remove inactive APWiFiInterface and AP objects.
		removeInactiveApWifiIfs(sgeUncategorizedAps);

		if (!idps.isEmpty()) {
			// Exclude previously imported uncategorized APs from the new HM detected AP list.
			for (Iterator<AP> sgeApIter = sgeUncategorizedAps.iterator(); sgeApIter.hasNext();) {
				AP sgeAp = sgeApIter.next();
				Collection<APWiFiInterface> sgeApWifiIfs = sgeAp.getAllWiFiInterfaces();

				for (Iterator<APWiFiInterface> sgeApWifiIfIter = sgeApWifiIfs.iterator(); sgeApWifiIfIter.hasNext();) {
					APWiFiInterface sgeApWifiIf = sgeApWifiIfIter.next();
					int sgeApIfProtocol = sgeApWifiIf.getProtocol();
					String sgeApWifiIfMac = sgeApWifiIf.getMacAddress();
					String unformatSgeApWifiIfMac = SgeUtil.getUnformattedMac(sgeApWifiIfMac);

					for (Idp idp : idps) {
						int channel = idp.getChannel();
						int hmApIfProtocol = SgeUtil.getProtocolByChannel(channel);
						String hmApWifiIfMac = idp.getIfMacAddress();

						// For the APWiFiInterface comparison, [macAddress, protocol] must be required.
						if (unformatSgeApWifiIfMac.equalsIgnoreCase(hmApWifiIfMac) && sgeApIfProtocol == hmApIfProtocol) {
							sgeApWifiIfIter.remove();
							break;
						}
					}
				}

				if (sgeApWifiIfs.isEmpty()) {
					sgeApIter.remove();
				}
			}
		}

		// Create SGE uncategorized AP objects based on HM Idp objects.
		Collection<AP> importedUncateAps = createUncategorizedAps(idps);

		if (!sgeUncategorizedAps.isEmpty()) {
			for (AP inactiveUncateAp : sgeUncategorizedAps) {
				for (APWiFiInterface inactiveUncateApWifiIf : inactiveUncateAp.getAllWiFiInterfaces()) {
					inactiveUncateApWifiIf.setActiveStatus(false);
				}
			}

			// Combine active and inactive APs into the same list to import.
			importedUncateAps.addAll(sgeUncategorizedAps);
		}

		if (!importedUncateAps.isEmpty()) {
			log.info("syncUncategorizedAps", "Importing number of " + importedUncateAps.size() + " uncategorized APs " + importedUncateAps + " to SGE.");

			for (AP importedUncateAp : importedUncateAps) {
				if (importedUncateAp.getGroupId() != Device.AP_UNCATEGORIZED_GROUP_ID) {
					importedUncateAp.setGroupId(Device.AP_UNCATEGORIZED_GROUP_ID);
				}
			}

			importAps(session, importedUncateAps, reporter);
		}

		long endTime = System.currentTimeMillis();
		log.info("syncUncategorizedAps", "It took " + (endTime - startTime) + "ms to synchronize uncategorized APs with SGE.");
	}

	/**
	 * Fetch APs from SGE that were previously imported with the same session.
	 *
	 * @param session -
	 * @param reporter -
	 * @return a list of APs imported with the same session.
	 * @throws APIException -
	 */
	@Override
	public Collection<AP> fetchAps(APISession session, ReportingEntity reporter) throws APIException {
		log.info("fetchAps", "Fetching previously imported APs from SGE.");
		DeviceManager devMgr = new DeviceManager(session);
		Collection<AP> fetchedAps;

		try {
			fetchedAps = devMgr.getAPs(reporter);
		} catch (ProcessingException pe) {
			String errorMsg = pe.getMessage();

			if (errorMsg != null && errorMsg.toLowerCase().contains(ERROR_MSG_ERROR_FETCHING_DEVICES)) {
				fetchedAps = new ArrayList<AP>(0);
			} else {
				throw pe;
			}
		}

		log.info("fetchAps", "Fetched number of " + fetchedAps.size() + " APs " + fetchedAps + " from SGE.");
		return fetchedAps;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof HiveAp) {
			HiveAp hiveAp = (HiveAp) bo;

			// Just call the getId() to get the required properties with lazy mode.
			if (hiveAp.getWifi0RadioProfile() != null) {
				hiveAp.getWifi0RadioProfile().getId();
			}

			if (hiveAp.getWifi1RadioProfile() != null) {
				hiveAp.getWifi1RadioProfile().getId();
			}
		}

		return null;
	}

	/**
	 * Fetch authorized HiveAPs from SGE that were previously imported with the same session.
	 *
	 * @param session -
	 * @param reporter -
	 * @return a list of HiveAPs imported with the same session.
	 * @throws APIException -
	 */
	private Collection<AP> fetchAuthorizedHiveAps(APISession session, ReportingEntity reporter) throws APIException {
		log.info("fetchAuthorizedHiveAps", "Fetching previously imported authorized HiveAPs from SGE.");
		DeviceManager devMgr = new DeviceManager(session);
		Collection<AP> fetchedAps;

		try {
			fetchedAps = devMgr.getAPs(reporter);
		} catch (ProcessingException pe) {
			String errorMsg = pe.getMessage();

			if (errorMsg != null && errorMsg.toLowerCase().contains(ERROR_MSG_ERROR_FETCHING_DEVICES)) {
				fetchedAps = new ArrayList<AP>(0);
			} else {
				throw pe;
			}
		}

		Collection<AP> fetchedAuthorizedHiveAps = new ArrayList<AP>(fetchedAps.size());

		for (AP fetchedAp : fetchedAps) {
			String wiredMac = fetchedAp.getWiredMACAddress();

			if (wiredMac != null) {
				String unformatWiredMac = SgeUtil.getUnformattedMac(wiredMac);

				// APs whose wired MAC starting with '001977' as well as in authorized group are the exact HiveAPs we want to fetch.
				if (NmsUtil.isAhMacOui(unformatWiredMac) && fetchedAp.getGroupId() == Device.AP_AUTHORIZED_GROUP_ID) {
					fetchedAuthorizedHiveAps.add(fetchedAp);
				}
			}
		}

		log.info("fetchAuthorizedHiveAps", "Fetched number of " + fetchedAuthorizedHiveAps.size() + " authorized HiveAPs " + fetchedAuthorizedHiveAps + " from SGE.");
		return fetchedAuthorizedHiveAps;
	}

	/**
	 * Fetch uncategorized APs from SGE that were previously imported with the same session.
	 *
	 * @param session -
	 * @param reporter -
	 * @return a list of uncategorized APs imported with the same session.
	 * @throws APIException -
	 */
	private Collection<AP> fetchUncategorizedAps(APISession session, ReportingEntity reporter) throws APIException {
		log.info("fetchUncategorizedAps", "Fetching previously imported uncategorized APs from SGE.");
		DeviceManager devMgr = new DeviceManager(session);
		Collection<AP> fetchedAps;

		try {
			fetchedAps = devMgr.getAPs(reporter);
		} catch (ProcessingException pe) {
			String errorMsg = pe.getMessage();

			if (errorMsg != null && errorMsg.toLowerCase().contains(ERROR_MSG_ERROR_FETCHING_DEVICES)) {
				fetchedAps = new ArrayList<AP>(0);
			} else {
				throw pe;
			}
		}

		Collection<AP> uncategorizedAps = new ArrayList<AP>(fetchedAps.size());

		for (AP fetchedAp : fetchedAps) {
			if (fetchedAp.getGroupId() != Device.AP_AUTHORIZED_GROUP_ID) {
				uncategorizedAps.add(fetchedAp);
			}
		}

		log.info("fetchUncategorizedAps", "Fetched number of " + uncategorizedAps.size() + " uncategorized APs " + uncategorizedAps + " from SGE.");
		return uncategorizedAps;
	}

	/**
	 * Fetch overall authorized APs from SGE.
	 *
	 * @param session -
	 * @return a list of authorized APs.
	 * @throws APIException -
	 */
	private Collection<AP> fetchAllAuthorizedAps(APISession session) throws APIException {
		log.info("fetchAllAuthorizedAps", "Fetching overall authorized APs from SGE.");
		DeviceManager devMgr = new DeviceManager(session);
		Collection<AP> allSgeAps = devMgr.getAPs();
		Collection<AP> allAuthAps = new ArrayList<AP>(allSgeAps.size());

		for (AP ap : allSgeAps) {
			if (ap.getGroupId() == Device.AP_AUTHORIZED_GROUP_ID) {
				allAuthAps.add(ap);
			}
		}

		log.info("fetchAllAuthorizedAps", "Fetched number of " + allAuthAps.size() + " authorized APs " + allAuthAps + " from SGE.");
		return allAuthAps;
	}

    /**
     * Get HM actively managed HiveAp objects.
	 *
	 * @param hmDomains the owner of HiveAps to be looked up should be in this list.
     * @return a composite map, keyed by HiveAp and value is its a list of WiFi interfaces.
     */
    private Map<HiveAp, Collection<AhLatestXif>> getActiveManagedHiveAps(Collection<HmDomain> hmDomains) {
		List<HiveAp> activeManagedHiveAps = !hmDomains.isEmpty() ? QueryUtil.executeQuery(HiveAp.class, null, new FilterParams("owner in (:s1) and deviceType = :s2 and manageStatus = :s3 and connected = :s4 and simulated = :s5", new Object[] { hmDomains, HiveAp.Device_TYPE_HIVEAP, HiveAp.STATUS_MANAGED, true, false }), null, this) : new ArrayList<HiveAp>(0);
		int size = activeManagedHiveAps.size();
		log.info("getActiveManagedHiveAps", "Searched number of " + size + " actively managed HiveAPs " + activeManagedHiveAps);
		Map<HiveAp, Collection<AhLatestXif>> hiveApAndWifiIfsMap = new HashMap<HiveAp, Collection<AhLatestXif>>(size);

		if (size > 0) {
			Collection<String> apNodeIds = new ArrayList<String>(size);
			StringBuilder inClauseBufForApNodeIds = new StringBuilder();

			for (Iterator<HiveAp> managedHiveApsIter = activeManagedHiveAps.iterator(); managedHiveApsIter.hasNext();) {
				HiveAp activeManagedHiveAp = managedHiveApsIter.next();
				String apNodeId = activeManagedHiveAp.getMacAddress();
				inClauseBufForApNodeIds.append("'").append(apNodeId).append("'");

				if (managedHiveApsIter.hasNext()) {
					inClauseBufForApNodeIds.append(",");
				}

				apNodeIds.add(apNodeId);
			}

			/* HiveAP WiFi interface list */
			List<AhLatestXif> hiveApWifiIfs = QueryUtil.executeQuery(AhLatestXif.class, new SortParams("bssid"), new FilterParams("apMac in (:s1) and ifType = :s2 and ifMode = :s3 and ifOperStatus = :s4 and bssid is not null and bssid != :s5 and ssidName is not null and upper(ssidName) not in (:s6)", new Object[] { apNodeIds, AhXIf.IFTYPE_VIRTURAL, AhXIf.IFMODE_ACCESS, AhXIf.IFOPERSTATUS_UP, "", illegalSsids }));
			log.info("getActiveManagedHiveAps", "Searched number of " + hiveApWifiIfs.size() + " HiveAP WiFi interfaces " + hiveApWifiIfs);

			/* Overall attributes used for creating SGE AP object should be collected below */
			if (!hiveApWifiIfs.isEmpty()) {
				/* Channel list */

				// Search HiveAP node id, physical WiFi interface name and radio channel.
				List<?> radioAttrsList = QueryUtil.executeNativeQuery("select x.apMac, x.ifName, r.radioChannel from HM_LATESTXIF as x, HM_LATESTRADIOATTRIBUTE as r where x.apMac = r.apMac and x.apMac in (" + inClauseBufForApNodeIds + ") and x.ifIndex = r.ifIndex");

				// A composite map, keyed by the AP node id and value is another map keyed by the string of 'wifi0'/'wifi1' and value is corresponding radio channel.
				Map<String, Map<String, Integer>> nodeIdAndChannelMap = new HashMap<String, Map<String, Integer>>(size);

				for (Object obj : radioAttrsList) {
					Object[] radioAttrs = (Object[]) obj;
					String apNodeId = (String) radioAttrs[0];
					String ifName = ((String) radioAttrs[1]).trim().toLowerCase();
					int channel = ((BigInteger) radioAttrs[2]).intValue();

					Map<String, Integer> ifNameAndChannelMap = nodeIdAndChannelMap.get(apNodeId);

					if (ifNameAndChannelMap == null) {
						ifNameAndChannelMap = new HashMap<String, Integer>(2);
						nodeIdAndChannelMap.put(apNodeId, ifNameAndChannelMap);
					}

					ifNameAndChannelMap.put(ifName, channel);
				}

				Map<String, Collection<AhLatestXif>> apNodeIdAndIfsMap = new HashMap<String, Collection<AhLatestXif>>(size);

				for (AhLatestXif hiveApWifiIf : hiveApWifiIfs) {
					String apNodeId = hiveApWifiIf.getApMac();
					Collection<AhLatestXif> hiveApWifiIfList = apNodeIdAndIfsMap.get(apNodeId);

					if (hiveApWifiIfList == null) {
						hiveApWifiIfList = new ArrayList<AhLatestXif>(32);
						apNodeIdAndIfsMap.put(apNodeId, hiveApWifiIfList);
					}

					hiveApWifiIfList.add(hiveApWifiIf);
				}

				for (HiveAp hiveAp : activeManagedHiveAps) {
					String apNodeId = hiveAp.getMacAddress();
					Collection<AhLatestXif> hiveApWifiIfList = apNodeIdAndIfsMap.get(apNodeId);

					if (hiveApWifiIfList != null) {
						/* Channel assignment */
						int channel;
						Map<String, Integer> ifNameAndChannelMap = nodeIdAndChannelMap.get(apNodeId);

						if (ifNameAndChannelMap != null) {
							for (AhLatestXif hiveApWifiIf : hiveApWifiIfList) {
								String ifName = hiveApWifiIf.getIfName().trim().toLowerCase();

								if (ifName.startsWith(HIVEAP_RADIO_WIFI0_NAME)) {
									Integer wifi0Channel = ifNameAndChannelMap.get(HIVEAP_RADIO_WIFI0_NAME);

									// Use configured radio channel as the actual interface channel if it wasn't found from the radio table ago.
									channel = wifi0Channel != null ? wifi0Channel : hiveAp.getWifi0().getChannel();
								} else {
									Integer wifi1Channel = ifNameAndChannelMap.get(HIVEAP_RADIO_WIFI1_NAME);

									// Use configured radio channel as the actual interface channel if it wasn't found from the radio table ago.
									channel = wifi1Channel != null ? wifi1Channel : hiveAp.getWifi1().getChannel();
								}

								hiveApWifiIf.setChannel(channel);
							}
						} else {
							// Use configured radio channel instead.
							for (AhLatestXif hiveApWifiIf : hiveApWifiIfList) {
								String ifName = hiveApWifiIf.getIfName().trim().toLowerCase();
								channel = ifName.startsWith(HIVEAP_RADIO_WIFI0_NAME) ? hiveAp.getWifi0().getChannel() : hiveAp.getWifi1().getChannel();
								hiveApWifiIf.setChannel(channel);
							}
						}

						hiveApAndWifiIfsMap.put(hiveAp, hiveApWifiIfList);
					} else {
						log.warn("getActiveManagedHiveAps", "Could not find any WiFi interfaces for HiveAP " + apNodeId);
					}
				}
			} else {
				log.warn("getActiveManagedHiveAps", "None of WiFi interfaces were found for HiveAPs " + apNodeIds);
			}
		}

        return hiveApAndWifiIfsMap;
    }

    /**
     * Get Idps.
	 *
	 * @param hmDomains -
	 * @param authAps the Idps to be looked up containing the AP BSSID shouldn't be included in any AP object in this list.
     * @return a list of Idps.
     */
    private List<Idp> getIdps(Collection<HmDomain> hmDomains, Collection<AP> authAps) {
		log.info("getIdps", "Getting IDPs.");
		StringBuilder inClauseBufForDomainIds = new StringBuilder();

		for (Iterator<HmDomain> hmDomainIter = hmDomains.iterator(); hmDomainIter.hasNext();) {
			HmDomain hmDomain = hmDomainIter.next();
			Long domainId = hmDomain.getId();
			inClauseBufForDomainIds.append(domainId);

			if (hmDomainIter.hasNext()) {
				inClauseBufForDomainIds.append(",");
			}
		}

		Collection<String> bssids = new HashSet<String>(authAps.size());

		for (AP authAp : authAps) {
			Collection<APWiFiInterface> authApWifiIfs = authAp.getAllWiFiInterfaces();

			for (APWiFiInterface authApWifiIf : authApWifiIfs) {
				String authApWifiIfMac = authApWifiIf.getMacAddress();
				String unformatApWifiIfMac = SgeUtil.getUnformattedMac(authApWifiIfMac);
				bssids.add(unformatApWifiIfMac);
			}
		}

		StringBuilder inClauseBufForBssids = new StringBuilder();

		for (String bssid : bssids) {
			inClauseBufForBssids.append("'").append(bssid).append("',");
		}

		inClauseBufForBssids.append("''");
		List<?> idpAttrsList = QueryUtil.executeNativeQuery("select distinct on (ifMacAddress) ifMacAddress, ssid, channel, inNetworkFlag from idp where owner in (" + inClauseBufForDomainIds.toString() + ") and stationType = " + BeCommunicationConstant.IDP_STATION_TYPE_AP + " and simulated = false and ifMacAddress not in (" + inClauseBufForBssids.toString() + ") order by ifMacAddress, time desc");
		List<Idp> idps = new ArrayList<Idp>(idpAttrsList.size());

		for (Object obj : idpAttrsList) {
			Object[] idpAttrs = (Object[]) obj;
			String ifMacAddress = (String) idpAttrs[0];
			String ssid = (String) idpAttrs[1];
			short channel = (Short) idpAttrs[2];
			short networkedStatus = (Short) idpAttrs[3];
			Idp idp = new Idp();
			idp.setIfMacAddress(ifMacAddress);
			idp.setSsid(ssid);
			idp.setChannel(channel);
			idp.setInNetworkFlag(networkedStatus);
			idps.add(idp);
		}

		log.info("getIdps", "Searched number of " + idps.size() + " HM IDPs " + idps);
        return idps;
    }

	private void removeInactiveApWifiIfs(Collection<AP> sgeAps) {
		// Remove inactive APWiFiInterface and AP objects.
		for (Iterator<AP> sgeApIter = sgeAps.iterator(); sgeApIter.hasNext();) {
			AP sgeAp = sgeApIter.next();
			Collection<APWiFiInterface> sgeApWifiIfs = sgeAp.getAllWiFiInterfaces();

			for (Iterator<APWiFiInterface> sgeApWifiIfIter = sgeApWifiIfs.iterator(); sgeApWifiIfIter.hasNext();) {
				APWiFiInterface sgeApWifiIf = sgeApWifiIfIter.next();

				if (!sgeApWifiIf.isActiveStatus()) {
					// Remove inactive APWiFiInterface.
					sgeApWifiIfIter.remove();
				}
			}

			if (sgeApWifiIfs.isEmpty()) {
				// Remove inactive AP.
				sgeApIter.remove();
			}
		}
	}

	private Collection<AP> createAuthorizedHiveAps(Map<HiveAp, Collection<AhLatestXif>> hiveApAndWifiIfsMap) {
		int size = hiveApAndWifiIfsMap.size();
		Collection<HmDomain> hmDomains = new HashSet<HmDomain>(size);
		Collection<String> ssids = new HashSet<String>(size);

		for (HiveAp hiveAp : hiveApAndWifiIfsMap.keySet()) {
			HmDomain hmDomain = hiveAp.getOwner();
			hmDomains.add(hmDomain);
			Collection<AhLatestXif> hiveApWifiIfs = hiveApAndWifiIfsMap.get(hiveAp);

			for (AhLatestXif hiveApWifiIf : hiveApWifiIfs) {
				String ssid = hiveApWifiIf.getSsidName();
				ssids.add(ssid);
			}
		}

		Collection<AP> authorizedHiveAps;

		if (!ssids.isEmpty()) {
			authorizedHiveAps = new ArrayList<AP>(size);

			// A composite map, keyed by HmDomain and value is another map keyed by SSID and value is an integer array consists of access mode, management key and encryption.
			Map<HmDomain, Map<String, int[]>> domainAndSsidMap = new HashMap<HmDomain, Map<String, int[]>>(hmDomains.size());
			List<?> ssidAttrsList = QueryUtil.executeQuery("select bo.owner, bo.ssid, bo.accessMode, bo.mgmtKey, bo.encryption from " + SsidProfile.class.getSimpleName() + " bo", null, new FilterParams("bo.owner in (:s1) and bo.ssid in (:s2)", new Object[] { hmDomains, ssids }));

			for (Object obj : ssidAttrsList) {
				Object[] ssidAttrs = (Object[]) obj;
				HmDomain hmDomain = (HmDomain) ssidAttrs[0];
				String ssid = (String) ssidAttrs[1];
				int accessMode = (Integer) ssidAttrs[2];
				int mgmtKey = (Integer) ssidAttrs[3];
				int encryption = (Integer) ssidAttrs[4];
				int[] attrs = new int[] { accessMode, mgmtKey, encryption };
				Map<String, int[]> ssidNameAndAttrsMap = domainAndSsidMap.get(hmDomain);

				if (ssidNameAndAttrsMap == null) {
					ssidNameAndAttrsMap = new HashMap<String, int[]>(ssidAttrsList.size());
					domainAndSsidMap.put(hmDomain, ssidNameAndAttrsMap);
				}

				ssidNameAndAttrsMap.put(ssid, attrs);
			}

			for (HiveAp hiveAp : hiveApAndWifiIfsMap.keySet()) {
				HmDomain hmDomain = hiveAp.getOwner();
				Map<String, int[]> ssidNameAndAttrsMap = domainAndSsidMap.get(hmDomain);

				if (ssidNameAndAttrsMap == null) {
					// Note: when this situation occurs, the SSID properties of authentication type, security, pairwise and groupwise for the list of WiFi interfaces relative to the HiveAp as one of keys for the map will be set to unknown.
					ssidNameAndAttrsMap = new HashMap<String, int[]>(0);
				}

				Collection<AhLatestXif> hiveApWifiIfs = hiveApAndWifiIfsMap.get(hiveAp);

				try {
					// SGE AP creation with multiple BSSIDs.
					AP authorizedHiveAp = createApWithBssids(hiveAp, hiveApWifiIfs, ssidNameAndAttrsMap);

					if (authorizedHiveAp != null) {
						authorizedHiveAps.add(authorizedHiveAp);
					}
				} catch (ValidationException e) {
					log.error("createAuthorizedHiveAps", "AP creation with multiple BSSIDs failed. HiveAP: " + hiveAp, e);
					SgeUtil.printErrorDetail(e);
				}
			}
		} else {
			authorizedHiveAps = new ArrayList<AP>(0);
		}

		return authorizedHiveAps;
	}

	private Collection<AP> createUncategorizedAps(Collection<Idp> idps) {
		Collection<AP> uncategorizedHiveAps = new ArrayList<AP>(idps.size());

		for (Idp idp : idps) {
			try {
				// SGE AP creation with single BSSID.
				AP ap = createApWithBssid(idp);

				if (ap != null) {
					uncategorizedHiveAps.add(ap);
				}
			} catch (ValidationException e) {
				log.error("createUncategorizedAps", "GAP creation with single BSSID failed. IDP: " + idp, e);
				SgeUtil.printErrorDetail(e);
			}
		}

		return uncategorizedHiveAps;
	}

	private AP createApWithBssids(HiveAp hiveAp, Collection<AhLatestXif> hiveApWifiIfs, Map<String, int[]> ssidNameAndAttrsMap) throws ValidationException {
		// Wired MAC address (just the HiveAP node id)
		String wiredMac = SgeUtil.getSgeFormatMac(hiveAp.getMacAddress());

		// WiFi interface IP (just HiveAP mgt0 IP).
		String ipAddress = hiveAp.getIpAddress();

		// Vendor Name
		String vendorName = "Aerohive";

		Collection<APWiFiInterface> apWifiIfs = new ArrayList<APWiFiInterface>(hiveApWifiIfs.size());

		for (AhLatestXif hiveApWifiIf : hiveApWifiIfs) {
			String ifName = hiveApWifiIf.getIfName().trim().toLowerCase();

			// WiFi interface MAC/BSSID
			String bssid = hiveApWifiIf.getBssid();
			String macAddress = SgeUtil.getSgeFormatMac(bssid);

			// Channel
			int channel = hiveApWifiIf.getChannel();

			// Protocol
			int protocol = SgeUtil.getProtocolByChannel(channel);

			// Activity Status
			boolean activeStatus = true;

			// Network Tag in -- Subnet/Netmask -- format, e.g. "192.168.1.0/24"
			String networkTag = "";

			// SSID
			String ssid = hiveApWifiIf.getSsidName();

			// Authentication Type
			int authType;

			// Security Settings
			int securitySettings;

			// Pairwise Encryption
			int pairwiseEncryption;

			// Groupwise Encryption
			int groupwiseEncryption;

			int[] ssidAttrs = ssidNameAndAttrsMap.get(ssid);

			if (ssidAttrs != null) {
				int accessMode = ssidAttrs[0];
				int mgmtKey = ssidAttrs[1];

				switch (accessMode) {
					case SsidProfile.ACCESS_MODE_WPA:
					case SsidProfile.ACCESS_MODE_PSK:
						authType = APWiFiInterface.AUTH_PSK;

						switch (mgmtKey) {
							case SsidProfile.KEY_MGMT_WPA_PSK:
								securitySettings = APWiFiInterface.SECURITY_WPA;
								break;
							case SsidProfile.KEY_MGMT_WPA2_PSK:
							case SsidProfile.KEY_MGMT_AUTO_WPA_OR_WPA2_PSK:
							default:
								securitySettings = APWiFiInterface.SECURITY_80211i;
								break;
						}
						break;
					case SsidProfile.ACCESS_MODE_8021X:
						authType = APWiFiInterface.AUTH_8021x;

						switch (mgmtKey) {
							case SsidProfile.KEY_MGMT_WPA_EAP_802_1_X:
								securitySettings = APWiFiInterface.SECURITY_WPA;
								break;
							case SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X:
							case SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X:
							default:
								securitySettings = APWiFiInterface.SECURITY_80211i;
								break;
						}
						break;
					case SsidProfile.ACCESS_MODE_WEP:
						authType = APWiFiInterface.AUTH_UNKNOWN;
						securitySettings = APWiFiInterface.SECURITY_WEP;
						break;
					case SsidProfile.ACCESS_MODE_OPEN:
						authType = APWiFiInterface.AUTH_UNKNOWN;
						securitySettings = APWiFiInterface.SECURITY_OPEN;
						break;
					default:
						log.warn("createApWithBssids", "Unknown Authentication Type: " + accessMode);
						authType = APWiFiInterface.AUTH_UNKNOWN;
						securitySettings = APWiFiInterface.SECURITY_UNKNOWN;
						break;
				}

				int encryption = ssidAttrs[2];

				switch (encryption) {
					case SsidProfile.KEY_ENC_NONE:
						pairwiseEncryption = APWiFiInterface.CIPHER_NONE;
						groupwiseEncryption = APWiFiInterface.CIPHER_NONE;
						break;
					case SsidProfile.KEY_ENC_CCMP:
						pairwiseEncryption = APWiFiInterface.CIPHER_CCMP;
						groupwiseEncryption = APWiFiInterface.CIPHER_CCMP;
						break;
					case SsidProfile.KEY_ENC_TKIP:
						pairwiseEncryption = APWiFiInterface.CIPHER_TKIP;
						groupwiseEncryption = APWiFiInterface.CIPHER_TKIP;
						break;
					case SsidProfile.KEY_ENC_WEP104:
					case SsidProfile.KEY_ENC_WEP40:
						pairwiseEncryption = APWiFiInterface.CIPHER_WEP104 | APWiFiInterface.CIPHER_WEP40;
						groupwiseEncryption = APWiFiInterface.CIPHER_WEP104 | APWiFiInterface.CIPHER_WEP40;
						break;
					case SsidProfile.KEY_ENC_AUTO_TKIP_OR_CCMP:
						// Pairwise should be a combination of APWiFiInterface.CIPHER_CCMP_VALUE | APWiFiInterface.CIPHER_TKIP_VALUE, however,
						// groupwise can be just set as APWiFiInterface.CIPHER_TKIP_VALUE only.
						pairwiseEncryption = APWiFiInterface.CIPHER_CCMP | APWiFiInterface.CIPHER_TKIP;
						groupwiseEncryption = APWiFiInterface.CIPHER_TKIP;
						break;
					default:
						log.warn("createApWithBssids", "Unknown Encryption Method: " + encryption);
						pairwiseEncryption = APWiFiInterface.CIPHER_UNKNOWN;
						groupwiseEncryption = APWiFiInterface.CIPHER_UNKNOWN;
						break;
				}
			} else {
				// The specified SSID profile doesn't exist in HM.
				log.warn("createApWithBssids", "The SSID " + ssid + " profile didn't exist in HM, so set all SSID attributes as 'Unknown' for AP WiFi interface " + bssid);
				authType = APWiFiInterface.AUTH_UNKNOWN;
				securitySettings = APWiFiInterface.SECURITY_UNKNOWN;
				pairwiseEncryption = APWiFiInterface.CIPHER_UNKNOWN;
				groupwiseEncryption = APWiFiInterface.CIPHER_UNKNOWN;
			}

			APWiFiInterface apWifiIf = new APWiFiInterface(macAddress, protocol);
			apWifiIf.setSSID(ssid);
			apWifiIf.setAuthType(authType);
			apWifiIf.setChannel(channel);
			apWifiIf.setActiveStatus(activeStatus);
			apWifiIf.setNetworkTag(networkTag);
			apWifiIf.setPairwiseEncryption(pairwiseEncryption);
			apWifiIf.setSecuritySettings(securitySettings);
			apWifiIf.setGroupwiseEncryption(groupwiseEncryption);

			// Networked Status
			apWifiIf.setNetworkedStatus(WiFiInterface.DEVICE_IS_NETWORKED);

			// Radio Profile
			RadioProfile radioProfile = ifName.startsWith(HIVEAP_RADIO_WIFI0_NAME) ? hiveAp.getWifi0RadioProfile() : hiveAp.getWifi1RadioProfile();

			if (radioProfile != null) {
				// Beacon Interval
				apWifiIf.setBeaconInterval(radioProfile.getBeaconPeriod());

				switch (radioProfile.getRadioMode()) {
					case RadioProfile.RADIO_PROFILE_MODE_NA:
					case RadioProfile.RADIO_PROFILE_MODE_NG:
					case RadioProfile.RADIO_PROFILE_MODE_AC: 
						// The WiFi interface will be provided with the 11n capability when the physical mode the radio is running on is NA or NG.
						apWifiIf.setCapability(APWiFiInterface.CAPABILITY_PRE_11N);
						break;
					case RadioProfile.RADIO_PROFILE_MODE_A:
					case RadioProfile.RADIO_PROFILE_MODE_BG:
					default:
						break;
				}
			}

			apWifiIfs.add(apWifiIf);
		}

		// Group ID
		int groupId;

		switch (hiveAp.getManageStatus()) {
			case HiveAp.STATUS_MANAGED:
				groupId = Device.AP_AUTHORIZED_GROUP_ID;
				break;
			case HiveAp.STATUS_NEW:
			default:
				groupId = Device.AP_UNCATEGORIZED_GROUP_ID;
				break;
		}

		// AP Name
		String apName = hiveAp.getHostName();

		// Device Tag
		String deviceTag = hiveAp.getProductName();

		AP ap = new AP(apWifiIfs);
		ap.setGroupId(groupId);
		ap.setName(apName);
		ap.setWiredMACAddress(wiredMac);
		ap.setDeviceTag(deviceTag);
		ap.setIpAddress(ipAddress);
		ap.setVendorName(vendorName);

		log.info("createApWithBssids", ap + " - AP creation with multiple BSSIDs succeeded.");
		return ap;
	}

	private AP createApWithBssid(Idp idp) throws ValidationException {
		// MAC Address
		String macAddress = SgeUtil.getSgeFormatMac(idp.getIfMacAddress());

		// Channel
		int channel = idp.getChannel();

		// Protocol
		int protocol = SgeUtil.getProtocolByChannel(channel);

		// IP Address
		String ipAddress = "";

		// Activity Status
		boolean activeStatus = true;

		// SSID
		String ssid = idp.getSsid();

		// Network Tag
		String networkTag = "";

		// Group ID
		int groupId = Device.AP_UNCATEGORIZED_GROUP_ID;

		/*-
		switch (idp.getIdpType()) {
			case BeCommunicationConstant.IDP_TYPE_ROGUE:
				groupId = Device.AP_ROGUE_GROUP_ID;
				break;
			case BeCommunicationConstant.IDP_TYPE_EXTERNAL:
				groupId = Device.AP_EXTERNAL_GROUP_ID;
				break;
			case BeCommunicationConstant.IDP_TYPE_VALID:
			default:
				groupId = Device.AP_UNCATEGORIZED_GROUP_ID;
				break;
		}*/

		// Vendor Name
		String vendorName = NmsUtil.isAhMacOui(idp.getIfMacAddress()) ? "Aerohive" : "";

		// Authentication Type
		int authType = APWiFiInterface.AUTH_UNKNOWN;

		// Security Settings
		int securitySettings = APWiFiInterface.SECURITY_UNKNOWN;

		// Pairwise Encryption
		int pairwiseEncryption = APWiFiInterface.CIPHER_UNKNOWN;

		// Groupwise Encryption
		int groupwiseEncryption = APWiFiInterface.CIPHER_UNKNOWN;

		APWiFiInterface apWifiIf = new APWiFiInterface(macAddress, protocol);
		apWifiIf.setSSID(ssid);
		apWifiIf.setAuthType(authType);
		apWifiIf.setChannel(channel);
		apWifiIf.setActiveStatus(activeStatus);
		apWifiIf.setNetworkTag(networkTag);
		apWifiIf.setPairwiseEncryption(pairwiseEncryption);
		apWifiIf.setSecuritySettings(securitySettings);
		apWifiIf.setGroupwiseEncryption(groupwiseEncryption);

		// Networked Status
		int networkedStatus;

		switch (idp.getInNetworkFlag()) {
			case BeCommunicationConstant.IDP_CONNECTION_IN_NET:
				networkedStatus = WiFiInterface.DEVICE_IS_NETWORKED;
				break;
			case BeCommunicationConstant.IDP_CONNECTION_NOT_SURE:
			default:
				networkedStatus = WiFiInterface.DEVICE_IS_INDETERMINATE;
				break;
		}

		apWifiIf.setNetworkedStatus(networkedStatus);

		Collection<APWiFiInterface> apWifiIfs = new ArrayList<APWiFiInterface>(1);
		apWifiIfs.add(apWifiIf);

		AP ap = new AP(apWifiIfs);
		ap.setGroupId(groupId);
		ap.setIpAddress(ipAddress);
		ap.setVendorName(vendorName);
		log.info("createApWithBssid", ap + " - AP creation with single BSSID succeeded.");
		return ap;
	}

    public void importAps(APISession session, Collection<AP> aps, ReportingEntity reporter) throws APIException {
        if (aps == null || aps.isEmpty()) {
			return;
        }

//		log.info("importAps", "Importing " + aps + " APs to SGE.");
		boolean succ = false;
		DeviceManager devMgr = new DeviceManager(session);

		try {
			devMgr.addOrUpdateAPs(aps, reporter);
			succ = true;
		} finally {
			if (!succ) {
				debugAps(aps);
			}
		}

		log.info("importAps", "APs import to SGE succeeded.");
    }

	private void debugAps(Collection<AP> aps) {
		for (AP ap : aps) {
			debugAp(ap);
		}
	}

	private void debugAp(AP ap) {
		StringBuilder apInfoBuf = new StringBuilder("\n***** AP Information *****");
		apInfoBuf.append("\nName: ").append(ap.getName());
		apInfoBuf.append("\nWired MAC: ").append(ap.getWiredMACAddress());
		apInfoBuf.append("\nIP Address: ").append(ap.getIpAddress());
		apInfoBuf.append("\nGroup ID: ").append(ap.getGroupId());
		apInfoBuf.append("\nDevice Tag: ").append(ap.getDeviceTag());
		apInfoBuf.append("\nVendor Name: ").append(ap.getVendorName());

		Collection<APWiFiInterface> apWifiIfs = ap.getAllWiFiInterfaces();
		apInfoBuf.append("\n***** AP WiFi Interface Information *****");

		for (APWiFiInterface apWifiIf : apWifiIfs) {
			apInfoBuf.append("\nMAC Address: ").append(apWifiIf.getMacAddress());
			boolean isActive = apWifiIf.isActiveStatus();

			if (isActive) {
				apInfoBuf.append("\nProtocol: ").append(apWifiIf.getProtocol());
				apInfoBuf.append("\nSSID: ").append(apWifiIf.getSSIDAsString());
				apInfoBuf.append("\nAuth Type: ").append(apWifiIf.getAuthType());
				apInfoBuf.append("\nChannel: ").append(apWifiIf.getChannel());
				apInfoBuf.append("\nNetwork Tag: ").append(apWifiIf.getNetworkTag());
				apInfoBuf.append("\nPairwise Encryption: ").append(apWifiIf.getPairwiseEncryption());
				apInfoBuf.append("\nSecurity Settings: ").append(apWifiIf.getSecuritySettings());
				apInfoBuf.append("\nGroupwise Encryption: ").append(apWifiIf.getGroupwiseEncryption());
				apInfoBuf.append("\nNetworked Status: ").append(apWifiIf.getNetworkedStatus());
				apInfoBuf.append("\nBeacon Interval: ").append(apWifiIf.getBeaconInterval());
				apInfoBuf.append("\nCapability: ").append(apWifiIf.getCapability());
			}

			apInfoBuf.append("\nActive Status: ").append(isActive);
			apInfoBuf.append("\n-----------------------------------------");
		}

		log.info("debugAp", apInfoBuf.toString());
	}

}