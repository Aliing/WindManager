package com.ah.test;

/*
 * @author Chris Scheers
 */
import java.util.Calendar;
import java.util.Date;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.hiveap.Idp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.performance.AhClientDetected;
import com.ah.test.util.HmTest;
import com.ah.util.Tracer;

public class ClientsTest extends HmTest {
	private static final Tracer log = new Tracer(ClientsTest.class
			.getSimpleName());

	public void run() {
		try {
			String apMac = "001977001460";
			createRogueClients(apMac);
			createRogueAPs(apMac);
		} catch (Exception e) {
			log.error("run", "Test failed: ", e);
		}
	}

	public void createRogueClients(String apMac) throws Exception {
		/*-		AhRogueClient client = new AhRogueClient();
		 client.setOwner(BoMgmt.getDomainMgmt().getHomeDomain());
		 //		client.setApName("AH-001460");
		 client.setApMac(apMac);
		 //		client.setClientIP("10.20.1.130");
		 String clientMac = "302783305560";
		 client.setClientMac(clientMac);
		 WirelessIDS ids = new WirelessIDS();
		 ids.setSsid("aero-CWP");
		 List<WirelessIDS> details = new ArrayList<WirelessIDS>();
		 details.add(ids);
		 client.setIdpDetails(details);
		 //		client.setStartTime(new Date());
		 //		client.setClientType(AhCurrentClientSession.CLIENT_TYPE_ACTIVATE);
		 Date lastDetectedTime = new Date();
		 client.setLastDetectedTime(lastDetectedTime);
		 QueryUtil.createBo(client);
		 */
		Idp idp = new Idp();
		idp.setOwner(BoMgmt.getDomainMgmt().getHomeDomain());
		idp.setStationType(BeCommunicationConstant.IDP_STATION_TYPE_CLIENT);
		idp.setIdpType(BeCommunicationConstant.IDP_TYPE_ROGUE);
		String clientMac = "302783305560";
		idp.setIfMacAddress(clientMac);
		Date lastDetectedTime = new Date();
		Calendar c = Calendar.getInstance();
		idp.setReportTime(HmTimeStamp.getTimeStamp(lastDetectedTime.getTime(), (byte)((c.get(Calendar.ZONE_OFFSET) + c.get(Calendar.DST_OFFSET)) / (60 * 60 * 1000))));
		idp.setReportNodeId(apMac);
		idp.setSsid("aero-CWP");
		QueryUtil.createBo(idp);
		createClientsDetected(clientMac, apMac, lastDetectedTime, -51, -48, -61);
	}

	public void createRogueAPs(String apMac) throws Exception {
		/*-		HiveAp hiveAp = new HiveAp();
		 hiveAp.setOwner(BoMgmt.getDomainMgmt().getHomeDomain());
		 hiveAp.setManageStatus(HiveAp.STATUS_ROGUE);
		 String mac = "041817301020";
		 hiveAp.setMacAddress(mac);
		 hiveAp.setHostName("10.30.5.200");
		 Date lastDetectedTime = new Date();
		 hiveAp.setLastDetectedTime(lastDetectedTime);
		 hiveAp.setIpAddress("10.30.5.200");
		 WirelessIDS ids = new WirelessIDS();
		 ids.setSsid("veriwave-psk");
		 List<WirelessIDS> details = new ArrayList<WirelessIDS>();
		 details.add(ids);
		 hiveAp.setIdpDetails(details);
		 QueryUtil.createBo(hiveAp);
		 */
		Idp idp = new Idp();
		idp.setOwner(BoMgmt.getDomainMgmt().getHomeDomain());
		idp.setStationType(BeCommunicationConstant.IDP_STATION_TYPE_AP);
		idp.setIdpType(BeCommunicationConstant.IDP_TYPE_ROGUE);
		String mac = "041817301020";
		idp.setIfMacAddress(mac);
		Date lastDetectedTime = new Date();
		Calendar c = Calendar.getInstance();
		idp.setReportTime(HmTimeStamp.getTimeStamp(lastDetectedTime.getTime(), (byte)((c.get(Calendar.ZONE_OFFSET) + c.get(Calendar.DST_OFFSET)) / (60 * 60 * 1000))));
		idp.setSsid("veriwave-psk");
		QueryUtil.createBo(idp);
		createClientsDetected(mac, apMac, lastDetectedTime, -58, -61, -48);
	}

	public void createClientsDetected(String clientMac, String apMac,
			Date lastDetectedTime, int rssi1, int rssi2, int rssi3)
			throws Exception {
		createClientDetected(clientMac, apMac, lastDetectedTime, rssi1);
		createClientDetected(clientMac, "001977000AE0", lastDetectedTime, rssi2);
		createClientDetected(clientMac, "001977002960", lastDetectedTime, rssi3);
		createClientDetected(clientMac, "022007020503", lastDetectedTime, -85);
	}

	public void createClientDetected(String clientMac, String apMac,
			Date detectedTime, int rssi) throws Exception {
		AhClientDetected detected = new AhClientDetected();
		detected.setClientMac(clientMac);
		detected.setDetectedTime(detectedTime);
		detected.setRssi(rssi);
	}
}