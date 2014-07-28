package com.ah.test;

import java.util.List;

import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.performance.AhClientSession;
import com.ah.test.util.HmTest;
import com.ah.util.Tracer;

public class MobileUserTest extends HmTest {
	private static final Tracer log = new Tracer(MobileUserTest.class
			.getSimpleName());

	private final int nClients;

	private List<?> hiveApList;

	public MobileUserTest(int clients) {
		this.nClients = clients;
	}

	public void run() {
		try {
			getHiveAPMacList();
			addMobileUsers();
		} catch (Exception e) {
			log.debug("run", "add mobile users failed." + e.getMessage());
		}
	}

	private void getHiveAPMacList() {
		String query = "select bo.macAddress from HiveAp bo";
		hiveApList = QueryUtil.executeQuery(query, null, null);
	}

	private void addMobileUsers() throws Exception {

		for (int i = 0; i < nClients; i++) {
			String name = "FF001" + (100000000 + i);
			AhClientSession client = getClient(name);
			QueryUtil.createBo(client);
		}
	}

	private AhClientSession getClient(String name) {
		AhClientSession client = new AhClientSession();
		String apMac = getRandomHiveAPMac();
		client.setApMac(apMac);
		client.setApName("Host-" + apMac);
		client.setClientAuthMethod((byte) 1);
		client.setClientEncryptionMethod((byte) 1);
		client.setClientMac(name);
		client.setClientMACProtocol((byte) 1);
		client.setClientHostname("client-" + name);
//		client.setClientRSSI((int) (Math.random() * 100f));
		client.setClientSSID("test-ssid");
//		client.setClientLastTxRate(54);
//		client.setClientType((short) ((Math.random() * 2f) + 1));
		client.setClientUsername("user-" + name);
//		client.setStartTime(new Date());
		client.setOwner(BoMgmt.getDomainMgmt().getHomeDomain());
		return client;
	}

	private String getRandomHiveAPMac() {
		if (null != hiveApList && hiveApList.size() > 0) {
			double n = Math.random();
			int size = hiveApList.size();

			int value = (int) (new Integer(size).doubleValue() * n);

			return (String) hiveApList.get(value);
		}
		return "FFFFFFFFFFFF";
	}

}