package com.ah.ws.rest.models.hmapi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringUtils;

import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.config.hiveap.UpdateParameters;
import com.ah.be.db.configuration.ConfigurationProcessor.ConfigurationType;
import com.ah.be.db.configuration.ConfigurationResources;
import com.ah.bo.HmBo;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApUpdateResult;
import com.ah.bo.hiveap.HiveApWifi;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapNode;
import com.ah.bo.performance.AhClientSession;
import com.ah.bo.performance.AhClientUtil;
import com.ah.ui.actions.hiveap.HiveApPagingCache;
import com.ah.util.MgrUtil;
import com.ah.util.datetime.AhDateTimeUtil;
import com.ah.ws.rest.server.auth.exception.ApiException;

public class RestModelUtil {

	private static RestDeviceModel HiveApToDeviceModel(HiveAp hiveAp,
			RestDeviceModel deviceModel, Map<String, String> stageMap) {
		if (hiveAp == null || deviceModel == null) {
			return null;
		}
		deviceModel.setAudit(getAuditTitle(hiveAp, stageMap));
		deviceModel.setHostName(hiveAp.getHostName());
		deviceModel.setAlarm(MgrUtil.getEnumString("enum.severity."
				+ hiveAp.getSeverity()));
		deviceModel.setInterfaceIp(hiveAp.getIpAddress());
		deviceModel.setExternalIp(hiveAp.getCapwapClientIp());
		deviceModel.setTopologyName(hiveAp.getTopologyName());
		deviceModel.setNodeId(hiveAp.getMacAddress());
		deviceModel.setConnection(hiveAp.isConnected());
		deviceModel.setDeviceMode(hiveAp.getHiveApTypeString());
		SimpleHiveAp s_hiveAp = CacheMgmt.getInstance().getSimpleHiveAp(
				hiveAp.getMacAddress());
		int activeClients = 0;
		if (null != s_hiveAp) {
			activeClients = s_hiveAp.getActiveClientCount();
		}
		deviceModel.setClients(activeClients);
		deviceModel.setUpTime(hiveAp.getUpTimeString());
		deviceModel.setHiveOS(hiveAp.getDisplayVer());
		deviceModel.setDeviceFunciton(hiveAp.getDeviceCategory());
		deviceModel.setAppSignature(hiveAp.getSignatureVerString());
		deviceModel.setCountryCode(hiveAp.getCountryName());
		deviceModel.setDefaultGateWay(hiveAp.getGateway());
		deviceModel.setDhcpClient(hiveAp.getDhcpString());
		deviceModel.setDiscoveryTime(hiveAp.getDiscoveryTimeString());
		deviceModel.setEth0LLDPPort(hiveAp.getEth0PortIdString());
		deviceModel.setEth0LLDPSysId(hiveAp.getEth0DeviceIdString());
		deviceModel.setEth0LLDPSysName(hiveAp.getEth0SystemIdString());
		deviceModel.setEth1LLDPPort(hiveAp.getEth1PortIdString());
		deviceModel.setEth1LLDPSysId(hiveAp.getEth1DeviceIdString());
		deviceModel.setEth1LLDPSysName(hiveAp.getEth1SystemIdString());
		deviceModel.setHive(hiveAp.getHiveName());
		deviceModel.setHWModel(hiveAp.getProductName());
		String doorType = "";
		if (null != hiveAp.getIsOutdoor()) {
			boolean isOutDoor = hiveAp.getIsOutdoor();
			if (isOutDoor) {
				doorType = MgrUtil.getUserMessage("hiveAp.isOutdoor.dsp.true");
			} else {
				doorType = MgrUtil.getUserMessage("hiveAp.isOutdoor.dsp.false");
			}
		}
		deviceModel.setInOrOutDoor(doorType);
		deviceModel.setLocation(hiveAp.getLocation());
		deviceModel.setMgtVlan(hiveAp.getVlanName());
		deviceModel.setNativeVlan(hiveAp.getNativeVlanName());
		deviceModel.setNetmask(hiveAp.getNetmask());
		deviceModel.setNetworkPolicy(hiveAp.getConfigTemplateName());
		deviceModel.setSerialNumber(hiveAp.getSerialNumber());
		HiveApWifi wifi0 = hiveAp.getWifi0();
		if (null != wifi0) {
			deviceModel.setWifi0Channel(wifi0.getRunningChannel());
			deviceModel.setWifi0Power(wifi0.getRunningPower());
			deviceModel.setWifi0RadioProfile(hiveAp.getWifi0RadioProfileName());
		}
		HiveApWifi wifi1 = hiveAp.getWifi1();
		if (null != wifi1) {
			deviceModel.setWifi1Channel(wifi1.getRunningChannel());
			deviceModel.setWifi1Power(wifi1.getRunningPower());
			deviceModel.setWifi1RadioProfile(hiveAp.getWifi1RadioProfileName());
		}
		return deviceModel;
	}

	public static List<RestDeviceModel> HiveApsToDeviceModels(
			List<HiveAp> hiveAps) {
		List<RestDeviceModel> RestDeviceModelList = new ArrayList<RestDeviceModel>();
		if (null == hiveAps || hiveAps.isEmpty()) {
			return RestDeviceModelList;
		}
		HiveApPagingCache.queryLazyInfo(hiveAps);
		HiveApPagingCache.filledChannelPowers(hiveAps);
		List<String> macAddressList = new ArrayList<String>();
		Map<String, String> stageMap = new HashMap<String, String>();
		for (HiveAp hiveAp : hiveAps) {
			macAddressList.add(hiveAp.getMacAddress());
		}
		String where = "nodeId in (:s1) and result=:s2";
		List<?> stageList = QueryUtil.executeQuery("select nodeId from "
				+ HiveApUpdateResult.class.getSimpleName(), null,
				new FilterParams(where, new Object[] { macAddressList,
						UpdateParameters.UPDATE_STAGED }));
		for (Object obj : stageList) {
			String macAddress = (String) obj;
			stageMap.put(macAddress, macAddress);
		}
		for (HiveAp hiveAp : hiveAps) {
			RestDeviceModelList.add(HiveApToDeviceModel(hiveAp,
					new RestDeviceModel(), stageMap));
		}
		return RestDeviceModelList;
	}

	public static List<RestClientModel> clientsToClientModels(
			List<AhClientSession> clients) {
		List<RestClientModel> RestClientModeList = new ArrayList<RestClientModel>();
		if (null == clients || clients.isEmpty()) {
			return RestClientModeList;
		}
		AhClientUtil.setClientNatIp(clients);
		List<String> apMacList = new ArrayList<String>();
		Map<String, Object[]> apMaps = new HashMap<String, Object[]>();
		for (AhClientSession client : clients) {
			apMacList.add(client.getApMac());
		}
		FilterParams FilterParams = null;
		if (apMacList.size() > 1) {
			FilterParams = new FilterParams("apId in (:s1)",
					new Object[] { apMacList });
		} else {
			FilterParams = new FilterParams("apId =:s1",
					new Object[] { apMacList.get(0) });
		}
		List<?> queryList = QueryUtil.executeQuery("select apId, "
				+ getMapNameField() + " from " + MapNode.class.getSimpleName(),
				null, FilterParams);
		for (Object object : queryList) {
			Object[] objects = (Object[]) object;
			apMaps.put((String) objects[0], objects);
		}
		for (AhClientSession client : clients) {
			Object[] mapName = apMaps.get(client.getApMac());
			if (mapName != null) {
				client.setMapName((String) mapName[1]);
			}
		}
		for (AhClientSession client : clients) {
			RestClientModel clientModel = new RestClientModel();
			String vendor = "";
			String apMac = client.getApMac();
			if (!StringUtils.isBlank(apMac)) {
				vendor = apMac.substring(0, 6);
			}
			clientModel.setVendor(vendor);
			clientModel.setChannel(client.getClientChannelString());
			clientModel.setRSSI(client.getClientRSSI4Show());
			long startTimeStamp = client.getStartTimeStamp();
			String startTimeString = AhDateTimeUtil.getSpecifyDateTime(
					startTimeStamp,
					TimeZone.getTimeZone(client.getStartTimeZone()));
			clientModel.setReportTime(startTimeString);
			clientModel.setHealth(client.getOverallClientHealthScore());
			clientModel.setMacAddress(client.getClientMac());
			clientModel.setLocalIpAddress(client.getClientIP());
			clientModel.setNatIpAddress(client.getClientNatIP());
			clientModel.setHostName(client.getClientHostname());
			clientModel.setUserName(client.getClientUsername());
			clientModel.setClientOS(client.getClientOsInfo());
			clientModel.setLocation(client.getMapName());
			clientModel.setVlan(client.getClientVLANString());
			clientModel.setType(client.getActiveClientType());
			clientModel.setLastTowHoursData(client.getLast2HourDataString());
			clientModel.setSessionStartTime(startTimeString);
			clientModel.setDeviceName(client.getApName());
			clientModel.setSignalToNoiseRatio(client.getClientSNRShow());
			clientModel.setSSIDOrSecurityObj(client.getClientSSID());
			clientModel.setInterfaceName(client.getIfName());
			clientModel.setClientAuthMethod(client.getClientAuthMethodString());
			clientModel.setAssociationMode(client.getClientMacPtlString());
			clientModel.setBSSID(client.getClientBSSID());
			clientModel.setComment1(client.getComment1());
			clientModel.setComment2(client.getComment2());
			clientModel.setCompanyName(client.getCompanyName());
			clientModel.setDeviceMac(client.getApMac());
			clientModel.setEmailAddress(client.getEmail());
			clientModel.setEncryption(client.getClientEncryptionMethodString());
			clientModel.setUserProfileAttribute(client
					.getClientUserProfId4Show());
			clientModel.setVendorName(client.getVendorName());
			RestClientModeList.add(clientModel);
		}
		return RestClientModeList;
	}

	public static RestMapNodeModel mapNodeToMapNodeModel(
			MapContainerNode mapNode) {
		if (null == mapNode) {
			return null;
		}
		RestMapNodeModel mapNodeModel = new RestMapNodeModel();
		mapNodeModel.setId(mapNode.getId());
		if (null != mapNode.getParentMap()) {
			mapNodeModel.setParentId(mapNode.getParentMap().getId());
		}
		mapNodeModel.setMapName(mapNode.getMapName());
		mapNodeModel.setMapType(RestMapNodeModel.getMapTypeName(mapNode
				.getMapType()));
		return mapNodeModel;
	}

	public static List<RestMapNodeModel> mapNodesToMapNodeModels(
			List<MapContainerNode> mapNodes) {
		List<RestMapNodeModel> RestMapNodeModelList = new ArrayList<RestMapNodeModel>();
		if (null == mapNodes || mapNodes.isEmpty()) {
			return RestMapNodeModelList;
		}
		for (MapContainerNode mapNode : mapNodes) {
			RestMapNodeModel RestMapNodeModel = mapNodeToMapNodeModel(mapNode);
			if (null != RestMapNodeModel) {
				RestMapNodeModelList.add(RestMapNodeModel);
			}
		}
		return buildListToTree(RestMapNodeModelList);
	}

	public static RestErrorMsgModel exceptionToErrorMsgModel(ApiException ex) {
		RestErrorMsgModel error = new RestErrorMsgModel();
		error.setStatus(ex.getStatus());
		error.setCode(ex.getCode());
		error.setMessage(ex.getMessage());
		error.setErrorParams(ex.getErrorParams());
		error.setMoreInfo(ex.getMoreInfo());
		return error;
	}

	private static List<RestMapNodeModel> buildListToTree(
			List<RestMapNodeModel> allNotes) {
		List<RestMapNodeModel> roots = findRoots(allNotes);
		List<RestMapNodeModel> notRoots = new ArrayList<RestMapNodeModel>();
		notRoots.addAll(allNotes);
		notRoots.removeAll(roots);
		for (RestMapNodeModel root : roots) {
			root.setChildren(findChildren(root, notRoots));
		}
		return roots;
	}

	private static List<RestMapNodeModel> findRoots(
			List<RestMapNodeModel> allNodes) {
		List<RestMapNodeModel> results = new ArrayList<RestMapNodeModel>();
		for (RestMapNodeModel node : allNodes) {
			boolean isRoot = true;
			for (RestMapNodeModel comparedOne : allNodes) {
				if (node.getParentId() == comparedOne.getId()) {
					isRoot = false;
					break;
				}
			}
			if (isRoot) {
				results.add(node);
			}
		}
		return results;
	}

	public static List<RestMapNodeModel> findChildren(RestMapNodeModel root,
			List<RestMapNodeModel> allNodes) {
		List<RestMapNodeModel> children = new ArrayList<RestMapNodeModel>();
		for (RestMapNodeModel comparedOne : allNodes) {
			if (comparedOne.getParentId() == root.getId()) {
				children.add(comparedOne);
			}
		}
		List<RestMapNodeModel> notChildren = new ArrayList<RestMapNodeModel>();
		notChildren.addAll(allNodes);
		notChildren.removeAll(children);
		for (RestMapNodeModel child : children) {
			List<RestMapNodeModel> tmpChildren = findChildren(child,
					notChildren);
			child.setChildren(tmpChildren);
		}
		return children;
	}

	public static List<Long> getAllChildrenMapName(String ownerSql,
			String mapName) throws ApiException {
		StringBuffer query = new StringBuffer("from MapContainerNode where 1=1");
		query.append(ownerSql);
		List<?> list = QueryUtil.executeQuery(query.toString(), null, null,
				null, new LoadQueryBo());
		List<MapContainerNode> allMapNodes = (List<MapContainerNode>) list;
		if (allMapNodes.isEmpty()) {
			return new ArrayList<Long>();
		}
		List<MapContainerNode> mapNodeList = new ArrayList<MapContainerNode>();
		Map<Long, List<MapContainerNode>> nodeMap = new HashMap<Long, List<MapContainerNode>>();
		for (MapContainerNode mapNode : allMapNodes) {
			if (mapName.equals(mapNode.getMapName())) {
				mapNodeList.add(mapNode);
			}
			MapContainerNode parentMap = mapNode.getParentMap();
			if (null == parentMap) {
				continue;
			}
			long key = parentMap.getId();
			if (!nodeMap.containsKey(key)) {
				nodeMap.put(key, new ArrayList<MapContainerNode>());
			}
			nodeMap.get(key).add(mapNode);
		}
		List<Long> mapIdList = getChildMapName(nodeMap, mapNodeList,
				new ArrayList<Long>());
		if (mapIdList.isEmpty()) {
			throw new ApiException(Status.BAD_REQUEST, MgrUtil.getUserMessage(
					"error.rest.map.notExist", mapName), "mapName");
		}
		return mapIdList;
	}

	private static List<Long> getChildMapName(
			Map<Long, List<MapContainerNode>> nodeMap,
			List<MapContainerNode> mapContainerNodes, List<Long> ids) {
		if (mapContainerNodes.isEmpty()) {
			return ids;
		}
		for (MapContainerNode mapContainerNode : mapContainerNodes) {
			ids.add(mapContainerNode.getId());
			if (mapContainerNode.getMapType() != MapContainerNode.MAP_TYPE_FLOOR) {
				List<MapContainerNode> list = nodeMap.get(mapContainerNode
						.getId());
				if (null != list && !list.isEmpty()) {
					getChildMapName(nodeMap, list, ids);
				}
			}
		}
		return ids;
	}

	public static class LoadQueryBo implements QueryBo {
		@Override
		public Collection<HmBo> load(HmBo bo) {
			if (null == bo) {
				return null;
			}
			if (bo instanceof MapContainerNode) {
				MapContainerNode mapContainerNode = (MapContainerNode) bo;
				if (mapContainerNode.getParentMap() != null) {
					mapContainerNode.getParentMap().getId();
				}
			}
			return null;
		}
	}

	private static String getAuditTitle(HiveAp hiveAp,
			Map<String, String> stageMap) {
		if (null == hiveAp || null == stageMap) {
			return "";
		}
		String auditTitle = "";
		try {
			if (null != stageMap.get(hiveAp.getMacAddress())) {
				auditTitle = "Staged";
			} else if (hiveAp.isPending()) {
				auditTitle = ConfigurationResources.getMismatchMessage(
						hiveAp.getPendingIndex(), hiveAp.getPendingMsg(),
						ConfigurationType.Configuration);
			} else if (hiveAp.isPending_user()) {
				auditTitle = ConfigurationResources.getMismatchMessage(
						hiveAp.getPendingIndex_user(),
						hiveAp.getPendingMsg_user(),
						ConfigurationType.UserDatabase);
			} else {
				auditTitle = "Matched";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		auditTitle = auditTitle.replace("<", "\"").replace(">", "\"")
				.replace("'", "&#39;");
		return auditTitle;
	}

	private static String getMapNameField() {
		return "case when parentMap.mapType=3 then (parentMap.parentMap.mapName||'_'||parentMap.mapName) "
				+ "else parentMap.mapName end as mapName";
	}
}
