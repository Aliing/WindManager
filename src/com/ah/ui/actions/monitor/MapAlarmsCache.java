package com.ah.ui.actions.monitor;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.json.JSONObject;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmUser;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapLeafNode;
import com.ah.bo.monitor.MapLink;
import com.ah.bo.monitor.MapNode;
import com.ah.bo.monitor.Vertex;
import com.ah.bo.monitor.Wall;
import com.ah.events.BoEventFilter;
import com.ah.events.BoEventListener;
import com.ah.events.impl.BoObserver;
import com.ah.util.Tracer;

/*
 * @author Chris Scheers
 */

public class MapAlarmsCache<T extends MapNode> implements QueryBo,
		BoEventListener<T>, HttpSessionBindingListener {

	private static final Tracer log = new Tracer(
			MapAlarmsCache.class.getSimpleName());

	public MapAlarmsCache(HmUser userContext) {
		this.userContext = userContext;
		refreshTasks = new LinkedList<Short>();
		refreshTasks.offer(REFRESH_TYPE_ROGUES);
		refreshTasks.offer(REFRESH_TYPE_CLIENTS);
		refreshTasks.offer(REFRESH_TYPE_SUMMARY);
		log.info("MapAlarmsCache", "refresh tasks count:" + refreshTasks.size());
		BoObserver.addBoEventListener(this, new BoEventFilter<MapLeafNode>(
				MapLeafNode.class));
		BoObserver.addBoEventListener(this,
				new BoEventFilter<MapContainerNode>(MapContainerNode.class));
	}

	@Override
	public void valueBound(HttpSessionBindingEvent event) {
		log.info("valueBound", "Bound event: " + event.getName());
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent event) {
		log.info("valueUnbound", "Unbound event: " + event.getName());
		BoObserver.removeBoEventListener(this);
	}

	private Long mapId;

	private MapContainerNode mapContainerNode;

	private Long pageId;

	private ConcurrentMap<Long, MapNode> childNodes;

	private boolean refreshNodes, refreshLinks, refreshRogues, refreshClients,
			refreshSummary;

	private HmUser userContext;

	private ConcurrentMap<Long, Long> expandedTreeNodes = new ConcurrentHashMap<Long, Long>();

	private static final short REFRESH_TYPE_ROGUES = 1;

	private static final short REFRESH_TYPE_CLIENTS = 2;

	private static final short REFRESH_TYPE_SUMMARY = 3;

	private Queue<Short> refreshTasks;

	/*
	 * Set up alarms cache for new container map.
	 */
	public synchronized void setMapId(Long mapId, Long pageId) {
		if (mapId == null || pageId == null) {
			return;
		}
		log.info("setMapContainerNode", "Setting up alarms cache for map: "
				+ mapId + ", page: " + pageId);
		// Load child nodes from database, but don't create alarms cache yet
		loadMapContainerNode(mapId, false, false);
		if (mapContainerNode == null) {
			return;
		}
		this.mapId = mapId;
		this.pageId = pageId;
	}

	public synchronized void loadMapContainerNode(Long mapId,
			boolean nodesChanged, boolean linksChanged) {
		QueryUtil.findBoById(MapNode.class, mapId, this);
		if (mapContainerNode == null) {
			return;
		}
		childNodes = null;
		refreshNodes = nodesChanged;
		refreshLinks = linksChanged;
	}

	public synchronized void initCache() {
		if (childNodes != null) {
			return;
		}
		childNodes = new ConcurrentHashMap<Long, MapNode>();
		if (mapContainerNode == null) {
			return;
		}
		for (MapNode mapNode : mapContainerNode.getChildNodes()) {
			childNodes.putIfAbsent(mapNode.getId(), mapNode);
		}
	}

	public boolean nodesChanged(MapContainerNode newMapContainerNode) {
		if (mapContainerNode == null) {
			return false;
		}
		log.info("nodesChanged", "old # nodes: "
				+ mapContainerNode.getChildNodes().size() + ", new # nodes: "
				+ newMapContainerNode.getChildNodes().size());
		if (mapContainerNode.getChildNodes().size() != newMapContainerNode
				.getChildNodes().size()) {
			// Nodes need to be updated.
			return true;
		}
		for (MapNode mapNode : newMapContainerNode.getChildNodes()) {
			if (mapContainerNode.getChildNodes().contains(mapNode)) {
				log.info("nodesChanged", "Node: " + mapNode.getId()
						+ " is same as before.");
			} else {
				log.info("nodesChanged", "Node: " + mapNode.getId()
						+ " is missing.");
				return true;
			}
		}
		return false;
	}

	public boolean linksChanged(MapContainerNode newMapContainerNode) {
		if (mapContainerNode == null) {
			return false;
		}
		log.info("linksChanged", "old # links: "
				+ mapContainerNode.getChildLinks().size() + ", new # links: "
				+ newMapContainerNode.getChildLinks().size());
		if (mapContainerNode.getChildLinks().size() != newMapContainerNode
				.getChildLinks().size()) {
			// Links need to be updated.
			return true;
		}
		for (String linkId : newMapContainerNode.getChildLinks().keySet()) {
			MapLink mapLink = mapContainerNode.getChildLinks().get(linkId);
			if (mapLink == null) {
				log.info("linksChanged", "Link: " + linkId + " is missing.");
				return true;
			} else {
				log.info("linksChanged", "Link: " + linkId
						+ " is same as before.");
			}
		}
		return false;
	}

	public Collection<HmBo> load(HmBo bo) {
		mapContainerNode = (MapContainerNode) bo;
		if (bo == null) {
			return null;
		}
		for (MapNode mapNode : mapContainerNode.getChildNodes()) {
			// No need to be pickup up by next Ajax call
			mapNode.setSelected(false);
			if (mapNode.isLeafNode()) {
				HiveAp hiveAp = ((MapLeafNode) mapNode).getHiveAp();
				if (hiveAp == null) {
					log.info("load",
							"Foreign node has no AP (" + mapNode.getLabel()
									+ ")");
				} else {
					// Trigger load from DB
					hiveAp.getHiveApModel(); // we need this attribute to
					// calculate ERP
				}
			}
		}
		for (MapLink mapLink : mapContainerNode.getChildLinks().values()) {
			// Just to trigger load from database
		}
		for (Vertex vertex : mapContainerNode.getPerimeter()) {
			// Just to trigger load from database
		}
		for (Wall wall : mapContainerNode.getWalls()) {
			// Just to trigger load from database
		}
		return null;
	}

	@Override
	public void boCreated(T hmBo) {
		if (hmBo instanceof MapContainerNode) {
			MapContainerNode mapContainerNode = (MapContainerNode) hmBo;
			log.info("boCreated",
					"Refresh links for map: " + mapContainerNode.getId());
			if (mapContainerNode.getId().equals(mapId)) {
				log.info("boCreated", "Use this event to refresh nodes/links: "
						+ hmBo.getLabel());
				boolean nodesChanged = nodesChanged(mapContainerNode);
				boolean linksChanged = false;
				if (!nodesChanged) {
					linksChanged = linksChanged(mapContainerNode);
				}
				if (nodesChanged || linksChanged) {
					loadMapContainerNode(mapId, nodesChanged, linksChanged);
				}
			}
		} else if (hmBo instanceof MapLeafNode) {
			MapLeafNode mapLeafNode = (MapLeafNode) hmBo;
			if (!mapLeafNode.getParentMap().getId().equals(mapId)) {
				// Different map
				return;
			}
			// assume both nodes and links have changed.
			loadMapContainerNode(mapId, true, true);
		} else {
			log.info("boCreated",
					"Ignoring this event for now: " + hmBo.getLabel());
		}
	}

	@Override
	public void boUpdated(T hmBo) {
		log.info("boUpdated", "New/Updated Alarm: " + hmBo.getLabel());
		updateMapNode(hmBo);
	}

	@Override
	public void boRemoved(T hmBo) {
		if (hmBo instanceof MapLeafNode) {
			MapLeafNode mapLeafNode = (MapLeafNode) hmBo;
			if (!mapLeafNode.getParentMap().getId().equals(mapId)) {
				// Different map
				return;
			}
			// assume both nodes and links have changed.
			loadMapContainerNode(mapId, true, true);
		}
	}

	public void updateMapNode(MapNode updatedMapNode) {
		log.info("updateMapNode", "Updated node: " + updatedMapNode.getId()
				+ " severity: " + updatedMapNode.getSeverity());
		initCache();
		MapNode mapNode = childNodes.get(updatedMapNode.getId());
		if (mapNode == null) {
			return;
		}
		if (mapNode.getSeverity() != updatedMapNode.getSeverity()) {
			mapNode.setSeverity(updatedMapNode.getSeverity());
			// Needs to be pickup up by next Ajax call
			mapNode.setSelected(true);
		}
		if (mapNode.getX() != updatedMapNode.getX()
				|| mapNode.getY() != updatedMapNode.getY()) {
			log.info("updateMapNode", "position changed (" + mapNode.getX()
					+ ", " + mapNode.getY() + ") -> (" + updatedMapNode.getX()
					+ ", " + updatedMapNode.getY() + ")");
			mapNode.setX(updatedMapNode.getX());
			mapNode.setY(updatedMapNode.getY());
			// refreshNodes = true; TODO, UI doesn't support moving nodes for
			// now
		}
	}

	protected boolean validMap(Long mapId, Long pageId) {
		if (this.mapId == null || this.pageId == null) {
			return false;
		}
		if (!this.mapId.equals(mapId)) {
			log.info("validateMap", "Get cached data must be for map: "
					+ this.mapId);
			return false;
		}
		if (!this.pageId.equals(pageId)) {
			log.info("validateMap", "Get cached data must be from page: "
					+ this.pageId);
			return false;
		}
		return true;
	}

	public synchronized Set<MapNode> getMapNodes(Long mapId, Long pageId)
			throws Exception {
		if (mapContainerNode != null && validMap(mapId, pageId)) {
			return mapContainerNode.getChildNodes();
		} else {
			return new HashSet<MapNode>();
		}
	}

	public synchronized JSONObject getNewAlarms(Long mapId, Long pageId,
			double scale, boolean rogueChecked, boolean clientChecked,
			boolean summaryChecked) throws Exception {
		log.info("getNewAlarms", "Get new alarms for map: " + mapId
				+ ", page: " + pageId);
		JSONObject jsonObject = new JSONObject();
		if (!validMap(mapId, pageId)) {
			return jsonObject;
		}
		log.info("getNewAlarms", "Refresh nodes ? " + refreshNodes
				+ ", refreshLinks: " + refreshLinks);
		if (refreshNodes) {
			refreshNodes = false;
			refreshLinks = true;
			return MapsAction.prepareNodes(mapContainerNode.getChildNodes(),
					pageId, scale);
		} else if (refreshLinks) {
			refreshLinks = false;
			return MapsAction.prepareLinks(mapContainerNode.getChildLinks()
					.values(), pageId, MapsAction.showRssi(userContext));
		}
		// alarms
		if (childNodes != null) {
			Collection<JSONObject> jsonNodes = new Vector<JSONObject>();
			boolean hasPageId = false;
			for (MapNode mapNode : childNodes.values()) {
				if (!mapNode.isSelected()) {
					continue;
				}
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("nodeId", "n" + mapNode.getId());
				jsonObj.put("x", MapsAction.scale(mapNode.getX(), scale));
				jsonObj.put("y", MapsAction.scale(mapNode.getY(), scale));
				jsonObj.put("s", mapNode.getSeverity());
				jsonObj.put("i", mapNode.getIconName());
				if (!hasPageId) {
					jsonObj.put("pageId", pageId);
					hasPageId = true;
				}
				if (!mapNode.isLeafNode()) {
					jsonObj.put("container", true);
					jsonObj.put("mapName",
							((MapContainerNode) mapNode).getMapName());
				}
				jsonNodes.add(jsonObj);
				mapNode.setSelected(false);
			}
			if (!jsonNodes.isEmpty()) {
				jsonObject.put("ntp", "alarms");
				jsonObject.put("alarms", jsonNodes);
				return jsonObject;
			}
		}
		// rogues or clients or summary information
		if (null != refreshTasks) {
			short count = 0;
			while (count++ < refreshTasks.size()) {
				Short task = refreshTasks.poll();
				if (null != task) {
					refreshTasks.offer(task);
					if (rogueChecked && task == REFRESH_TYPE_ROGUES) {
						refreshRogues = true;
						break;
					}
					if (clientChecked && task == REFRESH_TYPE_CLIENTS) {
						refreshClients = true;
						break;
					}
					if (summaryChecked && task == REFRESH_TYPE_SUMMARY) {
						refreshSummary = true;
						break;
					}
				}
			}
			if (refreshRogues) {
				refreshRogues = false;
				return MapsAction.prepareRogues(mapContainerNode,
						this.getMapNodes(mapId, pageId), pageId, scale);
			}
			if (refreshClients) {
				refreshClients = false;
				return MapsAction.prepareClients(mapContainerNode,
						this.getMapNodes(mapId, pageId), pageId, scale);
			}
			if (refreshSummary) {
				refreshSummary = false;
				// retrieve summary information.
				return MapSettingsAction.retrieveSummaryInfo(mapId);
			}
		}
		return jsonObject;
	}

	private long latchId;

	private int heatmapResolution;

	public synchronized void createHeatMapLatch(long latchId,
			int heatmapResolution) {
		this.heatmapResolution = heatmapResolution;
		this.latchId = latchId;
	}

	public synchronized Integer getHeatMapLatch(long latchId) {
		if (this.latchId != latchId) {
			return null;
		}
		return heatmapResolution;
	}

	public synchronized long getHeatmapLatchId() {
		return latchId;
	}

	public boolean isTreeNodeExpanded(Long id) {
		return expandedTreeNodes.get(id) != null;
	}

	public void setTreeNodeExpanded(Long id, boolean expanded) {
		if (expanded) {
			expandedTreeNodes.put(id, id);
		} else {
			expandedTreeNodes.remove(id);
		}
	}
}
