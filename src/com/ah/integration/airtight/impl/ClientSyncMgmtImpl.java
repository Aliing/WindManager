package com.ah.integration.airtight.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.airtight.spectraguard.api.datamanagers.DeviceManager;
import com.airtight.spectraguard.api.dataobjects.devices.Client;
import com.airtight.spectraguard.api.dataobjects.devices.ClientWiFiInterface;
import com.airtight.spectraguard.api.dataobjects.devices.Device;
import com.airtight.spectraguard.api.dataobjects.session.APISession;
import com.airtight.spectraguard.api.dataobjects.session.ReportingEntity;
import com.airtight.spectraguard.api.exceptions.APIException;
import com.airtight.spectraguard.api.exceptions.ProcessingException;

import com.ah.be.common.DBOperationUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.log.HmLogConst;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.performance.AhClientSession;
import com.ah.integration.airtight.ClientSyncMgmt;
import com.ah.integration.airtight.util.SgeUtil;
import com.ah.util.Tracer;

public class ClientSyncMgmtImpl implements ClientSyncMgmt {

	private static final Tracer log = new Tracer(ClientSyncMgmtImpl.class, HmLogConst.M_SGE);

	@Override
	public synchronized void syncClients(APISession session, Collection<HmDomain> hmDomains, ReportingEntity reporter) throws APIException {
		log.info("syncClients", "Synchronizing clients with SGE.");
		long startTime = System.currentTimeMillis();

		// Get HM active clients.
		List<AhClientSession> activeClients = getActiveClients(hmDomains);

		// Active client MACs holder.
		Collection<String> activeClientMacs = new ArrayList<String>(activeClients.size());

		for (AhClientSession activeClient : activeClients) {
			String activeClientMac = activeClient.getClientMac();

			if (!activeClientMacs.contains(activeClientMac)) {
				activeClientMacs.add(activeClientMac);
			}
		}

		// Get HM rogue clients.
		List<?> rogueClients = getRogueClients(hmDomains, activeClientMacs);

		// Overall HM client MACs holder.
		Collection<String> hmClientMacs = new ArrayList<String>(activeClientMacs.size() + rogueClients.size());
		hmClientMacs.addAll(activeClientMacs);

		for (Object obj : rogueClients) {
			String rogueClientMac = (String) obj;

			if (!hmClientMacs.contains(rogueClientMac)) {
				hmClientMacs.add(rogueClientMac);
			}
		}

		// Fetch SGE clients previously imported by the same HM with the same session.
		Collection<Client> fetchedSgeClients = fetchClients(session, reporter);

		// Remove identical and inactive clients from the fetched SGE client list.
		for (Iterator<Client> sgeClientIter = fetchedSgeClients.iterator(); sgeClientIter.hasNext();) {
			Client sgeClient = sgeClientIter.next();
			ClientWiFiInterface sgeClientWiFiIf = sgeClient.getWiFiInterface();

			if (sgeClientWiFiIf.isActiveStatus()) {
				String sgeClientWifiMac = sgeClientWiFiIf.getMacAddress();
				String unformatClientWifiMac = SgeUtil.getUnformattedMac(sgeClientWifiMac);

				for (String hmClientMac : hmClientMacs) {
					if (unformatClientWifiMac.equalsIgnoreCase(hmClientMac)) {
						// Remove identical client.
						sgeClientIter.remove();
						break;
					}
				}
			} else {
				// Remove inactive client.
				sgeClientIter.remove();
			}
		}

		// Create SGE clients based on HM active clients.
		Collection<Client> importedSgeClients = createClientsByClientSessions(activeClients);

		// Create SGE clients based on HM rogue clients.
		Collection<Client> sgeRogueClients = createClientsByClientMacs(rogueClients);

		if (!sgeRogueClients.isEmpty()) {
			importedSgeClients.addAll(sgeRogueClients);
		}

		if (!fetchedSgeClients.isEmpty()) {
			// Push inactive clients into the client list to be imported.
			for (Client inactiveClient : fetchedSgeClients) {
				ClientWiFiInterface inactiveClientWifiIf = inactiveClient.getWiFiInterface();
				inactiveClientWifiIf.setActiveStatus(false);
			}

			importedSgeClients.addAll(fetchedSgeClients);
		}

		if (!importedSgeClients.isEmpty()) {
			for (Client importedSgeClient : importedSgeClients) {
				if (importedSgeClient.getGroupId() != Device.CLIENT_UNCATEGORIZED_GROUP_ID) {
					importedSgeClient.setGroupId(Device.CLIENT_UNCATEGORIZED_GROUP_ID);
				}								
			}

			log.info("syncClients", "Importing number of " + importedSgeClients.size() + " clients " + importedSgeClients + " to SGE.");
			importClients(session, importedSgeClients, reporter);
		}

		long endTime = System.currentTimeMillis();
		log.info("syncClients", "It took " + (endTime - startTime) + "ms to synchronize clients with SGE.");
	}

	/**
	 * Fetch clients from SGE that were previously imported by the same HM with the same session.
	 *
	 * @param session -
	 * @return a list of clients imported by the same HM with the same session.
	 * @throws APIException -
	 */
	@Override
	public Collection<Client> fetchClients(APISession session, ReportingEntity reporter) throws APIException {
		log.info("fetchClients", "Fetching clients from SGE.");
		DeviceManager devMgr = new DeviceManager(session);
		Collection<Client> fetchedClients;

		try {
			fetchedClients = devMgr.getClients(reporter);
		} catch (ProcessingException pe) {
			String errorMsg = pe.getMessage();

			if (errorMsg != null && errorMsg.toLowerCase().contains(ApSyncMgmtImpl.ERROR_MSG_ERROR_FETCHING_DEVICES)) {
				fetchedClients = new ArrayList<Client>(0);
			} else {
				throw pe;
			}
		}

		log.info("fetchClients", "Fetched number of " + fetchedClients.size() + " clients " + fetchedClients + " from SGE.");
		return fetchedClients;
	}

    /**
     * Get HM active clients.
	 *
	 * @param hmDomains -
     * @return a list of AhClientSessions.
     */
    private List<AhClientSession> getActiveClients(Collection<HmDomain> hmDomains) {
		log.info("getActiveClients", "Getting HM active clients.");
//		List<AhClientSession> activeClients = !hmDomains.isEmpty() ? QueryUtil
//				.executeQuery(
//						AhClientSession.class,
//						null,
//						new FilterParams(
//								"owner in (:s1) and clientchannel > :s2 and connectstate = :s3 and simulated = :s4",
//								new Object[] { hmDomains, 0,
//										AhClientSession.CONNECT_STATE_UP, false }))
//				: new ArrayList<AhClientSession>(0);
		List<AhClientSession> activeClients;

		if (!hmDomains.isEmpty()) {
			Collection<Long> domainIds = new ArrayList<Long>(hmDomains.size());
			StringBuilder where = new StringBuilder("owner in (");

			for (Iterator<HmDomain> domainIter = hmDomains.iterator(); domainIter.hasNext();) {
				HmDomain hmDomain = domainIter.next();
				Long domainId = hmDomain.getId();
				domainIds.add(domainId);
				where.append("?");

				if (domainIter.hasNext()) {
					where.append(",");
				}
			}

			where.append(") and clientchannel > ? and connectstate = ? and simulated = ?");

			Collection<Object> paraList = new ArrayList<Object>(4);
			paraList.addAll(domainIds);
			paraList.add(0);
			paraList.add(AhClientSession.CONNECT_STATE_UP);
			paraList.add(false);
			activeClients = DBOperationUtil.executeQuery(AhClientSession.class, null, new FilterParams(where.toString(), paraList.toArray()));
		} else {
			activeClients = new ArrayList<AhClientSession>(0);
		}
		
		log.info("getActiveClients", "Searched number of " + activeClients.size() + " HM active clients " + activeClients);
        return activeClients;
    }

    /**
     * Get HM rouge clients.
	 *
	 * @param hmDomains -
	 * @param excludedClientMacs a collection client MAC addresses any of which should not be in the rogue client MAC address list to be searched.
     * @return a list of rogue client MAC addresses.
     */
    private List<?> getRogueClients(Collection<HmDomain> hmDomains, Collection<String> excludedClientMacs) {
		log.info("getRogueClients", "Getting HM rogue clients.");
		StringBuilder inClauseForDomainIds = new StringBuilder();

		for (Iterator<HmDomain> hmDomainIter = hmDomains.iterator(); hmDomainIter.hasNext();) {
			HmDomain hmDomain = hmDomainIter.next();
			Long domainId = hmDomain.getId();
			inClauseForDomainIds.append(domainId);

			if (hmDomainIter.hasNext()) {
				inClauseForDomainIds.append(",");
			}
		}

		List<?> rogueClients;

		if (excludedClientMacs != null && !excludedClientMacs.isEmpty()) {
			StringBuilder inClauseForExcludedClientMacs = new StringBuilder();

			for (Iterator<String> excludedClientMacIter = excludedClientMacs.iterator(); excludedClientMacIter.hasNext();) {
				String excludedClientMac = excludedClientMacIter.next();
				inClauseForExcludedClientMacs.append("'").append(excludedClientMac).append("'");
	
				if (excludedClientMacIter.hasNext()) {
					inClauseForExcludedClientMacs.append(",");
				}
			}

			rogueClients = QueryUtil.executeNativeQuery("select distinct on (ifMacAddress) ifMacAddress from idp where owner in (" + inClauseForDomainIds.toString() + ") and stationType = " + BeCommunicationConstant.IDP_STATION_TYPE_CLIENT + " and simulated = false and ifMacAddress not in (" + inClauseForExcludedClientMacs.toString() + ") order by ifMacAddress, time desc");
		} else {
			rogueClients = QueryUtil.executeNativeQuery("select distinct on (ifMacAddress) ifMacAddress from idp where owner in (" + inClauseForDomainIds.toString() + ") and stationType = " + BeCommunicationConstant.IDP_STATION_TYPE_CLIENT + " and simulated = false order by ifMacAddress, time desc");	
		}

		log.info("getRogueClients", "Searched number of " + rogueClients.size() + " HM rogue clients " + rogueClients);
        return rogueClients;
    }

	private Collection<Client> createClientsByClientSessions(Collection<AhClientSession> clientSessions) {
		Collection<Client> clients = new ArrayList<Client>(clientSessions.size());

		for (AhClientSession clientSession : clientSessions) {
			Client client = createClient(clientSession);

			if (client != null) {
				clients.add(client);
			}
		}

		return clients;
	}

	private Collection<Client> createClientsByClientMacs(Collection<?> clientMacs) {
		Collection<Client> clients = new ArrayList<Client>(clientMacs.size());

		for (Object obj : clientMacs) {
			String clientMac = (String) obj;
			Client client = createClient(clientMac);

			if (client != null) {
				clients.add(client);
			}
		}

		return clients;
	}

	private Client createClient(AhClientSession clientSession) {
		// MAC Address
		String macAddress = SgeUtil.getSgeFormatMac(clientSession.getClientMac());

		// IP Address
		String ipAddress = clientSession.getClientIP();

		if (ipAddress == null || ipAddress.trim().equals("0.0.0.0")) {
			ipAddress = "";
		}

		// Activity Status
		boolean activeStatus = true;

		// Vendor Name
		String vendorName = "";

		// User Name
		String userName = clientSession.getClientUsername();

		// Client Name
		String clientName = clientSession.getClientHostname();

		if (clientName == null || clientName.trim().isEmpty()) {
			clientName = SgeUtil.getDeviceName(vendorName, macAddress);
		}

		ClientWiFiInterface clientWifiIf = new ClientWiFiInterface(macAddress);
		clientWifiIf.setActiveStatus(activeStatus);

		// Group ID
		int groupId = Device.CLIENT_UNCATEGORIZED_GROUP_ID;

		Client client = new Client(clientWifiIf);
		client.setName(clientName);
		client.setGroupId(groupId);
		client.setIpAddress(ipAddress);
		client.setVendorName(vendorName);

		if (userName != null && !userName.isEmpty()) {
			client.setUserName(userName);
		}

		log.info("createClient", clientSession.getClientMac() + " - Client creation with " + AhClientSession.class.getSimpleName() + " succeeded.");
		return client;
	}

	private Client createClient(String clientMac) {
		// MAC Address
		String macAddress = SgeUtil.getSgeFormatMac(clientMac);

		// IP Address
		String ipAddress = "";

		// Activity Status
		boolean activeStatus = true;

		// Vendor Name
		String vendorName = "";

		// Client Name
		String clientName = SgeUtil.getDeviceName(vendorName, macAddress);

		ClientWiFiInterface clientWifiIf = new ClientWiFiInterface(macAddress);
		clientWifiIf.setActiveStatus(activeStatus);

		// Group ID
		int groupId = Device.CLIENT_UNCATEGORIZED_GROUP_ID;

		Client client = new Client(clientWifiIf);
		client.setName(clientName);
		client.setGroupId(groupId);
		client.setIpAddress(ipAddress);
		client.setVendorName(vendorName);

		log.info("createClient", clientMac + " - Client creation succeeded.");
		return client;
	}

    public void importClients(APISession session, Collection<Client> clients, ReportingEntity reporter) throws APIException {
        if (clients == null || clients.isEmpty()) {
			return;
        }

//		log.info("importClients", "Importing " + clients + " clients to SGE.");
		boolean succ = false;
		DeviceManager devMgr = new DeviceManager(session);

		try {
			devMgr.addOrUpdateClients(clients, reporter);
			succ = true;
		} finally {
			if (!succ) {
				debugClients(clients);
			}
		}

		log.info("importClients", "Clients import to SGE succeeded.");
    }

	private void debugClients(Collection<Client> clients) {
		for (Client client : clients) {
			debugClient(client);
		}
	}

	private void debugClient(Client client) {
		StringBuilder clientInfoBuf = new StringBuilder("\n***** Client Information *****");
		clientInfoBuf.append("\nName: ").append(client.getName());
		clientInfoBuf.append("\nIP Address: ").append(client.getIpAddress());
		clientInfoBuf.append("\nGroup ID: ").append(client.getGroupId());
		clientInfoBuf.append("\nVendor Name: ").append(client.getVendorName());
		clientInfoBuf.append("\nUser Name: ").append(client.getUserName());

		clientInfoBuf.append("\n***** Client WiFi Interface Information *****");
		ClientWiFiInterface clientWifiIf = client.getWiFiInterface();
		clientInfoBuf.append("\nMAC Address: ").append(clientWifiIf.getMacAddress());
		clientInfoBuf.append("\nProtocol: ").append(clientWifiIf.getProtocol());
		clientInfoBuf.append("\nActive Status: ").append(clientWifiIf.isActiveStatus());
		clientInfoBuf.append("\n---------------------------------------------");

		log.info("debugClient", clientInfoBuf.toString());
	}

}