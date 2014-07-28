package com.ah.hm.ws.platform.client;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

import com.ah.hm.ws.platform.client.HmServiceStub.HiveAp;
import com.ah.hm.ws.platform.client.HmServiceStub.Passport;
import com.ah.hm.ws.platform.client.HmServiceStub.Session;
import com.ah.util.http.XTrustProvider;

public class HmWsClient {

	public HmWsClient() {

	}

	public HmServiceStub login(String endpointAddress, Passport passport) throws RemoteException, SoapFault {
		HmServiceStub stub = new HmServiceStub(endpointAddress);
		Session session = stub.login(passport);
		System.out.println("Version: "+ session.getVersion());

		return stub;
	}
	
	public void keepSessionAlive(HmServiceStub stub) throws RemoteException, SoapFault {
		stub.keepSessionAlive();
	}

	public void logout(HmServiceStub stub) throws RemoteException, SoapFault {
		stub.logout();
	}

	public void fetchAllHiveAps(HmServiceStub stub) throws RemoteException, SoapFault {
		HiveAp[] hiveAps = stub.fetchAllHiveAps();

		if (hiveAps != null) {
			for (HiveAp hiveAp : hiveAps) {
				printHiveAp(hiveAp);
			}
		}
	}

	public void fetchHiveApsByNodeIds(HmServiceStub stub, String[] nodeIds) throws RemoteException, SoapFault {
		HiveAp[] hiveAps = stub.fetchHiveApsByNodeIds(nodeIds);

		if (hiveAps != null) {
			for (HiveAp hiveAp : hiveAps) {
				printHiveAp(hiveAp);
			}
		} else {
			System.out.println("None HiveAPs fetched.");
		}
	}

	public void fetchHiveApByNodeId(HmServiceStub stub, String nodeId) throws RemoteException, SoapFault {
		HiveAp hiveAp = stub.fetchHiveApByNodeId(nodeId);

		if (hiveAp != null) {
			printHiveAp(hiveAp);
		} else {
			System.out.println("No such HiveAP fetched.");
		}
	}

	public void fetchHiveApsByClientMacs(HmServiceStub stub, String[] clientMacs) throws RemoteException, SoapFault {
		HiveAp[] hiveAps = stub.fetchHiveApsByClientMacs(clientMacs);

		if (hiveAps != null) {
			for (HiveAp hiveAp : hiveAps) {
				printHiveAp(hiveAp);
			}
		} else {
			System.out.println("None HiveAPs fetched.");
		}
	}

	public void fetchHiveApByClientMac(HmServiceStub stub, String clientMac) throws RemoteException, SoapFault {
		HiveAp hiveAp = stub.fetchHiveApByClientMac(clientMac);

		if (hiveAp != null) {
			printHiveAp(hiveAp);
		} else {
			System.out.println("No such HiveAP fetched.");
		}
	}

	public void fetchHiveApsByClientNames(HmServiceStub stub, String[] clientNames) throws RemoteException, SoapFault {
		HiveAp[] hiveAps = stub.fetchHiveApsByClientNames(clientNames);

		if (hiveAps != null) {
			for (HiveAp hiveAp : hiveAps) {
				printHiveAp(hiveAp);
			}
		} else {
			System.out.println("None HiveAPs fetched.");
		}
	}

	public void fetchHiveApByClientName(HmServiceStub stub, String clientName) throws RemoteException, SoapFault {
		HiveAp hiveAp = stub.fetchHiveApByClientName(clientName);

		if (hiveAp != null) {
			printHiveAp(hiveAp);
		} else {
			System.out.println("No such HiveAP fetched.");
		}
	}

	private void printHiveAp(HiveAp hiveAp) {
		StringBuilder sb = new StringBuilder("\nHiveAP: ")
				.append("\nNode ID: ").append(hiveAp.getNodeId())
				.append("\nSerial Number: ").append(hiveAp.getSerialNumber())
				.append("\nHost Name: ").append(hiveAp.getHostName())
				.append("\nIp Address: ").append(hiveAp.getIpAddress())
				.append("\nNetmask: ").append(hiveAp.getNetmask())
				.append("\nGateway: ").append(hiveAp.getGateway())
				.append("\nSoftware Version: ").append(hiveAp.getSoftwareVersion())
				.append("\nNative Vlan: ").append(hiveAp.getNativeVlan())
				.append("\nMgt Vlan: ").append(hiveAp.getMgtVlan())
				.append("\nActive: ").append(hiveAp.getActive())
				.append("\nDevice Group: ").append(hiveAp.getDeviceGroup())
				.append("\nModel: ").append(hiveAp.getModel())
				.append("\nType: ").append(hiveAp.getType());

		System.out.println(sb.toString());
	}

	public static void main(String[] args) {
		String endpointAddress = "https://localhost:8443/hm/services/HmService";
		HmWsClient client = new HmWsClient();
		String userName = "admin";
		String password = "aerohive";
		int sessionTimeout = 1 * 60 * 60;
		Passport passport = new Passport();
		passport.setUserName(userName);
		passport.setPassword(password);
		passport.setSessionTimeout(sessionTimeout);

		// Since Axis2 doesn't work with self-signed servers, it needs to use XTrustProvider to bypass the server validation.
		XTrustProvider.install();

		try {
			HmServiceStub stub = client.login(endpointAddress, passport);
			
			client.fetchAllHiveAps(stub);

			Collection<String> nodeIds = new ArrayList<String>(1);
			nodeIds.add("00197703EC00");
			client.fetchHiveApsByNodeIds(stub, nodeIds.toArray(new String[nodeIds.size()]));

			client.fetchHiveApByNodeId(stub, "00197703EC00");

			Collection<String> clientMacs = new ArrayList<String>(1);
			clientMacs.add("0CEEE69AE1B3");
			client.fetchHiveApsByClientMacs(stub, clientMacs.toArray(new String[clientMacs.size()]));

			client.fetchHiveApByClientMac(stub, "0CEEE69AE1B3");

			Collection<String> clientNames = new ArrayList<String>(1);
			clientNames.add("ychen");
			client.fetchHiveApsByClientNames(stub, clientNames.toArray(new String[clientNames.size()]));

			client.fetchHiveApByClientName(stub, "ychen");

			client.keepSessionAlive(stub);
			client.logout(stub);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}