package com.ah.test;

import java.util.ArrayList;
import java.util.List;

import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.NetworkService;
import com.ah.bo.network.SingleTableItem;
import com.ah.test.util.HmTest;
import com.ah.util.Tracer;

public class NetObjectsTest extends HmTest {
	private static final Tracer log = new Tracer(NetObjectsTest.class
			.getSimpleName());

	public void run() {
		try {
			// createServices();
			createNetObjects();
		} catch (Exception ex) {
			log.error(this.getClass().getName() + ".run()", ex.getMessage());
		}
	}

	protected void createNetObjects() throws Exception {
		IpAddress ipAddress = new IpAddress();
		ipAddress.setAddressName("ip1");		
		List<SingleTableItem> items = new ArrayList<SingleTableItem>();
		SingleTableItem single = new SingleTableItem();
		single.setIpAddress("10.3.101.45");
		single.setNetmask("255.255.255.0");
		single.setType(SingleTableItem.TYPE_GLOBAL);
		items.add(single);
		ipAddress.setItems(items);		
		QueryUtil.createBo(ipAddress);
		
		ipAddress = new IpAddress();
		ipAddress.setAddressName("ip2");	
		items = new ArrayList<SingleTableItem>();
		single = new SingleTableItem();
		single.setIpAddress("10.3.101.75");
		single.setNetmask("255.255.255.0");
		single.setType(SingleTableItem.TYPE_GLOBAL);
		items.add(single);
		ipAddress.setItems(items);
		QueryUtil.createBo(ipAddress);
	}

	protected void createServices() throws Exception {
		NetworkService networkService = new NetworkService();
		networkService.setServiceName("DHCP-Relay");
		networkService.setProtocolId(NetworkService.PROTOCOL_ID_UDP);
		networkService.setPortNumber(67);
		networkService.setIdleTimeout(60);
		networkService.setDescription("DHCP Relay Agent");
		createService(networkService);
		networkService = new NetworkService();
		networkService.setServiceName("DNS");
		networkService.setProtocolId(NetworkService.PROTOCOL_ID_UDP);
		networkService.setPortNumber(53);
		networkService.setIdleTimeout(60);
		networkService.setDescription("Domain Name Service");
		createService(networkService);
		networkService = new NetworkService();
		networkService.setServiceName("FTP");
		networkService.setProtocolId(NetworkService.PROTOCOL_ID_TCP);
		networkService.setPortNumber(21);
		networkService.setIdleTimeout(1800);
		networkService.setDescription("File Transfer Protocol");
		createService(networkService);
		networkService = new NetworkService();
		networkService.setServiceName("RADIUS-AUTH");
		networkService.setProtocolId(NetworkService.PROTOCOL_ID_UDP);
		networkService.setPortNumber(1812);
		networkService.setIdleTimeout(1800);
		networkService.setDescription("Radius Authentication");
		networkService = new NetworkService();
		networkService.setServiceName("RADIUS-ACCT");
		networkService.setProtocolId(NetworkService.PROTOCOL_ID_UDP);
		networkService.setPortNumber(1813);
		networkService.setIdleTimeout(1800);
		networkService.setDescription("Radius Accounting");
		createService(networkService);
		networkService = new NetworkService();
		networkService.setServiceName("VOIP-SVP");
		networkService.setProtocolId(NetworkService.PROTOCOL_ID_SVP);
		networkService.setIdleTimeout(1800);
		networkService.setDescription("VOIP based on SVP Protocol");
		createService(networkService);
		networkService = new NetworkService();
		networkService.setServiceName("RLOGIN");
		networkService.setProtocolId(NetworkService.PROTOCOL_ID_TCP);
		networkService.setPortNumber(513);
		networkService.setIdleTimeout(1800);
		networkService.setDescription("VOIP based on SVP Protocol");
		createService(networkService);
		networkService = new NetworkService();
		networkService.setServiceName("RSH");
		networkService.setProtocolId(NetworkService.PROTOCOL_ID_TCP);
		networkService.setPortNumber(514);
		networkService.setIdleTimeout(1800);
		networkService.setDescription("Remote shell");
		createService(networkService);
		networkService = new NetworkService();
		networkService.setServiceName("SNMP");
		networkService.setProtocolId(NetworkService.PROTOCOL_ID_UDP);
		networkService.setPortNumber(161);
		networkService.setIdleTimeout(60);
		networkService.setDescription("Simple Management Protocol");
		createService(networkService);
		networkService = new NetworkService();
		networkService.setServiceName("PC-Anywhere");
		networkService.setProtocolId(NetworkService.PROTOCOL_ID_UDP);
		networkService.setPortNumber(5632);
		networkService.setIdleTimeout(60);
		networkService.setDescription("Software tools");
		createService(networkService);
	}

	static int protocolNumber = 1;

	protected void createService(NetworkService networkService)
			throws Exception {
		networkService.setProtocolNumber(protocolNumber++);
		networkService.setPortNumber(protocolNumber++);
		QueryUtil.createBo(networkService);
	}
}
