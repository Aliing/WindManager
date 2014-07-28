package com.ah.ui.actions.monitor;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.ImageIcon;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.ah.be.db.configuration.ConfigurationUtils;
import com.ah.be.performance.dataretention.NetworkDeviceConfigTracking;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.dashboard.AhDashboard;
import com.ah.bo.mgmt.AccessControl;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.Folder;
import com.ah.bo.mgmt.LocationTracking;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.AccessControl.CrudOperation;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapLink;
import com.ah.bo.monitor.MapNode;
import com.ah.bo.monitor.MapSettings;
import com.ah.bo.monitor.PlannedAP;
import com.ah.bo.monitor.Trex;
import com.ah.bo.monitor.Vertex;
import com.ah.bo.monitor.Wall;
import com.ah.bo.performance.AhNewReport;
import com.ah.bo.performance.AhReport;
import com.ah.events.BoEvent;
import com.ah.events.BoEvent.BoEventType;
import com.ah.events.impl.BoObserver;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.Navigation;
import com.ah.ui.actions.SessionKeys;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class MapSettingsAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(
			MapSettingsAction.class.getSimpleName());

	@Override
	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		try {
			if ("new".equals(operation)) {
				log.info("execute", "New map under: " + domainMapId);
				String domainName = getDomainNameByMapId(domainMapId);
				preparePanelDetails(domainName);
				return "json";
			} else if ("createMap".equals(operation)) {
				log.info("execute", "Create new map: " + mapName + ", under: "
						+ id + ", icon name:" + mapIcon + ", sizeX: " + sizeX
						+ ", sizeY: " + sizeY + ", env: " + mapEnv
						+ ", width: " + mapWidth + ", unit: " + mapWidthUnit
						+ ", mapType: " + mapType + ", AP Elevation: "
						+ apElevation + ", loss: " + loss + ", mapAddress: "
						+ mapAddress);
				if (null != mapName && mapName.trim().length() > 0) {
					mapName = mapName.trim();
					MapContainerNode parentNode = (MapContainerNode) findBoById(
							MapNode.class, id, this);
					if (null != parentNode) {
						// auto expand parent node
						MapAlarmsCache cache = (MapAlarmsCache) MgrUtil
								.getSessionAttribute(SessionKeys.MAP_ALARMS_CACHE);
						if (cache != null) {
							cache.setTreeNodeExpanded(parentNode.getId(), true);
						}
						if (mapType < 0) {
							log.info_ln("Clone function: "
									+ parentNode.getMapName() + ", parent: "
									+ parentNode.getParentMap().getMapName()
									+ ", perimeters: "
									+ parentNode.getPerimeter().size());
							if (parentNode.getMapType() == MapContainerNode.MAP_TYPE_FLOOR) {
								cloneNode(getClonePeerNode(parentNode),
										mapName, null, parentNode, null);
							} else {
								log.info_ln("Clone building to region: "
										+ region + ", prefix: " + prefix);
								setDefaultPrefix();
								List<MapContainerNode> floors = reverseSortFloors(parentNode);
								// Floor names are reusable
								// if (validateCloneFloors(floors, parentNode)
								// == null) {
								MapContainerNode regionNode = (MapContainerNode) findBoById(
										MapNode.class, region, this);
								cloneNode(regionNode, mapName, mapAddress,
										parentNode, floors);
								// }
							}
						} else {
							createMapContainerNode(parentNode, mapEnv, mapName,
									mapType, mapWidthUnit, mapImage, sizeX,
									sizeY, mapWidth, apElevation, mapIcon,
									loss, mapAddress, null);
						}
					}
				}
				return SUCCESS;
			} else if ("moveMap".equals(operation)) {
				log.info_ln("Move map: " + id + " to region: " + region);
				MapContainerNode parentNode = (MapContainerNode) findBoById(
						MapNode.class, id);
				if (parentNode != null) {
					log.info_ln("Move map: " + parentNode.getMapName()
							+ " to region: " + region);
					MapContainerNode regionNode = (MapContainerNode) findBoById(
							MapNode.class, region);
					if (regionNode != null) {
						parentNode.setParentMap(regionNode);
						BoMgmt.getMapMgmt().updateMapContainer(parentNode);

						// tracking
						NetworkDeviceConfigTracking.topologyGroupChanged(
								Calendar.getInstance(), parentNode.getOwner()
										.getId(), region, id);
					}
				}
				return SUCCESS;
			} else if ("moveFloor".equals(operation)) {
				log.info_ln("Move node: " + id + ", up: " + useWidth);
				MapContainerNode floorNode = (MapContainerNode) findBoById(
						MapNode.class, id, this);
				jsonObject = new JSONObject();
				if (floorNode == null) {
					jsonObject.put("success", false);
					return "json";
				}
				log.info_ln("Move node: " + floorNode.getMapName());
				moveFloor(floorNode, useWidth);
				jsonObject.put("success", true);
				return "json";
			} else if ("removeMap".equals(operation)) {
				log.info("execute", "Remove map which id is: " + id);
				MapContainerNode removeMap = (MapContainerNode) QueryUtil
						.findBoById(MapNode.class, id, this);
				Long parentId = removeMap.getParentMap().getId();
				for (PlannedAP plannedAP : removeMap.getPlannedAPs()) {
					BoMgmt.getPlannedApMgmt()
							.removePlannedAP(plannedAP.getId());
				}
				removeMap.setWalls(Collections.EMPTY_LIST);
				removeMap.setPerimeter(Collections.EMPTY_LIST);
				removeMap.setPlannedAPs(Collections.EMPTY_SET);
				QueryUtil.updateBo(removeMap);
				QueryUtil.bulkRemoveBos(Trex.class, new FilterParams(
						"parentMap", removeMap));
				Collection<Long> removeMapId = new ArrayList<Long>();
				removeMapId.add(id);
				removeBos(MapNode.class, removeMapId);
				BoMgmt.getBoEventMgmt().publishBoEvent(
						new BoEvent(removeMap, BoEventType.REMOVED));
				if (id.equals(DrawHeatmapAction.getSelectedMapId())) {
					DrawHeatmapAction.setSelectedMapId(parentId);
				}
				return SUCCESS;
			} else if ("edit".equals(operation)) {
				log.info("execute", "edit map which id is: " + id);
				String domainName = getDomainNameByMapId(id);
				preparePanelDetails(domainName);
				return "json";
			} else if ("updateMap".equals(operation)) {
				log.info("execute", "update map which id is: " + id
						+ ", new map name:" + mapName + ", env: " + mapEnv
						+ ", icon name:" + mapIcon + ", AP Elevation: "
						+ apElevation + ", loss: " + loss + ", mapAddress: "
						+ mapAddress);
				if (null != mapName && mapName.trim().length() > 0) {
					mapName = mapName.trim();
					MapContainerNode mapNode = (MapContainerNode) findBoById(
							MapNode.class, id);
					if (null != mapNode) {
						updateMapContainerNode(mapNode, mapEnv, mapName,
								mapImage, mapIcon, mapWidthUnit, sizeX, sizeY,
								mapWidth, apElevation, loss, mapAddress);
					}
				}
				return SUCCESS;
			} else if ("editGlobalParams".equals(operation)) {
				log.info("execute", "edit map refresh interval. top map id:"
						+ id);
				prepareGlobalParams(id);
				return "json";
			} else if ("setGlobalParams".equals(operation)) {
				log.info("execute", "ppFlag:" + ppFlag + ", map id:" + id);
				MgrUtil.removeSessionAttribute(SessionKeys.MR_HEAT_MAP);
				boolean result = setPollingInterval(
						Integer.parseInt(refreshInterval), ppFlag, id);
				jsonObject = new JSONObject();
				if (result) {
					String name = MgrUtil
							.getUserMessage("topology.map.global.settings");
					jsonObject.put("msg",
							MgrUtil.getUserMessage(OBJECT_UPDATED, name));
					generateAuditLog(
							HmAuditLog.STATUS_SUCCESS,
							MgrUtil.getUserMessage("hm.audit.log.change.toplogical.map.interval")
									+ refreshInterval);
				} else {
					generateAuditLog(
							HmAuditLog.STATUS_FAILURE,
							MgrUtil.getUserMessage("hm.audit.log.change.toplogical.map.interval")
									+ refreshInterval);
				}
				return "json";
			} else if ("deleteImage".equals(operation)) {
				log.info("execute", "Delete map image which name is: "
						+ imageName + ", domain map container id:"
						+ domainMapId);
				String domainName = getDomainNameByMapId(domainMapId);
				boolean result = BeTopoModuleUtil.deleteBackgroundImage(
						imageName, domainName);
				preparePanelDetails(domainName);
				if (result) {
					String name = MgrUtil
							.getUserMessage("topology.map.background.image");
					jsonObject.put("msg", MgrUtil.getUserMessage(
							"info.map.background.image.removed", name + ":"
									+ imageName));
					generateAuditLog(
							HmAuditLog.STATUS_SUCCESS,
							MgrUtil.getUserMessage("hm.audit.log.remove.toplogical.map.background.image")
									+ imageName);
				} else {
					generateAuditLog(
							HmAuditLog.STATUS_FAILURE,
							MgrUtil.getUserMessage("hm.audit.log.remove.toplogical.map.background.image")
									+ imageName);
				}
				return "json";
			} else if ("uploadImage".equals(operation)) {
				jsonObject = new JSONObject();
				String domainName = getDomainNameByMapId(domainMapId);
				String fileName = FiledataFileName;
				File file = Filedata;
				boolean showSucMsg = false;
				if (null == FiledataFileName && null == Filedata) {
					fileName = imagedataFileName;
					file = imagedata;
					showSucMsg = true;
				}
				log.info("execute", "Upload map image which name is: "
						+ fileName + ", domain name:" + domainName);
				try {
				    //check permission
                    AccessControl.checkUserAccess(getUserContext(), getSelectedL2FeatureKey(), CrudOperation.UPDATE);
                    
                    if (fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".png")) {
                        BeTopoModuleUtil.addBackgroundImage(fileName, file,
                                domainName);
                        jsonObject.put("uploaded", true);
                        jsonObject.put("image", fileName);
                        jsonObject.put(
                                "imageFull",
                                MapsAction.getBackgroundPathByDomain(request,
                                        domainName) + fileName);
                        if (showSucMsg) {
                            jsonObject.put("sucMsg", MgrUtil.getUserMessage(
                                    "info.fileUploaded", fileName));
                        }
                    } else {
                        jsonObject.put("uploaded", false);
                        jsonObject.put("error", MgrUtil.getUserMessage("error.topo.import.background"));
                    }
				} catch (Exception e) {
					log.error("execute", "upload image error.", e);
					String msg = e.getMessage();
					if (null != msg && !"".equals(msg)) {
						jsonObject.put("uploaded", false);
						jsonObject.put("error", msg);
					}
				}
				return "json";
			} else if ("checkMapName".equals(operation)) {
				log.info("execute", "Check map name:" + mapName + ", Id:" + id
						+ ", domainMapId:" + domainMapId + ", mapType: "
						+ mapType + ", prefix: " + prefix);
				if (null != mapName && mapName.trim().length() > 0) {
					mapName = mapName.trim();
                   if(Jsoup.isValid(mapName, Whitelist.none())) {
                       setDefaultPrefix();
                       checkMapName(domainMapId);
                    } else {
                        jsonObject = new JSONObject();
                        jsonObject.put("v", "Please input valid name");
                    }
				}
				return "json";
			} else if ("checkDeletionRestrict".equals(operation)) {
				log.info("execute", "Check map id:" + id
						+ ", for deletion restrict");
				checkDeletionRestrict();
				return "json";
			} else if ("retrieveSummaryInfo".equals(operation)) {
				log.info("execute", "retrieve summary info under map id:" + id);
				jsonObject = retrieveSummaryInfo(id);
				return "json";
			} else if ("reviewImages".equals(operation)) {
				log.info("execute", "review background images"
						+ ", domainMapId:" + domainMapId);
				String domainName = getDomainNameByMapId(domainMapId);
				retrieveImageInfo(domainName);
				return "json";
			} else if ("resizeMap".equals(operation)) {
				log.info("execute", "Resize map: " + id);
				if (id != null && null != mapWidth && mapWidth > 0) {
					MapContainerNode mapNode = (MapContainerNode) findBoById(
							MapNode.class, id);
					log.info("execute", "Map '" + mapNode.getMapName()
							+ "' use width: " + useWidth + ", section width: "
							+ mapWidth + ", actualWidth: " + actualMapWidth);
					mapNode.setLengthUnit(mapWidthUnit);
					if (mapNode.getActualWidth() == 0) {
						mapNode.setApElevation(getDefaultApElevation(mapNode));
					}
					if (useWidth) {
						actualMapWidth = actualMapWidth / mapWidth
								* mapNode.getWidth();
						mapNode.setActualWidth(actualMapWidth);
						mapNode.setActualHeight(mapNode.getActualWidth()
								* mapNode.getHeight() / mapNode.getWidth());
					} else {
						actualMapWidth = actualMapWidth / mapWidth
								* mapNode.getHeight();
						mapNode.setActualHeight(actualMapWidth);
						mapNode.setActualWidth(mapNode.getActualHeight()
								* mapNode.getWidth() / mapNode.getHeight());
					}
					QueryUtil.updateBo(mapNode);
					log.info("execute",
							"New map size: (" + mapNode.getActualWidth() + ", "
									+ mapNode.getActualHeight() + ")");
				}
				jsonObject = new JSONObject();
				jsonObject.put("success", true);
				return "json";
			} else {
				return SUCCESS;
			}
		} catch (Exception e) {
			e.printStackTrace();
			addActionError(MgrUtil.getUserMessage(e));
			return SUCCESS;
		}
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_MAP_VIEW);
	}

	private String getDomainNameByMapId(Long mapId) throws Exception {
		MapContainerNode mapNode = (MapContainerNode) findBoById(MapNode.class,
				mapId);
		String domainName = null;
		if (null != mapNode) {
			domainName = mapNode.getOwner().getDomainName();
		}
		return domainName;
	}

	public Collection<HmBo> load(HmBo bo) {
		if (bo == null) {
			return null;
		}
		MapContainerNode mapContainerNode = (MapContainerNode) bo;
		if ("links".equals(operation)) {
			for (MapLink mapLink : mapContainerNode.getChildLinks().values()) {
				// Just to trigger load from database
			}
		} else if ("checkDeletionRestrict".equals(operation)) {
			for (MapLink mapLink : mapContainerNode.getChildLinks().values()) {
				// Just to trigger load from database
			}
			for (MapNode mapNode : mapContainerNode.getChildNodes()) {
				// Just to trigger load from database
			}
		} else if ("createMap".equals(operation)) {
			if (mapContainerNode.getMapType() == MapContainerNode.MAP_TYPE_FLOOR) {
				for (MapNode mapNode : mapContainerNode.getParentMap()
						.getChildNodes()) {
					// Just to trigger load from database
				}
			} else {
				for (MapNode mapNode : mapContainerNode.getChildNodes()) {
					// Just to trigger load from database
				}
			}
			if (mapType < 0) { // Clone
				for (PlannedAP plannedAP : mapContainerNode.getPlannedAPs()) {
					// Just to trigger load from database
				}
				for (Vertex vertex : mapContainerNode.getPerimeter()) {
					// Just to trigger load from database
				}
				for (Wall wall : mapContainerNode.getWalls()) {
					// Just to trigger load from database
				}
			}
		} else if ("checkMapName".equals(operation)) {
			if (mapType < 0) { // Clone
				for (MapNode mapNode : mapContainerNode.getChildNodes()) {
					// Just to trigger load from database
				}
			}
		} else if ("moveFloor".equals(operation)) {
			for (MapNode mapNode : mapContainerNode.getParentMap()
					.getChildNodes()) {
				// Just to trigger load from database
			}
		} else if ("removeMap".equals(operation)) {
			mapContainerNode.getPlannedAPs().size();
		}

		mapContainerNode.getOwner().getId();
		while (mapContainerNode.getParentMap() != null) {
			mapContainerNode = mapContainerNode.getParentMap();
			mapContainerNode.getId();
		}
		return null;
	}

	protected List<MapContainerNode> reverseSortFloors(MapContainerNode building) {
		List<MapContainerNode> floors = new ArrayList<MapContainerNode>();
		for (MapNode mapNode : building.getChildNodes()) {
			if (mapNode.isLeafNode()) {
				continue;
			}
			floors.add((MapContainerNode) mapNode);
		}
		Collections.sort(floors, new Comparator<MapContainerNode>() {
			@Override
			public int compare(MapContainerNode o1, MapContainerNode o2) {
				return o1.getMapOrder() - o2.getMapOrder();
			}
		});
		return floors;
	}

	private void moveFloor(MapContainerNode floor, boolean up) throws Exception {
		MapContainerNode building = floor.getParentMap();
		List<MapContainerNode> floors = reverseSortFloors(building);
		for (int i = 0; i < floors.size(); i++) {
			MapContainerNode f = floors.get(i);
			if (f.getId().equals(floor.getId())) {
				MapContainerNode f2 = null;
				if (f.getMapType() == MapContainerNode.MAP_TYPE_FLOOR) {
					log.info_ln("Move this floor: " + f.getMapName());
					f2 = up ? floors.get(i + 1) : floors.get(i - 1);
				} else {
					log.info_ln("Move this building or folder: "
							+ f.getMapName());
					f2 = up ? floors.get(i - 1) : floors.get(i + 1);
				}
				int f2Order = f2.getMapOrder();
				f2.setMapOrder(f.getMapOrder());
				f.setMapOrder(f2Order);
				Collection<MapContainerNode> floorsMoved = new Vector<MapContainerNode>();
				floorsMoved.add(f);
				floorsMoved.add(f2);
				QueryUtil.bulkUpdateBos(floorsMoved);
				return;
			}
		}
	}

	protected String validateCloneFloors(List<MapContainerNode> floors,
			MapContainerNode cloned) {
		for (MapContainerNode floor : floors) {
			String newName = prefix + floor.getMapName();
			if (newName.length() > 32) {
				log.info_ln("Floor name too long: " + newName);
				return MgrUtil
						.getUserMessage("error.floorNameTooLong", newName);
			}
			List<?> boIds = QueryUtil.executeQuery(
					"select bo.id from " + MapContainerNode.class.getSimpleName() + " bo", null,
					new FilterParams("mapName", newName), cloned.getOwner()
							.getId());
			if (boIds.size() > 0) {
				log.info_ln("Floor name already exists: " + newName);
				return MgrUtil.getUserMessage("error.objectExists", newName);
			}
		}
		return null;
	}

	protected Long cloneNode(MapContainerNode parentNode, String cloneName,
			String cloneAddress, MapContainerNode cloned,
			List<MapContainerNode> floors) throws Exception {
		Long cloneId = createMapContainerNode(parentNode,
				cloned.getEnvironment(), cloneName, cloned.getMapType(),
				cloned.getLengthUnit(), cloned.getBackground(),
				cloned.getActualWidth(), cloned.getActualHeight(),
				cloned.getActualWidth(), cloned.getApElevation(),
				cloned.getIconName(), cloned.getFloorLoss(), cloneAddress,
				cloned);
		if (floors == null) {
			return cloneId;
		}
		for (MapContainerNode floor : floors) {
			MapContainerNode clone = (MapContainerNode) findBoById(
					MapNode.class, cloneId, this);
			floor = (MapContainerNode) findBoById(MapNode.class, floor.getId(),
					this);
			log.info_ln("Clone floor: " + floor.getMapName());
			cloneNode(clone, prefix + floor.getMapName(), null, floor, null);
		}
		return cloneId;
	}

	protected void setDefaultPrefix() {
		if (prefix == null || prefix.trim().length() == 0) {
			prefix = mapName + " ";
		}
		prefix = ""; // Floor names can be reused.
	}

	protected synchronized void checkMapName(Long mapId) throws Exception {
		jsonObject = new JSONObject();
		MapContainerNode mapNode = findBoById(MapContainerNode.class, mapId,
				this);
		Long domainId = null;
		if (null != mapNode) {
			domainId = mapNode.getOwner().getId();
		}
		List<?> boIds = QueryUtil
				.executeQuery(
						"select bo.id, bo.mapType, bo.parentMap.id from " + MapContainerNode.class.getSimpleName() + " bo",
						null, new FilterParams("mapName", mapName), domainId);
		MapContainerNode building = null;
		if (mapNode.getMapType() == MapContainerNode.MAP_TYPE_BUILDING) {
			building = mapNode;
		} else if (mapNode.getMapType() == MapContainerNode.MAP_TYPE_FLOOR) {
			building = mapNode.getParentMap();
		}
		if (null == id) {
			// it's a new or clone operation
			if (boIds.size() > 0) {
				String msg = MgrUtil.getUserMessage("error.objectExists",
						mapName);
				if (building == null) {
					log.info_ln("Not creating a floor, don't allow name reuse.");
					jsonObject.put("v", msg);
				} else {
					for (Object[] obj : (List<Object[]>) boIds) {
						Long otherMapId = (Long) obj[0];
						Short otherMapType = (Short) obj[1];
						Long otherParentMapId = (Long) obj[2];
						if (otherMapType != MapContainerNode.MAP_TYPE_FLOOR) {
							log.info_ln("Matching node is not a floor, don't allow name reuse.");
							jsonObject.put("v", msg);
							break;
						} else if (building.getId().equals(otherParentMapId)) {
							log.info_ln("Don't allow name reuse within same building");
							jsonObject.put("v", msg);
							break;
						}
					}
				}
			}
			if (mapType < 0) {
				// Clone
				if (mapNode.getMapType() == MapContainerNode.MAP_TYPE_BUILDING) {
					log.info_ln("This is a building clone: "
							+ mapNode.getMapName());
					building = (MapContainerNode) findBoById(MapNode.class,
							mapNode.getId(), this);
					List<MapContainerNode> floors = reverseSortFloors(building);
					// Floor names can be reused.
					// String msg = validateCloneFloors(floors, building);
					// if (msg != null) {
					// jsonObject.put("v", msg);
					// }
				}
			}
		} else {
			// it's a update operation
			if (boIds.size() > 0) {
				for (Object[] obj : (List<Object[]>) boIds) {
					Long otherMapId = (Long) obj[0];
					Short otherMapType = (Short) obj[1];
					Long otherParentMapId = (Long) obj[2];
					String msg = MgrUtil.getUserMessage(
							"error.constraintViolation", mapName);
					if (otherMapId.longValue() == id.longValue()) {
						log.info_ln("Updating with same name.");
					} else if (building == null) {
						log.info_ln("Not updating a floor, don't allow name reuse.");
						jsonObject.put("v", msg);
						break;
					} else if (building.getId().equals(otherParentMapId)) {
						log.info_ln("Don't allow name reuse within same building");
						jsonObject.put("v", msg);
						break;
					} else if (otherMapType != MapContainerNode.MAP_TYPE_FLOOR) {
						log.info_ln("Matching node is not a floor, don't allow name reuse.");
						jsonObject.put("v", msg);
						break;
					}
				}
			}
		}
	}

	protected void checkDeletionRestrict() throws Exception {
		jsonObject = new JSONObject();
		if (null != id) {
			MapContainerNode container = findBoById(MapContainerNode.class, id,
					this);
			if (null != container) {
				Collection<MapLink> links = container.getChildLinks().values();
				Set<MapNode> nodes = container.getChildNodes();
				int level = getMapContainerLevel(container);
				if (level == 1) {
					jsonObject.put("removable", false);
					jsonObject.put("msg", MgrUtil.getUserMessage(
							"error.map.deletion.top.level",
							container.getMapName()));
					return;
				}
				if ((null != links && links.size() > 0)
						|| (null != nodes && nodes.size() > 0)) {
					jsonObject.put("removable", false);
					jsonObject.put("msg", MgrUtil.getUserMessage(
							"error.map.deletion.hasChild",
							container.getMapName()));
					return;
				}

				// check used in IP Address
				Set<String> ipAddresses = ConfigurationUtils
						.getRelevantIpAddressName(container);
				if (null != ipAddresses && !ipAddresses.isEmpty()) {
					String featureName = Navigation
							.getFeatureName(L2_FEATURE_IP_ADDRESS);
					jsonObject.put("removable", false);
					jsonObject.put("msg", MgrUtil.getUserMessage(
							"error.map.deletion.used",
							new String[] { container.getMapName(), featureName,
									ipAddresses.toString() }));
					return;
				}
				// check used in MAC Address
				Set<String> macAddresses = ConfigurationUtils
						.getRelevantMacAddressName(container);
				if (null != macAddresses && !macAddresses.isEmpty()) {
					String featureName = Navigation
							.getFeatureName(L2_FEATURE_MAC_OR_OUI);
					jsonObject.put("removable", false);
					jsonObject.put("msg", MgrUtil.getUserMessage(
							"error.map.deletion.used",
							new String[] { container.getMapName(), featureName,
									macAddresses.toString() }));
					return;
				}
				// check used in VLAN
				Set<String> vlans = ConfigurationUtils
						.getRelevantVlanName(container);
				if (null != vlans && !vlans.isEmpty()) {
					String featureName = Navigation
							.getFeatureName(L2_FEATURE_VLAN);
					jsonObject.put("removable", false);
					jsonObject.put("msg", MgrUtil.getUserMessage(
							"error.map.deletion.used",
							new String[] { container.getMapName(), featureName,
									vlans.toString() }));
					return;
				}
				// check used in user attribute
				Set<String> attributes = ConfigurationUtils
						.getRelevantUserAttributeName(container);
				if (null != attributes && !attributes.isEmpty()) {
					String featureName = Navigation
							.getFeatureName(L2_FEATURE_USER_PROFILE_ATTRIBUTE);
					jsonObject.put("removable", false);
					jsonObject.put("msg", MgrUtil.getUserMessage(
							"error.map.deletion.used",
							new String[] { container.getMapName(), featureName,
									attributes.toString() }));
					return;
				}
				// check used in location client watch
				Set<String> watches = ConfigurationUtils
						.getRelevantLocationClientWatchName(container);
				if (null != watches && !watches.isEmpty()) {
					String featureName = Navigation
							.getFeatureName(L2_FEATURE_LOCATIONCLIENTWATCH);
					jsonObject.put("removable", false);
					jsonObject.put("msg", MgrUtil.getUserMessage(
							"error.map.deletion.used",
							new String[] { container.getMapName(), featureName,
									watches.toString() }));
					return;
				}
				// check used in customized report
				Set<String> reports = ConfigurationUtils
						.getRelevantCustomizedReportName(container);
				if (null != reports && !reports.isEmpty()) {
					String featureName = Navigation
							.getFeatureName(L2_FEATURE_CUSTOMREPORT);
					jsonObject.put("removable", false);
					jsonObject.put("msg", MgrUtil.getUserMessage(
							"error.map.deletion.used",
							new String[] { container.getMapName(), featureName,
									reports.toString() }));
					return;
				}

				// check used in other reports
				Set<AhReport> set = ConfigurationUtils
						.getRelevantReports(container);
				if (null != set && !set.isEmpty()) {
					Map<String, Set<String>> map = new HashMap<String, Set<String>>();
					for (AhReport report : set) {
						String featureKey = AhReport
								.getReportLeftMenuName(report.getReportType());
						if (null == map.get(featureKey)) {
							Set<String> names = new HashSet<String>();
							map.put(featureKey, names);
						}
						map.get(featureKey).add(report.getName());
					}
					for (String key : map.keySet()) {
						// only show one feature key one time.
						if (!"null".equals(key)) {
							jsonObject.put("removable", false);
							jsonObject.put("msg", MgrUtil.getUserMessage(
									"error.map.deletion.used", new String[] {
											container.getMapName(),
											"Reports - " + key,
											map.get(key).toString() }));
							return;
						}
					}
				}

				// check used in network summary reports
				Set<AhNewReport> setNew = ConfigurationUtils
						.getRelevantNewReports(container);
				if (null != setNew && !setNew.isEmpty()) {
					Map<String, Set<String>> map = new HashMap<String, Set<String>>();
					for (AhNewReport report : setNew) {
						String featureKey = "Network Summary";
						if (null == map.get(featureKey)) {
							Set<String> names = new HashSet<String>();
							map.put(featureKey, names);
						}
						map.get(featureKey).add(report.getName());
					}
					for (String key : map.keySet()) {
						// only show one feature key one time.
						if (!"null".equals(key)) {
							jsonObject.put("removable", false);
							jsonObject.put("msg", MgrUtil.getUserMessage(
									"error.map.deletion.used", new String[] {
											container.getMapName(),
											"Reports - " + key,
											map.get(key).toString() }));
							return;
						}
					}
				}
				
				// check used in dashboard reports
				Set<AhDashboard> setDash = ConfigurationUtils
						.getRelevantDashboardReports(container);
				if (null != setDash && !setDash.isEmpty()) {
					Map<String, Set<String>> map = new HashMap<String, Set<String>>();
					for (AhDashboard report : setDash) {
						String featureKey = "Dashboard";
						if (null == map.get(featureKey)) {
							Set<String> names = new HashSet<String>();
							map.put(featureKey, names);
						}
						if (report.getUserName()!=null && !report.getUserName().equals("")) {
							map.get(featureKey).add(report.getDashName() + " (user: " + report.getUserName()+ ")");
						} else {
							map.get(featureKey).add(report.getDashName());
						}
					}
					for (String key : map.keySet()) {
						// only show one feature key one time.
						if (!"null".equals(key)) {
							jsonObject.put("removable", false);
							jsonObject.put("msg", MgrUtil.getUserMessage(
									"error.map.deletion.used", new String[] {
											container.getMapName(),
											"Dashboard or Network Summary report",
											map.get(key).toString() }));
							return;
						}
					}
				}
			}
		}
		jsonObject.put("removable", true);
	}

	private int getMapContainerLevel(MapContainerNode mapNode) {
		int level = 0;
		while (mapNode.getParentMap() != null) {
			mapNode = mapNode.getParentMap();
			level++;
		}
		return level;
	}

	public static JSONObject retrieveSummaryInfo(Long parentMapId)
			throws Exception {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("ntp", "summary");
		if (null == parentMapId) {
			return jsonObj;
		}
		Folder folder = BoMgmt.getMapHierarchyCache().getFolder(parentMapId);
		if (null == folder) {
			return jsonObj;
		}
		jsonObj.put("map_Id", String.valueOf(parentMapId));
		jsonObj.put("n_up", String.valueOf(folder.getNewUpApCount()));
		jsonObj.put("n_down", String.valueOf(folder.getNewDownApCount()));
		jsonObj.put("m_up", String.valueOf(folder.getManagedUpApCount()));
		jsonObj.put("m_down", String.valueOf(folder.getManagedDownApCount()));
		jsonObj.put("client", String.valueOf(folder.getClientCount()));
		return jsonObj;
	}

	protected JSONObject editMap(Long mapId) throws Exception {
		if (null == mapId) {
			return null;
		}

		MapContainerNode mapNode = (MapContainerNode) findBoById(MapNode.class,
				mapId);
		if (null != mapNode) {
			JSONObject jsonMap = new JSONObject();
			jsonMap.put("mapName", mapNode.getMapName());
			jsonMap.put("image", mapNode.getBackground());
			jsonMap.put("icon", mapNode.getIconName());
			jsonMap.put("mapWidth", mapNode.getActualWidth());
			jsonMap.put("mapHeight", mapNode.getActualHeight());
			jsonMap.put("apElevation", mapNode.getApElevation());
			jsonMap.put("loss", mapNode.getFloorLoss());
			jsonMap.put("lengthUnit", mapNode.getLengthUnit());
			jsonMap.put("mapEnv", mapNode.getEnvironment());
			jsonMap.put("address",
					mapNode.getAddress() == null ? "" : mapNode.getAddress());
			return jsonMap;
		}
		return null;
	}

	protected void preparePanelDetails(String domainName) throws Exception {
		JSONArray jsonIcons = prepareMapIcons();
		JSONArray jsonImages = prepareMapImages(domainName);
		JSONObject jsonMap = editMap(id);
		jsonObject = new JSONObject();
		jsonObject.put("icons", jsonIcons);
		jsonObject.put("images", jsonImages);
		if (null != jsonMap) {
			jsonObject.put("map", jsonMap);
		}
	}

	protected void retrieveImageInfo(String domainName) throws Exception {
		JSONArray jsonImages = prepareMapImages(domainName);
		jsonObject = new JSONObject();
		jsonObject.put("images", jsonImages);
	}

	private String mapIcon;

	private String mapImage;

	private String refreshInterval;

	private String imageName;

	/** file object */
	private File Filedata;

	/** file name */
	private String FiledataFileName;

	/** file content type */
	// private String FiledataContentType;
	private File imagedata;

	private String imagedataFileName;

	protected String mapName;

	private Double mapWidth, actualMapWidth, sizeX, sizeY, loss;

	private Double apElevation;

	private int mapEnv;

	private short mapWidthUnit;

	private boolean useWidth;

	private Long domainMapId;

	private short mapType;

	private Long region;

	private String prefix;

	private String mapAddress;

	public String getMapAddress() {
		return mapAddress;
	}

	public void setMapAddress(String mapAddress) {
		this.mapAddress = mapAddress;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setRegion(Long region) {
		this.region = region;
	}

	public void setMapType(short mapType) {
		this.mapType = mapType;
	}

	public void setMapWidthUnit(short mapWidthUnit) {
		this.mapWidthUnit = mapWidthUnit;
	}

	public void setSizeX(String sizeX) {
		this.sizeX = sizeX == null ? null : Double.parseDouble(sizeX);
	}

	public void setSizeY(String sizeY) {
		this.sizeY = sizeY == null ? null : Double.parseDouble(sizeY);
	}

	public void setLoss(String loss) {
		this.loss = loss == null ? null : Double.parseDouble(loss);
	}

	public void setMapEnv(int mapEnv) {
		this.mapEnv = mapEnv;
	}

	public void setDomainMapId(Long domainMapId) {
		this.domainMapId = domainMapId;
	}

	public void setUseWidth(boolean useWidth) {
		this.useWidth = useWidth;
	}

	public void setMapWidth(String mapWidth) {
		this.mapWidth = mapWidth == null ? null : Double.parseDouble(mapWidth);
	}

	public void setActualMapWidth(String actualMapWidth) {
		this.actualMapWidth = actualMapWidth == null ? null : Double
				.parseDouble(actualMapWidth);
	}

	public void setApElevation(String apElevation) {
		this.apElevation = apElevation == null ? null : Double
				.parseDouble(apElevation);
	}

	protected boolean ppFlag, showRssi, showOnHover, calibrateHeatmap,
			useHeatmap, periVal, realTime, useStreetMaps;

	protected int heatmapResolution, minRssi, clientRssiThreshold,
			calibrateRssiFrom, calibrateRssiUntil, locationWindow,
			bgMapOpacity, heatMapOpacity, wallsOpacity;

	public void setWallsOpacity(int wallsOpacity) {
		this.wallsOpacity = wallsOpacity;
	}

	public void setBgMapOpacity(int bgMapOpacity) {
		this.bgMapOpacity = bgMapOpacity;
	}

	public void setHeatMapOpacity(int heatMapOpacity) {
		this.heatMapOpacity = heatMapOpacity;
	}

	public void setRealTime(boolean realTime) {
		this.realTime = realTime;
	}

	public void setLocationWindow(int locationWindow) {
		this.locationWindow = locationWindow;
	}

	public void setClientRssiThreshold(int clientRssiThreshold) {
		this.clientRssiThreshold = clientRssiThreshold;
	}

	public void setCalibrateRssiFrom(int calibrateRssiFrom) {
		this.calibrateRssiFrom = calibrateRssiFrom;
	}

	public void setCalibrateRssiUntil(int calibrateRssiUntil) {
		this.calibrateRssiUntil = calibrateRssiUntil;
	}

	public void setMinRssi(int minRssi) {
		this.minRssi = minRssi;
	}

	public void setPeriVal(boolean periVal) {
		this.periVal = periVal;
	}

	public void setHeatmapResolution(int heatmapResolution) {
		this.heatmapResolution = heatmapResolution;
	}

	public void setCalibrateHeatmap(boolean calibrateHeatmap) {
		this.calibrateHeatmap = calibrateHeatmap;
	}

	public void setUseHeatmap(boolean useHeatmap) {
		this.useHeatmap = useHeatmap;
	}

	public void setUseStreetMaps(boolean useStreetMaps) {
		this.useStreetMaps = useStreetMaps;
	}

	public void setShowOnHover(boolean showOnHover) {
		this.showOnHover = showOnHover;
	}

	public void setShowRssi(boolean showRssi) {
		this.showRssi = showRssi;
	}

	public void setPpFlag(boolean ppFlag) {
		this.ppFlag = ppFlag;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public String getMapIcon() {
		return mapIcon;
	}

	public void setMapIcon(String mapIcon) {
		this.mapIcon = mapIcon;
	}

	public String getMapImage() {
		return mapImage;
	}

	public void setMapImage(String mapImage) {
		this.mapImage = mapImage;
	}

	public void setRefreshInterval(String refreshInterval) {
		this.refreshInterval = refreshInterval;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public void setFiledata(File filedata) {
		Filedata = filedata;
	}

	public void setFiledataFileName(String filedataFileName) {
		FiledataFileName = filedataFileName;
	}

	// public void setFiledataContentType(String filedataContentType) {
	// FiledataContentType = filedataContentType;
	// }

	public void setImagedata(File imagedata) {
		this.imagedata = imagedata;
	}

	public void setImagedataFileName(String imagedataFileName) {
		this.imagedataFileName = imagedataFileName;
	}

	protected JSONArray prepareMapIcons() throws Exception {
		List<String[]> icons = BeTopoModuleUtil.getMapIcons();
		Collection<JSONObject> jsonIcons = new Vector<JSONObject>();
		if (null != icons) {
			for (String[] icon : icons) {
				JSONObject jsonIcon = new JSONObject();
				jsonIcon.put("v", icon[0]);
				jsonIcon.put("t", icon[1]);
				jsonIcons.add(jsonIcon);
			}
		}
		return new JSONArray(jsonIcons);
	}

	protected JSONArray prepareMapImages(String domainName) throws Exception {
		List<String> images = BeTopoModuleUtil.getBackgroundImages(domainName);
		Collection<JSONObject> jsonImages = new Vector<JSONObject>();
		for (String image : images) {
			JSONObject jsonImage = new JSONObject();
			jsonImage.put("t", image);
			jsonImages.add(jsonImage);
		}
		return new JSONArray(jsonImages);
	}

	protected void prepareGlobalParams(Long mapId) throws Exception {
		jsonObject = new JSONObject();
		MapContainerNode mapNode = findBoById(MapContainerNode.class, mapId,
				this);
		if (null != mapNode) {
			MapSettings mapSettings = BeTopoModuleUtil
					.getMapGlobalSetting(mapNode.getOwner());
			int value = mapSettings.getPollingInterval();
			boolean flag = mapSettings.isSummaryFlag();
			jsonObject.put("interval", value);
			jsonObject.put("flag", flag);
			jsonObject.put("rssiFlag", mapSettings.isNeighborRssiFlag());
			jsonObject.put("resolution", mapSettings.getHeatmapResolution());
			jsonObject.put("minRssi", mapSettings.getMinRssiCount());
			jsonObject.put("clientRssiThreshold",
					-mapSettings.getClientRssiThreshold());
			jsonObject.put("calibrateRssiFrom", -mapSettings.getRssiFrom());
			jsonObject.put("calibrateRssiUntil", -mapSettings.getRssiUntil());
			jsonObject.put("locationWindow", mapSettings.getLocationWindow());
			jsonObject.put("bgMapOpacity", mapSettings.getBgMapOpacity());
			jsonObject.put("heatMapOpacity", mapSettings.getHeatMapOpacity());
			jsonObject.put("wallsOpacity", mapSettings.getWallsOpacity());
			jsonObject.put("useStreetMaps", mapSettings.isUseStreetMaps());
			jsonObject.put("realTime", mapSettings.isRealTime());
		} else {
			jsonObject.put("interval", MapSettings.DEFAULT_POLLING_INTERVAL);
			jsonObject.put("flag", false);
			jsonObject.put("rssiFlag", false);
			jsonObject.put("resolution", MapSettings.HEATMAP_RESOLUTION_AUTO);
			jsonObject.put("minRssi", MapSettings.DEFAULT_MIN_RSSI);
			jsonObject.put("clientRssiThreshold",
					-MapSettings.DEFAULT_CLIENT_RSSI_THRESHOLD);
			jsonObject.put("calibrateRssiFrom", -MapSettings.DEFAULT_RSSI_FROM);
			jsonObject.put("calibrateRssiUntil",
					-MapSettings.DEFAULT_RSSI_UNTIL);
			jsonObject.put("locationWindow",
					MapSettings.DEFAULT_LOCATION_WINDOW);
			jsonObject.put("bgMapOpacity", MapSettings.DEFAULT_BGMAP_OPACITY);
			jsonObject.put("heatMapOpacity",
					MapSettings.DEFAULT_HEATMAP_OPACITY);
			jsonObject.put("wallsOpacity", MapSettings.DEFAULT_WALLS_OPACITY);
			jsonObject.put("useStreetMaps", true);
			jsonObject.put("realTime", true);
		}
	}

	private boolean setPollingInterval(int interval, boolean flag, Long mapId)
			throws Exception {
		MapContainerNode mapNode = findBoById(MapContainerNode.class, mapId,
				this);

		if (null != mapNode) {
			List<MapSettings> settings = QueryUtil.executeQuery(
					MapSettings.class, null,
					new FilterParams("owner", mapNode.getOwner()));
			if (settings.isEmpty()) {
				// insert
				MapSettings setting = new MapSettings();
				setting.setOwner(mapNode.getOwner());
				getMapSettings(setting, interval, flag);
				QueryUtil.createBo(setting);
			} else {
				// update
				MapSettings setting = settings.get(0);
				getMapSettings(setting, interval, flag);
				QueryUtil.updateBo(setting);
			}
			return true;
		}
		return false;
	}

	private void getMapSettings(MapSettings setting, int interval, boolean flag) {
		setting.setPollingInterval(interval);
		setting.setSummaryFlag(flag);
		setting.setNeighborRssiFlag(showRssi);
		setting.setOnHoverFlag(showOnHover);
		setting.setCalibrateHeatmap(calibrateHeatmap);
		setting.setUseHeatmap(useHeatmap);
		setting.setHeatmapResolution(heatmapResolution);
		setting.setMinRssiCount(minRssi);
		setting.setClientRssiThreshold(-clientRssiThreshold);
		setting.setRssiFrom(-calibrateRssiFrom);
		setting.setRssiUntil(-calibrateRssiUntil);
		setting.setPeriVal(periVal);
		setting.setRealTime(realTime);
		setting.setBgMapOpacity(bgMapOpacity);
		setting.setHeatMapOpacity(heatMapOpacity);
		setting.setWallsOpacity(wallsOpacity);
		setting.setUseStreetMaps(useStreetMaps);
		if (locationWindow <= 0) {
			setting.setLocationWindow(MapSettings.DEFAULT_LOCATION_WINDOW);
		} else {
			setting.setLocationWindow(locationWindow);
		}
	}

	private MapContainerNode getClonePeerNode(MapContainerNode parentNode) {
		MapContainerNode peerNode = null;
		int peerNodeOrder = Integer.MAX_VALUE;
		for (MapNode mapNode : parentNode.getParentMap().getChildNodes()) {
			if (mapNode.isLeafNode()) {
				continue;
			}
			MapContainerNode map = (MapContainerNode) mapNode;
			if (map.getMapOrder() <= parentNode.getMapOrder()) {
				continue;
			} else if (map.getMapOrder() < peerNodeOrder) {
				// Select floor above current floor with the smallest order
				peerNodeOrder = map.getMapOrder();
				peerNode = map;
			}
		}
		return peerNode == null ? parentNode.getParentMap() : peerNode;
	}

	private void clonePlannedAPs(Long mapId, Set<PlannedAP> aps)
			throws Exception {
		MapContainerNode cloneNode = (MapContainerNode) QueryUtil.findBoById(
				MapNode.class, mapId);
		for (PlannedAP ap : aps) {
			PlannedAP newAP = new PlannedAP();
			newAP.apModel = ap.apModel;
			newAP.countryCode = ap.countryCode;
			newAP.x = ap.x;
			newAP.y = ap.y;
			newAP.wifi0Enabled = ap.wifi0Enabled;
			newAP.wifi0Channel = ap.wifi0Channel;
			newAP.wifi0ChannelWidth = ap.wifi0ChannelWidth;
			newAP.wifi0Power = ap.wifi0Power;
			newAP.wifi1Enabled = ap.wifi1Enabled;
			newAP.wifi1Channel = ap.wifi1Channel;
			newAP.wifi1ChannelWidth = ap.wifi1ChannelWidth;
			newAP.wifi1Power = ap.wifi1Power;
			newAP = BoMgmt.getPlannedApMgmt().createPlannedAP(cloneNode, newAP,
					getUserContext(), getSelectedL2FeatureKey());
		}
	}

	public static Long createMapContainerNode(MapContainerNode parentNode,
			int mapEnv, String mapName, short mapType, short mapWidthUnit,
			String mapImage, Double sizeX, Double sizeY, Double mapWidth,
			Double apElevation, String mapIcon, Double loss, String mapAddress,
			MapContainerNode cloned) throws Exception {
		MapContainerNode node = new MapContainerNode();
		if (cloned != null) {
			List<Vertex> newPerimeter = new ArrayList<Vertex>();
			for (Vertex vertex : cloned.getPerimeter()) {
				newPerimeter.add(vertex);
			}
			node.setPerimeter(newPerimeter);
			List<Wall> newWalls = new ArrayList<Wall>();
			for (Wall wall : cloned.getWalls()) {
				newWalls.add(wall);
			}
			node.setWalls(newWalls);
		}
		MapContainerNode peerNode = null;
		if (parentNode.getMapType() == MapContainerNode.MAP_TYPE_FLOOR) {
			peerNode = parentNode;
			parentNode = peerNode.getParentMap();
		}
		node.setParentMap(parentNode);
		node.setOwner(parentNode.getOwner());
		node.setEnvironment(mapEnv);
		node.setMapName(mapName);
		node.setAddress(mapAddress == null || mapAddress.trim().length() == 0 ? null
				: mapAddress);
		node.setMapType(mapType);
		if (mapType == 99) {
			node.setMapType(parentNode.getMapType() == MapContainerNode.MAP_TYPE_BUILDING ? MapContainerNode.MAP_TYPE_FLOOR
					: 1);
		}
		if (node.getMapType() == MapContainerNode.MAP_TYPE_FLOOR) {
			node.setAddress(null);
		} else if (node.getMapType() == 1) {
			node.setCenterZoom(parentNode.getCenterZoom());
		}
		node.setLengthUnit(mapWidthUnit);
		if (loss != null) {
			node.setFloorLoss(loss);
		}
		if (mapType == MapContainerNode.MAP_TYPE_BUILDING || mapType == 99) {
			node.setActualWidth(0);
		} else if (mapImage == null || mapImage.length() == 0) {
			log.info("execute", "no background, size (" + sizeX + ", " + sizeY
					+ ")");
			if (sizeX == null || sizeY == null || sizeX <= 0 || sizeY <= 0) {
				// Consider it as a group.
				node.setActualWidth(0);
			} else {
			    if(null != cloned) {
			        node.setWidth(cloned.getWidth());
			        node.setHeight(cloned.getHeight());
			    } else {
			        node.setWidth(sizeX);
			        node.setHeight(sizeY);
			    }
				node.setActualWidth(sizeX);
				node.setActualHeight(sizeY);
				setApElevation(node, apElevation);
			}
		} else {
			String fileName = BeTopoModuleUtil
					.getRealTopoBgImagePath(parentNode.getOwner()
							.getDomainName())
					+ File.separator + mapImage;
			ImageIcon image = new ImageIcon(fileName);
			node.setBackground(mapImage);
			if (null != image && image.getIconWidth() > 0) {
				node.setWidth(image.getIconWidth());
				node.setHeight(image.getIconHeight());
			} else {
				// Just in case the width/height could not be determined
				// from the file. The only time this could happen is if
				// the file had been removed
				node.setWidth(1);
				node.setHeight(1);
			}
			if (mapWidth == null) {
				// Initialize the actual dimensions to be 0
				node.setActualWidth(0);
				node.setActualHeight(0);
				node.setApElevation(0);
			} else {
				node.setActualWidth(mapWidth);
				node.setActualHeight(mapWidth * node.getHeight()
						/ node.getWidth());
				setApElevation(node, apElevation);
			}
		}
		BoMgmt.getMapMgmt().placeIcon(parentNode, node);
		node.setIconName(mapIcon);
		Long mapId = BoMgmt.getMapMgmt().createMapContainer(node, peerNode);
		BoObserver.notifyListeners(new BoEvent(node, BoEventType.CREATED));
		refreshInstancePermissions();

		// tracking
		NetworkDeviceConfigTracking.topologyGroupChanged(
				Calendar.getInstance(), parentNode.getOwner().getId(),
				parentNode.getId(), mapId);

		return mapId;
	}

	public static void updateMapContainerNode(MapContainerNode mapNode,
			int mapEnv, String mapName, String mapImage, String mapIcon,
			short mapWidthUnit, Double sizeX, Double sizeY, Double mapWidth,
			Double apElevation, Double loss, String mapAddress)
			throws Exception {
		mapNode.setEnvironment(mapEnv);
		mapNode.setMapName(mapName);
		if (mapAddress == null || mapAddress.trim().length() == 0) {
			mapNode.setAddress(null);
		} else if (!mapAddress.equals(mapNode.getAddress())) {
			log.info_ln("New address: " + mapAddress);
			mapNode.setCenterLatitude(null);
			mapNode.setCenterLongitude(null);
			mapNode.setViewType(null);
			mapNode.setLatitude(null);
			mapNode.setLongitude(null);
			mapNode.setAddress(mapAddress);
		}
		if (mapImage != null && mapImage.trim().length() == 0) {
			mapImage = null;
		}
		mapNode.setBackground(mapImage);
		mapNode.setIconName(mapIcon);
		if (loss != null) {
			mapNode.setFloorLoss(loss);
		}
		if (mapWidthUnit > 0) {
			if (mapWidthUnit != mapNode.getLengthUnit()) {
				if (mapWidthUnit == MapContainerNode.LENGTH_UNIT_METERS) {
					mapNode.setOriginX(mapNode.getOriginX()
							* LocationTracking.FEET_TO_METERS);
					mapNode.setOriginY(mapNode.getOriginY()
							* LocationTracking.FEET_TO_METERS);
				} else {
					mapNode.setOriginX(mapNode.getOriginX()
							/ LocationTracking.FEET_TO_METERS);
					mapNode.setOriginY(mapNode.getOriginY()
							/ LocationTracking.FEET_TO_METERS);
				}
			}
			mapNode.setLengthUnit(mapWidthUnit);
		}
		if (mapImage == null || mapImage.length() == 0) {
			log.info("execute", "no background, size (" + sizeX + ", " + sizeY
					+ ")");
			if (sizeX != null && sizeY != null && sizeX > 0 && sizeY > 0) {
				if (mapNode.getHeight() == 0) {
					// Must have been a group node before
					mapNode.setHeight(sizeY);
				}
				mapNode.setActualWidth(sizeX);
				mapNode.setActualHeight(sizeY);
				mapNode.setWidth(mapNode.getHeight() * sizeX / sizeY);
				setApElevation(mapNode, apElevation);
			} else {
				mapNode.setActualWidth(0);
				mapNode.setActualHeight(0);
				mapNode.setHeight(0);
				mapNode.setWidth(0);
			}
		} else {
			String fileName = BeTopoModuleUtil.getRealTopoBgImagePath(mapNode
					.getOwner().getDomainName()) + File.separator + mapImage;
			ImageIcon image = new ImageIcon(fileName);
			if (null != image && image.getIconWidth() > 0) {
				mapNode.setWidth(image.getIconWidth());
				mapNode.setHeight(image.getIconHeight());
			} else {
				// Just in case the width/height could not be
				// determined from the file. The only time this
				// could happen is if the file had been removed
				mapNode.setWidth(1);
				mapNode.setHeight(1);
			}
			if (mapWidth != null && mapWidth > 0) {
				mapNode.setActualWidth(mapWidth);
				setApElevation(mapNode, apElevation);
			} else {
				mapNode.setActualWidth(0);
				mapNode.setActualHeight(0);
				mapNode.setApElevation(0);
			}
			if (mapNode.getActualWidth() > 0) {
				mapNode.setActualHeight(mapNode.getActualWidth()
						* mapNode.getHeight() / mapNode.getWidth());
			}
		}
		BoMgmt.getMapMgmt().updateMapContainer(mapNode);
	}

	protected static double getDefaultApElevation(MapContainerNode node) {
		if (node.getLengthUnit() == MapContainerNode.LENGTH_UNIT_FEET) {
			return 3 / LocationTracking.FEET_TO_METERS;
		} else {
			return 3;
		}
	}

	protected static void setApElevation(MapContainerNode node,
			Double apElevation) {
		if (apElevation == null) {
			node.setApElevation(getDefaultApElevation(node));
		} else {
			double maxApElevation = MAX_AP_ELEVATION;
			if (node.getLengthUnit() == MapContainerNode.LENGTH_UNIT_FEET) {
				maxApElevation /= LocationTracking.FEET_TO_METERS;
			}
			if (apElevation > maxApElevation) {
				node.setApElevation(maxApElevation);
			} else {
				node.setApElevation(apElevation);
			}
		}
	}

	private static double MAX_AP_ELEVATION = 15; // meters

	// public static final String POP_UP_FLAG = "summaryOverlayPopUpFlag";

	protected JSONArray jsonArray = null;

	protected JSONObject jsonObject = null;

	public String getJSONString() {
		if (jsonArray == null) {
			log.debug("getJSONString", "JSON string: " + jsonObject.toString());
			return jsonObject.toString();
		} else {
			log.debug("getJSONString", "JSON string: " + jsonArray.toString());
			return jsonArray.toString();
		}
	}

}