package com.ah.bo.mgmt;

import java.util.Collection;

import com.ah.bo.HmBo;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapLeafNode;
import com.ah.bo.monitor.MapNode;

public class QueryLazyBo implements QueryBo {

	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof MapContainerNode) {
			MapContainerNode container = (MapContainerNode) bo;
			for (MapNode node : container.getChildNodes()) {
				if (node.isLeafNode()) {
					HiveAp ap = ((MapLeafNode) node).getHiveAp();
					if (null != ap) {
						ap.getId();
					}
				}
			}
			container.getPlannedAPs().size();
			container.getPerimeter().size();
			container.getWalls().size();
		} else if (bo instanceof MapLeafNode) {
			MapLeafNode leaf = (MapLeafNode) bo;
			if (leaf.getParentMap() != null) {
				leaf.getParentMap().getId();
				leaf.getParentMap().getChildLinks().size();
			}
			if (leaf.getHiveAp() != null) {
				leaf.getHiveAp().getId();
			}
		} else if (bo instanceof HiveAp) {
			HiveAp hiveAp = (HiveAp) bo;
			// Just calling the get method will fetch the LAZY attributes
			// Call additional LAZY methods
			if (hiveAp.getWifi0RadioProfile() != null)
				hiveAp.getWifi0RadioProfile().getId();
			if (hiveAp.getWifi1RadioProfile() != null)
				hiveAp.getWifi1RadioProfile().getId();

			if (hiveAp.getDeviceInterfaces()!=null) {
				hiveAp.getDeviceInterfaces().size();
			}
			
			if (hiveAp.getIpRoutes() != null) {
				for (int i = 0; i < hiveAp.getIpRoutes().size(); i++) {
					hiveAp.getIpRoutes().get(i).getGateway();
					hiveAp.getIpRoutes().get(i).getNetmask();
					hiveAp.getIpRoutes().get(i).getSourceIp();
				}
			}
			if (hiveAp.getVirtualConnections() != null) {
				for (int i = 0; i < hiveAp.getVirtualConnections().size(); i++) {
					hiveAp.getVirtualConnections().get(i).getDestMac();
					hiveAp.getVirtualConnections().get(i).getForwardAction();
					hiveAp.getVirtualConnections().get(i).getForwardName();
					hiveAp.getVirtualConnections().get(i).getInterface_in();
					hiveAp.getVirtualConnections().get(i).getInterface_out();
					hiveAp.getVirtualConnections().get(i).getRxMac();
					hiveAp.getVirtualConnections().get(i).getSourceMac();
					hiveAp.getVirtualConnections().get(i).getTxMac();
				}
			}
			if (hiveAp.getConfigTemplate() != null) {
				hiveAp.getConfigTemplate().getId();
				if (hiveAp.getConfigTemplate().getHiveProfile()!=null) {
					hiveAp.getConfigTemplate().getHiveProfile().getId();
				}
			}
			if (hiveAp.getRadiusServerProfile() != null) {
				hiveAp.getRadiusServerProfile().getId();
			}
			if (hiveAp.getDhcpServers() != null) {
				hiveAp.getDhcpServers().size();
			}
			if (null != hiveAp.getMapContainer()) {
				MapContainerNode container = hiveAp.getMapContainer();
				for (MapNode node : container.getChildNodes()) {
					if (node.isLeafNode()) {
						HiveAp ap = ((MapLeafNode) node).getHiveAp();
						if (null != ap) {
							ap.getId();
						}
					}
				}
				container.getChildLinks().size();
				container.getPlannedAPs().size();
				container.getPerimeter().size();
				container.getWalls().size();
			}
			if(hiveAp.getInternalNetworks() != null){
				hiveAp.getInternalNetworks().size();
			}
		}
		return null;
	}

}
