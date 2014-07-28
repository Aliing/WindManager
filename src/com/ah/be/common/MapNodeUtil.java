package com.ah.be.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapLeafNode;
import com.ah.bo.monitor.MapNode;
import com.ah.bo.performance.AhLatestXif;

public class MapNodeUtil {
	public static Map<String, List<AhLatestXif>> getWifiRadioMap(
			Set<MapNode> childNodes) {
		Map<String, List<AhLatestXif>> wifiRadioMap = new HashMap<String, List<AhLatestXif>>();
		if (null == childNodes) {
			return wifiRadioMap;
		}
		List<String> apMacList = new ArrayList<String>();
		for (MapNode node : childNodes) {
			if (node instanceof MapLeafNode) {
				MapLeafNode apNode = (MapLeafNode) node;
				apMacList.add(apNode.getApId());
			}
		}
		if (!apMacList.isEmpty()) {
			String where = "(ifName='wifi0' or ifName='wifi1') and apMac in(:s1)";
			Object[] values = new Object[1];
			values[0] = apMacList;
			List<AhLatestXif> radioList = QueryUtil.executeQuery(
					AhLatestXif.class, null, new FilterParams(where, values));
			for (AhLatestXif xif : radioList) {
				String ifName = xif.getIfName();
				if (null == ifName || "".equals(ifName.trim())) {
					continue;
				}
				ifName = ifName.toLowerCase().trim();
				String radioMapKey = xif.getApMac();
				if (!wifiRadioMap.containsKey(radioMapKey)) {
					wifiRadioMap.put(radioMapKey, new ArrayList<AhLatestXif>());
				}
				if ("wifi0".equals(ifName) || "wifi1".equals(ifName)) {
					wifiRadioMap.get(radioMapKey).add(xif);
					continue;
				}
			}
		}
		return wifiRadioMap;
	}
	
	public static List<MapNode> sortMapTree(Set<MapNode> set,final boolean orderFolders,final MapContainerNode map,boolean ignoreLeafNode){
		List<MapNode> resultList = new ArrayList<MapNode>();
		
		if(ignoreLeafNode){
			if(set != null){
				for (MapNode mapNode : set) {
					if(!mapNode.isLeafNode()){
						resultList.add(mapNode);
					}
				}
			}
		} else {
			resultList.addAll(set);
		}
		
		Collections.sort(resultList, new Comparator<MapNode>(){
			@Override
			public int compare(MapNode o1, MapNode o2) {
				if (map.getMapType() == MapContainerNode.MAP_TYPE_BUILDING
		                && o1 instanceof MapContainerNode 
		                && o2 instanceof MapContainerNode) {
					return ((MapContainerNode) o2).getMapOrder()
							- ((MapContainerNode) o1).getMapOrder();
				} else if (o1.isLeafNode() || o2.isLeafNode()) {
					return (int) (o1.getId() - o2.getId());
				} else if (orderFolders) {
					return ((MapContainerNode) o1).getMapOrder()
							- ((MapContainerNode) o2).getMapOrder();
				} else {
					return ((MapContainerNode) o1).getMapName().compareTo(
							((MapContainerNode) o2).getMapName());
				}
			}
		});
		
		return resultList;
	}
}
