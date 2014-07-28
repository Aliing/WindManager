package com.ah.be.topo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.app.HmBeLogUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeGetStatisticEvent;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.MapMgmt;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapLeafNode;
import com.ah.bo.monitor.MapLink;
import com.ah.bo.monitor.MapNode;
import com.ah.bo.performance.AhNeighbor;
import com.ah.bo.performance.AhXIf;
import com.ah.events.BoEvent;
import com.ah.events.BoEvent.BoEventType;
import com.ah.events.impl.BoObserver;
import com.ah.util.MgrUtil;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

public class MapLinkProcessorViaCapwap implements QueryBo {

	private final BlockingQueue<Set<Long>> queue;

	private Thread pollingMgr;

	private boolean shutDown = true;

	public MapLinkProcessorViaCapwap() {
		queue = new SynchronousQueue<Set<Long>>(true);
	}

	public void stopProcessor() {
		shutDown = true;
		addInterruptObj();
	}

	public synchronized void addPollingContainers(Set<Long> containers) {
		if (null == containers || containers.isEmpty()) {
			return;
		}
		boolean result = queue.offer(containers);
		if (result) {
			DebugUtil.topoDebugInfo("prepare polling map "
					+ containers.toString());
		} else {
			DebugUtil.topoDebugInfo("try to polling map ["
					+ containers.toString()
					+ " failed ,maybe other maps is polling.");
		}
	}

	private synchronized void addInterruptObj() {
		queue.clear();
		Set<Long> interruptObj = new HashSet<Long>();
		boolean result = queue.offer(interruptObj);
		while (!result) {
			try {
				DebugUtil
						.topoDebugWarn("The queue is not empty, try to put the interrupt object 20ms later.");
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			result = queue.offer(interruptObj);
		}
	}

	public void start() {
		if (isStart()) {
			return;
		}

		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_TOPO,
				"<BE Thread> Map link processor via CAPWAP is running...");

		shutDown = false;

		pollingMgr = new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						Set<Long> pollingMapId_set = queue.take();
						if (pollingMapId_set.isEmpty()) {
							BeLogTools
									.info(HmLogConst.M_TRACER
											| HmLogConst.M_TOPO,
											"<BE Thread> Map link processor via CAPWAP is shutdown");
							break;
						} else {
							pollingTask(pollingMapId_set);
						}
					} catch (Exception e) {
						DebugUtil
								.topoDebugError(
										"Exception happened while running Map Link Refresh task.",
										e);
					} catch (Error e) {
						DebugUtil
								.topoDebugError(
										"Error happened while running Map Link Refresh task.",
										e);
					}
				}
			}
		};
		pollingMgr.setName("pollingMgr");
		pollingMgr.start();
	}

	public boolean isStart() {
		return pollingMgr != null && pollingMgr.isAlive();
	}

	private void pollingTask(Set<Long> pollingMapId_set) {
		DebugUtil.topoDebugInfo("Start polling.. refresh map count:"
				+ pollingMapId_set.size());
		for (Long id : pollingMapId_set) {
			long start = System.currentTimeMillis();
			updateMapContainer(id);
			DebugUtil
					.topoDebugInfo("polling neighbors for map [" + id
							+ "] cost " + (System.currentTimeMillis() - start)
							+ " ms.");
		}
	}

	private void updateMapContainer(Long mapContainerId) {
		try {
			MapContainerNode container = QueryUtil.findBoById(
					MapContainerNode.class, mapContainerId, this);
			if (null == container) {
				return;
			}
			List<BeGetStatisticEvent> request_aps = getRequests(container);
			if (null == request_aps || request_aps.isEmpty()) {
				DebugUtil.topoDebugInfo("No polling request on map:"
						+ container.getMapName());
				return;
			}

			List<BeCommunicationEvent> response_aps = HmBeCommunicationUtil
					.sendSyncGroupRequest(request_aps, 30);

			if (null == response_aps) {
				DebugUtil
						.topoDebugError("Send polling neighbor requests failed for map:"
								+ container.getMapName());
				return;
			} else if (request_aps.size() != response_aps.size()) {
				DebugUtil.topoDebugError("Polling neighbor request size("
						+ request_aps.size()
						+ ") doesn't the same as response size("
						+ response_aps.size() + ")!!!");
			}

			Map<String, StatisticResultsObject> results_map = getResults(response_aps);

			// re-search this map
			container = QueryUtil.findBoById(MapContainerNode.class,
					mapContainerId, this);
			if (null == container) {
				return;
			}

			Map<String, MapLink> childLinks = new HashMap<String, MapLink>();
			Map<String, MapLink> timeoutLinks = new HashMap<String, MapLink>();
			Map<String, MapLeafNode> foreignNodes = new HashMap<String, MapLeafNode>();
			Map<Long, Collection<MapLeafNode>> foreignLinks = new HashMap<Long, Collection<MapLeafNode>>();

			boolean timeoutChanged = false;
			boolean ethIdChanged = false;
			for (MapNode child : container.getChildNodes()) {
				if (null != child && child.isLeafNode()) {
					MapLeafNode leafNode = (MapLeafNode) child;
					HiveAp host = leafNode.getHiveAp();
					if (null == host
							|| HiveAp.STATUS_MANAGED != host.getManageStatus()) {
						continue;
					}
					StatisticResultsObject results = results_map.get(host
							.getMacAddress());
					if (null == results) {
						continue;
					}
					List<AhNeighbor> neighbors = results.getNeighbors();
					List<AhXIf> xifs = results.getXifs();

					if (neighbors == null) {
						DebugUtil.topoDebugInfo("Polling hiveAp:"
								+ host.getHostName()
								+ ". Must have timed out, keep old links.");
						//comment this system log, because it will cause the system log too large.
//						HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR,
//								HmSystemLog.FEATURE_TOPOLOGY, ""
//										+ MgrUtil.getUserMessage("hm.system.log.map.link.processor.via.capwap.poll.neighbors.time.out",new String[]{NmsUtil.getOEMCustomer().getAccessPonitName(),host.getHostName()}));
						addTimeoutLinks(host, timeoutLinks, container);
						// reset the timeout flag on mapLeafNode if needed;
						if (!host.findMapLeafNode().isFetchLinksTimeout()) {
							BoMgmt.getMapMgmt().updateLeafNodeFetchTimeout(
									host.findMapLeafNode().getId(), true);
							// assign a value to timeoutChanged
							timeoutChanged = true;
						}
					} else {
						DebugUtil.topoDebugInfo("Polling hiveAp:"
								+ host.getHostName()
								+ ". Adding new links. Entry count:"
								+ neighbors.size());
						addLinks(host, childLinks, neighbors, foreignLinks);
						// reset the timeout flag on mapLeafNode if needed;
						if (host.findMapLeafNode().isFetchLinksTimeout()) {
							BoMgmt.getMapMgmt().updateLeafNodeFetchTimeout(
									host.findMapLeafNode().getId(), false);
							// assign a value to timeoutChanged
							timeoutChanged = true;
						}
					}
					boolean elcs = updateEthernetLink(xifs, host);
					ethIdChanged = !ethIdChanged ? elcs : ethIdChanged;
				}
			}

			// put links which between both nodes timeout into childLinks
			for (MapLink link : timeoutLinks.values()) {
				String reverseKey = link.getReverseKey();
				Long from = link.getFromNode().getId();
				Long to = link.getToNode().getId();
				boolean bidirectional = timeoutLinks.containsKey(reverseKey);
				if (bidirectional
						&& !isLinkExist(from, to, childLinks.values())) {
					childLinks.put(link.getKey(), link);
				}
			}

			for (MapNode child : container.getChildNodes()) {
				if (null != child && child.isLeafNode()) {
					MapLeafNode leafNode = (MapLeafNode) child;
					if (leafNode.getApId().charAt(0) == 'M') {
						foreignNodes.put(leafNode.getApId(), leafNode);
					}
				}
			}
			DebugUtil.topoDebugInfo("Existing foreign nodes count: "
					+ foreignNodes.size());

			// Create foreign nodes/links
			for (Long mapId : foreignLinks.keySet()) {
				Collection<MapLeafNode> fromNodes = foreignLinks.get(mapId);
				MapLeafNode foreignNode = foreignNodes.get("M" + mapId);
				if (foreignNode == null) {
					// Create new foreign node
					MapContainerNode foreignMap = QueryUtil.findBoById(
							MapContainerNode.class, mapId);
					foreignNode = new MapLeafNode();
					foreignNode.setX(container.getWidth() * 0.03);
					foreignNode.setY(container.getHeight() * 0.2
							+ Math.random() * container.getHeight() * 0.6);
					foreignNode.setIconName(MapMgmt.EXTERNAL_ICON);
					foreignNode.setParentMap(container);
					foreignNode.setOwner(container.getOwner());
					foreignNode.setApId("M" + mapId);
					DebugUtil.topoDebugInfo("Create new foreign node: "
							+ foreignNode.getApId());
					// Shown as the node label
					foreignNode.setApName(foreignMap.getMapName());
					Long nodeId = QueryUtil.createBo(foreignNode);
					foreignNode = QueryUtil.findBoById(MapLeafNode.class,
							nodeId);
				} else {
					DebugUtil.topoDebugInfo("Use existing foreign node: "
							+ foreignNode.getApId());
					foreignNodes.remove(foreignNode.getApId());
				}
				// Create foreign links
				for (MapLeafNode fromNode : fromNodes) {
					MapLink foreignLink = new MapLink();
					foreignLink.setFromNode(fromNode);
					foreignLink.setToNode(foreignNode);
					childLinks.put(foreignLink.getKey(), foreignLink);
				}
			}
			// Verify if any foreign nodes need to be removed
			Collection<Long> nodeIds = new Vector<Long>();
			for (MapLeafNode foreignNode : foreignNodes.values()) {
				DebugUtil.topoDebugInfo("Remove foreign node: "
						+ foreignNode.getApId());
				nodeIds.add(foreignNode.getId());
			}
			if (shutDown) {
				return;
			}
			// Update and reload nodes/links
			MapContainerNode updatedContainer = BoMgmt.getMapMgmt()
					.updateMapContainerLinks(container.getId(), childLinks,
							this);
			if (nodeIds.size() > 0) {
				QueryUtil.removeBos(MapLeafNode.class, nodeIds);
				// Refresh from server because nodes were removed.
				updatedContainer = QueryUtil.findBoById(MapContainerNode.class,
						container.getId(), this);
			}
			// make sure only notify once at most every time.
			if (timeoutChanged || ethIdChanged) {
				// Notify MapAlarmsCache objects, it will refresh both
				// nodes and links.
				// Just create a new node and fill in
				// the parent map, which is all that the
				// MapAlarmsCache uses.
				MapLeafNode newLeafNode = new MapLeafNode();
				newLeafNode.setParentMap(container);
				BoObserver.notifyListeners(new BoEvent<MapLeafNode>(
						newLeafNode, BoEventType.CREATED));
			} else if (null != updatedContainer) {
				// Notify MapAlarmsCache objects, it will refresh object
				// based on what's changed.
				BoObserver.notifyListeners(new BoEvent<MapContainerNode>(
						updatedContainer, BoEventType.UPDATED));
			}
		} catch (Exception e) {
			DebugUtil.topoDebugWarn("updateMapContainer failed: ", e);
		}
	}

	private void addTimeoutLinks(HiveAp host,
			Map<String, MapLink> timeoutLinks, MapContainerNode container) {
		for (MapLink link : container.getChildLinks().values()) {
			String fromAp = link.getFromNode().getApId();
			String toAp = link.getToNode().getApId();
			if (fromAp.equals(host.getMacAddress())
					|| toAp.equals(host.getMacAddress())) {
				// Only keep link between both timeout nodes,
				// if either is OK, just uses that value.
				// For the foreign link, just remove it when
				// node timeout.
				if (fromAp.charAt(0) != 'M' && toAp.charAt(0) != 'M') {
					timeoutLinks.put(link.getKey(), link);
					DebugUtil.topoDebugInfo("Must keep link: " + link.getKey()
							+ " between nodes: " + fromAp + " and: " + toAp
							+ " if both nodes timeout.");
				}
			}
		}
	}

	private void addLinks(HiveAp host, Map<String, MapLink> linkList,
			List<AhNeighbor> neighbors,
			Map<Long, Collection<MapLeafNode>> foreignLinks) {
		if (null == host.findMapLeafNode()) {
			DebugUtil.topoDebugWarn("HiveAP [" + host.getMacAddress()
					+ "] is out of map container.");
			return;
		}

		for (AhNeighbor neighbor_result : neighbors) {
			String macAddress = neighbor_result.getNeighborAPID();
			byte linkType = neighbor_result.getLinkType();
			DebugUtil.topoDebugInfo("Get neighbor by the macAddress: ["
					+ macAddress + "].");
			if (shutDown) {
				return;
			}
			// check whether is Ethernet link;
			if (linkType == AhNeighbor.LINKTYPE_ETHLINK) {
				continue;
			}
			HiveAp neighbor = QueryUtil.findBoByAttribute(HiveAp.class,
					"macAddress", macAddress.toUpperCase(), host.getOwner()
							.getId());
			if (null == neighbor || null == neighbor.getMapContainer()) {
				continue;
			}
			// not in the same map, add node in list to create foreign
			// nodes/links later
			if (!host.getMapContainer().getId()
					.equals(neighbor.getMapContainer().getId())) {
				Collection<MapLeafNode> fromNodes = foreignLinks.get(neighbor
						.getMapContainer().getId());
				if (fromNodes == null) {
					fromNodes = new Vector<MapLeafNode>();
					foreignLinks.put(neighbor.getMapContainer().getId(),
							fromNodes);
				}
				fromNodes.add(host.findMapLeafNode());
				continue;
			}

			Long from = host.findMapLeafNode().getId();
			Long to = neighbor.findMapLeafNode().getId();
			boolean exist = setLinkRssi(from, to,
					neighbor_result.getRssi() - 95, linkList.values());

			if (exist) {
				DebugUtil.topoDebugInfo("Link for neighbor (" + from + ", "
						+ to + ") already exists.");
			} else {
				DebugUtil.topoDebugInfo("Creating new link for neighbor ("
						+ from + ", " + to + ").");
				MapLink link = new MapLink();
				link.setFromNode(host.findMapLeafNode());
				link.setToNode(neighbor.findMapLeafNode());
				link.setFromRssi(neighbor_result.getRssi() - 95);
				linkList.put(link.getKey(), link);
			}
		}
	}

	private boolean updateEthernetLink(List<AhXIf> xifs, HiveAp hiveAp)
			throws Exception {
		boolean ethIdChanged = false;
		if (null != xifs) {
		    
            MapLeafNode leafNode = hiveAp.findMapLeafNode();
            // check for the previous Ethernet link of this HiveAP.
            String current_ethId = leafNode.getEthId();
            
			if(hiveAp.isSwitchProduct()) {
			    boolean ethUpFlag = false;
                for (AhXIf xif : xifs) {
                    if (xif.getIfName().toLowerCase().startsWith("p") 
                            || xif.getIfName().toLowerCase().startsWith("eth1/")) {
                        ethUpFlag = xif.getIfAdminStatus() == AhXIf.IFADMINSTATUS_UP
                                && xif.getIfOperStatus() == AhXIf.IFOPERSTATUS_UP;
                        if (ethUpFlag) {
                            break;
                        }
                    }
                }
                if(ethUpFlag) {
                    // Ethernet up
                    ethIdChanged = changeEthUp(hiveAp, ethIdChanged, leafNode,
                            current_ethId);
                } else {
                    // Ethernet down
                    ethIdChanged = changeEthDown(ethIdChanged, leafNode,
                            current_ethId);
                }
			} else {
			    byte adminStatus_eth0 = 0;
			    byte operStatus_eth0 = 0;
			    byte adminStatus_eth1 = 0;
			    byte operStatus_eth1 = 0;
			    
			    for (AhXIf xif : xifs) {
			        if ("eth0".equals(xif.getIfName())) {
			            adminStatus_eth0 = xif.getIfAdminStatus();
			            operStatus_eth0 = xif.getIfOperStatus();
			        } else if ("eth1".equals(xif.getIfName())) {
			            adminStatus_eth1 = xif.getIfAdminStatus();
			            operStatus_eth1 = xif.getIfOperStatus();
			        }
			    }
			    if ((adminStatus_eth0 == AhXIf.IFADMINSTATUS_UP && operStatus_eth0 == AhXIf.IFOPERSTATUS_UP)
			            || (adminStatus_eth1 == AhXIf.IFADMINSTATUS_UP && operStatus_eth1 == AhXIf.IFOPERSTATUS_UP)) {
			        // Ethernet up
			        ethIdChanged = changeEthUp(hiveAp, ethIdChanged, leafNode,
                            current_ethId);
			    } else if (adminStatus_eth0 == AhXIf.IFADMINSTATUS_DOWN
			            || operStatus_eth0 == AhXIf.IFOPERSTATUS_DOWN
			            || adminStatus_eth1 == AhXIf.IFADMINSTATUS_DOWN
			            || operStatus_eth1 == AhXIf.IFOPERSTATUS_DOWN) {
			        // Ethernet down
			        ethIdChanged = changeEthDown(ethIdChanged, leafNode,
                            current_ethId);
			    }
			}
		}
		return ethIdChanged;
	}

    private boolean changeEthDown(boolean ethIdChanged, MapLeafNode leafNode,
            String current_ethId) throws Exception {
        if (null != current_ethId && !("".equals(current_ethId))) {
            BoMgmt.getMapMgmt().updateLeafNodeEthId(leafNode.getId(),"");
            ethIdChanged = true;
        }
        return ethIdChanged;
    }

    private boolean changeEthUp(HiveAp hiveAp, boolean ethIdChanged,
            MapLeafNode leafNode, String current_ethId) throws Exception {
        String ipAddress = hiveAp.getIpAddress();
        String netmask = hiveAp.getNetmask();
        if (null != ipAddress && null != netmask) {
            String subNet = AhDecoder.int2IP(AhEncoder
                    .ip2Int(ipAddress) & AhEncoder.ip2Int(netmask));
            if (!subNet.equals(current_ethId)) {
                BoMgmt.getMapMgmt().updateLeafNodeEthId(
                        leafNode.getId(), subNet);
                ethIdChanged = true;
            }
        } else {
            DebugUtil.topoDebugWarn("HiveAP [" + hiveAp.getHostName()
                    + "] get AhXIf information, but has no ip ["
                    + ipAddress + "] or netmask [" + netmask
                    + "] in database, ??");
        }
        return ethIdChanged;
    }

	private List<BeGetStatisticEvent> getRequests(MapContainerNode container) {
		Set<MapNode> childNodes = container.getChildNodes();
		if (null == childNodes || childNodes.isEmpty()) {
			return null;
		}
		List<BeGetStatisticEvent> requests = new ArrayList<BeGetStatisticEvent>();
		// collect HiveAP for request neighbors.
		for (MapNode child : childNodes) {
			if (null != child && child.isLeafNode()) {
				MapLeafNode leafNode = (MapLeafNode) child;
				String nodeId = leafNode.getApId();

				HiveAp hiveAp = leafNode.getHiveAp();
				if (null == hiveAp) {
					continue;
				}
				// if (hiveAp.isSimulated()){
				// continue;
				// }
				String hostname = hiveAp.getHostName();
				String mapName = hiveAp.getMapContainer().getMapName();
				if (hiveAp.getManageStatus() != HiveAp.STATUS_MANAGED) {
					DebugUtil.topoDebugInfo("Don't get neighbor for HiveAp:"
							+ hostname + " (" + nodeId + ") on map: " + mapName
							+ ", for it isn't managed.");
					continue;
				}
				BeGetStatisticEvent request = BeTopoModuleUtil
						.getLinksStatisticEvent(hiveAp);
				if (null != request) {
					requests.add(request);
				}
			}
		}
		return requests;
	}

	private Map<String, StatisticResultsObject> getResults(
			List<BeCommunicationEvent> response_aps) {
		Map<String, StatisticResultsObject> results_map = new HashMap<String, StatisticResultsObject>();
		for (BeCommunicationEvent event : response_aps) {
			StatisticResultsObject results = BeTopoModuleUtil
					.getStatisticResult(event);
			if (null != results) {
				results_map.put(results.getOwner().getMacAddress(), results);
			}
		}
		return results_map;
	}

	private boolean isLinkExist(Long from, Long to, Collection<MapLink> mapLinks) {
		boolean exist = false;
		if (null != mapLinks) {
			for (MapLink link : mapLinks) {
				Long fromNode = link.getFromNode().getId();
				Long toNode = link.getToNode().getId();
				if ((fromNode.equals(from) && toNode.equals(to))
						|| (fromNode.equals(to) && toNode.equals(from))) {
					exist = true;
					break;
				}
			}
		}
		return exist;
	}

	private boolean setLinkRssi(Long from, Long to, int rssi,
			Collection<MapLink> mapLinks) {
		if (null != mapLinks) {
			for (MapLink link : mapLinks) {
				Long fromNode = link.getFromNode().getId();
				Long toNode = link.getToNode().getId();
				if (fromNode.equals(from) && toNode.equals(to)) {
					link.setFromRssi(rssi);
					return true;
				}
				if (fromNode.equals(to) && toNode.equals(from)) {
					link.setToRssi(rssi);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo == null) {
			return null;
		}
		MapContainerNode mapContainerNode = (MapContainerNode) bo;
		for (MapNode mapNode : mapContainerNode.getChildNodes()) {
			// Reload the nodes as well
			if (mapNode.isLeafNode()) {
				MapLeafNode leafNode = (MapLeafNode) mapNode;
				if (null != leafNode.getHiveAp()) {
					leafNode.getHiveAp().getId();
				}
			}
		}
		// Just to trigger load from database
		mapContainerNode.getChildLinks().values();
		return null;
	}

}