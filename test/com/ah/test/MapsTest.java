package com.ah.test;

/*
 * @author Chris Scheers
 */

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.admin.adminOperateImpl.BeVersionInfo;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.parameter.BeParaModule;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.HmBo;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.Idp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.MapMgmt;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapLeafNode;
import com.ah.bo.monitor.MapLink;
import com.ah.bo.performance.AhClientDetected;
import com.ah.bo.wlan.RadioProfile;
import com.ah.events.BoEvent;
import com.ah.events.BoEvent.BoEventType;
import com.ah.test.util.HmTest;
import com.ah.util.EnumConstUtil;
import com.ah.util.Tracer;
import com.ah.util.coder.AhDecoder;

public class MapsTest extends HmTest implements QueryBo {
	private static final Tracer log = new Tracer(MapsTest.class.getSimpleName());

	public Collection<HmBo> load(HmBo bo) {
		log.info("run", "# child nodes: "
				+ ((MapContainerNode) bo).getChildNodes().size());
		log.info("run", "# child links: "
				+ ((MapContainerNode) bo).getChildLinks().values().size());
		return null;
	}

	private final BeVersionInfo versionInfo;

	public MapsTest() {
		this.versionInfo = NmsUtil.getVersionInfo();
	}

	public void run() {
		try {
			// BoMgmt.removeAllBos(MapNode.class, null, null);
			// BoMgmt.getMapMgmt().init();
			MapContainerNode rootMap = BoMgmt.getMapMgmt().getRootMap();
			MapContainerNode map1 = (MapContainerNode) rootMap.getChildNodes()
					.iterator().next();
			map1 = (MapContainerNode) QueryUtil.updateBo(map1, this).iterator()
					.next();

			// MapLeafNode node1 = createLeafNode(map1, 1743, 208,
			// "001977004024",null);
			// MapLeafNode node2 = createLeafNode(map1, 1118, 246);
			// MapLeafNode node3 = createLeafNode(map1, 1099, 554);
			// MapLeafNode node4 = createLeafNode(map1, 1500, 471);
			// createLink(map1, node1, node4);
			// createLink(map1, node1, node2);
			// createLink(map1, node3, node4);
			// createLink(map1, node2, node3);
			MapContainerNode map2 = createContainerNode(map1, "US",
					EnumConstUtil.MAP_ENV_OUTDOOR_FREE_SPACE, "us.png",
					2098000, 1183000, 438, 372, 0);
			MapContainerNode map4 = createContainerNode(map2, "First Floor",
					EnumConstUtil.MAP_ENV_ENTERPRISE, "map_floorplan.png",
					2216000, 1559000, 304000, 408000, 0);
			MapContainerNode map4a = createContainerNode(map2, "Second Floor",
					EnumConstUtil.MAP_ENV_ENTERPRISE, "map_floorplan.png",
					2216000, 1559000, 304000, 408000, 105);
			MapLeafNode map4aLeaf = createLeafNode(map4a, 800000, 252000);
			createLink(map4a, map4aLeaf, createLeafNode(map4a, 1733000, 667000));
			map4aLeaf = createLeafNode(map4a, 835000, 1072000);
			map4aLeaf = createLeafNode(map4a, 321000, 800000);
			map4aLeaf = createLeafNode(map4a, 1905000, 1267000);
			QueryUtil.updateBo(map4a);
			MapContainerNode map4b = createContainerNode(map2, "Third Floor",
					EnumConstUtil.MAP_ENV_ENTERPRISE, "map_floorplan.png",
					2216, 1559, 304000, 408000, 61);
			String clientMac = "022732801040";
			Date lastDetectedTime = new Date();
			// client.setStartTime(new Date());
			// QueryUtil.createBo(client);
			createRogueAP(clientMac, lastDetectedTime, "00"
					+ (1977004020 + nodeCount));
			createClientDetected(clientMac, "00" + (1977004020 + nodeCount),
					lastDetectedTime, -62);
			MapLeafNode map4bLeaf = createLeafNode(map4b, 552, 385,
					"AH-0015a0-L1");
			createClientDetected(clientMac, "00" + (1977004020 + nodeCount),
					lastDetectedTime, -49);
			map4bLeaf = createLeafNode(map4b, 1640, 636, "AH-0015a0-L2");
			createClientDetected(clientMac, "00" + (1977004020 + nodeCount),
					lastDetectedTime, -54);
			map4bLeaf = createLeafNode(map4b, 954, 1077, "AH-0015a0-L3");
			QueryUtil.updateBo(map4b);
			// MapLeafNode node5 = createExtNode(map1, map4);
			// createLink(map1, node3, node5);
			QueryUtil.updateBo(map1);
			MapLeafNode map5 = createLeafNode(map4, 1043000, 1030000);
			MapLeafNode map6 = createLeafNode(map4, 1937000, 525000);
			MapLeafNode map7 = createLeafNode(map4, 988000, 1435000);
			MapLeafNode map8 = createLeafNode(map4, 586000, 194000);
			createLink(map4, map5, map6);
			createLink(map4, map5, map7);
			createLink(map4, map5, map8);
			// createLink(map1, node3, node5);
			// MapLeafNode map9 = createExtNode(map4, map1);
			// createLink(map4, map9, map8);
			QueryUtil.updateBo(map4);
			MapContainerNode map3 = createContainerNode(map2, "Santa Clara",
					EnumConstUtil.MAP_ENV_OUTDOOR_FREE_SPACE, "campus.png", 1817000,
					1133000, 415000, 620000, 315);
			createManyLinks(map3, createManyNodes(map3, 1817000, 1133000));
		} catch (Exception e) {
			log.error("run", "Test failed: ", e);
		}
	}

	public void createRogueAP(String clientMac, Date lastDetectedTime,
			String apMac) throws Exception {
		/*-		HiveAp hiveAp = new HiveAp();
		 hiveAp.setOwner(BoMgmt.getDomainMgmt().getHomeDomain());
		 hiveAp.setManageStatus(HiveAp.STATUS_ROGUE);
		 hiveAp.setMacAddress(clientMac);
		 hiveAp.setHostName("10.30.5.200");
		 hiveAp.setLastDetectedTime(lastDetectedTime);
		 hiveAp.setIpAddress("10.30.5.200");
		 WirelessIDS ids = new WirelessIDS();
		 ids.setSsid("veriwave-psk");
		 List<WirelessIDS> details = new ArrayList<WirelessIDS>();
		 details.add(ids);
		 hiveAp.setIdpDetails(details);
		 */
		Calendar c = Calendar.getInstance();
		Idp idp = new Idp();
		idp.setOwner(BoMgmt.getDomainMgmt().getHomeDomain());
		idp.setStationType(BeCommunicationConstant.IDP_STATION_TYPE_AP);
		idp.setIdpType(BeCommunicationConstant.IDP_TYPE_ROGUE);
		idp.setIfMacAddress(clientMac);
		idp.setReportTime(HmTimeStamp.getTimeStamp(lastDetectedTime.getTime(),
				(byte) ((c.get(Calendar.ZONE_OFFSET) + c
						.get(Calendar.DST_OFFSET)) / (60 * 60 * 1000))));
		idp.setReportNodeId(apMac);
		idp.setSsid("veriwave-psk");
		QueryUtil.createBo(idp);
	}

	public void createClientDetected(String clientMac, String apMac,
			Date detectedTime, int rssi) throws Exception {
		AhClientDetected detected = new AhClientDetected();
		detected.setClientMac(clientMac);
		detected.setDetectedTime(detectedTime);
		detected.setRssi(rssi);
	}

	int columns = 20;

	int rows = 5;

	private Map<String, MapLeafNode> createManyNodes(MapContainerNode map,
			int width, int height) throws Exception {
		Map<String, MapLeafNode> nodes = new HashMap<String, MapLeafNode>();
		long stepX = 1100000 / columns;
		long stepY = Math.round(stepX * 2.5);
		double margin = 0.05;
		long marginX = Math.round(width * margin);
		long marginY = Math.round(height * margin);
		long usableWidth = width - 2 * marginX;
		long usableHeight = height - 2 * marginY;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				// On a grid
				long x = (j + 3) * stepX;
				long y = (i + 1) * stepY;
				// Random placement
				x = marginX + Math.round(Math.random() * usableWidth);
				y = marginY + Math.round(Math.random() * usableHeight);
				MapLeafNode node = createLeafNode(map, x, y);
				nodes.put("r" + i + "c" + j, node);
			}
		}
		return nodes;
	}

	private void createManyLinks(MapContainerNode map,
			Map<String, MapLeafNode> nodes) throws Exception {
		int degree = 5;
		for (int j = 0; j < columns; j++) {
			String fromNodeId = "r1c" + (j / degree * degree + 1);
			String toNodeId = "r0" + "c" + j;
			MapLeafNode fromNode = nodes.get(fromNodeId);
			MapLeafNode toNode = nodes.get(toNodeId);
			createLink(map, fromNode, toNode);
		}
		for (int j = 0; j < columns; j++) {
			String fromNodeId = "r2c" + (j / degree * degree + 2);
			String toNodeId = "r1" + "c" + j;
			MapLeafNode fromNode = nodes.get(fromNodeId);
			MapLeafNode toNode = nodes.get(toNodeId);
			createLink(map, fromNode, toNode);
		}
		for (int j = 0; j < columns; j++) {
			String fromNodeId = "r3c" + (j / degree * degree + 3);
			String toNodeId = "r2" + "c" + j;
			MapLeafNode fromNode = nodes.get(fromNodeId);
			MapLeafNode toNode = nodes.get(toNodeId);
			createLink(map, fromNode, toNode);
		}
		for (int j = 0; j < columns; j++) {
			String fromNodeId = "r3c10";
			String toNodeId = "r4" + "c" + j;
			MapLeafNode fromNode = nodes.get(fromNodeId);
			MapLeafNode toNode = nodes.get(toNodeId);
			createLink(map, fromNode, toNode);
		}
		for (int j = 0; j < columns; j += 4) {
			for (int i = 0; i < 4; i++) {
				String fromNodeId = "r" + i + "c" + j;
				String toNodeId = "r" + (i + 1) + "c" + j;
				MapLeafNode fromNode = nodes.get(fromNodeId);
				MapLeafNode toNode = nodes.get(toNodeId);
				createLink(map, fromNode, toNode);
			}
		}
		BoMgmt.updateBo(map, null, null);
	}

	private MapContainerNode createContainerNode(MapContainerNode parent,
			String mapName, int mapEnv, String background, double width,
			double height, double x, double y, double actualWidth)
			throws Exception {
		MapContainerNode node = new MapContainerNode();
		node.setParentMap(parent);
		node.setOwner(parent.getOwner());
		node.setMapName(mapName);
		node.setEnvironment(mapEnv);
		node.setBackground(background);
		node.setWidth(width);
		node.setHeight(height);
		if (actualWidth == 0) {
			node.setActualWidth(0);
			node.setActualHeight(0);
		} else {
			node.setActualWidth(actualWidth);
			node.setActualHeight(actualWidth * height / width);
		}
		node.setX(x);
		node.setY(y);
		node.setIconName(BeTopoModuleUtil.getMapIcons().get(
				(int) Math.round(Math.random() * 6))[0]);
		QueryUtil.createBo(node);
		BoMgmt.getBoEventMgmt().publishBoEvent(
				new BoEvent(node, BoEventType.CREATED));
		return node;
	}

	static int nodeCount = 0;

	private MapLeafNode createExtNode(MapContainerNode parent,
			MapContainerNode mapContainerNode) throws Exception {
		MapLeafNode node = new MapLeafNode();
		node.setX(parent.getWidth() * 0.03);
		node.setY(parent.getHeight() * 0.2 + Math.random() * parent.getHeight()
				* 0.6);
		node.setIconName(MapMgmt.BASE_LEAFNODE_ICON);
		node.setParentMap(parent);
		node.setOwner(parent.getOwner());
		node.setApId("M" + mapContainerNode.getId());
		// Shown as the node label
		node.setApName(mapContainerNode.getMapName());
		Long nodeId = QueryUtil.createBo(node);
		node = (MapLeafNode) QueryUtil.findBoById(MapLeafNode.class, nodeId);
		return node;
	}

	private MapLeafNode createLeafNode(MapContainerNode parent, double x,
			double y) throws Exception {
		return createLeafNode(parent, x, y, null);
	}

	private MapLeafNode createLeafNode(MapContainerNode parent, double x,
			double y, String apName) throws Exception {
		return createLeafNode(parent, x, y, "00" + (1977004020 + nodeCount++),
				apName);
	}

	private MapLeafNode createLeafNode(MapContainerNode parent, double x,
			double y, String apId, String apName) throws Exception {
		MapLeafNode node = new MapLeafNode();
		node.setX(x);
		node.setY(y);
		node.setIconName(MapMgmt.BASE_LEAFNODE_ICON);
		node.setFetchLinksTimeout(false);
		if (apName == null) {
			apName = "AH-0015a0-" + nodeCount++;
		}
		HiveAp hiveAp = createHiveAP(apName, apId);
		Long hiveApId = QueryUtil.createBo(hiveAp);
		hiveAp.setId(hiveApId);
		BoMgmt.getMapMgmt().createMapLeafNode(hiveAp, node, parent);
		return node;
	}

	protected HiveAp createHiveAP(String hiveApName, String macAddress)
			throws Exception {
		HiveAp hiveAp = new HiveAp();
		hiveAp.setOwner(BoMgmt.getDomainMgmt().getHomeDomain());
		List<?> configTemplates = QueryUtil.executeQuery(ConfigTemplate.class,
				null, new FilterParams("configName",
						BeParaModule.DEFAULT_DEVICE_GROUP_NAME));
		if (configTemplates.size() > 0) {
			hiveAp.setConfigTemplate((ConfigTemplate) configTemplates.get(0));
		}
		hiveAp.setHostName(hiveApName);
		hiveAp.setMacAddress(macAddress);
		hiveAp.setIpAddress(AhDecoder.int2IP((int) Math.round(Math.random() * 1000000000)));
		hiveAp.setNetmask("255.255.255.0");
		hiveAp.setSerialNumber("SN " + Math.round(Math.random() * 1000000000));
		hiveAp.setLocation("First floor");
		hiveAp.setWifi0RadioProfile(null);
		hiveAp.setWifi1RadioProfile(null);
		hiveAp.setPassPhrase(NmsUtil.generatePassphrase());
		hiveAp.setSoftVer(NmsUtil.getHiveOSVersion(versionInfo));
		List<?> radioProfiles = QueryUtil.executeQuery(RadioProfile.class,
				null, new FilterParams("radioName",
						BeParaModule.DEFAULT_RADIO_PROFILE_NAME_A));
		if (radioProfiles.size() > 0) {
			hiveAp.setWifi0RadioProfile((RadioProfile) radioProfiles.get(0));
		}
		radioProfiles = QueryUtil.executeQuery(RadioProfile.class, null,
				new FilterParams("radioName",
						BeParaModule.DEFAULT_RADIO_PROFILE_NAME_BG));
		if (radioProfiles.size() > 0) {
			hiveAp.setWifi1RadioProfile((RadioProfile) radioProfiles.get(0));
		}
		hiveAp.setNativeVlan(6);
		return hiveAp;
	}

	private MapLink createLink(MapContainerNode parent, MapLeafNode fromNode,
			MapLeafNode toNode) {
		MapLink mapLink = new MapLink();
		mapLink.setFromNode(fromNode);
		mapLink.setToNode(toNode);
		parent.addLink(mapLink);
		return mapLink;
	}

}