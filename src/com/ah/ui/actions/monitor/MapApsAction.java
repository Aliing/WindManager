package com.ah.ui.actions.monitor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

import com.ah.be.common.MapNodeUtil;
import com.ah.be.performance.dataretention.NetworkDeviceConfigTracking;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmPermission;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.MapMgmt;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapLeafNode;
import com.ah.bo.monitor.MapNode;
import com.ah.ui.actions.Navigation;
import com.ah.ui.actions.SessionKeys;
import com.ah.ui.actions.hiveap.HiveApAction;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/*
 * @author Chris Scheers
 */

public class MapApsAction extends DrawHeatmapAction {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(
			MapApsAction.class.getSimpleName());

	public String execute() throws Exception {
		try {
			if ("avlb".equals(operation)) {
				log.info_ln("avlb: " + id + ", pageId: " + pageId
						+ ", domain: " + domainId);
				jsonObject = new JSONObject();
				jsonObject.put("pageId", pageId);
				jsonObject.put("aps", findUnassignedAps());
				return "json";
			} else if ("upaps".equals(operation)) {
				log.info_ln("avlb: " + id + ", pageId: " + pageId
						+ ", domain: " + domainId + ", selectedIds: "
						+ selectedIds);
				MgrUtil.removeSessionAttribute(SessionKeys.MR_HEAT_MAP);
				jsonObject = new JSONObject();
				jsonObject.put("pageId", pageId);
				assignAps();
				return "json";
			} else if ("tree".equals(operation)) {
				log.info("tree operation.");
				jsonString = createHierarchy();
				return "json";
			}
		} catch (Exception e) {
			log.error("execute", "execute error. operation:" + operation, e);
			addActionError(MgrUtil.getUserMessage(e));
		}
		return null;
	}

	private Collection<JSONObject> findUnassignedAps() throws Exception {
		List aps = QueryUtil.executeQuery("select id, hostName from "
				+ HiveAp.class.getSimpleName(), new SortParams("hostName"),
				new FilterParams("mapContainer is null", new Object[] {}),
				domainId, 1000);
		log.info_ln("# unassigned APs: " + aps.size());
		Collection<JSONObject> jsonNodes = new Vector<JSONObject>();
		for (Object[] obj : (List<Object[]>) aps) {
			Long id = (Long) obj[0];
			String name = (String) obj[1];
			JSONObject jo = new JSONObject();
			jo.put("id", id);
			jo.put("nm", name);
			jsonNodes.add(jo);
		}
		return jsonNodes;
	}

	private void assignAps() throws Exception {
		MapAlarmsCache<?> mapAlarmsCache = getMapAlarmsCache();
		Set<MapNode> nodes = mapAlarmsCache.getMapNodes(id, pageId);
		if (nodes == null) {
			log.info_ln("Wrong page ?");
			return;
		}
		for (MapNode node : nodes) {
			if (selectedIds != null && selectedIds.remove(node.getId())) {
				log.info_ln("This node was already assigned: " + node.getId()
						+ ", " + node.getLabel());
			} else if (node.isLeafNode()) {
				log.info_ln("This node is no longer assigned: " + node.getId()
						+ ", " + node.getLabel());
				HiveAp ap = ((MapLeafNode) node).getHiveAp();
				if (ap != null) { // Otherwide, must be place holder ('M') node
					ap = (HiveAp) findBoById(HiveAp.class, ap.getId());
					if (ap != null) {
						BoMgmt.getMapMgmt().updateHiveApWithPropagation(ap,
								null, ap.isConnected(), ap.getManageStatus());
						// generate realm name when Topology Map changes
						updateRealmName(ap.getId(), null);
						updateHiveApMapChanged(ap, null);
					}
				}
			}
		}
		if (selectedIds != null) {
			MapContainerNode mapContainerNode = id == null ? null
					: (MapContainerNode) findBoById(MapNode.class, id);
			for (Long apId : selectedIds) {
				HiveAp ap = (HiveAp) findBoById(HiveAp.class, apId);
				log.info_ln("Assigning AP: " + ap.getId() + ", "
						+ ap.getHostName() + "to: " + mapContainerNode);
				BoMgmt.getMapMgmt().updateHiveApWithPropagation(ap,
						mapContainerNode, ap.isConnected(),
						ap.getManageStatus());

				// generate realm name when Topology Map changes
				Long topologyMapId = mapContainerNode == null ? null
						: mapContainerNode.getId();
				updateRealmName(ap.getId(), topologyMapId);

				updateHiveApMapChanged(ap, mapContainerNode);
			}
		}
	}

	private void updateHiveApMapChanged(HiveAp hiveAP,
			MapContainerNode mapContainerNode) {
		if (hiveAP == null) {
			return;
		}
		final Long vHMdomain = hiveAP.getOwner().getId();
		String[] tags = null;
		List<String> tagsStr = new ArrayList<>();

		if (null != hiveAP.getClassificationTag1()
				&& !"".equals(hiveAP.getClassificationTag1())) {
			tagsStr.add(hiveAP.getClassificationTag1());
		}
		if (null != hiveAP.getClassificationTag2()
				&& !"".equals(hiveAP.getClassificationTag2())) {
			tagsStr.add(hiveAP.getClassificationTag2());
		}
		if (null != hiveAP.getClassificationTag3()
				&& !"".equals(hiveAP.getClassificationTag3())) {
			tagsStr.add(hiveAP.getClassificationTag3());
		}
		if (null != tagsStr && tagsStr.size() > 0) {
			tags = new String[tagsStr.size()];
			tagsStr.toArray(tags);
		}
		if (mapContainerNode == null) {
			String sql = "select id from map_node where parent_map_id = "
					+ "(select id from map_node where parent_map_id is null) and owner="
					+ vHMdomain;
			List<?> list = QueryUtil.executeNativeQuery(sql, 1);
			if (!list.isEmpty()) {
				NetworkDeviceConfigTracking.topologyChanged(
						Calendar.getInstance(), vHMdomain,
						hiveAP.getMacAddress(), hiveAP.getTimeZoneOffset(),
						new long[] { Long.parseLong(list.get(0).toString()) },
						tags);
			}
		} else {
			NetworkDeviceConfigTracking.topologyChanged(Calendar.getInstance(),
					vHMdomain, hiveAP.getMacAddress(),
					hiveAP.getTimeZoneOffset(),
					new long[] { mapContainerNode.getId() }, tags); // TODO
																	// topologyGroupPkFromTop(exclusive)ToBottom(inclusive)
		}
	}

	private void updateRealmName(Long apId, Long topologyMapId) {
		try {
			final class QueryConfigTemplate implements QueryBo {
				public Collection<HmBo> load(HmBo bo) {
					if (bo instanceof HiveAp) {
						HiveAp hiveAp = (HiveAp) bo;
						if (hiveAp.getConfigTemplate() != null) {
							hiveAp.getConfigTemplate().getId();
						}
					}

					return null;
				}
			}
			HiveAp hiveApBo = QueryUtil.findBoById(HiveAp.class, apId,
					new QueryConfigTemplate());
			String realmName = HiveApAction.generateRealmName(
					hiveApBo.getConfigTemplateId(), topologyMapId,
					hiveApBo.isLockRealmName(), hiveApBo.getRealmName());
			hiveApBo.setRealmName(realmName);

			QueryUtil.updateBo(hiveApBo);
		} catch (Exception e) {
			log.error("updateRealmName", "update realm name error");
			e.printStackTrace();
		}
	}

	private String createHierarchy() throws Exception {
		MapContainerNode rootMap = BoMgmt.getMapMgmt().getRootMap();
		HmUserGroup userGroup = userContext == null ? null : userContext
				.getUserGroup();
		Long worldMapId = null;
		Long domainId = QueryUtil.getDomainFilter(userContext);
		if (domainId != null) {
			worldMapId = BoMgmt.getMapMgmt().getWorldMapId(domainId);
		}
		MapAlarmsCache cache = (MapAlarmsCache) MgrUtil
				.getSessionAttribute(SessionKeys.MAP_ALARMS_CACHE);
		boolean featureWrite = true;
		if (!userGroup.isAdministrator()) {
			HmPermission featurePermission = userGroup.getFeaturePermissions()
					.get(Navigation.L2_FEATURE_MAP_VIEW);
			featureWrite = featurePermission != null
					&& featurePermission
							.hasAccess(HmPermission.OPERATION_WRITE);
		}
		StringBuffer results = new StringBuffer("");
		generateTree(results, userGroup, cache, rootMap,
				userContext.isOrderFolders(), worldMapId, getShowDomain(),
				featureWrite, 0);
		return results.toString();
	}

	private void generateTree(StringBuffer results, HmUserGroup userGroup,
			MapAlarmsCache cache, final MapContainerNode map,
			final boolean orderFolders, Long worldMapId, boolean showDomain,
			boolean featureWrite, int level) {
		if (map == null) {
			return;
		}
		if (map.getParentMap() != null) {
			String mapName = map.getMapName();
			boolean uninitialized = MapMgmt.VHM_ROOT_MAP_NAME.equals(mapName);
			boolean expanded = cache == null ? false : cache
					.isTreeNodeExpanded(map.getId());
			mapName = mapName == null ? "" : mapName.replace("\\", "\\\\")
					.replace("'", "\\\'");
			if (map.getParentMap().getParentMap() == null) {
				if (worldMapId != null && !map.getId().equals(worldMapId)) {
					return;
				}
				// update the uninitialized VHM root map name
				if (uninitialized) {
					mapName = "Uninitialized";
				}
				// set root map node expand status default.
				if (null == cache) {
					expanded = true;
				}
				if (showDomain) {
					String domainName = map.getOwner().getDomainName();
					domainName = domainName == null ? "" : domainName;
					mapName = domainName + " - " + mapName;
				}
			}
			results.append("{label:'"
					+ StringEscapeUtils.escapeHtml4(mapName)
					+ "',id:'"
					+ map.getId()
					+ "',tp:'"
					+ map.getMapType()
					+ "',wp:"
					+ (getInstanceWrite(userGroup, map, showDomain) && featureWrite)
					+ ",lvl: " + level + ", uiz:" + uninitialized
					+ ",expanded:" + expanded);
			results.append(",items:[");
		}
		if (map.getMapType() != MapContainerNode.MAP_TYPE_FLOOR) {
			// order by id
			Set<MapNode> children = map.getChildNodes();
			List<MapNode> list = MapNodeUtil.sortMapTree(children, orderFolders, map,true);
			boolean needComma = false;
			for (MapNode child : list) {
				if (child.isLeafNode()) {
					continue;
				}
				if (userGroup != null && !userGroup.isAdministrator()) {
					HmPermission permission = userGroup
							.getInstancePermissions().get(child.getId());
					if (permission == null) {
						continue;
					}
				}
				if (needComma) {
					results.append(",");
				} else {
					needComma = true;
				}
				generateTree(results, userGroup, cache,
						(MapContainerNode) child, orderFolders, worldMapId,
						showDomain, featureWrite, level + 1);
			}
		}
		if (map.getParentMap() != null) {
			results.append("]}");
		}
	}

	private boolean getInstanceWrite(HmUserGroup userGroup, MapNode mapNode,
			boolean showDomain) {
		boolean instanceWrite = true;
		if (userGroup != null && !userGroup.isAdministrator()) {
			HmPermission permission = userGroup.getInstancePermissions().get(
					mapNode.getId());
			if (permission != null) {
				instanceWrite = permission
						.hasAccess(HmPermission.OPERATION_WRITE);
			}
		} else if (userGroup != null && userGroup.isAdministrator()) {
			if (showDomain) {
				if (!HmDomain.HOME_DOMAIN.equals(mapNode.getOwner()
						.getDomainName())) {
					instanceWrite = false;
				}
			}
		}
		return instanceWrite;
	}

	public String getJSONString() {
		if (jsonString != null) {
			return jsonString;
		} else {
			return super.getJSONString();
		}
	}

	private String jsonString;

	private Long pageId;

	public void setPageId(Long pageId) {
		this.pageId = pageId;
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_MAP_VIEW);
	}
}
