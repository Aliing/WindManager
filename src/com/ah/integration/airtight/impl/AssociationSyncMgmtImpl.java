package com.ah.integration.airtight.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.airtight.spectraguard.api.datamanagers.AssociationManager;
import com.airtight.spectraguard.api.dataobjects.associations.AdHocAssociation;
import com.airtight.spectraguard.api.dataobjects.associations.InfrastructureAssociation;
import com.airtight.spectraguard.api.dataobjects.devices.AP;
import com.airtight.spectraguard.api.dataobjects.devices.APWiFiInterface;
import com.airtight.spectraguard.api.dataobjects.devices.Client;
import com.airtight.spectraguard.api.dataobjects.devices.ClientWiFiInterface;
import com.airtight.spectraguard.api.dataobjects.session.APISession;
import com.airtight.spectraguard.api.dataobjects.session.ReportingEntity;
import com.airtight.spectraguard.api.exceptions.APIException;

import com.ah.be.common.DBOperationUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.log.HmLogConst;
import com.ah.bo.hiveap.Idp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.performance.AhClientSession;
import com.ah.integration.airtight.AssociationSyncMgmt;
import com.ah.integration.airtight.util.SgeUtil;
import com.ah.util.Tracer;

public class AssociationSyncMgmtImpl implements AssociationSyncMgmt {

	private static final Tracer log = new Tracer(AssociationSyncMgmtImpl.class, HmLogConst.M_SGE);

	private static final String ENCODING = "UTF-8";

	/**
	 * Synchronize infrastructure associations with SGE.
	 *
	 * @param session SGE session.
	 * @param associatedHiveAps the HiveAPs fetched from SGE.
	 * @param associatedClients the clients fetched from SGE.
	 * @throws APIException any error thrown from AirTight API.
	 */
	@Override
	public synchronized void syncInfrastructureAssociations(APISession session, Collection<AP> associatedHiveAps, Collection<Client> associatedClients, ReportingEntity reporter) throws APIException {
		log.info("syncInfrastructureAssociations", "Synchronizing infrastructure associations with SGE.");
		long startTime = System.currentTimeMillis();

		if (associatedHiveAps == null || associatedHiveAps.isEmpty()) {
			return;
		}

		if (associatedClients == null || associatedClients.isEmpty()) {
			return;
		}

		// Associated AP BSSIDs.
		Collection<String> associatedApBssids = new HashSet<String>(associatedHiveAps.size());

		// Associated AP SSIDs.
		Collection<String> associatedApSsids = new HashSet<String>(associatedHiveAps.size());

		// Collect BSSIDs and SSIDs.
		for (AP associatedHiveAp : associatedHiveAps) {
			Collection<APWiFiInterface> apWifiIfs = associatedHiveAp.getAllWiFiInterfaces();

			for (APWiFiInterface apWifiIf : apWifiIfs) {
				// The state of AP WiFiInterface should be active which is required by SGE API guide.
				if (apWifiIf.isActiveStatus()) {
					String associatedApBssid = apWifiIf.getMacAddress();
					String unformatApBssid = SgeUtil.getUnformattedMac(associatedApBssid);
					associatedApBssids.add(unformatApBssid);
				}

				String associatedApSsid = apWifiIf.getSSIDAsString();
				associatedApSsids.add(associatedApSsid);
			}
		}

		Collection<String> associatedClientMacs = new HashSet<String>(associatedClients.size());

		// Collect client MACs.
		for (Client associatedClient : associatedClients) {
			// The state of Client WiFiInterface should be active which is required by SGE API guide.
			ClientWiFiInterface clientWiFiIf = associatedClient.getWiFiInterface();

			if (clientWiFiIf.isActiveStatus()) {
				String clientMac = clientWiFiIf.getMacAddress();
				String unformatClientMac = SgeUtil.getUnformattedMac(clientMac);
				associatedClientMacs.add(unformatClientMac);
			}
		}

		// A map of infrastructure AhClientSession lists, keyed by a combined string BSSID + SSID and value is a list of AhClientSessions.
		Map<String, Collection<AhClientSession>> hmInfrastructureAssociations = getInfrastructureAssociations(associatedApBssids, associatedClientMacs);

		// A map of infrastructure Associations, keyed by a combined string BSSID + SSID and value is Association.
		Map<String, InfrastructureAssociation> oldSgeInfrastructureAssociations = fetchInfrastructureAssociations(session, associatedApSsids);

		// A list of SGE infrastructure Associations to be deleted.
		Collection<InfrastructureAssociation> deletedInfrastructureAssociations = new ArrayList<InfrastructureAssociation>(oldSgeInfrastructureAssociations.size());

		// A list of SGE infrastructure Associations to be updated.
		Collection<InfrastructureAssociation> updatedInfrastructureAssociations = new ArrayList<InfrastructureAssociation>(oldSgeInfrastructureAssociations.size());

		for (String mapKey : oldSgeInfrastructureAssociations.keySet()) {
			InfrastructureAssociation oldSgeInfrastructureAssociation = oldSgeInfrastructureAssociations.get(mapKey);
			Collection<String> oldClients = oldSgeInfrastructureAssociation.getClients();
			Collection<AhClientSession> clientSessions = hmInfrastructureAssociations.get(mapKey);

			if (clientSessions != null && !clientSessions.isEmpty()) {
				Collection<String> newClients = new ArrayList<String>(clientSessions.size());

				for (AhClientSession clientSession : clientSessions) {
					// Convert into SGE formatted MAC to compare.
					String clientMac = clientSession.getClientMac();
					String sgeFormatClientMac = SgeUtil.getSgeFormatMac(clientMac);

					if (!newClients.contains(sgeFormatClientMac)) {
						newClients.add(sgeFormatClientMac);
					}
				}

				if (oldClients.equals(newClients)) {
					log.info("syncInfrastructureAssociations", "Both the HM and SGE have the same association {" + oldSgeInfrastructureAssociation + "}. Skipping handing it.");
				} else {
					log.info("syncInfrastructureAssociations", "Replaced old clients " + oldClients + " with new clients " + newClients + " for association {" + oldSgeInfrastructureAssociation + "}");
					oldClients.clear();
					oldClients.addAll(newClients);
					updatedInfrastructureAssociations.add(oldSgeInfrastructureAssociation);
				}

				hmInfrastructureAssociations.remove(mapKey);
			} else {
				// Delete the association doesn't exist in HM yet.
				log.info("syncInfrastructureAssociations", oldSgeInfrastructureAssociation + " - infrastructure association to be deleted.");
				deletedInfrastructureAssociations.add(oldSgeInfrastructureAssociation);
			}
		}

		// Delete non-existent infrastructure associations.
		deleteInfrastructureAssociations(session, deletedInfrastructureAssociations, reporter);

		// Update existing infrastructure associations.
		updateInfrastructureAssociations(session, updatedInfrastructureAssociations, reporter);

		// Add new infrastructure associations.
		Collection<InfrastructureAssociation> newAssociations = createInfrastructureAssociations(hmInfrastructureAssociations);
		addInfrastructureAssociations(session, newAssociations, reporter);

		long endTime = System.currentTimeMillis();
		log.info("syncInfrastructureAssociations", "It took " + (endTime - startTime) + "ms to synchronize infrastructure associations with SGE.");
	}

	/**
	 * Synchronize ad-hoc associations with SGE.
	 *
	 * @param session SGE session.
	 * @param clients the clients fetched from SGE.
	 * @throws APIException any error thrown from AirTight API.
	 */
	@Override
	public synchronized void syncAdHocAssociations(APISession session, Collection<Client> clients, ReportingEntity reporter) throws APIException {
		log.info("syncAdHocAssociations", "Synchronizing ad-hoc associations with SGE.");
		long startTime = System.currentTimeMillis();

		if (clients == null || clients.isEmpty()) {
			return;
		}

		Collection<String> unformatClientMacs = new HashSet<String>(clients.size());
		Collection<String> cellIDs = new HashSet<String>(clients.size());

		// Collect client MACs and cell IDs.
		for (Client client : clients) {
			ClientWiFiInterface clientWifiIf = client.getWiFiInterface();
			String clientMac = clientWifiIf.getMacAddress();

			// The state of Client WiFiInterface should be active which is required by SGE API guide.
			if (clientWifiIf.isActiveStatus()) {
				String unformatClientMac = SgeUtil.getUnformattedMac(clientMac);
				unformatClientMacs.add(unformatClientMac);
			}

			cellIDs.add(clientMac);
		}

		// A map of Idps, keyed by client MAC and value is Idp representing an ad-hoc client.
		Map<String, Idp> hmAdHocAssociations = getAdHocAssociations(unformatClientMacs);

		// A map of ad-hoc Associations, keyed by unformatted cell ID and value is ad-hoc Association.
		Map<String, AdHocAssociation> oldSgeAdHocAssociations = fetchAdHocAssociations(session, cellIDs);

		// A list of SGE ad-hoc Associations to be deleted.
		Collection<AdHocAssociation> deletedAdHocAssociations = new ArrayList<AdHocAssociation>(oldSgeAdHocAssociations.size());

		for (String clientMac : oldSgeAdHocAssociations.keySet()) {
			Idp rougeClient = hmAdHocAssociations.get(clientMac);

			if (rougeClient != null) {
				hmAdHocAssociations.remove(clientMac);
			} else {
				AdHocAssociation oldSgeAdHocAssociation = oldSgeAdHocAssociations.get(clientMac);
				// Delete the association doesn't exist in HM yet.
				log.info("syncAdHocAssociations", oldSgeAdHocAssociation + " - ad-hoc association to be deleted.");
				deletedAdHocAssociations.add(oldSgeAdHocAssociation);
			}
		}

		// Delete non-existent ad-hoc associations.
		deleteAdHocAssociations(session, deletedAdHocAssociations, reporter);

		// Add new ad-hoc associations.
		Collection<AdHocAssociation> newAdHocAssociations = createAdHocAssociations(hmAdHocAssociations.values());
		addAdHocAssociations(session, newAdHocAssociations, reporter);

		long endTime = System.currentTimeMillis();
		log.info("syncAdHocAssociations", "It took " + (endTime - startTime) + "ms to synchronize ad-hoc associations with SGE.");
	}

	private Map<String, Collection<AhClientSession>> getInfrastructureAssociations(Collection<String> associatedApBssids, Collection<String> associatedClientMacs) {
		log.info("getInfrastructureAssociations", "Getting HM infrastructure associations.");

		// A map of AhClientSession lists, keyed by AP BSSID + SSID and value is a list of AhClientSessions.
		Map<String, Collection<AhClientSession>> hmInfrastructureAssociations;

		if (!associatedApBssids.isEmpty() && !associatedClientMacs.isEmpty()) {
			log.info("getInfrastructureAssociations", "Searching HM client sessions with AP BSSIDs " + associatedApBssids + " and Client MACs " + associatedClientMacs);
//			List<AhClientSession> clientSessionList = QueryUtil
//					.executeQuery(
//							AhClientSession.class,
//							null,
//							new FilterParams(
//									"clientBSSID in (:s1) and clientMac in (:s2) and connectstate = :s3 and clientSSID != :s4",
//									new Object[] { associatedBssids,
//											associatedClientMacs,
//											AhClientSession.CONNECT_STATE_UP,
//											"" }));
			StringBuilder where = new StringBuilder();
			where.append("clientBSSID in (");
			int associatedApBssidCount = associatedApBssids.size();

			for (int i = 0; i < associatedApBssidCount; i++) {
				where.append("?");

				if (i != associatedApBssidCount - 1) {
					where.append(",");
				}
			}

			where.append(") and clientMac in (");
			int associatedClientMacCount = associatedClientMacs.size();

			for (int i = 0; i < associatedClientMacCount; i++) {
				where.append("?");

				if (i != associatedClientMacCount - 1) {
					where.append(",");
				}
			}

			where.append(") and clientchannel > ? and connectstate = ? and clientSSID != ?");

			List<Object> paraList = new ArrayList<Object>(5);
			paraList.addAll(associatedApBssids);
			paraList.addAll(associatedClientMacs);
			paraList.add(0);
			paraList.add(AhClientSession.CONNECT_STATE_UP);
			paraList.add("");

			List<AhClientSession> clientSessionList = DBOperationUtil.executeQuery(AhClientSession.class,
					null, new FilterParams(where.toString(), paraList.toArray()));
			log.info("getInfrastructureAssociations", "Searched number of " + clientSessionList.size() + " HM client sessions " + clientSessionList);
			hmInfrastructureAssociations = new HashMap<String, Collection<AhClientSession>>(associatedApBssidCount);

			for (AhClientSession clientSession : clientSessionList) {
				String bssid = clientSession.getClientBSSID();
				String ssid = clientSession.getClientSSID();
				String mapKey = bssid + ssid;
				Collection<AhClientSession> assoClientSessions = hmInfrastructureAssociations.get(mapKey);

				if (assoClientSessions == null) {
					assoClientSessions = new ArrayList<AhClientSession>(clientSessionList.size());
					hmInfrastructureAssociations.put(mapKey, assoClientSessions);
				}

				assoClientSessions.add(clientSession);
			}
		} else {
			hmInfrastructureAssociations = new HashMap<String, Collection<AhClientSession>>(0);
		}

		return hmInfrastructureAssociations;
	}

	private Map<String, Idp> getAdHocAssociations(Collection<String> clientMacs) {
		log.info("getAdHocAssociations", "Getting HM ad-hoc associations.");

		// A map of Idps, keyed by cell ID and value is Idp.
		Map<String, Idp> hmAdHocAssociations;

		if (!clientMacs.isEmpty()) {
			log.info("getAdHocAssociations", "Searching HM ad-hoc clients with Client MACs " + clientMacs);
			List<?> adHocClientList = QueryUtil.executeNativeQuery("select distinct on (ifMacAddress) ifMacAddress, ssid, channel from idp where stationType = " + BeCommunicationConstant.IDP_STATION_TYPE_CLIENT + " and simulated = false and (stationData >= " + Idp.IDP_MATRIX_AD_HOC + " or compliance >= " + Idp.IDP_MATRIX_AD_HOC + ") order by ifMacAddress, time desc");
			log.info("getAdHocAssociations", "Searched number of " + adHocClientList.size() + " HM ad-hoc clients " + adHocClientList);
			hmAdHocAssociations = new HashMap<String, Idp>(adHocClientList.size());

			for (Object obj : adHocClientList) {
				Object[] adHocClientAttrs = (Object[]) obj;
				String clientMac = (String) adHocClientAttrs[0];
				String ssid = (String) adHocClientAttrs[1];
				short channel = (Short) adHocClientAttrs[2];
				Idp adHocClient = new Idp();
				adHocClient.setIfMacAddress(clientMac);
				adHocClient.setSsid(ssid);
				adHocClient.setChannel(channel);
				hmAdHocAssociations.put(clientMac, adHocClient);
			}
		} else {
			hmAdHocAssociations = new HashMap<String, Idp>(0);
		}

		return hmAdHocAssociations;
	}

	private Map<String, InfrastructureAssociation> fetchInfrastructureAssociations(APISession session, Collection<String> associatedApSsids) throws APIException {
		// A map of infrastructure Associations, keyed by a combined string BSSID + SSID and value is infrastructure Association.
		Map<String, InfrastructureAssociation> associationMap;

		if (!associatedApSsids.isEmpty()) {
			log.info("fetchInfrastructureAssociations", "Fetching SGE infrastructure associations.");
			Collection<byte[]> associatedApSsidsToBytes = new ArrayList<byte[]>(associatedApSsids.size());

			for (String associatedApSsid : associatedApSsids) {
				byte[] associatedApSsidToBytes;

				try {
					associatedApSsidToBytes = associatedApSsid.getBytes(ENCODING);
				} catch (UnsupportedEncodingException uee) {
					associatedApSsidToBytes = associatedApSsid.getBytes();
				}

				associatedApSsidsToBytes.add(associatedApSsidToBytes);
			}

			AssociationManager assocMgr = new AssociationManager(session);
			Collection<InfrastructureAssociation> infrastructureAssociations = assocMgr.getInfrastructureAssociation(associatedApSsidsToBytes);
			log.info("fetchInfrastructureAssociations", "Fetched number of " + infrastructureAssociations.size() + " SGE infrastructure associations " + infrastructureAssociations);
			associationMap = new HashMap<String, InfrastructureAssociation>(infrastructureAssociations.size());

			for (InfrastructureAssociation infrastructureAssociation : infrastructureAssociations) {
				String bssid = infrastructureAssociation.getApMAC();
				String unformatBssid = SgeUtil.getUnformattedMac(bssid);

				// Collect infrastructure associations.
				if (NmsUtil.isAhMacOui(unformatBssid)) {
					byte[] ssidToBytes = infrastructureAssociation.getApSSID();
					String ssid;

					try {
						ssid = new String(ssidToBytes, ENCODING);
					} catch (UnsupportedEncodingException uee) {
						ssid = new String(ssidToBytes);
					}

					// It should make use of unformatted MAC to construct the map key here.
					String mapKey = unformatBssid + ssid;
					associationMap.put(mapKey, infrastructureAssociation);
				}
			}
		} else {
			associationMap = new HashMap<String, InfrastructureAssociation>(0);
		}

		return associationMap;
	}

	private Map<String, AdHocAssociation> fetchAdHocAssociations(APISession session, Collection<String> cellIDs) throws APIException {
		// A map of ad-hoc Associations, keyed by cell ID and value is ad-hoc Association.
		Map<String, AdHocAssociation> adHocAssociationMap;

		if (!cellIDs.isEmpty()) {
			log.info("fetchAdHocAssociations", "Fetching SGE ad-hoc associations.");
			AssociationManager assocMgr = new AssociationManager(session);
			Collection<AdHocAssociation> adHocAssociations = assocMgr.getAdHocAssociation(cellIDs);
			log.info("fetchAdHocAssociations", "Fetched number of " + adHocAssociations.size() + " SGE ad-hoc associations " + adHocAssociations);
			adHocAssociationMap = new HashMap<String, AdHocAssociation>(adHocAssociations.size());

			for (AdHocAssociation adHocAssociation : adHocAssociations) {
				String cellID = adHocAssociation.getCellId();
				String unformatCellID = SgeUtil.getUnformattedMac(cellID);
				adHocAssociationMap.put(unformatCellID, adHocAssociation);
			}
		} else {
			adHocAssociationMap = new HashMap<String, AdHocAssociation>(0);
		}

		return adHocAssociationMap;
	}

	private Collection<InfrastructureAssociation> createInfrastructureAssociations(Map<String, Collection<AhClientSession>> hmInfrastructureAssociations) {
		Collection<InfrastructureAssociation> infrastructureAssociations = new ArrayList<InfrastructureAssociation>(hmInfrastructureAssociations.size());

		for (String mapKey : hmInfrastructureAssociations.keySet()) {
			// Separate BSSID and SSID from the complex key.
			String bssid = mapKey.substring(0, 12);
			String sgeFormatBssid = SgeUtil.getSgeFormatMac(bssid);
		//	String ssid = key.substring(12);
			int apProtocol = 0;
			Collection<AhClientSession> clientSessions = hmInfrastructureAssociations.get(mapKey);
			Collection<String> clientMacs = new ArrayList<String>(clientSessions.size());

			for (AhClientSession clientSession : clientSessions) {
				String clientMac = clientSession.getClientMac();

				// Associated client MAC should be in the SGE MAC format.
				String sgeFormatClientMac = SgeUtil.getSgeFormatMac(clientMac);
				clientMacs.add(sgeFormatClientMac);

				if (apProtocol == 0) {
					int channel = clientSession.getClientChannel();
					apProtocol = SgeUtil.getProtocolByChannel(channel);
				}
			}

			InfrastructureAssociation infrastructureAssociation = new InfrastructureAssociation(sgeFormatBssid, apProtocol, clientMacs);
			log.info("createInfrastructureAssociations", infrastructureAssociation + " - infrastructure association creation succeeded.");
			infrastructureAssociations.add(infrastructureAssociation);
		}

		return infrastructureAssociations;
	}

	private Collection<AdHocAssociation> createAdHocAssociations(Collection<Idp> adHocClients) {
		Collection<AdHocAssociation> adHocAssociations = new ArrayList<AdHocAssociation>(adHocClients.size());

		for (Idp adHocClient : adHocClients) {
			String clientMac = adHocClient.getIfMacAddress();
			String cellID = SgeUtil.getSgeFormatMac(clientMac);
			Collection<String> clients = new ArrayList<String>(0);
			AdHocAssociation adHocAssociation = new AdHocAssociation(cellID, clients);
			log.info("createAdHocAssociations", adHocAssociation + " - ad-hoc association creation succeeded.");
			adHocAssociations.add(adHocAssociation);
		}

		return adHocAssociations;
	}

    public void addInfrastructureAssociations(APISession session, Collection<InfrastructureAssociation> infrastructureAssociations, ReportingEntity reporter) throws APIException {
        if (infrastructureAssociations == null || infrastructureAssociations.isEmpty()) {
			return;
        }

		log.info("addInfrastructureAssociations", "Adding number of " + infrastructureAssociations.size() + " infrastructure associations " + infrastructureAssociations + " to SGE.");
		boolean succ = false;
	   	AssociationManager assocMgr = new AssociationManager(session);

		try {
			assocMgr.addOrUpdateInfrastructureAssociations(infrastructureAssociations, reporter);
			succ = true;
		} finally {
			if (!succ) {
				debugInfrastructureAssociationAssociations(infrastructureAssociations);
			}
		}

		log.info("addInfrastructureAssociations", "Infrastructure associations addition to SGE succeeded.");
    }

    public void addAdHocAssociations(APISession session, Collection<AdHocAssociation> adHocAssociations, ReportingEntity reporter) throws APIException {
        if (adHocAssociations == null || adHocAssociations.isEmpty()) {
			return;
        }

		log.info("addAdHocAssociations", "Adding number of " + adHocAssociations.size() + " ad-hoc associations " + adHocAssociations + " to SGE.");
		boolean succ = false;
		AssociationManager assocMgr = new AssociationManager(session);

		try {
			assocMgr.addOrUpdateAdHocAssociations(adHocAssociations, reporter);
			succ = true;
		} finally {
			if (!succ) {
				debugAdHocAssociationAssociationAssociations(adHocAssociations);
			}
		}

		log.info("addAdHocAssociations", "Ad-hoc associations addition to SGE succeeded.");
    }

	public void updateInfrastructureAssociations(APISession session, Collection<InfrastructureAssociation> infrastructureAssociations, ReportingEntity reporter) throws APIException {
        if (infrastructureAssociations == null || infrastructureAssociations.isEmpty()) {
			return;
        }

		log.info("updateInfrastructureAssociations", "Updating number of " + infrastructureAssociations.size() + " infrastructure associations " + infrastructureAssociations + " to SGE.");
		boolean succ = false;
		AssociationManager assocMgr = new AssociationManager(session);

		try {
			assocMgr.addOrUpdateInfrastructureAssociations(infrastructureAssociations, reporter);
			succ = true;
		} finally {
			if (!succ) {
				debugInfrastructureAssociationAssociations(infrastructureAssociations);
			}
		}

		log.info("updateInfrastructureAssociations", "Infrastructure associations update to SGE succeeded.");
	}

	public void updateAdHocAssociations(APISession session, Collection<AdHocAssociation> adHocAssociations, ReportingEntity reporter) throws APIException {
        if (adHocAssociations == null || adHocAssociations.isEmpty()) {
			return;
        }

		log.info("updateAdHocAssociations", "Updating number of " + adHocAssociations.size() + " ad-hoc associations " + adHocAssociations + " to SGE.");
		boolean succ = false;
		AssociationManager assocMgr = new AssociationManager(session);

		try {
			assocMgr.addOrUpdateAdHocAssociations(adHocAssociations, reporter);
			succ = true;
		} finally {
			if (!succ) {
				debugAdHocAssociationAssociationAssociations(adHocAssociations);
			}
		}

		log.info("updateAdHocAssociations", "Ad-hoc associations update to SGE succeeded.");
	}

	public void deleteInfrastructureAssociations(APISession session, Collection<InfrastructureAssociation> infrastructureAssociations, ReportingEntity reporter) throws APIException {
        if (infrastructureAssociations == null || infrastructureAssociations.isEmpty()) {
			return;
        }

		log.info("deleteInfrastructureAssociations", "Deleting number of " + infrastructureAssociations.size() + " infrastructure associations " + infrastructureAssociations + " from SGE.");
		boolean succ = false;
		AssociationManager assocMgr = new AssociationManager(session);

		try {
			assocMgr.deleteInfrastructureAssociations(infrastructureAssociations, reporter);
			succ = true;
		} finally {
			if (!succ) {
				debugInfrastructureAssociationAssociations(infrastructureAssociations);
			}
		}

		log.info("deleteInfrastructureAssociations", "Infrastructure associations deletion from SGE succeeded.");
	}

	public void deleteAdHocAssociations(APISession session, Collection<AdHocAssociation> adHocAssociations, ReportingEntity reporter) throws APIException {
        if (adHocAssociations == null || adHocAssociations.isEmpty()) {
			return;
        }

		log.info("deleteAdHocAssociations", "Deleting number of " + adHocAssociations.size() + " ad-hoc associations " + adHocAssociations + " from SGE.");
		boolean succ = false;
		AssociationManager assocMgr = new AssociationManager(session);

		try {
			assocMgr.deleteAdHocAssociations(adHocAssociations, reporter);
			succ = true;
		} finally {
			if (!succ) {
				debugAdHocAssociationAssociationAssociations(adHocAssociations);
			}
		}

		log.info("deleteAdHocAssociations", "Ad-hoc associations deletion from SGE succeeded.");
	}

	private void debugInfrastructureAssociationAssociations(Collection<InfrastructureAssociation> infrastructureAssociations) {
		for (InfrastructureAssociation infrastructureAssociation : infrastructureAssociations) {
			debugInfrastructureAssociationAssociation(infrastructureAssociation);
		}
	}

	private void debugInfrastructureAssociationAssociation(InfrastructureAssociation infrastructureAssociation) {
		StringBuilder assoInfoBuf = new StringBuilder("\n***** Association Information *****");
		assoInfoBuf.append("\nAssociation Type: ").append(infrastructureAssociation.getType());
		assoInfoBuf.append("\nAssociated AP MAC: ").append(infrastructureAssociation.getApMAC());
		assoInfoBuf.append("\nAssociated AP SSID: ").append(new String(infrastructureAssociation.getApSSID()));
		assoInfoBuf.append("\nAssociated AP Protocol: ").append(infrastructureAssociation.getApProtocol());
		assoInfoBuf.append("\nAssociated Clients: ").append(infrastructureAssociation.getClients());
		assoInfoBuf.append("\nDeleted Clients: ").append(infrastructureAssociation.getDeletedClients());
		log.info("debugInfrastructureAssociationAssociation", assoInfoBuf.toString());
	}

	private void debugAdHocAssociationAssociationAssociations(Collection<AdHocAssociation> adHocAssociations) {
		for (AdHocAssociation adHocAssociation : adHocAssociations) {
			debugAdHocAssociationAssociationAssociation(adHocAssociation);
		}
	}

	private void debugAdHocAssociationAssociationAssociation(AdHocAssociation adHocAssociations) {
		StringBuilder assoInfoBuf = new StringBuilder("\n***** Association Information *****");
		assoInfoBuf.append("\nAssociation Type: ").append(adHocAssociations.getType());
		assoInfoBuf.append("\nCell ID: ").append(adHocAssociations.getCellId());
		assoInfoBuf.append("\nSSID: ").append(new String(adHocAssociations.getSSID()));
		assoInfoBuf.append("\nAP Protocol: ").append(adHocAssociations.getProtocol());
		assoInfoBuf.append("\nAssociated Clients: ").append(adHocAssociations.getClients());
		assoInfoBuf.append("\nDeleted Clients: ").append(adHocAssociations.getDeletedClients());
		log.info("debugAdHocAssociationAssociationAssociation", assoInfoBuf.toString());
	}

}